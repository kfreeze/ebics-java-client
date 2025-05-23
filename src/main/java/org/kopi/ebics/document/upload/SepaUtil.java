/*
 *  All rights reserved.
 */
package org.kopi.ebics.document.upload;

import org.iban4j.BicUtil;
import org.iban4j.IbanFormatException;
import org.iban4j.IbanUtil;
import org.iban4j.InvalidCheckDigitException;
import org.iban4j.UnsupportedCountryException;
import org.kopi.ebics.exception.SepaValidationException;

/**
 * Utility class providing validation methods for SEPA-related identifiers
 * such as IBAN and BIC using the iban4j library.
 * <p>
 * This class cannot be instantiated.
 */
public class SepaUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private SepaUtil() {
        // Prevent instantiation of utility class
    }

    /**
     * Validates the given IBAN string using iban4j's {@link IbanUtil}.
     *
     * @param iban the IBAN string to validate
     * @throws SepaValidationException if the IBAN is invalid, e.g. due to formatting,
     *                                 incorrect check digits, or unsupported country
     */
    public static void validateIban(String iban) throws SepaValidationException {
        try {
            // Validates IBAN format, check digits, and country
            IbanUtil.validate(iban);
        } catch (IbanFormatException | InvalidCheckDigitException | UnsupportedCountryException e) {
            // Wrap library-specific exceptions into custom exception
            throw new SepaValidationException("Invalid IBAN " + iban, e);
        }
    }

    /**
     * Validates the given BIC string using iban4j's {@link BicUtil}.
     *
     * @param bic the BIC string to validate
     * @throws SepaValidationException if the BIC is invalid, e.g. due to formatting,
     *                                 incorrect check digits, or unsupported country
     */
    public static void validateBic(String bic) throws SepaValidationException {
        try {
            // Validates BIC format and structure
            BicUtil.validate(bic);
        } catch (IbanFormatException | InvalidCheckDigitException | UnsupportedCountryException e) {
            // Wrap library-specific exceptions into custom exception
            throw new SepaValidationException("Invalid BIC " + bic, e);
        }
    }
}
