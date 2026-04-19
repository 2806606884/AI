package com.example.demo.agentrepair;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class AgentRepairService {

    private final RepairPolicy policy;

    public AgentRepairService() {
        this(RepairPolicy.demoDefaults());
    }

    AgentRepairService(RepairPolicy policy) {
        this.policy = policy;
    }

    public RepairRunResult run(ServiceSnapshot snapshot) {
        snapshot.validate();

        List<String> timeline = new ArrayList<String>();
        String runId = "repair-" + UUID.randomUUID().toString();

        Observation observation = observe(snapshot);
        timeline.add("observed:" + joinSignals(observation.signals()));

        Diagnosis diagnosis = diagnose(snapshot, observation);
        timeline.add("diagnosed:" + diagnosis.incidentType() + ":" + diagnosis.rootCause());

        RepairPlan plan = plan(snapshot, diagnosis);
        timeline.add("planned:" + plan.action() + ":" + plan.fromValue() + "->" + plan.toValue());

        if (plan.action() == RepairAction.NOOP) {
            VerificationResult verification = verify(snapshot);
            RollbackResult rollback = new RollbackResult(false, "service is already healthy", snapshot);
            timeline.add("verified:" + verification.passed());
            return new RepairRunResult(
                    runId,
                    observation,
                    diagnosis,
                    plan,
                    snapshot,
                    snapshot,
                    snapshot,
                    verification,
                    rollback,
                    RepairOutcome.NO_ACTION,
                    immutableCopy(timeline)
            );
        }

        if (!isPlanAllowed(snapshot, plan)) {
            VerificationResult verification = verify(snapshot);
            RollbackResult rollback = new RollbackResult(false, "repair plan rejected by guardrail", snapshot);
            timeline.add("rejected:" + plan.guardrail());
            return new RepairRunResult(
                    runId,
                    observation,
                    diagnosis,
                    plan,
                    snapshot,
                    snapshot,
                    snapshot,
                    verification,
                    rollback,
                    RepairOutcome.REJECTED,
                    immutableCopy(timeline)
            );
        }

        ServiceSnapshot candidate = apply(snapshot, plan);
        timeline.add("applied:" + plan.action());

        VerificationResult verification = verify(candidate);
        timeline.add("verified:" + verification.passed());

        if (!verification.passed()) {
            RollbackResult rollback = new RollbackResult(true, "verification failed after repair", snapshot);
            timeline.add("rolled_back:restored previous snapshot");
            return new RepairRunResult(
                    runId,
                    observation,
                    diagnosis,
                    plan,
                    snapshot,
                    candidate,
                    snapshot,
                    verification,
                    rollback,
                    RepairOutcome.ROLLED_BACK,
                    immutableCopy(timeline)
            );
        }

        RollbackResult rollback = new RollbackResult(false, "verification passed", candidate);
        return new RepairRunResult(
                runId,
                observation,
                diagnosis,
                plan,
                snapshot,
                candidate,
                candidate,
                verification,
                rollback,
                RepairOutcome.VERIFIED,
                immutableCopy(timeline)
        );
    }

    private Observation observe(ServiceSnapshot snapshot) {
        List<String> signals = new ArrayList<String>();
        if (snapshot.p95LatencyMs() > policy.maxP95LatencyMs()) {
            signals.add("latency_p95_breach=" + snapshot.p95LatencyMs());
        }
        if (snapshot.errorRate() > policy.maxErrorRate()) {
            signals.add("error_rate_breach=" + snapshot.errorRate());
        }
        if (snapshot.cpuUsage() > policy.maxCpuUsage()) {
            signals.add("cpu_saturation=" + snapshot.cpuUsage());
        }
        if (snapshot.canaryRecentlyPromoted()) {
            signals.add("recent_canary_promotion=" + snapshot.version());
        }
        if (signals.isEmpty()) {
            signals.add("service_within_slo");
        }
        boolean healthyOnly = signals.size() == 1 && "service_within_slo".equals(signals.get(0));
        return new Observation(!healthyOnly, immutableCopy(signals));
    }

    private Diagnosis diagnose(ServiceSnapshot snapshot, Observation observation) {
        if (!observation.sloBreached()) {
            return new Diagnosis(
                    IncidentType.HEALTHY,
                    Severity.INFO,
                    "all monitored signals are within policy",
                    0.99,
                    observation.signals()
            );
        }

        if (snapshot.canaryRecentlyPromoted() && snapshot.errorRate() > policy.maxErrorRate()) {
            return new Diagnosis(
                    IncidentType.BAD_DEPLOYMENT,
                    Severity.CRITICAL,
                    "new canary version correlates with elevated error rate",
                    0.91,
                    observation.signals()
            );
        }

        if (snapshot.cpuUsage() > policy.maxCpuUsage() || snapshot.p95LatencyMs() > policy.maxP95LatencyMs()) {
            return new Diagnosis(
                    IncidentType.RESOURCE_SATURATION,
                    Severity.WARNING,
                    "latency and cpu indicate capacity saturation",
                    0.86,
                    observation.signals()
            );
        }

        return new Diagnosis(
                IncidentType.RESOURCE_SATURATION,
                Severity.WARNING,
                "unknown degradation, use bounded capacity repair first",
                0.58,
                observation.signals()
        );
    }

    private RepairPlan plan(ServiceSnapshot snapshot, Diagnosis diagnosis) {
        if (diagnosis.incidentType() == IncidentType.HEALTHY) {
            return new RepairPlan(
                    RepairAction.NOOP,
                    snapshot.serviceName(),
                    "healthy",
                    "healthy",
                    false,
                    "no mutation when service is within SLO"
            );
        }
        if (diagnosis.incidentType() == IncidentType.BAD_DEPLOYMENT) {
            return new RepairPlan(
                    RepairAction.ROLLBACK_VERSION,
                    snapshot.serviceName(),
                    snapshot.version(),
                    snapshot.previousStableVersion(),
                    true,
                    "rollback only to recorded previousStableVersion"
            );
        }

        int targetReplicas = Math.min(snapshot.maxReplicas(), snapshot.replicas() + policy.maxScaleStep());
        return new RepairPlan(
                RepairAction.SCALE_OUT,
                snapshot.serviceName(),
                Integer.toString(snapshot.replicas()),
                Integer.toString(targetReplicas),
                true,
                "scale step <= " + policy.maxScaleStep() + ", target <= maxReplicas"
        );
    }

    private boolean isPlanAllowed(ServiceSnapshot snapshot, RepairPlan plan) {
        if (plan.action() == RepairAction.NOOP) {
            return true;
        }
        if (plan.action() == RepairAction.ROLLBACK_VERSION) {
            return !snapshot.version().equals(snapshot.previousStableVersion());
        }
        if (plan.action() == RepairAction.SCALE_OUT) {
            int targetReplicas = Integer.parseInt(plan.toValue());
            int scaleStep = targetReplicas - snapshot.replicas();
            return scaleStep > 0
                    && scaleStep <= policy.maxScaleStep()
                    && targetReplicas <= snapshot.maxReplicas();
        }
        return false;
    }

    private ServiceSnapshot apply(ServiceSnapshot snapshot, RepairPlan plan) {
        if (plan.action() == RepairAction.ROLLBACK_VERSION) {
            return snapshot.withStableVersionMetrics(
                    Math.min(snapshot.p95LatencyMs(), 350),
                    Math.min(snapshot.errorRate(), 0.01),
                    Math.min(snapshot.cpuUsage(), 0.45)
            );
        }

        int targetReplicas = Integer.parseInt(plan.toValue());
        double ratio = snapshot.replicas() / (double) targetReplicas;
        return snapshot.withReplicasAndMetrics(
                targetReplicas,
                round(snapshot.p95LatencyMs() * ratio * 0.85),
                round(Math.max(0.005, snapshot.errorRate() * 0.7)),
                round(snapshot.cpuUsage() * ratio)
        );
    }

    private VerificationResult verify(ServiceSnapshot snapshot) {
        List<String> checks = new ArrayList<String>();
        boolean latencyOk = snapshot.p95LatencyMs() <= policy.maxP95LatencyMs();
        boolean errorRateOk = snapshot.errorRate() <= policy.maxErrorRate();
        boolean cpuOk = snapshot.cpuUsage() <= policy.maxCpuUsage();

        checks.add("latency_p95<=" + policy.maxP95LatencyMs() + ":" + latencyOk);
        checks.add("error_rate<=" + policy.maxErrorRate() + ":" + errorRateOk);
        checks.add("cpu_usage<=" + policy.maxCpuUsage() + ":" + cpuOk);

        return new VerificationResult(latencyOk && errorRateOk && cpuOk, immutableCopy(checks));
    }

    private String joinSignals(List<String> signals) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < signals.size(); i++) {
            if (i > 0) {
                builder.append('|');
            }
            builder.append(signals.get(i));
        }
        return builder.toString();
    }

    private List<String> immutableCopy(List<String> source) {
        return Collections.unmodifiableList(new ArrayList<String>(source));
    }

    private double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
