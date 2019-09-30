package com.jas777.signalbox.blocks.controller;

public enum ControllerType {

    SIGNAL_CONTROLLER("controller_master"),
    DISPLAY_CONTROLLER("controller_display");

    private String blockName;

    ControllerType(String blockName) {
        this.blockName = blockName;
    }

    public String getBlockName() {
        return blockName;
    }
}
