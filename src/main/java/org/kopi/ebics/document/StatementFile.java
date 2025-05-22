package org.kopi.ebics.document;

import java.util.ArrayList;
import java.util.List;

public class StatementFile {
    private List<StatementRecord> records = new ArrayList<>();

    public List<StatementRecord> getRecords() {
        return records;
    }

    public void setRecords(List<StatementRecord> records) {
        this.records = records;
    }

    List<StatementEntry> getEntries() {
        List<StatementEntry> statementEntries = new ArrayList<>();
        for (StatementRecord statementRecord : getRecords()) {
            statementEntries.addAll(statementRecord.getEntries());
        }
        return statementEntries;
    }
}
