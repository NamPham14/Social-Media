package com.social_media.postservice.infrastructure.adapter;

import com.social_media.postservice.domain.model.post.aggregate.Post;
import com.social_media.postservice.domain.repository.PostRepository;
import com.social_media.postservice.infrastructure.entity.PostEntity;
import com.social_media.postservice.infrastructure.mapper.PostMapper;
import com.social_media.postservice.infrastructure.repository.PostJpaRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostRepositoryAdapter implements PostRepository {

    PostJpaRepository postJpaRepository;
    PostMapper postMapper;

    @Override
    public Optional<Post> findById(UUID id) {
        return postJpaRepository.findById(id)
                .map(postMapper::toDomain);
    }

    @Override
    public Page<Post> findByAuthorId(UUID userId, Pageable pageable) {
        return postJpaRepository.findPostEntityByUserId(userId, pageable)
                .map(postMapper::toDomain);
    }

    @Override
    public Page<Post> findByAuthorIds(List<UUID> userIds, Pageable pageable) {   // huy thêm
        return postJpaRepository.findByUserIdIn(userIds, pageable)
                .map(postMapper::toDomain);
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        return postJpaRepository.findAll(pageable)
                .map(postMapper::toDomain);
    }

    @Override
    public Page<Post> searchByKeyword(String keyword, Pageable pageable) {
        return postJpaRepository.findByCaptionLikeIgnoreCase(keyword, pageable)
                .map(postMapper::toDomain);
    }

    @Override
    public Post save(Post post) {
        PostEntity entity = postMapper.toEntity(post);
        PostEntity saved = postJpaRepository.save(entity);
        return postMapper.toDomain(saved);
    }

    @Override
    public void delete(Post post) {
        PostEntity entity = postMapper.toEntity(post);
        postJpaRepository.delete(entity);
    }

    // hiếu thêm
    @Override
    public Page<Post> findAll(Pageable pageable, UUID viewerId, List<UUID> followingIds) {
        return postJpaRepository.findAllVisible(viewerId, followingIds, pageable)
                .map(postMapper::toDomain);
    }

    // hiếu thêm
    @Override
    public Page<Post> findByAuthorId(UUID userId, Pageable pageable, UUID viewerId, List<UUID> followingIds) {
        return postJpaRepository.findVisibleByAuthorId(userId, viewerId, followingIds, pageable)
                .map(postMapper::toDomain);
    }

    // hiếu thêm
    @Override
    public Page<Post> searchByKeyword(String keyword, Pageable pageable, UUID viewerId, List<UUID> followingIds) {
        return postJpaRepository.searchVisible(keyword, viewerId, followingIds, pageable)
                .map(postMapper::toDomain);
    }

    // hiếu thêm
    @Override
    public Page<Post> findByAuthorIds(List<UUID> userIds, Pageable pageable, UUID viewerId, List<UUID> followingIds) {
        return postJpaRepository.findVisibleByAuthorIds(userIds, viewerId, followingIds, pageable)
                .map(postMapper::toDomain);
    }
}
