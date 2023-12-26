package com.rite.products.convertrite.service;

import com.rite.products.convertrite.model.CrCloudTemplateHeadersView;
import com.rite.products.convertrite.po.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CrCloudTemplateService {

	List<CrCloudTemplateHeadersView> getAllCloudTemplates() throws Exception;

	CrCloudTemplateHeaderResPo getCloudTemplateById(Long templateId) throws Exception;

	CrCloudTemplateHeaderResPo saveCloudTemplateHeaders(CrCloudTemplateHeaderReqPo crCloudTemplateHeaderCreateReqPo) throws Exception;

	void deleteCloudTemplateById(Long templateId) throws Exception;

	List<CrCloudTemplateColumnsResPo> getCloudTemplateColumns(Long templateId) throws Exception;

	CrCloudTemplateColumnsResPo getCloudTemplateByColumnId(Long columnId) throws Exception;

	CrCloudTemplateColumnsResPo saveCloudTemplateColumn(CrCloudTemplateColumnsReqPo crCloudTemplateColumnsReqPo) throws Exception;

	List<CrCloudTemplateColumnsResPo> saveAllCloudTemplateColumns(List<CrCloudTemplateColumnsReqPo> crCloudTemplateColumnsReqPo) throws Exception;

	CloudSourceColumnsPo getCloudSourceColumns(String sourceTemplateName, String cloudTableName) throws Exception;

	CloudTablesTemplatesPo getCloudTablesTemplates(long projectId, long objectId, long parentObjectId) throws Exception;

	CloudSourceColumnsPo getCloudSourceColumnsByIds(Long templateId, Long tableId) throws Exception;

	List<CloudMappingSetPo> getCloudMappingSetNames() throws Exception;
}
