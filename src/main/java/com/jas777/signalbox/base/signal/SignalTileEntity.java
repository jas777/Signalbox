package com.jas777.signalbox.base.signal;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.control.ChannelMessage;
import com.jas777.signalbox.control.ControlChannel;
import com.jas777.signalbox.control.Controllable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.apache.commons.lang3.StringUtils;

public class SignalTileEntity extends TileEntity implements Controllable<SignalTileEntity> {

    private ControlChannel channel = null;
    private int signalVariant = 0;
    private int frequency = 0;

    @Override
    public int getFrequency() {
        return frequency;
    }

    @Override
    public ControlChannel getChannel() {
        return this.channel;
    }

    @Override
    public void handleMessage(ChannelMessage<SignalTileEntity> message) {
        message.handle(this);
    }

    @Override
    public void sendMessage(ChannelMessage message) {
        Signalbox.channelDispatcher.sendMessage(channel, message);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

        channel = Signalbox.channelDispatcher.getChannelFromFrequency(nbt.getString("frequency"));
        frequency = Integer.parseInt(StringUtils.split(nbt.getString("frequency"), '.')[1]);

        channel.tune(frequency, this);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

        nbt.setString("frequency", channel.getFrequencyAsString(frequency));

        return nbt;
    }
}
