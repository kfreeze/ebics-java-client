package org.kopi.ebics.document.upload;

import java.util.ArrayList;
import java.util.List;

public class StatementRecord {
    private List<StatementEntry> entries = new ArrayList<>();

    public List<StatementEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<StatementEntry> entries) {
        this.entries = entries;
    }
}
