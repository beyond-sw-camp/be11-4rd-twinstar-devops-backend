package com.TwinStar.TwinStar.post.repository;

import com.TwinStar.TwinStar.post.domain.Post;
import com.TwinStar.TwinStar.post.domain.PostLike;
import com.TwinStar.TwinStar.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository

public interface PostLikeRepository extends JpaRepository<PostLike,Long> {
    Long countByPost(Post post);

    Optional<PostLike> findByPostAndUser(Post post, User user);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    @Query("SELECT pl.user FROM PostLike pl WHERE pl.post.id = :postId")
    Page<User> findUsersWhoLikedPost(@Param("postId") Long postId, Pageable pageable);
}
