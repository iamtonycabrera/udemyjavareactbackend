package com.cursoudemy.backendcursoudemy.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cursoudemy.backendcursoudemy.models.requests.CreatePostRequestModel;
import com.cursoudemy.backendcursoudemy.models.responses.OperationStatusModel;
import com.cursoudemy.backendcursoudemy.models.responses.PostRest;
import com.cursoudemy.backendcursoudemy.services.PostServiceInterface;
import com.cursoudemy.backendcursoudemy.services.UserServiceInterface;
import com.cursoudemy.backendcursoudemy.shared.dto.PostCreationDto;
import com.cursoudemy.backendcursoudemy.shared.dto.PostDto;
import com.cursoudemy.backendcursoudemy.shared.dto.UserDto;
import com.cursoudemy.backendcursoudemy.utils.Exposures;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    PostServiceInterface postServiceInterface;

    @Autowired
    ModelMapper mapper;

    @Autowired
    UserServiceInterface userServiceInterface;

    @PostMapping
    public PostRest createPost(@RequestBody @Valid CreatePostRequestModel createPostRequestModel) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getPrincipal().toString();

        PostCreationDto postCreationDto = mapper.map(createPostRequestModel, PostCreationDto.class);

        postCreationDto.setUserEmail(email);

        PostDto postDto = postServiceInterface.createPost(postCreationDto);

        PostRest postToReturn = mapper.map(postDto, PostRest.class);

        return postToReturn;
    }

    @GetMapping(path = "/last") // localhost:8080/posts/last
    public List<PostRest> lastPosts() {

        List<PostDto> posts = postServiceInterface.getLastPosts();

        List<PostRest> postsToReturn = new ArrayList<>();

        for (PostDto postDto : posts) {
            PostRest postRest = mapper.map(postDto, PostRest.class);
            postsToReturn.add(postRest);
        }

        return postsToReturn;
    }

    @GetMapping(path = "/{id}") // localhost:8080/posts/uuid
    public PostRest getPost(@PathVariable String id) {

        PostDto post = postServiceInterface.getPost(id);

        PostRest postRest = mapper.map(post, PostRest.class);

        if (postRest.getExposure().getId() == Exposures.PRIVATE || postRest.isExpired()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            UserDto user = userServiceInterface.getUser(authentication.getPrincipal().toString());

            if (user.getId() != post.getUser().getId()) {
                throw new RuntimeException("You don't have permission for this action");
            }
        }

        return postRest;
    }

    @DeleteMapping(path = "/{id}") // localhost:8080/posts/uuid
    public OperationStatusModel deletePost(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDto user = userServiceInterface.getUser(authentication.getPrincipal().toString());

        OperationStatusModel operationStatusModel = new OperationStatusModel();
        operationStatusModel.setOperationName("DELETE");

        postServiceInterface.deletePost(id, user.getId());
        operationStatusModel.setOperationResult("SUCCESS");

        return operationStatusModel;
    }

    @PutMapping(path = "/{id}") // localhost:8080/posts/uuid
    public PostRest updatePost(@RequestBody @Valid CreatePostRequestModel createPostRequestModel, @PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDto user = userServiceInterface.getUser(authentication.getPrincipal().toString());

        PostCreationDto postUpdatedDto = mapper.map(createPostRequestModel, PostCreationDto.class); 

        PostDto postDto = postServiceInterface.updatePost(id, user.getId(), postUpdatedDto);

        PostRest updatedPost = mapper.map(postDto, PostRest.class);

        return updatedPost;
    }
}
