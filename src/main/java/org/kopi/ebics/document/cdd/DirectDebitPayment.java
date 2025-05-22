package org.kopi.ebics.document.cdd;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.kopi.ebics.document.MandateType;
import org.kopi.ebics.document.SepaStringSanitizer;
import org.kopi.ebics.document.SepaUtil;
import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.exception.SepaValidationException;

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

    public DirectDebitPayment() {
    }

    public DirectDebitPayment(BigDecimal paymentSum, String debitorIban, String debitorBic, String debitorName,
                              String reasonForPayment, String mandateId, Date mandateDate, Date directDebitDueDate,
                              MandateType mandateType) {
        this.paymentSum = paymentSum;
        this.debitorIban = debitorIban;
        this.debitorBic = debitorBic;
        this.debitorName = debitorName;
        this.reasonForPayment = reasonForPayment;
        this.mandateId = mandateId;
        this.mandateDate = mandateDate;
        this.directDebitDueDate = directDebitDueDate;
        this.mandateType = mandateType;
    }

    public void validate() throws EbicsException {
        if (directDebitDueDate == null) {
            throw new EbicsException("Due date missing.");
        }
    }

    public BigDecimal getPaymentSum() {
        return paymentSum;
    }

    public void setPaymentSum(BigDecimal paymentSum) {
        this.paymentSum = paymentSum.setScale(2, RoundingMode.HALF_UP);
    }

    public String getDebitorIban() {
        return debitorIban;
    }

    public void setDebitorIban(String debitorIban) throws SepaValidationException {
        SepaUtil.validateIban(debitorIban);
        this.debitorIban = debitorIban;
    }

    public String getDebitorBic() {
        return debitorBic;
    }

    public void setDebitorBic(String debitorBic) throws SepaValidationException {
        SepaUtil.validateBic(debitorBic);
        this.debitorBic = debitorBic;
    }

    public String getDebitorName() {
        return debitorName;
    }

    public void setDebitorName(String debitorName) {
        this.debitorName = SepaStringSanitizer.of(debitorName).withMaxLength(70).sanitize();
    }

    public String getReasonForPayment() {
        return reasonForPayment;
    }

    public void setReasonForPayment(String reasonForPayment) {
        this.reasonForPayment = SepaStringSanitizer.of(reasonForPayment).sanitize();
    }

    public String getMandateId() {
        return mandateId;
    }

    public void setMandateId(String mandateId) {
        this.mandateId = mandateId;
    }

    public Date getMandateDate() {
        return mandateDate;
    }

    public void setMandateDate(Date mandateDate) {
        this.mandateDate = mandateDate;
    }

    public Date getDirectDebitDueDate() {
        return directDebitDueDate;
    }

    public void setDirectDebitDueDate(Date directDebitDueDate) {
        this.directDebitDueDate = directDebitDueDate;
    }

    public MandateType getMandateType() {
        return mandateType;
    }

    public void setMandateType(MandateType mandateType) {
        this.mandateType = mandateType;
    }

}
