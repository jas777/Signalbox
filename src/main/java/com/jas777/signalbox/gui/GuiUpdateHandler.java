package com.jas777.signalbox.gui;

import com.jas777.signalbox.network.SignalboxInputStream;
import com.jas777.signalbox.network.SignalboxOutputStream;
import com.jas777.signalbox.util.interfaces.HasWorld;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public interface GuiUpdateHandler extends HasWorld {

    default void writeGuiData(SignalboxOutputStream data) throws IOException {}

    default void readGuiData(SignalboxInputStream data, EntityPlayer sender) throws IOException {}

}