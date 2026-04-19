package com.example.demo.agentrepair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RepairRunResult {

    private final String runId;
    private final Observation observation;
    private final Diagnosis diagnosis;
    private final RepairPlan repairPlan;
    private final ServiceSnapshot beforeSnapshot;
    private final ServiceSnapshot candidateSnapshot;
    private final ServiceSnapshot finalSnapshot;
    private final VerificationResult verification;
    private final RollbackResult rollback;
    private final RepairOutcome outcome;
    private final List<String> timeline;

    public RepairRunResult(
            String runId,
            Observation observation,
            Diagnosis diagnosis,
            RepairPlan repairPlan,
            ServiceSnapshot beforeSnapshot,
            ServiceSnapshot candidateSnapshot,
            ServiceSnapshot finalSnapshot,
            VerificationResult verification,
            RollbackResult rollback,
            RepairOutcome outcome,
            List<String> timeline
    ) {
        this.runId = runId;
        this.observation = observation;
        this.diagnosis = diagnosis;
        this.repairPlan = repairPlan;
        this.beforeSnapshot = beforeSnapshot;
        this.candidateSnapshot = candidateSnapshot;
        this.finalSnapshot = finalSnapshot;
        this.verification = verification;
        this.rollback = rollback;
        this.outcome = outcome;
        this.timeline = immutableCopy(timeline);
    }

    public String runId() {
        return runId;
    }

    public String getRunId() {
        return runId;
    }

    public Observation observation() {
        return observation;
    }

    public Observation getObservation() {
        return observation;
    }

    public Diagnosis diagnosis() {
        return diagnosis;
    }

    public Diagnosis getDiagnosis() {
        return diagnosis;
    }

    public RepairPlan repairPlan() {
        return repairPlan;
    }

    public RepairPlan getRepairPlan() {
        return repairPlan;
    }

    public ServiceSnapshot beforeSnapshot() {
        return beforeSnapshot;
    }

    public ServiceSnapshot getBeforeSnapshot() {
        return beforeSnapshot;
    }

    public ServiceSnapshot candidateSnapshot() {
        return candidateSnapshot;
    }

    public ServiceSnapshot getCandidateSnapshot() {
        return candidateSnapshot;
    }

    public ServiceSnapshot finalSnapshot() {
        return finalSnapshot;
    }

    public ServiceSnapshot getFinalSnapshot() {
        return finalSnapshot;
    }

    public VerificationResult verification() {
        return verification;
    }

    public VerificationResult getVerification() {
        return verification;
    }

    public RollbackResult rollback() {
        return rollback;
    }

    public RollbackResult getRollback() {
        return rollback;
    }

    public RepairOutcome outcome() {
        return outcome;
    }

    public RepairOutcome getOutcome() {
        return outcome;
    }

    public List<String> timeline() {
        return timeline;
    }

    public List<String> getTimeline() {
        return timeline;
    }

    private static List<String> immutableCopy(List<String> source) {
        if (source == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<String>(source));
    }
}
