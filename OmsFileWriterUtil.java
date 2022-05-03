/**
 * ==========================================================================
 * Filename: OmsFileWriterUtil.java
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
 * Date Create â€“ 11-03-2017
 * =============================================================================
 */
package com.ups.ops.oms.batch.mdc.utilities;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ups.ops.oms.batch.mdc.constants.OmsMDCBatchConstants;
import com.ups.ops.oms.batch.mdc.dto.OrderBillingData;
import com.ups.ops.oms.batch.mdc.mainframe.PackedDecimal;
import com.ups.ops.oms.batch.mdc.repository.OmsMDCBatchDBCount;
import com.ups.ops.oms.common.artifact.dto.OrderRecord;
import com.ups.ops.oms.common.artifact.dto.OrderStatusText;
import com.ups.ops.oms.common.artifact.dto.OrderStatusTextDBMaster;
import com.ups.ops.oms.common.artifact.dto.PackageRecordText;
import com.ups.ops.oms.common.artifact.dto.ShipmentControlRecordText;

/**
 * The Class OmsFileWriterUtil.
 */
@Service
public class OmsFileWriterUtil {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(OmsFileWriterUtil.class);

	/** The sftp file transfer util. */
	@Autowired
	SftpFileTransferUtil sftpFileTransferUtil;
	
	@Value("${oms.mdc.applicationprotocolversion}")
	private String applicationProtocolVersionNumber;

	@Value("${oms.mdc.filepath}")
	private String filePath;

	@Value("${oms.mdc.filenameprefix}")
	private String fileName;

	@Value("${oms.mdc.errorfilenameprefix}")
	private String errorFileName;

	@Value("${oms.mdc.fileextension}")
	private String fileExtension;
	
	private int errorRecordCount;
	
	private String previousOrderNumber;
	@Autowired
	OmsMDCBatchDBCount omsMDCBatchDBCount;
	
	public int writeContentsToFile(List<OrderBillingData> orderList) throws IOException, SQLException {
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date());
		String completeFileName = filePath + OmsMDCBatchConstants.PATH_SEPARATOR + fileName + OmsMDCBatchConstants.UNDERSCORE + date + OmsMDCBatchConstants.DOT + fileExtension; 
		
