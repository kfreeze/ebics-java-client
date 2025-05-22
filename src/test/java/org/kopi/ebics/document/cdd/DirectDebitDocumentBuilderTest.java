package org.kopi.ebics.document.cdd;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.kopi.ebics.document.MandateType;
import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.exception.SepaValidationException;

import static org.junit.jupiter.api.Assertions.*;


class DirectDebitDocumentBuilderTest {
    @Test
    void testToXml() throws SepaValidationException, EbicsException {
        DirectDebitDocumentData ddd = new DirectDebitDocumentData("MALADE51NWD", "DE89370400440532013000", "DE98ZZZ09999999999", "Hans Mustermann", "12345");

        Calendar dueDate = Calendar.getInstance();
        dueDate.set(Calendar.HOUR, 0);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.SECOND, 0);
        dueDate.add(Calendar.DATE, 14);

        for (MandateType mt : MandateType.values()) {
            ddd.addPayment(createTestPayment(mt, "123.4539", "Arme Wurst", "MALADE51NWD", "DE89370400440532013000", dueDate));
            ddd.addPayment(createTestPayment(mt, "99.9930", "Arme Wurst2", "MALADE51NWD", "DE89370400440532013000", dueDate));
        }
        ddd.addPayment(createTestPayment(MandateType.RECURRENT, "10", "Loooooong Loooooong Loooooong Loooooong Loooooong Loooooong Loooooong Name", "MALADE51NWD", "DE89370400440532013000", dueDate));

        String result = DirectDebitDocumentBuilder.toXml(ddd);
        assertTrue(result.contains("<InstdAmt Ccy=\"EUR\">123.45</InstdAmt>"));
        assertTrue(result.contains("<InstdAmt Ccy=\"EUR\">99.99</InstdAmt>"));
        assertTrue(result.contains("<CtrlSum>903.76</CtrlSum>"));
        assertTrue(result.contains("DE98ZZZ09999999999"));
        assertTrue(result.contains("DE89370400440532013000"));
        assertTrue(result.contains("Arme Wurst2"));
        assertTrue(result.contains("Hans Mustermann"));
        assertTrue(result.contains("test- berweisung"));
        assertTrue(result.contains("Loooooong Loooooong Loooooong Loooooong Loooooong Loooooong Loooooong "));
        assertFalse(result.contains("Loooooong Loooooong Loooooong Loooooong Loooooong Loooooong Loooooong N"));
    }

    private DirectDebitPayment createTestPayment(MandateType mt, String sum, String debitorName, String bic, String iban, Calendar dueDate) throws SepaValidationException {
        DirectDebitPayment result = new DirectDebitPayment();

        result.setDirectDebitDueDate(dueDate.getTime());
        result.setMandateDate(Calendar.getInstance().getTime());
        result.setMandateId("mandate001");
        result.setMandateType(mt);
        result.setDebitorBic(bic);
        result.setDebitorIban(iban);
        result.setDebitorName(debitorName);
        result.setPaymentSum(new BigDecimal(sum));
        result.setReasonForPayment("test-Überweisung");
        return result;
    }
}
