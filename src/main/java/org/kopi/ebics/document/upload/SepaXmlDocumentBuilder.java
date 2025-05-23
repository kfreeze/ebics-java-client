package org.kopi.ebics.document.upload;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.kopi.ebics.exception.EbicsException;

public abstract class SepaXmlDocumentBuilder implements Serializable {

    protected static Calendar getCalender(Calendar cal) {
        Calendar result = new GregorianCalendar();
        result.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
        return result;
    }

    protected static XMLGregorianCalendar dateToXmlGregorianCalendarDate(Date d) throws EbicsException {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
        return calendarToXmlGregorianCalendarDate(cal);
    }

    private static XMLGregorianCalendar calendarToXmlGregorianCalendarDate(GregorianCalendar d) throws EbicsException {
        XMLGregorianCalendar result;
        try {
            result = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(d.get(Calendar.YEAR),
                    d.get(Calendar.MONTH) + 1, d.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException ex) {
            throw new EbicsException("Could not convert GregorianCalendar to XMLGregorianCalendar", ex);
        }
        return result;
    }

}
