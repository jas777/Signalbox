package com.jas777.signalbox.blocks.de.vorsignal;

import com.jas777.signalbox.blocks.BaseDisplay;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class BlockVorsignalZs3v extends BaseDisplay {

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(1 * 0.0625, 0, 2 * 0.0625, 13 * 0.0625, 16 * 0.0625, 13 * 0.0625);

    public static final PropertyBool CONNECTED = PropertyBool.create("connected");

    public BlockVorsignalZs3v() {
        super("de_vorsignal_zs3v", Material.IRON);
        this.setDefaultState(this.getDefaultState().withProperty(ACTIVE, Boolean.FALSE));

        this.displayX1 = 6 * 0.0625;
        this.displayX2 = 11 * 0.0625;

        this.displayY1 = 7 * 0.0625;
        this.displayY2 = 14 * 0.0625;

        this.displayZ = 7 * 0.0625;

    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING, ACTIVE, CONNECTED});
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
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(CONNECTED, canFrameConnectTo(worldIn, pos, state.getValue(FACING)));
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        IBlockState state = world.getBlockState(pos.offset(facing));
        return world.getBlockState(pos.offset(facing)).getBlock() instanceof BlockVorsignalFrame && facing.getOpposite() == state.getValue(FACING);

    }

    private boolean canFrameConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        BlockPos other = pos.offset(facing);
        Block block = world.getBlockState(other).getBlock();
        return block.canBeConnectedTo(world, other, facing.getOpposite());
    }

}
