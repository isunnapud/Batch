/**
 * ==========================================================================
 * Filename: OmsMDCBatchDBCount.java
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
import com.ups.ops.oms.batch.mdc.constants.OmsMDCBatchDatabaseConstants;

@Transactional
@Repository
public class OmsMDCBatchDBCount {

	/** The log. */
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	EntityManagerFactory entityManagerFactory;

	@Value("${oms.db.getmdccountstoredprocname}")
	private String storedProcDBCountName;


	public int getRecordsCount() {
		EntityManager entityManager = null;
		int count = 0;

		try {
			entityManager = entityManagerFactory.createEntityManager();
			log.info("entering stored proc name: {}", storedProcDBCountName);
			StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery(storedProcDBCountName);
			storedProcedure.registerStoredProcedureParameter(OmsMDCBatchDatabaseConstants.RECORDCOUNT, Integer.class,
					ParameterMode.OUT);
			storedProcedure.execute();
			count = (int) storedProcedure.getOutputParameterValue(OmsMDCBatchDatabaseConstants.RECORDCOUNT);
			log.info("Total Records in Database  is: {}", count);
		} catch (Exception ex) {
			log.error("Exception occured while communicating with Database {}", ex);
		}

		finally {
			closeResources(entityManager);
		}

		return count;
	}

	private void closeResources(EntityManager entityManager) {
		if (null != entityManager)
			entityManager.close();
	}

}