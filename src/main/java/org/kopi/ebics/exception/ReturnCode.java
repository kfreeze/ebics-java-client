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

package org.kopi.ebics.exception;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.kopi.ebics.messages.Messages;


/**
 * Representation of EBICS return codes.
 * The return codes are described in chapter 13
 * of EBICS specification.
 *
 * @author hachani
 */
public class ReturnCode implements Serializable {

    public static final ReturnCode EBICS_OK;
    public static final ReturnCode EBICS_DOWNLOAD_POSTPROCESS_DONE;
    public static final ReturnCode EBICS_DOWNLOAD_POSTPROCESS_SKIPPED;
    public static final ReturnCode EBICS_TX_SEGMENT_NUMBER_UNDERRUN;
    public static final ReturnCode EBICS_ORDER_PARAMS_IGNORED;
    public static final ReturnCode EBICS_AUTHENTICATION_FAILED;
    public static final ReturnCode EBICS_INVALID_REQUEST;
    public static final ReturnCode EBICS_INTERNAL_ERROR;
    public static final ReturnCode EBICS_TX_RECOVERY_SYNC;
    public static final ReturnCode EBICS_INVALID_USER_OR_USER_STATE;


    public static final ReturnCode EBICS_USER_UNKNOWN;
    public static final ReturnCode EBICS_INVALID_USER_STATE;
    public static final ReturnCode EBICS_INVALID_ORDER_IDENTIFIER;
    public static final ReturnCode EBICS_UNSUPPORTED_ORDER_IDENTIFIER;
    public static final ReturnCode EBICS_BANK_PUBKEY_UPDATE_REQUIRED;
    public static final ReturnCode EBICS_SEGMENT_SIZE_EXCEEDED;
    public static final ReturnCode EBICS_INVALID_XML;
    public static final ReturnCode EBICS_INVALID_HOST_ID;
    public static final ReturnCode EBICS_TX_UNKNOWN_TXID;
    public static final ReturnCode EBICS_TX_ABORT;
    public static final ReturnCode EBICS_TX_MESSAGE_REPLAY;
    public static final ReturnCode EBICS_TX_SEGMENT_NUMBER_EXCEEDED;
    public static final ReturnCode EBICS_INVALID_ORDER_PARAMS;
    public static final ReturnCode EBICS_INVALID_REQUEST_CONTENT;
    public static final ReturnCode EBICS_MAX_ORDER_DATA_SIZE_EXCEEDED;
    public static final ReturnCode EBICS_MAX_SEGMENTS_EXCEEDED;
    public static final ReturnCode EBICS_MAX_TRANSACTIONS_EXCEEDED;
    public static final ReturnCode EBICS_PARTNER_ID_MISMATCH;
    public static final ReturnCode EBICS_ORDER_ALREADY_EXISTS;
    public static final ReturnCode EBICS_NO_ONLINE_CHECKS;
    public static final ReturnCode EBICS_DOWNLOAD_SIGNED_ONLY;
    public static final ReturnCode EBICS_DOWNLOAD_UNSIGNED_ONLY;
    public static final ReturnCode EBICS_AUTHORISATION_ORDER_IDENTIFIERFAILED;
    public static final ReturnCode EBICS_INVALID_ORDER_DATA_FORMAT;
    public static final ReturnCode EBICS_NO_DOWNLOAD_DATA_AVAILABLE;
    public static final ReturnCode EBICS_UNSUPPORTED_REQUEST_FOR_ORDER_INSTANCE;
    public static final ReturnCode EBICS_RECOVERY_NOT_SUPPORTED;
    public static final ReturnCode EBICS_INVALID_SIGNATURE_FILE_FORMAT;
    public static final ReturnCode EBICS_ORDERID_UNKNOWN;
    public static final ReturnCode EBICS_ORDERID_ALREADY_FINAL;
    public static final ReturnCode EBICS_PROCESSING_ERROR;
    public static final ReturnCode EBICS_KEYMGMT_UNSUPPORTED_VERSION_SIGNATURE;
    public static final ReturnCode EBICS_KEYMGMT_UNSUPPORTED_VERSION_AUTHENTICATION;
    public static final ReturnCode EBICS_KEYMGMT_UNSUPPORTED_VERSION_ENCRYPTION;
    public static final ReturnCode EBICS_KEYMGMT_KEYLENGTH_ERROR_SIGNATURE;
    public static final ReturnCode EBICS_KEYMGMT_KEYLENGTH_ERROR_AUTHENTICATION;
    public static final ReturnCode EBICS_KEYMGMT_KEYLENGTH_ERROR_ENCRYPTION;
    public static final ReturnCode EBICS_X509_CERTIFICATE_EXPIRED;
    public static final ReturnCode EBICS_X509_CERTIFICATE_NOT_VALID_YET;
    public static final ReturnCode EBICS_X509_WRONG_KEY_USAGE;
    public static final ReturnCode EBICS_X509_WRONG_ALGORITHM;
    public static final ReturnCode EBICS_X509_CTL_INVALID;
    public static final ReturnCode EBICS_X509_UNKNOWN_CERTIFICATE_AUTHORITY;
    public static final ReturnCode EBICS_X509_INVALID_POLICY;
    public static final ReturnCode EBICS_X509_INVALID_BASIC_CONSTRAINTS;
    public static final ReturnCode EBICS_ONLY_X509_SUPPORT;
    public static final ReturnCode EBICS_KEYMGMT_DUPLICATE_KEY;
    public static final ReturnCode EBICS_CERTIFICATE_VALIDATION_ERROR;
    public static final ReturnCode EBICS_SIGNATURE_VERIFICATION_FAILED;
    public static final ReturnCode EBICS_ACCOUNT_AUTHORISATION_FAILED;
    public static final ReturnCode EBICS_AMOUNT_CHECK_FAILED;
    public static final ReturnCode EBICS_SIGNER_UNKNOWN;
    public static final ReturnCode EBICS_INVALID_SIGNER_STATE;
    public static final ReturnCode EBICS_DUPLICATE_SIGNATURE;
    private static final String BUNDLE_NAME = "org.kopi.ebics.exception.messages";
    private static final long serialVersionUID = -497883146384363199L;
    private static final Map<String, ReturnCode> returnCodes = new HashMap<>();
    private static final Messages messages = new Messages(BUNDLE_NAME);

