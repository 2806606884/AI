package com.example.demo.agentrepair;

public class RepairPlan {

    private final RepairAction action;
    private final String target;
    private final String fromValue;
    private final String toValue;
    private final boolean approvalRequired;
    private final String guardrail;

    public RepairPlan(RepairAction action, String target, String fromValue, String toValue, boolean approvalRequired, String guardrail) {
        this.action = action;
        this.target = target;
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.approvalRequired = approvalRequired;
        this.guardrail = guardrail;
    }

    public RepairAction action() {
        return action;
    }

    public RepairAction getAction() {
        return action;
    }

    public String target() {
        return target;
    }

    public String getTarget() {
        return target;
    }

    public String fromValue() {
        return fromValue;
    }

    public String getFromValue() {
        return fromValue;
    }

    public String toValue() {
        return toValue;
    }

    public String getToValue() {
        return toValue;
    }

    public boolean approvalRequired() {
        return approvalRequired;
    }

    public boolean isApprovalRequired() {
        return approvalRequired;
    }

    public String guardrail() {
        return guardrail;
    }

    public String getGuardrail() {
        return guardrail;
    }
}
