package com.rite.products.convertrite.service;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rite.products.convertrite.model.CrObjectGroupLines;
import com.rite.products.convertrite.model.CrObjectGroupLinesView;
import com.rite.products.convertrite.po.CrObjectGroupLinesReqPo;
import com.rite.products.convertrite.respository.CrObjectGroupLinesRepo;

@Service
public class CrObjectGroupLinesServiceImpl implements CrObjectGroupLinesService {

	@Autowired
	CrObjectGroupLinesRepo crObjectGroupLinesRepo;

	@Override
	public List<CrObjectGroupLines> getAllByGroupId(Long groupId) {

		return crObjectGroupLinesRepo.getobjectLinesByGroupId(groupId);
	}

	@Override
	public List<CrObjectGroupLines> saveAllObjectGroupLinesByGroupId(List<CrObjectGroupLinesReqPo> lineList,
			Long groupId) {
		List<CrObjectGroupLines> insertList = new ArrayList<CrObjectGroupLines>();
		List<CrObjectGroupLines> updateList = new ArrayList<CrObjectGroupLines>();
		List<Long> deleteList = new ArrayList<Long>();

		for (CrObjectGroupLinesReqPo crObjectGroupLinesReqPo : lineList) {
			if (crObjectGroupLinesReqPo.getInsertOrDelete().equalsIgnoreCase("D")) {
				deleteList.add(crObjectGroupLinesReqPo.getObjGrpLineId());
			} else {
				CrObjectGroupLines crObjectGroupLines = new CrObjectGroupLines();
				crObjectGroupLines.setAttribute1(crObjectGroupLinesReqPo.getAttribute1());
				crObjectGroupLines.setAttribute2(crObjectGroupLinesReqPo.getAttribute2());
				crObjectGroupLines.setAttribute3(crObjectGroupLinesReqPo.getAttribute3());
				crObjectGroupLines.setAttribute4(crObjectGroupLinesReqPo.getAttribute4());
				crObjectGroupLines.setAttribute5(crObjectGroupLinesReqPo.getAttribute5());
				crObjectGroupLines.setGroupId(crObjectGroupLinesReqPo.getGroupId());
				crObjectGroupLines.setObjectId(crObjectGroupLinesReqPo.getObjectId());
				crObjectGroupLines.setSequence(crObjectGroupLinesReqPo.getSequence());
				if (Objects.isNull(crObjectGroupLinesReqPo.getObjGrpLineId())) {
					crObjectGroupLines.setCreatedBy("Somu");
					crObjectGroupLines.setCreationDate(new Date());
					insertList.add(crObjectGroupLines);
				} else {
					crObjectGroupLines.setLastUpdateBy("Somu");
					crObjectGroupLines.setLastUpdatedDate(new Date());
					crObjectGroupLines.setObjGrpLineId(crObjectGroupLinesReqPo.getObjGrpLineId());
					updateList.add(crObjectGroupLines);
				}
			}
		}
		if (deleteList.size() > 0) {
			try {
				crObjectGroupLinesRepo.deleteAllById(deleteList);
			} catch (Exception e) {
			}
		}
		if (insertList.size() > 0) {
			try {
				crObjectGroupLinesRepo.saveAll(insertList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (updateList.size() > 0) {
			try {
				crObjectGroupLinesRepo.saveAll(updateList);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return crObjectGroupLinesRepo.getobjectLinesByGroupId(groupId);
	}
	
	@Override
	public List<CrObjectGroupLinesView> saveAllObjectGrpLinesByGroupId(List<CrObjectGroupLinesReqPo> lineList,
			Long groupId) {
		List<CrObjectGroupLines> insertList = new ArrayList<CrObjectGroupLines>();
		List<CrObjectGroupLines> updateList = new ArrayList<CrObjectGroupLines>();
		List<Long> deleteList = new ArrayList<Long>();

		for (CrObjectGroupLinesReqPo crObjectGroupLinesReqPo : lineList) {
			if (crObjectGroupLinesReqPo.getInsertOrDelete().equalsIgnoreCase("D")) {
				deleteList.add(crObjectGroupLinesReqPo.getObjGrpLineId());
			} else {
				CrObjectGroupLines crObjectGroupLines = new CrObjectGroupLines();
				crObjectGroupLines.setAttribute1(crObjectGroupLinesReqPo.getAttribute1());
				crObjectGroupLines.setAttribute2(crObjectGroupLinesReqPo.getAttribute2());
				crObjectGroupLines.setAttribute3(crObjectGroupLinesReqPo.getAttribute3());
				crObjectGroupLines.setAttribute4(crObjectGroupLinesReqPo.getAttribute4());
				crObjectGroupLines.setAttribute5(crObjectGroupLinesReqPo.getAttribute5());
				crObjectGroupLines.setGroupId(crObjectGroupLinesReqPo.getGroupId());
				crObjectGroupLines.setObjectId(crObjectGroupLinesReqPo.getObjectId());
				crObjectGroupLines.setSequence(crObjectGroupLinesReqPo.getSequence());
				if (Objects.isNull(crObjectGroupLinesReqPo.getObjGrpLineId())) {
					crObjectGroupLines.setCreatedBy("Somu");
					crObjectGroupLines.setCreationDate(new Date());
					insertList.add(crObjectGroupLines);
				} else {
					crObjectGroupLines.setLastUpdateBy("Somu");
					crObjectGroupLines.setLastUpdatedDate(new Date());
					crObjectGroupLines.setObjGrpLineId(crObjectGroupLinesReqPo.getObjGrpLineId());
					updateList.add(crObjectGroupLines);
				}
			}
		}
		if (deleteList.size() > 0) {
			try {
				crObjectGroupLinesRepo.deleteAllById(deleteList);
			} catch (Exception e) {
			}
		}
		if (insertList.size() > 0) {
			try {
				crObjectGroupLinesRepo.saveAll(insertList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (updateList.size() > 0) {
			try {
				crObjectGroupLinesRepo.saveAll(updateList);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return crObjectGroupLinesRepo.getObjectLinesViewByGroupId(groupId);
	}

	@Override
	public String deleteAllLinesByGroupId(Long groupId) {
		crObjectGroupLinesRepo.deleteAllLinesByGroupId(groupId);

		return "Success";

	}

	@Override
	public List<CrObjectGroupLinesView> getAllLinesViewByGroupId(Long groupId) {
		
		return crObjectGroupLinesRepo.getObjectLinesViewByGroupId(groupId);
	}

}
