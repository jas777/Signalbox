package com.jas777.signalbox.blocks;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.init.ModBlocks;
import com.jas777.signalbox.init.ModItems;
import com.jas777.signalbox.util.HasModel;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nullable;

public class BasePole extends Block implements HasModel {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public static final PropertyBool POWERED = PropertyBool.create("active");

    public BasePole(String name, Material material) {
        super(material);

        setUnlocalizedName(name);
        setRegistryName(name);

        setHardness(3.5F);
        setResistance(5.0F);

        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    @Override
    public void registerModels() {
        Signalbox.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
        System.out.println("Registering [BB]: " + this.getRegistryName());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getFront(5 - (meta & 3));

        boolean powered = (meta & 4) == 4;

        if (facing.getAxis() == EnumFacing.Axis.Y) {
            facing = EnumFacing.NORTH;
        }

        return getDefaultState().withProperty(FACING, facing).withProperty(POWERED, powered);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int facing = state.getValue(FACING).getIndex();
        int active = state.getValue(POWERED) ? 4 : 0;

        return active | 5 - facing;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return state.getValue(POWERED);
    }

    @Override
    public boolean canConnectRedstone(IBlockState p_canConnectRedstone_1_, IBlockAccess p_canConnectRedstone_2_, BlockPos p_canConnectRedstone_3_, @Nullable EnumFacing p_canConnectRedstone_4_) {
        return true;
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockState p_shouldCheckWeakPower_1_, IBlockAccess p_shouldCheckWeakPower_2_, BlockPos p_shouldCheckWeakPower_3_, EnumFacing p_shouldCheckWeakPower_4_) {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public boolean getWeakChanges(IBlockAccess p_getWeakChanges_1_, BlockPos p_getWeakChanges_2_) {
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (worldIn.isBlockPowered(fromPos)) {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.TRUE));
        } else {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.FALSE));
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (worldIn.isBlockPowered(pos)) {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.TRUE));
        } else {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.FALSE));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING, POWERED});
    }

}
