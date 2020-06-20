package com.tuling.javaagent;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

//打包，是为了把含有 premain 的入口类打成jar包 以便在执行test的时候，将这个jar包作为jvm运行参数传入，将jar包放进jvm的classpath里面去，从而实现插桩
//premain 的入口类
//在执行main或test之前，先执行premain方法哦，前提时候你有这个premain方法，且作为参数
/**
 * -ea
 * -javaagent:D:\\programming\\unimall\\trunk\\iotechn-unimall-master\\tuling-apm\\tuling-apm-demo\\target\\tuling-apm-demo-1.0-SNAPSHOT.jar=parama=a
 * */
//bootstrap启动程序
public class HelloAgent {

    public static void  premain(String arg, Instrumentation instrumentation){
        //最终目标是为了执行Instrumentation的addTransformer方法。给插桩做准备
        System.out.println("装载成功，方法prmain的参数："+arg);

        //先执行这个方法
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

                //表示我要在哪个类之前插桩
                String cname="com.tuling.demo.test.UserServiceImpl";

                //判断当前类名是不是你要的类名
                if(className.replaceAll("/",".").equals(cname)){
                    //如果是，先建立一个类缓冲池
                    ClassPool classPool = new ClassPool();
                    //在这个缓冲池里面，我要插入类路径，类路径也是个对象哦，本质就是加载类的路径
                    classPool.insertClassPath(new LoaderClassPath(loader));

                    try {
                        //获取要插桩的类名
                        CtClass ctClass = classPool.get(cname);
                        //获取要插桩的方法
                        CtMethod method = ctClass.getDeclaredMethod("getUser");
                        //在这个类的这个方法执行之前，先执行这段代码，目的是在主代码之前嵌入新的东西，辅助的代码而已
                        //所谓的插桩，就是在合适的位置，插入自己想执行的一段代码而已！！！
                        method.insertBefore("System.out.println(System.currentTimeMillis());");
                        //你对任何修改了，则最终修改的都是类哦
                        return ctClass.toBytecode();//必须返回其字节码
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    } catch (CannotCompileException | IOException e) {
                        e.printStackTrace();
                    }
                }
                return null; //是返回原来的字节码，还是新的
            }
        });
    }
}