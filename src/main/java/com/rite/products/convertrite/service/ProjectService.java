package com.rite.products.convertrite.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rite.products.convertrite.model.CrProjectActivities;
import com.rite.products.convertrite.model.CrProjects;
import com.rite.products.convertrite.po.*;
import org.springframework.http.HttpHeaders;

import com.rite.products.convertrite.exception.ValidationException;

public interface ProjectService {

	List<XxrProjectWbsResPo> loadWbs(Long projectId,Long podId,HttpServletRequest request) throws Exception;

	String loadTask(Long projectId,Long podId,HttpServletRequest request) throws Exception;

	SaveProjectHeaderResponsePo saveProjectHeaders(CrProjects projectHeadersPo, HttpServletRequest request)throws Exception;

	List<CrProjects> getAllProjectHeaders() throws Exception;

	List<CrProjectActivities> getProjectLinesById(ProjectLinesReqPo projectLinesReqPo,HttpHeaders httpHeaders)throws Exception;

	DataResPo updateProjectWbsSelections(List<ProjectWbsPo> projectWbsPo) throws Exception;

	ActivitiesResPo upsertProjectActivities(List<CrProjectActivities> activitiesPo, HttpServletRequest request) throws Exception;

	List<String> getPreRequisiteTaskLov(Long projectId, String taskNumber,Long objectId,Long podId) throws Exception;
	
	List<TaskBreakDownPo> getTaskBreakDown(Long projectId,Long podId) throws Exception;

	SaveProjectHeaderResponsePo copyProject(CopyProjectRequestPo copyProjectRequest,HttpServletRequest request) throws ValidationException,Exception;

	List<TaskOwnerLov> getTaskOwnerLov(Long projectId)throws Exception;

    Object
	getProjectsAndObjects(Long clientId, Long podId,String projectCode, String bearerToken) throws JsonProcessingException;

	Object insertProjectActivities(Long projectId);

	Object getParentObjects(Long projectId);

	Object getObjectsByObjectCode(Long projectId,String objectCode);

	Object getObjectsByUserId(Long userId,String bearerToken);
}
