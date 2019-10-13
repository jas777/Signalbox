package com.jas777.signalbox.blocks;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.gui.GuiSignal;
import com.jas777.signalbox.tileentity.SignalTileEntity;
import com.jas777.signalbox.util.CanBePowered;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
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

public class BaseSignal extends BaseBlock {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BaseSignal(String name, Material material) {
        super(name, material);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getFront(5 - (meta & 3));

        boolean active = (meta & 4) == 4;

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
                blockIn.setLightLevel(15F);
                worldIn.setBlockState(pos, state.withProperty(ACTIVE, Boolean.TRUE), 2);
            } else {
                CanBePowered tileEntity = (CanBePowered) worldIn.getTileEntity(pos);
                tileEntity.setActive(false);
                blockIn.setLightLevel(0F);
                worldIn.setBlockState(pos, state.withProperty(ACTIVE, Boolean.FALSE), 2);
            }
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new SignalTileEntity();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        SignalTileEntity te = (SignalTileEntity) worldIn.getTileEntity(pos);
        if (te != null && (te.getChannel() > 0 || te.getId() > 0)) {
            Signalbox.instance.getChannelDispatcher().getChannels().get(te.getChannel()).getReceivers().remove(te.getId());
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiSignal((SignalTileEntity) worldIn.getTileEntity(pos)));
        }
        return false;
    }

    @Override
    public float getAmbientOcclusionLightValue(IBlockState state) {
        return 3F;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(ACTIVE) ? 15 : 0;
    }

}
