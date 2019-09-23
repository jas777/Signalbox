package com.jas777.signalbox.proxy;

import com.jas777.signalbox.tileentity.DisplayTileEntity;
import com.jas777.signalbox.tileentity.tesr.DisplayTileEntityRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerRender() {
        ClientRegistry.bindTileEntitySpecialRenderer(DisplayTileEntity.class, new DisplayTileEntityRenderer());
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }
}
