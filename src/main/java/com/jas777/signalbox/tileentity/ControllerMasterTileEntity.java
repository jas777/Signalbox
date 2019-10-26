package com.jas777.signalbox.tileentity;

import com.jas777.signalbox.Signalbox;
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
import com.jas777.signalbox.util.SignalboxTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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

public class ControllerMasterTileEntity extends TileEntity implements ITickable, GuiUpdateHandler, CanBePowered, Controller, SignalboxTileEntity {

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

    public void updateSignal(int variant) {
        if (getSignal() == null)
            return;

        if (getSignal() != null) {
            if (isActive()) {
                Signalbox.instance.getChannelDispatcher().dispatchMessage(world, getChannel(), getId(), variant);
            } else {
                Signalbox.instance.getChannelDispatcher().dispatchMessage(world, getChannel(), getId(), variant);
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
        updateSignal(isActive() ? getVariantOn() : getVariantOff());
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
        updateSignal(isActive() ? getVariantOn() : getVariantOff());
    }

    public void setVariantOn(int variant) {
        this.variantOn = variant;
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateControllerMaster(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        updateSignal(isActive() ? getVariantOn() : getVariantOff());
    }

    public void setVariantOff(int variantOff) {
        this.variantOff = variantOff;
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateControllerMaster(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
        updateSignal(isActive() ? getVariantOn() : getVariantOff());
    }

    public int getMaxVariant() {

        Channel dispatchChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);

        if (dispatchChannel == null || dispatchChannel.getReceivers().get(id) == null) return 0;

        TileEntity tileEntity = world.getTileEntity(dispatchChannel.getReceivers().get(id));

        if (tileEntity == null) return 0;

        HasVariant signal = (HasVariant) tileEntity.getBlockType();

        return Collections.max(signal.getSignalVariant().getAllowedValues());
    }

    public SignalTileEntity getSignal() {
        if (!world.isRemote) {
            if (Signalbox.instance.getChannelDispatcher() != null) {
                Channel dispatchChannel = Signalbox.instance.getChannelDispatcher().getChannels().get(channel);
                if (dispatchChannel == null || dispatchChannel.getReceivers().get(id) == null) return null;
                TileEntity tileEntity = world.getTileEntity(dispatchChannel.getReceivers().get(id));
                if (!(tileEntity instanceof SignalTileEntity)) return null;
                return (SignalTileEntity) tileEntity;
            }
        }
        return null;
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
        updateSignal(isActive() ? getVariantOn() : getVariantOff());
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

            int tickResult = -2;

            if (Signalbox.instance.IR_LOADED) {
                tickResult = ImmersiveRailroading.doNormalTick(this);
            }

            switch (tickResult) {
                case -1:
                    signal.updateSignal();
                    break;
                default:
                    updateSignal(tickResult);
                    break;
            }

        }

    }

    public boolean doChunkLoad(BlockPos nextPos) {
        if (!world.isRemote) {
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

                ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(Signalbox.instance, world, ForgeChunkManager.Type.NORMAL);

                signal.setLastTicket(ticket);
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
        }

        return true;
    }

    public int getNextOccupied() {
        return nextOccupied;
    }

    public void setNextOccupied(int nextOccupied) {
        this.nextOccupied = nextOccupied;
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            if (getSignal() != null && getSignal().getMode() == SignalMode.AUTO) {
                updateSignalDistant();
            } else if (getSignal() != null && getSignal().getMode() != SignalMode.AUTO) {
                updateSignal(isActive() ? getVariantOn() : getVariantOff());
            }
        }
    }

}
