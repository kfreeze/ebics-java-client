package org.kopi.ebics.document.upload.cdd;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.kopi.ebics.document.upload.MandateType;
import org.kopi.ebics.document.upload.SepaStringSanitizer;
import org.kopi.ebics.document.upload.SepaUtil;
import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.exception.SepaValidationException;

/**
 * Represents a single SEPA direct debit payment within a SEPA Direct Debit Document.
 * Each payment contains debtor information, mandate details, amount and execution date.
 */
public class DirectDebitPayment {
    private BigDecimal paymentSum;

    private String debitorIban;

    private String debitorBic;

    private String debitorName;

    private String reasonForPayment;

    private String mandateId;

    private Date mandateDate;

    private Date directDebitDueDate;

    private MandateType mandateType;

    /**
     * Default constructor.
     */
    public DirectDebitPayment() {
    }

    /**
     * Full constructor for initializing a direct debit payment.
     *
     * @param paymentSum         the payment amount (will be scaled to 2 decimal places)
     * @param debitorIban        IBAN of the debtor (validated)
     * @param debitorBic         BIC of the debtor bank (validated)
     * @param debitorName        name of the debtor (sanitized)
     * @param reasonForPayment   purpose of the payment (sanitized)
     * @param mandateId          mandate identifier
     * @param mandateDate        date of the mandate signature
     * @param directDebitDueDate due date for the debit execution
     * @param mandateType        type of mandate (FRST, RCUR, OOFF, FNAL)
     */
    public DirectDebitPayment(BigDecimal paymentSum, String debitorIban, String debitorBic, String debitorName,
                              String reasonForPayment, String mandateId, Date mandateDate,
                              Date directDebitDueDate, MandateType mandateType) {
        setPaymentSum(paymentSum);
        this.debitorIban = debitorIban;
        this.debitorBic = debitorBic;
        this.debitorName = debitorName;
        this.reasonForPayment = reasonForPayment;
        this.mandateId = mandateId;
        this.mandateDate = mandateDate;
        this.directDebitDueDate = directDebitDueDate;
        this.mandateType = mandateType;
    }

    /**
     * Validates the state of this payment instance.
     *
     * @throws EbicsException if the due date is missing
     */
    public void validate() throws EbicsException {
        if (directDebitDueDate == null) {
            throw new EbicsException("Due date missing.");
        }
    }

    /**
     * @return the amount to be debited
     */
    public BigDecimal getPaymentSum() {
        return paymentSum;
    }

    /**
     * Sets the payment amount and enforces 2 decimal precision.
     *
     * @param paymentSum the payment sum
     */
    public void setPaymentSum(BigDecimal paymentSum) {
        this.paymentSum = paymentSum.setScale(2, RoundingMode.HALF_UP);
    }

    /** @return the debtor IBAN */
    public String getDebitorIban() {
        return debitorIban;
    }

    /**
     * Sets the debtor's IBAN after validating it.
     *
     * @param debitorIban the IBAN
     * @throws SepaValidationException if the IBAN is invalid
     */
    public void setDebitorIban(String debitorIban) throws SepaValidationException {
        SepaUtil.validateIban(debitorIban);
        this.debitorIban = debitorIban;
    }

    /** @return the debtor BIC */
    public String getDebitorBic() {
        return debitorBic;
    }

    /**
     * Sets the debtor's BIC after validating it.
     *
     * @param debitorBic the BIC
     * @throws SepaValidationException if the BIC is invalid
     */
    public void setDebitorBic(String debitorBic) throws SepaValidationException {
        SepaUtil.validateBic(debitorBic);
        this.debitorBic = debitorBic;
    }

    /** @return the name of the debtor */
    public String getDebitorName() {
        return debitorName;
    }

    /**
     * Sets the name of the debtor, sanitized to SEPA-compatible format.
     *
     * @param debitorName the name
     */
    public void setDebitorName(String debitorName) {
        this.debitorName = SepaStringSanitizer.of(debitorName).withMaxLength(70).sanitize();
    }

    /** @return the reason for payment */
    public String getReasonForPayment() {
        return reasonForPayment;
    }

    /**
     * Sets the reason for payment, sanitized for SEPA compatibility.
     *
     * @param reasonForPayment purpose of payment
     */
    public void setReasonForPayment(String reasonForPayment) {
        this.reasonForPayment = SepaStringSanitizer.of(reasonForPayment).sanitize();
    }

    /** @return the mandate identifier */
    public String getMandateId() {
        return mandateId;
    }

    /**
     * Sets the mandate ID.
     *
     * @param mandateId the mandate identifier
     */
    public void setMandateId(String mandateId) {
        this.mandateId = mandateId;
    }

    /** @return the date of mandate signature */
    public Date getMandateDate() {
        return mandateDate;
    }

    /**
     * Sets the date the mandate was signed.
     *
     * @param mandateDate the signature date
     */
    public void setMandateDate(Date mandateDate) {
        this.mandateDate = mandateDate;
    }

    /** @return the execution due date of the direct debit */
    public Date getDirectDebitDueDate() {
        return directDebitDueDate;
    }

    /**
     * Sets the date on which the direct debit is to be executed.
     *
     * @param directDebitDueDate the due date
     */
    public void setDirectDebitDueDate(Date directDebitDueDate) {
        this.directDebitDueDate = directDebitDueDate;
    }

    /** @return the type of mandate (FRST, RCUR, etc.) */
    public MandateType getMandateType() {
        return mandateType;
    }

    /**
     * Sets the mandate type for this payment.
     *
     * @param mandateType the mandate type
     */
    public void setMandateType(MandateType mandateType) {
        this.mandateType = mandateType;
    }

}
