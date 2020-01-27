package com.jas777.signalbox.network;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

public class SignalboxOutputStream extends DataOutputStream {

    public SignalboxOutputStream(OutputStream os) {
        super(os);
    }

    public void writeBlockPos(BlockPos pos) throws IOException {
        writeLong(pos.toLong());
    }

    public void writeUUID(@Nullable UUID uuid) throws IOException {
        if (uuid == null) {
            writeLong(0);
            writeLong(0);
        } else {
            writeLong(uuid.getMostSignificantBits());
            writeLong(uuid.getLeastSignificantBits());
        }
    }

    public void writeBitSet(BitSet bitSet) throws IOException {
        byte[] bytes = bitSet.toByteArray();
        writeByte(bytes.length);
        write(bytes);
    }

    public void writeEnum(Enum<?> value) throws IOException {
        assert value.ordinal() < Byte.MAX_VALUE;
        writeByte(value.ordinal());
    }

    public void writeNBT(@Nullable NBTTagCompound nbt) throws IOException {
        if (nbt == null) {
            writeByte(0);
        } else {
            try (DataOutputStream nbtStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(this)))) {
                CompressedStreamTools.write(nbt, nbtStream);
            }
        }
    }

}
