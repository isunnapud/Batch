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

import com.ups.ops.oms.common.artifact.dto.OrderRecord;
import com.ups.ops.oms.common.artifact.dto.OrderStatusTextDBMaster;
import com.ups.ops.oms.common.artifact.dto.ServiceTypeRecordText;
import com.ups.ops.oms.common.artifact.dto.ShipmentControlRecordText;

public class OrderData {
	public OrderRecord orderRecordText;
	public ServiceTypeRecordText[] serviceTypeRecordText;
	public ShipmentControlRecordText[] shipmentControlRecordText;
	public OrderStatusTextDBMaster orderStatusText;

	public OrderRecord getOrderRecordText() {
		return orderRecordText;
	}
	public void setOrderRecordText(OrderRecord orderRecordText) {
		this.orderRecordText = orderRecordText;
	}
	public ServiceTypeRecordText[] getServiceTypeRecordText() {
		return serviceTypeRecordText;
	}
	public void setServiceTypeRecordText(ServiceTypeRecordText[] serviceTypeRecordText) {
		this.serviceTypeRecordText = serviceTypeRecordText;
	}
	public ShipmentControlRecordText[] getShipmentControlRecordText() {
		return shipmentControlRecordText;
	}
	public void setShipmentControlRecordText(ShipmentControlRecordText[] shipmentControlRecordText) {
		this.shipmentControlRecordText = shipmentControlRecordText;
	}
	public OrderStatusTextDBMaster getOrderStatusText() {
		return orderStatusText;
	}
	public void setOrderStatusText(OrderStatusTextDBMaster orderStatusText) {
		this.orderStatusText = orderStatusText;
	}
}
