package com.jas777.signalbox.channel;

import com.jas777.signalbox.util.Controller;
import gnu.trove.map.hash.THashMap;

public class ControllerDispatcher {

    private THashMap<String, Controller> controllers;

    public ControllerDispatcher() {
        this.controllers = new THashMap<String, Controller>();
    }

    public THashMap<String, Controller> getControllers() {
        return controllers;
    }
}
