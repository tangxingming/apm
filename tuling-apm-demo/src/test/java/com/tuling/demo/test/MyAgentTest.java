package com.tuling.demo.test;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import org.junit.Ignore;
import org.junit.Test;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * Created by Tommy on 2018/3/6.
 */
public class MyAgentTest {

    @Ignore
    @Test
    public void premainTest() {

    }

    //每一个应用都有一个进程
    public static void main(String[] args) throws InterruptedException {
        //获取当前进程 即main的进程ID ManagementFactory.getRuntimeMXBean().getName()
        System.out.println("输出进程ID："+ ManagementFactory.getRuntimeMXBean().getName());
        while (true) {
            Thread.sleep(100); //我启动一个进程，并且让他一直执行
        }
    }

    //只要是main函数或者是Test方法，不论你在哪，都能单独执行哦，每次执行单独一个进程
    //你标注成Test，就说明可以执行，和你的方法名无关
    @Ignore
    @Test
    public void agentAttach() throws Exception {
        String targetPid = "19696";
        //我让jvm把当前的这个agentAttach方法的执行，附加到你目标的进程里面，相当于是在运行着的进程中，我嵌入
        VirtualMachine vm = VirtualMachine.attach(targetPid);
        //加载agent 一个是build好的jar包
        vm.loadAgent(System.getProperty("user.dir") + "/target/tuling-apm-demo-1.0-SNAPSHOT.jar",
                "toagent");
    }
}
