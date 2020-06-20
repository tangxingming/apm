package com.tuling.demo.test;

//我这定义一个类，这个类有一个方法，打印一个语句，可以执行的
public class UserServiceImpl {
    public void getUser() {
        System.out.println("user is luban");
    }

    public void addUser(String name, String sex,String addr) {
        System.out.println("addUser方法中:" + name + "," + sex+ "," + addr);
    }

    public void addUser2(String name, String sex,String addr) {
        System.out.println("addUser2方法中:" + name + "," + sex+ "," + addr);
    }

    @Override
    public String toString() {
        return "UserServiceImpl{}";
    }
}
