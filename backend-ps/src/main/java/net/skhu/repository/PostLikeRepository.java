package net.skhu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.skhu.domain.Comment;
import net.skhu.domain.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Integer>{

	PostLike findByPost_PostIdAndUser_UserId(int postId, int userId);

	List<PostLike> findByPost_PostId(int postId);

}
