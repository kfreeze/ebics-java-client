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

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.interfaces.EbicsUser;
import org.kopi.ebics.schema.s002.OrderSignatureDataType;
import org.kopi.ebics.schema.s002.UserSignatureDataSigBookType;


/**
 * A root EBICS element representing the user signature
 * element. The user data is signed with the user signature
 * key sent in the INI request to the EBICS bank server
 *
 * @author hachani
 */
public class UserSignature extends DefaultEbicsRootElement {


    private static final long serialVersionUID = 2992372604876703738L;
    private final EbicsUser user;
    private final String signatureVersion;
    private final byte[] toSign;


    private final String name;

    /**
     * Constructs a new <code>UserSignature</code> element for
     * an Ebics user and a data to sign
     *
     * @param user             the ebics user
     * @param signatureVersion the signature version
     * @param toSign           the data to be signed
     */
    public UserSignature(EbicsUser user,
                         String name,
                         String signatureVersion,
                         byte[] toSign) {
        this.user = user;
        this.toSign = toSign;
        this.name = name;
        this.signatureVersion = signatureVersion;
    }

    @Override
    public void build() throws EbicsException {
        UserSignatureDataSigBookType userSignatureData;
        OrderSignatureDataType orderSignatureData;
        byte[] signature;

        try {
            signature = user.sign(toSign);
        } catch (IOException e) {
            throw new EbicsException(e.getMessage(), e);
        } catch (GeneralSecurityException e) {
            throw new EbicsException(e.getMessage(), e);
        }

        orderSignatureData = EbicsXmlFactory.createOrderSignatureDataType(signatureVersion,
                user.getPartner().getPartnerId(),
                user.getUserId(),
                signature);
        userSignatureData = EbicsXmlFactory.createUserSignatureDataSigBookType(new OrderSignatureDataType[]{orderSignatureData});
        document = EbicsXmlFactory.createUserSignatureDataDocument(userSignatureData);
    }

    @Override
    public String getName() {
        return name + ".xml";
    }

    @Override
    public byte[] toByteArray() {
        setSaveSuggestedPrefixes("http://www.ebics.org/S002", "");

        return super.toByteArray();
    }
}
