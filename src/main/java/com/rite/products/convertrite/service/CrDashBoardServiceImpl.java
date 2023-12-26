package com.rite.products.convertrite.service;

import com.rite.products.convertrite.model.CrCldTransformStatsView;
import com.rite.products.convertrite.model.CrTemplateStatisticsView;
import com.rite.products.convertrite.respository.CrCldTransformStatsRepo;
import com.rite.products.convertrite.respository.CrTemplateStatisticsViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrDashBoardServiceImpl implements  CrDashBoardService{
@Autowired
CrTemplateStatisticsViewRepository crTemplateStatisticsViewRepo;
    @Autowired
    CrCldTransformStatsRepo crCldTransformStatsRepo;
    @Override
    public Object getTemplateStatistics() {
        List<CrTemplateStatisticsView> list=null;
        try {
     list=  crTemplateStatisticsViewRepo.findAll();

    } catch (Exception e) {
        return new ResponseEntity<>(e.getCause(), HttpStatus.EXPECTATION_FAILED);
    }
    return list;

    }

    @Override
    public Object getCrTransformStats() {
        List<CrCldTransformStatsView> list=null;
        try {
            list=  crCldTransformStatsRepo.findAll();

        } catch (Exception e) {
            return new ResponseEntity<>(e.getCause(), HttpStatus.EXPECTATION_FAILED);
        }
        return list;

    }
}
