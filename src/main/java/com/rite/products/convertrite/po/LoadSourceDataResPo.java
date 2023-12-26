package com.rite.products.convertrite.po;

public class LoadSourceDataResPo {

	private int loadedRecords;
	private long failedRecords;
	private String stgTableName;
	private String message;
	private String error;

	
	public String getStgTableName() {
		return stgTableName;
	}

	public void setStgTableName(String stgTableName) {
		this.stgTableName = stgTableName;
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
