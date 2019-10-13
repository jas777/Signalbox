package com.jas777.signalbox.init;

import com.jas777.signalbox.blocks.controller.ControllerType;
import com.jas777.signalbox.blocks.de.BlockShuntSignalGroundOld;
import com.jas777.signalbox.blocks.de.ksmehrfachssignal.BlockKsMehrfachssignalHead;
import com.jas777.signalbox.blocks.de.ksmehrfachssignal.BlockKsMehrfachssignalZs3;
import com.jas777.signalbox.blocks.de.vorsignal.*;
import com.jas777.signalbox.blocks.de.vorsignal.hv.BlockHvVorsignalHead;
import com.jas777.signalbox.blocks.de.vorsignal.kompakt.BlockKompaktvorsignalHead;
import com.jas777.signalbox.blocks.controller.BlockController;
import com.jas777.signalbox.blocks.fi.BlockMainSignalHead;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {

    public static final List<Block> BLOCKS = new ArrayList<Block>();

    // GENERAL

    public static final Block CONTROLLER_MASTER = new BlockController(ControllerType.SIGNAL_CONTROLLER);
    public static final Block CONTROLLER_DISPLAY = new BlockController(ControllerType.DISPLAY_CONTROLLER);

    // PL

    // DE

    public static final Block DE_SHUNT_SIGNAL_GROUND_OLD = new BlockShuntSignalGroundOld();

    public static final Block DE_VORSIGNAL_ZS3V = new BlockVorsignalZs3v();
    public static final Block DE_VORSIGNAL_FRAME = new BlockVorsignalFrame();
    public static final Block DE_VORSIGNAL_SIGN = new BlockVorsignalSign();
    public static final Block DE_VORSIGNAL_POLE = new BlockVorsignalPole();
    public static final Block DE_VORSIGNAL_STAND = new BlockVorsignalStand();

    public static final Block DE_KOMPAKTVORSIGNAL_HEAD = new BlockKompaktvorsignalHead();
    public static final Block DE_HV_VORSIGNAL_HEAD = new BlockHvVorsignalHead();

    public static final Block DE_KS_MEHRFACHSSIGNAL_HEAD = new BlockKsMehrfachssignalHead();
    public static final Block DE_KS_MEHRFACHSSIGNAL_ZS3 = new BlockKsMehrfachssignalZs3();

    // FI

    public static final Block FI_MAIN_SIGNAL_HEAD = new BlockMainSignalHead();

}
