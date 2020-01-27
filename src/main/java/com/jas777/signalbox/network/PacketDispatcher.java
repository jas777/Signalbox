package com.jas777.signalbox.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class PacketDispatcher {

    public static void sendToServer(SignalPacket packet) {
        PacketHandler.INSTANCE.eventChannel.sendToServer(packet.getPacket());
    }

    public static void sendToPlayer(SignalPacket packet, EntityPlayerMP player) {
        PacketHandler.INSTANCE.eventChannel.sendTo(packet.getPacket(), player);
    }

    public static void sendToPlayer(Packet<?> packet, EntityPlayerMP player) {
        player.connection.sendPacket(packet);
    }

    public static void sendToAll(SignalPacket packet) {
        PacketHandler.INSTANCE.eventChannel.sendToAll(packet.getPacket());
    }

    public static NetworkRegistry.TargetPoint targetPoint(int dim, BlockPos pos, double range) {
        return new NetworkRegistry.TargetPoint(dim, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, range);
    }

    public static NetworkRegistry.TargetPoint targetPoint(int dim, Vec3d vec, double range) {
        return new NetworkRegistry.TargetPoint(dim, vec.x, vec.y, vec.z, range);
    }

    public static NetworkRegistry.TargetPoint targetPoint(int dim, double x, double y, double z, double range) {
        return new NetworkRegistry.TargetPoint(dim, x, y, z, range);
    }

    public static void sendToAllAround(SignalPacket packet, NetworkRegistry.TargetPoint zone) {
        PacketHandler.INSTANCE.eventChannel.sendToAllAround(packet.getPacket(), zone);
    }

    public static void sendToDimension(SignalPacket packet, int dimensionId) {
        PacketHandler.INSTANCE.eventChannel.sendToDimension(packet.getPacket(), dimensionId);
    }

    public static void sendToWatchers(SignalPacket packet, WorldServer world, int worldX, int worldZ) {
        sendToWatchers(packet.getPacket(), world, worldX, worldZ);
    }

    public static void sendToWatchers(Packet<?> packet, WorldServer world, int worldX, int worldZ) {
        int chunkX = worldX >> 4;
        int chunkZ = worldZ >> 4;

        PlayerChunkMapEntry chunkManager = world.getPlayerChunkMap().getEntry(chunkX, chunkZ);
        if (chunkManager != null)
            chunkManager.sendPacket(packet);
    }

}