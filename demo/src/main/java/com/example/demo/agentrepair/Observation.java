package com.example.demo.agentrepair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Observation {

    private final boolean sloBreached;
    private final List<String> signals;

    public Observation(boolean sloBreached, List<String> signals) {
        this.sloBreached = sloBreached;
        this.signals = immutableCopy(signals);
    }

    public boolean sloBreached() {
        return sloBreached;
    }

    public boolean isSloBreached() {
        return sloBreached;
    }

    public List<String> signals() {
        return signals;
    }

    public List<String> getSignals() {
        return signals;
    }

    private static List<String> immutableCopy(List<String> source) {
        if (source == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<String>(source));
    }
}
