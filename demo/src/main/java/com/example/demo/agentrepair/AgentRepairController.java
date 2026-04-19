package com.example.demo.agentrepair;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/agent-repair")
public class AgentRepairController {

    private final AgentRepairService agentRepairService;

    public AgentRepairController(AgentRepairService agentRepairService) {
        this.agentRepairService = agentRepairService;
    }

    @GetMapping("/scenarios/{name}")
    public RepairRunResult runScenario(@PathVariable String name) {
        return agentRepairService.run(scenario(name));
    }

    @PostMapping("/runs")
    public RepairRunResult run(@RequestBody ServiceSnapshot snapshot) {
        return agentRepairService.run(snapshot);
    }

    private ServiceSnapshot scenario(String name) {
        Map<String, ServiceSnapshot> scenarios = new HashMap<String, ServiceSnapshot>();
        scenarios.put("scale-success", new ServiceSnapshot(
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
        ));
        scenarios.put("rollback-version", new ServiceSnapshot(
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
        ));
        scenarios.put("repair-fails-and-rolls-back", new ServiceSnapshot(
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
        ));

        ServiceSnapshot snapshot = scenarios.get(name);
        if (snapshot == null) {
            throw new IllegalArgumentException("unknown scenario: " + name);
        }
        return snapshot;
    }
}
