package com.example;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MyTest {
    /**
     * 程序入口函数，用于演示Spring框架的基本使用
     * 该函数通过读取XML配置文件来获取Bean实例，并调用相应的方法
     *
     * @param args 命令行参数数组
     */
    public static void main(String[] args) {
        // 从classpath路径下加载applicationContext.xml配置文件，创建Spring应用上下文
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        // 从Spring容器中获取名为"userService"的Bean实例，并强制转换为UserService类型
        UserService userService = (UserService) context.getBean("userService");

        // 调用UserService的sayHello方法
        userService.sayHello(); // 输出: Hello from UserService!
    }

}