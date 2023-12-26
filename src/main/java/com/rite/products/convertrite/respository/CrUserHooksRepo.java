package com.rite.products.convertrite.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rite.products.convertrite.model.CrUserHooks;

public interface CrUserHooksRepo extends JpaRepository<CrUserHooks, Long>{
	
	@Query("Select C From CrUserHooks C Where C.hookCode = :hookCode")
	public CrUserHooks findByUserHookCode(String hookCode);

}
