package com.tuling.demo.test;

import org.junit.Ignore;
import org.junit.Test;

public class HelloAgentTest {

    /**
     * 装载成功，方法prmain的参数：parama=a 在main之前执行
     * hello world 执行主类
     * 1592640776093 在这个方法前先执行
     * user is luban 执行具体方法
     * */
    @Ignore
    @Test
    public void helloTest(){
        System.out.println("hello world");
        new UserServiceImpl().getUser();
    }
}
