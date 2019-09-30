package com.jas777.signalbox.network.packet;

import com.jas777.signalbox.tileentity.ControllerMasterTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestUpdateControllerMaster implements IMessage {

    private BlockPos pos;
    private int dimension;

    public PacketRequestUpdateControllerMaster(BlockPos pos, int dimension) {
        this.pos = pos;
        this.dimension = dimension;
    }

    public PacketRequestUpdateControllerMaster(ControllerMasterTileEntity te) {
        this(te.getPos(), te.getWorld().provider.getDimension());
    }

    public PacketRequestUpdateControllerMaster() {
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

    public static class Handler implements IMessageHandler<PacketRequestUpdateControllerMaster, PacketUpdateControllerMaster> {

        @Override
        public PacketUpdateControllerMaster onMessage(PacketRequestUpdateControllerMaster message, MessageContext ctx) {
            World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension);
            ControllerMasterTileEntity te = (ControllerMasterTileEntity) world.getTileEntity(message.pos);
            if (te != null) {
                return new PacketUpdateControllerMaster(te);
            } else {
                return null;
            }
        }

    }

}
