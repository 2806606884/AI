package com.example.demo.agentrepair;

import java.util.Objects;

public class ServiceSnapshot {

    private String serviceName;
    private String environment;
    private String version;
    private String previousStableVersion;
    private int replicas;
    private int maxReplicas;
    private double p95LatencyMs;
    private double errorRate;
    private double cpuUsage;
    private double memoryUsage;
    private boolean canaryRecentlyPromoted;

    public ServiceSnapshot() {
    }

    public ServiceSnapshot(
            String serviceName,
            String environment,
            String version,
            String previousStableVersion,
            int replicas,
            int maxReplicas,
            double p95LatencyMs,
            double errorRate,
            double cpuUsage,
            double memoryUsage,
            boolean canaryRecentlyPromoted
    ) {
        this.serviceName = serviceName;
        this.environment = environment;
        this.version = version;
        this.previousStableVersion = previousStableVersion;
        this.replicas = replicas;
        this.maxReplicas = maxReplicas;
        this.p95LatencyMs = p95LatencyMs;
        this.errorRate = errorRate;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.canaryRecentlyPromoted = canaryRecentlyPromoted;
        validate();
    }

    public void validate() {
        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new IllegalArgumentException("serviceName must not be blank");
        }
        if (environment == null || environment.trim().isEmpty()) {
            throw new IllegalArgumentException("environment must not be blank");
        }
        if (version == null || version.trim().isEmpty()) {
            throw new IllegalArgumentException("version must not be blank");
        }
        if (previousStableVersion == null || previousStableVersion.trim().isEmpty()) {
            throw new IllegalArgumentException("previousStableVersion must not be blank");
        }
        if (replicas < 1) {
            throw new IllegalArgumentException("replicas must be greater than 0");
        }
        if (maxReplicas < replicas) {
            throw new IllegalArgumentException("maxReplicas must be greater than or equal to replicas");
        }
        validateRatio("errorRate", errorRate);
        validateRatio("cpuUsage", cpuUsage);
        validateRatio("memoryUsage", memoryUsage);
        if (p95LatencyMs < 0) {
            throw new IllegalArgumentException("p95LatencyMs must be greater than or equal to 0");
        }
    }

    private static void validateRatio(String name, double value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }

    public ServiceSnapshot withReplicasAndMetrics(
            int targetReplicas,
            double targetP95LatencyMs,
            double targetErrorRate,
            double targetCpuUsage
    ) {
        return new ServiceSnapshot(
                serviceName,
                environment,
                version,
                previousStableVersion,
                targetReplicas,
                maxReplicas,
                targetP95LatencyMs,
                targetErrorRate,
                targetCpuUsage,
                memoryUsage,
                canaryRecentlyPromoted
        );
    }

    public ServiceSnapshot withStableVersionMetrics(
            double targetP95LatencyMs,
            double targetErrorRate,
            double targetCpuUsage
    ) {
        return new ServiceSnapshot(
                serviceName,
                environment,
                previousStableVersion,
                previousStableVersion,
                replicas,
                maxReplicas,
                targetP95LatencyMs,
                targetErrorRate,
                targetCpuUsage,
                memoryUsage,
                false
        );
    }

    public String serviceName() {
        return serviceName;
    }

    public String environment() {
        return environment;
    }

    public String version() {
        return version;
    }

    public String previousStableVersion() {
        return previousStableVersion;
    }

    public int replicas() {
        return replicas;
    }

    public int maxReplicas() {
        return maxReplicas;
    }

    public double p95LatencyMs() {
        return p95LatencyMs;
    }

    public double errorRate() {
        return errorRate;
    }

    public double cpuUsage() {
        return cpuUsage;
    }

    public double memoryUsage() {
        return memoryUsage;
    }

    public boolean canaryRecentlyPromoted() {
        return canaryRecentlyPromoted;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPreviousStableVersion() {
        return previousStableVersion;
    }

    public void setPreviousStableVersion(String previousStableVersion) {
        this.previousStableVersion = previousStableVersion;
    }

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }

    public int getMaxReplicas() {
        return maxReplicas;
    }

    public void setMaxReplicas(int maxReplicas) {
        this.maxReplicas = maxReplicas;
    }

    public double getP95LatencyMs() {
        return p95LatencyMs;
    }

    public void setP95LatencyMs(double p95LatencyMs) {
        this.p95LatencyMs = p95LatencyMs;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(double errorRate) {
        this.errorRate = errorRate;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public boolean isCanaryRecentlyPromoted() {
        return canaryRecentlyPromoted;
    }

    public void setCanaryRecentlyPromoted(boolean canaryRecentlyPromoted) {
        this.canaryRecentlyPromoted = canaryRecentlyPromoted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceSnapshot)) {
            return false;
        }
        ServiceSnapshot that = (ServiceSnapshot) o;
        return replicas == that.replicas
                && maxReplicas == that.maxReplicas
                && Double.compare(that.p95LatencyMs, p95LatencyMs) == 0
                && Double.compare(that.errorRate, errorRate) == 0
                && Double.compare(that.cpuUsage, cpuUsage) == 0
                && Double.compare(that.memoryUsage, memoryUsage) == 0
                && canaryRecentlyPromoted == that.canaryRecentlyPromoted
                && Objects.equals(serviceName, that.serviceName)
                && Objects.equals(environment, that.environment)
                && Objects.equals(version, that.version)
                && Objects.equals(previousStableVersion, that.previousStableVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, environment, version, previousStableVersion, replicas, maxReplicas,
                p95LatencyMs, errorRate, cpuUsage, memoryUsage, canaryRecentlyPromoted);
    }
}
