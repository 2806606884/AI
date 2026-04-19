package com.example.demo.agentrepair;

public class RepairPolicy {

    private final double maxErrorRate;
    private final double maxP95LatencyMs;
    private final double maxCpuUsage;
    private final int maxScaleStep;

    public RepairPolicy(double maxErrorRate, double maxP95LatencyMs, double maxCpuUsage, int maxScaleStep) {
        this.maxErrorRate = maxErrorRate;
        this.maxP95LatencyMs = maxP95LatencyMs;
        this.maxCpuUsage = maxCpuUsage;
        this.maxScaleStep = maxScaleStep;
    }

    public static RepairPolicy demoDefaults() {
        return new RepairPolicy(0.03, 800, 0.75, 2);
    }

    public double maxErrorRate() {
        return maxErrorRate;
    }

    public double maxP95LatencyMs() {
        return maxP95LatencyMs;
    }

    public double maxCpuUsage() {
        return maxCpuUsage;
    }

    public int maxScaleStep() {
        return maxScaleStep;
    }
}
