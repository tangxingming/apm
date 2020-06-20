package com.tuling.demo.test;

import javassist.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class JavassistTest {
    @Test
    public void updateMethod() throws NotFoundException, CannotCompileException, IOException {

        ClassPool pool = new ClassPool();//加载器
        pool.appendSystemPath();//把系统路径加进来
        CtClass ctl = pool.get("com.tuling.demo.test.UserServiceImpl");//获取字节码
        CtMethod method = ctl.getDeclaredMethod("getUser");
        method.insertBefore("System.out.println(\"abc\");");
        CtField f = new CtField(pool.get(String.class.getName()), "abc", ctl);//创建新的成员，类名，属性名，字节码地址
        ctl.addField(f);//为类加属性
        File file = new File(System.getProperty("user.dir") + "/target/UserServiceImpl.class");//重写字节码之后，要新建文件
        file.createNewFile();
        Files.write(file.toPath(), ctl.toBytecode());//新生成的放到新的路径而已
    }

    //特殊符号
    @Test
    public void update() throws NotFoundException, CannotCompileException, IOException {

        ClassPool pool = new ClassPool();//加载器
        pool.appendSystemPath();//把系统路径加进来
        CtClass ctl = pool.get("com.tuling.demo.test.UserServiceImpl");//获取要修改类的字节码

        /**
         * 先修改方法的字节码
         * */
//        CtMethod method = ctl.getDeclaredMethod("getUser");
        CtMethod method = ctl.getDeclaredMethod("addUser"); //修改现有方法，首先获取要修改的类的方法
        //不是在你这个方法之前来插入，而是你这个方法如果后续要执行的时候，在你这个方法执行前，先执行这段代码
        //插入的顺序是有讲究的！
        //其实就是在你这个方法里面，真正以字节码插入的，但是在你源码中是没有的，只不过是你看不到而已
        method.insertBefore("System.out.println(\"insertBefore addUser:\"+$3);");//总是在当前的前面再插入
        method.insertBefore("System.out.println(\"insertBefore addUser:\"+$2);");
        method.insertBefore("System.out.println(\"insertBefore addUser:\"+$1);");
        method.insertBefore("System.out.println(\"insertBefore addUser:\"+($0).toString());");
        method.insertBefore("addUser2($$);");
        ctl.toClass();//弄完以后，你得重写class字节码

        /**
         * 修改完了，直接使用了
         * */
        //其实它是把你这个对象，作为最后一个参数了
        new UserServiceImpl().addUser("luban", "man", "河北");//我要执行这个方法了

//        CtField f = new CtField(pool.get(String.class.getName()), "abc", ctl);//创建新的成员，类名，属性名，字节码地址
//        ctl.addField(f);//为类加属性

        /**
         * 使用完了，再把它重新写成class文件
         * */
        File file = new File(System.getProperty("user.dir") + "/target/UserServiceImpl.class");//重写字节码之后，要新建文件
        file.createNewFile();
        Files.write(file.toPath(), ctl.toBytecode());//新生成的放到新的路径而已
    }
}
