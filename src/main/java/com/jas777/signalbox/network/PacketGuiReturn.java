package com.jas777.signalbox.network;

import com.jas777.signalbox.gui.GuiUpdateHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.io.IOException;
import java.util.Objects;

public class PacketGuiReturn extends SignalPacket {

    private EntityPlayer sender;
    private GuiUpdateHandler object;
    private byte[] extra;

    public PacketGuiReturn(EntityPlayer sender) {
        this.sender = sender;
    }

    public PacketGuiReturn(GuiUpdateHandler object) {
        this.object = object;
    }

    public PacketGuiReturn(GuiUpdateHandler object, byte[] extra) {
        this.object = object;
        this.extra = extra;
    }

    @Override
    public void writeData(SignalboxOutputStream data) throws IOException {

        data.writeInt(Objects.requireNonNull(object.theWorld()).provider.getDimension());

        if (object instanceof TileEntity) {

            TileEntity tile = (TileEntity) object;
            data.writeBoolean(true);
            BlockPos pos = tile.getPos();
            data.writeInt(pos.getX());
            data.writeInt(pos.getY());
            data.writeInt(pos.getZ());

        } else if (object instanceof Entity) {

            Entity entity = (Entity) object;
            data.writeBoolean(false);
            data.writeInt(entity.getEntityId());

        } else
            return;

        object.writeGuiData(data);

        if (extra != null)
            data.write(extra);

    }

    @Override
    public void readData(SignalboxInputStream data) throws IOException {

        int dim = data.readInt();
        World world = DimensionManager.getWorld(dim);
        boolean tileReturn = data.readBoolean();

        if (tileReturn) {
            int x = data.readInt();
            int y = data.readInt();
            int z = data.readInt();

            TileEntity t = world.getTileEntity(new BlockPos(x, y, z));

            if (t instanceof GuiUpdateHandler)
                ((GuiUpdateHandler) t).readGuiData(data, sender);

        } else {

            int entityId = data.readInt();
            Entity entity = world.getEntityByID(entityId);

            if (entity instanceof GuiUpdateHandler)
                ((GuiUpdateHandler) entity).readGuiData(data, sender);

        }

    }

    public void sendPacket() {
        PacketDispatcher.sendToServer(this);
    }

    @Override
    public PacketType getType() {
        return PacketType.GUI_RETURN;
    }

}