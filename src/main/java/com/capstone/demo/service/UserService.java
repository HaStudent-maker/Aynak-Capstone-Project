package com.capstone.demo.service;

import com.capstone.demo.model.Users;
import java.util.List;

public interface UserService {

	Users saveUser(Users user);

	Users getUserById(String id);

    List<Users> getAllUsers();

    void deleteUser(String id);

    Users updateUser(String id, Users user);
}
