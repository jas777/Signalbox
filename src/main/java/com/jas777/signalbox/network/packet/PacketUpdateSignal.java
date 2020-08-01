package com.jas777.signalbox.network.packet;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.tileentity.SignalTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateSignal implements IMessage {

    private BlockPos pos;

    private int channel;
    private int frequency;
    private int signalVariant;

    public PacketUpdateSignal(BlockPos pos, int channel, int frequency, int signalVariant) {
        this.pos = pos;
        this.channel = channel;
        this.frequency = frequency;
        this.signalVariant = signalVariant;
    }

    public PacketUpdateSignal(SignalTileEntity te) {
        this(te.getPos(), te.getChannel() != null ? te.getChannel().getFrequency() : 0, te.getFrequency(), te.getSignalVariant());
    }

    public PacketUpdateSignal() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        channel = buf.readInt();
        frequency = buf.readInt();
        signalVariant = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(channel);
        buf.writeInt(frequency);
        buf.writeInt(signalVariant);
    }

    public static class Handler implements IMessageHandler<PacketUpdateSignal, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateSignal message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {

                TileEntity te = Minecraft.getMinecraft().world.getTileEntity(message.pos);
                if (!(te instanceof SignalTileEntity)) return;
                SignalTileEntity signal = (SignalTileEntity) te;

                if (message.frequency != 0 && message.channel != 0) {
                    signal.setFrequency(message.channel);
                    signal.setChannel(Signalbox.channelDispatcher.addChannel(message.channel));
                    signal.getChannel().tune(message.frequency, signal);
                }

            });
            return null;
        }
    }

}