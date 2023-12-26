package com.rite.products.convertrite.respository;

import com.rite.products.convertrite.model.CrCloudTemplateHeaders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrCloudTemplateHeadersRepository extends JpaRepository<CrCloudTemplateHeaders, Long> {
}
