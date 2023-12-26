package com.rite.products.convertrite.respository;

import com.rite.products.convertrite.model.CrCldTransformStatsView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrCldTransformStatsRepo extends JpaRepository<CrCldTransformStatsView,Long> {
}
