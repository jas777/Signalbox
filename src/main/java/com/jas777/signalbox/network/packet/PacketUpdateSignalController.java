package com.jas777.signalbox.network.packet;

import com.jas777.signalbox.base.signal.SignalMode;
import com.jas777.signalbox.tileentity.SignalControllerTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateSignalController implements IMessage {

    private BlockPos pos;
    private int frequency;
    private int subFrequency;
    private int variantOn;
    private int variantOff;
    private int variantNoc;
    private SignalMode mode;
    private boolean active;

    public PacketUpdateSignalController(BlockPos pos, int frequency, int subFrequency, int variantOn, int variantOff, int variantNoc, SignalMode mode, boolean active) {
        this.pos = pos;
        this.frequency = frequency;
        this.subFrequency = subFrequency;
        this.variantOn = variantOn;
        this.variantOff = variantOff;
        this.variantNoc = variantNoc;
        this.mode = mode;
        this.active = active;
    }

    public PacketUpdateSignalController(SignalControllerTileEntity te) {
        this(te.getPos(), te.getFrequency(), te.getSubFrequency(), te.getVariantOn(), te.getVariantOff(), te.getVariantNoc(), te.getMode(), te.isActive());
    }

    public PacketUpdateSignalController() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        frequency = buf.readInt();
        subFrequency = buf.readInt();
        variantOn = buf.readInt();
        variantOff = buf.readInt();
        variantNoc = buf.readInt();
        mode = SignalMode.values()[buf.readInt()];
        active = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(frequency);
        buf.writeInt(subFrequency);
        buf.writeInt(variantOn);
        buf.writeInt(variantOff);
        buf.writeInt(variantNoc);
        buf.writeInt(mode.ordinal());
        buf.writeBoolean(active);
    }

    public static class Handler implements IMessageHandler<PacketUpdateSignalController, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateSignalController message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                SignalControllerTileEntity te = (SignalControllerTileEntity) Minecraft.getMinecraft().world.getTileEntity(message.pos);
                if (te == null) return;
                te.setFrequency(message.frequency);
                te.setSubFrequency(message.subFrequency);
                te.setVariantOn(message.variantOn);
                te.setVariantOff(message.variantOff);
                te.setVariantNoc(message.variantNoc);
                te.setMode(message.mode);
                te.setActive(message.active);
                System.out.println(message.frequency);
                System.out.println(message.subFrequency);
                System.out.println(message.variantOn);
                System.out.println(message.variantOff);
                System.out.println(message.variantNoc);
                System.out.println(message.mode);
                System.out.println(message.active);
                System.out.println(te);
                System.out.println("---------------");
            });
            return null;
        }
    }

}
