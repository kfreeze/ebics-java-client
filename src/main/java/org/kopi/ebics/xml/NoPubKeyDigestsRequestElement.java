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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Calendar;

import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.schema.h005.EbicsNoPubKeyDigestsRequestDocument;
import org.kopi.ebics.schema.h005.EbicsNoPubKeyDigestsRequestDocument.EbicsNoPubKeyDigestsRequest;
import org.kopi.ebics.schema.h005.EbicsNoPubKeyDigestsRequestDocument.EbicsNoPubKeyDigestsRequest.Body;
import org.kopi.ebics.schema.h005.EbicsNoPubKeyDigestsRequestDocument.EbicsNoPubKeyDigestsRequest.Header;
import org.kopi.ebics.schema.h005.EmptyMutableHeaderType;
import org.kopi.ebics.schema.h005.NoPubKeyDigestsRequestStaticHeaderType;
import org.kopi.ebics.schema.h005.OrderDetailsType;
import org.kopi.ebics.schema.h005.ProductElementType;
import org.kopi.ebics.schema.xmldsig.SignatureType;
import org.kopi.ebics.session.EbicsSession;
import org.kopi.ebics.session.OrderType;
import org.kopi.ebics.utils.Utils;

/**
 * The <code>NoPubKeyDigestsRequestElement</code> is the root element
 * for a HPB ebics server request.
 *
 * @author hachani
 */
public class NoPubKeyDigestsRequestElement extends DefaultEbicsRootElement {

    private static final long serialVersionUID = 3177047145408329472L;

    /**
     * Construct a new No Public Key Digests Request element.
     *
     * @param session the current ebics session.
     */
    public NoPubKeyDigestsRequestElement(EbicsSession session) {
        super(session);
    }

    /**
     * Returns the digest value of the authenticated XML portions.
     *
     * @return the digest value.
     * @throws EbicsException Failed to retrieve the digest value.
     */
    public byte[] getDigest() throws EbicsException {
        addNamespaceDecl("ds", "http://www.w3.org/2000/09/xmldsig#");

        try {
            return MessageDigest.getInstance("SHA-256", "BC").digest(Utils.canonize(toByteArray()));
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new EbicsException(e.getMessage(), e);
        }
    }

    /**
     * Sets the authentication signature of the <code>NoPubKeyDigestsRequestElement</code>
     *
     * @param authSignature the the authentication signature.
     */
    public void setAuthSignature(SignatureType authSignature) {
        ((EbicsNoPubKeyDigestsRequestDocument) document).getEbicsNoPubKeyDigestsRequest().setAuthSignature(authSignature);
    }

    /**
     * Sets the signature value of the request.
     *
     * @param signature the signature value
     */
    public void setSignatureValue(byte[] signature) {
        ((EbicsNoPubKeyDigestsRequestDocument) document).getEbicsNoPubKeyDigestsRequest().getAuthSignature().setSignatureValue(EbicsXmlFactory.createSignatureValueType(signature));
    }

    @Override
    public void build() throws EbicsException {
        EbicsNoPubKeyDigestsRequest request;
        Body body;
        Header header;
        EmptyMutableHeaderType mutable;
        NoPubKeyDigestsRequestStaticHeaderType xstatic;
        ProductElementType product;
        OrderDetailsType orderDetails;

        product = EbicsXmlFactory.createProductElementType(session.getProduct().getLanguage(),
                session.getProduct().getName(), session.getProduct().getInstituteID());
        orderDetails = EbicsXmlFactory.createOrderDetailsType(OrderType.HPB.getCode());
        xstatic = EbicsXmlFactory.createNoPubKeyDigestsRequestStaticHeaderType(session.getBankID(),
                Utils.generateNonce(),
                Calendar.getInstance(),
                session.getUser().getPartner().getPartnerId(),
                session.getUser().getUserId(),
                product,
                orderDetails,
                session.getUser().getSecurityMedium());
        mutable = EbicsXmlFactory.createEmptyMutableHeaderType();
        header = EbicsXmlFactory.createDigestsRequestHeader(true, mutable, xstatic);
        body = EbicsXmlFactory.createDigestsRequestBody();
        request = EbicsXmlFactory.createEbicsNoPubKeyDigestsRequest(header, body);
        document = EbicsXmlFactory.createEbicsNoPubKeyDigestsRequestDocument(request);
    }

    @Override
    public byte[] toByteArray() {
        setSaveSuggestedPrefixes("http://www.w3.org/2000/09/xmldsig#", "ds");
        setSaveSuggestedPrefixes("urn:org:ebics:H005", "");

        return super.toByteArray();
    }


    @Override
    public String getName() {
        return "NoPubKeyDigestsRequest.xml";
    }
}
