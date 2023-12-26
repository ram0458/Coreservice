package com.rite.products.convertrite.service;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.opencsv.CSVReader;
import com.rite.products.convertrite.Validations.Validations;
import com.rite.products.convertrite.po.LoadMappingValuesResPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rite.products.convertrite.model.CrMappingSetValues;
import com.rite.products.convertrite.po.CrMappingSetValuesCreateReqPo;
import com.rite.products.convertrite.respository.CrMappingSetValuesRepo;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CrMappingSetValuesServiceImpl implements CrMappingSetValuesService {

    @Autowired
    CrMappingSetValuesRepo crMappingSetValuesRepo;

    @Override
    public List<CrMappingSetValues> getValuesBySetId(int mappingSetId) {

        return crMappingSetValuesRepo.getValuesByMappingSetId(mappingSetId);
    }

    @Override
    public List<CrMappingSetValues> saveAllMappingValues(
            ArrayList<CrMappingSetValuesCreateReqPo> mappingValuesReqPoList, int mapsetHdrId) {

        List<CrMappingSetValues> insertList = new ArrayList<CrMappingSetValues>();
        List<CrMappingSetValues> updateList = new ArrayList<CrMappingSetValues>();
        List<Long> deleteList = new ArrayList<Long>();
        for (CrMappingSetValuesCreateReqPo crMappingSetValuesCreateReqPo : mappingValuesReqPoList) {
            if (crMappingSetValuesCreateReqPo.getInsertOrDelete().equalsIgnoreCase("D")) {
                deleteList.add((long) crMappingSetValuesCreateReqPo.mapLineId);
            } else {
                CrMappingSetValues crMappingSetValues = new CrMappingSetValues();

                crMappingSetValues.setMapSetId(crMappingSetValuesCreateReqPo.getMapSetId());
                crMappingSetValues.setSourceField1(crMappingSetValuesCreateReqPo.getSourceField1());
                crMappingSetValues.setSourceField2(crMappingSetValuesCreateReqPo.getSourceField2());
                crMappingSetValues.setSourceField3(crMappingSetValuesCreateReqPo.getSourceField3());
                crMappingSetValues.setTargetValue(crMappingSetValuesCreateReqPo.getTargetValue());
                crMappingSetValues.setAttribute1(crMappingSetValuesCreateReqPo.getAttribute1());
                crMappingSetValues.setAttribute2(crMappingSetValuesCreateReqPo.getAttribute2());
                crMappingSetValues.setAttribute3(crMappingSetValuesCreateReqPo.getAttribute3());
                crMappingSetValues.setAttribute4(crMappingSetValuesCreateReqPo.getAttribute4());
                crMappingSetValues.setAttribute5(crMappingSetValuesCreateReqPo.getAttribute5());
                crMappingSetValues.setEnabledFlag(crMappingSetValuesCreateReqPo.getEnabledFlag());

                if (Objects.isNull(crMappingSetValuesCreateReqPo.getMapLineId())
                        || crMappingSetValuesCreateReqPo.getMapLineId() == 0) {
                    crMappingSetValues.setCreationDate(new Date());
                    crMappingSetValues.setCreatedBy("Somu");
                    insertList.add(crMappingSetValues);
                } else {
                    crMappingSetValues.setMapLineId(crMappingSetValuesCreateReqPo.getMapLineId());
                    crMappingSetValues.setLastUpdateBy("Somu");
                    crMappingSetValues.setLastUpdateDate(new Date());
                    updateList.add(crMappingSetValues);
                }
            }
        }
        System.out.println(deleteList.size() + " --> " + insertList.size() + " --> " + updateList.size());
        if (deleteList.size() > 0) {
            try {
                crMappingSetValuesRepo.deleteAllById(deleteList);
            } catch (Exception e) {

            }
        }
        if (insertList.size() > 0) {
            try {
                crMappingSetValuesRepo.saveAll(insertList);
            } catch (Exception e) {

            }
        }
        if (updateList.size() > 0) {
            try {
                crMappingSetValuesRepo.saveAll(updateList);
            } catch (Exception e) {

            }

        }

        return this.getValuesBySetId(mapsetHdrId);
    }

    @Override
    public Optional<CrMappingSetValues> findByValueId(int mappingValueId) {

        return crMappingSetValuesRepo.findById((long) mappingValueId);
    }

    @Override
    public String deleteByValueId(int mappingValueId) {
        crMappingSetValuesRepo.deleteById((long) mappingValueId);
        return "Success";
    }

    @Override
    public String saveMappingSetValues(CrMappingSetValuesCreateReqPo crMappingSetValuesCreateReqPo) {
        CrMappingSetValues crMappingSetValues = new CrMappingSetValues();
        crMappingSetValues.setMapSetId(crMappingSetValuesCreateReqPo.getMapSetId());
        crMappingSetValues.setSourceField1(crMappingSetValuesCreateReqPo.getSourceField1());
        crMappingSetValues.setSourceField2(crMappingSetValuesCreateReqPo.getSourceField2());
        crMappingSetValues.setSourceField3(crMappingSetValuesCreateReqPo.getSourceField3());
        crMappingSetValues.setTargetValue(crMappingSetValuesCreateReqPo.getTargetValue());
        crMappingSetValues.setAttribute1(crMappingSetValuesCreateReqPo.getAttribute1());
        crMappingSetValues.setAttribute2(crMappingSetValuesCreateReqPo.getAttribute2());
        crMappingSetValues.setAttribute3(crMappingSetValuesCreateReqPo.getAttribute3());
        crMappingSetValues.setAttribute4(crMappingSetValuesCreateReqPo.getAttribute4());
        crMappingSetValues.setAttribute5(crMappingSetValuesCreateReqPo.getAttribute5());
        crMappingSetValues.setEnabledFlag(crMappingSetValuesCreateReqPo.getEnabledFlag());
        if (Objects.isNull(crMappingSetValuesCreateReqPo.getMapLineId())) {
            crMappingSetValues.setCreationDate(new Date());
            crMappingSetValues.setCreatedBy("Somu");
        } else {
            crMappingSetValues.setMapLineId(crMappingSetValuesCreateReqPo.getMapLineId());
            crMappingSetValues.setLastUpdateBy("Somu");
            crMappingSetValues.setLastUpdateDate(new Date());
        }

        crMappingSetValuesRepo.save(crMappingSetValues);

        return "Success";
    }

    @Override
    public String deleteAllMappingValuesBySetId(int mappingSetId) {
        crMappingSetValuesRepo.deleteAllMappingValuesBySetId(mappingSetId);
        return "Success";
    }

    @Override
    public LoadMappingValuesResPo loadMappingValues(MultipartFile file, int mappingSetId) throws Exception {
        LoadMappingValuesResPo loadMappingValuesResPo = new LoadMappingValuesResPo();
        CSVReader reader = null;
        List<CrMappingSetValues> mappingValuesLi = new ArrayList<>();
        try {
            reader = new CSVReader(new InputStreamReader(file.getInputStream()));
            List<String[]> csvBody = reader.readAll();
            for (int i = 1; i < csvBody.size(); i++) {
                CrMappingSetValues crMappingSetValues = new CrMappingSetValues();
                crMappingSetValues.setMapSetId(mappingSetId);
                if (!Validations.isNullOrEmpty(csvBody.get(i)[0]))
                    crMappingSetValues.setSourceField1(csvBody.get(i)[0]);
                if (!Validations.isNullOrEmpty(csvBody.get(i)[1]))
                    crMappingSetValues.setSourceField2(csvBody.get(i)[1]);
                if (!Validations.isNullOrEmpty(csvBody.get(i)[2]))
                    crMappingSetValues.setSourceField3(csvBody.get(i)[2]);
                if (!Validations.isNullOrEmpty(csvBody.get(i)[3]))
                    crMappingSetValues.setTargetValue(csvBody.get(i)[3]);
                crMappingSetValues.setEnabledFlag("Y");
                crMappingSetValues.setCreationDate(new Date());
                crMappingSetValues.setCreatedBy("ConvertRite");
                crMappingSetValues.setLastUpdateDate(new Date());
                crMappingSetValues.setLastUpdateBy("ConvertRite");
                mappingValuesLi.add(crMappingSetValues);
            }
            crMappingSetValuesRepo.deleteAllMappingValuesBySetId(mappingSetId);
            List<CrMappingSetValues> mappingValuesLiRes = crMappingSetValuesRepo.saveAll(mappingValuesLi);

            loadMappingValuesResPo.setCount(mappingValuesLiRes.size());
            loadMappingValuesResPo.setMessage("Successfully saved MappingsetValues");
        } finally {
            if (reader != null)
                reader.close();
        }

        return loadMappingValuesResPo;
    }

}