		String errorCompleteFileName = filePath + OmsMDCBatchConstants.PATH_SEPARATOR + errorFileName + OmsMDCBatchConstants.UNDERSCORE + date + OmsMDCBatchConstants.DOT + fileExtension; 
		int recordCount = 0;
		
		
		RandomAccessFile mdcFile = null;
		RandomAccessFile errorMdcFile = null;
		errorRecordCount = 0;
		try {
			mdcFile = new RandomAccessFile(completeFileName, "rw");
			mdcFile.seek(mdcFile.length());
			log.info("Starting Writing of a chunk");
			
			errorMdcFile = new RandomAccessFile(errorCompleteFileName, "rw");
			errorMdcFile.seek(errorMdcFile.length());
			
			for (OrderBillingData orderObj : orderList) {

				if(orderObj.getConvertedOrder() == null) {
					log.debug("converted order is null for orderSystemNumber: {}", orderObj.getOrderSystemNumber());
					continue;
				}
				OrderRecord orderRecord =  orderObj.getConvertedOrder().getOrderRecordText();
				if(orderRecord == null) {
					log.debug("orderRecord is null for orderSystemNumber: {}", orderObj.getOrderSystemNumber());
					continue;
				}
				
				if(orderObj.getAcknowledgementTypeCode() != null && OmsMDCBatchConstants.REJECTED_CODE.equals(orderObj.getAcknowledgementTypeCode().trim())) {
					log.info("Order is in Rejected State due to invalid SLIC {} ", orderRecord.getOrderNumber());
					continue;
				}
				
				if(previousOrderNumber != null && previousOrderNumber.equals(orderRecord.getOrderNumber())) {
					log.info("Duplicate Order in MDC {} ", orderRecord.getOrderNumber());
					continue;
				}
				
				previousOrderNumber = orderRecord.getOrderNumber();

				boolean errorRecord = false;
				String [] abc = null;

				ShipmentControlRecordText[] shpCtlRecArray = orderObj.getConvertedOrder().getShipmentControlRecordText();
				int shpCtlRecArrayLength = shpCtlRecArray.length;
				
				boolean orderRecordWritten = false;
				String firstShipmentRecordNumber="";
				
				/*
				if(orderRecord.getPickupPaymentCode() == null || "".equals(orderRecord.getPickupPaymentCode().trim())) {
					log.debug("PickupPayment code is null for orderSystemNumber: {}", orderObj.getOrderSystemNumber());
					continue;
				}
				*/
				
				/*
				if(orderRecord.getInformationSystemTypeCode() == null || "".equals(orderRecord.getInformationSystemTypeCode().trim()) || 
						orderRecord.getServiceRegionCode() == null || "".equals(orderRecord.getServiceRegionCode().trim()) ||
						orderRecord.getServiceDistrictCode() == null || "".equals(orderRecord.getServiceDistrictCode().trim()) ||
						orderRecord.getServiceCenterCountryCode() == null || "".equals(orderRecord.getServiceCenterCountryCode().trim()) ||
						orderRecord.getOrganizationNumber() == null || "".equals(orderRecord.getOrganizationNumber().trim()) ||
						orderRecord.getCustomerName() == null || "".equals(orderRecord.getCustomerName().trim()) ||
						orderRecord.getContactName() == null || "".equals(orderRecord.getContactName().trim()) ||
						orderRecord.getPoliticalDivision1Name() == null || "".equals(orderRecord.getPoliticalDivision1Name().trim()) ||
						orderRecord.getPoliticalDivision2Name() == null || "".equals(orderRecord.getPoliticalDivision2Name().trim()) ||
						orderRecord.getPostal1Code() == null || "".equals(orderRecord.getPostal1Code().trim()) ||
						orderRecord.getStreetAddressText() == null || "".equals(orderRecord.getStreetAddressText().trim()) ||
						orderRecord.getHighLevelServiceText() == null || "".equals(orderRecord.getHighLevelServiceText().trim()) ||
						orderRecord.getStopResidentialIndicator() == null || "".equals(orderRecord.getStopResidentialIndicator().trim()) ||
						orderRecord.getPickupTransactionTypeCode() == null || "".equals(orderRecord.getPickupTransactionTypeCode().trim()) ||
						orderRecord.getOrderPlacedTime() == null || "".equals(orderRecord.getOrderPlacedTime().trim())) {
					errorRecord = true;
				}
				*/

				/*
				if(orderRecord.getPickupPaymentCode() != null && !"".equals(orderRecord.getPickupPaymentCode().trim()) && (Integer.parseInt(orderRecord.getPickupPaymentCode()) == 1 || 
						Integer.parseInt(orderRecord.getPickupPaymentCode()) == 2)) {
					if(orderObj.getConvertedOrder().getOrderRecordText().getFinancialInstitutionAccountNumber() == null || 
							"".equals(orderRecord.getFinancialInstitutionAccountNumber().trim()) ||
							orderRecord.getLocationSystemNumber() == null ||
							"".equals(orderRecord.getLocationSystemNumber().trim())) {
						errorRecord = true;
					}
				}
				*/
				
				if(shpCtlRecArrayLength > 0) {
				Shipment : for (int i = 0; i < shpCtlRecArrayLength; i++) {
						ShipmentControlRecordText shpCtlRec = shpCtlRecArray[i];
						
						if(i == 0) {
							firstShipmentRecordNumber = shpCtlRec.getShipmentRecordText().getShipmentNumber();
						}
						
						PackageRecordText[] pkgRecArray = shpCtlRec.getPackageRecordText();
						int pkgRecArrayLength = 0;
						if(pkgRecArray != null) {
							pkgRecArrayLength = pkgRecArray.length;
						}
						
						int pkgPickChgIndOneCount = 0;
						
						if(pkgRecArrayLength > 0) {
							PackageRecordText pkgRecForErrorCond = null;
					Package :	for (int j = 0; j < pkgRecArrayLength; j++) {
								PackageRecordText pkgRec = pkgRecArray[j];
								
								/*
								if(orderObj.getConvertedOrder().getOrderRecordText().getPickupPaymentCode() == null || "".equals(orderRecord.getPickupPaymentCode().trim()) || 
										Integer.parseInt(orderObj.getConvertedOrder().getOrderRecordText().getPickupPaymentCode().trim()) != 6) {
									if(j > 0) {
										break Package;
									}
								} else {
									if(pkgRec.getPackagePickupChargeBillableIndicator() == null || "".equals(pkgRec.getPackagePickupChargeBillableIndicator().trim()) || Integer.parseInt(pkgRec.getPackagePickupChargeBillableIndicator().trim()) != 1) {
										pkgRecForErrorCond = pkgRec;
										errorRecord = true;
										log.debug("getPackagePickupChargeBillableIndicator is null or not 1 for orderSystemNumber: {}", orderObj.getOrderSystemNumber());
										continue;
									} else {
										pkgPickChgIndOneCount = pkgPickChgIndOneCount + 1;
									}
									*/
									
									/*
									if(pkgRec.getPackageBillingClassificationCode() == null ||
											"".equals(pkgRec.getPackageBillingClassificationCode().trim())) {
										errorRecord = true;
									}
									*/
								//}
								
								if (null != pkgRec) {
									StringBuffer toWrite = new StringBuffer();
									if(!errorRecord) {
										writeOrderData(orderObj, mdcFile);
										mdcFile.write(getBytesForShort(shpCtlRec.getShipmentRecordText().getShipmentNumber()));		//139
										writePackageData(pkgRec, mdcFile);
										recordCount = recordCount + 1;
										orderRecordWritten = true;
									} else {
										writeOrderData(orderObj, errorMdcFile);
										errorMdcFile.write(getBytesForShort(shpCtlRec.getShipmentRecordText().getShipmentNumber()));		//139
										writePackageData(pkgRec, errorMdcFile);
										errorRecordCount = errorRecordCount + 1;
									}
								}
							}
							
							/*
							if (null != pkgRecForErrorCond) {
								writeOrderData(orderObj, errorMdcFile);
								errorMdcFile.write(getBytesForShort(shpCtlRec.getShipmentRecordText().getShipmentNumber()));		//139
								writePackageData(pkgRecForErrorCond, errorMdcFile);
								errorRecordCount = errorRecordCount + 1;
							}
							*/
						} else {
							/*
							if(!errorRecord) {
								writeOrderData(orderObj, mdcFile);
								mdcFile.write(getBytesForShort(shpCtlRec.getShipmentRecordText().getShipmentNumber()));		//139
								writePackageData(null, mdcFile);
								recordCount = recordCount + 1;
							} else {
								writeOrderData(orderObj, errorMdcFile);
								errorMdcFile.write(getBytesForShort(shpCtlRec.getShipmentRecordText().getShipmentNumber()));		//139
								writePackageData(null, errorMdcFile);
								errorRecordCount = errorRecordCount + 1;
							}
							*/
						}
					}
				} else {
					/*
					if(!errorRecord) {
						writeOrderData(orderObj, mdcFile);
						mdcFile.write(getBytesForShort(null));		//139
						writePackageData(null, mdcFile);
						recordCount = recordCount + 1;
					} else {
						writeOrderData(orderObj, errorMdcFile);
						errorMdcFile.write(getBytesForShort(null));		//139
						writePackageData(null, errorMdcFile);
						errorRecordCount = errorRecordCount + 1;
					}
					*/
				}
				
				if(!orderRecordWritten) {
					writeOrderData(orderObj, mdcFile);
					mdcFile.write(getBytesForShort(firstShipmentRecordNumber));		//139
					writePackageData(null, mdcFile);
					recordCount = recordCount + 1;
					orderRecordWritten = true;
				}
			}
			
		} catch (Exception ex) {
			log.error("MDCBatchError");
			ex.printStackTrace();
			log.error("Exception occurred while creating file content using RandomAccessFile: {}", ex.getMessage());
		}
		finally {
			if(mdcFile != null) {
				mdcFile.close();
			}
			if(errorMdcFile != null) {
				errorMdcFile.close();
			}
		}
		return recordCount;
	}
	
	private StringBuffer writeOrderData(OrderBillingData orderObj, RandomAccessFile mdcFile) throws IOException {
		OrderRecord orderRecord =  orderObj.getConvertedOrder().getOrderRecordText();
		OrderStatusText orderStatusText = new OrderStatusText();
		OrderStatusTextDBMaster orderStatusTextDBMaster = orderObj.getConvertedOrder().getOrderStatusText();
		if(orderStatusTextDBMaster != null) {
			orderStatusText = orderStatusTextDBMaster.getOrderStatusText();
		}
		int orderServiceRecordsCount = orderObj.getConvertedOrder().getServiceTypeRecordText().length;
		ShipmentControlRecordText[] shpCtlRecArray = orderObj.getConvertedOrder().getShipmentControlRecordText();
		int shpCtlRecArrayLength = shpCtlRecArray.length;

		StringBuffer orderDataToWrite = new StringBuffer();
		mdcFile.write(getBytesForText(rightPaddingSpaces(OmsMDCBatchConstants.RECORD_TYPE_DETAIL, 1)));	//1
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getOrderNumber(), 11)));	//2
		mdcFile.write(getBytesForShort(orderRecord.getInformationSystemTypeCode()));	//3
		mdcFile.write(getBytesForText(rightPaddingSpaces(getFormattedDate(orderRecord.getServiceDate(), "yyyyMMdd", "yyyy-MM-dd"), 10)));// service date	//4
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getDayOfWeekCode(), 1)));	//5
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getOrderActionCode(), 1)));	//6
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getOrderCategoryCode(),2)));	//7
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getPickupRequestRoutingCode(), 2)));	//8
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getCommentsText(), 80)));	//9
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getServiceRegionCode(), 2)));	//10
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getServiceDistrictCode(), 2)));		//11
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getServiceCenterCountryCode(), 3)));		//12
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getOrganizationNumber(), 5)));		//13
		String serviceCenterOrgShipperNumber = leftPaddingZeroesForString(orderRecord.getServiceCenterOrganizationShipperNumber(), 10);
		mdcFile.write(getBytesForText(serviceCenterOrgShipperNumber));	//14
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getCustomerOrderReferenceNumber(), 35)));		//15
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getCustomerName(), 27)));		//16
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getPhoneNumber(), 25)));		//17
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getOrderRequestorTelephoneExtensionNumber(), 10)));		//18
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getTelecommunicationsLineMobileTelephoneNumber(), 25)));		//19
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getWirelessTelecommunicationsCarrierCode(), 3)));		//20
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getElectronicMailFailureDefaultElectronicMailAddressText(), 50)));		//21
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getNotificationElectronicMailListText(),254)));		//22
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getElectronicMailNotificationCustomerCommentText(), 300)));		//23
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getPreferredCustomerIndicator(), 1)));		//24
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getRegistrationIDNumber(), 10)));		//25
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getContactName(), 22)));		//26
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getPickupPointText(), 11)));		//27
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getStreetNumber(), 11)));		//28
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getDirectionPrefixCode(), 2)));		//29
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getStreetTypeCode(), 4)));		//30
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getDirectionSuffixCode(), 2)));		//31
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getStreetName(), 50)));		//32
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getRoomNumber(), 8)));		//33
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getFloorNumber(), 3)));		//34
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getLoopNumber(), 4)));		//35
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getUnitNumber(), 4)));		//36
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getSequenceNumber(), 4)));		//37
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getSequenceNumberSuffixCode(), 1)));		//38
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getPoliticalDivision1Name(), 50))); 		//39
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getPoliticalDivision2Name(), 50)));		//40
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getPoliticalDivision3Name(), 50)));		//41
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getServiceLocationCountryCode(), 3)));		//42
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getPostal1Code(), 8)));		//43
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getPostal2Code(), 8)));		//44
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getAddressMatchCode(), 2)));		//45
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getAddressArtifactSourceCode(), 2)));		//46
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getStreetAddressText(), 73)));		//47
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getLatitudeNumber(), 7)));		//48
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getLongitudeNumber(), 8)));		//49
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getDestinationServiceCenterCountryCode(),3)));		//50
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getDestinationServiceCenterOrganizationNumber(), 5)));		//51
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getPickupPaymentCode(), 2)));		//52
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getHighLevelServiceText(), 10)));		//53
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getStopResidentialIndicator(), 1)));		//54
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getPickupTransactionTypeCode(), 3)));		//55
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getPickupStopBillingClassificationCode(),2)));		//56
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getCashOnPickupTransactionCategoryCode(),2)));		//57
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getServiceClassificationTypeCode(), 2)));		//58
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getTransportationPaymentCode(), 2)));		//59
		mdcFile.write(getBytesForShort(orderRecord.getPaymentMethodTypeCode()));		//60
		mdcFile.write(getBytesForShort(orderRecord.getPayorTypeCode()));		//61
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getCurrencyCode(), 3)));		//62
		mdcFile.write(getPackedDecimal(orderRecord.getCollectionAmount(), 10));		//63
		mdcFile.write(getPackedDecimal(orderRecord.getOrderTotalPickupChargeAmount(), 10));		//64
		mdcFile.write(getPackedDecimal(orderRecord.getPickupTaxAmount(), 10));		//65
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getOrderRatedIndicator(), 1)));  	//66
		mdcFile.write(getPackedDecimal(orderRecord.getOrderTotalShippingChargeAmount(), 10));		//67
		mdcFile.write(getPackedDecimal(orderRecord.getTaxAmount(), 10));		//68
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getFinancialInstitutionAccountCountryCode(), 3)));  //check		//69
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getFinancialInstitutionIdentificationNumberPostalCode(), 8)));		//70
		
		/* changes added for billing defect where shipper number coming in lower case  start */
		if (orderRecord.getFinancialInstitutionAccountNumber()!=null && !"".equals(orderRecord.getFinancialInstitutionAccountNumber().trim())) {
			mdcFile.write(getBytesForText(leftPaddingZeroesForString(orderRecord.getFinancialInstitutionAccountNumber().toUpperCase(), 10)));		//71
		} else {
			mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getFinancialInstitutionAccountNumber(), 10)));		//71
		}
		/* changes added for billing defect where shipper number coming in lower case  end */
		
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getFinancialMediaCategoryIdentifierNumber(), 16)));		//72
		mdcFile.write(getBytesForShort(orderRecord.getFinancialMediaCategoryIdentifierCode()));		//73
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getExpirationDate(), 10)));		//74
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getFinancialInstitutionName(), 22)));		//75
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getFinancialMediaCategoryAccountHolderName(), 4)));		//76
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getChargeCardAuthorizationNumber(), 24)));		//77
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getPaymentTransactionGloballyUniqueIdentifierNumber(), 32)));		//78
		mdcFile.write(getBytesForInt(orderRecord.getLettersQuantity()));		//79
		mdcFile.write(getBytesForInt(orderRecord.getPackageQuantity()));		//80
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getOverweightLimitIndicator(), 1)));		//81
		mdcFile.write(getPackedDecimal(orderRecord.getActualWeightQuantity(), 5));		//82
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getWeightMeasurmentUnitTypeCode(), 3)));		//83
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getShippingDocumentCode(), 1)));		//84
		
		String orderPlacedOffet = orderRecord.getPickupLocationOrderPlacedTimeZoneOffsetTime();
		orderPlacedOffet = orderPlacedOffet.replaceAll("GMT", "");
		orderPlacedOffet = orderPlacedOffet.replaceAll(":", ".");
		
		mdcFile.write(getPackedDecimal(orderPlacedOffet, 3));   //check		//85
		
		String orderCommitOffet = orderRecord.getPickupLocationCommitTimeZoneOffsetTime();
		orderCommitOffet = orderCommitOffet.replaceAll("GMT", "");
		orderCommitOffet = orderCommitOffet.replaceAll(":", ".");
		
		mdcFile.write(getPackedDecimal(orderCommitOffet, 3));  //check		//86
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getOrderReturnServiceLabelsIndicator(), 1)));		//87
		mdcFile.write(getBytesForInt(orderRecord.getOrderPlacedTime()));	//88
		mdcFile.write(getBytesForInt(orderRecord.getReadyTime()));		//89
		mdcFile.write(getBytesForInt(orderRecord.getCommitTime()));		//90
		mdcFile.write(getBytesForInt(orderRecord.getClosingTime()));		//91
		mdcFile.write(getBytesForInt(orderRecord.getCountryLunchStartLunchTime()));		//92
		mdcFile.write(getBytesForInt(orderRecord.getCountryLunchEndLunchTime()));		//93
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getOrderDocumentsOfNoCommercialValueIndicator(), 1)));		//94
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getMobileLanguageLocaleText(), 5)));		//95
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getOriginLiftgateIndicator(), 1)));		//96
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getHazMatIndicator(), 1)));		//97
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getPickupDropoffCode(), 1)));		//98
		mdcFile.write(getBytesForText(getDefaultTimestampIfEmpty(getFormattedDateFromEpoch(orderRecord.getOrderPlacedTime()))));  //check		//99
		mdcFile.write(getBytesForText(getDefaultTimestampIfEmpty(getFormattedDateFromEpoch(orderRecord.getOrderPlacedTime(), orderObj.getConvertedOrder().getOrderRecordText().getPickupLocationOrderPlacedTimeZoneOffsetTime()))));		//100
		mdcFile.write(getBytesForText(getDefaultTimestampIfEmpty(getFormattedDateFromEpoch(orderRecord.getReadyTime(), orderObj.getConvertedOrder().getOrderRecordText().getPickupLocationCommitTimeZoneOffsetTime()))));		//101
		mdcFile.write(getBytesForText(getDefaultTimestampIfEmpty(getFormattedDateFromEpoch(orderRecord.getCommitTime(), orderObj.getConvertedOrder().getOrderRecordText().getPickupLocationCommitTimeZoneOffsetTime()))));		//102
		mdcFile.write(getBytesForText(getDefaultTimestampIfEmpty(getFormattedDateFromEpoch(orderRecord.getClosingTime(), orderObj.getConvertedOrder().getOrderRecordText().getPickupLocationCommitTimeZoneOffsetTime()))));		//103
		mdcFile.write(getBytesForText(getDefaultTimestampIfEmpty(getFormattedDateFromEpoch(orderRecord.getCountryLunchStartLunchTime(), orderObj.getConvertedOrder().getOrderRecordText().getPickupLocationOrderPlacedTimeZoneOffsetTime()))));		//104
		mdcFile.write(getBytesForText(getDefaultTimestampIfEmpty(getFormattedDateFromEpoch(orderRecord.getCountryLunchEndLunchTime(), orderObj.getConvertedOrder().getOrderRecordText().getPickupLocationOrderPlacedTimeZoneOffsetTime()))));		//105
		mdcFile.write(getBytesForText(rightPaddingSpaces(OmsMDCBatchConstants.FILLER_230, 230)));		//106
		mdcFile.write(getBytesForShort(orderObj.getOrderUpdateCode()));	//107		//query OrderUpdateCode from TORDER_UPDATE_EVENT
		String statusTime = "";
		String etaTime = "";
		String serviceFailureText = "";
		int noOfPiecesQuantity = 0;
		String collectionAmount = "0";
		String currencyCode = "";
		String employeeNumber = "";
		String commentsText = "";
		if(orderStatusText != null) {
			statusTime = orderStatusText.getStatusTime();
			etaTime = orderStatusText.getETATime();
			serviceFailureText = orderStatusText.getServiceFailureReasonText();
			noOfPiecesQuantity = orderStatusText.getNoOfPiecesQuantity();
			collectionAmount = orderStatusText.getCollectionAmount();
			currencyCode = orderStatusText.getCurrencyCode();
			employeeNumber = orderStatusText.getEmployeeNumber();
			commentsText = orderStatusText.getCommentsText();
		}
		mdcFile.write(getBytesForInt(statusTime));		//108
		mdcFile.write(getBytesForInt(etaTime));		//109
		mdcFile.write(getBytesForText(rightPaddingSpaces(serviceFailureText, 15)));		//110
		mdcFile.write(getBytesForInt(""+noOfPiecesQuantity));  ////toWrite.append(rightPaddingSpaces(orderObj.getConvertedOrder().getServiceTypeRecordText().get, 4));		//111
		mdcFile.write(getPackedDecimal(collectionAmount, 10));		//112
		mdcFile.write(getBytesForText(rightPaddingSpaces(currencyCode, 3)));		//113
		mdcFile.write(getBytesForShort(""+orderObj.getConvertedOrder().getServiceTypeRecordText().length));		//114
		mdcFile.write(getBytesForText(getDefaultTimestampIfEmpty(getFormattedDateFromTimestamp(orderObj.getRecordInsertTimestamp()))));		//115		//need to join TORDER_UPDATE_EVENT and TORDER On OrderSystemNumber and get latest OrderUpdateTimestamp
		mdcFile.write(getBytesForText(rightPaddingSpaces(employeeNumber, 10)));		//116
		mdcFile.write(getBytesForText(rightPaddingSpaces(commentsText, 120)));		//117
		mdcFile.write(getBytesForText(getDefaultTimestampIfEmpty(getFormattedDateFromEpoch(statusTime, orderObj.getConvertedOrder().getOrderRecordText().getPickupLocationCommitTimeZoneOffsetTime()))));		//118		
		mdcFile.write(getBytesForText(getDefaultTimestampIfEmpty(getFormattedDateFromEpoch(etaTime, orderObj.getConvertedOrder().getOrderRecordText().getPickupLocationCommitTimeZoneOffsetTime()))));		//119
		mdcFile.write(getBytesForText(rightPaddingSpaces(OmsMDCBatchConstants.FILLER_208, 208)));		//120
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderObj.getOrderUpdateCode() == null ? OmsMDCBatchConstants.ORDER_HAVE_TO_BE_ROUTED : OmsMDCBatchConstants.ORDER_ALREADY_ROUTED, 2)));		//121		// from TORDER_UPDATE table based on order update code
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderObj.getDispatchFacilitySystemNumber(), 15)));		//122		//join TORDER with TSLIC_TABLE on OrganisationNumber to get the DispatchFacilityNumber
		mdcFile.write(getBytesForText(getDefaultTimestampIfEmpty(getFormattedDateFromTimestamp(orderObj.getAcknowledgementTimestamp()))));		//123		//from TORDER_UPDATE_EVENT table - UpdateTimestamp for OrderUpdateCode of Acknowledge
		mdcFile.write(getBytesForText(getDefaultTimestampIfEmpty(getFormattedDateFromTimestamp(orderObj.getOrderUpdateTimestamp()))));		//124		//OrderUpdateTimestamp from TORDER_UPDATE_EVENT
		mdcFile.write(getBytesForText(getDefaultTimestampIfEmpty(getFormattedDateFromTimestamp(orderObj.getRecordInsertMinTimestamp()))));		//125		//RecInsertTimestamp from TORDER_UPDATE_EVENT
		mdcFile.write(getBytesForText(getDefaultTimestampIfEmpty(OmsMDCBatchConstants.SPACE)));		//126		//Not relevant field for OMS
		mdcFile.write(getBytesForText(rightPaddingSpaces(OmsMDCBatchConstants.CHAR_NO, 1)));//check		//127	//Not relevant for OMS. Should be "N" always
		mdcFile.write(getBytesForText(rightPaddingSpaces(OmsMDCBatchConstants.ORDER_CANCELLED_CODE.equals(orderObj.getOrderUpdateCode()) ? OmsMDCBatchConstants.CHAR_YES : OmsMDCBatchConstants.CHAR_NO, 1)));		//128		// from TORDER_UPDATE_EVENT table OrderUpdateCode
		mdcFile.write(getBytesForShort(""+shpCtlRecArrayLength));		//129

		if(shpCtlRecArrayLength > 0) {
			ShipmentControlRecordText shpCtlRecTemp = shpCtlRecArray[0];
			PackageRecordText[] pkgRecArrayTemp = shpCtlRecTemp.getPackageRecordText();
			if(pkgRecArrayTemp != null && pkgRecArrayTemp.length > 0) {
				String pkLength = ""+pkgRecArrayTemp.length;
				mdcFile.write(getBytesForShort(pkLength));		//130
			} else {
				mdcFile.write(getBytesForShort("0"));		//130
			}
		} else {
			mdcFile.write(getBytesForShort("0"));		//130
			
		}
		
		mdcFile.write(getBytesForShort(""+orderServiceRecordsCount));		//131
		mdcFile.write(getBytesForText(rightPaddingSpaces(applicationProtocolVersionNumber, 10)));		//132
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getMessageSegmentVersionNumber(), 10)));		//133
		mdcFile.write(getBytesForText(rightPaddingSpaces(OmsMDCBatchConstants.FILLER_201, 201)));		//134
		
		/* changes added for billing defect where shipper number coming in lower case  start */
		if (orderRecord.getLocationSystemNumber()!=null) {
			mdcFile.write(getBytesForText(spacesIfDefaultShipperNumber(orderRecord.getPickupPaymentCode(), leftPaddingZeroesForString(orderRecord.getLocationSystemNumber().toUpperCase(), 10), serviceCenterOrgShipperNumber)));		//135
		} else {
			mdcFile.write(getBytesForText(spacesIfDefaultShipperNumber(orderRecord.getPickupPaymentCode(), leftPaddingZeroesForString(orderRecord.getLocationSystemNumber(), 10), serviceCenterOrgShipperNumber)));		//135
		}
		/* changes added for billing defect where shipper coming in lower case end */
		
		mdcFile.write(getBytesForText(rightPaddingSpaces(orderRecord.getShipperNumberCountryCode(), 3)));		//136
		mdcFile.write(getBytesForText(getDefaultTimestampIfEmpty(getFormattedDateFromTimestamp(orderObj.getRecordInsertMinTimestamp()))));		//137
		mdcFile.write(getBytesForText(rightPaddingSpaces(OmsMDCBatchConstants.FILLER_201, 201)));		//138
		
		return orderDataToWrite;
	}
	
	private StringBuffer writePackageData(PackageRecordText pkgRec, RandomAccessFile mdcFile) throws IOException {
		StringBuffer packageDataToWrite = new StringBuffer();
		if(pkgRec != null) {
			mdcFile.write(getBytesForShort(pkgRec.getPackageNumber()));		//140
			mdcFile.write(getPackedDecimal(pkgRec.getActualWeightQuantity(), 5));		//141
			mdcFile.write(getPackedDecimal(pkgRec.getBillableWeightQuantity(), 5));		//142
			mdcFile.write(getPackedDecimal(pkgRec.getDimensionalWeightQuantity(), 5));		//143
			mdcFile.write(getBytesForText(rightPaddingSpaces(pkgRec.getLinearMeasurementUnitTypeCode(), 3)));		//144
			mdcFile.write(getBytesForShort(pkgRec.getDimension1Quantity()));		//145
			mdcFile.write(getBytesForShort(pkgRec.getDimension2Quantity()));		//146
			mdcFile.write(getBytesForShort(pkgRec.getDimension3Quantity()));		//147
			mdcFile.write(getPackedDecimal(pkgRec.getCollectOnDeliveryAmount(), 10));		//148
			mdcFile.write(getPackedDecimal(pkgRec.getDeclaredValueAmount(), 10));		//149
			mdcFile.write(getBytesForText(rightPaddingSpaces(pkgRec.getOversizeIndicator(), 1)));		//150
			mdcFile.write(getBytesForText(rightPaddingSpaces(pkgRec.getCashOnlyIndicator(), 1)));		//151
			mdcFile.write(getBytesForText(rightPaddingSpaces(pkgRec.getPackageTrackingNumber(), 35)));		//152
			mdcFile.write(getBytesForText(rightPaddingSpaces(pkgRec.getReturnServiceIndicator(), 1)));		//153
			mdcFile.write(getBytesForText(rightPaddingSpaces(pkgRec.getPackagePickupChargeAcceptIndicator(), 1)));		//154
			mdcFile.write(getBytesForText(rightPaddingSpaces(pkgRec.getPackagePickupChargeBillableIndicator(), 1)));		//155
			mdcFile.write(getBytesForText(rightPaddingSpaces(pkgRec.getTransportationServiceCategoryCode(), 2)));		//156
			mdcFile.write(getBytesForText(rightPaddingSpaces(pkgRec.getPackageBillingClassificationCode(), 2)));		//157
			mdcFile.write(getBytesForText(rightPaddingSpaces(pkgRec.getPackageReference1Number(), 35)));		//158
			mdcFile.write(getBytesForText(rightPaddingSpaces(pkgRec.getPackageReference2Number(), 35)));		//159
			mdcFile.write(getBytesForText(rightPaddingSpaces(pkgRec.getPackageReference3Number(), 35)));		//160
			mdcFile.write(getBytesForText(rightPaddingSpaces(OmsMDCBatchConstants.FILLER_203, 203)));		//161
		} else {
			mdcFile.write(getBytesForShort("0"));		//140
			mdcFile.write(getPackedDecimal("-100", 5));		//141
			mdcFile.write(getPackedDecimal("-100", 5));		//142
			mdcFile.write(getPackedDecimal("-100", 5));		//143
			mdcFile.write(getBytesForText(rightPaddingSpaces(null, 3)));		//144
			mdcFile.write(getBytesForShort("-1"));		//145
			mdcFile.write(getBytesForShort("-1"));		//146
			mdcFile.write(getBytesForShort("-1"));		//147
			mdcFile.write(getPackedDecimal("-100", 10));		//148
			mdcFile.write(getPackedDecimal("-100", 10));		//149
			mdcFile.write(getBytesForText(rightPaddingSpaces(null, 1)));		//150
			mdcFile.write(getBytesForText(rightPaddingSpaces(null, 1)));		//151
			mdcFile.write(getBytesForText(rightPaddingSpaces(null, 35)));		//152
			mdcFile.write(getBytesForText(rightPaddingSpaces(null, 1)));		//153
			mdcFile.write(getBytesForText(rightPaddingSpaces(null, 1)));		//154
			mdcFile.write(getBytesForText(rightPaddingSpaces(null, 1)));		//155
			mdcFile.write(getBytesForText(rightPaddingSpaces(null, 2)));		//156
			mdcFile.write(getBytesForText(rightPaddingSpaces(null, 2)));		//157
			mdcFile.write(getBytesForText(rightPaddingSpaces(null, 35)));		//158
			mdcFile.write(getBytesForText(rightPaddingSpaces(null, 35)));		//159
			mdcFile.write(getBytesForText(rightPaddingSpaces(null, 35)));		//160
			mdcFile.write(getBytesForText(rightPaddingSpaces(OmsMDCBatchConstants.FILLER_203, 203)));		//161
		}
		return packageDataToWrite;
	}
	
	
	private String getFormattedDateFromEpoch(String epoch, String offset) {
		if(epoch == null || "".equals(epoch.trim())) {
			return " ";
		}
		Long longEpoch = Long.parseLong(epoch);
		if (offset == null) {
			offset = "";
		}
		Date convertedEpochDate = new Date(longEpoch.longValue() * 1000);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSSSSS");
		String timeZone = offset.contains("GMT") ? offset : "GMT" + offset;
        format.setTimeZone(TimeZone.getTimeZone(timeZone));
        String formattedConvertedEpochDateDate = format.format(convertedEpochDate);
        return formattedConvertedEpochDateDate;
	}
	
	private String getFormattedDateFromEpoch(String epoch) {
		if(epoch == null || "".equals(epoch.trim())) {
			return "";
		}
		Long longEpoch = Long.parseLong(epoch);
		Date convertedEpochDate = new Date(longEpoch.longValue() * 1000);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSSSSS");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String formattedConvertedEpochDateDate = format.format(convertedEpochDate);
        return formattedConvertedEpochDateDate;
	}
	
	
	private String getFormattedDateFromTimestamp(Timestamp timestamp) {
		if(timestamp == null) {
			return " ";
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp.getTime());
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSSSSS");
        String formattedDate = format.format(cal.getTime());
        return formattedDate;
	}
	
	private String getFormattedDate(Date date, String format) {
		DateFormat dtFormat = new SimpleDateFormat(format);
        String formattedDate = dtFormat.format(date);
        return formattedDate;
	}
	
	private String getFormattedDate(String date, String sourceFormat, String targetFormat) {
		if(date == null || "".equals(date.trim())) {
			return " ";
		}
		DateFormat dtFormat = new SimpleDateFormat(sourceFormat);
		Date convDate = null;
		try {
			convDate = dtFormat.parse(date);
		} catch(Exception e) {
			e.printStackTrace();
		}
		DateFormat tgFormat = new SimpleDateFormat(targetFormat);
        String formattedDate = tgFormat.format(convDate);
        return formattedDate;
	}
	
	public static String rightPaddingSpaces(String str, int num) {
		if(str == null || "".equals(str.trim())) {
			str = "";
		}
		String strToReturn = String.format("%1$-" + num + "s", str);
		if(strToReturn.length() > num) {
			strToReturn = strToReturn.substring(0, num);
		}
		return strToReturn;
	}
	
	public static String leftPaddingZeroesForFloat(String str, int totNum, int numAfterDec) {
		if(str == null || "".equals(str.trim())) {
			str = "0";
		}
		float convFloat = 0;
		try {
			convFloat = Float.parseFloat(str);
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
		return ((convFloat < 0) ? "-" : " ") + String.format("%0" + (totNum-1) + "." + numAfterDec + "f", Math.abs(convFloat));
	}
	
	public static String leftPaddingZeroes(String str, int num) {
		if(str == null || "".equals(str.trim())) {
			str = "0";
		}
		int convInt = 0;
		try {
			convInt = Integer.parseInt(str);
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
		return ((convInt < 0) ? "-" : " ") + String.format("%0" + (num-1) + "d", Math.abs(convInt)); 
	}
	
	public static String leftPaddingZeroesForString(String str, int num) {
		if(str == null || "".equals(str.trim())) {
			str = "0";
		}
		str = str.trim();
	    return String.format("%1$" + num + "s", str).replace(' ', '0');
	}	

	private static byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(x);
	    return buffer.array();
	}

	private static byte[] integerToBytes(int x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
	    buffer.putInt(x);
	    return buffer.array();
	}

	private static byte[] shortToBytes(short x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
	    buffer.putShort(x);
	    return buffer.array();
	}

	public static byte[] getBytesForText(String text) {
		if(text == null) {
			text = "";
		}
		try {
			return text.getBytes("Cp1047");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return "".getBytes();
	}

	public static byte[] getBytesForShort(String text) {
		if(text == null || "".equals(text)) {
			text = "0";
		}
		try {
			short sText = Short.parseShort(text);
			return shortToBytes(sText);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		short x = 0;
		return shortToBytes(x);
	}

	public static byte[] getBytesForInt(String text) {
		if(text == null || "".equals(text)) {
			text = "0";
		}
		try {
			int sText = Integer.parseInt(text);
			return integerToBytes(sText);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return integerToBytes(0);
	}

	public static byte[] getPackedDecimal(String text, int byteCount) {
		if(text == null || "".equals(text)) {
			text = "0";
		}
		int dotIndex = text.indexOf(".");
		int textLen = text.length();
		if(text.indexOf(".") != -1) {
			try {
				double dTxt = Double.parseDouble(text);
				text = text.format("%.2f", dTxt);
				text = text.replace(".", "");
			} catch(Exception e) {
				text = "0";
				e.printStackTrace();
			}
		} else {
			try {
				long lTxt = Long.parseLong(text);
			} catch(Exception e) {
				text = "0";
				e.printStackTrace();
			}
		}
		return PackedDecimal.format(text, byteCount);
	}

	private String spacesIfDefaultShipperNumber(String pickupPaymentCode, String shipperNumber, String serviceCenterOrgShipperNumber) {
		if(OmsMDCBatchConstants.DEFAULT_SHIPPER_NUM.equalsIgnoreCase(shipperNumber) && OmsMDCBatchConstants.DEFAULT_SHIPPER_NUM.equalsIgnoreCase(serviceCenterOrgShipperNumber)) {
			//if(OmsMDCBatchConstants.CHARGE_CARD.equals(pickupPaymentCode) || OmsMDCBatchConstants.PAY_PAL.equals(pickupPaymentCode)
				//	|| OmsMDCBatchConstants.CASH.equals(pickupPaymentCode) || OmsMDCBatchConstants.ALT_PYMT.equals(pickupPaymentCode)) {
				return "          ";
			//}
		}
		return shipperNumber;
	}

	public int getErrorRecordCount() {
		return errorRecordCount;
	}

	public void setErrorRecordCount(int errorRecordCount) {
		this.errorRecordCount = errorRecordCount;
	}
	
	public String getDefaultTimestampIfEmpty(String timeStamp) {
		if(timeStamp == null) {
			timeStamp = "";
		}
		if(timeStamp != null && "".equals(timeStamp.trim())) {
			return "1970-01-01-00.00.00.000001";
		}
		return timeStamp;
	}

	public String getPreviousOrderNumber() {
		return previousOrderNumber;
	}

	public void setPreviousOrderNumber(String previousOrderNumber) {
		this.previousOrderNumber = previousOrderNumber;
	}
	
	
}