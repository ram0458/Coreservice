package com.rite.products.convertrite.po;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TemplateStatisticsResPo {
	
	private Long criteriaId;
	private String criteriaName;
	@JsonIgnore
	private String criteriaType;
	private Integer success;
	private Integer failed;
	private Integer unverified;
	
	@JsonIgnore
	public String getCriteriaType() {
		return criteriaType;
	}
	@JsonIgnore
	public void setCriteriaType(String criteriaType) {
		this.criteriaType = criteriaType;
	}
	
	public Long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(Long criteriaId) {
		this.criteriaId = criteriaId;
	}
	public String getCriteriaName() {
		return criteriaName;
	}
	public void setCriteriaName(String criteriaName) {
		this.criteriaName = criteriaName;
	}
	public Integer getSuccess() {
		return success;
	}
	public void setSuccess(Integer success) {
		this.success = success;
	}
	public Integer getFailed() {
		return failed;
	}
	public void setFailed(Integer failed) {
		this.failed = failed;
	}
	public Integer getUnverified() {
		return unverified;
	}
	public void setUnverified(Integer unverified) {
		this.unverified = unverified;
	}
	

}
