package com.jas777.signalbox.tileentity;

import com.jas777.signalbox.control.ChannelMessage;
import com.jas777.signalbox.control.ControlChannel;
import com.jas777.signalbox.control.Controllable;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

public class ControllerTileEntity<T> extends TileEntity implements Controllable<ControllerTileEntity<T>, T> {

    private ControlChannel channel = null;
    private int subFrequency = 0;
    private int frequency = 0;

    @Override
    public void sendMessage(ChannelMessage<T> message) {
        if (channel == null) return;
        List<Controllable> devices = channel.getTuned().get(subFrequency);
        devices.forEach(c -> c.handleMessage(message));
    }

    @Override
    public void handleMessage(ChannelMessage<ControllerTileEntity<T>> message) {
        message.handle(this);
    }

    public ControlChannel getChannel() {
        return channel;
    }

    public void setChannel(ControlChannel channel) {
        this.channel = channel;
    }

    public int getSubFrequency() {
        return subFrequency;
    }

    public void setSubFrequency(int subFrequency) {
        this.subFrequency = subFrequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = Math.max(frequency, 0);
    }

}
