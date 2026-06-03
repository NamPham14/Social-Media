package com.social_media.postservice.infrastructure.repository;


import com.social_media.postservice.domain.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostJpaRepository extends JpaRepository<Post, UUID> {

    @Query("SELECT p FROM Post p WHERE LOWER(p.caption) LIKE LOWER(:keyword)")
    public Page<Post> findByCaptionLikeIgnoreCase(@Param("keyword") String keyword,  Pageable pageable);


    Page<Post> findPostByUserId(UUID userId, Pageable pageable);

}
