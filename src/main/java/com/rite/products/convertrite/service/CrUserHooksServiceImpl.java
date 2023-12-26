package com.rite.products.convertrite.service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rite.products.convertrite.model.CrUserHooks;
import com.rite.products.convertrite.po.CrUserHooksCreateReqPo;
import com.rite.products.convertrite.respository.CrUserHooksRepo;

@Service
public class CrUserHooksServiceImpl implements CrUserHooksService {

	@Autowired
	CrUserHooksRepo crUserHooksRepo;

	@Override
	public List<CrUserHooks> getAllUserHooks() {
		return crUserHooksRepo.findAll();
	}

	@Override
	public Optional<CrUserHooks> getUserHookById(Long hookId) {
		return crUserHooksRepo.findById(hookId);
	}

	@Override
	public CrUserHooks getUserHookByCode(String hookCode) {

		return crUserHooksRepo.findByUserHookCode(hookCode);
	}

	@Override
	public String deleteUserHook(Long hookId) {

		crUserHooksRepo.deleteById(hookId);
		return "Success";
	}

	@Override
	public CrUserHooks saveUserHook(CrUserHooksCreateReqPo createReqPo) {
		CrUserHooks crUserHooks = new CrUserHooks();
		crUserHooks.setAttribute1(createReqPo.getAttribute1());
		crUserHooks.setAttribute2(createReqPo.getAttribute2());
		crUserHooks.setAttribute3(createReqPo.getAttribute3());
		crUserHooks.setAttribute4(createReqPo.getAttribute4());
		crUserHooks.setAttribute5(createReqPo.getAttribute5());
		crUserHooks.setDescription(createReqPo.getDescription());
		crUserHooks.setHookCode(createReqPo.getHookCode());
		crUserHooks.setHookName(createReqPo.getHookName());
		crUserHooks.setHookText(createReqPo.getHookText());
		crUserHooks.setHookType(createReqPo.getHookType());
		
		if (Objects.isNull(createReqPo.getHookId())) {
			crUserHooks.setCreatedBy("Somu");
			crUserHooks.setCreationDate(new Date());
		}else {
			crUserHooks.setLastUpdateBy("Somu");
			crUserHooks.setLastUpdateDate(new Date());
			crUserHooks.setHookId(createReqPo.getHookId());
		}
		crUserHooksRepo.save(crUserHooks);
		return crUserHooksRepo.findByUserHookCode(crUserHooks.getHookCode());
	}

}
