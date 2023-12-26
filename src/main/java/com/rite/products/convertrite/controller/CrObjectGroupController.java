package com.rite.products.convertrite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rite.products.convertrite.po.CrObjectGroupHdrReqPo;
import com.rite.products.convertrite.po.CrObjectGroupLinesReqPo;
import com.rite.products.convertrite.service.CrObjectGroupHdrsService;
import com.rite.products.convertrite.service.CrObjectGroupLinesService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/convertritecore/")
public class CrObjectGroupController {
	
	@Autowired
	CrObjectGroupHdrsService crObjectGroupHdrsService;
	
	@Autowired
	CrObjectGroupLinesService crObjectGroupLinesService;

	@ApiOperation(value = "API to return all Object Groups Summary")
	@GetMapping("/getAllObjectGroupHdrs")
	public ResponseEntity<?> getAllObjectGroups() {
		try {
		return new ResponseEntity<>(crObjectGroupHdrsService.getAllObjectGroups(),HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<>(e.getCause(),HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@ApiOperation(value = "API to return all Object Groups Summary")
	@GetMapping("/getAllObjectGroupHdrsView")
	public ResponseEntity<?> getAllObjectGroupsView() {
		try {
		return new ResponseEntity<>(crObjectGroupHdrsService.getAllObjectGroupHdrsView(),HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<>(e.getCause(),HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@ApiOperation(value = "API to return Object Group By Id")
	@GetMapping("/getObjectGroupHdrById/{objectGroupId}")
	public ResponseEntity<?> getObjectGroupHdrById(@PathVariable Long objectGroupId) {
		try {
		return new ResponseEntity<>(crObjectGroupHdrsService.getObjectGroupHdrsById(objectGroupId),HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<>(e.getCause(),HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	
	
	@ApiOperation(value = "API to Save Object Group Summary")
	@PostMapping("/saveObjectGroupHdr")
	public ResponseEntity<?> saveObjectGroupHdr(@RequestBody CrObjectGroupHdrReqPo crObjectGroupHdrReqPo) {
		try {
		return new ResponseEntity<>(crObjectGroupHdrsService.saveObjectGroupHdr(crObjectGroupHdrReqPo),HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<>(e.getCause(),HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	
	@ApiOperation(value = "Delete Object Group with Lines")
	@DeleteMapping("/deleteObjectGroup/{objectGroupId}")
	public ResponseEntity<?> deleteObjectGroup(@PathVariable Long objectGroupId){
		try {
			crObjectGroupHdrsService.deleteObjectGroup(objectGroupId);
			crObjectGroupLinesService.deleteAllLinesByGroupId(objectGroupId);
			return new ResponseEntity<>("Success",HttpStatus.OK);
			}catch(Exception e) {
				return new ResponseEntity<>(e.getCause(),HttpStatus.EXPECTATION_FAILED);
			}
	}
	
	@ApiOperation(value = "Get All Lines in Object Group")
	@GetMapping("/getAllLinesInObjectGroup/{objectGroupId}")
	public ResponseEntity<?> getAllLinesInObjectGroup(@PathVariable Long objectGroupId){
		try {
		return new ResponseEntity<>(crObjectGroupLinesService.getAllLinesViewByGroupId(objectGroupId),HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getCause(),HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	
	@ApiOperation(value = "API to Save Object Group Lines")
	@PostMapping("/saveObjectGroupLines/{objectGroupId}")
	public ResponseEntity<?> saveObjectGroupLines(@RequestBody List<CrObjectGroupLinesReqPo> lineList, @PathVariable Long objectGroupId) {
		try {
		return new ResponseEntity<>(crObjectGroupLinesService.saveAllObjectGrpLinesByGroupId(lineList,objectGroupId),HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<>(e.getCause(),HttpStatus.EXPECTATION_FAILED);
		}
	}
	
}
