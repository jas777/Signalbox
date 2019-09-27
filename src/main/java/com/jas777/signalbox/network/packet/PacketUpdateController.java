package com.jas777.signalbox.network.packet;

import com.jas777.signalbox.tileentity.ControllerTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateController implements IMessage {

    private BlockPos pos;
    private int channel;
    private int id;
    private int variantOn;
    private int variantOff;
    private boolean active;

    public PacketUpdateController(BlockPos pos, int channel, int id, int variantOn, int variantOff, boolean active) {
        this.pos = pos;
        this.channel = channel;
        this.id = id;
        this.variantOn = variantOn;
        this.variantOff = variantOff;
        this.active = active;
    }

    public PacketUpdateController(ControllerTileEntity te) {
        this(te.getPos(), te.getChannel(), te.getId(), te.getVariantOn(), te.getVariantOff(), te.isActive());
    }

    public PacketUpdateController() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        channel = buf.readInt();
        id = buf.readInt();
        variantOn = buf.readInt();
        variantOff = buf.readInt();
        active = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(channel);
        buf.writeInt(id);
        buf.writeInt(variantOn);
        buf.writeInt(variantOff);
        buf.writeBoolean(active);
    }

    public static class Handler implements IMessageHandler<PacketUpdateController, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateController message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                ControllerTileEntity te = (ControllerTileEntity) Minecraft.getMinecraft().world.getTileEntity(message.pos);
                te.setChannel(message.channel);
                te.setId(message.id);
                te.setVariantOn(message.variantOn);
                te.setVariantOff(message.variantOff);
                te.setActive(message.active);
            });
            return null;
        }
    }

}
