package com.jas777.signalbox.tileentity;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.blocks.BaseSignal;
import com.jas777.signalbox.channel.Channel;
import com.jas777.signalbox.gui.GuiUpdateHandler;
import com.jas777.signalbox.integration.ImmersiveRailroading;
import com.jas777.signalbox.network.packet.PacketRequestUpdateSignal;
import com.jas777.signalbox.network.packet.PacketUpdateSignal;
import com.jas777.signalbox.network.signalpacket.SignalboxInputStream;
import com.jas777.signalbox.network.signalpacket.SignalboxOutputStream;
import com.jas777.signalbox.signal.SignalMode;
import com.jas777.signalbox.util.CanBePowered;
import com.jas777.signalbox.util.CanReceive;
import com.jas777.signalbox.util.HasVariant;
import com.jas777.signalbox.util.SignalMast;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import scala.Tuple2;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;

public class SignalTileEntity extends TileEntity implements GuiUpdateHandler, CanBePowered, CanReceive {

    private int channel = 0;
    private int id = 0;
    private int signalVariant = 0;

    private SignalMode mode = SignalMode.ANALOG;

    // AUTO MODE

    private LastSwitchInfo lastSwitchInfo = new LastSwitchInfo();

    private Vec3d origin = new Vec3d(0, -1, 0);
    private Vec3d lastLocation;
    private Vec3d lastMotion;

    private int blocksTravelled = 0;
    private boolean lastTickTimedOut = false;

    private ForgeChunkManager.Ticket lastTicket;

    private Tuple2<BlockPos, BlockPos> endPoint;

    //

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        signalVariant = compound.getInteger("signal_variant");
        channel = compound.getInteger("channel");
        id = compound.getInteger("signal_id");
        mode = SignalMode.values()[compound.getInteger("signal_mode")];

        BlockPos endRail = null;
        BlockPos endTE = null;

        if (compound.hasKey("endpoint_rail")) {
            endRail = BlockPos.fromLong(compound.getLong("endpoint_rail"));
        }

        if (compound.hasKey("endpoint_tileentity")) {
            endTE = BlockPos.fromLong(compound.getLong("endpoint_tileentity"));
        }

        if (endRail != null && endTE != null) {
            endPoint = new Tuple2<BlockPos, BlockPos>(endRail, endTE);
        } else {
            endPoint = null;
        }

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("signal_variant", signalVariant);
        compound.setInteger("channel", channel);
        compound.setInteger("signal_id", id);
        compound.setInteger("signal_mode", mode.ordinal());

        if (endPoint != null) {
            compound.setLong("endpoint_rail", endPoint._1().toLong());
            compound.setLong("endpoint_tileentity", endPoint._2().toLong());
        }

