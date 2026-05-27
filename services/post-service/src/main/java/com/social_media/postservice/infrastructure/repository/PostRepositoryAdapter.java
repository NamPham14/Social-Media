package com.social_media.postservice.infrastructure.repository;

import com.social_media.postservice.domain.model.Post;
import com.social_media.postservice.domain.repository.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostRepositoryAdapter implements PostRepository {

    PostJpaRepository postJpaRepository;

    @Override
    public Optional<Post> findById(UUID id) {
        return  postJpaRepository.findById(id);
    }

    @Override
    public Page<Post> findByAuthorId(UUID userId, Pageable pageable) {
        return postJpaRepository.findPostByUserId(userId, pageable);
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        return postJpaRepository.findAll(pageable);
    }

    @Override
    public Page<Post> searchByKeyword(String keyword, Pageable pageable) {
        return postJpaRepository.findByCaptionLikeIgnoreCase(keyword, pageable);
    }

    @Override
    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

    @Override
    public void delete(Post post) {
        postJpaRepository.delete(post);
    }
}
