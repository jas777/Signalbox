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

    public boolean addReceiver(int id, BlockPos pos) {
        if (receivers.containsKey(id)) return false;
        if (receivers.containsValue(pos)) {
            receivers.forEach((rId, rPos) -> {
                if (rPos.equals(pos)) receivers.remove(rId);
            });
        }
        receivers.put(id, pos);
        return true;
    }

    public boolean removeReceiver(int id, BlockPos pos) {
        if (!receivers.containsKey(id)) return false;
        if (!receivers.get(id).equals(pos)) return false;
        receivers.remove(id);
        return true;
    }

}
