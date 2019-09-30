package com.jas777.signalbox.blocks;

import com.jas777.signalbox.gui.GuiDisplay;
import com.jas777.signalbox.tileentity.DisplayTileEntity;
import com.jas777.signalbox.util.CanBePowered;
import com.jas777.signalbox.util.HasVariant;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;

public class BaseDisplay extends BaseBlock implements HasVariant {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");
    public static final PropertyInteger SIGNAL_VARIANT = PropertyInteger.create("signal_variant", 1, 16);

    private String displayedText = String.valueOf(Collections.min(SIGNAL_VARIANT.getAllowedValues()) * 10);

    protected int color;

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
                CanBePowered tileEntity = (CanBePowered) worldIn.getTileEntity(pos);
                tileEntity.setActive(true);
                worldIn.setBlockState(pos, state.withProperty(ACTIVE, Boolean.TRUE), 2);
            } else {
                CanBePowered tileEntity = (CanBePowered) worldIn.getTileEntity(pos);
                tileEntity.setActive(false);
                worldIn.setBlockState(pos, state.withProperty(ACTIVE, Boolean.FALSE), 2);
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
                CanBePowered tileEntity = (CanBePowered) worldIn.getTileEntity(pos);
                tileEntity.setActive(true);
                worldIn.setBlockState(pos, state.withProperty(ACTIVE, Boolean.TRUE), 2);
            } else {
                CanBePowered tileEntity = (CanBePowered) worldIn.getTileEntity(pos);
                tileEntity.setActive(false);
                worldIn.setBlockState(pos, state.withProperty(ACTIVE, Boolean.FALSE), 2);
            }
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new DisplayTileEntity();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiDisplay((DisplayTileEntity) worldIn.getTileEntity(pos)));
        }
        return false;
    }

    @Override
    public int getLightValue(IBlockState state) {
        return 7;
    }

    public String getDisplayedText() {
        return displayedText;
    }

    @Override
    public PropertyInteger getSignalVariant() {
        return SIGNAL_VARIANT;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
