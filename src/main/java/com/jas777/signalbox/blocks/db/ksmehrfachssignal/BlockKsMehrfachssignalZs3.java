package com.jas777.signalbox.blocks.db.ksmehrfachssignal;

import com.jas777.signalbox.blocks.BaseDisplay;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class BlockKsMehrfachssignalZs3 extends BaseDisplay {

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(1 * 0.0625, 0, 2 * 0.0625, 13 * 0.0625, 16 * 0.0625, 13 * 0.0625);

    public BlockKsMehrfachssignalZs3() {
        super("db_ks_mehrfachssignal_zs3", Material.IRON);
        this.setDefaultState(this.getDefaultState().withProperty(ACTIVE, Boolean.TRUE));

        this.displayX1 = 5 * 0.0625;
        this.displayX2 = 11 * 0.0625;

        this.displayY1 = 2 * 0.0625;
        this.displayY2 = 9 * 0.0625;

        this.displayZ = 2 * 0.0625;

    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING, ACTIVE});
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return BOUNDING_BOX;
    }
}