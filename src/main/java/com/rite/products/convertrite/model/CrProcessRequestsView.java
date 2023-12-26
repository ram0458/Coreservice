package com.rite.products.convertrite.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Data
@Table(name = "CR_PROCESS_REQUESTS_V")
public class CrProcessRequestsView {

	@Id
	@Column(name = "REQUEST_ID")
	private Long requestId;
	@Column(name = "REQUEST_TYPE")
	private String requestType;
	@Column(name = "CLD_TEMPLATE_ID")
	private Long templateId;
	@Column(name = "CLD_TEMPLATE_NAME")
	private String templateName;
	@Column(name="CR_BATCH_NAME")
	private String batchName;
	@Column(name = "STATUS")
	private String status;
	@Column(name = "TOTAL_RECORDS")
	private Long totalRecords;
	@Column(name = "COMPLETED_PERCENTAGE")
	private Long percentage;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Kolkata")
	@Column(name = "START_DATE")
	private Date startDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Kolkata")
	@Column(name = "END_DATE")
	private Date endDate;
	@Column(name = "ERR_MSG")
	private String errorMsg;
	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "PARENT_OBJECT_CODE")
	private String parentObjectCode;
	@Column(name = "SUCCESS_REC")
	private Long successRec;
	@Column(name = "FAIL_REC")
	private Long failRec;
}
