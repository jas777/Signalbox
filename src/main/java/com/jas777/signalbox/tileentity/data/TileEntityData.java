package com.jas777.signalbox.tileentity.data;

import java.util.HashMap;

public class TileEntityData {

    private HashMap<Object, Object> variables;

    public TileEntityData() {
        this.variables = new HashMap<Object, Object>();
    }

    public HashMap<Object, Object> getVariables() {
        return variables;
    }
}
