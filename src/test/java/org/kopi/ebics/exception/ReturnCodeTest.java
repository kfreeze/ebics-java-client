package org.kopi.ebics.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReturnCodeTest {
    @Test
    void test() {
        assertEquals(0, ReturnCode.EBICS_OK.getCode());
        assertEquals("EBICS_OK", ReturnCode.EBICS_OK.getSymbolicName());
    }
}
