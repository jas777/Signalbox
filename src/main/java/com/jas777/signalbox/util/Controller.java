package com.jas777.signalbox.util;

import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface Controller {

    public List<Controller> getSlaves();

    public BlockPos getPosition();

}
