package com.jas777.signalbox.tileentity;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.blocks.BaseDisplay;
import com.jas777.signalbox.channel.Channel;
import com.jas777.signalbox.gui.GuiUpdateHandler;
import com.jas777.signalbox.network.packet.PacketRequestUpdateDisplay;
import com.jas777.signalbox.network.packet.PacketUpdateDisplay;
import com.jas777.signalbox.network.signalpacket.SignalboxInputStream;
import com.jas777.signalbox.network.signalpacket.SignalboxOutputStream;
import com.jas777.signalbox.tileentity.data.TileEntityData;
import com.jas777.signalbox.util.CanBePowered;
import com.jas777.signalbox.util.CanReceive;
import com.jas777.signalbox.util.HasVariant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.io.IOException;

public class DisplayTileEntity extends TileEntity implements GuiUpdateHandler, CanBePowered, CanReceive {

    private String displayedText = "0";
    private int channel = 0;
    private int id = 0;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        displayedText = compound.getString("displayed_text");
        channel = compound.getInteger("channel");
        id = compound.getInteger("display_id");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("displayed_text", displayedText);
        compound.setInteger("channel", channel);
        compound.setInteger("display_id", id);
        return compound;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return false;
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

    @Override
    public void setData(ByteBuf data) {
        setDisplayedText(ByteBufUtils.readUTF8String(data));
    }

    public void setDisplayedText(String displayedText) {
        this.displayedText = displayedText;
    }

    public BaseDisplay getDisplay() {
        return (BaseDisplay) world.getBlockState(pos).getBlock();
    }

    @Override
    public void updateBlock() {
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateDisplay(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
    }

    public void setActive(boolean active) {
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos).withProperty(BaseDisplay.ACTIVE, true/*active*/), 2);
        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateDisplay(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }
    }

    public boolean isActive() {
        return true; //world.getBlockState(pos).getValue(BaseSignal.ACTIVE);
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

        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateDisplay(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }

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

        if (!world.isRemote) {
            Signalbox.network.sendToAllAround(new PacketUpdateDisplay(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
        }

    }

    @Nullable
    @Override
    public World theWorld() {
        return getWorld();
    }

    @Override
    public void writeGuiData(SignalboxOutputStream data) throws IOException {
        data.writeUTF(displayedText);
        data.writeByte(channel);
        data.writeByte(id);
    }

    @Override
    public void readGuiData(SignalboxInputStream data, EntityPlayer sender) throws IOException {
        displayedText = data.readUTF();
        setChannel(data.readByte());
        setId(data.readByte());
    }
}
