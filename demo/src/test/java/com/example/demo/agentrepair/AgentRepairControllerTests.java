package com.example.demo.agentrepair;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgentRepairController.class)
@Import(AgentRepairService.class)
class AgentRepairControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exposesRunnableScenarioForDemo() throws Exception {
        mockMvc.perform(get("/api/agent-repair/scenarios/scale-success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosis.incidentType", is("RESOURCE_SATURATION")))
                .andExpect(jsonPath("$.repairPlan.action", is("SCALE_OUT")))
                .andExpect(jsonPath("$.verification.passed", is(true)))
                .andExpect(jsonPath("$.outcome", is("VERIFIED")));
    }

    @Test
    void acceptsSyntheticSnapshotData() throws Exception {
        String body = "{" +
                "\"serviceName\":\"search-service\"," +
                "\"environment\":\"prod\"," +
                "\"version\":\"2026.04.19.2\"," +
                "\"previousStableVersion\":\"2026.04.18.9\"," +
                "\"replicas\":4," +
                "\"maxReplicas\":5," +
                "\"p95LatencyMs\":1800," +
                "\"errorRate\":0.018," +
                "\"cpuUsage\":0.94," +
                "\"memoryUsage\":0.66," +
                "\"canaryRecentlyPromoted\":false" +
                "}";

        mockMvc.perform(post("/api/agent-repair/runs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outcome", is("ROLLED_BACK")))
                .andExpect(jsonPath("$.rollback.reason", containsString("verification failed")));
    }
}

