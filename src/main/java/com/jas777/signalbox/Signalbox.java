package com.jas777.signalbox;

import com.jas777.signalbox.channel.ChannelDispatcher;
import com.jas777.signalbox.channel.ControllerDispatcher;
import com.jas777.signalbox.network.packet.*;
import com.jas777.signalbox.proxy.CommonProxy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
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

    private ChannelDispatcher channelDispatcher;
    private ControllerDispatcher controllerDispatcher;

    public static final String MODID = "signalbox";
    public static final String NAME = "Signalbox";
    public static final String VERSION = "0.0.1";

    public static Logger logger;

    public boolean IR_LOADED = false;

    @SidedProxy(clientSide = "com.jas777.signalbox.proxy.ClientProxy", serverSide = "com.jas777.signalbox.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        logger = event.getModLog();

        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

        network.registerMessage(new PacketUpdateDisplay.Handler(), PacketUpdateDisplay.class, 0, Side.CLIENT);
        network.registerMessage(new PacketRequestUpdateDisplay.Handler(), PacketRequestUpdateDisplay.class, 1, Side.SERVER);

        network.registerMessage(new PacketUpdateControllerMaster.Handler(), PacketUpdateControllerMaster.class, 2, Side.CLIENT);
        network.registerMessage(new PacketRequestUpdateControllerMaster.Handler(), PacketRequestUpdateControllerMaster.class, 3, Side.SERVER);

        network.registerMessage(new PacketUpdateControllerDisplay.Handler(), PacketUpdateControllerDisplay.class, 4, Side.CLIENT);
        network.registerMessage(new PacketRequestUpdateControllerDisplay.Handler(), PacketRequestUpdateControllerDisplay.class, 5, Side.SERVER);

        network.registerMessage(new PacketUpdateSignal.Handler(), PacketUpdateSignal.class, 6, Side.CLIENT);
        network.registerMessage(new PacketRequestUpdateSignal.Handler(), PacketRequestUpdateSignal.class, 7 , Side.SERVER);

        network.registerMessage(new PacketSetSignalOccupationOriginOnServer.Handler(), PacketSetSignalOccupationOriginOnServer.class, 8, Side.SERVER);

        channelDispatcher = new ChannelDispatcher();
        controllerDispatcher = new ControllerDispatcher();

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        IR_LOADED = Loader.isModLoaded("immersiverailroading");
    }

    public ChannelDispatcher getChannelDispatcher() {
        return channelDispatcher;
    }

    public ControllerDispatcher getControllerDispatcher() {
        return controllerDispatcher;
    }
}
