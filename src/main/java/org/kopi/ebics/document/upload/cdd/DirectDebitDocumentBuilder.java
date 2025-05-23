package org.kopi.ebics.document.upload.cdd;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import iso.std.iso._20022.tech.xsd.pain_008_001.AccountIdentification4Choice;
import iso.std.iso._20022.tech.xsd.pain_008_001.ActiveOrHistoricCurrencyAndAmount;
import iso.std.iso._20022.tech.xsd.pain_008_001.BranchAndFinancialInstitutionIdentification6;
import iso.std.iso._20022.tech.xsd.pain_008_001.CashAccount38;
import iso.std.iso._20022.tech.xsd.pain_008_001.ChargeBearerType1Code;
import iso.std.iso._20022.tech.xsd.pain_008_001.CustomerDirectDebitInitiationV08;
import iso.std.iso._20022.tech.xsd.pain_008_001.DirectDebitTransaction10;
import iso.std.iso._20022.tech.xsd.pain_008_001.DirectDebitTransactionInformation23;
import iso.std.iso._20022.tech.xsd.pain_008_001.Document;
import iso.std.iso._20022.tech.xsd.pain_008_001.FinancialInstitutionIdentification18;
import iso.std.iso._20022.tech.xsd.pain_008_001.GenericPersonIdentification1;
import iso.std.iso._20022.tech.xsd.pain_008_001.GroupHeader83;
import iso.std.iso._20022.tech.xsd.pain_008_001.LocalInstrument2Choice;
import iso.std.iso._20022.tech.xsd.pain_008_001.MandateRelatedInformation14;
import iso.std.iso._20022.tech.xsd.pain_008_001.Party38Choice;
import iso.std.iso._20022.tech.xsd.pain_008_001.PartyIdentification135;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentIdentification6;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentInstruction29;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentMethod2Code;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentTypeInformation29;
import iso.std.iso._20022.tech.xsd.pain_008_001.PersonIdentification13;
import iso.std.iso._20022.tech.xsd.pain_008_001.PersonIdentificationSchemeName1Choice;
import iso.std.iso._20022.tech.xsd.pain_008_001.RemittanceInformation16;
import iso.std.iso._20022.tech.xsd.pain_008_001.ServiceLevel8Choice;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.kopi.ebics.document.upload.MandateType;
import org.kopi.ebics.document.upload.SepaXmlDocumentBuilder;
import org.kopi.ebics.exception.EbicsException;


public class DirectDebitDocumentBuilder extends SepaXmlDocumentBuilder {

    public static String toXml(DirectDebitDocumentData ddd) throws EbicsException {
        //sepa xml document
        Document doc = Document.Factory.newInstance();

        // CustomerDirectDebitInitiationV08
        CustomerDirectDebitInitiationV08 cddiv = CustomerDirectDebitInitiationV08.Factory.newInstance();
        doc.setCstmrDrctDbtInitn(cddiv);

        //group header
        cddiv.setGrpHdr(createGroupHeaderSdd(ddd));

        cddiv.getPmtInfList().addAll(createPaymentInstructions(ddd));

        StringWriter resultWriter = new StringWriter();

        try {
            JAXBContext jaxbCtx = JAXBContext.newInstance(Document.class);
            Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(doc, resultWriter);
            return resultWriter.toString();
        } catch (JAXBException e) {
            // If something crashes here it needs to be fixed in the library, not by the user.
            throw new EbicsException("could not marshal java object to XML", e);
        }
    }

    private static List<PaymentInstruction29> createPaymentInstructions(DirectDebitDocumentData ddd) throws EbicsException {
        List<PaymentInstruction29> result = new ArrayList<>();
        for (MandateType mandateType : MandateType.values()) {
            if (ddd.getNumberOfPaymentsByMandateType(mandateType) > 0) {
                result.add(createPaymentInstructionInformation(ddd, mandateType));
            }
        }
        return result;
    }

