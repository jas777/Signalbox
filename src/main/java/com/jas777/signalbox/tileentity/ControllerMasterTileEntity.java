package com.jas777.signalbox.tileentity;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.blocks.BaseSignal;
import com.jas777.signalbox.blocks.controller.BlockController;
import com.jas777.signalbox.channel.Channel;
import com.jas777.signalbox.gui.GuiUpdateHandler;
import com.jas777.signalbox.integration.ImmersiveRailroading;
import com.jas777.signalbox.network.packet.PacketRequestUpdateControllerMaster;
import com.jas777.signalbox.network.packet.PacketUpdateControllerMaster;
import com.jas777.signalbox.network.signalpacket.SignalboxInputStream;
import com.jas777.signalbox.network.signalpacket.SignalboxOutputStream;
import com.jas777.signalbox.signal.SignalMode;
import com.jas777.signalbox.util.CanBePowered;
import com.jas777.signalbox.util.Controller;
import com.jas777.signalbox.util.HasVariant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ControllerMasterTileEntity extends TileEntity implements GuiUpdateHandler, CanBePowered, Controller {

    private boolean active;
    private int channel = 0;
    private int id = 0;
    private int variantOn = 1;
    private int variantOff = 0;
    private NBTTagIntArray slavePos;

    // DISTANT
    private int nextOccupied = 2;
    //

    private List<Controller> slaves = new ArrayList<Controller>();

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.channel = compound.getInteger("channel");
        this.id = compound.getInteger("signal_id");
        this.variantOn = compound.getInteger("signal_variant_on");
        this.variantOff = compound.getInteger("signal_variant_off");
        this.nextOccupied = compound.getInteger("signal_next_occupied");
        this.active = compound.getBoolean("active");
        this.slavePos = (NBTTagIntArray) compound.getTag("slaves");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("channel", getChannel());
        compound.setInteger("signal_id", getId());
        compound.setInteger("signal_variant_on", getVariantOn());
        compound.setInteger("signal_variant_off", getVariantOff());
        compound.setInteger("signal_next_occupied", getNextOccupied());
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
            Signalbox.network.sendToServer(new PacketRequestUpdateControllerMaster(this));
        } else {
            Signalbox.network.sendToAllAround(new PacketUpdateControllerMaster(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        super.onLoad();
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        this.readFromNBT(packet.getNbtCompound());
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateControllerMaster(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
    }

    public void updateSignal() {
        if (!(Signalbox.instance.getChannelDispatcher().getReceiver(world, getChannel(), getId()) instanceof SignalTileEntity))
            return;

        if (isActive()) {
            if (getSignal().getMode() == SignalMode.AUTO) return;
            Signalbox.instance.getChannelDispatcher().dispatchMessage(world, getChannel(), getId(), getVariantOn());
        } else {
            if (getSignal().getMode() == SignalMode.AUTO) return;
            Signalbox.instance.getChannelDispatcher().dispatchMessage(world, getChannel(), getId(), getVariantOff());
        }
        getSignal().markDirty();
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
            Signalbox.network.sendToAllAround(new PacketUpdateControllerMaster(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        updateSignal();
    }

    public void setId(int id) {
        this.id = id;
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateControllerMaster(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        updateSignal();
    }

    public void setVariantOn(int variant) {
        this.variantOn = variant;
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateControllerMaster(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        updateSignal();
    }

    public void setVariantOff(int variantOff) {
        this.variantOff = variantOff;
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateControllerMaster(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        updateSignal();
    }

    public int getMaxVariant() {

        Channel dispatchChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);

        if (dispatchChannel == null || dispatchChannel.getReceivers().get(id) == null) return 0;

        TileEntity tileEntity = world.getTileEntity(dispatchChannel.getReceivers().get(id));

        if (tileEntity == null) return 0;

        HasVariant signal = (HasVariant) tileEntity.getBlockType();

        return Collections.max(signal.getSignalVariant().getAllowedValues());
    }

    private SignalTileEntity getSignal() {
        Channel dispatchChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);
        if (dispatchChannel == null || dispatchChannel.getReceivers().get(id) == null) return null;
        TileEntity tileEntity = world.getTileEntity(dispatchChannel.getReceivers().get(id));
        if (!(tileEntity instanceof SignalTileEntity)) return null;
        return (SignalTileEntity) tileEntity;
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
            Signalbox.network.sendToAllAround(new PacketUpdateControllerMaster(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        updateSignal();
    }

    @Override
    public void writeGuiData(SignalboxOutputStream data) throws IOException {
        data.writeByte(variantOn);
        data.writeByte(variantOff);
        data.writeByte(nextOccupied);
        data.writeByte(channel);
        data.writeByte(id);
    }

    @Override
    public void readGuiData(SignalboxInputStream data, EntityPlayer sender) throws IOException {
        setVariantOn(data.readByte());
        setVariantOff(data.readByte());
        setNextOccupied(data.readByte());
        setChannel(data.readByte());
        setId(data.readByte());
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

    public String getFrequency() {
        return getChannel() + "." + getId();
    }

    public void updateSignalDistant() {
        SignalTileEntity signal = getSignal();
        if (signal.getMode() == SignalMode.AUTO && Signalbox.instance.IR_LOADED) {
            if (signal.getOrigin().y == -1) {
                signal.setOccupationOrigin();

                if (signal.getOrigin().y == -1) {
                    signal.setEndPoint(null);
                    return;
                }
            }

            doNormalTick();
        }
    }

    private boolean doChunkLoad(BlockPos nextPos) {
        Chunk chunk = world.getChunkFromBlockCoords(nextPos);
        SignalTileEntity signal = getSignal();
        if (signal == null) return false;
        if (!chunk.isLoaded()) {
            if (signal.getLastTicket() != null) {
                ChunkPos lastChunkPos = null;
                for (ChunkPos pos : signal.getLastTicket().getChunkList()) {
                    lastChunkPos = pos;
                    break;
                }

                ForgeChunkManager.unforceChunk(signal.getLastTicket(), lastChunkPos);
                ForgeChunkManager.releaseTicket(signal.getLastTicket());
            }

            signal.setLastTicket(ForgeChunkManager.requestTicket(Signalbox.instance, world, ForgeChunkManager.Type.NORMAL));
            if (signal.getLastTicket() == null) {
                FMLLog.getLogger().error("Signal failed to load chunk during tick - maybe there are too many signals?");
                return false;
            }

            ForgeChunkManager.forceChunk(signal.getLastTicket(), new ChunkPos(chunk.x, chunk.z));
        } else if (signal.getLastTicket() != null && !signal.getLastTicket().getChunkList().contains(new ChunkPos(chunk.x, chunk.z))) {
            ChunkPos lastChunkPos = null;
            for (ChunkPos pos : signal.getLastTicket().getChunkList()) {
                lastChunkPos = pos;
                break;
            }

            ForgeChunkManager.unforceChunk(signal.getLastTicket(), lastChunkPos);
            ForgeChunkManager.releaseTicket(signal.getLastTicket());
        }

        return true;
    }

    private void doNormalTick() {
        SignalTileEntity signal = getSignal();
        if (signal.getEndPoint() == null) {
            if (signal.getSignalVariant() != getVariantOff()) {
                signal.setSignalVariant(getVariantOff());
                signal.markDirty();
                signal.updateBlock();
            }
            return;
        }

        if (signal.getBlocksTravelled() == 0) {
            EnumFacing signalFacing = world.getBlockState(getPos()).getValue(BaseSignal.FACING).getOpposite();
            BlockPos current = new BlockPos(signal.getOrigin());
            BlockPos motionBP = current.offset(signalFacing);

            signal.setLastLocation(new Vec3d(motionBP));
        }

        for (int i = 0; i < 10; i++) {
            if (signal.getLastLocation() == null) {
                signal.setLastLocation(signal.getOrigin());
            }

            Vec3d motion = signal.getLastMotion();

            Vec3d nextLocation = ImmersiveRailroading.getNextPosition(signal.getLastLocation(), motion, world, signal.getLastSwitchInfo());

            if (!doChunkLoad(new BlockPos(nextLocation.x, nextLocation.y, nextLocation.z))) {
                signal.setLastLocation(null);
                signal.setBlocksTravelled(0);
                break;
            }

            if (ImmersiveRailroading.hasStockNearby(signal.getOrigin(), world) || ImmersiveRailroading.hasStockNearby(nextLocation, world)) {
                signal.setSignalVariant(getVariantOff());
                signal.setLastTickTimedOut(false);
                signal.markDirty();
                signal.updateBlock();

                signal.setLastLocation(null);
                signal.setBlocksTravelled(0);
                break;
            } else if (signal.getEndPoint()._1().equals(new BlockPos(nextLocation))) {
                TileEntity masterTE = world.getTileEntity(signal.getEndPoint()._2());
                if (masterTE instanceof SignalTileEntity) {
                    SignalTileEntity masterTESignal = (SignalTileEntity) masterTE;

                    ControllerMasterTileEntity controller = (ControllerMasterTileEntity) Signalbox.instance.getControllerDispatcher().getControllers().get(signal.getFrequency());

                    if (masterTESignal.getSignalVariant() == controller.getVariantOn()) {
                        masterTESignal.setSignalVariant(controller.getVariantOn());
                    } else if (masterTESignal.getSignalVariant() == controller.getNextOccupied()) {
                        masterTESignal.setSignalVariant(controller.getVariantOff());
                    } else if (masterTESignal.getSignalVariant() == controller.getVariantOff()) {
                        masterTESignal.setSignalVariant(controller.getNextOccupied());
                    } else {
                        masterTESignal.setSignalVariant(controller.getVariantOff());
                    }

                    masterTESignal.setLastTickTimedOut(false);
                    masterTESignal.markDirty();
                    masterTESignal.setLastLocation(null);
                    masterTESignal.setBlocksTravelled(0);
                    break;
                } else if (world.getBlockState(signal.getEndPoint()._2()).getBlock() instanceof BaseSignal) {
                    SignalTileEntity lastSignal = (SignalTileEntity) world.getTileEntity(signal.getEndPoint()._2());
                    ControllerMasterTileEntity controller = (ControllerMasterTileEntity) Signalbox.instance.getControllerDispatcher().getControllers().get(signal.getFrequency());
                    if (controller != null && lastSignal != null) {
                        if (lastSignal.getSignalVariant() == controller.getVariantOn()) {
                            signal.setSignalVariant(controller.getVariantOn());
                        } else if (lastSignal.getSignalVariant() == controller.getNextOccupied()) {
                            signal.setSignalVariant(controller.getVariantOff());
                        } else if (lastSignal.getSignalVariant() == controller.getVariantOff()) {
                            signal.setSignalVariant(controller.getNextOccupied());
                        } else {
                            signal.setSignalVariant(controller.getVariantOff());
                        }
                    } else {
                        signal.setSignalVariant(getNextOccupied());
                    }
                    signal.setLastTickTimedOut(false);
                    markDirty();
                    signal.updateBlock();
                    signal.setLastLocation(null);
                    signal.setBlocksTravelled(0);
                    break;
                }
            }

            signal.setLastMotion(nextLocation.subtract(signal.getLastLocation()));

            signal.setBlocksTravelled(signal.getBlocksTravelled() + 1);
            if (signal.getBlocksTravelled() >= 5000 || nextLocation == signal.getLastLocation()) {
                signal.setSignalVariant(getVariantOff());
                signal.setLastTickTimedOut(true);
                markDirty();
                signal.updateBlock();
                signal.setLastLocation(null);
                signal.setBlocksTravelled(0);
                break;
            }

            signal.setLastLocation(nextLocation);
            updateSignal();
        }
    }

    public int getNextOccupied() {
        return nextOccupied;
    }

    public void setNextOccupied(int nextOccupied) {
        this.nextOccupied = nextOccupied;
    }
}
