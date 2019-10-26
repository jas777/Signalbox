package com.jas777.signalbox.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface HasParts {

    void updateParts(World worldIn, BlockPos pos, IBlockState state);

}
