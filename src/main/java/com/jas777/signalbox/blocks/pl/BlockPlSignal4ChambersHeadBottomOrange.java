package com.jas777.signalbox.blocks.pl;

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

public class BlockPlSignal4ChambersHeadBottomOrange extends BaseSignal implements HasVariant, HasParts {

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(1 * 0.0625, 0, 4 * 0.0625, 15 * 0.0625, 15 * 0.0625, 12 * 0.0625);

    public static final PropertyInteger SIGNAL_VARIANT = PropertyInteger.create("signal_variant", 0, 7);

    public BlockPlSignal4ChambersHeadBottomOrange() {
        super("pl_signal_4chambers_head_bottom_orange", Material.IRON);
        //this.setDefaultState(this.getDefaultState().withProperty(ACTIVE, Boolean.TRUE).withProperty(SIGNAL_VARIANT, 0));
        setCreativeTab(SignalboxTabs.PL_CREATIVE_TAB);
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
        return p_canPlaceBlockAt_1_.isAirBlock(p_canPlaceBlockAt_2_.up()) && p_canPlaceBlockAt_1_.isAirBlock(p_canPlaceBlockAt_2_.up().up());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos.up(), ModBlocks.PL_SIGNAL_4_HEAD_MIDDLE_ORANGE.getDefaultState().withProperty(BlockPlSignal4ChambersHeadMiddleOrange.FACING, state.getValue(FACING)).withProperty(BlockPlSignal4ChambersHeadMiddleOrange.SIGNAL_VARIANT, state.getValue(SIGNAL_VARIANT)),2 );
        worldIn.setBlockState(pos.up().up(), ModBlocks.PL_SIGNAL_4_HEAD_TOP.getDefaultState().withProperty(BlockPlSignal4ChambersHeadTop.FACING, state.getValue(FACING)).withProperty(BlockPlSignal4ChambersHeadTop.SIGNAL_VARIANT, state.getValue(SIGNAL_VARIANT)), 2);
    }

    @Override
    public void updateParts(World worldIn, BlockPos pos, IBlockState state) {

        if (!(worldIn.getBlockState(pos.up()).getBlock() instanceof BlockPlSignal4ChambersHeadMiddleOrange)) {
            breakBlock(worldIn, pos, state);
        }
        if (!(worldIn.getBlockState(pos.up().up()).getBlock() instanceof BlockPlSignal4ChambersHeadTop)) {
            breakBlock(worldIn, pos, state);
        }

        worldIn.setBlockState(pos.up(), worldIn.getBlockState(pos.up()).withProperty(BlockPlSignal4ChambersHeadMiddleOrange.SIGNAL_VARIANT, state.getValue(SIGNAL_VARIANT)).withProperty(BlockPlSignal4ChambersHeadMiddleOrange.ACTIVE, state.getValue(ACTIVE)), 2);
        worldIn.setBlockState(pos.up().up(), worldIn.getBlockState(pos.up().up()).withProperty(BlockPlSignal4ChambersHeadTop.SIGNAL_VARIANT, state.getValue(SIGNAL_VARIANT)).withProperty(BlockPlSignal4ChambersHeadTop.ACTIVE, state.getValue(ACTIVE)), 2);

    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {

        if (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockPlSignal4ChambersHeadMiddleOrange) {
            worldIn.destroyBlock(pos.up(), true);
        }

        if (worldIn.getBlockState(pos.up().up()).getBlock() instanceof BlockPlSignal4ChambersHeadTop) {
            worldIn.destroyBlock(pos.up().up(), true);
        }

        super.breakBlock(worldIn, pos, state);

    }
}
