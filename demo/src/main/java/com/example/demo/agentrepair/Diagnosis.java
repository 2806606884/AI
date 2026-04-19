package com.example.demo.agentrepair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Diagnosis {

    private final IncidentType incidentType;
    private final Severity severity;
    private final String rootCause;
    private final double confidence;
    private final List<String> evidence;

    public Diagnosis(IncidentType incidentType, Severity severity, String rootCause, double confidence, List<String> evidence) {
        this.incidentType = incidentType;
        this.severity = severity;
        this.rootCause = rootCause;
        this.confidence = confidence;
        this.evidence = immutableCopy(evidence);
    }

    public IncidentType incidentType() {
        return incidentType;
    }

    public IncidentType getIncidentType() {
        return incidentType;
    }

    public Severity severity() {
        return severity;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String rootCause() {
        return rootCause;
    }

    public String getRootCause() {
        return rootCause;
    }

    public double confidence() {
        return confidence;
    }

    public double getConfidence() {
        return confidence;
    }

    public List<String> evidence() {
        return evidence;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    private static List<String> immutableCopy(List<String> source) {
        if (source == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<String>(source));
    }
}
