package com.jas777.signalbox.blocks.db.vorsignal;

import com.jas777.signalbox.blocks.BaseBlock;
import com.jas777.signalbox.blocks.BaseDisplay;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class BlockVorsignalFrame extends BaseBlock {

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(2 * 0.0625, 0, 2 * 0.0625, 14 * 0.0625, 1, 14 * 0.0625);

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool CONNECTED = PropertyBool.create("connected");

    public BlockVorsignalFrame() {
        super("db_vorsignal_frame", Material.IRON);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.SOLID;
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
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getFront(5 - (meta & 3));

        boolean active = (meta & 4) == 4;

        if (facing.getAxis() == EnumFacing.Axis.Y) {
            facing = EnumFacing.NORTH;
        }

        return getDefaultState().withProperty(FACING, facing).withProperty(CONNECTED, active);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int facing = state.getValue(FACING).getIndex();
        int active = state.getValue(CONNECTED) ? 4 : 0;

        return active | 5 - facing;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING, CONNECTED});
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(CONNECTED, canFrameConnectTo(worldIn, pos, state.getValue(FACING)));
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        IBlockState state = world.getBlockState(pos);
        if (world.getBlockState(pos.offset(facing)).getBlock() instanceof BaseDisplay && facing == state.getValue(FACING)) {
            return true;
        }
        return false;

    }

    private boolean canFrameConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        BlockPos other = pos.offset(facing);
        Block block = world.getBlockState(other).getBlock();
        return block.canBeConnectedTo(world, other, facing.getOpposite());
    }
}
