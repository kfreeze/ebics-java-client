package org.kopi.ebics.document.cdd;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.kopi.ebics.document.MandateType;
import org.kopi.ebics.document.SepaStringSanitizer;
import org.kopi.ebics.document.SepaUtil;
import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.exception.SepaValidationException;

public class DirectDebitDocumentData {

    private String creditorBic;

    private String creditorIban;

    private String creditorIdentifier;

    private String creditorName;

    private String documentMessageId;

    private final List<DirectDebitPayment> payments = new ArrayList<>();

    public String toXml() throws EbicsException {
        return DirectDebitDocumentBuilder.toXml(this);
    }

    public DirectDebitDocumentData() {
    }

    public DirectDebitDocumentData(String creditorBic, String creditorIban, String creditorCreditorIdentifier, String creditorName, String documentMessageId) throws SepaValidationException {
        setCreditorBic(creditorBic);
        setCreditorIban(creditorIban);
        setCreditorIdentifier(creditorCreditorIdentifier);
        setCreditorName(creditorName);
        setDocumentMessageId(documentMessageId);
    }

    public Date getDueDateByMandateType(MandateType mandateType) {
        for (DirectDebitPayment p : payments) {
            if (mandateType.equals(p.getMandateType())) {
                return p.getDirectDebitDueDate();
            }
        }
        return null;
    }

    public int getNumberOfPaymentsByMandateType(MandateType mt) {
        int result = 0;
        for (DirectDebitPayment p : getPaymentsByMandateType(mt)) {
            result++;
        }
        return result;
    }

    public BigDecimal getTotalPaymentSumOfPaymentsByMandateType(MandateType mt) {
        BigDecimal result = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);
        for (DirectDebitPayment p : getPaymentsByMandateType(mt)) {
            result = result.add(p.getPaymentSum());
        }
        return result;
    }

    private void validatePayment(DirectDebitPayment p) throws EbicsException {
        p.validate();

        Date currentDueDate = getDueDateByMandateType(p.getMandateType());
        if (currentDueDate != null && !Objects.equals(p.getDirectDebitDueDate(), currentDueDate)) {
            throw new EbicsException("Due date of direct debit payment must be equal to other direct debit payments of the same mandate and direct debit type.");
        }
    }

    Iterable<DirectDebitPayment> getPaymentsByMandateType(MandateType mandateType) {
        List<DirectDebitPayment> result = new ArrayList<>();
        for (DirectDebitPayment p : payments) {
            if (mandateType.equals(p.getMandateType())) {
                result.add(p);
            }
        }
        return result;
    }

    public BigDecimal getTotalPaymentSum() {
        BigDecimal result = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);
        for (DirectDebitPayment p : payments) {
            result = result.add(p.getPaymentSum());
        }
        return result;
    }

    public void addPayment(DirectDebitPayment payment) throws EbicsException {
        validatePayment(payment);
        payments.add(payment);
    }

    public String getCreditorBic() {
        return creditorBic;
    }

    public final void setCreditorBic(String creditorBic) throws SepaValidationException {
        SepaUtil.validateBic(creditorBic);
        this.creditorBic = creditorBic;
    }

    public String getCreditorIban() {
        return creditorIban;
    }

    public final void setCreditorIban(String creditorIban) throws SepaValidationException {
        SepaUtil.validateIban(creditorIban);
        this.creditorIban = creditorIban;
    }


    public String getCreditorIdentifier() {
        return creditorIdentifier;
    }

    public final void setCreditorIdentifier(String creditorCreditorIdentifier) {
        this.creditorIdentifier = creditorCreditorIdentifier;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public final void setCreditorName(String creditorName) {
        this.creditorName = SepaStringSanitizer.of(creditorName).withMaxLength(70).sanitize();
    }

    public String getDocumentMessageId() {
        return documentMessageId;
    }

    public final void setDocumentMessageId(String documentMessageId) {
        this.documentMessageId = SepaStringSanitizer.of(documentMessageId).withMaxLength(35).sanitize();
    }

    public List<DirectDebitPayment> getPayments() {
        return payments;
    }
}
