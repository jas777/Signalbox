package com.jas777.signalbox;

import com.jas777.signalbox.network.packet.PacketRequestUpdateDisplay;
import com.jas777.signalbox.network.packet.PacketUpdateDisplay;
import com.jas777.signalbox.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

@Mod(modid = Signalbox.MODID, name = Signalbox.NAME, version = Signalbox.VERSION)
public class Signalbox {

    @Mod.Instance
    public static Signalbox instance;

    public static SimpleNetworkWrapper network;

    public static final String MODID = "signalbox";
    public static final String NAME = "Signalbox";
    public static final String VERSION = "0.0.1";

    private static Logger logger;

    @SidedProxy(clientSide = "com.jas777.signalbox.proxy.ClientProxy", serverSide = "com.jas777.signalbox.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        logger = event.getModLog();

        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        network.registerMessage(new PacketUpdateDisplay.Handler(), PacketUpdateDisplay.class, 0, Side.CLIENT);
        network.registerMessage(new PacketRequestUpdateDisplay.Handler(), PacketRequestUpdateDisplay.class, 1, Side.SERVER);

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

    }
}