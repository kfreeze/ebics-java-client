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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.kopi.ebics.client.UploadService;
import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.interfaces.ContentFactory;
import org.kopi.ebics.interfaces.EbicsOrderType;
import org.kopi.ebics.io.Splitter;
import org.kopi.ebics.schema.h005.BTUParamsType;
import org.kopi.ebics.schema.h005.DataDigestType;
import org.kopi.ebics.schema.h005.DataEncryptionInfoType.EncryptionPubKeyDigest;
import org.kopi.ebics.schema.h005.DataTransferRequestType;
import org.kopi.ebics.schema.h005.DataTransferRequestType.DataEncryptionInfo;
import org.kopi.ebics.schema.h005.DataTransferRequestType.SignatureData;
import org.kopi.ebics.schema.h005.EbicsRequestDocument.EbicsRequest;
import org.kopi.ebics.schema.h005.EbicsRequestDocument.EbicsRequest.Body;
import org.kopi.ebics.schema.h005.EbicsRequestDocument.EbicsRequest.Header;
import org.kopi.ebics.schema.h005.MutableHeaderType;
import org.kopi.ebics.schema.h005.ParameterDocument.Parameter;
import org.kopi.ebics.schema.h005.ParameterDocument.Parameter.Value;
import org.kopi.ebics.schema.h005.StandardOrderParamsType;
import org.kopi.ebics.schema.h005.StaticHeaderOrderDetailsType;
import org.kopi.ebics.schema.h005.StaticHeaderType;
import org.kopi.ebics.schema.h005.StaticHeaderType.BankPubKeyDigests;
import org.kopi.ebics.schema.h005.StaticHeaderType.BankPubKeyDigests.Authentication;
import org.kopi.ebics.schema.h005.StaticHeaderType.BankPubKeyDigests.Encryption;
import org.kopi.ebics.schema.h005.StaticHeaderType.Product;
import org.kopi.ebics.session.EbicsSession;
import org.kopi.ebics.session.OrderType;
import org.kopi.ebics.utils.Utils;


/**
 * The <code>UInitializationRequestElement</code> is the common initialization
 * element for all ebics file uploads.
 *
 * @author Hachani
 */
public class UploadInitializationRequestElement extends InitializationRequestElement {


    private static final long serialVersionUID = -8083183483311283608L;
    private final byte[] userData;
    private final Splitter splitter;
    private UserSignature userSignature;

