package com.example.backend.controller;

import com.example.backend.entity.AppUser;
import com.example.backend.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserController(UserRepository userRepository,
                          RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping
    public AppUser createUser(@RequestBody AppUser user) {
        redisTemplate.delete("users");
        return userRepository.save(user);
    }

    @GetMapping
    public List<AppUser> getAllUsers() {

        List<AppUser> users =
                (List<AppUser>) redisTemplate.opsForValue().get("users");

        if (users != null) {
            System.out.println("Fetching from Redis Cache");
            return users;
        }

        System.out.println("Fetching from MySQL Database");
        users = userRepository.findAll();

        redisTemplate.opsForValue().set("users", users, 60, TimeUnit.SECONDS);

        return users;
    }
}