package com.example.service.impl;

import com.example.service.UserRepository;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    public void sayHello() {
        userRepository.save();
        System.out.println("Hello from UserService!");
    }
}
