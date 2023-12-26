package com.rite.products.convertrite.service;

import com.rite.products.convertrite.model.*;
import com.rite.products.convertrite.po.CrTemplateStatisticsResPo;
import com.rite.products.convertrite.po.CrProcessRequestsPagePo;
import com.rite.products.convertrite.po.ProcessJobPo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public interface CrConversionService {

	List<CrTemplateStateView> getTemplateState() throws Exception;

	List<CrTemplateStatisticsResPo> getTemplateStatistics() throws Exception;

	List<CrProcessRequestsView> getProcessRequests(CrProcessRequestsPagePo crProcessRequestsPagePo, HttpHeaders httpHeaders) throws Exception;

	ProcessJobPo processJobV1(String cloudTemplateName, String type, String batchName, HttpServletRequest request)throws Exception;

	void downloadFbdi(Long cloudTemplateId,String batchName, HttpServletResponse response)  throws Exception;

	ResponseEntity<Object> transformDataToCloud(String cloudTemplateName, String pReprocessFlag, String pBatchFlag, String pBatchName, HttpServletRequest request);

	void generateHdlFromLob(Long cloudTemplateId, String batchName, HttpServletResponse response) throws Exception;
}
