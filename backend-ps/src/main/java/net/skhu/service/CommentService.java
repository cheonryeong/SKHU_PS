package net.skhu.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.skhu.domain.Comment;
import net.skhu.domain.Post;
import net.skhu.domain.Project;
import net.skhu.domain.ProjectJoin;
import net.skhu.domain.Timeline;
import net.skhu.repository.CommentRepository;
import net.skhu.repository.PostRepository;
import net.skhu.repository.TimelineRepository;
import net.skhu.repository.UserRepository;

@Service
public class CommentService {
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	PostRepository postRepository;
	@Autowired
	ProjectService projectService;
	@Autowired
	TimelineRepository timelineRepository;
	
	
	@Transactional
	public void noticeAddComment(String content,int postId,int userId) {
		Comment c=new Comment();
		c.setContent(content);
		c.setWriteTime(LocalDateTime.now());
		c.setChoice(0);
		c.setPost(postRepository.findById(postId).get());
		c.setUser(userRepository.findById(userId).get());
		commentRepository.save(c);
		
		//TIMELINE 댓글 작성 [작성자에게]
		Post p = c.getPost();
		if(userId != p.getUser().getUserId()) { //댓글 작성자가 게시글 작성자가 아닌 경우
			if(p.getSubject() != null) {  //공지게시판
				String text = p.getSubject().getTitle()+"의 공지 게시글에 새로운 댓글이 추가되었습니다.";
				String url = "/subject/"+p.getSubject().getSubjectId()+"/noticeBoard/"+postId;
				
				timelineRepository.save(new Timeline(0, text, LocalDateTime.now(), url, p.getUser()));
				
			} else if(p.getProject() != null){  //자유게시판
				String text = p.getProject().getProjectName()+"의 자유 게시글에 새로운 댓글이 추가되었습니다.";
				String url = "/project/"+p.getProject().getProjectId()+"/freeBoard/"+postId;
				
				timelineRepository.save(new Timeline(0, text, LocalDateTime.now(), url, p.getUser()));
			}
		}
	}
	@Transactional
	public void noticedeleteComment(int commentId) {
		commentRepository.delete(commentRepository.findById(commentId).get());
	}
	
	//FIXME PostService로 이동
	@Transactional
	public void noticedeletePost(int postId) {
		//TIMELINE 새로운 공지게시글 삭제 [과목 선택 프로젝트 모든 참가자가 받음]
		//어떤 과목의 공지 게시글이 삭제되었습니다.
		Post p = postRepository.findById(postId).get();
		for(Project project : projectService.subjectProjects(p.getSubject().getSubjectId())) {
			for(ProjectJoin join : projectService.allMember(project.getProjectId())) {
				String content = p.getSubject().getTitle()+"의 공지 게시글이 삭제되었습니다. ("+p.getTitle()+")";
				String url = "/project/"+project.getProjectId()+"/noticeBoard?page=1";
				
				timelineRepository.save(new Timeline(0, content, LocalDateTime.now(), url, join.getUser()));
			}
		}
		
		postRepository.delete(postRepository.findById(postId).get());
	}
	@Transactional
	public void commentCheck(int commentId) {
		Comment c=commentRepository.findById(commentId).get();
		if(c.getChoice()==0) 
			c.setChoice(1);
		else
			c.setChoice(0);
		commentRepository.save(c);
	}
}