    static {
        EBICS_OK = create("000000", "EBICS_OK");
        EBICS_DOWNLOAD_POSTPROCESS_DONE = create("011000", "EBICS_DOWNLOAD_POSTPROCESS_DONE");
        EBICS_DOWNLOAD_POSTPROCESS_SKIPPED = create("011001", "EBICS_DOWNLOAD_POSTPROCESS_SKIPPED");
        EBICS_TX_SEGMENT_NUMBER_UNDERRUN = create("011101", "EBICS_TX_SEGMENT_NUMBER_UNDERRUN");
        EBICS_ORDER_PARAMS_IGNORED = create("031001", "EBICS_ORDER_PARAMS_IGNORED");
        EBICS_AUTHENTICATION_FAILED = create("061001", "EBICS_AUTHENTICATION_FAILED");
        EBICS_INVALID_REQUEST = create("061002", "EBICS_INVALID_REQUEST");
        EBICS_INTERNAL_ERROR = create("061099", "EBICS_INTERNAL_ERROR");
        EBICS_TX_RECOVERY_SYNC = create("061101", "EBICS_TX_RECOVERY_SYNC");
        EBICS_INVALID_USER_OR_USER_STATE = create("091002", "EBICS_INVALID_USER_OR_USER_STATE");
        EBICS_USER_UNKNOWN = create("091003", "EBICS_USER_UNKNOWN");
        EBICS_INVALID_USER_STATE = create("091004", "EBICS_INVALID_USER_STATE");
        EBICS_INVALID_ORDER_IDENTIFIER = create("091005", "EBICS_INVALID_ORDER_IDENTIFIER");
        EBICS_UNSUPPORTED_ORDER_IDENTIFIER = create("091006", "EBICS_UNSUPPORTED_ORDER_IDENTIFIER");
        EBICS_BANK_PUBKEY_UPDATE_REQUIRED = create("091008", "EBICS_BANK_PUBKEY_UPDATE_REQUIRED");
        EBICS_SEGMENT_SIZE_EXCEEDED = create("091009", "EBICS_SEGMENT_SIZE_EXCEEDED");
        EBICS_INVALID_XML = create("091010", "EBICS_INVALID_XML");
        EBICS_INVALID_HOST_ID = create("091011", "EBICS_INVALID_HOST_ID");
        EBICS_TX_UNKNOWN_TXID = create("091101", "EBICS_TX_UNKNOWN_TXID");
        EBICS_TX_ABORT = create("091102", "EBICS_TX_ABORT");
        EBICS_TX_MESSAGE_REPLAY = create("091103", "EBICS_TX_MESSAGE_REPLAY");
        EBICS_TX_SEGMENT_NUMBER_EXCEEDED = create("091104", "EBICS_TX_SEGMENT_NUMBER_EXCEEDED");
        EBICS_INVALID_ORDER_PARAMS = create("091112", "EBICS_INVALID_ORDER_PARAMS");
        EBICS_INVALID_REQUEST_CONTENT = create("091113", "EBICS_INVALID_REQUEST_CONTENT");
        EBICS_MAX_ORDER_DATA_SIZE_EXCEEDED = create("091117", "EBICS_MAX_ORDER_DATA_SIZE_EXCEEDED");
        EBICS_MAX_SEGMENTS_EXCEEDED = create("091118", "EBICS_MAX_SEGMENTS_EXCEEDED");
        EBICS_MAX_TRANSACTIONS_EXCEEDED = create("091119", "EBICS_MAX_TRANSACTIONS_EXCEEDED");
        EBICS_PARTNER_ID_MISMATCH = create("091120", "EBICS_PARTNER_ID_MISMATCH");
        EBICS_ORDER_ALREADY_EXISTS = create("091122", "EBICS_ORDER_ALREADY_EXISTS");
        EBICS_NO_ONLINE_CHECKS = create("013001", "EBICS_NO_ONLINE_CHECKS");
        EBICS_DOWNLOAD_SIGNED_ONLY = create("090001", "EBICS_DOWNLOAD_SIGNED_ONLY");
        EBICS_DOWNLOAD_UNSIGNED_ONLY = create("090002", "EBICS_DOWNLOAD_UNSIGNED_ONLY");
        EBICS_AUTHORISATION_ORDER_IDENTIFIERFAILED = create("090003", "EBICS_AUTHORISATION_ORDER_IDENTIFIERFAILED");
        EBICS_INVALID_ORDER_DATA_FORMAT = create("090004", "EBICS_INVALID_ORDER_DATA_FORMAT");
        EBICS_NO_DOWNLOAD_DATA_AVAILABLE = create("090005", "EBICS_NO_DOWNLOAD_DATA_AVAILABLE");
        EBICS_UNSUPPORTED_REQUEST_FOR_ORDER_INSTANCE = create("090006", "EBICS_UNSUPPORTED_REQUEST_FOR_ORDER_INSTANCE");
        EBICS_RECOVERY_NOT_SUPPORTED = create("091105", "EBICS_RECOVERY_NOT_SUPPORTED");
        EBICS_INVALID_SIGNATURE_FILE_FORMAT = create("091111", "EBICS_INVALID_SIGNATURE_FILE_FORMAT");
        EBICS_ORDERID_UNKNOWN = create("091114", "EBICS_ORDERID_UNKNOWN");
        EBICS_ORDERID_ALREADY_FINAL = create("091115", "EBICS_ORDERID_ALREADY_FINAL");
        EBICS_PROCESSING_ERROR = create("091116", "EBICS_PROCESSING_ERROR");
        EBICS_KEYMGMT_UNSUPPORTED_VERSION_SIGNATURE = create("091201", "EBICS_KEYMGMT_UNSUPPORTED_VERSION_SIGNATURE");
        EBICS_KEYMGMT_UNSUPPORTED_VERSION_AUTHENTICATION = create("091202", "EBICS_KEYMGMT_UNSUPPORTED_VERSION_AUTHENTICATION");
        EBICS_KEYMGMT_UNSUPPORTED_VERSION_ENCRYPTION = create("091203", "EBICS_KEYMGMT_UNSUPPORTED_VERSION_ENCRYPTION");
        EBICS_KEYMGMT_KEYLENGTH_ERROR_SIGNATURE = create("091204", "EBICS_KEYMGMT_KEYLENGTH_ERROR_SIGNATURE");
        EBICS_KEYMGMT_KEYLENGTH_ERROR_AUTHENTICATION = create("091205", "EBICS_KEYMGMT_KEYLENGTH_ERROR_AUTHENTICATION");
        EBICS_KEYMGMT_KEYLENGTH_ERROR_ENCRYPTION = create("091206", "EBICS_KEYMGMT_KEYLENGTH_ERROR_ENCRYPTION");
        EBICS_X509_CERTIFICATE_EXPIRED = create("091208", "EBICS_X509_CERTIFICATE_EXPIRED");
        EBICS_X509_CERTIFICATE_NOT_VALID_YET = create("091209", "EBICS_X509_CERTIFICATE_NOT_VALID_YET");
        EBICS_X509_WRONG_KEY_USAGE = create("091210", "EBICS_X509_WRONG_KEY_USAGE");
        EBICS_X509_WRONG_ALGORITHM = create("091211", "EBICS_X509_WRONG_ALGORITHM");
        EBICS_X509_CTL_INVALID = create("091213", "EBICS_X509_CTL_INVALID");
        EBICS_X509_UNKNOWN_CERTIFICATE_AUTHORITY = create("091214", "EBICS_X509_UNKNOWN_CERTIFICATE_AUTHORITY");
        EBICS_X509_INVALID_POLICY = create("091215", "EBICS_X509_INVALID_POLICY");
        EBICS_X509_INVALID_BASIC_CONSTRAINTS = create("091216", "EBICS_X509_INVALID_BASIC_CONSTRAINTS");
        EBICS_ONLY_X509_SUPPORT = create("091217", "EBICS_ONLY_X509_SUPPORT");
        EBICS_KEYMGMT_DUPLICATE_KEY = create("091218", "EBICS_KEYMGMT_DUPLICATE_KEY");
        EBICS_CERTIFICATE_VALIDATION_ERROR = create("091219", "EBICS_CERTIFICATE_VALIDATION_ERROR");
        EBICS_SIGNATURE_VERIFICATION_FAILED = create("091301", "EBICS_SIGNATURE_VERIFICATION_FAILED");
        EBICS_ACCOUNT_AUTHORISATION_FAILED = create("091302", "EBICS_ACCOUNT_AUTHORISATION_FAILED");
        EBICS_AMOUNT_CHECK_FAILED = create("091303", "EBICS_AMOUNT_CHECK_FAILED");
        EBICS_SIGNER_UNKNOWN = create("091304", "EBICS_SIGNER_UNKNOWN");
        EBICS_INVALID_SIGNER_STATE = create("091305", "EBICS_INVALID_SIGNER_STATE");
        EBICS_DUPLICATE_SIGNATURE = create("091306", "EBICS_DUPLICATE_SIGNATURE");
    }

