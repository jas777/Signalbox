package com.jas777.signalbox.network.signalpacket;

import com.jas777.signalbox.Signalbox;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.io.IOException;

public abstract class SignalPacket {

    public abstract void readData(SignalboxInputStream in) throws IOException;

    public abstract void writeData(SignalboxOutputStream out) throws IOException;

    public abstract PacketType getType();

    public FMLProxyPacket getPacket() {
        ByteBuf byteBuf = Unpooled.buffer();
        try (ByteBufOutputStream out = new ByteBufOutputStream(byteBuf);
             SignalboxOutputStream data = new SignalboxOutputStream(out)) {
            data.writeEnum(getType());
            writeData(data);
            return new FMLProxyPacket(new PacketBuffer(byteBuf), "SBX");
        } catch (IOException e) {
            e.printStackTrace();
        }
        PacketBuffer buffer = new PacketBuffer(byteBuf);
        buffer.writeByte(-1);
        return new FMLProxyPacket(buffer, "SBX");
    }

}
