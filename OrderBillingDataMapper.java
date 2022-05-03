/**
 * ==========================================================================
 * Filename: OrderBillingDataMapper.java
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
package com.ups.ops.oms.batch.mdc.mapper;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ups.ops.oms.batch.mdc.constants.OmsMDCBatchDatabaseConstants;
import com.ups.ops.oms.batch.mdc.dto.OrderBillingData;
import com.ups.ops.oms.batch.mdc.dto.OrderData;

public class OrderBillingDataMapper implements RowMapper {
	
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		OrderBillingData data = new OrderBillingData();
		data.setOrganisationNumber(rs.getString(OmsMDCBatchDatabaseConstants.ORGANISATION_NUMBER_COLUMN));
		//data.setAllJsonData(rs.getString(ALLJSONDATA_COLUMN));
		data.setConvertedOrder(convertOrderJsonToJava(rs.getString(OmsMDCBatchDatabaseConstants.ALL_JSONDATA_COLUMN)));
		data.setOrderSystemNumber(rs.getLong(OmsMDCBatchDatabaseConstants.ORDER_SYSTEM_NUMBER_COLUMN));
		data.setOrderUpdateCode(rs.getString(OmsMDCBatchDatabaseConstants.ORDER_UPDATE_CODE_COLUMN));
		data.setOrderUpdateTimestamp(rs.getTimestamp(OmsMDCBatchDatabaseConstants.ORDER_UPDATE_TIMESTAMP_COLUMN));
		data.setRecordInsertTimestamp(rs.getTimestamp(OmsMDCBatchDatabaseConstants.RECORD_INSERT_TIMESTAMP_COLUMN));
		data.setOrderStatusRecordDocumentText(rs.getString(OmsMDCBatchDatabaseConstants.ORDER_STATUS_RECORD_DOCUMENT_TEXT_COLUMN));
		data.setOrderUpdateTimestamp(rs.getTimestamp(OmsMDCBatchDatabaseConstants.ORDER_UPDATE_MIN_TIMESTAMP_COLUMN));
		data.setDispatchFacilitySystemNumber(rs.getString(OmsMDCBatchDatabaseConstants.DISPATCH_FACILITY_SYSTEM_NUMBER_COLUMN));
		data.setRecordInsertMinTimestamp(rs.getTimestamp(OmsMDCBatchDatabaseConstants.RECORD_INSERT_MIN_TIMESTAMP_COLUMN));
		data.setAcknowledgementTimestamp(rs.getTimestamp(OmsMDCBatchDatabaseConstants.ACKNOWLEDGEMENT_TIMESTAMP_COLUMN));
		data.setAcknowledgementTypeCode(rs.getString(OmsMDCBatchDatabaseConstants.ACKNOWLEDGEMENT_TYPECODE_COLUMN));
		
		return data;
	}
	
	public OrderData convertOrderJsonToJava(String jsonString) {
		OrderData orderObj = null;
		ObjectMapper jacksonMapper = new ObjectMapper();
		//jacksonMapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		jacksonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		try {
			orderObj = jacksonMapper.readValue(jsonString, OrderData.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderObj;
	}
}
