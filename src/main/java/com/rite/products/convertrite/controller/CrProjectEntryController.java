package com.rite.products.convertrite.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.rite.products.convertrite.model.CrProjectActivities;
import com.rite.products.convertrite.model.CrProjects;
import com.rite.products.convertrite.po.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rite.products.convertrite.Validations.Validations;
import com.rite.products.convertrite.exception.BadRequestException;
import com.rite.products.convertrite.exception.ConvertRiteException;
import com.rite.products.convertrite.exception.ValidationException;
import com.rite.products.convertrite.service.ProjectService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "/api/convertritecore")
public class CrProjectEntryController {

	@Autowired
	ProjectService projectService;

	private static final Logger log = LoggerFactory.getLogger(CrProjectEntryController.class);

	@ApiOperation(value = "This Api will load wbs")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/loadwbs")
	public List<XxrProjectWbsResPo> loadWbs(@RequestParam("projectId") Long projectId,
											@RequestParam("podId") Long podId, HttpServletRequest request) throws ConvertRiteException {
		log.info("Start of loadWbs Method in Controller ###");
		log.debug("projectId:::::::" + projectId);
		List<XxrProjectWbsResPo> xxrProjectWbsTab = new ArrayList<>();
		try {
			if (projectId != null)
				xxrProjectWbsTab = projectService.loadWbs(projectId, podId, request);
			/*
			 * else throw new ConvertRiteException("Missing projectId in the Request",
			 * HttpStatus.BAD_REQUEST);
			 */
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return xxrProjectWbsTab;
	}

	@ApiOperation(value = "This Api will save project headers")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/saveprojectheaders")
	public ResponseEntity<SaveProjectHeaderResponsePo> saveProjectHeaders(
			@RequestBody CrProjects projectHeadersPo, HttpServletRequest request) {
		log.info("Start of saveProjectHeaders Method in Controller ###");
		SaveProjectHeaderResponsePo saveProjectHeaderResponsePo = new SaveProjectHeaderResponsePo();
		try {

			if ( Validations.isNullOrEmpty(projectHeadersPo.getProjectName())
					|| Validations.isNullOrEmpty(projectHeadersPo.getProjecManager())
				)
				throw new BadRequestException(
						"projectName,projectManager,programNumber,clientManager,kpiAggregartionLevel,startDate,projectStatus and accessLevel are Mandatory fields");

			saveProjectHeaderResponsePo = projectService.saveProjectHeaders(projectHeadersPo, request);

		} catch (BadRequestException e) {
			log.error(e.getMessage());
			saveProjectHeaderResponsePo.setMessage("Error while saving project headers");
			saveProjectHeaderResponsePo.setError(e.getMessage());
			return new ResponseEntity<>(saveProjectHeaderResponsePo, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error(e.getMessage());
			saveProjectHeaderResponsePo.setMessage("Error while saving project headers");
			saveProjectHeaderResponsePo.setError(e.getMessage());
			return new ResponseEntity<SaveProjectHeaderResponsePo>(saveProjectHeaderResponsePo,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<SaveProjectHeaderResponsePo>(saveProjectHeaderResponsePo, new HttpHeaders(),
				HttpStatus.OK);
	}

	@ApiOperation(value = "This Api will copy project headers")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/copyproject")
	public ResponseEntity<SaveProjectHeaderResponsePo> copyProject(@RequestBody CopyProjectRequestPo copyProjectRequest,
																   HttpServletRequest request)  {
		log.info("Start of copyProject Method in Controller ###");
		SaveProjectHeaderResponsePo saveProjectHeaderResponsePo = new SaveProjectHeaderResponsePo();
		try {

			if (copyProjectRequest.getNewPodId() == null || copyProjectRequest.getOldPodId() == null
					|| Validations.isNullOrEmpty(copyProjectRequest.getProjectName())
					|| Validations.isNullOrEmpty(copyProjectRequest.getProjectStatus())
					|| copyProjectRequest.getStartDate() == null || copyProjectRequest.getCompletionDate() == null)
				throw new BadRequestException(
						"podId,projectName,startDate,projectStatus and completiondate are Mandatory fields");

			saveProjectHeaderResponsePo = projectService.copyProject(copyProjectRequest, request);

		} catch (BadRequestException e) {
			log.error(e.getMessage());
			saveProjectHeaderResponsePo.setMessage("Error while copying project headers");
			saveProjectHeaderResponsePo.setError(e.getMessage());
			return new ResponseEntity<SaveProjectHeaderResponsePo>(saveProjectHeaderResponsePo, HttpStatus.BAD_REQUEST);
		}  catch (ValidationException e) {
			log.error(e.getMessage());
			saveProjectHeaderResponsePo.setMessage("Error while copying project headers");
			saveProjectHeaderResponsePo.setError(e.getMessage());
			return new ResponseEntity<SaveProjectHeaderResponsePo>(saveProjectHeaderResponsePo, HttpStatus.OK);
		}catch (Exception e) {
			log.error(e.getMessage());
			saveProjectHeaderResponsePo.setMessage("Error while copying project headers");
			saveProjectHeaderResponsePo.setError(e.getMessage());
			return new ResponseEntity<SaveProjectHeaderResponsePo>(saveProjectHeaderResponsePo,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<SaveProjectHeaderResponsePo>(saveProjectHeaderResponsePo, new HttpHeaders(),
				HttpStatus.OK);
	}

	@ApiOperation(value = "This Api will load task")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/loadtask")
	public ResponseEntity<DataResPo> loadTask(@RequestParam("projectId") Long projectId,
											  @RequestParam("podId") Long podId, HttpServletRequest request) throws ConvertRiteException {
		log.info("Start of loadtask Method in Controller ###");
		log.debug("projectId:::::::" + projectId);
		String result = "";
		DataResPo dataResPo = new DataResPo();
		try {
			if (projectId != null)
				result = projectService.loadTask(projectId, podId, request);

			if (result.equalsIgnoreCase("Success"))
				dataResPo.setMessage("LoadWBS data loaded successfully");
			else
				dataResPo.setError("Something went wrong");

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<DataResPo>(dataResPo, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "This Api return all project headers")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@GetMapping("/getallprojectheaders")
	public ResponseEntity<Object> getAllProjectHeaders() throws ConvertRiteException {
		log.info("Start of getAllProjectHeaders Method in Controller ###");
		List<CrProjects> xxrProjectsList = new ArrayList<>();
		try {
			xxrProjectsList = projectService.getAllProjectHeaders();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(xxrProjectsList, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "This Api return all project lines based on projectId")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@GetMapping("/getprojectlinesbyid")
	public ResponseEntity<List<CrProjectActivities>> getProjectLinesById(ProjectLinesReqPo projectLinesReqPo) throws ConvertRiteException {
		log.info("Start of getProjectLinesById Method in Controller ###");
		List<CrProjectActivities> xxrActivitiesList = new ArrayList<>();
		HttpHeaders httpHeaders =new HttpHeaders();
		try {
			xxrActivitiesList = projectService.getProjectLinesById(projectLinesReqPo,httpHeaders);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(

					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<CrProjectActivities>>(xxrActivitiesList, httpHeaders, HttpStatus.OK);
	}


	/*
	 * @ApiOperation(value = "This Api return all project lines based on projectId")
	 *
	 * @ApiResponses(value = { @ApiResponse(code = 200, message =
	 * "Successful Response"),
	 *
	 * @ApiResponse(code = 500, message = "Server Side Error"),
	 *
	 * @ApiResponse(code = 400, message = "Bad Request") })
	 *
	 * @GetMapping("/getprojectlinesbyid") public
	 * ResponseEntity<List<XxrActivitiesResPo>>
	 * getProjectLinesById(@RequestParam("projectId") Long projectId,
	 *
	 * @RequestParam("podId") Long podId) throws ConvertRiteException {
	 * log.info("Start of getProjectLinesById Method in Controller ###");
	 * List<XxrActivitiesResPo> xxrActivitiesList = new ArrayList<>(); try {
	 * xxrActivitiesList = projectService.getProjectLinesById(projectId, podId);
	 *
	 * } catch (Exception e) { log.error(e.getMessage()); throw new
	 * ConvertRiteException(
	 * "Please contact System Administrator there is an error while processing the request"
	 * , HttpStatus.INTERNAL_SERVER_ERROR); } return new
	 * ResponseEntity<List<XxrActivitiesResPo>>(xxrActivitiesList, new
	 * HttpHeaders(), HttpStatus.OK); }
	 */
	@ApiOperation(value = "This Api return pre reqisite task lov")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@GetMapping("/getprerequisitetask")
	public ResponseEntity<List<String>> getPreRequisiteTaskLov(@RequestParam("projectId") Long projectId,
															   @RequestParam("taskNumber") String taskNumber, @RequestParam("objectId") Long objectId,
															   @RequestParam("podId") Long podId) throws ConvertRiteException {
		log.info("Start of getPreRequisiteTaskLov Method in Controller ###");
		List<String> preRequisiteTaskList = new ArrayList<>();
		try {
			preRequisiteTaskList = projectService.getPreRequisiteTaskLov(projectId, taskNumber, objectId, podId);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<String>>(preRequisiteTaskList, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "This Api is to update selectionFlag based on user selection")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PutMapping("/updateprojectwbsselections")
	public ResponseEntity<DataResPo> updateProjectWbsSelections(@RequestBody List<ProjectWbsPo> projectWbsPo)
			throws ConvertRiteException {
		log.info("Start of updateProjectWbsSelections Method in Controller ###");
		DataResPo dataResPo = new DataResPo();
		try {
			dataResPo = projectService.updateProjectWbsSelections(projectWbsPo);
		} catch (Exception e) {
			dataResPo.setError("Something went wrong");
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<DataResPo>(dataResPo, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "This Api is to save or update Activity lines")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/upsertprojectactivities")
	public ResponseEntity<ActivitiesResPo> upsertProjectActivities(@RequestBody List<CrProjectActivities> activitiesPo,
																   HttpServletRequest request)  {
		log.info("Start of upsertProjectActivities Method in Controller ###");
		// List<XxrActivities> xxrActivities = new ArrayList<>();
		ActivitiesResPo activitiesResPo = new ActivitiesResPo();
		//if (activitiesPo.get(0).getProjectId() != null) {
		try {
			activitiesResPo = projectService.upsertProjectActivities(activitiesPo, request);
		} catch (BadRequestException e) {
			log.error(e.getMessage());
			activitiesResPo.setMessage("Error while saving project activities");
			activitiesResPo.setError(e.getMessage());
			return new ResponseEntity<ActivitiesResPo>(activitiesResPo, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error(e.getMessage());
			activitiesResPo.setMessage("Error while saving project activities");
			activitiesResPo.setError(e.getMessage());
			return new ResponseEntity<ActivitiesResPo>(activitiesResPo, HttpStatus.INTERNAL_SERVER_ERROR);
		}
//		} else {
//			activitiesResPo.setMessage("Error while saving project activities");
//			activitiesResPo.setError("Missing projectId in the Request");
//			return new ResponseEntity<ActivitiesResPo>(activitiesResPo, HttpStatus.BAD_REQUEST);
//		}

		return new ResponseEntity<ActivitiesResPo>(activitiesResPo, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "This Api return taskbreak down")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error") })
	@GetMapping("/gettaskbreakdown")
	public ResponseEntity<List<TaskBreakDownPo>> getTaskBreakDown(@RequestParam("projectId") Long projectId,
																  @RequestParam("podId") Long podId) throws ConvertRiteException {
		log.info("Start of getTaskBreakDown Method in Controller ###");
		List<TaskBreakDownPo> taskBreakDownList = new ArrayList<>();
		try {
			taskBreakDownList = projectService.getTaskBreakDown(projectId, podId);

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<TaskBreakDownPo>>(taskBreakDownList, new HttpHeaders(), HttpStatus.OK);

	}

	@ApiOperation(value = "This Api return task owner lov")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error") })
	@GetMapping("/gettaskownerlov")
	public ResponseEntity<List<TaskOwnerLov>> getTaskOwnerLov(@RequestParam("projectId") Long projectId)
			throws ConvertRiteException {
		List<TaskOwnerLov> taskOwnerLi = new ArrayList<>();
		try {
			taskOwnerLi=projectService.getTaskOwnerLov(projectId);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<TaskOwnerLov>>(taskOwnerLi, HttpStatus.OK);
	}

	@GetMapping("/getProjectsAndObjects")
	public ResponseEntity<Object> getProjectsAndObjects(@RequestParam(name = "clientId") Long clientId, @RequestParam(name = "podId") Long podId,@RequestParam(name = "projectCode") String projectCode,@RequestHeader("Authorization") String bearerToken) {
		Object response = null;
		try {
			response = projectService.getProjectsAndObjects(clientId, podId, projectCode,bearerToken);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	@GetMapping("/getParentObjectsByProjectId/{projectId}")
	public ResponseEntity<Object> getParentObjects(@PathVariable Long projectId) {
		Object response = null;
		try {
			response = projectService.getParentObjects(projectId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	@GetMapping("/getObjectsByObjectCode/{projectId}/{objectCode}")
	public ResponseEntity<Object> getObjectsByObjectCode(@PathVariable Long projectId,@PathVariable String objectCode) {
		Object response = null;
		try {
			response = projectService.getObjectsByObjectCode(projectId,objectCode);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	@GetMapping("/insertProjectActivities")
	public ResponseEntity<Object> insertProjectActivities(@RequestParam(name = "projectId") Long projectId) {
		Object response = null;
		try {
			response = projectService.insertProjectActivities(projectId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	@GetMapping("/getObjectsByUserId/{userId}")
	public ResponseEntity<Object> getObjectsByUserId(@PathVariable Long userId,@RequestHeader("Authorization") String bearerToken) {
		Object response = projectService.getObjectsByUserId(userId,bearerToken);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
}
