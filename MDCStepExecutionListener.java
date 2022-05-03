/**
 * ==========================================================================
 * Filename: MDCStepExecutionListener.java
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
package com.ups.ops.oms.batch.mdc.listener;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.ups.ops.oms.batch.mdc.constants.OmsMDCBatchConstants;
import com.ups.ops.oms.batch.mdc.repository.OmsMDCBatchDBCount;
import com.ups.ops.oms.batch.mdc.utilities.OmsFileWriterUtil;
import com.ups.ops.oms.batch.mdc.utilities.SftpFileTransferUtil;
import com.ups.ops.oms.batch.mdc.writer.OrderJsonDataWriter;

public class MDCStepExecutionListener implements StepExecutionListener {

	private static Logger log = LoggerFactory.getLogger(MDCStepExecutionListener.class);

	private OrderJsonDataWriter writer;

	private String filePath;

	private String fileName;

	private String extension;

	private String errorFileName;

	private String ftpServerAddress;

	private String ftpUserName;

	private String ftpPassword;

	private String remoteAddress;

	private boolean isMDCEnabled;
	private String mdcFtpScriptPath;
	private boolean isMDCFtpEnabled;
	private String bkpScriptCompletePath;
	private String logPath;
	int dbRecordCount=0;
	public MDCStepExecutionListener(String filePath, String fileName, String extension, String errorFileName,
			OrderJsonDataWriter writer, String ftpServerAddress, String ftpUserName, String ftpPassword,
			String remoteAddress, boolean isMDCEnabled, String mdcFtpScriptPath, boolean isMDCFtpEnabled,
			String bkpScriptCompletePath,String logPath) {

		this.filePath = filePath;
		this.fileName = fileName;
		this.extension = extension;
		this.errorFileName = errorFileName;
		this.writer = writer;
		this.ftpServerAddress = ftpServerAddress;
		this.ftpUserName = ftpUserName;
		this.ftpPassword = ftpPassword;
		this.remoteAddress = remoteAddress;
		this.isMDCEnabled = isMDCEnabled;
		this.mdcFtpScriptPath = mdcFtpScriptPath;
		this.isMDCFtpEnabled = isMDCFtpEnabled;
		this.bkpScriptCompletePath = bkpScriptCompletePath;
		this.logPath=logPath;
	}

	@Override
	public void beforeStep(StepExecution exec) {

		log.info("Free memory during beforeStep: {}", Runtime.getRuntime().freeMemory());
		log.info("Max memory during beforeStep: {}", Runtime.getRuntime().maxMemory());
		log.info("Total memory during beforeStep: {}", Runtime.getRuntime().totalMemory());
		// log.info(");exec.getJobParameters().getParameters().
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date());
		String completeFileName = filePath + OmsMDCBatchConstants.PATH_SEPARATOR + fileName
				+ OmsMDCBatchConstants.UNDERSCORE + date + OmsMDCBatchConstants.DOT + extension;
		File file = new File(completeFileName);
		log.info("Checking if file {} already exists........ before starting the step", completeFileName);
		if (file.exists()) {
			file.delete();
			log.info("deleted the file");
		}
		String completeErrorFileName = filePath + OmsMDCBatchConstants.PATH_SEPARATOR + this.errorFileName
				+ OmsMDCBatchConstants.UNDERSCORE + date + OmsMDCBatchConstants.DOT + extension;
		File errorFile = new File(completeErrorFileName);
		log.info("Checking if error file {} already exists........ before starting the step", completeErrorFileName);
		if (errorFile.exists()) {
			errorFile.delete();
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -3);
		date = format.format(cal.getTime());
		String prevDay3completeFileName = filePath + OmsMDCBatchConstants.PATH_SEPARATOR + fileName
				+ OmsMDCBatchConstants.UNDERSCORE + date + OmsMDCBatchConstants.DOT + extension;
		File prevDay3completeFile = new File(prevDay3completeFileName);
		log.info("Checking if file {} created 3 days before exists........ before starting the step",
				prevDay3completeFileName);
		if (prevDay3completeFile.exists()) {
			prevDay3completeFile.delete();
			log.info("deleted historical file");
		}
		String prevDay3completeErrorFileName = filePath + OmsMDCBatchConstants.PATH_SEPARATOR + errorFileName
				+ OmsMDCBatchConstants.UNDERSCORE + date + OmsMDCBatchConstants.DOT + extension;
		File prevDay3completeErrorFile = new File(prevDay3completeErrorFileName);
		log.info("Checking if error file {} created 3 days before exists........ before starting the step",
				prevDay3completeErrorFileName);
		if (prevDay3completeErrorFile.exists()) {
			prevDay3completeErrorFile.delete();
			log.info("deleted historical error file");
		}
		writer.setRecordCount(0);
		writer.setErrorRecordCount(0);
		writer.setProcessedRecords(0);
		writer.setPreviousOrderNumber(null);
		OmsMDCBatchConstants.FILE_RETRY_COUNT=1;
		OmsMDCBatchConstants.SCRIPT_RETRY_COUNT=1;
		
		/*
		 * try { SftpFileTransferUtil.triggerScript(ftpServerAddress, ftpUserName,
		 * ftpPassword, bkpScriptCompletePath); } catch(IOException e) {
		 * e.printStackTrace(); }
		 */
	}

	@Override
	public ExitStatus afterStep(StepExecution exec) {

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date());
		String completeFileName = filePath + OmsMDCBatchConstants.PATH_SEPARATOR + fileName
				+ OmsMDCBatchConstants.UNDERSCORE + date + OmsMDCBatchConstants.DOT + extension;
		File file = new File(completeFileName);
		log.info("Checking if file {} already exists after completing the step", completeFileName);
		// if(file.exists()) {
		RandomAccessFile mdcFile = null;
		try {
			log.info("File exists.....generating trailer");
			log.info("Total Number of records in file {}", writer.getRecordCount());
			mdcFile = new RandomAccessFile(completeFileName, "rw");
			mdcFile.seek(mdcFile.length());
			mdcFile.write(OmsFileWriterUtil.getBytesForText("T"));
			mdcFile.write(OmsFileWriterUtil.getBytesForText(String.format("%010d", writer.getRecordCount())));
			mdcFile.write(OmsFileWriterUtil.getBytesForText(OmsMDCBatchConstants.SPACE));
			mdcFile.write(OmsFileWriterUtil
					.getBytesForText(String.format("%1$-" + 10 + "s", getFormattedDate(new Date(), "MM/dd/yy"))));
			mdcFile.write(
					OmsFileWriterUtil.getBytesForText(String.format("%1$-" + 3378 + "s", OmsMDCBatchConstants.SPACE)));
			log.info("Trailer Written");
		     dbRecordCount = writer.getRepositoryCount().getRecordsCount();	   
		     log.info("Total processed Records {}",writer.getProcessedRecords());
			if (dbRecordCount != writer.getProcessedRecords()) {
				log.info("Processed records and Databse records  are not same");
				exec.setStatus(BatchStatus.FAILED);
				
				return exec.getExitStatus();				

			}
			
			if (isMDCEnabled) {
				boolean status = SftpFileTransferUtil.transferFileMethod(file, ftpServerAddress, ftpUserName,
						ftpPassword, remoteAddress, mdcFtpScriptPath, isMDCFtpEnabled,logPath);
				if (status) {			
					exec.setStatus(BatchStatus.COMPLETED);				

				} else {
					log.info("File :{} sending Retry completed to server still sftp not connected",file.getName());
					exec.setStatus(BatchStatus.STOPPED);
				}
			} else {
				log.info("OMS MDC Job Not enabled");
			}

		} catch (Exception ex) {
			log.info("Failed to complete the MDC Batch Process." + ex);
			exec.setStatus(BatchStatus.FAILED);
			return exec.getExitStatus();

		}

		finally {
			if (mdcFile != null) {
				try {
					mdcFile.close();
				} catch (IOException ex) {
					log.error("MDCBatchError while closing resources", ex);
					
				}
			}
		}
		// }
		String completeErrorFileName = filePath + OmsMDCBatchConstants.PATH_SEPARATOR + this.errorFileName
				+ OmsMDCBatchConstants.UNDERSCORE + date + OmsMDCBatchConstants.DOT + extension;
		File errorFile = new File(completeErrorFileName);
		log.info("completeErrorFileName {}", completeErrorFileName);

		if (errorFile.exists()) {
			if (writer.getErrorRecordCount() == 0) {
				errorFile.delete();
			} else {
				if (isMDCEnabled) {
					try {
						boolean status = SftpFileTransferUtil.transferFileMethod(errorFile, ftpServerAddress,
								ftpUserName, ftpPassword, remoteAddress, mdcFtpScriptPath, false,logPath);
						if (status) {
							exec.setStatus(BatchStatus.COMPLETED);

						} else {
							log.info("Error File :{} sending Retry completed to server still sftp not connected",file.getName());
							exec.setStatus(BatchStatus.STOPPED);
						}

					} catch (Exception ex) {
						log.error("MDCBatchError while sending to error file ",ex);
						
					}
				}
			}
		}
		log.info("Free memory during afterStep before gc: {}", Runtime.getRuntime().freeMemory());
		log.info("Max memory during afterStep before gc: {}", Runtime.getRuntime().maxMemory());
		log.info("Total memory during afterStep before gc: {}", Runtime.getRuntime().totalMemory());
		System.gc();
		log.info("Free memory during afterStep after gc: {}", Runtime.getRuntime().freeMemory());
		log.info("Max memory during afterStep after gc: {}", Runtime.getRuntime().maxMemory());
		log.info("Total memory during afterStep after gc: {}", Runtime.getRuntime().totalMemory());

		return exec.getExitStatus();
	}

	private String getFormattedDate(Date date, String format) {
		DateFormat dtFormat = new SimpleDateFormat(format);
		String formattedDate = dtFormat.format(date);
		return formattedDate;
	}

}
