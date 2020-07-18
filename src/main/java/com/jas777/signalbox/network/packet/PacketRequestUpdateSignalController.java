package com.jas777.signalbox.network.packet;

import com.jas777.signalbox.tileentity.SignalControllerTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestUpdateSignalController implements IMessage {

    private BlockPos pos;
    private int dimension;

    public PacketRequestUpdateSignalController(BlockPos pos, int dimension) {
        this.pos = pos;
        this.dimension = dimension;
    }

    public PacketRequestUpdateSignalController(SignalControllerTileEntity te) {
        this(te.getPos(), te.getWorld().provider.getDimension());
    }

    public PacketRequestUpdateSignalController() {
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

    public static class Handler implements IMessageHandler<PacketRequestUpdateSignalController, PacketUpdateSignalController> {

        @Override
        public PacketUpdateSignalController onMessage(PacketRequestUpdateSignalController message, MessageContext ctx) {
            World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension);
            SignalControllerTileEntity te = (SignalControllerTileEntity) world.getTileEntity(message.pos);
            if (te != null) {
                return new PacketUpdateSignalController(te);
            } else {
                return null;
            }
        }

    }

}
