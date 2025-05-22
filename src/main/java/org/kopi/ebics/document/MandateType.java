package org.kopi.ebics.document;

import iso.std.iso._20022.tech.xsd.pain_008_001.SequenceType3Code;


public enum MandateType {

    ONE_OFF, RECURRENT, RECURRENT_FIRST, RECURRENT_FINAL;

    public SequenceType3Code getSepaSequenceType3Code() {
        switch (this) {
            case ONE_OFF:
                return SequenceType3Code.OOFF;
            case RECURRENT:
                return SequenceType3Code.RCUR;
            case RECURRENT_FINAL:
                return SequenceType3Code.FNAL;
            case RECURRENT_FIRST:
                return SequenceType3Code.FRST;
        }
        return null;
    }
}
