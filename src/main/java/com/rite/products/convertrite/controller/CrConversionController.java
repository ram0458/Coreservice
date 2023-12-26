package com.rite.products.convertrite.controller;

import com.rite.products.convertrite.Validations.Validations;
import com.rite.products.convertrite.exception.BadRequestException;
import com.rite.products.convertrite.exception.ConvertRiteException;
import com.rite.products.convertrite.model.CrProcessRequestsView;
import com.rite.products.convertrite.po.CrProcessRequestsPagePo;
import com.rite.products.convertrite.po.LoadandImportDataReqPo;
import com.rite.products.convertrite.po.LoadandImportDataResPo;
import com.rite.products.convertrite.po.ProcessJobPo;
import com.rite.products.convertrite.service.CrConversionService;
import com.rite.products.convertrite.service.ErpIntegrationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/convertritecore/conversion")
public class CrConversionController {
	private static final Logger log = LoggerFactory.getLogger(CrConversionController.class);

	@Autowired
	CrConversionService crConversionService;
	@Autowired
	ErpIntegrationService erpIntegrationService;
	@ApiOperation(value = "This Api returns templates state")
	@GetMapping("/gettemplatestate")
	public ResponseEntity<?> getTemplatesState() {
		try {
			return new ResponseEntity<>(crConversionService.getTemplateState(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getCause(), HttpStatus.EXPECTATION_FAILED);
		}
	}

	@ApiOperation(value = "This Api returns template statistics")
	@GetMapping("/gettemplatestatistics")
	public ResponseEntity<?> getTemplatesStatistics() {
		try {
			return new ResponseEntity<>(crConversionService.getTemplateStatistics(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getCause(), HttpStatus.EXPECTATION_FAILED);
		}
	}

	@ApiOperation(value = "This Api returns status of process requests")
	@GetMapping("/getprocessrequests")
	public ResponseEntity<?> getProcessRequests(CrProcessRequestsPagePo crProcessRequestsPagePo) {
		try {
			HttpHeaders httpHeaders = new HttpHeaders();
			List<CrProcessRequestsView> requests = crConversionService.getProcessRequests(crProcessRequestsPagePo, httpHeaders);
			return new ResponseEntity<>(requests, httpHeaders, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getCause(), HttpStatus.EXPECTATION_FAILED);
		}
	}

	@ApiOperation(value = "This Api is for processing Job, 'type' Param value should be Validation or Conversion or ReProcesses")
	@PostMapping("/processjobv1")
	public ResponseEntity<ProcessJobPo> processJobV1(@RequestParam("cloudTemplateName") String cloudTemplateName,
													 @RequestParam("batchName") String batchName,
													 @RequestParam("type") String type, HttpServletRequest request)
			throws ConvertRiteException {
		ProcessJobPo processJobPo = new ProcessJobPo();
		if (type.matches("(?i)Validation|Conversion|ReProcesses")) {
			try {
				if (cloudTemplateName != null && !Validations.isNullOrEmpty(type))
					processJobPo = crConversionService.processJobV1(cloudTemplateName, type, batchName, request);
				else
					throw new ConvertRiteException("Missing cloudTemplateName or type in the Request",
							HttpStatus.BAD_REQUEST);
			} catch (Exception e) {
				throw new ConvertRiteException(
						"Please contact System Administrator there is an error while processing the request",
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			throw new ConvertRiteException("type Param value should be Validation or Conversion or ReProcesses",
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<ProcessJobPo>(processJobPo, new HttpHeaders(), HttpStatus.OK);
	}
	@ApiOperation(value = "This Api is to Transform the Data to Cloud")
	@PostMapping("/transformDataToCloud")
	public ResponseEntity<Object> transformDataToCloud(@RequestParam("cloudTemplateName") String cloudTemplateName, @RequestParam("pReprocessFlag") String pReprocessFlag, @RequestParam("pBatchFlag") String pBatchFlag, @RequestParam("pBatchName") String pBatchName,HttpServletRequest request ) {
		ResponseEntity<Object> resEntity=null;
		try {
			resEntity=crConversionService.transformDataToCloud(cloudTemplateName,pReprocessFlag, pBatchFlag,pBatchName,request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resEntity;
	}
	@ApiOperation(value = "This Api is for downloading fbdi from ftp")
	@GetMapping("/downloadfbdi")
	public void downloadFbdi(@RequestParam("cloudTemplateId") Long cloudTemplateId, @RequestParam("batchName") String batchName, HttpServletResponse response)
			throws Exception {
		try {
			crConversionService.downloadFbdi(cloudTemplateId,batchName, response);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	@ApiOperation(value = "This Api is for generating hdl from lob")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error") })
	@GetMapping("/generatehdl")
	public void generateHdlFromLob(@RequestParam("cloudTemplateId") Long cloudTemplateId,
								   @RequestParam("batchName") String batchName, HttpServletResponse response) throws Exception {

		try {
			crConversionService.generateHdlFromLob(cloudTemplateId, batchName, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

	}
	@ApiOperation(value = "This Api is to load and import data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/loadandimportdatav1")
	public ResponseEntity<LoadandImportDataResPo> loadAndImportDataV1(
			@RequestBody LoadandImportDataReqPo loadandImportDataReqPo,@RequestHeader("Authorization") String bearerToken) {
		log.info("Start of loadAndImportData Method in Controller ###");

		LoadandImportDataResPo loadandImportDataResPo = new LoadandImportDataResPo();
		try {

			if (loadandImportDataReqPo.getCloudTemplateId() == null
					|| Validations.isNullOrEmpty(loadandImportDataReqPo.getBatchName()))
				throw new BadRequestException("cloudTemplateId and batchName are Mandatory fields");

			//loadandImportDataResPo = erpIntegrationService.loadAndImportDataV1(loadandImportDataReqPo,bearerToken);
			loadandImportDataResPo = erpIntegrationService.loadAndImportDataV2(loadandImportDataReqPo, bearerToken);

		}  catch (Exception e) {
			log.error(e.getMessage());
			loadandImportDataResPo
					.setMessage("Please contact System Administrator there is an error while processing the request");
			loadandImportDataResPo.setError(e.getMessage());
			return new ResponseEntity<LoadandImportDataResPo>(loadandImportDataResPo, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<LoadandImportDataResPo>(loadandImportDataResPo, new HttpHeaders(), HttpStatus.OK);
	}

}
