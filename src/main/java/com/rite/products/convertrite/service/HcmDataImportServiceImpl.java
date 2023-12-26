package com.rite.products.convertrite.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.servlet.http.HttpServletRequest;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.rite.products.convertrite.exception.ValidationException;
import com.rite.products.convertrite.hcm.stubs.GenericSoapServiceStub;
import com.rite.products.convertrite.hcm.stubs.GenericSoapServiceStub.Document_type0;
import com.rite.products.convertrite.hcm.stubs.GenericSoapServiceStub.Field_type0;
import com.rite.products.convertrite.hcm.stubs.GenericSoapServiceStub.Fil;
import com.rite.products.convertrite.hcm.stubs.GenericSoapServiceStub.Generic;
import com.rite.products.convertrite.hcm.stubs.GenericSoapServiceStub.GenericRequest;
import com.rite.products.convertrite.hcm.stubs.GenericSoapServiceStub.GenericResponse;
import com.rite.products.convertrite.hcm.stubs.GenericSoapServiceStub.Servce;
import com.rite.products.convertrite.hcm.stubs.HCMDataLoaderStub;
import com.rite.products.convertrite.hcm.stubs.HCMDataLoaderStub.GetDataSetStatus;
import com.rite.products.convertrite.hcm.stubs.HCMDataLoaderStub.GetDataSetStatusResponse;
import com.rite.products.convertrite.hcm.stubs.HCMDataLoaderStub.ImportAndLoadData;
import com.rite.products.convertrite.hcm.stubs.HCMDataLoaderStub.ImportAndLoadDataResponse;
import com.rite.products.convertrite.model.XxrCldTempHdrsView;
import com.rite.products.convertrite.model.XxrCloudDataProcess;
import com.rite.products.convertrite.model.XxrCloudTemplateHeader;
import com.rite.products.convertrite.model.XxrErpIntegration;
import com.rite.products.convertrite.model.XxrHcmDataLoader;
import com.rite.products.convertrite.po.DataSetStausResPo;
import com.rite.products.convertrite.po.HcmDetailsPageReqPo;
import com.rite.products.convertrite.po.HcmLoadAndImportDataRes;
import com.rite.products.convertrite.po.HcmLoadandImportDataReqPo;
import com.rite.products.convertrite.po.XxrHcmDataLoaderResPo;
import com.rite.products.convertrite.respository.XxrCldTempHdrsViewRepository;
import com.rite.products.convertrite.respository.XxrCloudTableRepository;
import com.rite.products.convertrite.respository.XxrCloudTemplateHeadersRepository;
import com.rite.products.convertrite.respository.XxrHcmDataLoaderRepository;
import com.rite.products.convertrite.respository.XxrLookUpValuesRepository;
import com.rite.products.convertrite.utils.Utils;

@Service
public class HcmDataImportServiceImpl implements HcmDataImportService {

	private static final Logger log = LoggerFactory.getLogger(HcmDataImportServiceImpl.class);

	@Autowired
	Utils utils;
	@Autowired
	XxrHcmDataLoaderRepository xxrHcmDataLoaderRepository;
	@Autowired
	XxrCloudTemplateHeadersRepository xxrCloudTemplateHeadersRepository;
	@Autowired
	XxrCloudTableRepository xxrCloudTableRepository;
	@Autowired
	XxrLookUpValuesRepository xxrLookUpValuesRepository;
	@Autowired
	XxrCldTempHdrsViewRepository xxrCldTempHdrsViewRepository;

	@Value("${hcm_soapurl}")
	private String hcmUrl;
	@Value("${generic_soapurl}")
	private String genericSoapUrl;

