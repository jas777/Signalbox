package com.jas777.signalbox.tileentity;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.channel.Channel;
import com.jas777.signalbox.gui.GuiUpdateHandler;
import com.jas777.signalbox.network.packet.PacketRequestUpdateSignal;
import com.jas777.signalbox.network.packet.PacketUpdateSignal;
import com.jas777.signalbox.network.signalpacket.SignalboxInputStream;
import com.jas777.signalbox.network.signalpacket.SignalboxOutputStream;
import com.jas777.signalbox.util.HasVariant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;

public class SignalTileEntity extends TileEntity implements GuiUpdateHandler {

    private int channel = 0;
    private int id = 0;
    private int signalVariant = 0;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        signalVariant = compound.getInteger("signal_variant");
        channel = compound.getInteger("channel");
        id = compound.getInteger("signal_id");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("signal_variant", signalVariant);
        compound.setInteger("channel", channel);
        compound.setInteger("signal_id", id);
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onLoad() {
        if (world.isRemote) {
            Signalbox.network.sendToServer(new PacketRequestUpdateSignal(this));
        }
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
        HasVariant sVariant = (HasVariant) world.getBlockState(pos).getBlock();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos).withProperty(sVariant.getSignalVariant(), signalVariant), 2);
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateSignal(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return false;
    }

    public int getSignalVariant() {
        return signalVariant;
    }

    public void cycleSignalVariant() {
        HasVariant sVariant = (HasVariant) world.getBlockState(pos).getBlock();
        if (signalVariant < Collections.max(sVariant.getSignalVariant().getAllowedValues())) {
            this.signalVariant++;
        } else {
            this.signalVariant = 0;
        }
        updateBlock();
    }

    public void setSignalVariant(int signalVariant) {
        this.signalVariant = signalVariant;
    }

    public void updateBlock() {
        HasVariant sVariant = (HasVariant) world.getBlockState(pos).getBlock();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos).withProperty(sVariant.getSignalVariant(), signalVariant), 2);
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateSignal(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
    }

    public int getId() {
        return id;
    }

    public int getChannel() {
        return channel;
    }

    public void setId(int id) {

        Channel dispatchChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);

        if (dispatchChannel == null) {
            Signalbox.instance.getChannelDispatcher().getChannels().put(channel, new Channel());
            Channel newChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);
            newChannel.getSignals().put(id, pos);
        } else {
            if (dispatchChannel.getSignals().containsKey(this.id)) {
                dispatchChannel.getSignals().remove(pos);
                dispatchChannel.getSignals().put(id, pos);
            } else {
                dispatchChannel.getSignals().put(id, pos);
            }
        }

        this.id = id;

        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateSignal(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }

    }

    public void setChannel(int channel) {

        Channel dispatchChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(this.channel);

        if (dispatchChannel == null) {
            Signalbox.instance.getChannelDispatcher().getChannels().put(channel, new Channel());
            Channel newChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);
            newChannel.getSignals().put(id, pos);
        } else {
            if (Signalbox.instance.getChannelDispatcher().getChannels().get(channel) == null) {
                Signalbox.instance.getChannelDispatcher().getChannels().put(channel, new Channel());
                Channel newChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);
                newChannel.getSignals().put(id, pos);
            } else {
                dispatchChannel.getSignals().remove(pos);
                Signalbox.instance.getChannelDispatcher().getChannels().put(channel, new Channel());
                Channel newChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);
                newChannel.getSignals().put(id, pos);
            }
        }

        this.channel = channel;

        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateSignal(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }

    }

    @Nullable
    @Override
    public World theWorld() {
        return getWorld();
    }

    @Override
    public void writeGuiData(SignalboxOutputStream data) throws IOException {
        data.writeByte(signalVariant);
        data.writeByte(channel);
        data.writeByte(id);
    }

    @Override
    public void readGuiData(SignalboxInputStream data, EntityPlayer sender) throws IOException {
        signalVariant = data.readByte();
        channel = data.readByte();
        id = data.readByte();
    }
}
