/**
 * ==========================================================================
 * Filename: OmsMDCBatchRepository.java
 * =============================================================================
 * <p>
 * =============================================================================
 * <p>
 * NOTICE
 * Confidential, unpublished property of United Parcel Service.
 * Use and distribution limited solely to authorized personnel.
 * <p>
 * The use, disclosure, reproduction, modification, transfer, or
 * transmittal of this work for any purpose in any form or by
 * any means without the written permission of United Parcel
 * Service is strictly prohibited.
 * <p>
 * Copyright (c) 2016-2017, United Parcel Service of America, Inc.
 * All Rights Reserved.
 * <p>
 * =============================================================================
 * Author/Architect - OMS@ups.com
 * Date Create – 11-03-2017
 * =============================================================================
 */
package com.ups.ops.oms.batch.mdc.repository;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ups.ops.oms.batch.mdc.constants.OmsMDCBatchConstants;
import com.ups.ops.oms.batch.mdc.constants.OmsMDCBatchDatabaseConstants;

/**
 * <h1>OmsMDCBatchRepository</h1> 
* The OmsMDCBatchRepository acts as a repository
 
*
* @author  Subramanian C
* @version 1.0
*/
@Transactional
@Repository
public class OmsMDCBatchRepository {

	/** The log. */
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/** The entity manager. */
	//@PersistenceContext
	//private EntityManager entityManager;
	
	@Autowired
	EntityManagerFactory entityManagerFactory;
	

	/** The update order data stored proc name. */
	@Value("${oms.mdc.batch.updatelastruntimestamp.storedprocname}")
	private String updateOrderDataStoredProcName;

	/**
	 * Update last run timestamp.
	 ** @return true, if successful
	 * @throws SQLException the SQL exception
	 */
	public boolean updateLastRunTimestamp() throws SQLException {
		EntityManager entityManager =  null;
		boolean execStatus = false;
		try {
			entityManager =  entityManagerFactory.createEntityManager();
			log.info("entering stored proc name: {}", updateOrderDataStoredProcName);
			StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery(updateOrderDataStoredProcName);
			storedProcedure.registerStoredProcedureParameter(OmsMDCBatchDatabaseConstants.BATCH_NAME, String.class,
					ParameterMode.IN);
			storedProcedure.registerStoredProcedureParameter(OmsMDCBatchDatabaseConstants.COMMENT_TEXT, String.class,
					ParameterMode.IN);
			storedProcedure.registerStoredProcedureParameter(OmsMDCBatchDatabaseConstants.BATCH_STATUS_CODE,
					String.class, ParameterMode.IN);
			storedProcedure.registerStoredProcedureParameter(
					OmsMDCBatchDatabaseConstants.PROCESSING_JOB_LAST_RUN_TIMESTAMP, Timestamp.class, ParameterMode.IN);
			storedProcedure.registerStoredProcedureParameter(OmsMDCBatchDatabaseConstants.RESPONSE_CODE, String.class,
					ParameterMode.OUT);
			storedProcedure.registerStoredProcedureParameter(OmsMDCBatchDatabaseConstants.STATUS_MESSAGE, String.class,
					ParameterMode.OUT);
			storedProcedure.registerStoredProcedureParameter(OmsMDCBatchDatabaseConstants.NUMBER_OF_ROWS, Integer.class,
					ParameterMode.OUT);
			storedProcedure.registerStoredProcedureParameter(OmsMDCBatchDatabaseConstants.START_TIME, String.class,
					ParameterMode.OUT);
			storedProcedure.registerStoredProcedureParameter(OmsMDCBatchDatabaseConstants.END_TIME, String.class,
					ParameterMode.OUT);
			storedProcedure.registerStoredProcedureParameter(OmsMDCBatchDatabaseConstants.ELAPSED_TIME, Integer.class,
					ParameterMode.OUT);
	
			storedProcedure.setParameter(OmsMDCBatchDatabaseConstants.BATCH_NAME, OmsMDCBatchConstants.MDC_BATCH_NAME);
			storedProcedure.setParameter(OmsMDCBatchDatabaseConstants.COMMENT_TEXT, OmsMDCBatchConstants.SUCCESS_COMMENT_TEXT);
			storedProcedure.setParameter(OmsMDCBatchDatabaseConstants.BATCH_STATUS_CODE, OmsMDCBatchConstants.SUCCESS_STATUS_CODE);
	
			Date currentDateTime = new Date();
			Timestamp ts = new Timestamp(currentDateTime.getTime());
			storedProcedure.setParameter(OmsMDCBatchDatabaseConstants.PROCESSING_JOB_LAST_RUN_TIMESTAMP, ts);
	
			execStatus = storedProcedure.execute();
			
			log.info("updateLastRunTimestamp proc status result is: {}", execStatus);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		finally {
			closeResources(entityManager);
		}

		return execStatus;
	}
	
	private void closeResources(EntityManager entityManager)
	{
			if(null!=entityManager)
				entityManager.close();
	}
	
}