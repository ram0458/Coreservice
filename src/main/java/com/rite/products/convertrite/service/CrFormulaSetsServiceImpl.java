package com.rite.products.convertrite.service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rite.products.convertrite.model.CrFormulaSets;
import com.rite.products.convertrite.po.CrFormulaSetsCreateReqPo;
import com.rite.products.convertrite.respository.CrFomrulaSetsRepo;

@Service
public class CrFormulaSetsServiceImpl implements CrFormulaSetsService {

	@Autowired
	CrFomrulaSetsRepo crFomrulaSetsRepo;

	@Override
	public List<CrFormulaSets> getAllFormulaSets() {

		return crFomrulaSetsRepo.findAll();
	}

	@Override
	public Optional<CrFormulaSets> getFormulaSetById(Long FormulaSetId) {

		return crFomrulaSetsRepo.findById(FormulaSetId);
	}

	@Override
	public CrFormulaSets saveFormulaSet(CrFormulaSetsCreateReqPo createReqPo) {

		CrFormulaSets crFormulaSets = new CrFormulaSets();

		if (Objects.isNull(createReqPo.getFormulaSetId())) {
			crFormulaSets.setCreatedBy("Somu");
			crFormulaSets.setCreationDate(new Date());
		} else {
			crFormulaSets.setLastUpdateBy("Somu");
			crFormulaSets.setLastUpdatedDate(new Date());
			crFormulaSets.setFormulaSetId(createReqPo.getFormulaSetId());
		}
		crFormulaSets.setAttribute1(createReqPo.getAttribute1());
		crFormulaSets.setAttribute2(createReqPo.getAttribute2());
		crFormulaSets.setAttribute3(createReqPo.getAttribute3());
		crFormulaSets.setAttribute4(createReqPo.getAttribute4());
		crFormulaSets.setAttribute5(createReqPo.getAttribute5());
		crFormulaSets.setDescription(createReqPo.getDescription());
		crFormulaSets.setFormulaSetCode(createReqPo.getFormulaSetCode());
		crFormulaSets.setFormulaSetName(createReqPo.getFormulaSetName());
		crFormulaSets.setFormulaText(createReqPo.getFormulaText());
		crFormulaSets.setFormulaType(createReqPo.getFormulaType());
		crFormulaSets.setCountOfParams(createReqPo.getCountOfParams());

		crFomrulaSetsRepo.save(crFormulaSets);
		return crFomrulaSetsRepo.getFormulaByCode(crFormulaSets.getFormulaSetCode());
	}

	@Override
	public String deleteFormulaSetById(Long formulaId) {

		crFomrulaSetsRepo.deleteById(formulaId);
		return "Success";
	}

	@Override
	public CrFormulaSets getFormulaByCode(String formulaCode) {

		return crFomrulaSetsRepo.getFormulaByCode(formulaCode);
	}

}
