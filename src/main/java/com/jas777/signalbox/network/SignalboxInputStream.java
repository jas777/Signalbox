package com.jas777.signalbox.network;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

public class SignalboxInputStream extends DataInputStream {

    public SignalboxInputStream(InputStream is) {
        super(is);
    }

    public BlockPos readBlockPos() throws IOException {
        return BlockPos.fromLong(readLong());
    }

    public UUID readUUID() throws IOException {
        return new UUID(readLong(), readLong());
    }

    public BitSet readBitSet() throws IOException {
        int length = readByte();
        byte[] bytes = new byte[length];
        readFully(bytes);
        return BitSet.valueOf(bytes);
    }

    public <T extends Enum<T>> T readEnum(T[] enumConstants) throws IOException {
        return enumConstants[readByte()];
    }

    public @Nullable
    NBTTagCompound readNBT() throws IOException {
        mark(1);
        byte b = readByte();

        NBTTagCompound nbt = null;
        if (b != 0) {
            reset();
            try (DataInputStream nbtStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(this)))) {
                nbt = CompressedStreamTools.read(nbtStream, new NBTSizeTracker(2097152L));
            }
        }
        return nbt;
    }

}