	@Override
	public DataSetStausResPo getDataSetStatus(String contentId, String processId, Long cldTemplateId)
			throws ValidationException, Exception {
		// TODO Auto-generated method stub
		log.info("Start of getDataSetStatus in service####");
		DataSetStausResPo dataSetStausResPo = new DataSetStausResPo();
		GetDataSetStatusResponse getDataSetStatusResponse = new GetDataSetStatusResponse();
		try {
			XxrCldTempHdrsView xxrCldTempHdrsView = xxrCldTempHdrsViewRepository.findByTemplateId(cldTemplateId);
			GetDataSetStatus getDataSetStatus = new GetDataSetStatus();
			getDataSetStatus.setParameters("ContentId=" + contentId + ",ProcessId=" + processId);
			String soapUrl = utils.getCloudUrl(xxrCldTempHdrsView) + hcmUrl;
			HCMDataLoaderStub hCMDataLoaderStub = new HCMDataLoaderStub(soapUrl);
			// BasicAuthentication for soap service
			utils.basicAuthentication(hCMDataLoaderStub._getServiceClient(), xxrCldTempHdrsView);
			getDataSetStatusResponse = hCMDataLoaderStub.getDataSetStatus(getDataSetStatus);
			dataSetStausResPo.setResultStatus(getDataSetStatusResponse.getResult());

		} catch (ValidationException e) {
			throw new ValidationException(e.getMessage());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return dataSetStausResPo;
	}

	@Override
	public HcmLoadAndImportDataRes hcmLoadAndImportData(HcmLoadandImportDataReqPo hcmLoadandImportDataReqPo)
			throws ValidationException, Exception {
		// TODO Auto-generated method stub
		log.info("Start of hcmLoadAndImportData in service####");
		HcmLoadAndImportDataRes hcmLoadAndImportDataRes = new HcmLoadAndImportDataRes();
		String contentId = "";
		String processId = "";
		try {
			XxrCldTempHdrsView xxrCldTempHdrsView = xxrCldTempHdrsViewRepository
					.findByTemplateId(hcmLoadandImportDataReqPo.getCloudTemplateId());
			// upload zipped ".dat" file to UCM location
			contentId = uploadZipToUcm(hcmLoadandImportDataReqPo, xxrCldTempHdrsView);
//			log.info("contentId::" + contentId);
			// Hcm Data loading to cloud
			processId = hcmCloudDataLoader(contentId, xxrCldTempHdrsView);
			// save details to hcmdataloder table
			saveHcmDataLoaderDetails(hcmLoadandImportDataReqPo, contentId, processId);
			hcmLoadAndImportDataRes.setContentId(contentId);
			hcmLoadAndImportDataRes.setProcessId(processId);

		} catch (ValidationException e) {
			throw new ValidationException(e.getMessage());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return hcmLoadAndImportDataRes;
	}

	private void saveHcmDataLoaderDetails(HcmLoadandImportDataReqPo hcmLoadandImportDataReqPo, String contentId,
			String processId) {
		XxrHcmDataLoader xxrHcmDataLoader = new XxrHcmDataLoader();
		xxrHcmDataLoader.setCloudTemplateId(hcmLoadandImportDataReqPo.getCloudTemplateId());
		xxrHcmDataLoader.setContentId(contentId);
		xxrHcmDataLoader.setProcessId(processId);
		xxrHcmDataLoader.setPodId(hcmLoadandImportDataReqPo.getPodId());
		xxrHcmDataLoader.setProjectId(hcmLoadandImportDataReqPo.getProjectId());
		xxrHcmDataLoader.setParentObjectId(hcmLoadandImportDataReqPo.getParentObjectId());
		xxrHcmDataLoader.setXxrBatchName(hcmLoadandImportDataReqPo.getBatchName());
		xxrHcmDataLoader.setDocumentAccount(hcmLoadandImportDataReqPo.getDocumentAccount());
		xxrHcmDataLoader.setDocumentAuthor(hcmLoadandImportDataReqPo.getDocumentAuthor());
		xxrHcmDataLoader.setDocumentSecurityGroup(hcmLoadandImportDataReqPo.getDocumentSecurityGroup());
		xxrHcmDataLoader.setDocumentTitle(hcmLoadandImportDataReqPo.getDocumentTitle());
		xxrHcmDataLoader.setCreationDate(new java.sql.Date(new java.util.Date().getTime()));
		xxrHcmDataLoader.setCreatedBy("ConvertRite");
		xxrHcmDataLoader.setLastUpdatedDate(new java.sql.Date(new java.util.Date().getTime()));
		xxrHcmDataLoader.setLastUpdateBy("ConvertRite");
		xxrHcmDataLoaderRepository.save(xxrHcmDataLoader);
	}

	private String hcmCloudDataLoader(String contentId, XxrCldTempHdrsView xxrCldTempHdrsView)
			throws ValidationException, Exception {
		ImportAndLoadDataResponse importAndLoadDataResponse = new ImportAndLoadDataResponse();
		try {
			ImportAndLoadData importAndLoadData = new ImportAndLoadData();
			importAndLoadData.setContentId(contentId);
			importAndLoadData.setParameters("DeleteSourceFile=N");

			String soapUrl = utils.getCloudUrl(xxrCldTempHdrsView) + hcmUrl;
			HCMDataLoaderStub hCMDataLoaderStub = new HCMDataLoaderStub(soapUrl);
			// BasicAuthentication for soap service
			utils.basicAuthentication(hCMDataLoaderStub._getServiceClient(), xxrCldTempHdrsView);

			importAndLoadDataResponse = hCMDataLoaderStub.importAndLoadData(importAndLoadData);
		} catch (ValidationException e) {
			throw new ValidationException(e.getMessage());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return importAndLoadDataResponse.getResult();
	}

	private String uploadZipToUcm(HcmLoadandImportDataReqPo hcmLoadandImportDataReqPo,
			XxrCldTempHdrsView xxrCldTempHdrsView) throws ValidationException, Exception {
		String contentId = "";
		String statusMessage = "";
		String statusCode = "";
		String zipfilePath = "";
		try {
			Field_type0 fieldType0 = new Field_type0();
			fieldType0.setName("dDocTitle");
			fieldType0.setString(hcmLoadandImportDataReqPo.getDocumentTitle());
			Field_type0 fieldType1 = new Field_type0();
			fieldType1.setName("dDocType");
			fieldType1.setString("Document");
			Field_type0 fieldType2 = new Field_type0();
			fieldType2.setName("dDocAuthor");
			fieldType2.setString(hcmLoadandImportDataReqPo.getDocumentAuthor());
			Field_type0 fieldType3 = new Field_type0();
			fieldType3.setName("dSecurityGroup");
			fieldType3.setString(hcmLoadandImportDataReqPo.getDocumentSecurityGroup());
			Field_type0 fieldType4 = new Field_type0();
			fieldType4.setName("dDocAccount");
			fieldType4.setString(hcmLoadandImportDataReqPo.getDocumentAccount());

			// Zip ".dat" file in temp location
			zipfilePath = zipDatFile(hcmLoadandImportDataReqPo);

			DataSource source = new ByteArrayDataSource(Files.readAllBytes(Paths.get(zipfilePath)),
					"text/plain;charset=UTF-8");
			DataHandler dataHandler = new DataHandler(source);
			/*
			 * DataSource source = new ByteArrayDataSource(
			 * Files.readAllBytes(Paths.get("c://temp//Worker.zip")),
			 * "text/plain;charset=UTF-8"); DataHandler dataHandler = new
			 * DataHandler(source);
			 */

			Fil file = new Fil();
			file.setHref(hcmLoadandImportDataReqPo.getDocumentTitle());
			file.setName("primaryFile");
			file.setContents(dataHandler);
			Field_type0[] fieldArr = new Field_type0[] { fieldType0, fieldType1, fieldType2, fieldType3, fieldType4 };
			Fil[] fileArr = new Fil[] { file };

			Document_type0 document_type0 = new Document_type0();
			document_type0.setField(fieldArr);
			document_type0.setFile(fileArr);

			Servce service = new Servce();
			service.setIdcService("CHECKIN_UNIVERSAL");
			service.setDocument(document_type0);

			Generic generic = new Generic();
			generic.setWebKey("cs");
			generic.setService(service);

			GenericRequest genericRequest = new GenericRequest();
			genericRequest.setGenericRequest(generic);

			String soapUrl = utils.getCloudUrl(xxrCldTempHdrsView) + genericSoapUrl;
			GenericSoapServiceStub genericSoapServiceStub = new GenericSoapServiceStub(soapUrl);
			utils.basicAuthentication(genericSoapServiceStub._getServiceClient(), xxrCldTempHdrsView);

			GenericResponse genericResp = genericSoapServiceStub.genericSoapOperation(genericRequest);

			Field_type0[] fieldArry = genericResp.getGenericResponse().getService().getDocument().getField();

			// log.info(fieldArry.length + ":::::::::::length");
			for (Field_type0 fieldType : fieldArry) {
				// log.info(fieldType.getName() + ":::::::Name::" + fieldType.getString());
				if (fieldType.getName().equals("dDocName")) {
//					log.info("enetringg");
					contentId = fieldType.getString();
				}
				if (fieldType.getName().equals("StatusMessage")) {
					statusMessage = fieldType.getString();
				}
				if (fieldType.getName().equals("StatusCode")) {
					statusCode = fieldType.getString();
				}
			}
			Predicate<String> p = x -> (x.equals("0"));
			if (!p.test(statusCode)) {
				throw new ValidationException(statusMessage);
			}
		} catch (ValidationException e) {
			throw new ValidationException(e.getMessage());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return contentId;
	}

	private String zipDatFile(HcmLoadandImportDataReqPo hcmLoadandImportDataReqPo) throws Exception {
		// List<XxrCloudTemplateHeader> cloudTemplateHeaderList = new ArrayList<>();
		Long parentObjectId = null;
		// String metaDataTableName = "";
		Path target = null;
		String clobString = "";
		String zipfilePath = "";
		FileWriter outputfile = null;
		String parentObjectCode = "";
		try {
			// Create Temp Dir
			target = Files.createTempDirectory("");
			XxrCloudTemplateHeader xxrCloudTemplateHeader = xxrCloudTemplateHeadersRepository
					.getCloudTemplateById(hcmLoadandImportDataReqPo.getCloudTemplateId());
			// XxrCloudTemplateHeader xxrCloudTemplateHeader =
			// cloudTemplateHeaderList.get(0);
			if (xxrCloudTemplateHeader != null)
				parentObjectId = xxrCloudTemplateHeader.getParentObjectId();
			if (parentObjectId != null)
				parentObjectCode = xxrLookUpValuesRepository.getValueById(parentObjectId);

			String sql = "select xxr_conversion_utils_pkg.hdl_filegen(" + hcmLoadandImportDataReqPo.getCloudTemplateId()
					+ ",'" + hcmLoadandImportDataReqPo.getBatchName() + "') from dual";
			clobString = utils.getClobString(sql);
			// create dat file in temp location
			String filePath = target + File.separator + parentObjectCode + ".dat";
			File fil = new File(filePath);
			outputfile = new FileWriter(fil);
			outputfile.write(clobString);
			outputfile.close();
			zipfilePath = target + File.separator + parentObjectCode + ".zip";
			// zip dat file
			Utils.zipFile(target.toString(), parentObjectCode, parentObjectCode + ".dat");
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return zipfilePath;
	}

	@Override
	public List<XxrHcmDataLoaderResPo> getHcmDataLoaderDetails(HcmDetailsPageReqPo hcmDetailsPageReqPo,
			HttpHeaders httpHeaders) throws Exception {
		// TODO Auto-generated method stub
		log.info("Start of getHcmDataLoaderDetails Method in Service###");
		List<XxrHcmDataLoader> hcmDataLoaderLi = new ArrayList<>();
		List<XxrHcmDataLoaderResPo> hcmDataRes = new ArrayList<>();
		try {

			Pageable pageable = PageRequest.of(hcmDetailsPageReqPo.getPageNo(), hcmDetailsPageReqPo.getPageSize(),
					Sort.by(hcmDetailsPageReqPo.getSortDirection(), hcmDetailsPageReqPo.getSortBy()));
			Page<XxrHcmDataLoader> pageContent = xxrHcmDataLoaderRepository.findAll(pageable);
			httpHeaders.set("pagecount", String.valueOf(pageContent.getTotalPages()));
			httpHeaders.set("totalcount", String.valueOf(pageContent.getTotalElements()));
			if (pageContent.hasContent())
				hcmDataLoaderLi = pageContent.getContent();
			// hcmDataLoaderLi = xxrHcmDataLoaderRepository.findAll();
			hcmDataLoaderLi.stream().forEach(x -> {
				// List<XxrCloudTemplateHeader> cloudTemplateHeaderList = new ArrayList<>();
				XxrHcmDataLoaderResPo xxrHcmDataLoaderResPo = new XxrHcmDataLoaderResPo();
				xxrHcmDataLoaderResPo.setCloudTemplateId(x.getCloudTemplateId());
				XxrCldTempHdrsView xxrCloudTemplateHeader = xxrCldTempHdrsViewRepository.findByTemplateId(x.getCloudTemplateId());
				// XxrCloudTemplateHeader xxrCloudTemplateHeader =
				// cloudTemplateHeaderList.get(0);
				xxrHcmDataLoaderResPo.setPodName(xxrCloudTemplateHeader.getPodName());
				xxrHcmDataLoaderResPo.setPodId(x.getPodId());
				xxrHcmDataLoaderResPo.setProjectId(x.getProjectId());
				xxrHcmDataLoaderResPo.setProjectName(xxrCloudTemplateHeader.getProjectName());
				xxrHcmDataLoaderResPo.setParentObjectName(xxrCloudTemplateHeader.getParentObjectCode());
				xxrHcmDataLoaderResPo.setParentObjectId(x.getParentObjectId());
				xxrHcmDataLoaderResPo.setXxrBatchName(x.getXxrBatchName());
				xxrHcmDataLoaderResPo.setCloudTemplateName(xxrCloudTemplateHeader.getTemplateName());
				xxrHcmDataLoaderResPo.setContentId(x.getContentId());
				xxrHcmDataLoaderResPo.setDocumentAccount(x.getDocumentAccount());
				xxrHcmDataLoaderResPo.setDocumentAuthor(x.getDocumentAuthor());
				xxrHcmDataLoaderResPo.setDocumentSecurityGroup(x.getDocumentSecurityGroup());
				xxrHcmDataLoaderResPo.setDocumentTitle(x.getDocumentTitle());
				xxrHcmDataLoaderResPo.setId(x.getId());
				xxrHcmDataLoaderResPo.setProcessId(x.getProcessId());

				hcmDataRes.add(xxrHcmDataLoaderResPo);
			});
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return hcmDataRes;
	}

	@Override
	public XxrCloudDataProcess processHdlReconcile(String contentId, Long cldTemplateId, HttpServletRequest request)
			throws Exception {
		// TODO Auto-generated method stub
		log.info("Start of processHdlReconcile Method in service#####");
		XxrCloudDataProcess xxrCloudDataProcess = new XxrCloudDataProcess();
		try {

			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("HdlErrorDetails.sql");
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String sql = reader.lines().collect(Collectors.joining(System.lineSeparator()));
			sql = sql.replaceAll("\\{0\\}", contentId);
			XxrCloudTemplateHeader xxrCloudTemplateHeader = xxrCloudTemplateHeadersRepository
					.getCloudTemplateById(cldTemplateId);
			xxrCloudDataProcess = utils.cldDataProcess(sql, xxrCloudTemplateHeader, "HdlReconcile", request);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return xxrCloudDataProcess;
	}

	@Override
	public XxrCloudDataProcess processHdlSummary(String contentId, Long podId, Long projectId,
			HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		log.info("Start of processHdlSummary Method in service#####");
		XxrCloudDataProcess xxrCloudDataProcess = new XxrCloudDataProcess();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("HDL_Summary.sql");
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String sql = reader.lines().collect(Collectors.joining(System.lineSeparator()));
		sql = sql.replaceAll("\\{0\\}", contentId);
		xxrCloudDataProcess = utils.cldDataProcess(sql, "HdlSummary", request, projectId);
		return xxrCloudDataProcess;
	}

	@Override
	public XxrCloudDataProcess processHdlStatus(String contentId, Long podId, Long projectId,
			HttpServletRequest request) throws Exception {
		log.info("Start of processHdlStatus Method in service#####");
		XxrCloudDataProcess xxrCloudDataProcess = new XxrCloudDataProcess();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("HDL_Success_Error.sql");
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String sql = reader.lines().collect(Collectors.joining(System.lineSeparator()));
		sql = sql.replaceAll("\\{0\\}", contentId);
		xxrCloudDataProcess = utils.cldDataProcess(sql, "HdlStatus", request, projectId);
		return xxrCloudDataProcess;
	}

}
