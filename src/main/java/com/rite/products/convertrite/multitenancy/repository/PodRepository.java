package com.rite.products.convertrite.multitenancy.repository;

import com.rite.products.convertrite.multitenancy.model.Pod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PodRepository extends JpaRepository<Pod, Long> {

}
