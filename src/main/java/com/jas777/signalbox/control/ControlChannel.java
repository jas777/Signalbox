package com.jas777.signalbox.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlChannel {

    private int frequency;

    private HashMap<Integer, List<Controllable>> tuned;

    public ControlChannel(int frequency) {
        if (frequency <= 0) throw new Error("ID cannot be lower than 1!");
        this.frequency = frequency;
        this.tuned = new HashMap<Integer, List<Controllable>>();
    }

    public int getFrequency() {
        return frequency;
    }

    public Map<Integer, List<Controllable>> getTuned() {
        return tuned;
    }

    public ControlChannel tune(int subFreq, Controllable device) {

        if (subFreq < 1) return this;

        if (!tuned.containsKey(subFreq)) tuned.put(subFreq, new ArrayList<>());

        if (tuned.get(subFreq).contains(device)) {
            return this;
        } else {
            tuned.get(subFreq).add(device);
        }

        return this;

    }

    public void remove(Controllable device) {

        if (!tuned.containsKey(device.getFrequency())) return;

        tuned.forEach((subFreq, list) -> {
            list.remove(device);
        });

    }

    public String getFrequencyAsString(int subFrequency) {
        return frequency + "." + subFrequency;
    }

}
