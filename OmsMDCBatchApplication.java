/**
 * ==========================================================================
 * Filename: OmsMDCBatchApplication.java
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
package com.ups.ops.oms.batch.mdc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The Class OmsMDCBatchApplication - This class is the entry point for the application's boot up.
 * This is the main class which will run and start the whole application with specified configuration.
 * 
 */
@SpringBootApplication
@EnableScheduling
public class OmsMDCBatchApplication {

	/**
	 * The main method - where the application is initiated / batch flow begins.
	 *
	 * @param args - the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(OmsMDCBatchApplication.class, args);
	}
}
