package com.jas777.signalbox.tileentity;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.base.signal.BaseSignal;
import com.jas777.signalbox.control.ChannelMessage;
import com.jas777.signalbox.control.ControlChannel;
import com.jas777.signalbox.control.Controllable;
import com.jas777.signalbox.network.packet.PacketRequestUpdateSignal;
import com.jas777.signalbox.network.packet.PacketUpdateSignal;
import com.jas777.signalbox.util.interfaces.SignalVariants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.commons.lang3.StringUtils;

public class SignalTileEntity extends TileEntity implements Controllable<SignalTileEntity, ControllerTileEntity<SignalTileEntity>> {

    private ControlChannel channel = null;
    private int signalVariant = 0;
    private int frequency = 0;

    @Override
    public void sendMessage(ChannelMessage message) {
        // Signalbox.channelDispatcher.sendMessage(channel, message);
    }

    @Override
    public void handleMessage(ChannelMessage<SignalTileEntity> message) {
        message.handle(this);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

        if (!nbt.getString("frequency").isEmpty()) {
            channel = Signalbox.channelDispatcher.getChannelFromFrequency(nbt.getString("frequency"));
            frequency = Integer.parseInt(StringUtils.split(nbt.getString("frequency"), '.')[1]);
        }

        signalVariant = nbt.getInteger("signal_variant");

        if (frequency != 0 && channel != null) {
            channel.tune(frequency, this);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

        if (channel != null) {
            nbt.setString("frequency", channel.getFrequencyAsString(frequency));
        }

        nbt.setInteger("signal_variant",signalVariant);

        return nbt;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        this.readFromNBT(packet.getNbtCompound());
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateSignal(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return false;
    }

    @Override
    public int getFrequency() {
        return frequency;
    }

    @Override
    public ControlChannel getChannel() {
        return this.channel;
    }

    public int getSignalVariant() {
        return signalVariant;
    }

    public void setSignalVariant(int signalVariant) {
        this.signalVariant = signalVariant;
    }

    public void setChannel(ControlChannel channel) {
        this.channel = channel.tune(frequency, this);
    }

    public void setFrequency(int frequency) {

        if (channel != null) {
            this.channel = channel.tune(frequency, this);
        }

        this.frequency = frequency;
    }

    public void updateSignal() {

        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof BaseSignal) {

            world.notifyBlockUpdate(pos, state, state.withProperty(((SignalVariants) state.getBlock()).getSignalVariant(), signalVariant), 2);

            if (world.isRemote) {
                Signalbox.network.sendToServer(new PacketRequestUpdateSignal(this));
            } else {
                Signalbox.network.sendToAllAround(new PacketUpdateSignal(this), new NetworkRegistry.TargetPoint(this.getWorld().provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
            }

        }

    }
}
