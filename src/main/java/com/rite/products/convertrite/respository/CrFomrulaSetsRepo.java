package com.rite.products.convertrite.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rite.products.convertrite.model.CrFormulaSets;

public interface CrFomrulaSetsRepo extends JpaRepository<CrFormulaSets, Long>{
	
	@Query("Select C From CrFormulaSets C where C.formulaSetCode = :formulaSetCode ")
	public CrFormulaSets getFormulaByCode(String formulaSetCode);

}
