package com.jas777.signalbox.channel;

import com.jas777.signalbox.tileentity.SignalTileEntity;
import net.minecraft.world.World;

import java.util.HashMap;

public class ChannelDispatcher {

    private HashMap<Integer, Channel> channels;

    public ChannelDispatcher() {
        this.channels = new HashMap<Integer, Channel>();
    }

    public HashMap<Integer, Channel> getChannels() {
        return channels;
    }

    public void dispatchMessage(World world, int channel, int id, int variant) {
        Channel channelToDispatch = channels.get(channel);
        if (channelToDispatch == null) return;
        SignalTileEntity tileEntity = (SignalTileEntity) world.getTileEntity(channelToDispatch.getSignals().get(id));
        if (tileEntity == null) return;
        tileEntity.setSignalVariant(variant);
        tileEntity.markDirty();
        System.out.println("Dispatch: " + channel + " - " + id + " - " + variant);
        tileEntity.updateBlock();
    }


}
