package com.jas777.signalbox.gui;

import com.jas777.signalbox.network.signalpacket.SignalboxInputStream;
import com.jas777.signalbox.network.signalpacket.SignalboxOutputStream;
import com.jas777.signalbox.util.HasWorld;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public interface GuiUpdateHandler extends HasWorld {

    default void writeGuiData(SignalboxOutputStream data) throws IOException { }

    default void readGuiData(SignalboxInputStream data, EntityPlayer sender) throws IOException { }

}
