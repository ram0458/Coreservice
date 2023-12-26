package com.rite.products.convertrite.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Data
@Entity
@Table(name = "CR_SOURCE_LOAD_FAIL_RECORDS")
public class CrSourceLoadFailRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name="ID")
    private Long id;
    @Column(name="TEMPLATE_ID")
    private Long templateId;
    @Column(name = "FILE_NAME")
    private String fileName;
    @Lob
    @Column(name = "FAILED_RECORDS")
    private String failedClob;
    @Column(name = "SUCCESS")
    private long success;
    @Column(name = "FAILED")
    private long failed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Kolkata")
    @Column(name = "CREATION_DATE")
    private Date creationDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Kolkata")
    @Column(name = "LAST_UPDATED_DATE")
    private Date lastUpdatedDate;
    @Column(name = "CREATED_BY")
    private String createdBy;
    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdateBy;
    @Lob
    @Column(name="LOG_FILE")
    private byte[] logFileBlob;
    @Column(name="CR_BATCH_NAME")
    private String xxrBatchName;
}
