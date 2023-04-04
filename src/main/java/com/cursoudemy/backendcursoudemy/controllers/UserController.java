package com.cursoudemy.backendcursoudemy.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cursoudemy.backendcursoudemy.models.requests.UserDetailRequestModel;
import com.cursoudemy.backendcursoudemy.models.responses.PostRest;
import com.cursoudemy.backendcursoudemy.models.responses.UserRest;
import com.cursoudemy.backendcursoudemy.services.UserServiceInterface;
import com.cursoudemy.backendcursoudemy.shared.dto.PostDto;
import com.cursoudemy.backendcursoudemy.shared.dto.UserDto;

@RestController
@RequestMapping("/users") //localhost:8080/users
public class UserController {

    @Autowired
    UserServiceInterface userService;

    @Autowired
    ModelMapper mapper;

    /// Retorna la informacion del usuario que esta loggeado
    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getPrincipal().toString();

        UserDto userDto = userService.getUser(email);

        UserRest userToReturn = mapper.map(userDto, UserRest.class);

        return userToReturn;
    }

    @PostMapping
    public UserRest createUser(@RequestBody @Valid UserDetailRequestModel userDetails){
        UserRest userToReturn = new UserRest();

        UserDto userDto = new UserDto(); // Objeto que se va a enviar a la logica de la app

        BeanUtils.copyProperties(userDetails, userDto); // Copiar propiedades del objeto recibido => userDetails a userDto

        UserDto createdUser = userService.createUser(userDto); // Servicio donde va a tener metodo createUser que va a crear el user en la db

        BeanUtils.copyProperties(createdUser, userToReturn);

        return userToReturn;
    }

    @GetMapping(path = "/posts") //localhost:8080/users/posts
    public List<PostRest> getPosts() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getPrincipal().toString();

        List<PostDto> posts = userService.getUserPosts(email);

        List<PostRest> postsToReturn = new ArrayList<>();

        for (PostDto postDto : posts) {
            PostRest postRest = mapper.map(postDto, PostRest.class);
            if (postRest.getExpiresAt().compareTo(new Date(System.currentTimeMillis())) < 0) {
                postRest.setExpired(true);
            }
            postsToReturn.add(postRest);
        }

        return postsToReturn;
    }
    
}
