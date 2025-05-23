package org.kopi.ebics.document.upload;

import iso.std.iso._20022.tech.xsd.pain_008_001.SequenceType3Code;


public enum MandateType {

    ONE_OFF, RECURRENT, RECURRENT_FIRST, RECURRENT_FINAL;

    public SequenceType3Code.Enum getSepaSequenceType3Code() {
        return switch (this) {
            case ONE_OFF -> SequenceType3Code.OOFF;
            case RECURRENT -> SequenceType3Code.RCUR;
            case RECURRENT_FINAL -> SequenceType3Code.FNAL;
            case RECURRENT_FIRST -> SequenceType3Code.FRST;
        };
    }
}
