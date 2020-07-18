package com.jas777.signalbox.blocks.de;

import com.jas777.signalbox.base.signal.BaseSignal;
import com.jas777.signalbox.tileentity.SignalTileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;
import java.util.HashMap;

public class BlockKompaktvorsignalHead extends BaseSignal {

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(1 * 0.0625, 0, 2 * 0.0625, 13 * 0.0625, 16 * 0.0625, 13 * 0.0625);

    private static final PropertyInteger SIGNAL_VARIANT = PropertyInteger.create("signal_variant", 0, 2);

    public BlockKompaktvorsignalHead() {
        super("de_kompaktvorsignal_head", Material.IRON);
        this.setDefaultState(this.getDefaultState().withProperty(ACTIVE, Boolean.TRUE).withProperty(SIGNAL_VARIANT, 0));
        this.specialVariants = new HashMap<>();
        // setCreativeTab(SignalboxTabs.DE_CREATIVE_TAB);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ACTIVE, SIGNAL_VARIANT);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {

        SignalTileEntity tileEntity = (SignalTileEntity) worldIn.getTileEntity(pos);

        assert tileEntity != null;

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

    public PropertyInteger getSignalVariant() {
        return SIGNAL_VARIANT;
    }
}
