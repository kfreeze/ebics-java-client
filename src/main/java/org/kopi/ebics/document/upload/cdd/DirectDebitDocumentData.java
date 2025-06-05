package org.kopi.ebics.document.upload.cdd;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.kopi.ebics.document.upload.MandateType;
import org.kopi.ebics.document.upload.SepaStringSanitizer;
import org.kopi.ebics.document.upload.SepaUtil;
import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.exception.SepaValidationException;

/**
 * This class encapsulates the business data required to create a SEPA Direct Debit XML document.
 * It contains metadata about the creditor as well as a list of {@link DirectDebitPayment} entries.
 * <p>
 * Provides validation, grouping, and aggregation methods for SEPA-compliant transactions.
 */
public class DirectDebitDocumentData {

    /**
     * List of direct debit payments contained in the document.
     */
    private final List<DirectDebitPayment> payments = new ArrayList<>();

    private String creditorBic;
    private String creditorIban;
    private String creditorIdentifier;
    private String creditorName;
    private String documentMessageId;

    /**
     * Default constructor.
     */
    public DirectDebitDocumentData() {
    }

    /**
     * Constructs a new document instance with all required creditor details.
     *
     * @param creditorBic                the BIC of the creditor bank
     * @param creditorIban               the IBAN of the creditor account
     * @param creditorCreditorIdentifier the SEPA creditor identifier
     * @param creditorName               the name of the creditor
     * @param documentMessageId          the document message identifier
     * @throws SepaValidationException if BIC or IBAN are invalid
     */
    public DirectDebitDocumentData(String creditorBic, String creditorIban, String creditorCreditorIdentifier,
                                   String creditorName, String documentMessageId) throws SepaValidationException {
        setCreditorBic(creditorBic);
        setCreditorIban(creditorIban);
        setCreditorIdentifier(creditorCreditorIdentifier);
        setCreditorName(creditorName);
        setDocumentMessageId(documentMessageId);
    }

    /**
     * Converts this document to a SEPA-compliant XML format.
     *
     * @return XML string representation
     * @throws EbicsException if conversion fails
     */
    public String toXml() throws EbicsException {
        return DirectDebitDocumentBuilder.toXml(this);
    }

    /**
     * Returns the due date of the first payment with the given mandate type.
     *
     * @param mandateType the mandate type
     * @return the due date, or null if no payment with the type exists
     */
    public Date getDueDateByMandateType(MandateType mandateType) {
        for (DirectDebitPayment p : payments) {
            if (mandateType.equals(p.getMandateType())) {
                return p.getDirectDebitDueDate();
            }
        }
        return null;
    }

    /**
     * Returns the number of payments matching a specific mandate type.
     *
     * @param mt the mandate type
     * @return number of payments of that type
     */
    public int getNumberOfPaymentsByMandateType(MandateType mt) {
        int result = 0;
        for (DirectDebitPayment ignored : getPaymentsByMandateType(mt)) {
            result++;
        }
        return result;
    }

    /**
     * Returns the total payment sum for all payments of a given mandate type.
     *
     * @param mt the mandate type
     * @return total sum of payments
     */
    public BigDecimal getTotalPaymentSumOfPaymentsByMandateType(MandateType mt) {
        BigDecimal result = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (DirectDebitPayment p : getPaymentsByMandateType(mt)) {
            result = result.add(p.getPaymentSum());
        }
        return result;
    }

    /**
     * Validates a payment against the existing payments of the same mandate type.
     *
     * @param p the payment to validate
     * @throws EbicsException if validation fails
     */
    private void validatePayment(DirectDebitPayment p) throws EbicsException {
        p.validate();

        Date currentDueDate = getDueDateByMandateType(p.getMandateType());
        if (currentDueDate != null && !Objects.equals(p.getDirectDebitDueDate(), currentDueDate)) {
            throw new EbicsException("Due date of direct debit payment must be equal to other direct debit payments of the same mandate and direct debit type.");
        }
    }

    /**
     * Returns all payments of a specific mandate type.
     *
     * @param mandateType the mandate type
     * @return iterable of payments with the specified type
     */
    Iterable<DirectDebitPayment> getPaymentsByMandateType(MandateType mandateType) {
        List<DirectDebitPayment> result = new ArrayList<>();
        for (DirectDebitPayment p : payments) {
            if (mandateType.equals(p.getMandateType())) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * Returns the total sum of all payments in this document.
     *
     * @return total payment amount
     */
    public BigDecimal getTotalPaymentSum() {
        BigDecimal result = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (DirectDebitPayment p : payments) {
            result = result.add(p.getPaymentSum());
        }
        return result;
    }

    /**
     * Adds a new payment to the document after validating it.
     *
     * @param payment the payment to add
     * @throws EbicsException if validation fails
     */
    public void addPayment(DirectDebitPayment payment) throws EbicsException {
        validatePayment(payment);
        payments.add(payment);
    }

    /** @return the BIC of the creditor */
    public String getCreditorBic() {
        return creditorBic;
    }

    /**
     * Sets the creditor BIC after validating it.
     *
     * @param creditorBic the BIC
     * @throws SepaValidationException if the BIC is invalid
     */
    public final void setCreditorBic(String creditorBic) throws SepaValidationException {
        SepaUtil.validateBic(creditorBic);
        this.creditorBic = creditorBic;
    }

    /** @return the IBAN of the creditor */
    public String getCreditorIban() {
        return creditorIban;
    }

    /**
     * Sets the creditor IBAN after validating it.
     *
     * @param creditorIban the IBAN
     * @throws SepaValidationException if the IBAN is invalid
     */
    public final void setCreditorIban(String creditorIban) throws SepaValidationException {
        SepaUtil.validateIban(creditorIban);
        this.creditorIban = creditorIban;
    }

    /** @return the SEPA creditor identifier */
    public String getCreditorIdentifier() {
        return creditorIdentifier;
    }

    /**
     * Sets the SEPA creditor identifier.
     *
     * @param creditorCreditorIdentifier the identifier
     */
    public final void setCreditorIdentifier(String creditorCreditorIdentifier) {
        this.creditorIdentifier = creditorCreditorIdentifier;
    }

    /** @return the name of the creditor */
    public String getCreditorName() {
        return creditorName;
    }

    /**
     * Sets the name of the creditor, sanitized to SEPA-compliant format.
     *
     * @param creditorName the creditor name
     */
    public final void setCreditorName(String creditorName) {
        this.creditorName = SepaStringSanitizer.of(creditorName).withMaxLength(70).sanitize();
    }

    /** @return the message ID of the document */
    public String getDocumentMessageId() {
        return documentMessageId;
    }

    /**
     * Sets the message ID of the document, sanitized to SEPA-compliant format.
     *
     * @param documentMessageId the message ID
     */
    public final void setDocumentMessageId(String documentMessageId) {
        this.documentMessageId = SepaStringSanitizer.of(documentMessageId).withMaxLength(35).sanitize();
    }

    /**
     * Returns the list of direct debit payments contained in this document.
     *
     * @return list of payments
     */
    public List<DirectDebitPayment> getPayments() {
        return payments;
    }
}
