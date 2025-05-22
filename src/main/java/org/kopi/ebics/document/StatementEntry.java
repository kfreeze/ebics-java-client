package org.kopi.ebics.document;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class StatementEntry {

    public enum DebitIdentifier {
        CREDIT,
        DEBIT,
        CANCELLATION_DEBIT,
        CANCELLATION_CREDIT
    }

    private LocalDate valueDate;
    private DebitIdentifier debitIdentifier;
    private BigDecimal amount;
    private String multipurposeField;
    private String accountDesignation;

    public StatementEntry() {
        //empty constructor
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("At ").append(valueDate);
        if (accountDesignation != null) {
            sb.append(" (");
            sb.append(accountDesignation);
            sb.append(")");
        }
        sb.append(", ");
        sb.append(debitIdentifier);
        sb.append(": ");
        if (amount != null) {
            // Set scale to 2 digits and round if necessary, then to plain string for nicer output.
            sb.append(amount.setScale(2, RoundingMode.HALF_EVEN).toPlainString());
        } else {
            sb.append("-");
        }
        sb.append(" for ");
        sb.append(multipurposeField);
        return sb.toString();
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public DebitIdentifier getDebitIdentifier() {
        return debitIdentifier;
    }

    public void setDebitIdentifier(DebitIdentifier debitIdentifier) {
        this.debitIdentifier = debitIdentifier;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMultipurposeField() {
        return multipurposeField;
    }

    public void setMultipurposeField(String multipurposeField) {
        this.multipurposeField = multipurposeField;
    }

    public void addToMultipurposeField(String string) {
        if (multipurposeField == null || multipurposeField.trim().isEmpty()) {
            multipurposeField = string;
        } else {
            multipurposeField += " ";
            multipurposeField += string;
        }
    }

    public String getAccountDesignation() {
        return accountDesignation;
    }

    public void setAccountDesignation(String accountDesignation) {
        this.accountDesignation = accountDesignation;
    }
}
