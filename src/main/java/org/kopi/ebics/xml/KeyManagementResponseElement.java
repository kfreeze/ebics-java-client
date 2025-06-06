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

package org.kopi.ebics.xml;

import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.exception.ReturnCode;
import org.kopi.ebics.interfaces.ContentFactory;
import org.kopi.ebics.schema.h005.EbicsKeyManagementResponseDocument;

/**
 * The <code>KeyManagementResponseElement</code> is the common element
 * for ebics key management requests. This element aims to control the
 * returned code from the ebics server and throw an exception if it is
 * not an EBICS_OK code.
 *
 * @author hachani
 */
public class KeyManagementResponseElement extends DefaultResponseElement<EbicsKeyManagementResponseDocument> {


    private static final long serialVersionUID = -3556995397305708927L;
    private EbicsKeyManagementResponseDocument.EbicsKeyManagementResponse response;


    /**
     * Creates a new <code>KeyManagementResponseElement</code>
     * from a given <code>ContentFactory</code>
     *
     * @param factory the content factory enclosing the ebics response
     * @param name    the element name
     */
    public KeyManagementResponseElement(ContentFactory factory, String name) {
        super(factory, name);
    }

    /**
     * Returns the transaction key of the response.
     *
     * @return the transaction key.
     */
    public byte[] getTransactionKey() {
        return response.getBody().getDataTransfer().getDataEncryptionInfo().getTransactionKey();
    }


    /**
     * Returns the order data of the response.
     *
     * @return the order data.
     */
    @SuppressWarnings("deprecation")
    public byte[] getOrderData() {
        return response.getBody().getDataTransfer().getOrderData().getByteArrayValue();
    }

    @Override
    public void build() throws EbicsException {
        String code;
        String text;

        parse(EbicsKeyManagementResponseDocument.Factory);
        response = document.getEbicsKeyManagementResponse();
        code = response.getHeader().getMutable().getReturnCode();
        text = response.getHeader().getMutable().getReportText();
        returnCode = ReturnCode.toReturnCode(code, text);
        report();
    }
}
