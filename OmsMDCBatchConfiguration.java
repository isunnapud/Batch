/**
 * ==========================================================================
 * Filename: OmsMdcBatchConfiguration.java
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
package com.ups.ops.oms.batch.mdc.configuration;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.StoredProcedureItemReader;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ups.ops.oms.batch.mdc.constants.OmsMDCBatchConstants;
import com.ups.ops.oms.batch.mdc.dto.OrderBillingData;
import com.ups.ops.oms.batch.mdc.listener.MDCStepExecutionListener;
import com.ups.ops.oms.batch.mdc.mapper.OrderBillingDataMapper;
import com.ups.ops.oms.batch.mdc.repository.OmsMDCBatchDBCount;
import com.ups.ops.oms.batch.mdc.writer.OrderJsonDataWriter;

/**
 * The Class OmsMdcBatchConfiguration.
 */
@Configuration
@EnableBatchProcessing
public class OmsMDCBatchConfiguration {
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(OmsMDCBatchConfiguration.class);

	/** The job builder factory. */
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	/** The step builder factory. */
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource datasource;

	@Value("${oms.db.getmdcdatastoredprocname}")
	private String storedProcItemReaderName;

	@Value("${oms.mdc.batch.chunksize}")
	private String chunkSize;
	
	@Value("${oms.mdc.filepath}")
	private String filePath;

	@Value("${oms.mdc.filenameprefix}")
	private String fileName;

	@Value("${oms.mdc.fileextension}")
	private String fileExtension;

	@Value("${oms.mdc.errorfilenameprefix}")
	private String errorFileName;
	
	@Value("${oms.mdc.ftpserveraddress}")
	private String ftpServerAddress;

	@Value("${oms.mdc.ftpserverusername}")
	private String ftpUserName;

	@Value("${oms.mdc.ftpserverpassword}")
	private String ftpPassword;

	@Value("${oms.mdc.ftpremotedir}")
	private String ftpRemoteDir;
	
	@Value("${oms.mdc.enabled}")
	private boolean isMDCEnabled;
	
	@Value("${oms.mdc.ftpscript.path}")
	private String ftpScriptPath;
	
	@Value("${oms.mdc.ftp.enabled}")
	private boolean isMDCFtpEnabled;
	
	@Value("${oms.mdc.bkpscript.path}")
	private String bkpScriptCompletePath;
	@Value("${oms.mdc.log.path}")
	private String logPath;
	
	
	
	
    /**
     * Transaction manager.
     *
     * @return the resourceless transaction manager
     */
    @Bean
    public ResourcelessTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    /**
     * Map job repository factory.
     *
     * @param txManager the tx manager
     * @return the map job repository factory bean
     * @throws Exception the exception
     */
    @Bean
    public MapJobRepositoryFactoryBean mapJobRepositoryFactory(
            ResourcelessTransactionManager txManager) throws Exception {
        
    	MapJobRepositoryFactoryBean factory = new 
    			MapJobRepositoryFactoryBean(txManager);
        
        factory.afterPropertiesSet();
        
        return factory;
    }

    /**
     * Job repository.
     *
     * @param factory the factory
     * @return the job repository
     * @throws Exception the exception
     */
    @Bean
    public JobRepository jobRepository(
            MapJobRepositoryFactoryBean factory) throws Exception {
        return factory.getObject();
    }

    /**
     * Job launcher.
     *
     * @param jobRepository the job repository
     * @return the simple job launcher
     */
    @Bean
    public SimpleJobLauncher jobLauncher(JobRepository jobRepository) {
        SimpleJobLauncher launcher = new SimpleJobLauncher();
        launcher.setJobRepository(jobRepository);
        return launcher;
    }

	/**
	 * Order json data writer.
	 *
	 * @return the item writer
	 */
	@Bean
	ItemWriter<OrderBillingData> orderJsonDataWriter() {
		return new OrderJsonDataWriter();
	}

	/**
	 * MDC data processing job.
	 *
	 * @return the job
	 */
	@Bean
	public Job mdcDataProcessingJob() {
		log.info("INSIDE JOB BEAN");
		return jobBuilderFactory.get(OmsMDCBatchConstants.MDC_DATA_PROCESSING_JOB).start(mdcDataProcessingStep()).build();
	}

	/**
	 * MDC data processing step.
	 *
	 * @return the step
	 */
	@Bean
	public Step mdcDataProcessingStep() {
		log.info("INSIDE STEP BEAN");

		OrderJsonDataWriter writer = (OrderJsonDataWriter)orderJsonDataWriter();

		MDCStepExecutionListener listener = new MDCStepExecutionListener(filePath, fileName, fileExtension, errorFileName, writer, ftpServerAddress, ftpUserName, ftpPassword, ftpRemoteDir, isMDCEnabled, ftpScriptPath, isMDCFtpEnabled,bkpScriptCompletePath,logPath); 

		return stepBuilderFactory.get(OmsMDCBatchConstants.MDC_DATA_PROCESSING_JOB)
				.listener(listener).<OrderBillingData, OrderBillingData>chunk(Integer.parseInt(chunkSize))
				.reader(reader()).processor(new PassThroughItemProcessor<>()).writer(writer)
				.build();
		
	}
	
	@Bean
	public StoredProcedureItemReader reader() {
		Step mdcDataProcessingStep;
		StoredProcedureItemReader itemReader = new StoredProcedureItemReader();
		itemReader.setDataSource(datasource);
		itemReader.setProcedureName(storedProcItemReaderName);
		itemReader.setRowMapper(new OrderBillingDataMapper());
		return itemReader;
	}
}
