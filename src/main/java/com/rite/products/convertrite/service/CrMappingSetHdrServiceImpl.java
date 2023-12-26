package com.rite.products.convertrite.service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rite.products.convertrite.model.CrMappingSetHdr;
import com.rite.products.convertrite.po.CrMappingSetHdrCreateReqPo;
import com.rite.products.convertrite.respository.CrMappingSetHeaderRepo;

@Service
public class CrMappingSetHdrServiceImpl implements CrMappingSetHdrService{
	
	@Autowired
	CrMappingSetHeaderRepo crMappingSetHeaderRepo;

	@Override
	public List<CrMappingSetHdr> getAllMappingsets() {
		return crMappingSetHeaderRepo.findAll();
	}

	@Override
	public Optional<CrMappingSetHdr> findById(long mappingSetId) {
		
		return crMappingSetHeaderRepo.findById(mappingSetId);
	}

	@Override
	public CrMappingSetHdr saveMappingSetHdr(CrMappingSetHdrCreateReqPo reqPO) {
		
		CrMappingSetHdr crMappingSetHdr = new CrMappingSetHdr();
	    if (Objects.isNull(reqPO.getMapSetId())){
	    	crMappingSetHdr.setCreationDate(new Date());
		    crMappingSetHdr.setCreatedBy("Somu");
	    }else {
	    	crMappingSetHdr.setMapSetId(reqPO.getMapSetId());
	    	crMappingSetHdr.setLastUpdateBy("Somu");
		    crMappingSetHdr.setLastUpdateDate(new Date());
	    }
	    crMappingSetHdr.setAttribute1(reqPO.getAttribute1());
	    crMappingSetHdr.setAttribute2(reqPO.getAttribute2());
	    crMappingSetHdr.setAttribute3(reqPO.getAttribute3());
	    crMappingSetHdr.setAttribute4(reqPO.getAttribute4());
	    crMappingSetHdr.setAttribute5(reqPO.getAttribute5());
	    crMappingSetHdr.setMapSetCode(reqPO.getMapSetCode());
	    crMappingSetHdr.setLookupSetId(reqPO.getLookupSetId());
	    crMappingSetHdr.setValidationType(reqPO.getValidationType());
	    crMappingSetHdr.setMapSetType(reqPO.getMapSetType());
	    crMappingSetHdr.setMapSetName(reqPO.getMapSetName());
	    crMappingSetHdr.setSqlQuery(reqPO.getSqlQuery());
	
	    
		crMappingSetHeaderRepo.save(crMappingSetHdr);
		return this.findByMapCode(reqPO.mapSetCode);
	}

	@Override
	public String deleteById(long mapingSetId) {
		
		crMappingSetHeaderRepo.deleteById(mapingSetId);
		return "Success";
	}
	
	@Override
	public CrMappingSetHdr findByMapCode(String mappingSetCode) {
		return crMappingSetHeaderRepo.findByMapSetCode(mappingSetCode);
	}

}
