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
import org.kopi.ebics.session.EbicsSession;
import org.kopi.ebics.session.OrderType;
import org.kopi.ebics.utils.Utils;

/**
 * The <code>HIARequestElement</code> is the root element used
 * to send the authentication and encryption keys to the ebics
 * bank server
 *
 * @author hachani
 */
public class HIARequestElement extends DefaultEbicsRootElement {


    private static final long serialVersionUID = 1130436605993828777L;
    private UnsecuredRequestElement unsecuredRequest;

    /**
     * Constructs a new HIA Request root element
     *
     * @param session the current ebics session
     */
    public HIARequestElement(EbicsSession session) {
        super(session);
    }

    @Override
    public String getName() {
        return "HIARequest.xml";
    }


    @Override
    public void build() throws EbicsException {
        HIARequestOrderDataElement requestOrderData;

        requestOrderData = new HIARequestOrderDataElement(session);
        requestOrderData.build();
        unsecuredRequest = new UnsecuredRequestElement(session,
                OrderType.HIA,
                Utils.zip(requestOrderData.prettyPrint()));
        unsecuredRequest.build();
    }

    @Override
    public byte[] toByteArray() {
        setSaveSuggestedPrefixes("http://www.ebics.org/H003", "");

        return unsecuredRequest.toByteArray();
    }

    @Override
    public void validate() throws EbicsException {
        unsecuredRequest.validate();
    }
}
