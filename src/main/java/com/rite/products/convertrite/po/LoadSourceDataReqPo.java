package com.rite.products.convertrite.po;

public class LoadSourceDataReqPo {

	private Long metaDataTableId;
	private String srcStgTableName;
	private Long srcTemplateId;
	private String fileName;
	private String batchName;
	
	

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public Long getMetaDataTableId() {
		return metaDataTableId;
	}

	public void setMetaDataTableId(Long metaDataTableId) {
		this.metaDataTableId = metaDataTableId;
	}

	public String getSrcStgTableName() {
		return srcStgTableName;
	}

	public void setSrcStgTableName(String srcStgTableName) {
		this.srcStgTableName = srcStgTableName;
	}

	public Long getSrcTemplateId() {
		return srcTemplateId;
	}

	public void setSrcTemplateId(Long srcTemplateId) {
		this.srcTemplateId = srcTemplateId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
