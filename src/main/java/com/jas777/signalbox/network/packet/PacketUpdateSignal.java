package com.jas777.signalbox.network.packet;

import com.jas777.signalbox.signal.SignalMode;
import com.jas777.signalbox.tileentity.SignalTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateSignal implements IMessage {

    private BlockPos pos;
    private int channel;
    private int id;
    private int variant;
    private int mode;

    public PacketUpdateSignal(BlockPos pos, int channel, int id, int variant, int mode) {
        this.pos = pos;
        this.channel = channel;
        this.id = id;
        this.variant = variant;
        this.mode = mode;
    }

    public PacketUpdateSignal(SignalTileEntity te) {
        this(te.getPos(), te.getChannel(), te.getId(), te.getSignalVariant(), te.getMode().ordinal());
    }

    public PacketUpdateSignal() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        channel = buf.readInt();
        id = buf.readInt();
        variant = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(channel);
        buf.writeInt(id);
        buf.writeInt(variant);
        buf.writeInt(mode);
    }

    public static class Handler implements IMessageHandler<PacketUpdateSignal, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateSignal message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                SignalTileEntity te = (SignalTileEntity) Minecraft.getMinecraft().world.getTileEntity(message.pos);
                te.setChannel(message.channel);
                te.setId(message.id);
                te.setSignalVariant(message.variant);
                te.setMode(SignalMode.values()[message.mode]);
            });
            return null;
        }
    }

}
