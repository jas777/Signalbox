package com.jas777.signalbox.blocks.controller;

import com.jas777.signalbox.blocks.BaseBlock;
import com.jas777.signalbox.gui.GuiControllerDisplay;
import com.jas777.signalbox.gui.GuiControllerMaster;
import com.jas777.signalbox.tileentity.ControllerDisplayTileEntity;
import com.jas777.signalbox.tileentity.ControllerMasterTileEntity;
import com.jas777.signalbox.util.CanBePowered;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockController extends BaseBlock {

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(2 * 0.0625, 0, 2 * 0.0625, 14 * 0.0625, 1, 14 * 0.0625);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    private ControllerType type;

    public BlockController(ControllerType type) {
        super(type.getBlockName(), Material.IRON);
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        this.type = type;
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
    @SideOnly(Side.CLIENT)
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            switch (type) {
                case SIGNAL_CONTROLLER:
                    if (worldIn.getTileEntity(pos) instanceof ControllerMasterTileEntity) {
                        Minecraft.getMinecraft().displayGuiScreen(new GuiControllerMaster((ControllerMasterTileEntity) worldIn.getTileEntity(pos)));
                    }
                    break;
                case DISPLAY_CONTROLLER:
                    if (worldIn.getTileEntity(pos) instanceof ControllerDisplayTileEntity) {
                        Minecraft.getMinecraft().displayGuiScreen(new GuiControllerDisplay((ControllerDisplayTileEntity) worldIn.getTileEntity(pos)));
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        switch (type) {
            case SIGNAL_CONTROLLER:
                return new ControllerMasterTileEntity();
            case DISPLAY_CONTROLLER:
                return new ControllerDisplayTileEntity();
            default:
                return null;
        }
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING, ACTIVE});
    }

    public ControllerType getType() {
        return type;
    }
}
