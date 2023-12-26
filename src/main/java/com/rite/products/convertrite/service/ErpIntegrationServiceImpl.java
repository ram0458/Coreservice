package com.rite.products.convertrite.service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rite.products.convertrite.model.*;
import com.rite.products.convertrite.po.*;
import com.rite.products.convertrite.respository.*;
import org.apache.axiom.attachments.ByteArrayDataSource;
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

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.opencsv.CSVWriter;
import com.rite.products.convertrite.Validations.Validations;
import com.rite.products.convertrite.exception.ValidationException;
import com.rite.products.convertrite.stubs.erp.ErpIntegrationServiceStub;
import com.rite.products.convertrite.stubs.erp.ErpIntegrationServiceStub.Base64BinaryDataHandler;
import com.rite.products.convertrite.stubs.erp.ErpIntegrationServiceStub.DocumentDetails;
import com.rite.products.convertrite.stubs.erp.ErpIntegrationServiceStub.DownloadExportOutput;
import com.rite.products.convertrite.stubs.erp.ErpIntegrationServiceStub.DownloadExportOutputResponse;
import com.rite.products.convertrite.stubs.erp.ErpIntegrationServiceStub.EssJob;
import com.rite.products.convertrite.stubs.erp.ErpIntegrationServiceStub.ExportBulkData;
import com.rite.products.convertrite.stubs.erp.ErpIntegrationServiceStub.ExportBulkDataResponse;
import com.rite.products.convertrite.stubs.erp.ErpIntegrationServiceStub.GetESSExecutionDetails;
import com.rite.products.convertrite.stubs.erp.ErpIntegrationServiceStub.GetESSExecutionDetailsResponse;
import com.rite.products.convertrite.stubs.erp.ErpIntegrationServiceStub.LoadAndImportData;
import com.rite.products.convertrite.stubs.erp.ErpIntegrationServiceStub.LoadAndImportDataResponse;
import com.rite.products.convertrite.stubs.erp.ErpIntegrationServiceStub.UploadFileToUcm;
import com.rite.products.convertrite.stubs.erp.ErpIntegrationServiceStub.UploadFileToUcmResponse;
import com.rite.products.convertrite.utils.DataSourceUtil;
import com.rite.products.convertrite.utils.Utils;
import org.springframework.web.client.RestTemplate;

@Service
public class ErpIntegrationServiceImpl implements ErpIntegrationService {

	private static final Logger log = LoggerFactory.getLogger(ErpIntegrationServiceImpl.class);
	@Value("${convertrite-admin-host}")
	String ConvertriteAdminHost;

	@Autowired
	XxrCloudTemplateHeadersRepository xxrCloudTemplateHeadersRepository;
	@Autowired
	XxrCloudTableRepository xxrCloudTableRepository;
	@Autowired
	DataSourceUtil dataSourceUtil;
	@Autowired
	Utils util;
	@Autowired
	XxrErpIntegrationRepository xxrErpIntegrationRepository;
	@Autowired
	XxrObjectCodeGroupingLinesRepository xxrObjectCodeGroupingLinesRepository;
	@Autowired
	CrObjectGroupLinesRepo crObjectGroupLinesRepo;
	@Autowired
	XxrTemplateRelationRepository xxrTemplateRelationRepository;
	@Autowired
	XxrLookUpValuesRepository xxrLookUpValuesRepository;
	@Autowired
	XxrCloudConfigRepository xxrCloudConfigRepository;
	@Autowired
	XxrErpIntegrationMetaDataRepository xxrErpIntegrationMetaDataRepository;
	@Autowired
	XxrCldTempHdrsViewRepository xxrCldTempHdrsViewRepository;
	@Autowired
	CrCloudTemplateHeadersViewRepository crCloudTemplateHeadersViewRepository;
	@Autowired
	CloudTemplateHeaderDaoImpl cloudTemplateHeaderDaoImpl;
	@Autowired
	Utils utils;
	@Value("${file.upload.dir.oracle}")
	private String fileUploadDir;
	@Value("${erp_url}")
	private String erpurl;
	@Value("${import-callback-url}")
	private String callBackUrl;

