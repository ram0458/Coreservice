package com.rite.products.convertrite.respository;

import com.rite.products.convertrite.model.CrSourceLoadFailRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrSourceLoadFailRecordsRepo extends JpaRepository<CrSourceLoadFailRecords,Long> {
}
