package com.jas777.signalbox.tileentity;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.blocks.signalbox.BlockController;
import com.jas777.signalbox.channel.Channel;
import com.jas777.signalbox.util.HasVariant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;

public class ControllerTileEntity extends TileEntity {

    private int channel;
    private int id;
    private int variantOn;
    private int variantOff;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        System.out.println(compound.getInteger("channel"));
        this.channel = compound.getInteger("channel");
        this.id = compound.getInteger("signal_id");
        this.variantOn = compound.getInteger("signal_variant_on");
        this.variantOff = compound.getInteger("signal_variant_off");
        System.out.println("" + channel + " " + id + " " + variantOn + " " + variantOff);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("channel", getChannel());
        compound.setInteger("signal_id", getId());
        compound.setInteger("signal_variant_on", getVariantOn());
        compound.setInteger("signal_variant_off", getVariantOff());
        System.out.println("" + getChannel() + " " + getId() + " " + getVariantOn() + " " + getVariantOff());
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
        update();
        super.onLoad();
    }

    public void update() {
        System.out.println("update");
        if (world.getBlockState(pos).getValue(BlockController.ACTIVE)) {
            System.out.println("1");
            Signalbox.instance.getChannelDispatcher().dispatchMessage(world, getChannel(), getId(), getVariantOn());
        } else {
            System.out.println("2");
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
        markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        update();
        this.channel = channel;
    }

    public void setId(int id) {
        markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        update();
        this.id = id;
    }

    public void setVariantOn(int variant) {
        markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        update();
        this.variantOn = variant;
    }

    public void setVariantOff(int variantOff) {
        markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        update();
        this.variantOff = variantOff;
    }

    public int getMaxVariant() {

        Channel dispatchChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);

        if (dispatchChannel == null) return 0;

        SignalTileEntity tileEntity = (SignalTileEntity) world.getTileEntity(dispatchChannel.getSignals().get(id));

        if (tileEntity == null) return 0;

        HasVariant signal = (HasVariant) tileEntity.getBlockType();

        return Collections.max(signal.getSignalVariant().getAllowedValues());
    }

}
