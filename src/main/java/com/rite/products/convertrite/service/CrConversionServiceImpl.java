package com.rite.products.convertrite.service;

import com.rite.products.convertrite.Validations.Validations;
import com.rite.products.convertrite.model.*;
import com.rite.products.convertrite.po.CrTemplateStatisticsResPo;
import com.rite.products.convertrite.po.CrProcessRequestsPagePo;
import com.rite.products.convertrite.po.ProcessJobPo;
import com.rite.products.convertrite.respository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CrConversionServiceImpl implements CrConversionService {
	@PersistenceContext
	private EntityManager entityManager;
	private static final Logger log = LoggerFactory.getLogger(CrConversionServiceImpl.class);

	@Autowired
	CrTemplateStateRepository crTemplateStateRepository;
	@Autowired
	CloudTemplateHeaderDaoImpl cloudTemplateHeaderDaoImpl;
	@Autowired
	CrTemplateStatisticsViewRepository crTemplateStatisticsViewRepository;

	@Autowired
	CrProcessRequestsViewRepository crProcessRequestsViewRepository;

	@Autowired
	CrCloudTemplateHeadersRepository crCloudTemplateHeadersRepository;
	@Autowired
	CrLookUpValuesRepo crLookUpValuesRepo;
	@Override
	public List<CrTemplateStateView> getTemplateState() throws Exception {
		return crTemplateStateRepository.findAll();
	}

	@Override
	public List<CrTemplateStatisticsResPo> getTemplateStatistics() throws Exception {
		List<CrTemplateStatisticsResPo> templateStatistic = new ArrayList<>();
		Map<String, List<CrTemplateStatisticsView>> dataByTemplate = crTemplateStatisticsViewRepository.findAll().stream()
				.collect(Collectors.groupingBy(CrTemplateStatisticsView::getCriteriaType));

		CrTemplateStatisticsResPo templateRes = new CrTemplateStatisticsResPo();
		templateRes.setCriteriaType("TEMPLATE");
		templateRes.setData(dataByTemplate.get("TEMPLATE"));
		CrTemplateStatisticsResPo podRes = new CrTemplateStatisticsResPo();
		podRes.setData(dataByTemplate.get("POD"));
		podRes.setCriteriaType("POD");
		CrTemplateStatisticsResPo projectRes = new CrTemplateStatisticsResPo();
		projectRes.setData(dataByTemplate.get("PROJECT"));
		projectRes.setCriteriaType("PROJECT");
		CrTemplateStatisticsResPo objectRes = new CrTemplateStatisticsResPo();
		objectRes.setData(dataByTemplate.get("OBJECT"));
		objectRes.setCriteriaType("OBJECT");
		CrTemplateStatisticsResPo parentObjectRes = new CrTemplateStatisticsResPo();
		parentObjectRes.setData(dataByTemplate.get("PARENT_OBJECT_CODE"));
		parentObjectRes.setCriteriaType("PARENT_OBJECT_CODE");

		templateStatistic.add(templateRes);
		templateStatistic.add(podRes);
		templateStatistic.add(projectRes);
		templateStatistic.add(objectRes);
		templateStatistic.add(parentObjectRes);

		return templateStatistic;
	}

	@Override
	public List<CrProcessRequestsView> getProcessRequests(CrProcessRequestsPagePo crProcessRequestsPagePo,
														  HttpHeaders httpHeaders) throws Exception {
		List<CrProcessRequestsView> processRequestLi = new ArrayList<>();
		try {

		Page<CrProcessRequestsView> page = null;
		Pageable pageable = PageRequest.of(crProcessRequestsPagePo.getPageNo(), crProcessRequestsPagePo.getPageSize(),
				Sort.by(crProcessRequestsPagePo.getSortDirection(), crProcessRequestsPagePo.getSortBy()));
		if (!Validations.isNullOrEmpty(crProcessRequestsPagePo.getBatchName()))
			page = crProcessRequestsViewRepository.findAllByBatchName(crProcessRequestsPagePo.getBatchName(),
					pageable);
		else
			page = crProcessRequestsViewRepository.findAll(pageable);

		httpHeaders.set("pagecount", String.valueOf(page.getTotalPages()));
		httpHeaders.set("totalcount", String.valueOf(page.getTotalElements()));

		if (page.hasContent())
			processRequestLi = page.getContent();

		}catch (Exception e){
			e.printStackTrace();
		}
		return processRequestLi;
	}

	@Override
	public ProcessJobPo processJobV1(String cloudTemplateName, String type, String batchName,
									 HttpServletRequest request) throws Exception {
		//TODO
		//return processJobDaoImpl.processJobV1(cloudTemplateName, type, batchName, request);
		return new ProcessJobPo();
	}

	@Override
	public void downloadFbdi(Long cloudTemplateId, String batchName, HttpServletResponse response) throws Exception {
		String fbdiFileName = "";
		try {
			cloudTemplateHeaderDaoImpl.downloadFbdi(cloudTemplateId, batchName,response);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public ResponseEntity<Object> transformDataToCloud(String cloudTemplateName, String pReprocessFlag, String pBatchFlag, String pBatchName, HttpServletRequest request) {
		Map<String,String> resMap=new HashMap<>();
		try {
		String userId=request.getHeader("userId");
		StoredProcedureQuery createStaggingStoredProcedure = entityManager
				.createStoredProcedureQuery("CR_CLD_TRANSFORM_ASYNC_PROC")
				.registerStoredProcedureParameter("p_cloud_template_name", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_user_id", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_reprocess_flag", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_batch_flag", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_batch_name", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_request_id", String.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_ret_code", String.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_ret_msg", String.class, ParameterMode.OUT)

				.setParameter("p_cloud_template_name", cloudTemplateName)
				.setParameter("p_user_id",userId )
				.setParameter("p_reprocess_flag", pReprocessFlag)
				.setParameter("p_batch_flag",pBatchFlag )
				.setParameter("p_batch_name", pBatchName);

		createStaggingStoredProcedure.execute();

		resMap.put("requestId",(String) createStaggingStoredProcedure.getOutputParameterValue("p_request_id"));
		resMap.put("responseCode",(String) createStaggingStoredProcedure.getOutputParameterValue("p_ret_code"));
		resMap.put("responseMessage",(String) createStaggingStoredProcedure.getOutputParameterValue("p_ret_msg"));

		entityManager.clear();
		entityManager.close();

		}catch (Exception e){
			e.printStackTrace();
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(resMap, HttpStatus.OK);
	}

	@Override
	public void generateHdlFromLob(Long cloudTemplateId, String batchName, HttpServletResponse response)
			throws Exception {
		log.info("Start Of generateHdlFromLob in service###");
		// List<XxrCloudTemplateHeader> cloudTemplateHeaderList = new ArrayList<>();
		Long parentObjectId = null;
		String parentObjectCode = "";
		try {
			response.setContentType("text/dat");
			CrCloudTemplateHeaders xxrCloudTemplateHeader = crCloudTemplateHeadersRepository
					.findById(cloudTemplateId).get();
			if (xxrCloudTemplateHeader != null)
				parentObjectId = xxrCloudTemplateHeader.getParentObjectId();
			if (parentObjectId != null)
				parentObjectCode = crLookUpValuesRepo.getValueById(parentObjectId);
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + parentObjectCode + ".dat");
			cloudTemplateHeaderDaoImpl.generateHdlFromLob(cloudTemplateId, batchName, response.getWriter());

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}

