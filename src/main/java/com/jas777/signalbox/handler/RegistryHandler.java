package com.jas777.signalbox.handler;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.init.ModBlocks;
import com.jas777.signalbox.init.ModItems;
import com.jas777.signalbox.tileentity.ControllerTileEntity;
import com.jas777.signalbox.tileentity.DisplayTileEntity;
import com.jas777.signalbox.tileentity.SignalTileEntity;
import com.jas777.signalbox.util.HasModel;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class RegistryHandler {

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        for (Item item : ModItems.ITEMS) {
            if (item instanceof HasModel) {
                ((HasModel) item).registerModels();
            }
        }

        for (Block block : ModBlocks.BLOCKS) {
            if (block instanceof HasModel) {
                ((HasModel) block).registerModels();
                System.out.println("Registering " + block.getUnlocalizedName());
            }
        }
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(ModBlocks.BLOCKS.toArray(new Block[0]));
        GameRegistry.registerTileEntity(SignalTileEntity.class, new ResourceLocation(Signalbox.MODID + ":signal_entity"));
        GameRegistry.registerTileEntity(DisplayTileEntity.class, new ResourceLocation(Signalbox.MODID + ":display_entity"));
        GameRegistry.registerTileEntity(ControllerTileEntity.class, new ResourceLocation(Signalbox.MODID + ":controller_entity"));
        Signalbox.proxy.registerRender();
    }

}
