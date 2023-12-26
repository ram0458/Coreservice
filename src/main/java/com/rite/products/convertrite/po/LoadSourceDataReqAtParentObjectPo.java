package com.rite.products.convertrite.po;

import java.util.List;

public class LoadSourceDataReqAtParentObjectPo {

	private List<Long> loadSrcTemplates;
	private Long parentObjectId;
	private String fileName;
	private String batchName;

	
	public List<Long> getLoadSrcTemplates() {
		return loadSrcTemplates;
	}

	public void setLoadSrcTemplates(List<Long> loadSrcTemplates) {
		this.loadSrcTemplates = loadSrcTemplates;
	}

	public Long getParentObjectId() {
		return parentObjectId;
	}

	public void setParentObjectId(Long parentObjectId) {
		this.parentObjectId = parentObjectId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

}
