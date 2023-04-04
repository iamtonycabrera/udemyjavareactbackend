package com.cursoudemy.backendcursoudemy.services;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cursoudemy.backendcursoudemy.entities.ExposureEntity;
import com.cursoudemy.backendcursoudemy.entities.PostEntity;
import com.cursoudemy.backendcursoudemy.entities.UserEntity;
import com.cursoudemy.backendcursoudemy.repositories.ExposureRepository;
import com.cursoudemy.backendcursoudemy.repositories.PostRepository;
import com.cursoudemy.backendcursoudemy.repositories.UserRepository;
import com.cursoudemy.backendcursoudemy.shared.dto.PostCreationDto;
import com.cursoudemy.backendcursoudemy.shared.dto.PostDto;
import com.cursoudemy.backendcursoudemy.utils.Exposures;

@Service
public class PostService implements PostServiceInterface {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ExposureRepository exposureRepository;

    @Autowired
    ModelMapper mapper;

    @Override
    public PostDto createPost(PostCreationDto post) {

        UserEntity userEntity = userRepository.findByEmail(post.getUserEmail());
        ExposureEntity exposureEntity = exposureRepository.findById(post.getExposureId());

        PostEntity postEntity = new PostEntity();
        postEntity.setUser(userEntity);
        postEntity.setExposure(exposureEntity);
        postEntity.setTitle(post.getTitle());
        postEntity.setContent(post.getContent());
        postEntity.setPostId(UUID.randomUUID().toString());
        postEntity.setExpiresAt(new Date(System.currentTimeMillis() + (post.getExpirationTime() * 60000)));

        PostEntity createdPostEntity = postRepository.save(postEntity);

        PostDto postToReturn = mapper.map(createdPostEntity, PostDto.class);
        
        return postToReturn;
    }

    @Override
    public List<PostDto> getLastPosts() {

        List<PostEntity> postsEntities = postRepository.getLastPublicPosts(Exposures.PUBLIC, new Date(System.currentTimeMillis()));

        List<PostDto> postsToReturn = new ArrayList<>();

        for (PostEntity post : postsEntities) {
            PostDto postDtos = mapper.map(post, PostDto.class);
            postsToReturn.add(postDtos);
        }

        return postsToReturn;
    }

    @Override
    public PostDto getPost(String postId) {

        PostEntity postEntity = postRepository.findByPostId(postId);

        PostDto postToReturn = mapper.map(postEntity, PostDto.class);
        
        return postToReturn;
    }

    @Override
    public void deletePost(String postId, long userId) {
        // COMPROBAR QUE EL POST PERTENEZCA AL USER
        PostEntity postEntity = postRepository.findByPostId(postId);
        if(postEntity.getUser().getId() != userId) throw new RuntimeException("Action not allowed");

        postRepository.delete(postEntity);

        
    }

    @Override
    public PostDto updatePost(String postId, long userId, PostCreationDto postUpdatedDto) {
        // COMPROBAR QUE EL POST PERTENEZCA AL USER
        PostEntity postEntity = postRepository.findByPostId(postId);
        if(postEntity.getUser().getId() != userId) throw new RuntimeException("Action not allowed");

        ExposureEntity exposureEntity = exposureRepository.findById(postUpdatedDto.getExposureId());

        postEntity.setExposure(exposureEntity);
        postEntity.setTitle(postUpdatedDto.getTitle());
        postEntity.setContent(postUpdatedDto.getContent());
        postEntity.setExpiresAt(new Date(System.currentTimeMillis() + (postUpdatedDto.getExpirationTime() * 60000)));

        PostEntity updatedPostEntity = postRepository.save(postEntity);

        PostDto postDto = mapper.map(updatedPostEntity, PostDto.class);

        return postDto;
    }

}
