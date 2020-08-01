package com.jas777.signalbox.tileentity;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.base.signal.SignalMode;
import com.jas777.signalbox.gui.GuiUpdateHandler;
import com.jas777.signalbox.network.SignalboxInputStream;
import com.jas777.signalbox.network.SignalboxOutputStream;
import com.jas777.signalbox.network.packet.PacketUpdateSignalController;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

public class SignalControllerTileEntity extends ControllerTileEntity<SignalTileEntity> implements GuiUpdateHandler {

    // Redstone-related

    private boolean active = false;

    // Signal variants

    private int variantOn = 0;
    private int variantOff = 0;
    private int variantNoc = 0;

    // Signal mode

    private SignalMode mode = SignalMode.ANALOG;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        setVariantOn(compound.getInteger("variant_on"));
        setVariantOff(compound.getInteger("variant_off"));
        setVariantNoc(compound.getInteger("variant_noc"));
        setMode(SignalMode.values()[compound.getInteger("mode")]);

        if (!compound.getString("frequency").isEmpty()) {
            setChannel(Signalbox.channelDispatcher.getChannelFromFrequency(compound.getString("frequency")));
            setFrequency(getChannel().getFrequency());
            setSubFrequency(Integer.parseInt(StringUtils.split(compound.getString("frequency"), '.')[1]));
            if (getChannel() != null) getChannel().tune(getSubFrequency(), this);
        }

        this.active = compound.getBoolean("active");

        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("variant_on", getVariantOn());
        compound.setInteger("variant_off", getVariantOff());
        compound.setInteger("variant_noc", getVariantNoc());
        compound.setInteger("mode", mode.ordinal());

        if (getChannel() != null) {
            compound.setString("frequency", getChannel().getFrequencyAsString(getSubFrequency()));
        } else {
            compound.setString("frequency", "");
        }

        compound.setBoolean("active", active);

        super.writeToNBT(compound);

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

    public int getVariantOn() {
        return variantOn;
    }

    public void setVariantOn(int variantOn) {
        this.variantOn = variantOn;
    }

    public int getVariantOff() {
        return variantOff;
    }

    public void setVariantOff(int variantOff) {
        this.variantOff = variantOff;
    }

    public int getVariantNoc() {
        return variantNoc;
    }

    public void setVariantNoc(int variantNoc) {
        this.variantNoc = variantNoc;
    }

    public int getMaxVariant() {
        if (getChannel() == null || getSubFrequency() == 0) return 0;
        return Collections.max(getChannel().getTuned().get(getSubFrequency()).stream().map(device -> {
            if (device instanceof SignalTileEntity) {
                return ((SignalTileEntity) device).getMaxSignalVariant();
            } else {
                return 0;
            }
        }).collect(Collectors.toList()));
    }

    public SignalMode getMode() {
        return mode;
    }

    public void setMode(SignalMode mode) {
        this.mode = mode;
    }

    public void writeGuiData(SignalboxOutputStream data) throws IOException {
        data.writeInt(getVariantOn());
        data.writeInt(getVariantOff());
        data.writeInt(getVariantNoc());
        data.writeInt(mode.ordinal());

        if (getChannel() != null) {
            data.writeUTF(getChannel().getFrequencyAsString(getSubFrequency()));
        } else {
            data.writeUTF("");
        }
    }

    public void readGuiData(SignalboxInputStream data, EntityPlayer sender) throws IOException {
        setVariantOn(data.readInt());
        setVariantOff(data.readInt());
        setVariantNoc(data.readInt());
        setMode(SignalMode.values()[data.readInt()]);

        String frequencyString = data.readUTF();

        if (!frequencyString.isEmpty()) {
            setChannel(Signalbox.channelDispatcher.getChannelFromFrequency(frequencyString));
            setFrequency(Integer.parseInt(StringUtils.split(frequencyString, '.')[0]));
            setSubFrequency(Integer.parseInt(StringUtils.split(frequencyString, '.')[1]));
            if (getChannel() != null) getChannel().tune(getSubFrequency(), this);
        }
        markDirty();
    }

    @Override
    public World getTheWorld() {
        return getWorld();
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return false;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateSignalController(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
    }

    public void updateConnected() {
        sendMessage(target -> {
            target.setSignalVariant(active ? getVariantOn() : getVariantOff());
            target.updateSignal();
        });
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        this.readFromNBT(packet.getNbtCompound());
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateSignalController(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
    }

}
