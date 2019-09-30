package com.jas777.signalbox.network.packet;

import com.jas777.signalbox.tileentity.ControllerDisplayTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestUpdateControllerDisplay implements IMessage {

    private BlockPos pos;
    private int dimension;

    public PacketRequestUpdateControllerDisplay(BlockPos pos, int dimension) {
        this.pos = pos;
        this.dimension = dimension;
    }

    public PacketRequestUpdateControllerDisplay(ControllerDisplayTileEntity te) {
        this(te.getPos(), te.getWorld().provider.getDimension());
    }

    public PacketRequestUpdateControllerDisplay() {
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(dimension);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        dimension = buf.readInt();
    }

    public static class Handler implements IMessageHandler<PacketRequestUpdateControllerDisplay, PacketUpdateControllerDisplay> {

        @Override
        public PacketUpdateControllerDisplay onMessage(PacketRequestUpdateControllerDisplay message, MessageContext ctx) {
            World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension);
            ControllerDisplayTileEntity te = (ControllerDisplayTileEntity) world.getTileEntity(message.pos);
            if (te != null) {
                return new PacketUpdateControllerDisplay(te);
            } else {
                return null;
            }
        }

    }

}
