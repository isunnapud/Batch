/**
 * ==========================================================================
 * Filename: BatchTableOutput.java
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
package com.ups.ops.oms.batch.mdc.model;

import java.sql.Timestamp;

/**
 * The Class BatchTableOutput.
 */
public class BatchTableOutput {
	
	/** The batch name. */
	private String batchName;
	
	/** The comment text. */
	private String commentText;
	
	/** The status code. */
	private String statusCode;
	
	/** The processing job last run timestamp. */
	private Timestamp processingJobLastRunTimestamp;

	/**
	 * Gets the batch name.
	 *
	 * @return the batchName
	 */
	public String getBatchName() {
		return batchName;
	}

	/**
	 * Sets the batch name.
	 *
	 * @param batchName the batchName to set
	 */
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	/**
	 * Gets the comment text.
	 *
	 * @return the commentText
	 */
	public String getCommentText() {
		return commentText;
	}

	/**
	 * Sets the comment text.
	 *
	 * @param commentText the commentText to set
	 */
	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	/**
	 * Gets the status code.
	 *
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * Sets the status code.
	 *
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Gets the processing job last run timestamp.
	 *
	 * @return the processingJobLastRunTimestamp
	 */
	public Timestamp getProcessingJobLastRunTimestamp() {
		return processingJobLastRunTimestamp;
	}

	/**
	 * Sets the processing job last run timestamp.
	 *
	 * @param processingJobLastRunTimestamp the processingJobLastRunTimestamp to set
	 */
	public void setProcessingJobLastRunTimestamp(Timestamp processingJobLastRunTimestamp) {
		this.processingJobLastRunTimestamp = processingJobLastRunTimestamp;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "***Batch name: "+batchName+", Comment text: "+commentText+", Batch status code: "+
				statusCode+", Processing job last run timestamp: "+processingJobLastRunTimestamp;
	}
}
