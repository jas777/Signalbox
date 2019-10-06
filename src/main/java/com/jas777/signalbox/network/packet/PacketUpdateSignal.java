package com.jas777.signalbox.network.packet;

import com.jas777.signalbox.signal.SignalMode;
import com.jas777.signalbox.tileentity.SignalTileEntity;
import io.netty.buffer.ByteBuf;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateSignal implements IMessage {

    private BlockPos pos;
    private int channel;
    private int id;
    private int variant;
    private int mode;
    private BlockPos origin;
    private BlockPos lastLocation;
    private BlockPos lastMotion;
    private int blocksTravelled;

    public PacketUpdateSignal(BlockPos pos, int channel, int id, int variant, int mode, BlockPos origin, Vec3d lastLocation, Vec3d motion, int blocksTravelled) {
        this.pos = pos;
        this.channel = channel;
        this.id = id;
        this.variant = variant;
        this.mode = mode;
        this.origin = origin;
        this.lastLocation = lastLocation == null ? null : new BlockPos(lastLocation);
        this.lastMotion = motion == null ? null : new BlockPos(motion);
        this.blocksTravelled = blocksTravelled;
    }

    public PacketUpdateSignal(SignalTileEntity te) {
        this(te.getPos(), te.getChannel(), te.getId(), te.getSignalVariant(), te.getMode().ordinal(), new BlockPos(te.getOrigin()), te.getLastLocation(), te.getLastMotion(), te.getBlocksTravelled());
    }

    public PacketUpdateSignal() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        channel = buf.readInt();
        id = buf.readInt();
        variant = buf.readInt();
        mode = buf.readInt();
        long originLong = buf.readLong();
        origin = originLong == 0 ? null : BlockPos.fromLong(originLong);
        long lastLocationLong = buf.readLong();
        lastLocation = lastLocationLong == 0 ? null : BlockPos.fromLong(lastLocationLong);
        long lastMotionLong = buf.readLong();
        lastMotion = lastMotionLong == 0 ? null : BlockPos.fromLong(lastMotionLong);
        blocksTravelled = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(channel);
        buf.writeInt(id);
        buf.writeInt(variant);
        buf.writeInt(mode);
        buf.writeLong(origin == null ? 0 : origin.toLong());
        buf.writeLong(lastLocation == null ? 0 : lastLocation.toLong());
        buf.writeLong(lastMotion == null ? 0 : lastMotion.toLong());
        buf.writeInt(blocksTravelled);
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
                te.setOrigin(message.origin == null ? null : new Vec3d(message.origin));
                te.setLastLocation(message.lastLocation == null ? null : new Vec3d(message.lastLocation));
                te.setLastMotion(message.lastMotion == null ? null : new Vec3d(message.lastMotion));
                te.setBlocksTravelled(message.blocksTravelled);
            });
            return null;
        }
    }

}
