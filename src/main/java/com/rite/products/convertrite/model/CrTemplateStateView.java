package com.rite.products.convertrite.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "CR_TEMPLATE_STATE_V")
public class CrTemplateStateView {

	@Column(name = "CLD_TEMPLATE_ID")
	private Long templateId;
	@Id
	@Column(name = "CLD_TEMPLATE_NAME")
	private String templateName;
	@Column(name = "VALIDATION")

	private String validation;
	@Column(name = "REPROCESS")
	private String reprocess;
	@Column(name = "CONVERSION")
	private String conversion;
	@Column(name = "FILE_GEN")
	private String filegen;

	@Column(name = "project_id")
	private String projectId;
	@Column(name = "project_name")
	private String projectName;
	@Column(name = "parent_object_id")
	private String parentObjectId;
	@Column(name = "parent_object_name")
	private String parentObjectName;
	@Column(name = "object_id")
	private String objectId;
	@Column(name = "object_name")
	private String objectName;
	@Column(name = "metadata_table_id")
	private String metadataTableId;
	@Column(name = "metadata_table_name")
	private String metadataTableName;
	@Column(name = "src_template_id")
	private String srcTemplateId;
	@Column(name = "source_template_name")
	private String sourceTemplateName;
	@Column(name = "staging_table_name")
	private String stagingTableName;
}
