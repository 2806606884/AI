package com.example.demo.agentrepair;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AgentRepairServiceDataTests {

    private final AgentRepairService service = new AgentRepairService();

    @Test
    void closesLoopWhenScaleOutRestoresServiceSlo() {
        ServiceSnapshot overloadedCheckout = new ServiceSnapshot(
                "checkout-service",
                "prod",
                "2026.04.19.1",
                "2026.04.18.4",
                2,
                6,
                1200,
                0.018,
                0.92,
                0.61,
                false
        );

        RepairRunResult result = service.run(overloadedCheckout);

        assertThat(result.observation().sloBreached()).isTrue();
        assertThat(result.diagnosis().incidentType()).isEqualTo(IncidentType.RESOURCE_SATURATION);
        assertThat(result.repairPlan().action()).isEqualTo(RepairAction.SCALE_OUT);
        assertThat(result.repairPlan().fromValue()).isEqualTo("2");
        assertThat(result.repairPlan().toValue()).isEqualTo("4");
        assertThat(result.candidateSnapshot().replicas()).isEqualTo(4);
        assertThat(result.verification().passed()).isTrue();
        assertThat(result.rollback().rolledBack()).isFalse();
        assertThat(result.outcome()).isEqualTo(RepairOutcome.VERIFIED);
        assertThat(result.timeline()).contains(
                "diagnosed:RESOURCE_SATURATION:latency and cpu indicate capacity saturation",
                "applied:SCALE_OUT",
                "verified:true"
        );
    }

    @Test
    void rollsBackVersionWhenCanaryCausesErrorSpike() {
        ServiceSnapshot badCanary = new ServiceSnapshot(
                "payment-service",
                "prod",
                "2026.04.19-canary",
                "2026.04.18-stable",
                4,
                8,
                420,
                0.12,
                0.42,
                0.58,
                true
        );

        RepairRunResult result = service.run(badCanary);

        assertThat(result.observation().signals()).contains(
                "error_rate_breach=0.12",
                "recent_canary_promotion=2026.04.19-canary"
        );
        assertThat(result.diagnosis().incidentType()).isEqualTo(IncidentType.BAD_DEPLOYMENT);
        assertThat(result.repairPlan().action()).isEqualTo(RepairAction.ROLLBACK_VERSION);
        assertThat(result.repairPlan().fromValue()).isEqualTo("2026.04.19-canary");
        assertThat(result.repairPlan().toValue()).isEqualTo("2026.04.18-stable");
        assertThat(result.finalSnapshot().version()).isEqualTo("2026.04.18-stable");
        assertThat(result.verification().passed()).isTrue();
        assertThat(result.outcome()).isEqualTo(RepairOutcome.VERIFIED);
    }

    @Test
    void restoresPreviousSnapshotWhenControlledRepairFailsVerification() {
        ServiceSnapshot severeSaturation = new ServiceSnapshot(
                "search-service",
                "prod",
                "2026.04.19.2",
                "2026.04.18.9",
                4,
                5,
                1800,
                0.018,
                0.94,
                0.66,
                false
        );

        RepairRunResult result = service.run(severeSaturation);

        assertThat(result.repairPlan().action()).isEqualTo(RepairAction.SCALE_OUT);
        assertThat(result.repairPlan().guardrail()).contains("scale step <= 2");
        assertThat(result.candidateSnapshot().replicas()).isEqualTo(5);
        assertThat(result.verification().passed()).isFalse();
        assertThat(result.rollback().rolledBack()).isTrue();
        assertThat(result.rollback().restoredSnapshot()).isEqualTo(severeSaturation);
        assertThat(result.finalSnapshot()).isEqualTo(severeSaturation);
        assertThat(result.outcome()).isEqualTo(RepairOutcome.ROLLED_BACK);
        assertThat(result.timeline()).contains(
                "applied:SCALE_OUT",
                "verified:false",
                "rolled_back:restored previous snapshot"
        );
    }
}
