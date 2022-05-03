package com.ups.ops.oms.batch.mdc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ups.ops.oms.batch.mdc.task.OmsMDCBatchJobScheduler;

@RestController
public class MdcController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	@RequestMapping(value = "/", produces = "text/plain")
	public String homePage() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException, InterruptedException {
		
		log.debug("Request received in default endpoint in controller");
		return "Welcome to OMS MDC Service. MDC Service is a batch Service";
	}
	
}
