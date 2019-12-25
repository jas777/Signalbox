package com.jas777.signalbox.channel;

import com.jas777.signalbox.util.CanReceive;
import com.jas777.signalbox.util.HasParts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.HashMap;

public class ChannelDispatcher {

    private HashMap<Integer, Channel> channels;

    public ChannelDispatcher() {
        this.channels = new HashMap<Integer, Channel>();
    }

    public HashMap<Integer, Channel> getChannels() {
        return channels;
    }

    public void dispatchMessage(World world, int channel, int id, int variant) {
        Channel channelToDispatch = channels.get(channel);
        if (world == null || channelToDispatch == null || channelToDispatch.getReceivers().get(id) == null) return;
        if (channel <= 0 || id < 0) return;
        TileEntity tileEntity = world.getTileEntity(channelToDispatch.getReceivers().get(id));
        if (!(tileEntity instanceof CanReceive)) return;
        ((CanReceive) tileEntity).setData(Unpooled.buffer().writeInt(variant));
        ((CanReceive) tileEntity).updateBlock();
        tileEntity.markDirty();
        if (tileEntity.getWorld().getBlockState(tileEntity.getPos()).getBlock() instanceof HasParts) {
            ((HasParts) tileEntity.getWorld().getBlockState(tileEntity.getPos()).getBlock()).updateParts(world, tileEntity.getPos(), tileEntity.getWorld().getBlockState(tileEntity.getPos()));
        }
    }

    public void dispatchMessage(World world, int channel, int id, String variant) {
        Channel channelToDispatch = channels.get(channel);
        if (world == null || channelToDispatch == null || channelToDispatch.getReceivers().get(id) == null) return;
        if (channel <= 0 || id < 0) return;
        TileEntity tileEntity = world.getTileEntity(channelToDispatch.getReceivers().get(id));
        if (!(tileEntity instanceof CanReceive)) return;
        ByteBuf buf = Unpooled.buffer();
        ByteBufUtils.writeUTF8String(buf, variant);
        ((CanReceive) tileEntity).setData(buf);
        tileEntity.markDirty();
        ((CanReceive) tileEntity).updateBlock();
        if (tileEntity.getWorld().getBlockState(tileEntity.getPos()).getBlock() instanceof HasParts) {
            ((HasParts) tileEntity.getWorld().getBlockState(tileEntity.getPos()).getBlock()).updateParts(world, tileEntity.getPos(), tileEntity.getWorld().getBlockState(tileEntity.getPos()));
        }
    }

    public CanReceive getReceiver(World world, int channel, int id) {
        Channel signalChannel = channels.get(channel);
        if (signalChannel == null) return null;
        BlockPos pos = signalChannel.getReceivers().get(id);
        if (pos == null) return null;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof CanReceive) {
            return (CanReceive) te;
        }
        return null;
    }

    public boolean tune(int channel, int id, BlockPos pos) {

        if (!channels.containsKey(channel)) {
            channels.put(channel, new Channel());
        }

        channels.forEach((i, c) -> {
            c.removeReceiver(id, pos);
        });

        Channel dispatchToTune = channels.get(channel);
        return dispatchToTune.addReceiver(id, pos);
    }

}
