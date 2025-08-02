package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @RequestMapping("/login")
    public String showLoginForm() {
        return "login"; // 返回视图名
    }

    @RequestMapping(value = "/login", method = "POST")
    public String handleLogin(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if ("admin".equals(username) && "123456".equals(password)) {
            request.setAttribute("message", "Login success!");
        } else {
            request.setAttribute("message", "Login failed!");
        }
        return "login";
    }
}