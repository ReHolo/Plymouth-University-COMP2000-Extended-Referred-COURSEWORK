// UserService.java
package com.example.tennisbooking.Interface;

import com.example.tennisbooking.entity.User;

public interface UserService {
    boolean registerUser(User user);
    boolean loginUser(String email, String password);
}