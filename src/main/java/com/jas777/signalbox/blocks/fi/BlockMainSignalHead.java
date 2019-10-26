package com.jas777.signalbox.blocks.fi;

import com.jas777.signalbox.blocks.BaseSignal;
import com.jas777.signalbox.tileentity.SignalTileEntity;
import com.jas777.signalbox.util.HasVariant;
import com.jas777.signalbox.util.SignalboxTabs;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class BlockMainSignalHead extends BaseSignal implements HasVariant {

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(1 * 0.0625, 0, 2 * 0.0625, 13 * 0.0625, 16 * 0.0625, 13 * 0.0625);

    public static final PropertyInteger SIGNAL_VARIANT = PropertyInteger.create("signal_variant", 0, 2);

    public BlockMainSignalHead() {
        super("fi_main_signal_head", Material.IRON);
        this.setDefaultState(this.getDefaultState().withProperty(ACTIVE, Boolean.TRUE).withProperty(SIGNAL_VARIANT, 0));
        setCreativeTab(SignalboxTabs.FI_CREATIVE_TAB);
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
