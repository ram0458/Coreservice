package com.rite.products.convertrite.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;

import com.rite.products.convertrite.exception.ValidationException;
import com.rite.products.convertrite.model.XxrCloudDataProcess;
import com.rite.products.convertrite.po.DataSetStausResPo;
import com.rite.products.convertrite.po.HcmDetailsPageReqPo;
import com.rite.products.convertrite.po.HcmLoadAndImportDataRes;
import com.rite.products.convertrite.po.HcmLoadandImportDataReqPo;
import com.rite.products.convertrite.po.XxrHcmDataLoaderResPo;

public interface HcmDataImportService {

	DataSetStausResPo getDataSetStatus(String contentId, String processId, Long cldTemplateId)
			throws ValidationException, Exception;

	HcmLoadAndImportDataRes hcmLoadAndImportData(HcmLoadandImportDataReqPo hcmLoadandImportDataReqPo)
			throws ValidationException, Exception;

	List<XxrHcmDataLoaderResPo> getHcmDataLoaderDetails(HcmDetailsPageReqPo hcmDetailsPageReqPo,HttpHeaders httpHeaders) throws Exception;

	XxrCloudDataProcess processHdlReconcile(String contentId, Long cldTemplateId, HttpServletRequest request)
			throws ValidationException, Exception;

	XxrCloudDataProcess processHdlSummary(String contentId,Long podId,Long projectId, HttpServletRequest request) throws Exception;

	XxrCloudDataProcess processHdlStatus(String contentId,Long podId,Long projectId, HttpServletRequest request) throws Exception;
}