	@Override
	public LoadandImportDataResPo loadAndImportData(LoadandImportDataReqPo loadandImportDataReqPo)
			throws ValidationException, Exception {
		log.info("Start of  loadAndImportData Method#####");

		Path target = null;
		List<XxrCloudTemplateHeader> cloudTemplateHeaderList = new ArrayList<>();
		Long metaDataTableId = null;
		String metaDataTableName = "";
		Long cloudTemplateId = null;
		// String clobString = "";
		Long objectId = null;
		Long parentObjectId = null;
		Long groupId = null;
		LoadandImportDataResPo loadImportDataRes = new LoadandImportDataResPo();
		String csvName = "";
		Long resultId = null;
		try {
			XxrErpIntegration xxrErpIntegration = xxrErpIntegrationRepository.findByXxrBatchNameAndCloudTemplateId(
					loadandImportDataReqPo.getBatchName(), loadandImportDataReqPo.getCloudTemplateId());
			if (xxrErpIntegration != null)
				throw new ValidationException("Load Import already executed for this batchName");
			cloudTemplateId = loadandImportDataReqPo.getCloudTemplateId();
			XxrCldTempHdrsView xxrCldTempHdrsView = xxrCldTempHdrsViewRepository.findByTemplateId(cloudTemplateId);
			// XxrCloudTemplateHeader xxrCloudTemplateHeader =
			// cloudTemplateHeaderList.get(0);
			objectId = xxrCldTempHdrsView.getObjectId();
			parentObjectId = xxrCldTempHdrsView.getParentObjectId();
			String objecCode = xxrCldTempHdrsView.getObjectCode();
			String parentObjectCode = xxrCldTempHdrsView.getParentObjectCode();
			csvName = util.getCtlFileName(objecCode);
			if (xxrCldTempHdrsView != null)
				metaDataTableId = xxrCldTempHdrsView.getMetaDataTableId();
			if (metaDataTableId != null) {
				metaDataTableName = xxrCldTempHdrsView.getMetaDataTableName();
			}
			// Create csv from clob in temp directory
			target = Files.createTempDirectory(null);
			log.debug("target:::::" + target);
			groupId = xxrObjectCodeGroupingLinesRepository.getGroupIdbyObjectId(objectId);
			XxrTemplateRelation xxrTemplateRelation = null;
			String zipfilePath = "";
			if (groupId != null) {
				// create csv files and zip them
				zipfilePath = target + File.separator + util.getCtlFileName(objecCode) + ".zip";
				xxrTemplateRelation = creatingCsvFilesAndZip(loadandImportDataReqPo, target, groupId);
			} else {
				writingToCsv(cloudTemplateId, target.toString(), csvName);
				zipfilePath = target + File.separator + metaDataTableName + ".zip";
				// zip csv file
				Utils.zipFile(target.toString(), metaDataTableName, csvName + ".csv");
			}
			// String url =
			// "https://ucf3-ztzb-fa-ext.oracledemos.com:443/fscmService/ErpIntegrationService";
			XxrErpIntegrationMetaData erpMetaData = xxrErpIntegrationMetaDataRepository
					.findByParentObjectIdAndObjectId(parentObjectId, objectId);
			if (erpMetaData == null)
				throw new Exception(
						"There is no configuration for LoadImport MetaData for these objectId & parentObjectId");

			String soapUrl = util.getCloudUrl(xxrCldTempHdrsView) + erpurl;
			ErpIntegrationServiceStub erpIntegrationServiceStub = new ErpIntegrationServiceStub(soapUrl);
			// BasicAuthentication for soap service
			util.basicAuthentication(erpIntegrationServiceStub._getServiceClient(), xxrCldTempHdrsView);

			// soapservice call for loadAndImportData
//			resultId = loadAndImportService(erpIntegrationServiceStub, loadandImportDataReqPo, zipfilePath,
//					erpMetaData);

			/*
			 * if ("Customer".equalsIgnoreCase(parentObjectCode)) { //Independent soap
			 * service call for uploadFileToUcm, ExportBulkData Application Tables resultId
			 * = loadAndImportIndependentService(erpIntegrationServiceStub,
			 * loadandImportDataReqPo, zipfilePath,erpMetaData); } else { // soapservice
			 * call for loadAndImportData resultId =
			 * loadAndImportService(erpIntegrationServiceStub, loadandImportDataReqPo,
			 * zipfilePath,erpMetaData); }
			 */
			// save details into ERPINEGRATION table
			saveErpIntegrationDetails(loadandImportDataReqPo, resultId, erpMetaData);

			/*
			 * if (xxrTemplateRelation != null) { xxrTemplateRelation.setIsZipped("Y");
			 * xxrTemplateRelationRepository.save(xxrTemplateRelation); }
			 */
			loadImportDataRes.setResultId(resultId);
			loadImportDataRes.setMessage("Sucessfully load and import submitted");

		} catch (ValidationException e) {
			// e.printStackTrace();
			log.error(e.getMessage());
			throw new ValidationException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			throw new Exception(e.getMessage());
		}
		// TODO Auto-generated method stub
		return loadImportDataRes;
	}

