package com.tuling.javaagent;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import javassist.*;
import org.junit.Assert;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.InetSocketAddress;
import java.security.ProtectionDomain;
import java.util.concurrent.Executors;

/**
 * Created by Tommy on 2018/3/6.
 */
//1、实现这个接口  ClassFileTransformer
public class C3p0Agent implements ClassFileTransformer {

    //3、定义插桩的目标，要对哪个类进行监控，插桩，要对哪个类进行修改
    static String targetClass = "com.mchange.v2.c3p0.ComboPooledDataSource";

    public  C3p0Agent() {
        try {
            openHttpServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getStatus() {
        Object source2 = System.getProperties().get("c3p0Source$agent");
        if (source2 == null) {
            return "未初始任何c3p0数据源";
        }
        return source2.toString();
    }

    //用java自带的http服务实现对外提供http服务
    public void openHttpServer() throws IOException {
        //默认用本机地址
        InetSocketAddress addr = new InetSocketAddress(5555);
        HttpServer server = HttpServer.create(addr, 0);
        server.createContext("/server", new HttpHandler());
        server.setExecutor(Executors.newCachedThreadPool());//线程池
        server.start();
        System.out.println("Server is listening on port 5555");
    }

    //2、可以开始插桩了
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        //你获取的还真是字节数组呢！！
        byte[] result = null;

        //我要对这个类进行改造
        if (className != null && className.replace("/", ".").equals(targetClass)) {
            ClassPool pool = new ClassPool();
            pool.insertClassPath(new LoaderClassPath(loader));
            try {
                CtClass ctl = pool.get(targetClass);//获取类
                //获取当前的构造方法？？？？？？？？？？？？？？
                ctl.getConstructor("()V")
                        .insertAfter("System.getProperties().put(\"c3p0Source$agent\", $0);");
                //修改完后，重新生成字节码
                result = ctl.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //返回修改后的字节码
        return result;
    }

    // 测试插桩
    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException {
        ClassPool pool = new ClassPool();
        pool.insertClassPath(new LoaderClassPath(C3p0Agent.class.getClassLoader()));
        CtClass ctl = pool.get(targetClass);
        ctl.getConstructor("()V")
                .insertAfter("System.getProperties().put(\"c3p0Source$agent\", $0);");
        ctl.toClass();//修改完类的字节码

        //对昨晚手术的类重新使用
        ComboPooledDataSource source = new ComboPooledDataSource("mysql");
        Object source2 = System.getProperties().get("c3p0Source$agent");
        System.out.println(source.toString());
        Assert.assertEquals(source, source2);

        new C3p0Agent().openHttpServer();
    }

    private class HttpHandler implements com.sun.net.httpserver.HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            //响应头
            Headers responseHeaders = exchange.getResponseHeaders();
            //格式
            responseHeaders.set("Content-Type", "text/plain;charset=UTF-8");
            exchange.sendResponseHeaders(200, 0);
            //响应体
            OutputStream responseBody = exchange.getResponseBody();

            // 输出c3p0状态
            responseBody.write(C3p0Agent.this.getStatus().getBytes());
            responseBody.flush();
            responseBody.close();
        }
    }
}