    private static PaymentInstruction29 createPaymentInstructionInformation(DirectDebitDocumentData ddd, MandateType mandateType) throws EbicsException {

        PaymentInstruction29 result = PaymentInstruction29.Factory.newInstance();
        // payment information id
        result.setPmtInfId(ddd.getDocumentMessageId());
        // payment method (fixed)
        result.setPmtMtd(PaymentMethod2Code.DD);
        // batch booking (fixed)
        result.setBtchBookg(Boolean.TRUE);

        // number of transactions
        result.setNbOfTxs(String.valueOf(ddd.getNumberOfPaymentsByMandateType(mandateType)));
        // control sum
        result.setCtrlSum(ddd.getTotalPaymentSumOfPaymentsByMandateType(mandateType));
        // payment type information
        result.setPmtTpInf(createPaymentTypeInformation(mandateType));

        // requested collection due date
        result.setReqdColltnDt(dateToXmlGregorianCalendarDate(ddd.getDueDateByMandateType(mandateType)).toGregorianCalendar());

        // creditor name
        result.setCdtr(PartyIdentification135.Factory.newInstance());
        result.getCdtr().setNm(ddd.getCreditorName());

        // creditor iban
        result.setCdtrAcct(ibanToCashAccountSepa1(ddd.getCreditorIban()));

        // creditor agt(?)
        result.setCdtrAgt(bicToBranchAndFinancialInstitutionIdentification(ddd.getCreditorBic()));

        // whatever, fixed
        result.setChrgBr(ChargeBearerType1Code.SLEV);

        // single payment transactions ... yay!
        result.getDrctDbtTxInfList().addAll(createDirectDebitTransactionInformationBlocks(ddd, mandateType));

        return result;
    }

    private static CashAccount38 ibanToCashAccountSepa1(String iban) {
        CashAccount38 result = CashAccount38.Factory.newInstance();
        result.setId(AccountIdentification4Choice.Factory.newInstance());
        result.getId().setIBAN(iban);
        return result;
    }

    private static Collection<DirectDebitTransactionInformation23> createDirectDebitTransactionInformationBlocks(DirectDebitDocumentData ddd, MandateType mandateType) throws EbicsException {
        List<DirectDebitTransactionInformation23> result = new ArrayList<>();

        for (DirectDebitPayment p : ddd.getPaymentsByMandateType(mandateType)) {
            result.add(createDirectDebitTransaction(ddd, p));
        }

        return result;
    }

    private static DirectDebitTransactionInformation23 createDirectDebitTransaction(DirectDebitDocumentData ddd, DirectDebitPayment p) throws EbicsException {
        DirectDebitTransactionInformation23 result = DirectDebitTransactionInformation23.Factory.newInstance();
        // mandate id
        result.setPmtId(PaymentIdentification6.Factory.newInstance());
        result.getPmtId().setEndToEndId(p.getMandateId());

        // currency and amount
        result.setInstdAmt(ActiveOrHistoricCurrencyAndAmount.Factory.newInstance());
        result.getInstdAmt().setCcy("EUR");
        result.getInstdAmt().setBigDecimalValue(p.getPaymentSum());

        // transaction information
        result.setDrctDbtTx(createDirectDebitTransaction(p, ddd));
        result.setDrctDbtTx(createDirectDebitTransaction(p, ddd));

        // debitor bic
        result.setDbtrAgt(bicToBranchAndFinancialInstitutionIdentification(p.getDebitorBic()));

        // debitor name
        result.setDbtr(PartyIdentification135.Factory.newInstance());
        result.getDbtr().setNm(p.getDebitorName());

        // debitor iban
        result.setDbtrAcct(CashAccount38.Factory.newInstance());
        result.getDbtrAcct().setId(AccountIdentification4Choice.Factory.newInstance());
        result.getDbtrAcct().getId().setIBAN(p.getDebitorIban());

        // reson of payment
        result.setRmtInf(RemittanceInformation16.Factory.newInstance());
        result.getRmtInf().getUstrdList().add(p.getReasonForPayment());

        return result;
    }