	private XxrTemplateRelation creatingCsvFilesAndZip(LoadandImportDataReqPo loadandImportDataReqPo, Path target,
			Long groupId) throws Exception {
		String clobString = "";
		Long objectId = null;
		XxrTemplateRelation xxrTemplateRelation = null;
		try {
			Predicate<XxrTemplateRelation> filterTemplateGrouping = x -> {
				String[] strArr = x.getTemplateIds().split(",");
				Long cldTemplateId = loadandImportDataReqPo.getCloudTemplateId();
				boolean flag = false;
				for (String s : strArr) {
					long templateId = Long.parseLong(s);
					if (templateId == cldTemplateId) {
						flag = true;
						break;
					}
				}
				return flag;
			};
			List<XxrTemplateRelation> xxrTemplateRelationLi = xxrTemplateRelationRepository
					.getTemplatRelations(groupId);
			if (xxrTemplateRelationLi.stream().filter(filterTemplateGrouping).count() <= 0)
				throw new ValidationException(
						"This Template is not part of template grouping,Please add into template grouping");
			xxrTemplateRelation = xxrTemplateRelationLi.stream().filter(filterTemplateGrouping).findFirst().get();
			if (xxrTemplateRelation != null) {
				String templateArr[] = xxrTemplateRelation.getTemplateIds().split(",");
				List<String> listOfCsvContents = new ArrayList<>();
				List<String> fileNames = new ArrayList<>();
				for (String tempId : templateArr) {
					long templateId = Long.parseLong(tempId);
					XxrCldTempHdrsView xxrCldTempHdrsView = xxrCldTempHdrsViewRepository.findByTemplateId(templateId);
					// XxrCloudTemplateHeader cloudTemplateHeader = cloudTemplateHeaderLi.get(0);
					objectId = xxrCldTempHdrsView.getObjectId();
					String objectCode = xxrCldTempHdrsView.getObjectCode();
					// util.getFileName(objectCode);
					String sql = "select xxr_conversion_utils_pkg.fbdi_filegen(" + templateId + ") from dual";
					clobString = util.getClobString(sql);
					// System.out.println(clobString+"clobString##########");
					listOfCsvContents.add(clobString);
					// fileNames.add(util.getFileName(objectCode));
					fileNames.add(util.getCtlFileName(objectCode));
				}
				Utils.zipMultipleFiles(listOfCsvContents, target.toString(), fileNames);
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return xxrTemplateRelation;
	}

	private ZipCsvFilesResPo creatingCsvFilesAndZipV1(LoadandImportDataReqPo loadandImportDataReqPo, Path target,
			Long groupId, ChannelSftp channelSftp) throws ValidationException, Exception {
		String clobString = "";
		Long objectId = null;
		XxrTemplateRelation xxrTemplateRelation = null;
		String zipPath = null;
		ZipCsvFilesResPo zipCsvFilesResPo = new ZipCsvFilesResPo();
		try {
			Predicate<XxrTemplateRelation> filterTemplateGrouping = x -> {
				String[] strArr = x.getTemplateIds().split(",");
				Long cldTemplateId = loadandImportDataReqPo.getCloudTemplateId();
				boolean flag = false;
				for (String s : strArr) {
					long templateId = Long.parseLong(s);
					if (templateId == cldTemplateId) {
						flag = true;
						break;
					}
				}
				return flag;
			};
			List<XxrTemplateRelation> xxrTemplateRelationLi = xxrTemplateRelationRepository
					.getTemplatRelations(groupId);
			if (xxrTemplateRelationLi.stream().filter(filterTemplateGrouping).count() <= 0)
				throw new ValidationException(
						"This Template is not part of template grouping,Please add into template grouping");
			xxrTemplateRelation = xxrTemplateRelationLi.stream().filter(filterTemplateGrouping).findFirst().get();
			if (xxrTemplateRelation != null) {
				String templateArr[] = xxrTemplateRelation.getTemplateIds().split(",");
				// checking wether loadimport is executed for one of template in grouping
				Long[] arr = new Long[templateArr.length];
				for (int i = 0; i < templateArr.length; i++) {
					arr[i] = Long.parseLong(templateArr[i]);
				}
				List<Long> listofTemplates = Arrays.asList(arr);
				XxrErpIntegration xxrErpIntegration = xxrErpIntegrationRepository
						.findByXxrBatchNameAndCloudTemplateIdIn(loadandImportDataReqPo.getBatchName(), listofTemplates);
				if (xxrErpIntegration != null)
					throw new ValidationException("FBDI Template Grouping applied");

				List<String> listOfCsvContents = new ArrayList<>();
				List<String> fileNames = new ArrayList<>();
				for (String tempId : templateArr) {
					long templateId = Long.parseLong(tempId);
					//XxrCldTempHdrsView xxrCldTempHdrsView = xxrCldTempHdrsViewRepository.findByTemplateId(templateId);
					CrCloudTemplateHeadersView crCloudTemplateHeadersView=crCloudTemplateHeadersViewRepository.findById(templateId).get();
					objectId = crCloudTemplateHeadersView.getObjectId();
					String objectCode = crCloudTemplateHeadersView.getObjectCode();
					String stagingTableName = crCloudTemplateHeadersView.getStagingTableName();
					// util.getFileName(objectCode);

//					clobString = cloudTemplateHeaderDaoImpl.downloadFbdiImport(templateId,
//							loadandImportDataReqPo.getBatchName(),
//							stagingTableName + "_" + loadandImportDataReqPo.getBatchName().toUpperCase() + ".csv", channelSftp);
					clobString=cloudTemplateHeaderDaoImpl.downloadFbdiData(templateId,loadandImportDataReqPo.getBatchName());
					// System.out.println(clobString+"clobString##########");
					listOfCsvContents.add(clobString);
					// fileNames.add(util.getFileName(objectCode));
					fileNames.add(util.getCtlFileName(objectCode));
				}
				zipPath = Utils.zipMultipleFiles(listOfCsvContents, target.toString(), fileNames);
			}
		} catch (ValidationException e) {
			throw new ValidationException(e.getMessage());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		zipCsvFilesResPo.setXxrTemplateRelation(xxrTemplateRelation);
		zipCsvFilesResPo.setZipFilePath(zipPath);
		return zipCsvFilesResPo;
	}

	private void writingToCsv(Long cloudTemplateId, String target, String csvName) throws Exception {
		String clobString = "";
		String sql = "select xxr_conversion_utils_pkg.fbdi_filegen(" + cloudTemplateId + ") from dual";
		clobString = util.getClobString(sql);
		String filePath = target + File.separator + csvName + ".csv";
		File file = new File(filePath);
		FileWriter outputfile = new FileWriter(file);
		// create CSVWriter with ',' as separator
		CSVWriter writerObj = new CSVWriter(outputfile, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
				CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
		if (!Validations.isNullOrEmpty(clobString))
			writerObj.writeNext(clobString.split(","));
		writerObj.flush();
		writerObj.close();
	}

	private void writingToCsvV1(Long cloudTemplateId, String target, String csvName,
			LoadandImportDataReqPo loadandImportDataReqPo, CrCloudTemplateHeadersView xxrCldTempHdrsView,
			ChannelSftp channelSftp) throws Exception {
		String clobString = "";

		HttpServletResponse response = null;
		 clobString =cloudTemplateHeaderDaoImpl.downloadFbdiData(cloudTemplateId, loadandImportDataReqPo.getBatchName());

//		clobString = cloudTemplateHeaderDaoImpl.downloadFbdiImport(cloudTemplateId,
//				loadandImportDataReqPo.getBatchName(),
//				xxrCldTempHdrsView.getStagingTableName() + "_" + loadandImportDataReqPo.getBatchName().toUpperCase() + ".csv",
//				channelSftp);
		String filePath = target + File.separator + csvName + ".csv";
		File file = new File(filePath);
		FileWriter outputfile = new FileWriter(file);
		// create CSVWriter with ',' as separator
		CSVWriter writerObj = new CSVWriter(outputfile, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
				CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
		if (!Validations.isNullOrEmpty(clobString))
			writerObj.writeNext(clobString.split(","));
		writerObj.flush();
		writerObj.close();
	}

	private Long loadAndImportIndependentService(ErpIntegrationServiceStub erpIntegrationServiceStub,
			LoadandImportDataReqPo loadImportDataReq, String zipfilePath, XxrErpIntegrationMetaData erpMetaData)
			throws Exception {
		log.info("Start of loadAndImportService Method######");
		Long result = null;
		try {
			DataSource source = new ByteArrayDataSource(Files.readAllBytes(Paths.get(zipfilePath)),
					"text/plain;charset=UTF-8");
			DataHandler dataHandler = new DataHandler(source);
			Base64BinaryDataHandler base64BinaryDataHandler = new Base64BinaryDataHandler();
			base64BinaryDataHandler.setBase64BinaryDataHandler(dataHandler);

			// Creating DocumentDetails
			DocumentDetails documentDetails = new DocumentDetails();
			documentDetails.setContentType("zip");
			documentDetails.setContent(base64BinaryDataHandler);
			documentDetails.setDocumentAccount(erpMetaData.getDocumentAccount());
			documentDetails.setDocumentAuthor("ConvertRite");
			documentDetails.setDocumentSecurityGroup(erpMetaData.getDocumentSecurityGroup());
			String objecCode = xxrLookUpValuesRepository.getValueById(erpMetaData.getObjectId());
			documentDetails.setDocumentTitle(objecCode + ".zip");
			documentDetails.setFileName(objecCode + ".zip");

			UploadFileToUcm uploadFileToUcm0 = new UploadFileToUcm();
			uploadFileToUcm0.setDocument(documentDetails);
			// upload File to UCM service call
			UploadFileToUcmResponse uploadFileRes = erpIntegrationServiceStub.uploadFileToUcm(uploadFileToUcm0);
			String documentId = uploadFileRes.getResult();
			String parameterList = loadImportDataReq.getParameterList();
			if (parameterList.contains("{0}")) {
				parameterList = parameterList.replace("{0}", documentId);
//				log.info(parameterList);
			}
			ExportBulkData exportBulkData = new ExportBulkData();
			exportBulkData.setJobName(erpMetaData.getJobName());
			exportBulkData.setCallbackURL("#NULL");
			exportBulkData.setParameterList(parameterList);
			exportBulkData.setNotificationCode("30");
			ExportBulkDataResponse exportBulkRes = erpIntegrationServiceStub.exportBulkData(exportBulkData);
			result = exportBulkRes.getResult();

		} catch (Exception e) {
			// e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return result;
	}

	private Long loadAndImportService(ErpIntegrationServiceStub erpIntegrationServiceStub,
			LoadandImportDataReqPo loadImportDataReq, String zipfilePath, XxrErpIntegrationMetaData erpMetaData,ObjectInfoWithPodClodConfigPo objectData)
			throws Exception {
		log.info("Start of loadAndImportService Method######");
		Long result = null;
		try {

			DataSource source = new ByteArrayDataSource(Files.readAllBytes(Paths.get(zipfilePath)),
					"text/plain;charset=UTF-8");
			DataHandler dataHandler = new DataHandler(source);
			Base64BinaryDataHandler base64BinaryDataHandler = new Base64BinaryDataHandler();
			base64BinaryDataHandler.setBase64BinaryDataHandler(dataHandler);

			// Creating DocumentDetails
			DocumentDetails documentDetails = new DocumentDetails();
			documentDetails.setContentType("zip");
			documentDetails.setContent(base64BinaryDataHandler);

			LoadAndImportData loadAndImportData = new LoadAndImportData();
			EssJob[] essJob = new EssJob[1];
			EssJob job = new EssJob();
			job.setParameterList(loadImportDataReq.getParameterList());
			if(loadImportDataReq.getObjectName().equalsIgnoreCase("Supplier Import")){
				System.out.println("Supplier Import-->"+loadImportDataReq.getObjectName());
				job.setParameterList("NEW,No,BATCH_CR_2");
				documentDetails.setDocumentAccount("prc/supplier/import");//
				documentDetails.setDocumentSecurityGroup("FAFusionImportExport");
				job.setJobName("/oracle/apps/ess/prc/poz/supplierImport,ImportSuppliers");//info_type
				loadAndImportData.setInterfaceDetails("24");
			}else if(loadImportDataReq.getObjectName().equalsIgnoreCase("Supplier Address Import")){
				System.out.println("Supplier Address Import-->"+loadImportDataReq.getObjectName());
				job.setParameterList("NEW,No,BATCH_CR_2");
				documentDetails.setDocumentAccount("prc/supplier/import");
				documentDetails.setDocumentSecurityGroup("FAFusionImportExport");
				job.setJobName("/oracle/apps/ess/prc/poz/supplierImport,ImportSupplierAddresses");
				loadAndImportData.setInterfaceDetails("56");
			}
			else if(loadImportDataReq.getObjectName().equalsIgnoreCase("Invoice Header")){
				System.out.println("Invoice Header-->"+loadImportDataReq.getObjectName());
				documentDetails.setDocumentAccount("fin/payables/import");
				documentDetails.setDocumentSecurityGroup("FAFusionImportExport");
				job.setJobName("/oracle/apps/ess/financials/payables/invoices/transactions/,APXIIMPT");
				loadAndImportData.setInterfaceDetails("1");
			}else if(loadImportDataReq.getObjectName().equalsIgnoreCase("Journal Import")){
				System.out.println("Items Import-->"+loadImportDataReq.getObjectName());
				documentDetails.setDocumentAccount("fin/generalLedger/import");
				documentDetails.setDocumentSecurityGroup("FAFusionImportExport");
				job.setJobName("/oracle/apps/ess/financials/generalLedger/programs/common/,JournalImportLauncher");
				loadAndImportData.setInterfaceDetails("15");
			//}else if(("Items Import").equalsIgnoreCase(loadImportDataReq.getObjectName())){
			}else if(loadImportDataReq.getObjectName().equalsIgnoreCase("Items Import")||loadImportDataReq.getObjectName().equalsIgnoreCase("Item Categories")){
				System.out.println("Items -->"+loadImportDataReq.getObjectName());
				documentDetails.setDocumentAccount("scm/item/import");
				documentDetails.setDocumentSecurityGroup("FAFusionImportExport");
				job.setJobName("/oracle/apps/ess/scm/productModel/items/,ItemImportJobDef");
				loadAndImportData.setInterfaceDetails("29");
			}else if(loadImportDataReq.getObjectName().equalsIgnoreCase("Parties")){
				System.out.println("loadImportDataReq.getObjectName()-->"+loadImportDataReq.getObjectName());
				documentDetails.setDocumentAccount("fin/receivables/import");
				documentDetails.setDocumentSecurityGroup("FAFusionImportExport");
				job.setJobName("/oracle/apps/ess/cdm/foundation/bulkImport,CDMAutoBulkImportJob");
				loadAndImportData.setInterfaceDetails("4");
			}

			documentDetails.setDocumentAuthor("ConvertRite");

			//xxrLookUpValuesRepository -- not required
			//get objcet code CRObjects
		//	String objecCode = xxrLookUpValuesRepository.getValueById(erpMetaData.getObjectId());
			documentDetails.setDocumentTitle(objectData.getObjectCode() + ".zip");
			documentDetails.setFileName(objectData.getObjectCode() + ".zip");

			// creating job details
			essJob[0] = job;
			loadAndImportData.setJobList(essJob);
			loadAndImportData.setDocument(documentDetails);

		//	loadAndImportData.setCallbackURL(callBackUrl);
			LoadAndImportDataResponse resp = erpIntegrationServiceStub.loadAndImportData(loadAndImportData);

			result = resp.getResult();
			System.out.println("result-->"+result);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return result;
	}

	private void saveErpIntegrationDetails(LoadandImportDataReqPo loadImportDataReq, Long result,
			XxrErpIntegrationMetaData erpMetaData) throws Exception {
		XxrErpIntegration erpIntegration = new XxrErpIntegration();
		try {
			erpIntegration.setContentType("zip");
			erpIntegration.setCloudTemplateId(loadImportDataReq.getCloudTemplateId());
			erpIntegration.setDocumentAccount(erpMetaData.getDocumentAccount());
			erpIntegration.setDocumentAuthor("ConvertRite");
			erpIntegration.setDocumentSecurityGroup(erpMetaData.getDocumentSecurityGroup());
			String objecCode = xxrLookUpValuesRepository.getValueById(erpMetaData.getObjectId());
			erpIntegration.setDocumentTitle(objecCode + ".zip");
			erpIntegration.setFileName(objecCode + ".zip");
			erpIntegration.setInterfaceDetails(erpMetaData.getInterfaceDetails());
			erpIntegration.setJobName(erpMetaData.getJobName());
			erpIntegration.setParameterList(loadImportDataReq.getParameterList());
			erpIntegration.setResult(result);
			erpIntegration.setXxrBatchName(loadImportDataReq.getBatchName());
			erpIntegration.setCreationDate(new java.sql.Date(new java.util.Date().getTime()));
			erpIntegration.setCreatedBy("ConvertRite");
			erpIntegration.setLastUpdatedDate(new java.sql.Date(new java.util.Date().getTime()));
			erpIntegration.setLastUpdateBy("ConvertRite");
			xxrErpIntegrationRepository.save(erpIntegration);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public LoadImportJobStatusResPo getJobStatus(Long resultId, Long cldTemplateId) throws Exception {
		// TODO Auto-generated method stub
		log.info("Start of getJobStatus Method######");
		LoadImportJobStatusResPo loadImportJobStatusRes = new LoadImportJobStatusResPo();
		try {
			// String url =
			// "https://ucf3-ztzb-fa-ext.oracledemos.com:443/fscmService/ErpIntegrationService";
			XxrCldTempHdrsView xxrCldTempHdrsView = xxrCldTempHdrsViewRepository.findByTemplateId(cldTemplateId);
			String soapUrl = util.getCloudUrl(xxrCldTempHdrsView) + erpurl;
			ErpIntegrationServiceStub erpIntegrationServiceStub = new ErpIntegrationServiceStub(soapUrl);

			// BasicAuthentication for soap service
			util.basicAuthentication(erpIntegrationServiceStub._getServiceClient(), xxrCldTempHdrsView);
			// Execution details
			GetESSExecutionDetails executionDetails = new GetESSExecutionDetails();
			executionDetails.setRequestId(resultId);

			GetESSExecutionDetailsResponse response = erpIntegrationServiceStub
					.getESSExecutionDetails(executionDetails);
			loadImportJobStatusRes.setResult(response.getResult());
			loadImportJobStatusRes.setMessage("Retrieved Job Status successfully");
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return loadImportJobStatusRes;
	}

	@Override
	public List<XxrErpIntegration> getErpIntegrationDetails(ErpIntegrationPageReqPo erpIntegrationPageReqPo,
			HttpHeaders httpHeaders) throws Exception {
		log.info("Start of getErpIntegrationDetails Method#####");
		// TODO Auto-generated method stub
		List<XxrErpIntegration> erpIntegrationLi = new ArrayList<>();
		try {

			Pageable pageable = PageRequest.of(erpIntegrationPageReqPo.getPageNo(),
					erpIntegrationPageReqPo.getPageSize(),
					Sort.by(erpIntegrationPageReqPo.getSortDirection(), erpIntegrationPageReqPo.getSortBy()));
			Page<XxrErpIntegration> pageContent = xxrErpIntegrationRepository.findAll(pageable);
			httpHeaders.set("pagecount", String.valueOf(pageContent.getTotalPages()));
			httpHeaders.set("totalcount", String.valueOf(pageContent.getTotalElements()));
			if (pageContent.hasContent())
				erpIntegrationLi = pageContent.getContent();

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return erpIntegrationLi;
	}

	@Override
	public byte[] downloadExportOutput(String requestId, Long cldTemplateId, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		log.info("Start of downloadExportOutput Method#####");
		// ResponseBuilder response = null ;

		response.setContentType("application/zip");
		response.setStatus(HttpServletResponse.SC_OK);
		response.addHeader("Content-Disposition", "attachment; filename=" + requestId + ".zip");
		// Response resp=null;
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = null;
		try {
			DownloadExportOutput downloadExportOutput = new DownloadExportOutput();
			downloadExportOutput.setRequestId(requestId);
			// String url =
			// "https://ucf3-ztzb-fa-ext.oracledemos.com:443/fscmService/ErpIntegrationService";
			XxrCldTempHdrsView xxrCldTempHdrsView = xxrCldTempHdrsViewRepository.findByTemplateId(cldTemplateId);
			String soapUrl = util.getCloudUrl(xxrCldTempHdrsView) + erpurl;
			ErpIntegrationServiceStub erpIntegrationServiceStub = new ErpIntegrationServiceStub(soapUrl);

			// BasicAuthentication for soap service
			util.basicAuthentication(erpIntegrationServiceStub._getServiceClient(), xxrCldTempHdrsView);

			DownloadExportOutputResponse downloadExportOutputResponse = erpIntegrationServiceStub
					.downloadExportOutput(downloadExportOutput);
			// System.out.println(DownloadExportOutputResponse.MY_QNAME+"##########MY_QNAME");
			// QName qName=new QName("ns0:","Content");

			DocumentDetails[] documentDetails = downloadExportOutputResponse.getResult();

			DocumentDetails details = documentDetails[0];

			DataHandler dataHandler = details.getContent().getBase64BinaryDataHandler();
			log.info("dataHandler::::" + dataHandler);
			buf = dataHandler.getDataSource().getInputStream().readAllBytes();
			/*
			 * OMElement omEle= downloadExportOutputResponse.getOMElement(new
			 * QName("Content"), org.apache.axiom.om.OMAbstractFactory.getOMFactory());
			 * System.out.println(omEle+"###########omEle");
			 * 
			 * System.out.println(omEle.getAttribute(new
			 * QName("ns0:Content"))+"contegentTag");
			 * 
			 * OMElement child = (OMElement) omEle.getFirstOMChild();
			 * System.out.println(child+"###########child"); OMAttribute attr =
			 * child.getAttribute(new QName("href"));
			 * 
			 * //Content ID processing String contentID = attr.getAttributeValue();
			 * contentID = contentID.trim(); if (contentID.substring(0,
			 * 3).equalsIgnoreCase("cid")) { contentID = contentID.substring(4); }
			 * 
			 * MessageContext msgCtx = MessageContext.getCurrentMessageContext();
			 * Attachments attachment = msgCtx.getAttachmentMap(); DataHandler dataHandler =
			 * attachment.getDataHandler(contentID);
			 */
			/*
			 * ZipOutputStream zos = new ZipOutputStream(baos); ZipEntry entry = new
			 * ZipEntry(requestId+".zip"); entry.setSize(buf.length);
			 * zos.putNextEntry(entry); zos.write(buf); zos.closeEntry(); zos.close();
			 */
			/*
			 * java.io.FileOutputStream out_zip_file = new
			 * java.io.FileOutputStream(requestId+".zip");
			 * out_zip_file.write(baos.toByteArray()); response= Response.ok((Object)
			 * out_zip_file); response.header("Content-Disposition",
			 * "attachment; filename="+requestId+".zip"); //
			 * response.setContentType("application/zip"); resp=response.build();
			 * out_zip_file.flush(); out_zip_file.close();
			 */

		} catch (Exception e) {
			// e.printStackTrace();
			throw new Exception(e.getMessage());
		}

		return buf;
	}

	@Override
	public LoadandImportDataResPo loadAndImportDataV1(LoadandImportDataReqPo loadandImportDataReqPo, String bearerToken)
			throws Exception, ValidationException {
		// TODO Auto-generated method stub
		log.info("Start of loadAndImportDataV1 #######");
		Path target = null;
		List<XxrCloudTemplateHeader> cloudTemplateHeaderList = new ArrayList<>();
		Long metaDataTableId = null;
		String metaDataTableName = "";
		Long cloudTemplateId = null;
		// String clobString = "";
		Long objectId = null;
		Long parentObjectId = null;
		Long groupId = null;
		LoadandImportDataResPo loadImportDataRes = new LoadandImportDataResPo();
		String csvName = "";
		Long resultId = null;
		String zipfilePath = "";
		Session jschSession = null;
		ChannelSftp channelSftp = null;

		try {
			ObjectMapper mapper = new ObjectMapper();
			List<ObjectInfoWithPodClodConfigPo> objectsInfoDetailsList=null;
			List<CloudLoginDetails> cloudLoginDetailsList=null;
			List<ObjectInfoWithPodClodConfigPo> newJsonNode =null;
			JsonNode node=	getObjectsWithInformation(loadandImportDataReqPo, bearerToken);
			JsonNode objectsDetailsNode=node.get("objectsDetails");
			JsonNode cloudLoginDetailsNode=node.get("cloudLoginDetails");
			newJsonNode = mapper.treeToValue(objectsDetailsNode, List.class);
			if(newJsonNode.size()>0){
				objectsInfoDetailsList = mapper.convertValue(objectsDetailsNode,new TypeReference<List<ObjectInfoWithPodClodConfigPo>>() { });
				cloudLoginDetailsList = mapper.convertValue(cloudLoginDetailsNode,new TypeReference<List<CloudLoginDetails>>() { });
				//System.out.println("URL-->"+objectsInfoDetailsList);
				System.out.println("URL-->"+cloudLoginDetailsList);
			}else{
				System.out.println("else-->"+ "No data Found");
				//return  "No data Found";
			}
//			//xxrErpIntegrationRepository -- object information
//			XxrErpIntegration xxrErpIntegration = xxrErpIntegrationRepository.findByXxrBatchNameAndCloudTemplateId(
//					loadandImportDataReqPo.getBatchName(), loadandImportDataReqPo.getCloudTemplateId());
//			if (xxrErpIntegration != null)
//				throw new ValidationException("Already BatchName exists");

			cloudTemplateId = loadandImportDataReqPo.getCloudTemplateId();
			CrCloudTemplateHeadersView crCldTempHdrsView = crCloudTemplateHeadersViewRepository.findById(cloudTemplateId).get();

			objectId = crCldTempHdrsView.getObjectId();
			parentObjectId = crCldTempHdrsView.getParentObjectId();
			String objecCode = crCldTempHdrsView.getObjectCode();
			String parentObjectCode = crCldTempHdrsView.getParentObjectCode();
			csvName = util.getCtlFileName(objecCode);
			if (crCldTempHdrsView != null)
				metaDataTableId = crCldTempHdrsView.getMetaDataTableId();
			if (metaDataTableId != null) {
				metaDataTableName = crCldTempHdrsView.getMetaDataTableName();
				log.info("metaDataTableName:::::" + metaDataTableName);
			}
			// Create csv from clob in temp directory
			target = Files.createTempDirectory(null);
			log.debug("target:::::" + target);
//			//xxrObjectCodeGroupingLinesRepository --Ignore as of now
		//	groupId = xxrObjectCodeGroupingLinesRepository.getGroupIdbyObjectId(objectId);
			groupId=crObjectGroupLinesRepo.getGroupIdbyObjectId(objectId);
			ZipCsvFilesResPo zipCsvFilesResPo = new ZipCsvFilesResPo();

			// connecting to Ftp location and fetching file
			jschSession = utils.setupJschSession();
			channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
			channelSftp.connect();
			channelSftp.cd(fileUploadDir);
				//Skip multile files zip as of now
			if (groupId != null) {
				// create csv files and zip them
				zipCsvFilesResPo = creatingCsvFilesAndZipV1(loadandImportDataReqPo, target, groupId, channelSftp);
				//zipCsvFilesResPo = creatingCsvFilesAndZipV2(loadandImportDataReqPo, target, groupId, channelSftp);
				zipfilePath = zipCsvFilesResPo.getZipFilePath();
			}
			//do this for single zip file
			else {
				writingToCsvV1(cloudTemplateId, target.toString(), csvName, loadandImportDataReqPo, crCldTempHdrsView,
						channelSftp);
				zipfilePath = target + File.separator + metaDataTableName + ".zip";
				// zip csv file
				Utils.zipFile(target.toString(), metaDataTableName, csvName + ".csv");
			}
			// String url =
			// "https://ucf3-ztzb-fa-ext.oracledemos.com:443/fscmService/ErpIntegrationService";
			//xxrErpIntegrationMetaDataRepository --CrObjectInformation
			XxrErpIntegrationMetaData erpMetaData = xxrErpIntegrationMetaDataRepository
					.findByParentObjectIdAndObjectId(parentObjectId, objectId);
			if (erpMetaData == null)
				throw new Exception(
						"There is no configuration for LoadImport MetaData for these objectId & parentObjectId");
			//String soapUrl = util.getCloudUrl(crCldTempHdrsView) + erpurl;
			String soapUrl="https://fa-etao-dev20-saasfademo1.ds-fa.oraclepdemos.com/fscmService/ErpIntegrationService";
			ErpIntegrationServiceStub erpIntegrationServiceStub = new ErpIntegrationServiceStub(soapUrl);
			// BasicAuthentication for soap service
			util.basicAuthentication(erpIntegrationServiceStub._getServiceClient(),null);

			// soapservice call for loadAndImportData
//			resultId = loadAndImportService(erpIntegrationServiceStub, loadandImportDataReqPo, zipfilePath,
//					erpMetaData);

			// save details into ERPINEGRATION table
			//Ignore as of now
			saveErpIntegrationDetails(loadandImportDataReqPo, resultId, erpMetaData);

			loadImportDataRes.setResultId(resultId);
			loadImportDataRes.setMessage("Sucessfully load and import submitted");

		} catch (ValidationException e) {
			// e.printStackTrace();
			log.error(e.getMessage());
			throw new ValidationException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			throw new Exception(e.getMessage());
		} finally {
			if (!Validations.isNullOrEmpty(zipfilePath))
				Files.deleteIfExists(Paths.get(zipfilePath));

			if (channelSftp != null) {
				channelSftp.exit();
				channelSftp.disconnect();
			}
			if (jschSession != null)
				jschSession.disconnect();

		}
		// TODO Auto-generated method stub
		return null;
	}

	private JsonNode getObjectsWithInformation(LoadandImportDataReqPo loadandImportDataReqPo, String bearerToken) {
		HttpHeaders header = new HttpHeaders();
		header.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		System.out.println("bearerToken-->"+bearerToken);
		header.set("Authorization",bearerToken);
		HttpEntity<String> entity = new HttpEntity<String>(header);
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;
		JsonNode name=null;
		try {
			String url=ConvertriteAdminHost + "/api/convertriteadmin/getObjectsWithInformation?clientId="+loadandImportDataReqPo.getClientId()+"&podId="+loadandImportDataReqPo.getPodId()+"&objectName="+loadandImportDataReqPo.getObjectName();
			ResponseEntity<String> objects = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			System.out.println("url-->"+url);
			root = mapper.readTree(objects.getBody());
			name = root.path("payload");

		}catch (Exception e){
			e.printStackTrace();
		}
		return name;
	}


	@Override
	public LoadandImportDataResPo loadAndImportDataV2(LoadandImportDataReqPo loadandImportDataReqPo, String bearerToken) {
		log.info("Start of loadAndImportDataV1 #######");
		Path target = null;
		List<XxrCloudTemplateHeader> cloudTemplateHeaderList = new ArrayList<>();
		Long metaDataTableId = null;
		String metaDataTableName = "";
		Long cloudTemplateId = null;
		// String clobString = "";
		Long objectId = null;
		Long parentObjectId = null;
		Long groupId = null;
		LoadandImportDataResPo loadImportDataRes = new LoadandImportDataResPo();
		//String csvName = "HzImpPartiesT";
		String csvName = "";
		Long resultId = null;
		String zipfilePath = "";
		Session jschSession = null;
		ChannelSftp channelSftp = null;

		try {
			getObjectsWithInformation(loadandImportDataReqPo,bearerToken);
			ObjectMapper mapper = new ObjectMapper();
			List<ObjectInfoWithPodClodConfigPo> objectsInfoDetailsList=null;
			List<CloudLoginDetails> cloudLoginDetailsList=null;
			List<ObjectInfoWithPodClodConfigPo> newJsonNode =null;
			JsonNode node=	getObjectsWithInformation(loadandImportDataReqPo, bearerToken);
			JsonNode objectsDetailsNode=node.get("objectsDetails");
			JsonNode cloudLoginDetailsNode=node.get("cloudLoginDetails");
			newJsonNode = mapper.treeToValue(objectsDetailsNode, List.class);
			if(newJsonNode.size()>0){
				objectsInfoDetailsList = mapper.convertValue(objectsDetailsNode,new TypeReference<List<ObjectInfoWithPodClodConfigPo>>() { });
				cloudLoginDetailsList = mapper.convertValue(cloudLoginDetailsNode,new TypeReference<List<CloudLoginDetails>>() { });
				//System.out.println("URL-->"+objectsInfoDetailsList);
				System.out.println("cloudLoginDetailsList-->"+cloudLoginDetailsList.size());
			}else{
				System.out.println("else-->"+ "No data Found");
				loadImportDataRes.setMessage("No data Found");
				return loadImportDataRes;
			}
		//	CrCloudTemplateHeadersView crCldTempHdrsView = crCloudTemplateHeadersViewRepository.findById(loadandImportDataReqPo.getCloudTemplateId()).get();

			target = Files.createTempDirectory(null);
			// connecting to Ftp location and fetching file
//			jschSession = utils.setupJschSession();
//			channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
//			channelSftp.connect();
//			channelSftp.cd(fileUploadDir);
			// BasicAuthentication for soap service
			if(objectsInfoDetailsList.size()>0){
				csvName=objectsInfoDetailsList.get(0).getCtlFileName();
			}else{
				System.out.println("Object Details Not Found");
				loadImportDataRes.setMessage("Object Details Not Found");
				return loadImportDataRes;
			}
			cloudTemplateId = loadandImportDataReqPo.getCloudTemplateId();
			CrCloudTemplateHeadersView crCldTempHdrsView = crCloudTemplateHeadersViewRepository.findById(cloudTemplateId).get();

			objectId = crCldTempHdrsView.getObjectId();
			parentObjectId = crCldTempHdrsView.getParentObjectId();
			String objecCode = crCldTempHdrsView.getObjectCode();
			String parentObjectCode = crCldTempHdrsView.getParentObjectCode();

			if (crCldTempHdrsView != null)
				metaDataTableId = crCldTempHdrsView.getMetaDataTableId();
			if (metaDataTableId != null) {
				metaDataTableName = crCldTempHdrsView.getMetaDataTableName();
				log.info("metaDataTableName:::::" + metaDataTableName);
			}

			writingToCsvV1(loadandImportDataReqPo.getCloudTemplateId(), target.toString(), csvName, loadandImportDataReqPo, null,
					null);
			zipfilePath = target + File.separator + metaDataTableName + ".zip";
			//zipfilePath="C:\\Code\\CR2.0\\Parties_V22D_INT.zip";
			// zip csv file
			Utils.zipFile(target.toString(), metaDataTableName, csvName + ".csv");
			//System.out.println("Zipfile Path-->"+target.toString(), metaDataTableName, csvName + ".csv");
			//String soapUrl = util.getCloudUrl(xxrCldTempHdrsView) + erpurl;
			String soapUrl="https://fa-etao-dev20-saasfademo1.ds-fa.oraclepdemos.com/fscmService/ErpIntegrationService";
			ErpIntegrationServiceStub erpIntegrationServiceStub = new ErpIntegrationServiceStub(soapUrl);
			XxrErpIntegrationMetaData erpMetaData=null;
			String	userName=null;
			String	password=null;

			// BasicAuthentication for soap service
			if(cloudLoginDetailsList.size()>0){
				userName=cloudLoginDetailsList.get(0).getUsername();
				password=cloudLoginDetailsList.get(0).getPassword();
			}else{
				System.out.println("No Pod Cloud Login Details Found");
				loadImportDataRes.setMessage("Pod Cloud Login Details Not Found");
				return loadImportDataRes;
			}
			util.basicAuthentication1(erpIntegrationServiceStub._getServiceClient(),userName,password);

			// soapservice call for loadAndImportData
			resultId = loadAndImportService(erpIntegrationServiceStub, loadandImportDataReqPo, zipfilePath,
					erpMetaData,objectsInfoDetailsList.get(0));
			loadImportDataRes.setResultId(resultId);
			loadImportDataRes.setMessage("Data Loaded successfully");
			System.out.println("resultId-->"+resultId);
		}catch (Exception e){
			e.printStackTrace();
			loadImportDataRes.setMessage("Data Loading Failed");
			loadImportDataRes.setError(e.getMessage());
		}
		return loadImportDataRes;
	}

}
