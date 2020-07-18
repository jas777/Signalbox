package com.jas777.signalbox.base.signal;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.control.ControlChannel;
import com.jas777.signalbox.init.ModBlocks;
import com.jas777.signalbox.init.ModItems;
import com.jas777.signalbox.tileentity.SignalTileEntity;
import com.jas777.signalbox.util.interfaces.HasModel;
import com.jas777.signalbox.util.interfaces.SignalVariants;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;

public abstract class BaseSignal extends Block implements HasModel, SignalVariants {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    protected HashMap<Integer, SpecialVariant> specialVariants;

    public BaseSignal(String name, Material material) {
        super(material);

        setUnlocalizedName(name);
        setRegistryName(name);

        setHardness(3.5F);
        setResistance(4.0F);

        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(Objects.requireNonNull(this.getRegistryName())));

        this.specialVariants = null;
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
    public void registerModels() {
        Signalbox.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
        System.out.println("Registering [BB]: " + this.getRegistryName());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World p_createTileEntity_1_, IBlockState p_createTileEntity_2_) {
        return new SignalTileEntity();
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState p_hasTileEntity_1_) {
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        SignalTileEntity te = (SignalTileEntity) worldIn.getTileEntity(pos);
        if (te != null && te.getChannel() != null) {
            te.getChannel().remove(te);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public float getAmbientOcclusionLightValue(IBlockState state) {
        return 3F;
    }

    @Override
    public int getLightValue(IBlockState state) {

        int value = 0;

        int variant = state.getValue(getSignalVariant());

        boolean active = state.getValue(ACTIVE);

        if (active) {
            if (!specialVariants.containsKey(variant)) {
                value = 15;
            }
        }

        return value;

    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        if ((worldIn.getBlockState(pos).getBlock() instanceof BaseSignal) && hand.equals(EnumHand.MAIN_HAND)) {

            SignalTileEntity tileEntity = (SignalTileEntity) worldIn.getTileEntity(pos);

            assert tileEntity != null;

            System.out.println(Signalbox.channelDispatcher.getChannel(1));

            ControlChannel channel = Signalbox.channelDispatcher.addChannel(1);

            tileEntity.setFrequency(1);
            System.out.println(tileEntity);
            ;
            System.out.println(channel);
            tileEntity.setChannel(channel);
            System.out.println(channel);
            tileEntity.updateSignal();

            tileEntity = (SignalTileEntity) worldIn.getTileEntity(pos);

            assert tileEntity != null;

            playerIn.sendMessage(new TextComponentString(tileEntity.getChannel().getFrequency() + "." + tileEntity.getFrequency()));
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            if (worldIn.isBlockPowered(pos)) {
                worldIn.setBlockState(pos, state.withProperty(ACTIVE, Boolean.TRUE), 2);
            } else {
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
                blockIn.setLightLevel(15F);
                worldIn.setBlockState(pos, state.withProperty(ACTIVE, Boolean.TRUE), 2);
            } else {
                blockIn.setLightLevel(0F);
                worldIn.setBlockState(pos, state.withProperty(ACTIVE, Boolean.FALSE), 2);
            }
        }
    }

    // public PropertyInteger getSignalVariant() {
    //     throw new Error("Method not implemented!");
    // }

}
