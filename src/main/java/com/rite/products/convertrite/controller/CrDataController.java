package com.rite.products.convertrite.controller;

import com.rite.products.convertrite.exception.ConvertRiteException;
import com.rite.products.convertrite.service.CrDataService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@RequestMapping("/api/convertritecore/crdata")
@RestController
public class CrDataController {
    @Autowired
    CrDataService crDataService;

    @ApiOperation(value = "This api is for fetching distinct batch names")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Response"),
            @ApiResponse(code = 500, message = "Server Side Error"),
            @ApiResponse(code = 400, message = "Bad Request") })
    @GetMapping("/getbatchnames")
    public ResponseEntity<List<String>> getBatchNames(@RequestParam("cldTemplateId") Long templateId, HttpServletRequest request) throws Exception {
        log.info("Start of getBatchNames in controller #####");
        List<String> batchNamesLi = new ArrayList<>();
        try {
            batchNamesLi =  crDataService.getBatchNames(templateId,request);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
        return new ResponseEntity<List<String>>(batchNamesLi, HttpStatus.OK);
    }
}
