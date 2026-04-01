package com.capstone.demo.serviceimpl;

import com.capstone.demo.model.Users;
import com.capstone.demo.ropositary.UserRepository;
import com.capstone.demo.exception.ResourceNotFoundException;
import com.capstone.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Users saveUser(Users user) {
        return userRepository.save(user);
    }

    @Override
    public Users getUserById(String id) {
    	return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
    }

    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(String id) {
    	Users user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.deleteById(id);
    }

    @Override
    public Users updateUser(String id, Users userData) {
    	Users user = userRepository.findById(id).orElseThrow(()
    			-> new ResourceNotFoundException("User", "ID", id));;
        if (user != null) {
            user.setUsername(userData.getUsername());
            user.setEmail(userData.getEmail());
            user.setPhoneNumber(userData.getPhoneNumber());
            user.setAddress(userData.getAddress());
            user.setRewardPoints(userData.getRewardPoints());
            return userRepository.save(user);
        }
        return null;
    }

	

}
