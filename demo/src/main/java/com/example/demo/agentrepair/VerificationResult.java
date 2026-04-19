package com.example.demo.agentrepair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VerificationResult {

    private final boolean passed;
    private final List<String> checks;

    public VerificationResult(boolean passed, List<String> checks) {
        this.passed = passed;
        this.checks = immutableCopy(checks);
    }

    public boolean passed() {
        return passed;
    }

    public boolean isPassed() {
        return passed;
    }

    public List<String> checks() {
        return checks;
    }

    public List<String> getChecks() {
        return checks;
    }

    private static List<String> immutableCopy(List<String> source) {
        if (source == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<String>(source));
    }
}
