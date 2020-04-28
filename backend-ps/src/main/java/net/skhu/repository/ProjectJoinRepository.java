package net.skhu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.skhu.domain.ProjectJoin;

public interface ProjectJoinRepository extends JpaRepository<ProjectJoin, Integer> {

	// 해당 프로젝트의 팀장을 제외한 팀원들
	List<ProjectJoin> findByProject_ProjectIdAndStateAndTypeNot(int projectId, int state, int type);
	
	// 초대 한번 더
	ProjectJoin findByProject_ProjectIdAndUser_UserNumAndStateNot(int projectId, int userNum, int state);

}