package com.rite.products.convertrite.respository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rite.products.convertrite.model.CrLookUpSets;

public interface CrLookupSetsRepo extends JpaRepository<CrLookUpSets, Long> {

    @Query("select C From CrLookUpSets C where C.lookUpSetCode = :lookUpSetCode")
    CrLookUpSets getLookupSetByCode(String lookUpSetCode);
}
