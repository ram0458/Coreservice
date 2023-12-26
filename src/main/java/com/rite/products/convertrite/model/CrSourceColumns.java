package com.rite.products.convertrite.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="CR_SOURCE_COLUMNS")
public class CrSourceColumns {


        @Column(name = "TABLE_ID")
        private Long tableId;
        @Id
        @Column(name="COLUMN_ID")
        private Long columnId;

        @Column(name = "COLUMN_NAME")
        private String columnName;
    @Column(name="USER_COLUMN_NAME")
    private String userColumnName;

    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name="APPLICATION_ID")
    private Long applicationId;

    @Column(name = "COLUMN_SEQUENCE")
    private Long columnSequence;
    @Column(name="COLUMN_TYPE")
    private String columnType;

    @Column(name = "WIDTH")
    private Long width;
    @Column(name="NULL_ALLOWED_FLAG")
    private String nullAllowedFlag;

    @Column(name = "TRANSLATE_FLAG")
    private String translateFlag;
    @Column(name="FLEXFIELD_USAGE_CODE")
    private String flexifieldUsageCode;

    @Column(name = "FLEXFIELD_APPLICATION_ID")
    private String flexifieldApplicationId;
    @Column(name="FLEXFIELD_NAME")
    private String flexifieldName;

    @Column(name = "FLEX_VALUE_SET_APPLICATION_ID")
    private Long flexValueSetApplicationId;
    @Column(name="FLEX_VALUE_SET_ID")
    private Long flexValueSetId;

    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;
    @Column(name="PRECISION")
    private Long precision;

    @Column(name = "SCALE")
    private String scale;
    @Column(name="IREP_COMMENTS")
    private String irepComments;

    @Column(name = "ATTRIBUTE1")
    private String attribute1;
    @Column(name = "ATTRIBUTE2")
    private String attribute2;
    @Column(name = "ATTRIBUTE3")
    private String attribute3;
    @Column(name = "ATTRIBUTE4")
    private String attribute4;
    @Column(name = "ATTRIBUTE5")
    private String attribute5;

    @Column(name = "LAST_UPDATE_DATE")
    private Date lastUpdateDate;
    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;
    @Column(name = "CREATION_DATE")
    private Date creationDate;

    @Column(name = "CREATED_BY")
    private String createdBy;
}
