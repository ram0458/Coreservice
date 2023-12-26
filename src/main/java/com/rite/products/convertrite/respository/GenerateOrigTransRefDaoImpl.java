package com.rite.products.convertrite.respository;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class GenerateOrigTransRefDaoImpl {

	@PersistenceContext
	private EntityManager entityManager;
	
	private static final Logger log = LoggerFactory.getLogger(GenerateOrigTransRefDaoImpl.class);

	@Transactional
	public void generateOrigTranRef(long templateId, String tableName,HttpServletRequest request,String batchName) {
		log.info("Start of generateOrigTranRef Method in DaoImpl### ");
		StoredProcedureQuery generateOrigTransRef = entityManager
				.createStoredProcedureQuery("CR_POPULATE_ORIG_TRANS_ID_PROC")
				.registerStoredProcedureParameter("p_template_id", Long.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_table_name", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_user_id", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_batch_name",String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_ret_code", String.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_ret_msg",String.class, ParameterMode.OUT)

				.setParameter("p_table_name", tableName)
				.setParameter("p_template_id", templateId)
				.setParameter("p_user_id", request.getHeader("userId"))
				.setParameter("p_batch_name",batchName);
		generateOrigTransRef.execute();
		entityManager.clear();
		entityManager.close();	
	}

}

