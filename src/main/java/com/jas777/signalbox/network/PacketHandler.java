package com.jas777.signalbox.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.io.IOException;

public class PacketHandler {

    public static final PacketHandler INSTANCE = new PacketHandler();

    final FMLEventChannel eventChannel;

    public PacketHandler() {
        eventChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel("SBX");
        eventChannel.register(this);
    }

    @SubscribeEvent
    public void onPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        EntityPlayerMP player = ((NetHandlerPlayServer) event.getHandler()).player;
        onPacketData(event.getPacket().payload(), player, player.getServer());
    }

    @SubscribeEvent
    public void onPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        onPacketData(event.getPacket().payload(), null, Minecraft.getMinecraft());
    }

    private void onPacketData(ByteBuf buf, EntityPlayerMP player, @Nullable IThreadListener listener) {

        SignalboxInputStream data = new SignalboxInputStream(new ByteBufInputStream(buf));

        try {

            SignalPacket packet = null;

            PacketType type = data.readEnum(PacketType.values());

            if (type == null) return;

            switch (type) {
                case GUI_RETURN:
                    packet = new PacketGuiReturn(player);
                    break;
            }

            readPacket(packet, data, listener);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void readPacket(final SignalPacket packet, final SignalboxInputStream data, @Nullable IThreadListener threadListener) {
        if (threadListener != null && !threadListener.isCallingFromMinecraftThread()) {
            threadListener.addScheduledTask(() -> {
                try {
                    packet.readData(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}