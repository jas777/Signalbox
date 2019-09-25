package com.jas777.signalbox.blocks;

import com.jas777.signalbox.tileentity.DisplayTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BaseDisplay extends BaseBlock {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    private String displayedText = "5";

    protected double displayX1;
    protected double displayX2;
    protected double displayY1;
    protected double displayY2;

    protected double displayZ;

    protected boolean active = true;

    public BaseDisplay(String name, Material material) {
        super(name, material);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getFront(5 - (meta & 3));

        this.active = (meta & 4) == 4;

        if (facing.getAxis() == EnumFacing.Axis.Y) {
            facing = EnumFacing.NORTH;
        }

        return getDefaultState().withProperty(FACING, facing).withProperty(ACTIVE, active);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int facing = state.getValue(FACING).getIndex();
        int active = state.getValue(ACTIVE) ? 4 : 0;

        return active | 5 - facing;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING, ACTIVE});
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        return true;
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            if (worldIn.isBlockPowered(pos)) {
                worldIn.setBlockState(pos, state.withProperty(ACTIVE, Boolean.TRUE), 2);
                this.active = true;
            } else {
                worldIn.setBlockState(pos, state.withProperty(ACTIVE, Boolean.FALSE), 2);
                this.active = false;
            }
        }
    }

    @Override
    public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
        return super.getWeakChanges(world, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote) {
            if (worldIn.isBlockPowered(pos)) {
                worldIn.setBlockState(pos, state.withProperty(ACTIVE, Boolean.TRUE), 2);
                this.active = true;
            } else {
                worldIn.setBlockState(pos, state.withProperty(ACTIVE, Boolean.FALSE), 2);
                this.active = false;
            }
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new DisplayTileEntity();
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public int getLightValue(IBlockState state) {
        return 7;
    }

    public String getDisplayedText() {
        return displayedText;
    }

    public void setDisplayedText(String displayedText) {
        this.displayedText = displayedText;
    }

    public double getDisplayX1() {
        return displayX1;
    }

    public double getDisplayX2() {
        return displayX2;
    }

    public double getDisplayY1() {
        return displayY1;
    }

    public double getDisplayY2() {
        return displayY2;
    }

    public double getDisplayZ() {
        return displayZ;
    }

    public boolean isActive() {
        return active;
    }
}
