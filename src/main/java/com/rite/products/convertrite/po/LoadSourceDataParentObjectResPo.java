package com.rite.products.convertrite.po;

public class LoadSourceDataParentObjectResPo {
	private Long srcTemplateId;
	private String srcTemplateName;
	private int loadedRecords;
	private long failedRecords;
	private String message;
	private String error;
	private String  stgTableName;

	
	public String getStgTableName() {
		return stgTableName;
	}

	public void setStgTableName(String stgTableName) {
		this.stgTableName = stgTableName;
	}

	public String getSrcTemplateName() {
		return srcTemplateName;
	}

	public void setSrcTemplateName(String srcTemplateName) {
		this.srcTemplateName = srcTemplateName;
	}

	public Long getSrcTemplateId() {
		return srcTemplateId;
	}

	public void setSrcTemplateId(Long srcTemplateId) {
		this.srcTemplateId = srcTemplateId;
	}

	public int getLoadedRecords() {
		return loadedRecords;
	}

	public void setLoadedRecords(int loadedRecords) {
		this.loadedRecords = loadedRecords;
	}

	public long getFailedRecords() {
		return failedRecords;
	}

	public void setFailedRecords(long failedRecords) {
		this.failedRecords = failedRecords;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
