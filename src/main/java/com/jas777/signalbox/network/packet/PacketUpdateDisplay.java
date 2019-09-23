package com.jas777.signalbox.network.packet;

import com.jas777.signalbox.tileentity.DisplayTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.Charset;

public class PacketUpdateDisplay implements IMessage {

    private BlockPos pos;
    private boolean active;
    private String textToDisplay;

    public PacketUpdateDisplay(BlockPos pos, boolean active, String textToDisplay) {
        this.pos = pos;
        this.active = active;
        this.textToDisplay = textToDisplay;
    }

    public PacketUpdateDisplay(DisplayTileEntity te) {
        this(te.getPos(), te.isActive(), te.getDisplayedText());
    }

    public PacketUpdateDisplay() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        active = buf.readBoolean();
        textToDisplay = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeBoolean(active);
        ByteBufUtils.writeUTF8String(buf, textToDisplay);
    }

    public static class Handler implements IMessageHandler<PacketUpdateDisplay, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateDisplay message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                DisplayTileEntity te = (DisplayTileEntity) Minecraft.getMinecraft().world.getTileEntity(message.pos);
                te.setActive(message.active);
                te.setDisplayedText(message.textToDisplay);
            });
            return null;
        }
    }

}