    private static DirectDebitTransaction10 createDirectDebitTransaction(DirectDebitPayment p, DirectDebitDocumentData ddd) throws EbicsException {
        DirectDebitTransaction10 result = DirectDebitTransaction10.Factory.newInstance();
        // mandate related info
        result.setMndtRltdInf(MandateRelatedInformation14.Factory.newInstance());

        // Erforderlich, wenn das Mandat seit letzten SEPA Lastschrift Einreichung geändert wurde.
        // In diesem Fall ist das Feld mit "TRUE" zu belegen, ansonsten bleibt es leer.
        // Relevanz für folgende Mandatsänderungen: Gläubiger ID, Gläubiger Name, Bankverbindung des Zahlungspflichtigen, Mandat ID
        // -- we'll leave it empty for now and see what happens
        // tx.getMndtRltdInf().setAmdmntInd(Boolean.FALSE);
        result.getMndtRltdInf().setMndtId(p.getMandateId());
        result.getMndtRltdInf().setDtOfSgntr(dateToXmlGregorianCalendarDate(p.getMandateDate()).toGregorianCalendar());

        // creditor related info
        result.setCdtrSchmeId(PartyIdentification135.Factory.newInstance());
        result.getCdtrSchmeId().setId(Party38Choice.Factory.newInstance());
        result.getCdtrSchmeId().getId().setPrvtId(PersonIdentification13.Factory.newInstance());

        // person identification - (creditor identifier)
        GenericPersonIdentification1 inf = GenericPersonIdentification1.Factory.newInstance();
        result.getCdtrSchmeId().getId().getPrvtId().getOthrList().add(inf);
        inf.setId(ddd.getCreditorIdentifier());

        // whatever, fixed to SEPA
        inf.setSchmeNm(PersonIdentificationSchemeName1Choice.Factory.newInstance());
        inf.getSchmeNm().setPrtry("SEPA");

        return result;
    }

    private static PaymentTypeInformation29 createPaymentTypeInformation(MandateType mandateType) {
        PaymentTypeInformation29 paymentType = PaymentTypeInformation29.Factory.newInstance();
        ServiceLevel8Choice serviceLevel8Choice = ServiceLevel8Choice.Factory.newInstance();
        serviceLevel8Choice.setCd("SEPA");
        paymentType.getSvcLvlList().add(serviceLevel8Choice);
        paymentType.setLclInstrm(LocalInstrument2Choice.Factory.newInstance());
        paymentType.getLclInstrm().setCd("CORE");
        paymentType.setSeqTp(mandateType.getSepaSequenceType3Code());
        return paymentType;
    }


    private static GroupHeader83 createGroupHeaderSdd(DirectDebitDocumentData ddd) {
        GroupHeader83 result = GroupHeader83.Factory.newInstance();
        // message id
        result.setMsgId(ddd.getDocumentMessageId());

        // created on
        result.setCreDtTm(getCalender(Calendar.getInstance()));

        // number of tx
        result.setNbOfTxs(String.valueOf(ddd.getPayments().size()));

        // control sum
        result.setCtrlSum(ddd.getTotalPaymentSum());

        // creditor name
        PartyIdentification135 pi = PartyIdentification135.Factory.newInstance();
        pi.setNm(ddd.getCreditorName());

        result.setInitgPty(pi);

        return result;
    }

    protected static BranchAndFinancialInstitutionIdentification6 bicToBranchAndFinancialInstitutionIdentification(String bic) {
        BranchAndFinancialInstitutionIdentification6 result = BranchAndFinancialInstitutionIdentification6.Factory.newInstance();
        result.setFinInstnId(FinancialInstitutionIdentification18.Factory.newInstance());
        result.getFinInstnId().setBICFI(bic);
        return result;
    }
}
