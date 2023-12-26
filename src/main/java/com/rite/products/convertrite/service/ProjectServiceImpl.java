package com.rite.products.convertrite.service;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rite.products.convertrite.model.CrProjectActivities;
import com.rite.products.convertrite.model.CrProjects;
import com.rite.products.convertrite.model.CrProjectsObjects;
import com.rite.products.convertrite.po.*;
import com.rite.products.convertrite.respository.*;
import com.rite.products.convertrite.utils.LookUpSets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import com.rite.products.convertrite.Validations.Validations;
import com.rite.products.convertrite.exception.BadRequestException;
import com.rite.products.convertrite.exception.ValidationException;
import com.rite.products.convertrite.model.XxrActivities;
import com.rite.products.convertrite.model.XxrProjectWbs;
import com.rite.products.convertrite.model.XxrProjects;
import org.springframework.web.client.RestTemplate;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Value("${convertrite-admin-host}")
	String ConvertriteAdminHost;
	@Autowired
	ProjectEntryDaoImpl projectEntryDaoImpl;
	@Autowired
	XxrProjectWbsTabRepository xxrProjectWbsTabRepository;
	@Autowired
	XxrProjectsRepository xxrProjectsRepository;
	@Autowired
	CrProjectsRepo crProjectsRepo;

	@Autowired
	LovService lovService;
	@Autowired
	CrProjectsObjectsRepo crProjectsObjectsRepo;
	@Autowired
	XxrActivitiesRepository xxrActivitiesRepository;

	@Autowired
	CrProjectActivitiesRepo crProjectActivitiesRepo;
	@Autowired
	XxrLookUpValuesRepository xxrLookUpValuesRepository;
	@Autowired
	SaveProjectHeaderDaoImpl saveProjectHeaderDaoImpl;
	@Autowired
	SaveProjectActivitiesDaoImpl saveProjectActivitiesDaoImpl;
	@Autowired
	XxrRoleObjectLinksRepository xxrRoleObjectLinksRepository;
	@Autowired
	XxrUsersRepository xxrUsersRepository;
	@Autowired
	XxrProjActivitiesViewRepository xxrProjActivitiesViewRepository;

	private static final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

	@Override
	public List<XxrProjectWbsResPo> loadWbs(Long projectId, Long podId, HttpServletRequest request) throws Exception {
		log.info("Start of loadWbs Method in Service ####");
		List<XxrProjectWbs> xxrProjectWbsTab = new ArrayList<>();
		List<XxrProjectWbsResPo> xxrProjectWbsResPo = new ArrayList<>();
		try {
			projectEntryDaoImpl.loadWbs(projectId, podId, request);
			xxrProjectWbsTab = xxrProjectWbsTabRepository.getByprojectId(projectId, podId);

			xxrProjectWbsTab.stream().forEach(x -> {
				XxrProjectWbsResPo projectWbsResPo = new XxrProjectWbsResPo();

				String projectName = xxrLookUpValuesRepository.getValueById(x.getProjectId());
				String objectCode = xxrLookUpValuesRepository.getValueById(x.getObjectId());
				String parentObjectCode = xxrLookUpValuesRepository.getValueById(x.getParentObjectId());
				String podName = xxrLookUpValuesRepository.getValueById(x.getPodId());

				projectWbsResPo.setPodId(x.getPodId());
				projectWbsResPo.setPodName(podName);
				projectWbsResPo.setProjectId(x.getProjectId());
				projectWbsResPo.setProjectName(projectName);
				projectWbsResPo.setObjectId(x.getObjectId());
				projectWbsResPo.setObjectCode(objectCode);
				projectWbsResPo.setParentObjectId(x.getParentObjectId());
				projectWbsResPo.setParentObjectCode(parentObjectCode);
				projectWbsResPo.setWbsId(x.getWbsId());
				projectWbsResPo.setSeq(x.getSeq());
				if (x.getStartDate() != null)
					projectWbsResPo.setStartDate(x.getStartDate());
				if (x.getEndDate() != null)
					projectWbsResPo.setEndDate(x.getEndDate());
				if (!Validations.isNullOrEmpty(x.getSelectedFlag()))
					projectWbsResPo.setSelectedFlag(x.getSelectedFlag());

				xxrProjectWbsResPo.add(projectWbsResPo);
			});

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

		return xxrProjectWbsResPo;
	}

	@Override
	public String loadTask(Long projectId, Long podId, HttpServletRequest request) throws Exception {
		log.info("Start of loadTask Method in Service ####");
		String result = "";
		try {
			result = projectEntryDaoImpl.loadTask(projectId, podId, request);
			String userId= request.getHeader("userId");
			result = xxrActivitiesRepository.saveLoadTask(Math.toIntExact(projectId), Math.toIntExact(podId), userId);

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return result;
	}

	@Override
	public SaveProjectHeaderResponsePo saveProjectHeaders(CrProjects projectHeadersPo,
														  HttpServletRequest request) throws Exception {
		log.info("Start of saveProjectHeaders Method in Service ####");
		SaveProjectHeaderResponsePo saveProjectHeaderResponsePo = new SaveProjectHeaderResponsePo();
		Long projectId = null;
		String message = "";
		try {
			//message = saveProjectHeaderDaoImpl.saveProjectHeader(projectHeadersPo, request);
//			CrProjects project=crProjectsRepo.findByProjectId(projectHeadersPo.getProjectId());
//			if(project==null){
				log.info("insert===>");
				saveProjectHeaderResponsePo=saveOrUpdateProjectHeaders(projectHeadersPo);
//			}else{
//				log.info("update===>");
//				project.setProjectName(projectHeadersPo.getProjectName());
//				project.setProjectStatus(projectHeadersPo.getProjectStatus());
//				project.setDescription(projectHeadersPo.getDescription());
//				saveProjectHeaderResponsePo=	saveOrUpdateProjectHeaders(project);
//			}

			//			List list=new ArrayList();
//			list.add(projectHeadersPo);
//			int userId= Integer.parseInt(request.getHeader("userId"));
//			message = xxrProjectsRepository.saveProjectHeaders(list, userId);

//			saveProjectHeaderResponsePo.setMessage(message);

//			if (!Validations.isNullOrEmpty(projectHeadersPo.getProjectName()) ) {
//				projectId = xxrProjectsRepository.getProjectId(projectHeadersPo.getProjectName(),
//						projectHeadersPo.getPodId());
//				saveProjectHeaderResponsePo.setProjectId(projectId);
			saveProjectHeaderResponsePo.setProjectName(projectHeadersPo.getProjectName());
//			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return saveProjectHeaderResponsePo;
	}

	private SaveProjectHeaderResponsePo saveOrUpdateProjectHeaders(CrProjects projectHeadersPo) {
		String message=null;
		SaveProjectHeaderResponsePo saveProjectHeaderResponsePo = new SaveProjectHeaderResponsePo();
		try {
			CrProjects pro=crProjectsRepo.save(projectHeadersPo);
			if(pro!=null){
				message=	"Project Header successfully saved";
				saveProjectHeaderResponsePo.setMessage(message);
				saveProjectHeaderResponsePo.setProjectId(pro.getProjectId());
			}
		} catch (Exception e) {
			message = "Error  while saving Project Header";
			saveProjectHeaderResponsePo.setMessage(message);
			saveProjectHeaderResponsePo.setError(e.getMessage());
			e.printStackTrace();

		}
		return saveProjectHeaderResponsePo;
	}
	private List<CrProjectActivities> saveOrUpdateProjectActivities(List<CrProjectActivities> activitiesPo) {
		List<CrProjectActivities> res=null;
		try {
			 res=crProjectActivitiesRepo.saveAll(activitiesPo);

		}catch (Exception e){
			//resMsg = "Exception while saving Project Activities";
			e.printStackTrace();
			log.error("Exception in saveOrUpdateProjectActivities-->"+e.getMessage());
		}
		return res;
	}
	@Override
	public List<CrProjects> getAllProjectHeaders() throws Exception {
		log.info("Start of getAllProjectHeaders Method in Service ####");
		// List<XxrProjects> xxrProjectsList = new ArrayList<>();
		List<CrProjects> xxrProjectsResPoList = new ArrayList<>();
		try {
			//xxrProjectsResPoList = xxrProjectsRepository.getAllProjectHeaders();
			xxrProjectsResPoList = crProjectsRepo.findAll();

			/*
			 * xxrProjectsList.stream().forEach(x -> { XxrProjectsResPo projectsResPo= new
			 * XxrProjectsResPo(); projectsResPo.setProjectId(x.getProjectId());
			 * projectsResPo.setAccessLevel(x.getAccessLevel());
			 * projectsResPo.setClientManager(x.getClientManager());
			 * projectsResPo.setClientProjectNumber(x.getClientProjectNumber());
			 * projectsResPo.setDescription(x.getDescription());
			 * projectsResPo.setEndDate(x.getEndDate());
			 * projectsResPo.setStartDate(x.getStartDate());
			 *
			 * String podValue=xxrLookUpValuesRepository.getValueById(x.getPodId());
			 *
			 * });
			 */

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return xxrProjectsResPoList;
	}

	/*
	 * @Override public List<XxrActivitiesResPo> getProjectLinesById(Long projectId,
	 * Long podId) throws Exception {
	 * log.info("Start of getProjectLinesById Method in Service ####");
	 * List<XxrActivities> xxrActivitiesList = new ArrayList<>();
	 * List<XxrActivitiesResPo> xxrActivitiesResPoList = new ArrayList<>(); try {
	 * xxrActivitiesList = xxrActivitiesRepository.getActivityLinesById(projectId,
	 * podId);
	 *
	 * xxrActivitiesList.stream().forEach(x -> { XxrActivitiesResPo
	 * xxrActivitiesResPo = new XxrActivitiesResPo(); String clientResource = "";
	 * String cloudResource = ""; String destinationResource = ""; String
	 * integratorResource = ""; String legacyResource = ""; String taskOwner = "";
	 * String podName = "";
	 *
	 * if (x.getClientResourceId() != null) { clientResource =
	 * xxrLookUpValuesRepository.getValueById(x.getClientResourceId());
	 * xxrActivitiesResPo.setClientResourceId(x.getClientResourceId()); } if
	 * (x.getCloudResourceId() != null) { cloudResource =
	 * xxrLookUpValuesRepository.getValueById(x.getCloudResourceId());
	 * xxrActivitiesResPo.setCloudResourceId(x.getCloudResourceId()); } if
	 * (x.getDestinationResourceId() != null) { destinationResource =
	 * xxrLookUpValuesRepository.getValueById(x.getDestinationResourceId());
	 * xxrActivitiesResPo.setDestinationResourceId(x.getDestinationResourceId()); }
	 * if (x.getIntegratorResourceId() != null) { integratorResource =
	 * xxrLookUpValuesRepository.getValueById(x.getIntegratorResourceId());
	 * xxrActivitiesResPo.setIntegratorResourceId(x.getIntegratorResourceId()); } if
	 * (x.getLegacyResourceId() != null) { legacyResource =
	 * xxrLookUpValuesRepository.getValueById(x.getLegacyResourceId());
	 * xxrActivitiesResPo.setLegacyResourceId(x.getLegacyResourceId()); }
	 *
	 * if (x.getTaskOwnerId() != null) { taskOwner =
	 * xxrUsersRepository.getUserName(x.getTaskOwnerId());
	 * xxrActivitiesResPo.setTaskOwnerId(x.getTaskOwnerId()); } if (x.getPodId() !=
	 * null) { podName = xxrLookUpValuesRepository.getValueById(x.getPodId());
	 * xxrActivitiesResPo.setPodId(x.getPodId()); }
	 *
	 * String objectCode = xxrLookUpValuesRepository.getValueById(x.getObjectId());
	 * xxrActivitiesResPo.setObjectId(x.getObjectId()); String parentObjectCode =
	 * xxrLookUpValuesRepository.getValueById(x.getParentObjectId());
	 * xxrActivitiesResPo.setParentObjectId(x.getParentObjectId());
	 *
	 * if (!Validations.isNullOrEmpty(podName))
	 * xxrActivitiesResPo.setPodName(podName); if
	 * (!Validations.isNullOrEmpty(clientResource))
	 * xxrActivitiesResPo.setClientResource(clientResource); if
	 * (!Validations.isNullOrEmpty(cloudResource))
	 * xxrActivitiesResPo.setCloudResource(cloudResource); if
	 * (!Validations.isNullOrEmpty(destinationResource))
	 * xxrActivitiesResPo.setDestinationResource(destinationResource); if
	 * (!Validations.isNullOrEmpty(integratorResource))
	 * xxrActivitiesResPo.setIntegratorResource(integratorResource); if
	 * (!Validations.isNullOrEmpty(legacyResource))
	 * xxrActivitiesResPo.setLegacyResource(legacyResource); if
	 * (!Validations.isNullOrEmpty(taskOwner))
	 * xxrActivitiesResPo.setTaskOwner(taskOwner); if
	 * (!Validations.isNullOrEmpty(objectCode))
	 * xxrActivitiesResPo.setObjectCode(objectCode); if
	 * (!Validations.isNullOrEmpty(parentObjectCode))
	 * xxrActivitiesResPo.setParentObjectCode(parentObjectCode); if
	 * (x.getCompletePercentage() != null)
	 * xxrActivitiesResPo.setCompletePercentage(x.getCompletePercentage()); if
	 * (!Validations.isNullOrEmpty(x.getCompletionFlag()))
	 * xxrActivitiesResPo.setCompletionFlag(x.getCompletionFlag()); if
	 * (x.getEndDate() != null) xxrActivitiesResPo.setEndDate(x.getEndDate()); if
	 * (!Validations.isNullOrEmpty(x.getPreReqTask()))
	 * xxrActivitiesResPo.setPreReqTask(x.getPreReqTask());
	 * xxrActivitiesResPo.setSeq(x.getSeq()); if (x.getStartDate() != null)
	 * xxrActivitiesResPo.setStartDate(x.getStartDate()); if (x.getTaskId() != null)
	 * xxrActivitiesResPo.setTaskId(x.getTaskId()); if
	 * (!Validations.isNullOrEmpty(x.getTaskName()))
	 * xxrActivitiesResPo.setTaskName(x.getTaskName()); if
	 * (!Validations.isNullOrEmpty(x.getTaskNum()))
	 * xxrActivitiesResPo.setTaskNum(x.getTaskNum()); if
	 * (!Validations.isNullOrEmpty(x.getTaskStatus()))
	 * xxrActivitiesResPo.setTaskStatus(x.getTaskStatus()); if
	 * (!Validations.isNullOrEmpty(x.getTaskType()))
	 * xxrActivitiesResPo.setTaskType(x.getTaskType()); if (x.getWeightage() !=
	 * null) xxrActivitiesResPo.setWeightage(x.getWeightage());
	 * xxrActivitiesResPo.setWbsId(x.getWbsId());
	 * xxrActivitiesResPo.setProjectId(x.getProjectId());
	 *
	 * xxrActivitiesResPoList.add(xxrActivitiesResPo); });
	 *
	 * } catch (Exception e) { throw new Exception(e.getMessage()); } return
	 * xxrActivitiesResPoList; }
	 */
	@Override
	public List<String> getPreRequisiteTaskLov(Long projectId, String taskNumber, Long objectId, Long podId)
			throws Exception {
		log.info("Start of getPreRequisiteTaskLov Method in Service ####");
		List<String> preRequisiteTaskList = new ArrayList<>();
		try {
			xxrActivitiesRepository.getActivityLinesById(projectId, podId).stream().forEach(x -> {
				if (!x.getTaskNum().equals(taskNumber) && x.getObjectId() == objectId)
					preRequisiteTaskList.add(x.getTaskNum());
			});
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return preRequisiteTaskList;
	}

	@Override
	public DataResPo updateProjectWbsSelections(List<ProjectWbsPo> projectWbsPo) throws Exception {
		log.info("Start of updateProjectWbsSelections Method in Service ####");
		List<XxrProjectWbs> xxrProjectWbsTab = new ArrayList<>();
		XxrProjectWbs projectWbsTab = new XxrProjectWbs();
		// String result = "";
		DataResPo dataResPo = new DataResPo();
		try {
			xxrProjectWbsTab = xxrProjectWbsTabRepository.getByprojectId(projectWbsPo.get(0).getProjectId(),
					projectWbsPo.get(0).getPodId());
			for (ProjectWbsPo projectWbs : projectWbsPo) {
				projectWbsTab = xxrProjectWbsTab.stream()
						.filter(x -> x.getObjectId() == projectWbs.getObjectId()
								&& x.getParentObjectId() == projectWbs.getParentObjectId())
						.findFirst().get();
				projectWbsTab.setSelectedFlag(projectWbs.getSelectedFlag());
				xxrProjectWbsTabRepository.save(projectWbsTab);
			}
			dataResPo.setMessage("WBS Saved Successfully");
		} catch (Exception e) {
			dataResPo.setError("Something went wrong");
			throw new Exception(e.getMessage());
		}
		return dataResPo;
	}

	@Override
	public ActivitiesResPo upsertProjectActivities(List<CrProjectActivities> activitiesPo, HttpServletRequest request)
			throws BadRequestException, Exception {
		log.info("Start of upsertProjectActivities Method in Service ####");

		//List<CrProjectActivities> xxrActivitiesResp = new ArrayList<>();
		ActivitiesResPo activitiesResPo = new ActivitiesResPo();
		String msg = "";

		try {
			List<CrProjectActivities> res=saveOrUpdateProjectActivities(activitiesPo);

			activitiesResPo.setMessage(msg);
			if (res.size()>0){
				activitiesResPo.setCrActivities(res);
			}else{
				System.out.println("No data Found--.>");
			}

//		} catch (BadRequestException e) {
//			e.printStackTrace();
//			throw new BadRequestException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return activitiesResPo;
	}



	@Override
	public List<TaskBreakDownPo> getTaskBreakDown(Long projectId, Long podId) throws Exception {
		log.info("Start of getTaskBreakDown Method in Service ###");
		List<TaskBreakDownPo> taskBreakDownList = new ArrayList<>();
		try {
			taskBreakDownList = xxrLookUpValuesRepository.getTaskBreakDown(projectId, podId);

			/*
			 * taskBreakDownList.stream().forEach(x->{
			 * if(!Validations.isNullOrEmpty(x.getPreReqTask())) {
			 *
			 * x.setPredecessor(xxrActivitiesRepository.getTaskIdByPreReqTask(x.
			 * getPreReqTask(),projectId)); } });
			 */

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return taskBreakDownList;
	}

	@Override
	public SaveProjectHeaderResponsePo copyProject(CopyProjectRequestPo copyProjectRequest, HttpServletRequest request)
			throws ValidationException, Exception {
		// TODO Auto-generated method stub
		log.info("Start of copyProject Method in Service ###");
		SaveProjectHeadersPo projectHeadersPo = new SaveProjectHeadersPo();
		SaveProjectHeaderResponsePo saveProjectHeaderResponsePo = new SaveProjectHeaderResponsePo();
		String message = "";
		Long projectId = null;
		try {

			projectId = xxrProjectsRepository.getProjectId(copyProjectRequest.getProjectName(),
					copyProjectRequest.getNewPodId());
			if (projectId != null)
				throw new ValidationException(
						"This Project already exists in this pod which you are trying to copy,Please select diff pod");

			XxrProjects xxrProjects = xxrProjectsRepository.getProject(copyProjectRequest.getProjectName(),
					copyProjectRequest.getOldPodId());

			projectHeadersPo.setProjectName(xxrProjects.getProjectName());
			projectHeadersPo.setProjectStatus(copyProjectRequest.getProjectStatus());
			projectHeadersPo.setStartDate(copyProjectRequest.getStartDate());
			projectHeadersPo.setCompletionDate(copyProjectRequest.getCompletionDate());
			projectHeadersPo.setPodId(copyProjectRequest.getNewPodId());
			projectHeadersPo.setAccessLevel(xxrProjects.getAccessLevel());
			projectHeadersPo.setClientManager(xxrProjects.getClientManager());
			projectHeadersPo.setClientProjectNum(xxrProjects.getClientProjectNumber());
			projectHeadersPo.setDescription(xxrProjects.getDescription());
			projectHeadersPo.setKpiAggregationLevel(xxrProjects.getKpiAggLevel());
			projectHeadersPo.setProgramNumber(xxrProjects.getProgramNumber());
			projectHeadersPo.setProjectManager(xxrProjects.getProjecManager());

			// save project header
			message = saveProjectHeaderDaoImpl.saveProjectHeader(projectHeadersPo, request);
			log.info(message);

			if (!Validations.isNullOrEmpty(copyProjectRequest.getProjectName())
					&& copyProjectRequest.getNewPodId() != null)
				projectId = xxrProjectsRepository.getProjectId(copyProjectRequest.getProjectName(),
						copyProjectRequest.getNewPodId());
			if (projectId == null)
				throw new Exception(
						"Please contact System Administrator there is an error while processing the request");
			else {
				// copy project wbs
				copyProjectWbs(projectId, copyProjectRequest);
				// copy project activity lines
				copyProjectActivities(projectId, copyProjectRequest, request);
				saveProjectHeaderResponsePo.setProjectId(projectId);
				saveProjectHeaderResponsePo.setProjectName(copyProjectRequest.getProjectName());
				saveProjectHeaderResponsePo.setMessage("Successfully copied project");
			}

		} catch (ValidationException e) {
			throw new ValidationException(e.getMessage());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return saveProjectHeaderResponsePo;
	}

	private void copyProjectWbs(Long projectId, CopyProjectRequestPo copyProjectRequest) throws Exception {
		log.info("copyProjectWbs######");
		try {
			List<XxrProjectWbs> projectWbsList = xxrProjectWbsTabRepository.getByprojectId(projectId,
					copyProjectRequest.getOldPodId());
			for (XxrProjectWbs xxrProjectWbsOld : projectWbsList) {

				XxrProjectWbs xxrProjectWbsNew = new XxrProjectWbs();

				xxrProjectWbsNew.setProjectId(projectId);
				xxrProjectWbsNew.setPodId(copyProjectRequest.getNewPodId());
				xxrProjectWbsNew.setEndDate(xxrProjectWbsOld.getEndDate());
				xxrProjectWbsNew.setStartDate(xxrProjectWbsOld.getStartDate());
				xxrProjectWbsNew.setObjectId(xxrProjectWbsOld.getObjectId());
				xxrProjectWbsNew.setParentObjectId(xxrProjectWbsOld.getParentObjectId());
				xxrProjectWbsNew.setSelectedFlag(xxrProjectWbsOld.getSelectedFlag());
				xxrProjectWbsNew.setSeq(xxrProjectWbsOld.getSeq());
				xxrProjectWbsNew.setWbsId(xxrProjectWbsOld.getWbsId());

				xxrProjectWbsTabRepository.save(xxrProjectWbsNew);
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	private void copyProjectActivities(Long projectId, CopyProjectRequestPo copyProjectRequest,
									   HttpServletRequest request) throws Exception {
		String msg = "";
		List<ActivitiesPo> activitiesPo = new ArrayList<>();
		try {
			List<XxrActivities> activityLines = xxrActivitiesRepository.getActivityLinesById(projectId,
					copyProjectRequest.getOldPodId());

			for (XxrActivities xxrActivitiesOld : activityLines) {
				ActivitiesPo activitiesPoReq = new ActivitiesPo();
				activitiesPoReq.setWeightage(xxrActivitiesOld.getWeightage());
				activitiesPoReq.setWbsId(xxrActivitiesOld.getWbsId());
				activitiesPoReq.setClientResourceId(xxrActivitiesOld.getClientResourceId());
				activitiesPoReq.setCloudResourceId(xxrActivitiesOld.getCloudResourceId());
				activitiesPoReq.setCompletePercentage(xxrActivitiesOld.getCompletePercentage());
				activitiesPoReq.setCompletionFlag(xxrActivitiesOld.getCompletionFlag());
				activitiesPoReq.setDestinationResourceId(xxrActivitiesOld.getDestinationResourceId());
				activitiesPoReq.setEndDate(xxrActivitiesOld.getEndDate());
				activitiesPoReq.setStartDate(xxrActivitiesOld.getStartDate());
				activitiesPoReq.setSeq(xxrActivitiesOld.getSeq());
				activitiesPoReq.setTaskId(xxrActivitiesOld.getTaskId());
				activitiesPoReq.setTaskName(xxrActivitiesOld.getTaskName());
				activitiesPoReq.setTaskNum(xxrActivitiesOld.getTaskNum());
				activitiesPoReq.setTaskOwnerId(xxrActivitiesOld.getTaskOwnerId());
				activitiesPoReq.setTaskStatus(xxrActivitiesOld.getTaskStatus());
				activitiesPoReq.setTaskType(xxrActivitiesOld.getTaskType());
				activitiesPoReq.setPreReqTask(xxrActivitiesOld.getPreReqTask());
				activitiesPoReq.setIntegratorResourceId(xxrActivitiesOld.getIntegratorResourceId());
				activitiesPoReq.setLegacyResourceId(xxrActivitiesOld.getLegacyResourceId());
				activitiesPoReq.setObjectId(xxrActivitiesOld.getObjectId());
				activitiesPoReq.setParentObjectId(xxrActivitiesOld.getParentObjectId());
				activitiesPoReq.setProjectId(projectId);
				activitiesPoReq.setPodId(copyProjectRequest.getNewPodId());

				activitiesPo.add(activitiesPoReq);

			}

			msg = saveProjectActivitiesDaoImpl.saveProjectActivities(activitiesPo, request);
			log.info(msg);

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public List<TaskOwnerLov> getTaskOwnerLov(Long projectId) throws Exception {
		// TODO Auto-generated method stub
		log.info("Start of getTaskOwnerLov#######");
		List<TaskOwnerLov> taskOwnerLi = new ArrayList<>();
		try {
			taskOwnerLi = xxrRoleObjectLinksRepository.getTaskOwnerLov(projectId);
//			taskOwnerLi = taskOwnerLi.stream().filter(Utils.distinctByKey(TaskOwnerLov::getTaskOwnerId))
//					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return taskOwnerLi;
	}


	@Override
	public List<CrProjectActivities> getProjectLinesById(ProjectLinesReqPo projectLinesReqPo,HttpHeaders httpHeaders) throws Exception {
		// TODO Auto-generated method stub
		log.info("Start of getProjectLinesById#######");
		List<CrProjectActivities> projActivitiesLi = new ArrayList<>();
		Pageable pageable = PageRequest.of(projectLinesReqPo.getPageNo(),
				projectLinesReqPo.getPageSize(),
				Sort.by(projectLinesReqPo.getSortDirection(), projectLinesReqPo.getSortBy()));

		//Page<XxrProjActivitiesView> pageContent = xxrProjActivitiesViewRepository.findAllByProjectIdAndPodId(projectLinesReqPo.getProjectId(), projectLinesReqPo.getPodId(),pageable);
		Page<CrProjectActivities> pageContent = crProjectActivitiesRepo.findAllByProjectId(projectLinesReqPo.getProjectId(),pageable);

		httpHeaders.set("pagecount", String.valueOf(pageContent.getTotalPages()));
		httpHeaders.set("totalcount", String.valueOf(pageContent.getTotalElements()));
		if (pageContent.hasContent())
			projActivitiesLi = pageContent.getContent();
		log.info(projActivitiesLi.size() + "::::::count");
		return projActivitiesLi;
	}
	@Override
	public Object getProjectsAndObjects(Long clientId, Long podId,String projectCode, String bearerToken) {
		HttpHeaders header = new HttpHeaders();
		header.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		System.out.println("bearerToken-->"+bearerToken);
		header.set("Authorization",bearerToken);
		HttpEntity<String> entity = new HttpEntity<String>(header);
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;
		JsonNode name;
		List<ProjectsWithObjectsPo> pojos=null;
		List<ProjectsWithObjectsPo> newJsonNode =null;
		try {
			ResponseEntity<String> projectsPo = restTemplate.exchange(ConvertriteAdminHost+"/api/convertriteadmin/getProjectsAndObjects?clientId=" + clientId + "&podId=" + podId, HttpMethod.GET, entity, String.class);

			root = mapper.readTree(projectsPo.getBody());
			name = root.path("payload");

			newJsonNode = mapper.treeToValue(name, List.class);
			if(newJsonNode.size()>0){
				pojos = mapper.convertValue(name,new TypeReference<List<ProjectsWithObjectsPo>>() { });

			}else{
				return  "No data Found";
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		List<CrProjects> projList=new ArrayList<CrProjects>();
		try {

		List<CrProjectsObjects> crProjectsObjectsList=new ArrayList<>();
		for(ProjectsWithObjectsPo p:pojos) {
			if (p.getProjectCode().equals(projectCode)) {
				System.out.println(p.getProjectCode() + "if----->" + projectCode);
				CrProjects proj = new CrProjects();
			CrProjectsObjects obj = new CrProjectsObjects();
			proj.setProjectId(p.getProjectId());
			proj.setProjectName(p.getProjectName());
			proj.setProjectCode(p.getProjectCode());

			proj.setStartDate(new java.sql.Date(new java.util.Date().getTime()));
			proj.setLastUpdatedBy("ConvertRite");
			proj.setLastUpdateDate(new java.sql.Date(new java.util.Date().getTime()));
			int count = 0;
			List<ObjectsPo> objList = mapper.convertValue(p.getObjects(), new TypeReference<List<ObjectsPo>>() {
			});
			System.out.println(objList);
			for (ObjectsPo objPo : objList) {
				CrProjectsObjects crProjectsObjects = new CrProjectsObjects();
				crProjectsObjects.setProjectObjLinkId(count++);
				crProjectsObjects.setProjectId(p.getProjectId());
				crProjectsObjects.setProjectCode(p.getProjectCode());
				crProjectsObjects.setObjectId(objPo.getObjectId());
				crProjectsObjects.setObjectCode(objPo.getObjectCode());
				crProjectsObjects.setObjectName(objPo.getObjectName());
				//crProjectsObjects.setModuleCode(objPo.getModuleCode());
				if (!Objects.isNull(objPo.parentObjectId)) {
					for (ObjectsPo innerobjPo : objList) {
						if (innerobjPo.objectId.equals(objPo.parentObjectId)) {
							crProjectsObjects.setParentObjectCode(innerobjPo.objectCode);
							break;
						}
					}
				}
				crProjectsObjectsList.add(crProjectsObjects);
		}
			crProjectsObjectsRepo.saveAll(crProjectsObjectsList);
			projList.add(proj);
			}
		}
		//crProjectsObjectsRepo.saveAll(crProjectsObjectsList);
		crProjectsRepo.saveAll(projList);

		}catch (Exception e){
			e.printStackTrace();
			return e;
		}
		return projList;
	}

	@Override
	public Object insertProjectActivities(Long projectId) {
		List<CrProjectActivities> activitieList=new ArrayList<>();
		List<CrProjectsObjects> crProjectsObjectsList=crProjectsObjectsRepo.findAllByProjectId(projectId);
		String lookupSetName="Task Type";
		List<LovValuesPo> tasksList  = null;
		List<String> lookUpSetsList  =null;
		try {
			//tasksList = lovService.getLovByLookUpSetName(lookupSetName);
			LookUpSets lookUpSets=new LookUpSets();
			lookUpSetsList  =	lookUpSets.getList();
			log.info("Look Ups Size-->"+lookUpSetsList.size());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		int count=0;

		for (CrProjectsObjects crProjectsObject:crProjectsObjectsList){
			//for (LovValuesPo task:tasksList){
				for (String task:lookUpSetsList){
				CrProjectActivities activitie=new CrProjectActivities();
				activitie.setProjectId(crProjectsObject.getProjectId());
				//activitie.setTaskName(crProjectsObject.getObjectName()+"_"+task.getValue());
				activitie.setTaskName(crProjectsObject.getObjectName()+"_"+task);

				activitie.setObjectId(crProjectsObject.getObjectId());
				activitie.setSeq(count++);
				activitie.setTaskNum(""+count++);
				//activitie.setTaskType(task.getLable());
				activitie.setTaskType("Task Type");
				activitie.setLastUpdatedBy("ConvertRiteAdmin");
				activitie.setLastUpdateDate(new java.sql.Date(new java.util.Date().getTime()));
				activitieList.add(activitie);
			}
		}
		crProjectActivitiesRepo.saveAll(activitieList);
		return activitieList;
	}

	@Override
	public Object getParentObjects(Long projectId) {
		List<CrProjectsObjects> crProjectsObjects=null;
		try {
			crProjectsObjects =crProjectsObjectsRepo.getAllByProjectId(projectId);
		}catch (Exception e){
			e.printStackTrace();
		}
		return crProjectsObjects;
	}

	@Override
	public Object getObjectsByObjectCode(Long projectId,String objectCode) {
		List<CrProjectsObjects> crProjectsObjects=null;
		try {
			crProjectsObjects =crProjectsObjectsRepo.getAllByObjectCode(projectId,objectCode);
		}catch (Exception e){
			e.printStackTrace();
		}
		return crProjectsObjects;
	}

	@Override
	public Object getObjectsByUserId(Long userId,String bearerToken) {
		HttpHeaders header = new HttpHeaders();
		header.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		System.out.println("bearerToken-->"+bearerToken);
		header.set("Authorization",bearerToken);
		HttpEntity<String> entity = new HttpEntity<String>(header);
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;
		try {
			ResponseEntity<String> projectsPo = restTemplate.exchange(ConvertriteAdminHost + "/api/convertriteadmin/getObjectsByUserId/"+userId, HttpMethod.GET, entity, String.class);
			System.out.println("projectsPo-->"+projectsPo);
			root = mapper.readTree(projectsPo.getBody());
		}catch (Exception e){
			e.printStackTrace();
		}
			return root;
	}

}
