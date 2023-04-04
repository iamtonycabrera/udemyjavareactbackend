package com.cursoudemy.backendcursoudemy.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cursoudemy.backendcursoudemy.entities.PostEntity;
import com.cursoudemy.backendcursoudemy.entities.UserEntity;
import com.cursoudemy.backendcursoudemy.exceptions.EmailExistsException;
import com.cursoudemy.backendcursoudemy.repositories.PostRepository;
import com.cursoudemy.backendcursoudemy.repositories.UserRepository;
import com.cursoudemy.backendcursoudemy.shared.dto.PostDto;
import com.cursoudemy.backendcursoudemy.shared.dto.UserDto;

@Service
public class UserService implements UserServiceInterface {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    ModelMapper mapper;

    @Override
    public UserDto createUser(UserDto user) {

        if (userRepository.findByEmail(user.getEmail()) != null)
            throw new EmailExistsException("El usuario ya existe en la base de datos");

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        UUID userUuid = UUID.randomUUID();

        userEntity.setUserId(userUuid.toString());
        userEntity.setEncryptedPassword(
                bCryptPasswordEncoder.encode(user.getPassword()));

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDto userToReturn = new UserDto();
        BeanUtils.copyProperties(storedUserDetails, userToReturn);

        return userToReturn;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        UserDto userToReturn = new UserDto();

        BeanUtils.copyProperties(userEntity, userToReturn);

        return userToReturn;
    }

    @Override
    public List<PostDto> getUserPosts(String email) {

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        List<PostEntity>posts = postRepository.getByUserIdOrderByCreatedAtDesc(userEntity.getId());

        List<PostDto> postsToReturn = new ArrayList<>();

        for (PostEntity post : posts) {
            PostDto postdDto  =mapper.map(post, PostDto.class);
            postsToReturn.add(postdDto);
        }

        return postsToReturn;
    }

}