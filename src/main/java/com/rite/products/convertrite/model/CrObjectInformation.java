package com.rite.products.convertrite.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrObjectInformation {
    private Long objInfoId;
    private Long objectId;
    private String info_type;
    private String info_value;
    private String info_description;
    private String additional_information1;
    private String additional_information2;
    private String additional_information3;
    private String additional_information4;
    private String additional_information5;
    private String creation_date;
    private String created_by;
    private String last_update_date;
    private String last_update_by;
}
