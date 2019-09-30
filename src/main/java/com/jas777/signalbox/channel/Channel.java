package com.jas777.signalbox.channel;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class Channel {

    private HashMap<Integer, BlockPos> receivers;

    public Channel() {
        this.receivers = new HashMap<Integer, BlockPos>();
    }

    public HashMap<Integer, BlockPos> getReceivers() {
        return receivers;
    }

}
