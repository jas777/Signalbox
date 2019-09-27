package com.jas777.signalbox.tileentity;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.channel.Channel;
import com.jas777.signalbox.network.packet.PacketRequestUpdateController;
import com.jas777.signalbox.network.packet.PacketUpdateController;
import com.jas777.signalbox.util.HasVariant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.Collections;

public class ControllerTileEntity extends TileEntity {

    private boolean active;
    private int channel;
    private int id;
    private int variantOn;
    private int variantOff;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.channel = compound.getInteger("channel");
        this.id = compound.getInteger("signal_id");
        this.variantOn = compound.getInteger("signal_variant_on");
        this.variantOff = compound.getInteger("signal_variant_off");
        this.active = compound.getBoolean("active");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("channel", getChannel());
        compound.setInteger("signal_id", getId());
        compound.setInteger("signal_variant_on", getVariantOn());
        compound.setInteger("signal_variant_off", getVariantOff());
        compound.setBoolean("active", active);
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return false;
    }

    @Override
    public void onLoad() {
        if (world.isRemote) {
            Signalbox.network.sendToServer(new PacketRequestUpdateController(this));
        }
        super.onLoad();
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        this.readFromNBT(packet.getNbtCompound());
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateController(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
    }

    public void update() {
        if (isActive()) {
            Signalbox.instance.getChannelDispatcher().dispatchMessage(world, getChannel(), getId(), getVariantOn());
        } else {
            Signalbox.instance.getChannelDispatcher().dispatchMessage(world, getChannel(), getId(), getVariantOff());
        }
        markDirty();
    }

    public int getChannel() {
        return channel;
    }

    public int getId() {
        return id;
    }

    public int getVariantOn() {
        return variantOn;
    }

    public int getVariantOff() {
        return variantOff;
    }

    public void setChannel(int channel) {
        this.channel = channel;
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateController(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        update();
    }

    public void setId(int id) {
        this.id = id;
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateController(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        update();
    }

    public void setVariantOn(int variant) {
        this.variantOn = variant;
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateController(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        update();
    }

    public void setVariantOff(int variantOff) {
        this.variantOff = variantOff;
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateController(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        update();
    }

    public int getMaxVariant() {

        Channel dispatchChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);

        if (dispatchChannel == null) return 0;

        SignalTileEntity tileEntity = (SignalTileEntity) world.getTileEntity(dispatchChannel.getSignals().get(id));

        if (tileEntity == null) return 0;

        HasVariant signal = (HasVariant) tileEntity.getBlockType();

        return Collections.max(signal.getSignalVariant().getAllowedValues());
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateController(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        update();
    }
}
