package com.jas777.signalbox.init;

import com.jas777.signalbox.blocks.BlockSignalStand;
import com.jas777.signalbox.blocks.db.BlockShuntSignalGroundOld;
import com.jas777.signalbox.blocks.db.ksmehrfachssignal.BlockKsMehrfachssignalHead;
import com.jas777.signalbox.blocks.db.ksmehrfachssignal.BlockKsMehrfachssignalZs3;
import com.jas777.signalbox.blocks.db.vorsignal.*;
import com.jas777.signalbox.blocks.db.vorsignal.hv.BlockHvVorsignalHead;
import com.jas777.signalbox.blocks.db.vorsignal.kompakt.BlockKompaktvorsignalHead;
import com.jas777.signalbox.blocks.pkp.BlockGroundSignal;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {

    public static final List<Block> BLOCKS = new ArrayList<Block>();

    public static final Block SIGNAL_STAND = new BlockSignalStand();

    // PKP

    public static final Block PKP_GROUND_SIGNAL = new BlockGroundSignal();

    // DB

    public static final Block DB_SHUNT_SIGNAL_GROUND_OLD = new BlockShuntSignalGroundOld();

    public static final Block DB_VORSIGNAL_ZS3V = new BlockVorsignalZs3v();
    public static final Block DB_VORSIGNAL_FRAME = new BlockVorsignalFrame();
    public static final Block DB_VORSIGNAL_SIGN = new BlockVorsignalSign();
    public static final Block DB_VORSIGNAL_POLE = new BlockVorsignalPole();
    public static final Block DB_VORSIGNAL_STAND = new BlockVorsignalStand();

    public static final Block DB_KOMPAKTVORSIGNAL_HEAD = new BlockKompaktvorsignalHead();
    public static final Block DB_HV_VORSIGNAL_HEAD = new BlockHvVorsignalHead();

    public static final Block DB_KS_MEHRFACHSSIGNAL_HEAD = new BlockKsMehrfachssignalHead();
    public static final Block DB_KS_MEHRFACHSSIGNAL_ZS3 = new BlockKsMehrfachssignalZs3();

}
