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
 * The INI request XML element. This root element is to be sent
 * to the ebics server to initiate the signature certificate.
 *
 * @author hachani
 */
public class INIRequestElement extends DefaultEbicsRootElement {


    private static final long serialVersionUID = -1966559247739923555L;
    private UnsecuredRequestElement unsecuredRequest;

    /**
     * Constructs a new INI request element.
     *
     * @param session the ebics session.
     */
    public INIRequestElement(EbicsSession session) {
        super(session);
    }

    @Override
    public String getName() {
        return "INIRequest.xml";
    }

    @Override
    public void build() throws EbicsException {
        SignaturePubKeyOrderDataElement signaturePubKey;

        signaturePubKey = new SignaturePubKeyOrderDataElement(session);
        signaturePubKey.build();
        unsecuredRequest = new UnsecuredRequestElement(session,
                OrderType.INI,
                Utils.zip(signaturePubKey.prettyPrint()));
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
