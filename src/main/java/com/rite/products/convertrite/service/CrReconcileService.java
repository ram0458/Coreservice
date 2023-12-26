package com.rite.products.convertrite.service;

import com.rite.products.convertrite.respository.CloudTemplateHeaderDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;

@Service
public class CrReconcileService {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    CloudTemplateHeaderDaoImpl cloudTemplateHeaderDaoImpl;
    public Object downloadCloudImportSuccessRec(Long cloudTemplateId, String batchName, String userId, HttpServletResponse response) throws Exception {
        Object res=null;
        try {
            res = cloudTemplateHeaderDaoImpl.downloadCloudImportSuccessRec(cloudTemplateId, batchName,userId,response);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return res;
    }

    public Object downloadCloudImportRejRec(Long cloudTemplateId, String batchName, String userId, HttpServletResponse response) throws Exception {
        Object res=null;
        try {
            res = cloudTemplateHeaderDaoImpl.downloadCloudImportRejRec(cloudTemplateId, batchName,userId,response);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return res;
    }

    public Object downloadValFailRec(Long cloudTemplateId, String batchName, String userId, HttpServletResponse response) throws Exception {
        Object res=null;
        try {
            res = cloudTemplateHeaderDaoImpl.downloadValFailRec(cloudTemplateId, batchName,userId,response);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return res;
    }
}
