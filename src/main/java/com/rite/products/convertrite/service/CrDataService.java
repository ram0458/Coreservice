package com.rite.products.convertrite.service;

import com.rite.products.convertrite.multitenancy.config.tenant.hibernate.DynamicDataSourceBasedMultiTenantConnectionProvider;
import com.rite.products.convertrite.respository.CrCloudTemplateHeadersViewRepository;
import com.rite.products.convertrite.respository.CrSourceTemplateHeadersViewRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CrDataService {
    @Autowired
    CrSourceTemplateHeadersViewRepo crSourceTemplateHeadersViewRepo;
    @Autowired
    DynamicDataSourceBasedMultiTenantConnectionProvider dynamicDataSourceBasedMultiTenantConnectionProvider;

    public List<String> getBatchNames(Long templateId, HttpServletRequest request) throws Exception {
        // TODO Auto-generated method stub
        log.info("Start of  getBatchNames in service ######");
        Connection con = null;
        PreparedStatement stmnt = null;
        List<String> li = new ArrayList<>();
        ResultSet rs = null;
        try {
            String srcStagingTableName = crSourceTemplateHeadersViewRepo.getSrcStagingTableName(templateId);
            log.info("srcStagingTableName-->" +srcStagingTableName);
            if (srcStagingTableName != null) {
                log.info("TENANT-->" + request.getHeader("X-TENANT-ID").toString());
                con=dynamicDataSourceBasedMultiTenantConnectionProvider.getConnection(request.getHeader("X-TENANT-ID").toString());

                stmnt = con.prepareStatement("select distinct cr_batch_name from " + srcStagingTableName);
                rs = stmnt.executeQuery();

                while (rs.next()) {
                    li.add(rs.getString("cr_batch_name"));
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } finally {
            if (rs != null)
                rs.close();
            if (con != null)
                con.close();
        }
        return li;
    }
}
