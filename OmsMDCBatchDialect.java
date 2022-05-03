/**
 * ==========================================================================
 * Filename: OmsMDCBatchDialect.java
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
package com.ups.ops.oms.batch.mdc.dialect;

import java.sql.Types;

import org.hibernate.dialect.SQLServer2012Dialect;

/**
 * The Class OmsMDCBatchDialect.
 */
public class OmsMDCBatchDialect extends SQLServer2012Dialect {
    
    /**
     * Instantiates a new oms MDC batch dialect.
     */
    public OmsMDCBatchDialect(){
        super();
        registerHibernateType(Types.NVARCHAR, "string");
    }
}
