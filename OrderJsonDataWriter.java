/**
 * ==========================================================================
 * Filename: OrderJsonDataWriter.java
 * =============================================================================
 * <p>
 * =============================================================================
 * <p>
 * NOTICE
 * Confidential, unpublished property of United Parcel Service.
 * Use and distribution limited solely to authorized personnel.
 * <p>
 * The use, disclosutoredre, reproduction, modification, transfer, or
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
package com.ups.ops.oms.batch.mdc.writer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.ups.ops.oms.batch.mdc.dto.OrderBillingData;
import com.ups.ops.oms.batch.mdc.repository.OmsMDCBatchDBCount;
import com.ups.ops.oms.batch.mdc.repository.OmsMDCBatchRepository;
import com.ups.ops.oms.batch.mdc.utilities.OmsFileWriterUtil;


/**
 * The Class OrderJsonDataWriter.
 */
public class OrderJsonDataWriter implements ItemWriter<OrderBillingData> {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(OrderJsonDataWriter.class);

	/** The repository. */
	@Autowired
	private OmsMDCBatchRepository repository;
	@Autowired
	private OmsMDCBatchDBCount repositoryCount;

	@Autowired
	private OmsFileWriterUtil omsFileWriterUtil;
	
	private int recordCount = 0;
	private int processedRecords=0;	
	private int errorRecordCount = 0;
	
	private String previousOrderNumber;
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends OrderBillingData> orderList) throws Exception  {
		List<OrderBillingData> orderListFinal = (List<OrderBillingData>) orderList;
	
		if(!orderListFinal.isEmpty()) {
			
			log.info("WRITER: size: {}", orderListFinal.size());
			omsFileWriterUtil.setPreviousOrderNumber(previousOrderNumber);
			
			int count = omsFileWriterUtil.writeContentsToFile(orderListFinal);
			int chunkSize=orderListFinal.size();
			processedRecords=processedRecords+chunkSize;
			recordCount = recordCount + count;
			previousOrderNumber = omsFileWriterUtil.getPreviousOrderNumber();
			errorRecordCount = errorRecordCount +  omsFileWriterUtil.getErrorRecordCount();
			
			
		}
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public OmsMDCBatchRepository getRepository() {
		return repository;
	}

	public OmsMDCBatchDBCount getRepositoryCount() {
		return repositoryCount;
	}

	public void setRepositoryCount(OmsMDCBatchDBCount repositoryCount) {
		this.repositoryCount = repositoryCount;
	}

	public void setRepository(OmsMDCBatchRepository repository) {
		this.repository = repository;
	}

	public int getErrorRecordCount() {
		return errorRecordCount;
	}

	public int getProcessedRecords() {
		return processedRecords;
	}

	
	public void setProcessedRecords(int processedRecords) {
		this.processedRecords = processedRecords;
	}

	public void setErrorRecordCount(int errorRecordCount) {
		this.errorRecordCount = errorRecordCount;
	}

	public String getPreviousOrderNumber() {
		return previousOrderNumber;
	}

	
	public void setPreviousOrderNumber(String previousOrderNumber) {
		this.previousOrderNumber = previousOrderNumber;
	}
	
	
}