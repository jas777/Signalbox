package com.jas777.signalbox.blocks.de.hv.hauptsignal;

import com.jas777.signalbox.blocks.BaseSignal;
import com.jas777.signalbox.init.ModBlocks;
import com.jas777.signalbox.tileentity.SignalTileEntity;
import com.jas777.signalbox.util.HasParts;
import com.jas777.signalbox.util.HasVariant;
import com.jas777.signalbox.util.SignalboxTabs;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockHvHauptsignalHeadBottom extends BaseSignal implements HasVariant, HasParts {

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(1 * 0.0625, 0, 4 * 0.0625, 15 * 0.0625, 15 * 0.0625, 12 * 0.0625);

    public static final PropertyInteger SIGNAL_VARIANT = PropertyInteger.create("signal_variant", 0, 4);

    public BlockHvHauptsignalHeadBottom() {
        super("de_hv_hauptsignal_head_bottom", Material.IRON);
        this.setDefaultState(this.getDefaultState().withProperty(ACTIVE, Boolean.TRUE).withProperty(SIGNAL_VARIANT, 0));
        setCreativeTab(SignalboxTabs.DE_CREATIVE_TAB);
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

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    public PropertyInteger getSignalVariant() {
        return SIGNAL_VARIANT;
    }

    @Override
    public boolean canPlaceBlockAt(World p_canPlaceBlockAt_1_, BlockPos p_canPlaceBlockAt_2_) {
        return p_canPlaceBlockAt_1_.isAirBlock(p_canPlaceBlockAt_2_.up());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos.up(), ModBlocks.DE_HV_HAUPTSIGNAL_HEAD_TOP.getDefaultState().withProperty(BlockHvHauptsignalHeadTop.FACING, state.getValue(FACING)).withProperty(BlockHvHauptsignalHeadTop.SIGNAL_VARIANT, state.getValue(SIGNAL_VARIANT)));
    }

    @Override
    public void updateParts(World worldIn, BlockPos pos, IBlockState state) {

        if (!(worldIn.getBlockState(pos.up()).getBlock() instanceof BlockHvHauptsignalHeadTop)) {
            breakBlock(worldIn, pos, state);
        }

        worldIn.setBlockState(pos.up(), worldIn.getBlockState(pos.up()).withProperty(BlockHvHauptsignalHeadTop.SIGNAL_VARIANT, state.getValue(SIGNAL_VARIANT)).withProperty(BlockHvHauptsignalHeadTop.ACTIVE, state.getValue(ACTIVE)));

    }
}
