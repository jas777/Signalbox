package com.jas777.signalbox.signal;

public enum SignalMode {

    ANALOG("Analog (Redstone)"),
    DIGITAL("Digital"),
    AUTO("Auto");

    private String name;

    SignalMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
