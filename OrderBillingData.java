/**
 * ==========================================================================
 * Filename: OrderData.java
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
package com.ups.ops.oms.batch.mdc.dto;

import java.sql.Timestamp;

public class OrderBillingData {
	
	private String organisationNumber;
	
	private String allJsonData;
	
	private OrderData convertedOrder; 
	
	private Long orderSystemNumber;

	private String orderUpdateCode;
	
	private Timestamp orderUpdateTimestamp;
	
	private Timestamp recordInsertTimestamp;
	
	private String orderStatusRecordDocumentText;
	
	private Timestamp orderUpdateMinTimestamp;
	
	private String dispatchFacilitySystemNumber;
	
	private Timestamp recordInsertMinTimestamp;
	
	private Timestamp AcknowledgementTimestamp;
	
	private String acknowledgementTypeCode;

	public String getOrganisationNumber() {
		return organisationNumber;
	}

	public void setOrganisationNumber(String organisationNumber) {
		this.organisationNumber = organisationNumber;
	}

	public String getAllJsonData() {
		return allJsonData;
	}

	public void setAllJsonData(String allJsonData) {
		this.allJsonData = allJsonData;
	}
	
	public OrderData getConvertedOrder() {
		return convertedOrder;
	}

	public void setConvertedOrder(OrderData convertedOrder) {
		this.convertedOrder = convertedOrder;
	}

	public Long getOrderSystemNumber() {
		return orderSystemNumber;
	}

	public void setOrderSystemNumber(Long orderSystemNumber) {
		this.orderSystemNumber = orderSystemNumber;
	}

	public String getOrderUpdateCode() {
		return orderUpdateCode;
	}

	public void setOrderUpdateCode(String orderUpdateCode) {
		this.orderUpdateCode = orderUpdateCode;
	}

	public Timestamp getOrderUpdateTimestamp() {
		return orderUpdateTimestamp;
	}

	public void setOrderUpdateTimestamp(Timestamp orderUpdateTimestamp) {
		this.orderUpdateTimestamp = orderUpdateTimestamp;
	}

	public Timestamp getRecordInsertTimestamp() {
		return recordInsertTimestamp;
	}

	public void setRecordInsertTimestamp(Timestamp recordInsertTimestamp) {
		this.recordInsertTimestamp = recordInsertTimestamp;
	}

	public String getOrderStatusRecordDocumentText() {
		return orderStatusRecordDocumentText;
	}

	public void setOrderStatusRecordDocumentText(String orderStatusRecordDocumentText) {
		this.orderStatusRecordDocumentText = orderStatusRecordDocumentText;
	}

	public Timestamp getOrderUpdateMinTimestamp() {
		return orderUpdateMinTimestamp;
	}

	public void setOrderUpdateMinTimestamp(Timestamp orderUpdateMinTimestamp) {
		this.orderUpdateMinTimestamp = orderUpdateMinTimestamp;
	}

	public String getDispatchFacilitySystemNumber() {
		return dispatchFacilitySystemNumber;
	}

	public void setDispatchFacilitySystemNumber(String dispatchFacilitySystemNumber) {
		this.dispatchFacilitySystemNumber = dispatchFacilitySystemNumber;
	}

	public Timestamp getRecordInsertMinTimestamp() {
		return recordInsertMinTimestamp;
	}

	public void setRecordInsertMinTimestamp(Timestamp recordInsertMinTimestamp) {
		this.recordInsertMinTimestamp = recordInsertMinTimestamp;
	}

	public Timestamp getAcknowledgementTimestamp() {
		return AcknowledgementTimestamp;
	}

	public void setAcknowledgementTimestamp(Timestamp acknowledgementTimestamp) {
		AcknowledgementTimestamp = acknowledgementTimestamp;
	}

	public String getAcknowledgementTypeCode() {
		return acknowledgementTypeCode;
	}

	public void setAcknowledgementTypeCode(String acknowledgementTypeCode) {
		this.acknowledgementTypeCode = acknowledgementTypeCode;
	}
}
