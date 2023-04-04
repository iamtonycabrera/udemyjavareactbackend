package com.cursoudemy.backendcursoudemy.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.cursoudemy.backendcursoudemy.shared.dto.PostDto;
import com.cursoudemy.backendcursoudemy.shared.dto.UserDto;

public interface UserServiceInterface extends UserDetailsService {
    public UserDto createUser(UserDto user);
    
    public UserDto getUser(String email);

    public List<PostDto> getUserPosts(String email);
    
}
