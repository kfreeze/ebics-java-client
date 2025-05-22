/*
 * Copyright (c) 1990-2012 kopiLeft Development SARL, Bizerte, Tunisia
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package org.kopi.ebics.session;

import org.kopi.ebics.interfaces.EbicsOrderType;

/**
 * A BCS order type.
 *
 * @author Pierre Ducroquet
 */
public enum OrderType implements EbicsOrderType {
    // -- Administrative / Initialization --
    HIA,
    HAA,
    HKD,
    HPB,
    HPD,
    HTD,
    HCA,
    HCS,
    HEV,
    HVD,
    HVE,
    HVS,
    HVT,
    HVU,
    HVZ,
    H3K,
    INI,
    SPR,
    PUB,

    // -- Upload Order Types --
    FUL,
    CCT,
    CCU,
    CD1,
    CDB,
    CDC,
    CDD,
    DTE,
    EUE,
    AZV,
    CCC,
    C2C,
    XCT,
    XE2,

    // -- Download Order Types --
    FDL,
    STA,
    VMK,
    ZDF,
    ZB6,
    Z01,
    C52,
    C53,
    C54,
    Z53,
    Z54,

    // -- File Management (3.0 / extended types) --
    UPL,
    DNL,
    BTU,
    BTD,

    // -- Technical / Special --
    PTK,
    HAC,
    XKD;


    @Override
    public String getCode() {
        return name();
    }

}
