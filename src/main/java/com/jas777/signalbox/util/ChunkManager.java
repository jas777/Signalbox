package com.jas777.signalbox.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.List;

public class ChunkManager implements ForgeChunkManager.LoadingCallback {

    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> list, World world) {
        for (ForgeChunkManager.Ticket ticket : list) {
            NBTTagCompound nbt = ticket.getModData();
            int x = nbt.getInteger("tileX");
            int y = nbt.getInteger("tileY");
            int z = nbt.getInteger("tileZ");
            TileEntity tile = world.getTileEntity(new BlockPos(x,y,z));
            if (tile instanceof SignalboxTileEntity) {
                ChunkPos lastChunkPos = null;
                for (ChunkPos pos : ticket.getChunkList()) {
                    lastChunkPos = pos;
                    break;
                }
                ForgeChunkManager.forceChunk(ticket, lastChunkPos);
            }
            else {
                ForgeChunkManager.releaseTicket(ticket);
            }
        }
    }

}
