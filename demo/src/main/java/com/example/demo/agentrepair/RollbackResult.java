package com.example.demo.agentrepair;

public class RollbackResult {

    private final boolean rolledBack;
    private final String reason;
    private final ServiceSnapshot restoredSnapshot;

    public RollbackResult(boolean rolledBack, String reason, ServiceSnapshot restoredSnapshot) {
        this.rolledBack = rolledBack;
        this.reason = reason;
        this.restoredSnapshot = restoredSnapshot;
    }

    public boolean rolledBack() {
        return rolledBack;
    }

    public boolean isRolledBack() {
        return rolledBack;
    }

    public String reason() {
        return reason;
    }

    public String getReason() {
        return reason;
    }

    public ServiceSnapshot restoredSnapshot() {
        return restoredSnapshot;
    }

    public ServiceSnapshot getRestoredSnapshot() {
        return restoredSnapshot;
    }
}
