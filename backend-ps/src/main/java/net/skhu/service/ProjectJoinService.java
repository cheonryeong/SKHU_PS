package net.skhu.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.skhu.domain.ProjectJoin;
import net.skhu.domain.User;
import net.skhu.model.FindPassModel;
import net.skhu.model.MyPinProjectModel;
import net.skhu.model.MyProjectListModel;
import net.skhu.repository.ProjectJoinRepository;

@Service
public class ProjectJoinService {
	@Autowired
	ProjectJoinRepository projectJoinRepository;
	@Autowired
	ProjectService projectService;
	
	@Transactional
	public List<MyProjectListModel> changePin(MyProjectListModel myProjectListModel,int userId) {
		ProjectJoin projectJoin=projectJoinRepository.findByProject_ProjectIdAndUser_UserId(myProjectListModel.getProjectId(),userId);
		projectJoin.setPin(!projectJoin.isPin());
		projectJoinRepository.save(projectJoin);
		return projectService.projectList(userId);
	}
	
	public List<MyPinProjectModel> pinProjectList(int userId) {
		List<ProjectJoin> projectJoins=projectJoinRepository.findAllByUser_UserId(userId);
		List<MyPinProjectModel> myPinProjectsList = new ArrayList<>();
		for(int i=0;i<projectJoins.size();i++) {
			// 초대가 승인인 상태이고 핀이 true일때
			if(projectJoins.get(i).getState()==1 && projectJoins.get(i).isPin()==true) { 
				MyPinProjectModel myPinProject=new MyPinProjectModel();
				myPinProject.setProjectId(projectJoins.get(i).getProject().getProjectId());
				myPinProject.setProjectName(projectJoins.get(i).getProject().getProjectName());
				myPinProjectsList.add(myPinProject);
			}
		}
		return myPinProjectsList;
	}
	
	//0430 조인테이블 수정
	public void turnState(int joinId, int state) {
		System.out.println(joinId);
		ProjectJoin projectJoin=projectJoinRepository.findById(joinId).get();
		projectJoin.setState(state);
		projectJoinRepository.save(projectJoin);
	}
	
//	public void changePw(FindPassModel findPassModel, String authKey) {
//		System.out.println("auth: " + authKey);
//		System.out.println("password: " + findPassModel.getPassword());
//		User user = userRepository.findByAuthKey(authKey);
//		user.setPassword(findPassModel.getPassword());
//		userRepository.save(user);
//	}


}