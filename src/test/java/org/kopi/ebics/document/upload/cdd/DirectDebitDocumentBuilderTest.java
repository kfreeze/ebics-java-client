package org.kopi.ebics.document.upload.cdd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

import iso.std.iso._20022.tech.xsd.pain_008_001.DirectDebitTransactionInformation23;
import iso.std.iso._20022.tech.xsd.pain_008_001.Document;
import iso.std.iso._20022.tech.xsd.pain_008_001.DocumentDocument;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentInstruction29;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;
import org.kopi.ebics.document.upload.MandateType;
import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.exception.SepaValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class DirectDebitDocumentBuilderTest {

    @Test
    void testGetDocument() throws EbicsException {
        File file = new File("src/test/resources/xml/pain.008.001.08.xml");
        try (InputStream xmlInput = new FileInputStream(file)) {
            assertNotNull(xmlInput, "XML file not found");

            // Parse XML
            DocumentDocument parent = DocumentDocument.Factory.parse(xmlInput);
            Document doc = parent.getDocument();

            // -- Header assertions --
            var hdr = doc.getCstmrDrctDbtInitn().getGrpHdr();
            assertEquals("12345", hdr.getMsgId());
            assertEquals("2025-06-06T13:38:55.349+02:00", hdr.getCreDtTm().toString());
            assertEquals("9", hdr.getNbOfTxs());
            assertEquals(new BigDecimal("903.76"), hdr.getCtrlSum());
            assertEquals("Hans Mustermann", hdr.getInitgPty().getNm());

            // -- Payment Information blocks --
            PaymentInstruction29[] pmtInfArray = doc.getCstmrDrctDbtInitn().getPmtInfArray();
            assertEquals(4, pmtInfArray.length);

            for (PaymentInstruction29 pmtInf : pmtInfArray) {
                assertEquals("12345", pmtInf.getPmtInfId());
                assertEquals("DD", pmtInf.getPmtMtd().toString());
                assertTrue(pmtInf.getBtchBookg());
                assertNotNull(pmtInf.getNbOfTxs());
                assertNotNull(pmtInf.getCtrlSum());
                assertEquals("SEPA", pmtInf.getPmtTpInf().getSvcLvlList().getFirst().getCd());
                assertEquals("CORE", pmtInf.getPmtTpInf().getLclInstrm().getCd());
                assertNotNull(pmtInf.getPmtTpInf().getSeqTp());
                assertEquals("Hans Mustermann", pmtInf.getCdtr().getNm());
                assertEquals("DE89370400440532013000", pmtInf.getCdtrAcct().getId().getIBAN());
                assertEquals("MALADE51NWD", pmtInf.getCdtrAgt().getFinInstnId().getBICFI());
                assertEquals("SLEV", pmtInf.getChrgBr().toString());

                for (DirectDebitTransactionInformation23 tx : pmtInf.getDrctDbtTxInfArray()) {
                    assertEquals("mandate001", tx.getPmtId().getEndToEndId());
                    assertTrue(tx.getInstdAmt().getBigDecimalValue().compareTo(BigDecimal.ZERO) > 0);
                    assertEquals("EUR", tx.getInstdAmt().getCcy());

                    // Mandate related
                    assertEquals("mandate001", tx.getDrctDbtTx().getMndtRltdInf().getMndtId());
                    assertTrue(tx.getDrctDbtTx().getMndtRltdInf().getDtOfSgntr().toString().startsWith("2025-06-06"));

                    // Creditor Scheme ID
                    assertEquals("DE98ZZZ09999999999", tx.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthrList().getFirst().getId());
                    assertEquals("SEPA", tx.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthrList().getFirst().getSchmeNm().getPrtry());

                    // Debtor
                    assertTrue(tx.getDbtr().getNm().startsWith("Arme Wurst") || tx.getDbtr().getNm().startsWith("Loooooong"));
                    assertEquals("DE89370400440532013000", tx.getDbtrAcct().getId().getIBAN());
                    assertEquals("MALADE51NWD", tx.getDbtrAgt().getFinInstnId().getBICFI());

                    // Remittance
                    assertEquals("test- berweisung", tx.getRmtInf().getUstrdArray(0));
                }
            }
        } catch (IOException | XmlException e) {
            throw new EbicsException("Could not parse XML file", e);
        }

    }

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
