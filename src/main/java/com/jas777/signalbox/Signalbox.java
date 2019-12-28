package com.jas777.signalbox;

import com.jas777.signalbox.proxy.CommonProxy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = Signalbox.MODID, name = Signalbox.NAME, version = Signalbox.VERSION)
public class Signalbox {

    @Mod.Instance
    public static Signalbox instance;

    public static SimpleNetworkWrapper network;

    public static final String MODID = "signalbox";
    public static final String NAME = "Signalbox";
    public static final String VERSION = "0.0.2";

    // Indicates if Immersive Railroading is loaded
    public boolean IR_LOADED = false;

    @SidedProxy(clientSide = "com.jas777.signalbox.proxy.ClientProxy", serverSide = "com.jas777.signalbox.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        IR_LOADED = Loader.isModLoaded("immersiverailroading");
    }

}
