package com.jas777.signalbox.blocks.pl;

import com.jas777.signalbox.blocks.BaseSignal;
import com.jas777.signalbox.tileentity.SignalTileEntity;
import com.jas777.signalbox.util.HasVariant;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class BlockGroundSignal extends BaseSignal implements HasVariant {

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(4 * 0.0625, 0, 5 * 0.0625, 12 * 0.0625, 1, 11 * 0.0625);

    public static final PropertyInteger SIGNAL_VARIANT = PropertyInteger.create("signal_variant", 0, 3);

    public BlockGroundSignal() {
        super("pl_ground_signal", Material.IRON);

        this.setDefaultState(this.getDefaultState().withProperty(ACTIVE, Boolean.FALSE).withProperty(SIGNAL_VARIANT, 0));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING, ACTIVE, SIGNAL_VARIANT});
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        SignalTileEntity tileEntity = (SignalTileEntity) worldIn.getTileEntity(pos);
        state = state.withProperty(SIGNAL_VARIANT, tileEntity.getSignalVariant());
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
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

    @Override
    public PropertyInteger getSignalVariant() {
        return SIGNAL_VARIANT;
    }
}
