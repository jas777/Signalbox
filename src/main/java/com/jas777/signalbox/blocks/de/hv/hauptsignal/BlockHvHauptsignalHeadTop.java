package com.jas777.signalbox.blocks.de.hv.hauptsignal;

import com.jas777.signalbox.blocks.BaseSignalPart;
import com.jas777.signalbox.tileentity.SignalTileEntity;
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

public class BlockHvHauptsignalHeadTop extends BaseSignalPart implements HasVariant {

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(1 * 0.0625, 0, 4 * 0.0625, 15 * 0.0625, 15 * 0.0625, 12 * 0.0625);

    public static final PropertyInteger SIGNAL_VARIANT = PropertyInteger.create("signal_variant", 0, 4);

    public BlockHvHauptsignalHeadTop() {
        super("de_hv_hauptsignal_head_top", Material.IRON);
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
        if (!(worldIn.getBlockState(pos.down()).getBlock() instanceof BlockHvHauptsignalHeadBottom)) {
            breakBlock((World) worldIn, pos, state);
        } else {
            SignalTileEntity tileEntity = (SignalTileEntity) worldIn.getTileEntity(pos.down());
            state = state.withProperty(SIGNAL_VARIANT, tileEntity.getSignalVariant());
        }
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
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!(worldIn.getBlockState(pos.down()).getBlock() instanceof BlockHvHauptsignalHeadBottom)) {
            breakBlock(worldIn, pos, state);
        }
    }

}
