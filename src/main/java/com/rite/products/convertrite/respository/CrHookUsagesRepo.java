package com.rite.products.convertrite.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rite.products.convertrite.model.CrHookUsages;

public interface CrHookUsagesRepo extends JpaRepository<CrHookUsages, Long>{

	@Query("SELECT C From CrHookUsages C Where C.templateId = :templateId")
	public List<CrHookUsages> getHooksByTemplateId(Long templateId);
}
