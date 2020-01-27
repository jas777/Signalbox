package com.jas777.signalbox.control;

import java.util.HashMap;

public class ControlChannel {

    private int frequency;

    private HashMap<Integer, Controllable> tuned;

    public ControlChannel(int frequency) {
        if (frequency <= 0) throw new Error("ID cannot be lower than 1!");
        this.frequency = frequency;
        this.tuned = new HashMap<Integer, Controllable>();
    }

    public int getFrequency() {
        return frequency;
    }

    public HashMap<Integer, Controllable> getTuned() {
        return tuned;
    }

    public void tune(int subFreq, Controllable device) {

        if (tuned.get(subFreq) != null) {
            tuned.replace(subFreq, device);
        } else {
            tuned.put(subFreq, device);
        }

    }

    public void remove(int subFreq) {

        if (!tuned.containsKey(subFreq)) return;

        tuned.remove(subFreq);

    }

    public void remove(Controllable device) {

        if (!tuned.containsValue(device)) return;

        tuned.forEach((subFreq, controllable) -> {
            if (controllable == device) tuned.remove(subFreq);
        });

    }

    public String getFrequencyAsString(int subFrequency) {
        return frequency + "." + subFrequency;
    }

}
