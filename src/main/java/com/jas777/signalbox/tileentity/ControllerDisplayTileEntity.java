package com.jas777.signalbox.tileentity;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.blocks.controller.BlockController;
import com.jas777.signalbox.gui.GuiUpdateHandler;
import com.jas777.signalbox.network.packet.PacketRequestUpdateControllerDisplay;
import com.jas777.signalbox.network.packet.PacketUpdateControllerDisplay;
import com.jas777.signalbox.network.signalpacket.SignalboxInputStream;
import com.jas777.signalbox.network.signalpacket.SignalboxOutputStream;
import com.jas777.signalbox.util.CanBePowered;
import com.jas777.signalbox.util.Controller;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ControllerDisplayTileEntity extends TileEntity implements GuiUpdateHandler, CanBePowered, Controller {

    private boolean active;
    private int channel = 0;
    private int id = 0;
    private int speedLimit = 0;
    private NBTTagIntArray slavePos;

    private List<Controller> slaves = new ArrayList<Controller>();

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        setChannel(compound.getInteger("channel"));
        setId(compound.getInteger("display_id"));
        setSpeedLimit(compound.getInteger("speed_limit"));
        this.active = compound.getBoolean("active");
        this.slavePos = (NBTTagIntArray) compound.getTag("slaves");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("channel", getChannel());
        compound.setInteger("display_id", getId());
        compound.setInteger("speed_limit", getSpeedLimit());
        compound.setBoolean("active", active);
        Integer[] serialized = slaves.stream().map(s -> (int) s.getPosition().toLong()).toArray(Integer[]::new);
        compound.setTag("slaves", new NBTTagIntArray(Arrays.asList(serialized)));
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

        if (slavePos != null && slavePos.getIntArray().length > 0) {
            for (int i : slavePos.getIntArray()) {
                BlockPos pos = BlockPos.fromLong(i);
                TileEntity te = world.getTileEntity(pos);
                if (!(te instanceof Controller)) return;
                if (!slaves.contains(te)) {
                    slaves.add((Controller) te);
                }
            }
        }

        if (world.isRemote) {
            Signalbox.network.sendToServer(new PacketRequestUpdateControllerDisplay(this));
        }
        super.onLoad();
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        this.readFromNBT(packet.getNbtCompound());
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateControllerDisplay(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
    }

    public void update() {
        if (!(Signalbox.instance.getChannelDispatcher().getReceiver(world, getChannel(), getId()) instanceof DisplayTileEntity))
            return;
        if (isActive()) {
            Signalbox.instance.getChannelDispatcher().dispatchMessage(world, getChannel(), getId(), String.valueOf(getSpeedLimit()));
        } else {
            Signalbox.instance.getChannelDispatcher().dispatchMessage(world, getChannel(), getId(), "");
        }
        markDirty();
    }

    public int getChannel() {
        return channel;
    }

    public int getId() {
        return id;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public void setChannel(int channel) {
        this.channel = channel;
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateControllerDisplay(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        update();
    }

    public void setId(int id) {
        this.id = id;
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateControllerDisplay(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        update();
    }

    public void setSpeedLimit(int variant) {
        this.speedLimit = variant;
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateControllerDisplay(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        update();
    }

    public int getMaxVariant() {
        return 16;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos).withProperty(BlockController.ACTIVE, active), 2);
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateControllerDisplay(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        update();
    }

    @Override
    public void writeGuiData(SignalboxOutputStream data) throws IOException {
        data.writeByte(speedLimit);
        data.writeByte(channel);
        data.writeByte(id);
    }

    @Override
    public void readGuiData(SignalboxInputStream data, EntityPlayer sender) throws IOException {
        speedLimit = data.readByte();
        channel = data.readByte();
        id = data.readByte();
    }

    @Nullable
    @Override
    public World theWorld() {
        return getWorld();
    }

    @Override
    public List<Controller> getSlaves() {
        return slaves;
    }

    @Override
    public BlockPos getPosition() {
        return getPos();
    }
}
