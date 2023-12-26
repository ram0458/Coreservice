package com.rite.products.convertrite.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rite.products.convertrite.model.CrSourceTemplateColumns;
import com.rite.products.convertrite.model.CrSourceTemplateHeaders;
import com.rite.products.convertrite.model.CrSourceTemplateHeadersView;
import com.rite.products.convertrite.po.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rite.products.convertrite.Validations.Validations;
import com.rite.products.convertrite.exception.BadRequestException;
import com.rite.products.convertrite.exception.ConvertRiteException;
import com.rite.products.convertrite.exception.ValidationException;
import com.rite.products.convertrite.service.XxrSourceService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/convertritecore/source")
public class SourceTemplateController {
	private static final Logger log = LoggerFactory.getLogger(SourceTemplateController.class);

	@Autowired
	XxrSourceService xxrSourceService;

	@ApiOperation(value = "This Api return all source templates available")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error") })
	@GetMapping("/getsourcetemplates")
	public ResponseEntity<Object> getSourceTemplates()
			throws ConvertRiteException {
		log.info("Start of getSourceTemplates Method in SourceTemplateController ###");

		List<CrSourceTemplateHeadersView> sourceTemplateList = new ArrayList<>();
		try {
			sourceTemplateList = xxrSourceService.getSourceTemplates();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Object>(sourceTemplateList, HttpStatus.OK);
	}

	/*
	 * @ApiOperation(value = "This Api return all source templates available")
	 * 
	 * @ApiResponses(value = { @ApiResponse(code = 200, message =
	 * "Successful Response"),
	 * 
	 * @ApiResponse(code = 500, message = "Server Side Error") })
	 * 
	 * @GetMapping("/getsourcetemplates") public
	 * ResponseEntity<List<SourceTemplateHeadersResPo>> getSourceTemplates() throws
	 * ConvertRiteException {
	 * log.info("Start of getSourceTemplates Method in SourceTemplateController ###"
	 * );
	 * 
	 * List<SourceTemplateHeadersResPo> sourceTemplateList = new ArrayList<>(); try
	 * { sourceTemplateList = xxrSourceService.getSourceTemplates(); } catch
	 * (Exception e) { log.error(e.getMessage()); throw new ConvertRiteException(
	 * "Please contact System Administrator there is an error while processing the request"
	 * , HttpStatus.INTERNAL_SERVER_ERROR); }
	 * 
	 * return new
	 * ResponseEntity<List<SourceTemplateHeadersResPo>>(sourceTemplateList,
	 * HttpStatus.OK); }
	 */

	@ApiOperation(value = "This Api return SourceTemplate based on templateId")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@GetMapping("/getsourcetemplateheaderbyid")
	public ResponseEntity<List<SourceTemplateHeadersResPo>> getSourceTemplateHeaderById(
			@RequestParam("templateId") Long templateId) throws ConvertRiteException {
		log.info("Start of GetSourceTemplateHeaderById Method in SourceTemplateController ###");
		log.debug("TemplateId::::::::" + templateId);

		List<SourceTemplateHeadersResPo> sourceTemplateList = new ArrayList<>();
		try {
			if (templateId != null)
				sourceTemplateList = xxrSourceService.getSourceTemplateHeaderById(templateId);
			/*
			 * else throw new ConvertRiteException("Missing TemplateId in Request",
			 * HttpStatus.BAD_REQUEST);
			 */
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<SourceTemplateHeadersResPo>>(sourceTemplateList, HttpStatus.OK);
	}

	/*
	 * @ApiOperation(value =
	 * "This Api return already existing source templates based on user input data")
	 * 
	 * @ApiResponses(value = { @ApiResponse(code = 200, message =
	 * "Successful Response"),
	 * 
	 * @ApiResponse(code = 500, message = "Server Side Error"),
	 * 
	 * @ApiResponse(code = 400, message = "Bad Request") })
	 * 
	 * @PostMapping("/getsourcetemplatesbypo") public
	 * ResponseEntity<List<SourceTemplateHeaders>> getSourceTemplatesByPo(
	 * 
	 * @RequestBody SourceTemplatePo sourceTemplatePo) throws ConvertRiteException {
	 * log.info("Start of getSourceTemplatesByPo Method in Controller ###");
	 * List<SourceTemplateHeaders> sourceTemplateHeaderList = new ArrayList<>(); try
	 * { if (!Validations.isNullOrEmpty(sourceTemplatePo.getObjectCode()))
	 * sourceTemplateHeaderList =
	 * xxrSourceService.getSourceTemplatesByPo(sourceTemplatePo); else throw new
	 * ConvertRiteException("Missing ObjectCode in Request Object",
	 * HttpStatus.BAD_REQUEST); } catch (Exception e) { log.error(e.getMessage());
	 * throw new ConvertRiteException(
	 * "Please contact System Administrator there is an error while processing the request"
	 * , HttpStatus.INTERNAL_SERVER_ERROR); } return new
	 * ResponseEntity<List<SourceTemplateHeaders>>(sourceTemplateHeaderList, new
	 * HttpHeaders(), HttpStatus.OK); }
	 */

	@ApiOperation(value = "This Api return SourceTemplte Columns based on templateId")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@GetMapping("/getsourcetemplatecolumns")
	public ResponseEntity<Object> getSourceTemplateColumns(
			@RequestParam("templateId") Long templateId) throws ConvertRiteException {
		log.info("Start of getSourceTemplateColumns Method in SourceTemplateController ###");
		log.debug("TemplateId::::::::" + templateId);

		List<Object> sourceTemplateList = new ArrayList<>();
		try {
			if (templateId != null)
				sourceTemplateList = xxrSourceService.getSourceTemplateColumns(templateId);
			/*
			 * else throw new ConvertRiteException("Missing TemplateId in Request",
			 * HttpStatus.BAD_REQUEST);
			 */
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Object>(sourceTemplateList, HttpStatus.OK);
	}

	@ApiOperation(value = "This Api return SourceColumns based on SourceTableName")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@GetMapping("/getsourcecolumnsbyname")
	public ResponseEntity<List<CrColumnPo>> getSourceColumnsByName(
			@RequestParam("sourceTableName") String sourceTableName) throws ConvertRiteException {
		log.info("Start of getSourceColumnsByName Method in SourceTemplateController ###");
		log.debug("sourceTableName::::::::" + sourceTableName);
		List<CrColumnPo> sourceColumnsList = new ArrayList<>();
		try {
			if (!Validations.isNullOrEmpty(sourceTableName))
				sourceColumnsList = xxrSourceService.getSourceColumnsByName(sourceTableName);
			/*
			 * else throw new ConvertRiteException("Missing sourceTableName in Request",
			 * HttpStatus.BAD_REQUEST);
			 */
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<CrColumnPo>>(sourceColumnsList, HttpStatus.OK);
	}

	@ApiOperation(value = "This Api return SourceColumns based on SourceTableId")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@GetMapping("/getsourcecolumnsbyId")
	public ResponseEntity<List<ColumnPo>> getSourceColumnsById(@RequestParam("sourceTableId") Long sourceTableId)
			throws ConvertRiteException {
		log.info("Start of getSourceColumnsById Method in SourceTemplateController ###");
		log.debug("sourceTableId::::::::" + sourceTableId);

		List<ColumnPo> sourceColumnsList = new ArrayList<>();
		try {
			if (sourceTableId != null)
				sourceColumnsList = xxrSourceService.getSourceColumnsById(sourceTableId);
			/*
			 * else throw new ConvertRiteException("Missing sourceTableId in Request",
			 * HttpStatus.BAD_REQUEST);
			 */
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<ColumnPo>>(sourceColumnsList, HttpStatus.OK);
	}

	@ApiOperation(value = "This Api is to save sourcetemplateheaders")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error") })
	@PostMapping("/savesourcetemplateheaders")
	public ResponseEntity<Object> saveSourceTemplateHeaders(
			@RequestBody CrSourceTemplateHeaders sourceHeader, HttpServletRequest request) {
		log.info("Start of saveSourceTemplateHeaders Method in Controller ###");
		SaveTemplateHeaderResponsePo saveTemplateHeaderResponsePo = new SaveTemplateHeaderResponsePo();

		try {

//			if (sourceHeader.getObjectId() == null || sourceHeader.getParentObjectId() == null
//					|| Validations.isNullOrEmpty(sourceHeader.getTemplateName()) || sourceHeader.getProjectId() == null)
//				throw new BadRequestException(
//						"objectId,parentObjectId,templateName and projectId are Mandatory fields");

			saveTemplateHeaderResponsePo = xxrSourceService.saveSourceTemplateHeaders(sourceHeader, request);
		} catch (Exception e) {
			log.error(e.getMessage());
			saveTemplateHeaderResponsePo
					.setMessage("Please contact System Administrator there is an error while processing the request");
			saveTemplateHeaderResponsePo.setError(e.getMessage());
		}
		return new ResponseEntity<Object>(saveTemplateHeaderResponsePo,
				HttpStatus.OK);
	}

	@ApiOperation(value = "This Api is to save sourcetemplatecolumns")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error") })
	@PostMapping("/savesourcetemplatecolumns")
	public ResponseEntity<SaveSourceTemplateColumnsResPo> saveSourceTemplateColumns(
			@RequestBody List<CrSourceTemplateColumns> sourceTemplateColumnsPo, HttpServletRequest request) {
		log.info("Start of saveSourceTemplateColumns Method in Controller ###");
		SaveSourceTemplateColumnsResPo saveSourceTemplateColumnsResPo = new SaveSourceTemplateColumnsResPo();

		try {

			saveSourceTemplateColumnsResPo = xxrSourceService.saveSourceTemplateColumns(sourceTemplateColumnsPo,
					request);

		} catch (BadRequestException e) {
			log.error(e.getMessage());
			saveSourceTemplateColumnsResPo.setMessage(e.getMessage());
			saveSourceTemplateColumnsResPo.setError("Error while saving source template cloumns");
			return new ResponseEntity<SaveSourceTemplateColumnsResPo>(saveSourceTemplateColumnsResPo,
					HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error(e.getMessage());
			saveSourceTemplateColumnsResPo
					.setMessage("Please contact System Administrator there is an error while processing the request");
			saveSourceTemplateColumnsResPo.setError(e.getMessage());
			return new ResponseEntity<SaveSourceTemplateColumnsResPo>(saveSourceTemplateColumnsResPo,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<SaveSourceTemplateColumnsResPo>(saveSourceTemplateColumnsResPo, new HttpHeaders(),
				HttpStatus.OK);

	}

	@ApiOperation(value = "This Api Create Source Stagging Table")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/createstgtable")
	public ResponseEntity<SourceStagingTablePo> createSourceStgTable(@RequestParam("environment") String environment,@RequestParam("tableId") Long tableId,
			@RequestParam("templateId") Long templateId,@RequestParam("templateCode") String templateCode, HttpServletRequest request)
			throws ConvertRiteException {
		log.info("Start of createSourceStgTable Method in Controller ###");

		SourceStagingTablePo stagingPo = new SourceStagingTablePo();
		try {
			if (tableId  != null && templateId != null&&templateCode!=null)
				stagingPo = xxrSourceService.createSourceStaggingTab(tableId, templateId,templateCode,environment, request);

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<SourceStagingTablePo>(stagingPo, new HttpHeaders(), HttpStatus.OK);

	}
	@PostMapping("/loadSourceTemplateMetaData")
	public ResponseEntity<Object> loadSourceTemplateMetaData(@RequestParam("environment") String type,@RequestParam("fileName") String fileName,
																	 @RequestParam("objectId") Long objectId, HttpServletRequest request)
			throws ConvertRiteException {
		log.info("Start of loadSourceTemplateMetaData Method in Controller ###");
		Object stagingPo = new Object();
		try {
			if (!(Validations.isNullOrEmpty(fileName)) && objectId != null)
				stagingPo = xxrSourceService.loadSourceTemplateMetaData(type,fileName, objectId, request);

		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<Object>(e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

		return new ResponseEntity<Object>(stagingPo, new HttpHeaders(), HttpStatus.OK);

	}
	@PostMapping("/loadSourceData")
	public ResponseEntity<Object> loadSourceData(@RequestParam("dataFileName") String dataFileName,@RequestParam("batchName") String batchName,
																	 @RequestParam("templateId") Long templateId,	@RequestParam("templateName") String templateName, HttpServletRequest request)
			throws ConvertRiteException {
		log.info("Start of createSourceStgTable Method in Controller ###");
		Object	res=null;
		try {
			if (dataFileName  != null && templateId != null&&batchName!=null)
				res = xxrSourceService.loadSourceData(dataFileName, batchName,templateId,templateName, request);

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Object>(res, new HttpHeaders(), HttpStatus.OK);

	}
	@ApiOperation(value = "This Api Create Dynamic View")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/createdynamicview")
	public ResponseEntity<CreateDynamicViewPo> createDynamicView(@RequestParam("templateId") Long templateId,
			@RequestParam("stgTableName") String stgTableName, HttpServletRequest request) throws ConvertRiteException {
		log.info("Start of createDynamicView Method in Controller ###");
		log.debug("templateId:::::::" + templateId + "stgTableName:::::::" + stgTableName);
		CreateDynamicViewPo createDynamicViewPo = new CreateDynamicViewPo();
		try {
			if (templateId != null && !Validations.isNullOrEmpty(stgTableName))
				createDynamicViewPo = xxrSourceService.createDynamicView(templateId, stgTableName, request);
			/*
			 * else throw new
			 * ConvertRiteException("Missing templateId or stgTableName in the Request",
			 * HttpStatus.BAD_REQUEST);
			 */
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<CreateDynamicViewPo>(createDynamicViewPo, new HttpHeaders(), HttpStatus.OK);

	}

	@ApiOperation(value = "This Api return all SourceTableNames and TableId's")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error") })
	@GetMapping("/getsourcetablenames")
	public ResponseEntity<List<SourceTablesPo>> getSourceTableNames(@RequestParam("objectId") Long objectId)
			throws ConvertRiteException {
		log.info("Start of getSourceTableNames Method in SourceTemplateController ###");

		List<SourceTablesPo> sourceTablesList = new ArrayList<>();
		try {
			if (objectId != null)
				sourceTablesList = xxrSourceService.getSourceTableNames(objectId);
			/*
			 * else throw new ConvertRiteException("objectCode is Mandatory in the Request",
			 * HttpStatus.BAD_REQUEST);
			 */
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<SourceTablesPo>>(sourceTablesList, HttpStatus.OK);
	}

	@ApiOperation(value = "This Api is for copy cloudtemplate")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/copysourcetemplate")
	public ResponseEntity<SaveTemplateHeaderResponsePo> copySourceTemplate(
			@RequestParam("newTemplateName") String newTemplateName, @RequestParam("templateId") Long templateId,
			HttpServletRequest request) {
		log.info("Start of copyCloudTemplate Method in Controller ###");
		SaveTemplateHeaderResponsePo saveTemplateHeaderResponsePo = new SaveTemplateHeaderResponsePo();

		try {
			saveTemplateHeaderResponsePo = xxrSourceService.copySourceTemplate(newTemplateName, templateId,
					request);
		} catch (Exception e) {
			saveTemplateHeaderResponsePo
					.setMessage("Please contact System Administrator there is an error while processing the request");
			saveTemplateHeaderResponsePo.setError(e.getMessage());
			log.error(e.getMessage());
			return new ResponseEntity<SaveTemplateHeaderResponsePo>(saveTemplateHeaderResponsePo, new HttpHeaders(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<SaveTemplateHeaderResponsePo>(saveTemplateHeaderResponsePo, new HttpHeaders(),
				HttpStatus.OK);

	}

	@ApiOperation(value = "This Api is for loading data to external table")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/loadSourceDataToExternalTable")
	public ResponseEntity<LoadSourceDataResPo> loadSourceDataToExternalTable(@RequestBody LoadSourceDataReqPo loadSourceDataReqPo,
			HttpServletRequest request) {
		log.info("Start of loadSourceData #########");
		LoadSourceDataResPo loadSourceDataResPo = new LoadSourceDataResPo();
		try {
			loadSourceDataResPo = xxrSourceService.loadSourceData(loadSourceDataReqPo, request);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			loadSourceDataResPo
					.setMessage("Please contact System Administrator there is an error while processing the request");
			loadSourceDataResPo.setError(e.getMessage());
			return new ResponseEntity<LoadSourceDataResPo>(loadSourceDataResPo, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<LoadSourceDataResPo>(loadSourceDataResPo, HttpStatus.OK);
	}

	@ApiOperation(value = "This Api is for loading data to external table")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/loadsourcedataatparentobject")
	public ResponseEntity<List<LoadSourceDataParentObjectResPo>> loadSourceDataAtParentObject(
			@RequestBody LoadSourceDataReqAtParentObjectPo loadSourceDataReqPo, HttpServletRequest request) {
		log.info("Start of loadSourceDataAtParentObject #########");
		List<LoadSourceDataParentObjectResPo> loadSourceDataResPo = new ArrayList<>();
		LoadSourceDataParentObjectResPo loadSourceData = new LoadSourceDataParentObjectResPo();
		try {
			loadSourceDataResPo = xxrSourceService.loadSourceDataAtParentObject(loadSourceDataReqPo, request);
		} catch (ValidationException e) {
			log.error(e.getMessage());
			loadSourceData.setMessage(e.getMessage());
			loadSourceData
					.setError("Please contact System Administrator there is an error while processing the request");
			loadSourceDataResPo.add(loadSourceData);
			return new ResponseEntity<List<LoadSourceDataParentObjectResPo>>(loadSourceDataResPo, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			loadSourceData
					.setMessage("Please contact System Administrator there is an error while processing the request");
			loadSourceData.setError(e.getMessage());
			loadSourceDataResPo.add(loadSourceData);
			return new ResponseEntity<List<LoadSourceDataParentObjectResPo>>(loadSourceDataResPo,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<LoadSourceDataParentObjectResPo>>(loadSourceDataResPo, HttpStatus.OK);
	}

	@ApiOperation(value = "Api downloads log file of failedrecords during load source  data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@GetMapping("/downloadfailedreclogfile")
	public void downloadFailedRecLogFile(@RequestParam("failRecId") Long failRecId, HttpServletResponse resp)
			throws ConvertRiteException {
		log.info("Start of downloadFailedRecLogFile #########");
		try {
			xxrSourceService.downloadFailedRecLogFile(failRecId, resp);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation(value = "This Api is to save sourcetemplateheaders without proc")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error") })
	@PostMapping("/savesourcetemplateheaderwithoutproc")
	public ResponseEntity<SavingSourceTemplateHeadersResPo> saveSourceTemplateHeadersWithOutProc(
			@RequestBody List<SaveSourceTemplateHeadersPo> sourceTemplateHeadersPo, HttpServletRequest request) {
		log.info("Start of saveSourceTemplateHeadersWithOutProc Method in Controller ###");
		SavingSourceTemplateHeadersResPo savingSourceTemplateHeadersPo = new SavingSourceTemplateHeadersResPo();
		try {
			SaveSourceTemplateHeadersPo sourceHeader = sourceTemplateHeadersPo.get(0);
			if (sourceHeader.getObjectId() == null || sourceHeader.getParentObjectId() == null
					|| Validations.isNullOrEmpty(sourceHeader.getTemplateName()) || sourceHeader.getProjectId() == null)
				throw new BadRequestException(
						"objectId,parentObjectId,templateName and projectId are Mandatory fields");

			savingSourceTemplateHeadersPo = xxrSourceService
					.saveSourceTemplateHeadersWithoutProc(sourceTemplateHeadersPo, request);
		} catch (BadRequestException e) {
			log.error(e.getMessage());
			savingSourceTemplateHeadersPo.setMessage("Error while saving source template header");
//			savingSourceTemplateHeadersPo.setMessage(e.getMessage());
			return new ResponseEntity<SavingSourceTemplateHeadersResPo>(savingSourceTemplateHeadersPo,
					HttpStatus.BAD_REQUEST);
		} catch (ValidationException e) {
			log.error(e.getMessage());
			savingSourceTemplateHeadersPo.setMessage("Error while saving source template header");
//			savingSourceTemplateHeadersPo.setError(e.getMessage());
			return new ResponseEntity<SavingSourceTemplateHeadersResPo>(savingSourceTemplateHeadersPo, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			savingSourceTemplateHeadersPo.setMessage("Error while saving source template header");
//			savingSourceTemplateHeadersPo.setError(e.getMessage());
			return new ResponseEntity<SavingSourceTemplateHeadersResPo>(savingSourceTemplateHeadersPo,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<SavingSourceTemplateHeadersResPo>(savingSourceTemplateHeadersPo, new HttpHeaders(),
				HttpStatus.OK);
	}

	@ApiOperation(value = "This Api is to save sourcetemplatecolumns with jpa")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error") })
	@PostMapping("/savesourcetemplatecolumnswithjpa")
	public ResponseEntity<SaveSourceTemplateColumnsResPo> saveSourceTemplateColumnsWithJpa(
			@RequestBody List<SaveSourceTemplatesColumnsPo> sourceTemplateColumnsPo) {
		log.info("Start of saveSourceTemplateColumnsWithJpa Method in Controller ###");
		SaveSourceTemplateColumnsResPo saveSourceTemplateColumnsResPo = new SaveSourceTemplateColumnsResPo();

		try {
			saveSourceTemplateColumnsResPo = xxrSourceService.saveSourceTemplateColumnsWithJpa(sourceTemplateColumnsPo);
		} catch (BadRequestException e) {
			log.error(e.getMessage());
			saveSourceTemplateColumnsResPo.setMessage(e.getMessage());
			saveSourceTemplateColumnsResPo
					.setError("Please contact System Administrator there is an error while processing the request");
			return new ResponseEntity<SaveSourceTemplateColumnsResPo>(saveSourceTemplateColumnsResPo,
					HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error(e.getMessage());
			saveSourceTemplateColumnsResPo
					.setMessage("Please contact System Administrator there is an error while processing the request");
			saveSourceTemplateColumnsResPo.setError(e.getMessage());
			return new ResponseEntity<SaveSourceTemplateColumnsResPo>(saveSourceTemplateColumnsResPo,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<SaveSourceTemplateColumnsResPo>(saveSourceTemplateColumnsResPo, HttpStatus.OK);

	}

	@ApiOperation(value = "This Api is for repopulate origtransref")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/repopulateorigtransref")
	public ResponseEntity<RepopulateOrigTransRefResPo> repopulateOrigTransRef(
			@RequestParam("templateId") Long templateId, @RequestParam("stagingTableName") String stagingTableName,
			@RequestParam("batchName") String batchName, HttpServletRequest request) {
		log.info("Start of repopulateOrigTransRef########");
		RepopulateOrigTransRefResPo repopulateOrigTransRefResPo = new RepopulateOrigTransRefResPo();
		try {
			repopulateOrigTransRefResPo = xxrSourceService.repopulateOrigTransRef(templateId, stagingTableName,
					batchName, request);
		} catch (Exception e) {
			log.error(e.getMessage());
			repopulateOrigTransRefResPo.setResCode("N");
			repopulateOrigTransRefResPo
					.setResMsg("Please contact System Administrator there is an error while processing the request");

			return new ResponseEntity<RepopulateOrigTransRefResPo>(repopulateOrigTransRefResPo,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<RepopulateOrigTransRefResPo>(repopulateOrigTransRefResPo, HttpStatus.OK);
	}

	@ApiOperation(value = "This Api is for source columns update")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/updateSourceTemplateColumns")
	public ResponseEntity<SrcTemplateColsUpdtRes> updateSourceTemplateColumns(
			@RequestBody SrcTemplateColsUpdateReq srcTemplateColsUpdateReq, HttpServletRequest request) {
		log.info("Start of srcTemplateColumnsUpdate Method in controller ###");
		SrcTemplateColsUpdtRes srcTemplateColsUpdtRes = new SrcTemplateColsUpdtRes();
		try {
			srcTemplateColsUpdtRes = xxrSourceService.srcTemplateColumnsUpdate(srcTemplateColsUpdateReq,request);
		} catch (Exception e) {
			log.error(e.getMessage());
			srcTemplateColsUpdtRes
					.setMessage("Please contact System Administrator there is an error while processing the request");
			srcTemplateColsUpdtRes.setMessage(e.getMessage());
			return new ResponseEntity<SrcTemplateColsUpdtRes>(srcTemplateColsUpdtRes, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<SrcTemplateColsUpdtRes>(srcTemplateColsUpdtRes, HttpStatus.OK);
	}
}
