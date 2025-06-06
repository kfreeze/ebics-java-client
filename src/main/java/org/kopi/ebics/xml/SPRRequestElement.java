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

import java.util.Calendar;

import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.schema.h005.DataDigestType;
import org.kopi.ebics.schema.h005.DataEncryptionInfoType.EncryptionPubKeyDigest;
import org.kopi.ebics.schema.h005.DataTransferRequestType;
import org.kopi.ebics.schema.h005.DataTransferRequestType.DataEncryptionInfo;
import org.kopi.ebics.schema.h005.DataTransferRequestType.SignatureData;
import org.kopi.ebics.schema.h005.EbicsRequestDocument.EbicsRequest;
import org.kopi.ebics.schema.h005.EbicsRequestDocument.EbicsRequest.Body;
import org.kopi.ebics.schema.h005.EbicsRequestDocument.EbicsRequest.Header;
import org.kopi.ebics.schema.h005.MutableHeaderType;
import org.kopi.ebics.schema.h005.StandardOrderParamsType;
import org.kopi.ebics.schema.h005.StaticHeaderOrderDetailsType;
import org.kopi.ebics.schema.h005.StaticHeaderType;
import org.kopi.ebics.schema.h005.StaticHeaderType.BankPubKeyDigests;
import org.kopi.ebics.schema.h005.StaticHeaderType.BankPubKeyDigests.Authentication;
import org.kopi.ebics.schema.h005.StaticHeaderType.BankPubKeyDigests.Encryption;
import org.kopi.ebics.schema.h005.StaticHeaderType.Product;
import org.kopi.ebics.session.EbicsSession;
import org.kopi.ebics.utils.Utils;


/**
 * The <code>SPRRequestElement</code> is the request element
 * for revoking a subscriber
 *
 * @author Hachani
 */
public class SPRRequestElement extends InitializationRequestElement {

    private static final long serialVersionUID = -6742241777786111337L;
    public static final String HTTP_WWW_W_3_ORG_2001_04_XMLENC_SHA_256 = "http://www.w3.org/2001/04/xmlenc#sha256";

    /**
     * Constructs a new SPR request element.
     *
     * @param session the current ebic session.
     */
    public SPRRequestElement(EbicsSession session) throws EbicsException {
        super(session, org.kopi.ebics.session.OrderType.SPR, "SPRRequest.xml");
    }


    @Override
    public void buildInitialization() throws EbicsException {
        EbicsRequest request;
        Header header;
        Body body;
        MutableHeaderType mutable;
        StaticHeaderType xstatic;
        Product product;
        BankPubKeyDigests bankPubKeyDigests;
        Authentication authentication;
        Encryption encryption;
        DataTransferRequestType dataTransfer;
        DataEncryptionInfo dataEncryptionInfo;
        SignatureData signatureData;
        EncryptionPubKeyDigest encryptionPubKeyDigest;
        StaticHeaderOrderDetailsType orderDetails;
        StaticHeaderOrderDetailsType.AdminOrderType adminOrderType;
        StandardOrderParamsType standardOrderParamsType;
        UserSignature userSignature;
        DataDigestType dataDigest;
        userSignature = new UserSignature(session.getUser(),
                generateName("SIG"),
                session.getConfiguration().getSignatureVersion(),
                " ".getBytes());
        userSignature.build();
        userSignature.validate();

        mutable = EbicsXmlFactory.createMutableHeaderType("Initialisation", null);
        product = EbicsXmlFactory.createProduct(session.getProduct().getLanguage(),
                session.getProduct().getName(), session.getProduct().getInstituteID());
        authentication = EbicsXmlFactory.createAuthentication(session.getConfiguration().getAuthenticationVersion(),
                HTTP_WWW_W_3_ORG_2001_04_XMLENC_SHA_256,
                decodeHex(session.getUser().getPartner().getBank().getX002Digest()));
        encryption = EbicsXmlFactory.createEncryption(session.getConfiguration().getEncryptionVersion(),
                HTTP_WWW_W_3_ORG_2001_04_XMLENC_SHA_256,
                decodeHex(session.getUser().getPartner().getBank().getE002Digest()));
        bankPubKeyDigests = EbicsXmlFactory.createBankPubKeyDigests(authentication, encryption);
        adminOrderType = EbicsXmlFactory.createAdminOrderType(type.getCode());
        standardOrderParamsType = EbicsXmlFactory.createStandardOrderParamsType();
        orderDetails = EbicsXmlFactory.createStaticHeaderOrderDetailsType(null,
                adminOrderType,
                standardOrderParamsType);
        xstatic = EbicsXmlFactory.createStaticHeaderType(session.getBankID(),
                nonce,
                0,
                session.getUser().getPartner().getPartnerId(),
                product,
                session.getUser().getSecurityMedium(),
                session.getUser().getUserId(),
                Calendar.getInstance(),
                orderDetails,
                bankPubKeyDigests);
        header = EbicsXmlFactory.createEbicsRequestHeader(true, mutable, xstatic);
        encryptionPubKeyDigest = EbicsXmlFactory.createEncryptionPubKeyDigest(session.getConfiguration().getEncryptionVersion(),
                HTTP_WWW_W_3_ORG_2001_04_XMLENC_SHA_256,
                decodeHex(session.getUser().getPartner().getBank().getE002Digest()));
        signatureData = EbicsXmlFactory.createSignatureData(true, Utils.encrypt(Utils.zip(userSignature.prettyPrint()), keySpec));
        dataEncryptionInfo = EbicsXmlFactory.createDataEncryptionInfo(true,
                encryptionPubKeyDigest,
                generateTransactionKey());
        dataDigest = EbicsXmlFactory.createDataDigestType(session.getConfiguration().getSignatureVersion(), null);
        dataTransfer = EbicsXmlFactory.createDataTransferRequestType(dataEncryptionInfo, signatureData, dataDigest);
        body = EbicsXmlFactory.createEbicsRequestBody(dataTransfer);
        request = EbicsXmlFactory.createEbicsRequest(header, body);
        document = EbicsXmlFactory.createEbicsRequestDocument(request);

    }
}