    private final String code;
    private final String symbolicName;
    private final String text;

    /**
     * Constructs a new <code>ReturnCode</code> with a given
     * standard code, symbolic name and text
     *
     * @param code         the given standard code.
     * @param symbolicName the symbolic name.
     * @param text         the code text
     */
    public ReturnCode(String code, String symbolicName, String text) {
        this.code = code;
        this.symbolicName = symbolicName;
        this.text = text;
    }

    /**
     * Returns the equivalent <code>ReturnCode</code> of a given code
     *
     * @param code the given code
     * @param text the given code text
     * @return the equivalent <code>ReturnCode</code>
     */
    public static ReturnCode toReturnCode(String code, String text) {
        ReturnCode returnCode = returnCodes.get(code);
        if (returnCode != null) {
            return returnCode;
        }
        return new ReturnCode(code, text, text);
    }

    private static ReturnCode create(String code, String symbolicName) {
        String text = messages.getString(code);
        if (text == null) {
            throw new NullPointerException("No text for code: " + code);
        }
        ReturnCode returnCode = new ReturnCode(code, symbolicName, text);
        ReturnCode prev = returnCodes.put(code, returnCode);
        if (prev != null) {
            throw new IllegalStateException("Duplicated code: " + code);
        }
        return returnCode;
    }

    /**
     * Throws an equivalent <code>EbicsException</code>
     *
     * @throws EbicsException
     */
    public void throwException() throws EbicsException {
        throw new EbicsException(this, text);
    }

    /**
     * Tells if the return code is an OK one.
     *
     * @return True if the return code is OK one.
     */
    public boolean isOk() {
        return equals(EBICS_OK);
    }

    /**
     * Returns a slightly more human readable version of this return code.
     *
     * @return a slightly more human readable version of this return code.
     */
    public String getSymbolicName() {
        return symbolicName;
    }

    /**
     * Returns a display text for the default locale.
     *
     * @return a text that can be displayed.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the code.
     *
     * @return the code.
     */
    public int getCode() {
        return Integer.parseInt(code);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ReturnCode returnCode) {
            return this.code.equals(returnCode.code);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return code + " " + symbolicName + " " + text;
    }
}
