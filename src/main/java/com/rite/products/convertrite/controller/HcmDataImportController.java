package com.rite.products.convertrite.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rite.products.convertrite.Validations.Validations;
import com.rite.products.convertrite.exception.BadRequestException;
import com.rite.products.convertrite.exception.ConvertRiteException;
import com.rite.products.convertrite.exception.ValidationException;
import com.rite.products.convertrite.model.XxrCloudDataProcess;
import com.rite.products.convertrite.po.DataSetStausResPo;
import com.rite.products.convertrite.po.HcmDetailsPageReqPo;
import com.rite.products.convertrite.po.HcmLoadAndImportDataRes;
import com.rite.products.convertrite.po.HcmLoadandImportDataReqPo;
import com.rite.products.convertrite.po.XxrHcmDataLoaderResPo;
import com.rite.products.convertrite.respository.XxrCloudDataProcessRepository;
import com.rite.products.convertrite.service.CloudDataProcessingService;
import com.rite.products.convertrite.service.HcmDataImportService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class HcmDataImportController {
	private static final Logger log = LoggerFactory.getLogger(HcmDataImportController.class);

	@Autowired
	HcmDataImportService hcmDataImportService;
	@Autowired
	CloudDataProcessingService cloudDataProcessingService;
	@Autowired
	XxrCloudDataProcessRepository xxrCloudDataProcessRepository;

	@ApiOperation(value = "This Api is to load and import data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/hcmloadandimportdata")
	public ResponseEntity<HcmLoadAndImportDataRes> hcmLoadAndImportData(
			@RequestBody HcmLoadandImportDataReqPo hcmLoadandImportDataReqPo) {
		log.info("Start of loadAndImportData Method in Controller ###");

		HcmLoadAndImportDataRes loadandImportDataResPo = new HcmLoadAndImportDataRes();
		try {

			if (hcmLoadandImportDataReqPo.getCloudTemplateId() == null || hcmLoadandImportDataReqPo.getPodId() == null
					|| hcmLoadandImportDataReqPo.getProjectId() == null
					|| hcmLoadandImportDataReqPo.getParentObjectId() == null
					|| Validations.isNullOrEmpty(hcmLoadandImportDataReqPo.getDocumentTitle())
					|| Validations.isNullOrEmpty(hcmLoadandImportDataReqPo.getDocumentAccount())
					|| Validations.isNullOrEmpty(hcmLoadandImportDataReqPo.getDocumentAuthor())
					|| Validations.isNullOrEmpty(hcmLoadandImportDataReqPo.getDocumentSecurityGroup())
					|| Validations.isNullOrEmpty(hcmLoadandImportDataReqPo.getBatchName()))
				throw new BadRequestException(
						"cloudTemplateId,podId,projectId,parentObjectId,documentTitle,documentAccount,documentSecurityGroup,batchName and documentAuthor are Mandatory fields");

			loadandImportDataResPo = hcmDataImportService.hcmLoadAndImportData(hcmLoadandImportDataReqPo);
		} catch (ValidationException e) {
			log.error(e.getMessage());
			loadandImportDataResPo.setMessage(e.getMessage());
			loadandImportDataResPo
					.setError("Please contact System Administrator there is an error while processing the request");
			return new ResponseEntity<HcmLoadAndImportDataRes>(loadandImportDataResPo, HttpStatus.OK);
		} catch (BadRequestException e) {
			log.error(e.getMessage());
			loadandImportDataResPo.setMessage(e.getMessage());
			loadandImportDataResPo
					.setError("Please contact System Administrator there is an error while processing the request");
			return new ResponseEntity<HcmLoadAndImportDataRes>(loadandImportDataResPo, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error(e.getMessage());
			loadandImportDataResPo
					.setMessage("Please contact System Administrator there is an error while processing the request");
			loadandImportDataResPo.setError(e.getMessage());
			return new ResponseEntity<HcmLoadAndImportDataRes>(loadandImportDataResPo,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<HcmLoadAndImportDataRes>(loadandImportDataResPo, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "This api is to get status of HCM load import")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@GetMapping("/getDataSetStatus")
	public DataSetStausResPo getDataSetStatus(@RequestParam("contentId") String contentId,
			@RequestParam("processId") String processId, @RequestParam("cldTemplateId") Long cldTemplateId)
			throws ConvertRiteException {
		log.info("Start of getDataSetStatus Method in Controller ###");
		DataSetStausResPo dataSetStausResPo = new DataSetStausResPo();
		try {
			dataSetStausResPo = hcmDataImportService.getDataSetStatus(contentId, processId, cldTemplateId);
		} catch (ValidationException e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(e.getMessage(), HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return dataSetStausResPo;
	}

	@ApiOperation(value = "This api is to get Hcmdataloader  details")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@GetMapping("/gethcmdataloaderdetails")
	public ResponseEntity<List<XxrHcmDataLoaderResPo>> getHcmDataLoaderDetails(HcmDetailsPageReqPo hcmDetailsPageReqPo) throws ConvertRiteException {
		log.info("Start of getHcmDataLoaderDetails Method in Controller ###");
		List<XxrHcmDataLoaderResPo> hcmDetailsList = new ArrayList<>();
		HttpHeaders httpHeaders = new HttpHeaders();
		try {
			hcmDetailsList = hcmDataImportService.getHcmDataLoaderDetails(hcmDetailsPageReqPo,httpHeaders);
		} catch (ValidationException e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(e.getMessage(), HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<XxrHcmDataLoaderResPo>>(hcmDetailsList, httpHeaders, HttpStatus.OK);
	}

	@ApiOperation(value = "This Api saves query of reconcile report")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/processhdlreconcile")
	public ResponseEntity<XxrCloudDataProcess> processHdlReconcile(@RequestParam("contentId") String contentId,
			@RequestParam("cldTemplateId") Long cldTemplateId, HttpServletRequest request) throws ConvertRiteException {
		log.info("Start of processHdlReconcile Method in Controller ###");
		log.debug("contentId:::::::" + contentId);
		XxrCloudDataProcess xxrCloudDataProcess = new XxrCloudDataProcess();
		try {
			xxrCloudDataProcess = hcmDataImportService.processHdlReconcile(contentId, cldTemplateId, request);
		} catch (ValidationException e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(e.getMessage(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<XxrCloudDataProcess>(xxrCloudDataProcess, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "This Api is to get Reconcile Report")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error") })
	@GetMapping("/hdlreconcilereport")
	public void hdlReconcileReport(@RequestParam("requestId") String requestId, HttpServletResponse response)
			throws ConvertRiteException {
		log.info("Start of downloadCsvFile in controller ###");

		try {
			response.setContentType("text/csv");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ReconcileReport.csv");
			XxrCloudDataProcess cloudDataProcess = xxrCloudDataProcessRepository.findAll().stream()
					.filter(x -> x.getRequestId().equalsIgnoreCase(requestId)).findFirst().get();
			if (cloudDataProcess.getStatus().equalsIgnoreCase("processing")
					|| cloudDataProcess.getStatus().equalsIgnoreCase("starting"))
				throw new ValidationException("Reconcile is still in process,Please wait for sometime");
			else if (cloudDataProcess.getStatus().equalsIgnoreCase("error"))
				throw new ValidationException(
						"Something went wrong while Reconcile,Please contact system administrator");
			else {
				cloudDataProcessingService.downloadCsvFile(requestId, response);
			}

		} catch (ValidationException e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(e.getMessage(), HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@ApiOperation(value = "This Api saves query of summary report")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/processhdlsummary")
	public ResponseEntity<XxrCloudDataProcess> processHdlSummary(@RequestParam("contentId") String contentId,
			@RequestParam("podId") Long podId, @RequestParam("projectId") Long projectId, HttpServletRequest request)
			throws ConvertRiteException {
		log.info("Start of processHdlSummary Method in Controller ###");
		log.debug("contentId:::::::" + contentId);
		XxrCloudDataProcess xxrCloudDataProcess = new XxrCloudDataProcess();
		try {
			xxrCloudDataProcess = hcmDataImportService.processHdlSummary(contentId, podId, projectId, request);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<XxrCloudDataProcess>(xxrCloudDataProcess, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiOperation(value = "This Api is to get Summary Report")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error") })
	@GetMapping("/hdlsummaryreport")
	public void hdlSummaryReport(@RequestParam("requestId") String requestId, HttpServletResponse response)
			throws ConvertRiteException {
		log.info("Start of hdlSummaryReport in controller ###");
		try {
			response.setContentType("text/csv");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=HDL_SummaryReport.csv");
			XxrCloudDataProcess cloudDataProcess = xxrCloudDataProcessRepository.findAll().stream()
					.filter(x -> x.getRequestId().equalsIgnoreCase(requestId)).findFirst().get();
			if (cloudDataProcess.getStatus().equalsIgnoreCase("processing")
					|| cloudDataProcess.getStatus().equalsIgnoreCase("starting"))
				throw new ValidationException("SummaryReport is still in process,Please wait for sometime");
			else if (cloudDataProcess.getStatus().equalsIgnoreCase("error"))
				throw new ValidationException("Something went wrong,Please contact system administrator");
			else {
				cloudDataProcessingService.downloadCsvFile(requestId, response);
			}

		} catch (ValidationException e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(e.getMessage(), HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@ApiOperation(value = "This Api saves query of status report")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
			@ApiResponse(code = 500, message = "Server Side Error"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@PostMapping("/processhdlstatus")
	public ResponseEntity<XxrCloudDataProcess> processHdlStatus(@RequestParam("contentId") String contentId,
			@RequestParam("podId") Long podId, @RequestParam("projectId") Long projectId, HttpServletRequest request)
			throws ConvertRiteException {
		log.info("Start of processHdlStatus Method in Controller ###");
		log.debug("contentId:::::::" + contentId);
		XxrCloudDataProcess xxrCloudDataProcess = new XxrCloudDataProcess();
		try {
			xxrCloudDataProcess = hcmDataImportService.processHdlStatus(contentId, podId, projectId, request);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			throw new ConvertRiteException(
					"Please contact System Administrator there is an error while processing the request",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<XxrCloudDataProcess>(xxrCloudDataProcess, new HttpHeaders(), HttpStatus.OK);
	}
}
