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

package org.kopi.ebics.certificate;

/**
 * X509 certificate constants
 *
 * @author hachani
 */
public class X509Constants {

    private X509Constants() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Certificates key usage
     */
    public static final int SIGNATURE_KEY_USAGE = 1;
    public static final int AUTHENTICATION_KEY_USAGE = 2;
    public static final int ENCRYPTION_KEY_USAGE = 3;

    /**
     * Certificate signature algorithm
     */
    public static final String SIGNATURE_ALGORITHM = "SHA256WithRSAEncryption";

    /**
     * Default days validity of a certificate
     */
    public static final int DEFAULT_DURATION = 10 * 365;

    /**
     * EBICS key size
     */
    public static final int EBICS_KEY_SIZE = 2048;
}
