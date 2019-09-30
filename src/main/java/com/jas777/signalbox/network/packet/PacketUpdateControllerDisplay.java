package com.jas777.signalbox.network.packet;

import com.jas777.signalbox.tileentity.ControllerDisplayTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateControllerDisplay implements IMessage {

    private BlockPos pos;
    private int channel;
    private int id;
    private int variantOn;
    private boolean active;

    public PacketUpdateControllerDisplay(BlockPos pos, int channel, int id, int variantOn, boolean active) {
        this.pos = pos;
        this.channel = channel;
        this.id = id;
        this.variantOn = variantOn;
        this.active = active;
    }

    public PacketUpdateControllerDisplay(ControllerDisplayTileEntity te) {
        this(te.getPos(), te.getChannel(), te.getId(), te.getSpeedLimit(), te.isActive());
    }

    public PacketUpdateControllerDisplay() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        channel = buf.readInt();
        id = buf.readInt();
        variantOn = buf.readInt();
        active = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(channel);
        buf.writeInt(id);
        buf.writeInt(variantOn);
        buf.writeBoolean(active);
    }

    public static class Handler implements IMessageHandler<PacketUpdateControllerDisplay, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateControllerDisplay message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                ControllerDisplayTileEntity te = (ControllerDisplayTileEntity) Minecraft.getMinecraft().world.getTileEntity(message.pos);
                te.setChannel(message.channel);
                te.setId(message.id);
                te.setSpeedLimit(message.variantOn);
                te.setActive(message.active);
            });
            return null;
        }
    }

}
