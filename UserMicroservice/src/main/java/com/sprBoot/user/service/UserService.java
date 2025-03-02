package com.sprBoot.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.sprBoot.user.dao.User;
import com.sprBoot.user.exception.ResourceNotFoundException;
import com.sprBoot.user.repo.UserRepository;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserService.class);
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create
    public User createUser(User user) {
    	 log.debug("Creating new user with email: {}", user.getEmail());
         User savedUser = userRepository.save(user);
         log.info("User created successfully. ID: {}", savedUser.getId());
         return savedUser;
    }

    // Read
    public User getUserById(Long id) {
    	  log.debug("Fetching user with ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Update
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }

    // Delete
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    // Pagination (Topic 33)
    public Page<User> getAllUsersPaginated(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    // Custom query (Topic 27)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}