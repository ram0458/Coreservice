package com.rite.products.convertrite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rite.products.convertrite.model.CrFormulaSets;
import com.rite.products.convertrite.po.CrFormulaSetsCreateReqPo;
import com.rite.products.convertrite.service.CrFormulaSetsService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/convertritecore")
public class CrFormulaSetsController {
	@Autowired
	CrFormulaSetsService crFormulaSetsService;

	@ApiOperation(value = "API to get all Formula sets")
	@GetMapping("/getAllFormulaSets")
	public ResponseEntity<?> getAllFormulaSets() {
		try {
			List<CrFormulaSets> formulaList = crFormulaSetsService.getAllFormulaSets();

			return new ResponseEntity<>(formulaList, HttpStatus.OK);
		} catch (Exception e) {

			return new ResponseEntity<>(e.getCause(), HttpStatus.EXPECTATION_FAILED);
		}
	}

	@ApiOperation(value = "API to get Formula sets by Id")
	@GetMapping("/getFormulaSetById/{formulaSetId}")
	public ResponseEntity<?> getFormulaSetById(@PathVariable Long formulaSetId) {
		try {

			return new ResponseEntity<>(crFormulaSetsService.getFormulaSetById(formulaSetId), HttpStatus.OK);
		} catch (Exception e) {

			return new ResponseEntity<>(e.getCause(), HttpStatus.EXPECTATION_FAILED);
		}
	}

	@ApiOperation(value = "API to Save/Update Formula Set")
	@PostMapping("/saveFormulaSet")
	public ResponseEntity<?> saveFormulaSet(@RequestBody CrFormulaSetsCreateReqPo createReqPo) {
		try {
			return new ResponseEntity<>(crFormulaSetsService.saveFormulaSet(createReqPo), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getCause(), HttpStatus.EXPECTATION_FAILED);
		}
	}

	@ApiOperation(value = "API to Delete Formula Set")
	@PostMapping("/deleteFormulaSet/{formulaSetId}")
	public ResponseEntity<?> saveFormulaSet(@PathVariable Long formulaSetId) {
		try {
			return new ResponseEntity<>(crFormulaSetsService.deleteFormulaSetById(formulaSetId), HttpStatus.OK);
		} catch (Exception e) {

			return new ResponseEntity<>(e.getCause(), HttpStatus.EXPECTATION_FAILED);
		}
	}
}
