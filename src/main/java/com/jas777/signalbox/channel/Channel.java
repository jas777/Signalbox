package com.jas777.signalbox.channel;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class Channel {

    private HashMap<Integer, BlockPos> signals;

    public Channel() {
        this.signals = new HashMap<Integer, BlockPos>();
    }

    public HashMap<Integer, BlockPos> getSignals() {
        return signals;
    }

}