        return compound;
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
    public void onLoad() {
        if (channel != 0 && id != 0) {

            Channel dispatchChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);

            if (dispatchChannel == null) {
                Signalbox.instance.getChannelDispatcher().getChannels().put(channel, new Channel());
                Channel newChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);
                newChannel.getReceivers().put(id, pos);
            } else {
                if (dispatchChannel.getReceivers().containsKey(this.id)) {
                    dispatchChannel.getReceivers().remove(pos);
                    dispatchChannel.getReceivers().put(id, pos);
                } else {
                    dispatchChannel.getReceivers().put(id, pos);
                }
            }
        }
        if (world.isRemote) {
            Signalbox.network.sendToServer(new PacketRequestUpdateSignal(this));
        } else {
            Signalbox.network.sendToAllAround(new PacketUpdateSignal(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
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

    @Override
    public void setData(ByteBuf data) {
        setSignalVariant(data.readInt());
    }

    public void setSignalVariant(int signalVariant) {
        this.signalVariant = signalVariant;
    }

    @Override
    public void updateBlock() {
        updateSignal();
    }

    public void updateVariant() {
        if (world.getBlockState(pos).getBlock() instanceof BaseSignal) {
            HasVariant sVariant = (HasVariant) world.getBlockState(pos).getBlock();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos).withProperty(sVariant.getSignalVariant(), signalVariant), 2);
        }
    }

    public void updateSignal() {
        if (world.getBlockState(pos).getBlock() instanceof BaseSignal) {
            HasVariant sVariant = (HasVariant) world.getBlockState(pos).getBlock();
            if (getSignalVariant() != world.getBlockState(pos).getValue(sVariant.getSignalVariant())) updateVariant();
        }
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateSignal(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    public void setId(int id) {

        if (channel == 0) return;

        Channel dispatchChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);

        if (dispatchChannel == null) {
            Signalbox.instance.getChannelDispatcher().getChannels().put(channel, new Channel());
            Channel newChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);
            newChannel.getReceivers().put(id, pos);
        } else {
            if (dispatchChannel.getReceivers().containsKey(this.id)) {
                dispatchChannel.getReceivers().remove(pos);
                dispatchChannel.getReceivers().put(id, pos);
            } else {
                dispatchChannel.getReceivers().put(id, pos);
            }
        }

        this.id = id;

    }

    @Override
    public void setChannel(int channel) {

        if (channel == 0) return;

        Channel dispatchChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(this.channel);

        if (dispatchChannel == null) {
            Signalbox.instance.getChannelDispatcher().getChannels().put(channel, new Channel());
            Channel newChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);
            newChannel.getReceivers().put(id, pos);
        } else {
            if (Signalbox.instance.getChannelDispatcher().getChannels().get(channel) == null) {
                Signalbox.instance.getChannelDispatcher().getChannels().put(channel, new Channel());
                Channel newChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);
                newChannel.getReceivers().put(id, pos);
            } else {
                dispatchChannel.getReceivers().remove(pos);
                Signalbox.instance.getChannelDispatcher().getChannels().put(channel, new Channel());
                Channel newChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);
                newChannel.getReceivers().put(id, pos);
            }
        }

        this.channel = channel;

    }

    @Nullable
    @Override
    public World theWorld() {
        return getWorld();
    }

    @Override
    public void writeGuiData(SignalboxOutputStream data) throws IOException {
        data.writeByte(signalVariant);
        data.writeInt(channel);
        data.writeInt(id);
        data.writeEnum(mode);
    }

    @Override
    public void readGuiData(SignalboxInputStream data, EntityPlayer sender) throws IOException {
        signalVariant = data.readByte();
        channel = data.readInt();
        id = data.readInt();
        mode = data.readEnum(SignalMode.values());
        markDirty();
    }

    public void setActive(boolean active) {
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos).withProperty(BaseSignal.ACTIVE, active), 2);
    }

    public SignalMode getMode() {
        return mode;
    }

    public void setMode(SignalMode mode) {
        this.mode = mode;
        markDirty();
    }


    public boolean isActive() {
        return world.getBlockState(pos).getValue(BaseSignal.ACTIVE);
    }

    public LastSwitchInfo getLastSwitchInfo() {
        return lastSwitchInfo;
    }

    public void setLastSwitchInfo(LastSwitchInfo lastSwitchInfo) {
        this.lastSwitchInfo = lastSwitchInfo;
    }

    public Tuple2<BlockPos, BlockPos> getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Tuple2<BlockPos, BlockPos> endPoint) {
        this.endPoint = endPoint;
    }

    public Vec3d getLastMotion() {
        return lastMotion;
    }

    public void setLastMotion(Vec3d lastMotion) {
        this.lastMotion = lastMotion;
    }

    public Vec3d getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Vec3d lastLocation) {
        this.lastLocation = lastLocation;
    }

    public Vec3d getOrigin() {
        return origin;
    }

    public void setOrigin(Vec3d origin) {
        this.origin = origin;
    }

    void setOccupationOrigin() {
        if (!(world.getBlockState(getPos()).getBlock() instanceof BaseSignal)) return;
        EnumFacing signalFacing = world.getBlockState(getPos()).getValue(BaseSignal.FACING);

        BlockPos workingPos = getPos();
        boolean correctedPos = false;
        while ((world.getBlockState(workingPos).getBlock() instanceof BaseSignal) || (world.getBlockState(workingPos).getBlock() instanceof SignalMast)) {
            workingPos = workingPos.down();
            correctedPos = true;
        }
        if (correctedPos) {
            workingPos = workingPos.up();
        }
        Vec3d origin = ImmersiveRailroading.findOrigin(workingPos, signalFacing, world);

        boolean willNotify = this.origin.y != origin.y;
        this.origin = origin;
        markDirty();

        if (willNotify) {
            updateSignal();
        }
    }

    public ForgeChunkManager.Ticket getLastTicket() {
        return lastTicket;
    }

    public void setLastTicket(ForgeChunkManager.Ticket lastTicket) {
        this.lastTicket = lastTicket;
    }

    public int getBlocksTravelled() {
        return blocksTravelled;
    }

    public void setBlocksTravelled(int blocksTravelled) {
        this.blocksTravelled = blocksTravelled;
    }

    public boolean isLastTickTimedOut() {
        return lastTickTimedOut;
    }

    public void setLastTickTimedOut(boolean lastTickTimedOut) {
        this.lastTickTimedOut = lastTickTimedOut;
    }

    public String getFrequency() {
        return getChannel() + "." + getId();
    }

    public void setOccupationOrigin(BlockPos newPos) {
        origin = new Vec3d(newPos);

        markDirty();
    }

    public static class LastSwitchInfo {
        public Vec3d lastSwitchPlacementPosition = null;
    }

}
