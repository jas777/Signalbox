package com.jas777.signalbox.tileentity;

import com.jas777.signalbox.blocks.BaseSignal;
import com.jas777.signalbox.util.HasVariant;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;

public class SignalTileEntity extends TileEntity {

    private int signalVariant = 0;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        signalVariant = compound.getInteger("signal_variant");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("signal_variant", signalVariant);
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
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        this.readFromNBT(packet.getNbtCompound());
        HasVariant sVariant = (HasVariant) world.getBlockState(pos).getBlock();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos).withProperty(sVariant.getSignalVariant(), signalVariant), 2);
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
    }
}