    /**
     * Constructs a new <code>UInitializationRequestElement</code> for uploads initializations.
     *
     * @param session   the current ebics session.
     * @param orderType the upload order type
     * @param userData  the user data to be uploaded
     * @throws EbicsException
     */
    public UploadInitializationRequestElement(EbicsSession session,
                                              EbicsOrderType orderType,
                                              byte[] userData)
            throws EbicsException {
        super(session, orderType, generateName(orderType));
        this.userData = userData;
        splitter = new Splitter(userData);
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
        StaticHeaderOrderDetailsType.AdminOrderType adminOrderType;
        DataDigestType dataDigest;

        userSignature = new UserSignature(session.getUser(),
                generateName("UserSignature"),
                session.getConfiguration().getSignatureVersion(),
                userData);
        userSignature.build();
        userSignature.validate();

        splitter.readInput(session.getConfiguration().isCompressionEnabled(), keySpec);

        mutable = EbicsXmlFactory.createMutableHeaderType("Initialisation", null);
        product = EbicsXmlFactory.createProduct(session.getProduct().getLanguage(),
                session.getProduct().getName(), session.getProduct().getInstituteID());
        authentication = EbicsXmlFactory.createAuthentication(session.getConfiguration().getAuthenticationVersion(),
                "http://www.w3.org/2001/04/xmlenc#sha256",
                decodeHex(session.getUser().getPartner().getBank().getX002Digest()));
        encryption = EbicsXmlFactory.createEncryption(session.getConfiguration().getEncryptionVersion(),
                "http://www.w3.org/2001/04/xmlenc#sha256",
                decodeHex(session.getUser().getPartner().getBank().getE002Digest()));
        bankPubKeyDigests = EbicsXmlFactory.createBankPubKeyDigests(authentication, encryption);
        adminOrderType = EbicsXmlFactory.createAdminOrderType(type.getCode());

        String nextOrderId = session.getUser().getPartner().nextOrderId();

        StaticHeaderOrderDetailsType orderDetails;
        if (type == OrderType.BTU) {
            UploadService uploadService = new UploadService("SDD", "COR", "pain.008");
            BTUParamsType btuParamsType = EbicsXmlFactory.createBTUParamsType(uploadService);

            List<Parameter> parameters = new ArrayList<>();
            if (Boolean.parseBoolean(session.getSessionParam("TEST"))) {
                Value value = EbicsXmlFactory.createValue("String", "TRUE");
                Parameter parameter = EbicsXmlFactory.createParameter("TEST", value);
                parameters.add(parameter);
            }

            if (Boolean.parseBoolean(session.getSessionParam("EBCDIC"))) {
                Value value = EbicsXmlFactory.createValue("String", "TRUE");
                Parameter parameter = EbicsXmlFactory.createParameter("EBCDIC", value);
                parameters.add(parameter);
            }

            if (!parameters.isEmpty()) {
                btuParamsType.setParameterArray(parameters.toArray(new Parameter[parameters.size()]));
            }
            orderDetails = EbicsXmlFactory.createStaticHeaderOrderDetailsType(null, adminOrderType, btuParamsType);
        } else {
            StandardOrderParamsType standardOrderParamsType = EbicsXmlFactory.createStandardOrderParamsType();
            orderDetails = EbicsXmlFactory.createStaticHeaderOrderDetailsType(nextOrderId, adminOrderType,
                    standardOrderParamsType);
        }

        xstatic = EbicsXmlFactory.createStaticHeaderType(session.getBankID(),
                nonce,
                splitter.getSegmentNumber(),
                session.getUser().getPartner().getPartnerId(),
                product,
                session.getUser().getSecurityMedium(),
                session.getUser().getUserId(),
                Calendar.getInstance(),
                orderDetails,
                bankPubKeyDigests);
//        header = EbicsXmlFactory.createEbicsRequestHeader(true, mutable, xstatic);
//        encryptionPubKeyDigest = EbicsXmlFactory.createEncryptionPubKeyDigest(session.getConfiguration().getEncryptionVersion(),
//                "http://www.w3.org/2001/04/xmlenc#sha256",
//                decodeHex(session.getUser().getPartner().getBank().getE002Digest()));
//        signatureData = EbicsXmlFactory.createSignatureData(true, Utils.encrypt(Utils.zip(userSignature.prettyPrint()), keySpec));
//
//        dataEncryptionInfo = EbicsXmlFactory.createDataEncryptionInfo(true,
//                encryptionPubKeyDigest,
//                generateTransactionKey());
//
        dataDigest = EbicsXmlFactory.createDataDigestType(session.getConfiguration().getSignatureVersion(), null);
//        dataTransfer = EbicsXmlFactory.createDataTransferRequestType(dataEncryptionInfo, signatureData, dataDigest);
//        body = EbicsXmlFactory.createEbicsRequestBody(dataTransfer);
//        request = EbicsXmlFactory.createEbicsRequest(header, body);
//        document = EbicsXmlFactory.createEbicsRequestDocument(request);

        header = EbicsXmlFactory.createEbicsRequestHeader(true, mutable, xstatic);
        encryptionPubKeyDigest = EbicsXmlFactory.createEncryptionPubKeyDigest(session.getConfiguration().getEncryptionVersion(),
                "http://www.w3.org/2001/04/xmlenc#sha256",
                decodeHex(session.getUser().getPartner().getBank().getE002Digest()));
        signatureData = EbicsXmlFactory.createSignatureData(true, Utils.encrypt(Utils.zip(userSignature.prettyPrint()), keySpec));
        dataEncryptionInfo = EbicsXmlFactory.createDataEncryptionInfo(true,
                encryptionPubKeyDigest,
                generateTransactionKey());
        dataTransfer = EbicsXmlFactory.createDataTransferRequestType(dataEncryptionInfo, signatureData, dataDigest);
        body = EbicsXmlFactory.createEbicsRequestBody(dataTransfer);
        request = EbicsXmlFactory.createEbicsRequest(header, body);
        document = EbicsXmlFactory.createEbicsRequestDocument(request);
    }


    @Override
    public byte[] toByteArray() {
        setSaveSuggestedPrefixes("urn:org:ebics:H005", "");

        return super.toByteArray();
    }

    /**
     * Returns the user signature data.
     *
     * @return the user signature data.
     */
    public UserSignature getUserSignature() {
        return userSignature;
    }

    /**
     * Returns the content of a given segment.
     *
     * @param segment the segment number
     * @return the content of the given segment
     */
    public ContentFactory getContent(int segment) {
        return splitter.getContent(segment);
    }

    /**
     * Returns the total segment number.
     *
     * @return the total segment number.
     */
    public int getSegmentNumber() {
        return splitter.getSegmentNumber();
    }
}
