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
import org.kopi.ebics.schema.h005.EbicsResponseDocument;
import org.kopi.ebics.schema.h005.EbicsResponseDocument.EbicsResponse;

/**
 * The <code>ReceiptResponseElement</code> is the response element
 * for ebics receipt request.
 *
 * @author Hachani
 */
public class ReceiptResponseElement extends DefaultResponseElement<EbicsResponseDocument> {

    private static final long serialVersionUID = 2994403708414164919L;

    /**
     * Constructs a new <code>ReceiptResponseElement</code> object
     *
     * @param factory the content factory
     * @param name    the element name
     */
    public ReceiptResponseElement(ContentFactory factory, String name) {
        super(factory, name);
    }

    @Override
    public void build() throws EbicsException {
        String code;
        String text;
        EbicsResponse response;

        parse(EbicsResponseDocument.Factory);
        response = document.getEbicsResponse();
        code = response.getHeader().getMutable().getReturnCode();
        text = response.getHeader().getMutable().getReportText();
        returnCode = ReturnCode.toReturnCode(code, text);
        report();
    }


    @Override
    public void report() throws EbicsException {
        if (!returnCode.equals(ReturnCode
                .EBICS_DOWNLOAD_POSTPROCESS_DONE)) {
            returnCode.throwException();
        }
    }
}
