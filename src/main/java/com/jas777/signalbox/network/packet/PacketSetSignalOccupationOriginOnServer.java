package com.jas777.signalbox.network.packet;

import com.jas777.signalbox.tileentity.SignalTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetSignalOccupationOriginOnServer implements IMessage {

    private BlockPos tePos;
    private BlockPos newPosition;

    public PacketSetSignalOccupationOriginOnServer(BlockPos tePos, BlockPos newPosition) {
        this.tePos = tePos;
        this.newPosition = newPosition;
    }

    public PacketSetSignalOccupationOriginOnServer() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        tePos = BlockPos.fromLong(buf.readLong());
        newPosition = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(tePos.toLong());
        buf.writeLong(newPosition.toLong());
    }

    public static class Handler implements IMessageHandler<PacketSetSignalOccupationOriginOnServer, IMessage>
    {

        @Override
        public IMessage onMessage(PacketSetSignalOccupationOriginOnServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketSetSignalOccupationOriginOnServer message, MessageContext ctx)
        {
            World world = ctx.getServerHandler().player.world;
            SignalTileEntity te = (SignalTileEntity) world.getTileEntity(message.tePos);
            te.setOccupationOrigin(message.newPosition);
        }
    }
}
