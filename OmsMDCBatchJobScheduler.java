/**
 * ==========================================================================
 * Filename: OmsMDCBatchJobScheduler.java
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
package com.ups.ops.oms.batch.mdc.task;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ups.ops.oms.batch.mdc.constants.OmsMDCBatchConstants;

/**
 * The Class OmsMDCBatchScheduler.
 */

@Component
public class OmsMDCBatchJobScheduler {

	private static Logger log = LoggerFactory.getLogger(OmsMDCBatchJobScheduler.class);
	
	/** The job launcher. */
	@Autowired
	SimpleJobLauncher jobLauncher;
	
	/** The MDC data processing job. */
	@Autowired
	Job mdcDataProcessingJob;

	
	
	/**
	 * Schedule OMS MDC job.
	 *
	 * @throws JobExecutionAlreadyRunningException the job execution already running exception
	 * @throws JobRestartException the job restart exception
	 * @throws JobInstanceAlreadyCompleteException the job instance already complete exception
	 * @throws JobParametersInvalidException the job parameters invalid exception
	 * @throws InterruptedException 
	 */
	@Scheduled(cron = "${oms.mdc.cron.frequency}", zone="America/New_York")
	public void scheduleOMSMdcJob() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException, InterruptedException {
		log.info("OMS MDC SERVICE: Job Started at: {}", new Date());		
		
		resetMDCParameters();
		
		JobExecution execution = runOMDCBatchProcess();
		
		while(OmsMDCBatchConstants.RETRY_COUNT <= OmsMDCBatchConstants.MAX_RETRY
				&& execution.getStatus().equals(BatchStatus.FAILED)  ) {
		
			log.info("MDC Batch Process Retry#" + OmsMDCBatchConstants.RETRY_COUNT);
			OmsMDCBatchConstants.RETRY_COUNT=OmsMDCBatchConstants.RETRY_COUNT+1;
			execution = runOMDCBatchProcess();		
			
		}
		
		log.info("OMS MDC SERVICE: Job finished with status: {}", execution.getStatus());
	}
	
	private JobExecution runOMDCBatchProcess() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		JobParameters param = new JobParametersBuilder().addString("JobID", String.valueOf(System.currentTimeMillis()))
				.toJobParameters();
		JobExecution execution = jobLauncher.run(mdcDataProcessingJob, param);
		return execution;
	}
	
	private void resetMDCParameters() {
		OmsMDCBatchConstants.MDC_RETRY_COMPLETE = Boolean.FALSE;
		OmsMDCBatchConstants.RETRY_COUNT = 1;		
	}
	

}