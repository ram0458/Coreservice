package com.rite.products.convertrite.service;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.opencsv.CSVReader;
import com.rite.products.convertrite.Validations.Validations;
import com.rite.products.convertrite.po.LoadLookupResPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rite.products.convertrite.model.CrLookUpValues;
import com.rite.products.convertrite.po.CrLookupSetValueCreateReqPo;
import com.rite.products.convertrite.respository.CrLookUpValuesRepo;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CrLookUpValuesServiceImpl implements CrLookUpValuesService {

    @Autowired
    CrLookUpValuesRepo crLookUpValuesRepo;

    @Override
    public List<CrLookUpValues> getAllLookupSetValues(Long lookUpSetId) {
        return crLookUpValuesRepo.getLookupValuesByLookUpSetId(lookUpSetId);

    }

    @Override
    public Optional<CrLookUpValues> getLookupSetValueById(Long lookUpValueId) {

        return crLookUpValuesRepo.findById(lookUpValueId);
    }

    @Override
    public List<CrLookUpValues> saveAllLookupSet(ArrayList<CrLookupSetValueCreateReqPo> crLookupSetValueCreateReqPoList,
                                                 Long lookUpSetId) {

        List<CrLookUpValues> insertList = new ArrayList<CrLookUpValues>();
        List<CrLookUpValues> updateList = new ArrayList<CrLookUpValues>();
        List<Long> deleteList = new ArrayList<Long>();
        for (CrLookupSetValueCreateReqPo createReqPo : crLookupSetValueCreateReqPoList) {
            if (createReqPo.getInsertOrDelete().equalsIgnoreCase("D")) {
                deleteList.add(createReqPo.getLookUpValueId());
            } else {
                CrLookUpValues crLookUpValues = new CrLookUpValues();
                crLookUpValues.setLookUpSetId(createReqPo.getLookUpSetId());
                crLookUpValues.setLookUpValue(createReqPo.getLookUpValue());
                crLookUpValues.setActualValue(createReqPo.getActualValue());
                crLookUpValues.setAttribute1(createReqPo.getAttribute1());
                crLookUpValues.setAttribute2(createReqPo.getAttribute2());
                crLookUpValues.setAttribute3(createReqPo.getAttribute3());
                crLookUpValues.setAttribute4(createReqPo.getAttribute4());
                crLookUpValues.setAttribute5(createReqPo.getAttribute5());
                crLookUpValues.setEnabledFlag(createReqPo.getEnabledFlag());
                if (Objects.isNull(createReqPo.getLookUpValueId())) {
                    crLookUpValues.setCreatedBy("Somu");
                    crLookUpValues.setCreationDate(new Date());
                    insertList.add(crLookUpValues);
                } else {
                    crLookUpValues.setLastUpdateBy("Somu");
                    crLookUpValues.setLastUpdateDate(new Date());
                    crLookUpValues.setLookUpValueId(createReqPo.getLookUpValueId());
                    updateList.add(crLookUpValues);
                }
            }
        }

        if (deleteList.size() > 0) {
            try {
                crLookUpValuesRepo.deleteAllById(deleteList);
            } catch (Exception e) {
            }
        }
        if (insertList.size() > 0) {
            try {
                crLookUpValuesRepo.saveAll(insertList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (updateList.size() > 0) {
            try {
                crLookUpValuesRepo.saveAll(updateList);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return getAllLookupSetValues(lookUpSetId);
    }

    @Override
    public String deleteLookupSetValueId(Long lookUpSetValueId) {
        crLookUpValuesRepo.deleteById(lookUpSetValueId);
        return "Success";
    }

    @Override
    public String deleteAllbyLookUpSetId(Long lookupSetId) {
        crLookUpValuesRepo.deleteAllLookupValuesBySetId(lookupSetId);
        return "Success";
    }

    @Override
    public String SaveLookupValue(CrLookupSetValueCreateReqPo createReqPo) {
        CrLookUpValues crLookUpValues = new CrLookUpValues();

        if (Objects.isNull(createReqPo.getLookUpValueId())) {
            crLookUpValues.setCreatedBy("Somu");
            crLookUpValues.setCreationDate(new Date());
        } else {
            crLookUpValues.setLastUpdateBy("Somu");
            crLookUpValues.setLastUpdateDate(new Date());
        }
        crLookUpValues.setLookUpSetId(createReqPo.getLookUpSetId());
        crLookUpValues.setLookUpValue(createReqPo.getLookUpValue());
        crLookUpValues.setActualValue(createReqPo.getActualValue());
        crLookUpValues.setAttribute1(createReqPo.getAttribute1());
        crLookUpValues.setAttribute2(createReqPo.getAttribute2());
        crLookUpValues.setAttribute3(createReqPo.getAttribute3());
        crLookUpValues.setAttribute4(createReqPo.getAttribute4());
        crLookUpValues.setAttribute5(createReqPo.getAttribute5());
        crLookUpValues.setEnabledFlag(createReqPo.getEnabledFlag());
        crLookUpValuesRepo.save(crLookUpValues);
        return "Success";
    }


    @Override
    public LoadLookupResPo loadLookupFromFile(MultipartFile file, Long lookupSetId) throws Exception {
        LoadLookupResPo loadLookupResPo = new LoadLookupResPo();
        CSVReader reader = null;
        try {
            reader = new CSVReader(new InputStreamReader(file.getInputStream()));
            List<String[]> csvBody = reader.readAll();
            List<CrLookUpValues> crLookUpValuesList = new ArrayList<>();
            for (int i = 1; i < csvBody.size(); i++) {
                CrLookUpValues crLookUpValues = new CrLookUpValues();
                crLookUpValues.setLookUpSetId(lookupSetId);
                if (!Validations.isNullOrEmpty(csvBody.get(i)[0]))
                    crLookUpValues.setLookUpValue(csvBody.get(i)[0]);
                if (!Validations.isNullOrEmpty(csvBody.get(i)[1]))
                    crLookUpValues.setActualValue(csvBody.get(i)[1]);
                if (!Validations.isNullOrEmpty(csvBody.get(i)[2]))
                    crLookUpValues.setAttribute1(csvBody.get(i)[2]);
                if (!Validations.isNullOrEmpty(csvBody.get(i)[3]))
                    crLookUpValues.setAttribute2(csvBody.get(i)[3]);
                if (!Validations.isNullOrEmpty(csvBody.get(i)[4]))
                    crLookUpValues.setAttribute3(csvBody.get(i)[4]);
                if (!Validations.isNullOrEmpty(csvBody.get(i)[5]))
                    crLookUpValues.setAttribute4(csvBody.get(i)[5]);
                if (!Validations.isNullOrEmpty(csvBody.get(i)[6]))
                    crLookUpValues.setAttribute5(csvBody.get(i)[6]);
                crLookUpValues.setEnabledFlag("Y");
                crLookUpValues.setCreatedBy("ConvertRite");
                crLookUpValues.setCreationDate(new Date());
                crLookUpValues.setLastUpdateBy("ConvertRite");
                crLookUpValues.setLastUpdateDate(new Date());
                crLookUpValuesList.add(crLookUpValues);
            }
            crLookUpValuesRepo.deleteAllLookupValuesBySetId(lookupSetId);
            crLookUpValuesRepo.saveAll(crLookUpValuesList);
            int count = crLookUpValuesList.size();
            loadLookupResPo.setCount(count);
            loadLookupResPo.setMessage("Successfully Loaded Lookup's");
        } finally {
            if (reader != null)
                reader.close();
        }
        return loadLookupResPo;
    }

}
