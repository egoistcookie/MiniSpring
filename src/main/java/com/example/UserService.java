package com.example;

import org.springframework.beans.factory.annotation.Autowired;

public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void sayHello() {
        userRepository.save();
        System.out.println("Hello from UserService!");
    }
}