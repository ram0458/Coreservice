package com.rite.products.convertrite.respository;

import com.rite.products.convertrite.model.CrCloudTemplateHeadersView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CrCloudTemplateHeadersViewRepository extends JpaRepository<CrCloudTemplateHeadersView, Long> {


}
