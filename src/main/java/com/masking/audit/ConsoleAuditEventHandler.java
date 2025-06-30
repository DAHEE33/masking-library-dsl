package com.masking.audit;

public class ConsoleAuditEventHandler implements AuditEventHandler {

    @Override
    public void handle(String field, String before, String after) {
        System.out.printf("[AUDIT] field=%s, before=\"%s\", after=\"%s\"%n", field, before, after);
    }
}
