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

    /**
     * HIA - Initialisation of the subscriber (sending the public keys).
     */
    HIA,

    /**
     * HAA - Download list of available order types from the bank (including BTFs).
     */
    HAA,

    /**
     * HKD - Retrieve customer and user-specific data including permissions and account access.
     */
    HKD,

    /**
     * HPB - Download the bank's public keys for signature, encryption, and authentication.
     */
    HPB,

    /**
     * HPD - Download EBICS bank parameters (e.g. supported versions and security policies).
     */
    HPD,

    /**
     * HTD - Retrieve technical subscriber data (e.g. key usage and timestamps).
     */
    HTD,

    /**
     * HCA - Send updated public key(s) to the bank after key change (without initialization).
     */
    HCA,

    /**
     * HCS - Change subscriber keys for electronic signatures or encryption.
     */
    HCS,

    /**
     * HEV - EBICS version check; used to verify protocol and server availability.
     */
    HEV,

    /**
     * HVD - Retrieve VEU (distributed signature) status.
     */
    HVD,

    /**
     * HVE - Add electronic signature to an order within VEU workflow.
     */
    HVE,

    /**
     * HVS - Cancel (storno) of a VEU order that has not yet been completely signed.
     */
    HVS,

    /**
     * HVT - Retrieve transaction details for a VEU order.
     */
    HVT,

    /**
     * HVU - Download a VEU order summary list.
     */
    HVU,

    /**
     * HVZ - Download detailed VEU order overview including signature history.
     */
    HVZ,

    /**
     * H3K - Key confirmation order, sent after verifying hash values manually (used rarely).
     */
    H3K,

    /**
     * INI - Password initialization by subscriber (first login or reset).
     */
    INI,

    /**
     * SPR - Lock the subscriber account at the bank (e.g. due to fraud or lost keys).
     */
    SPR,

    /**
     * PUB - Upload of the bank’s or subscriber’s public key (as part of update or replacement).
     */
    PUB,

    // -- File Management (3.0 / extended types) --

    /**
     * UPL - Upload files using the new BTF structure (EBICS 3.0 only).
     */
    UPL,

    /**
     * DNL - Download files using the new BTF structure (EBICS 3.0 only).
     */
    DNL,

    /**
     * BTU - Business Transaction Upload (legacy upload of e.g. pain.008 or pain.001).
     */
    BTU,

    /**
     * BTD - Business Transaction Download (legacy download e.g. camt.053, MT940).
     */
    BTD,

    // -- Technical / Special --

    /**
     * PTK - Download subscriber protocol files (e.g. log of signed transactions).
     */
    PTK,

    /**
     * HAC - Download customer protocol information (XML-based protocol report).
     */
    HAC,

    /**
     * XKD - Special diagnostic or internal test command (used rarely).
     */
    XKD;


    @Override
    public String getCode() {
        return name();
    }

}
