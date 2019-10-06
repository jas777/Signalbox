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
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ControllerMasterTileEntity extends TileEntity implements ITickable, GuiUpdateHandler, CanBePowered, Controller {

    private boolean active;
    private int channel = 0;
    private int id = 0;
    private int variantOn = 1;
    private int variantOff = 0;
    private int ticksPassed = 0;
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

        Signalbox.instance.getControllerDispatcher().getControllers().put(getFrequency(), this);

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
        if (getSignal() == null)
            return;

        if (getSignal() != null) {
            if (isActive()) {
                if (getSignal().getMode() == SignalMode.AUTO) return;
                Signalbox.instance.getChannelDispatcher().dispatchMessage(world, getChannel(), getId(), getVariantOn());
            } else {
                if (getSignal().getMode() == SignalMode.AUTO) return;
                Signalbox.instance.getChannelDispatcher().dispatchMessage(world, getChannel(), getId(), getVariantOff());
            }
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
        if (this.channel != channel) {
            Signalbox.instance.getControllerDispatcher().getControllers().remove(getFrequency());
        }
        this.channel = channel;
        Signalbox.instance.getControllerDispatcher().getControllers().put(getFrequency(), this);
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateControllerMaster(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        updateSignal();
    }

    public void setId(int id) {
        if (this.id != id) {
            Signalbox.instance.getControllerDispatcher().getControllers().remove(getFrequency());
        }
        this.id = id;
        Signalbox.instance.getControllerDispatcher().getControllers().put(getFrequency(), this);
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
        if (signal != null && signal.getMode() == SignalMode.AUTO && Signalbox.instance.IR_LOADED) {
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
                //FMLLog.getLogger().error("Signal failed to load chunk during tick - maybe there are too many signals?");
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
                signal.updateSignal();
            }
            return;
        }

        if (signal.getBlocksTravelled() == 0) {
            if (!(world.getBlockState(signal.getPos()).getBlock() instanceof BaseSignal)) return;
            EnumFacing signalFacing = world.getBlockState(signal.getPos()).getValue(BaseSignal.FACING);
            BlockPos current = new BlockPos(signal.getOrigin());
            BlockPos motionBP = current.offset(signalFacing);

            signal.setLastMotion(new Vec3d(motionBP).subtract(new Vec3d(current)));
            signal.markDirty();
            signal.updateSignal();
        }

        for (int i = 0; i < 2; i++) {
            if (signal.getLastLocation() == null) {
                signal.setLastLocation(signal.getOrigin());
            }

            if (signal.getLastMotion() == null) {
                signal.setLastMotion(new Vec3d(0, 0, 0));
            }

            Vec3d motion = signal.getLastMotion();

            if (motion == null) motion = new Vec3d(0, 0, 0);

            Vec3d nextLocation = ImmersiveRailroading.getNextPosition(signal.getLastLocation(), motion, signal.getWorld(), signal.getLastSwitchInfo());

            if (!doChunkLoad(new BlockPos(nextLocation))) {
                signal.setLastLocation(null);
                signal.setBlocksTravelled(0);
                signal.markDirty();
                signal.updateSignal();
                break;
            }

            if (ImmersiveRailroading.hasStockNearby(signal.getOrigin(), signal.getWorld()) || ImmersiveRailroading.hasStockNearby(nextLocation, signal.getWorld())) {
                signal.setSignalVariant(getVariantOff());
                signal.setLastTickTimedOut(false);
                signal.setLastLocation(null);
                signal.setBlocksTravelled(0);
                signal.markDirty();
                signal.updateSignal();
                break;
            } else if (signal.getEndPoint()._1().equals(new BlockPos(nextLocation))) {
                TileEntity masterTE = world.getTileEntity(signal.getEndPoint()._2());
                if (masterTE instanceof SignalTileEntity && ((SignalTileEntity) masterTE).getMode() == SignalMode.AUTO) {
                    SignalTileEntity masterTESignal = (SignalTileEntity) masterTE;

                    ControllerMasterTileEntity controller = (ControllerMasterTileEntity) Signalbox.instance.getControllerDispatcher().getControllers().get(signal.getFrequency());
                    if (controller == null) break;
                    if (masterTESignal.getSignalVariant() == controller.getVariantOn()) {
                        signal.setSignalVariant(controller.getVariantOn());
                    } else if (masterTESignal.getSignalVariant() == controller.getNextOccupied()) {
                        signal.setSignalVariant(controller.getVariantOn());
                    } else if (masterTESignal.getSignalVariant() == controller.getVariantOff()) {
                        signal.setSignalVariant(controller.getNextOccupied());
                    }

                    signal.setLastTickTimedOut(false);
                    signal.setLastLocation(null);
                    signal.setBlocksTravelled(0);
                    signal.markDirty();
                    signal.updateSignal();
                    break;
                } else if (world.getBlockState(signal.getEndPoint()._2()).getBlock() instanceof BaseSignal) {
                    SignalTileEntity lastSignal = (SignalTileEntity) world.getTileEntity(signal.getEndPoint()._2());
                    ControllerMasterTileEntity controller = (ControllerMasterTileEntity) Signalbox.instance.getControllerDispatcher().getControllers().get(lastSignal.getFrequency());
                    if (controller != null) {
                        if (lastSignal.getSignalVariant() == controller.getVariantOn()) {
                            signal.setSignalVariant(controller.getVariantOn());
                        } else if (lastSignal.getSignalVariant() == controller.getNextOccupied()) {
                            signal.setSignalVariant(controller.getVariantOn());
                        } else if (lastSignal.getSignalVariant() == controller.getVariantOff()) {
                            signal.setSignalVariant(controller.getNextOccupied());
                        }
                    } else {
                        signal.setSignalVariant(getNextOccupied());
                    }
                    signal.setLastTickTimedOut(false);
                    signal.setLastLocation(null);
                    signal.setBlocksTravelled(0);
                    signal.markDirty();
                    signal.updateSignal();
                    break;
                }
            }

            signal.setLastMotion(nextLocation.subtract(signal.getLastLocation()));

            signal.setBlocksTravelled(signal.getBlocksTravelled() + 1);
            if (signal.getBlocksTravelled() >= 5000 || nextLocation == signal.getLastLocation()) {
                signal.setSignalVariant(getVariantOff());
                signal.setLastTickTimedOut(true);
                signal.setLastLocation(null);
                signal.setBlocksTravelled(0);
                signal.markDirty();
                signal.updateSignal();
                break;
            }

            signal.setLastLocation(nextLocation);
            signal.markDirty();
            signal.updateSignal();
        }
    }

    public int getNextOccupied() {
        return nextOccupied;
    }

    public void setNextOccupied(int nextOccupied) {
        this.nextOccupied = nextOccupied;
    }

    @Override
    public void update() {
        ticksPassed++;
        if (ticksPassed >= 5) {
            ticksPassed = 0;
            if (getSignal() != null && getSignal().getMode() == SignalMode.AUTO) {
                updateSignalDistant();
            } else if (getSignal() != null && getSignal().getMode() != SignalMode.AUTO) {
                updateSignal();
            }
        }
    }

}
