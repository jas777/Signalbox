package com.jas777.signalbox.tileentity;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.blocks.BaseDisplay;
import com.jas777.signalbox.network.packet.PacketRequestUpdateDisplay;
import com.jas777.signalbox.network.packet.PacketUpdateDisplay;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class DisplayTileEntity extends TileEntity {

    private String displayedText = "0";

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        displayedText = compound.getString("displayed_text");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("displayed_text", displayedText);
        return compound;
    }

    @Override
    public void onLoad() {
        if (world.isRemote) {
            Signalbox.network.sendToServer(new PacketRequestUpdateDisplay(this));
        }
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
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateDisplay(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
    }

    public String getDisplayedText() {
        return displayedText;
    }

    public void setDisplayedText(String displayedText) {
        this.displayedText = displayedText;
    }

    public BaseDisplay getDisplay() {
        return (BaseDisplay) world.getBlockState(pos).getBlock();
    }

    public void setActive(boolean active) {
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos).withProperty(BaseDisplay.ACTIVE, active), 2);
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateDisplay(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
    }

    public boolean isActive() {
        return world.getBlockState(pos).getValue(BaseDisplay.ACTIVE);
    }
}
