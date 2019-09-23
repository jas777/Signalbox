package com.jas777.signalbox.init;

import com.jas777.signalbox.blocks.BlockSignalStand;
import com.jas777.signalbox.blocks.db.*;
import com.jas777.signalbox.blocks.db.kompaktvorsignal.*;
import com.jas777.signalbox.blocks.db.ksmehrfachssignal.BlockKsMehrfachssignalHead;
import com.jas777.signalbox.blocks.db.ksmehrfachssignal.BlockKsMehrfachssignalZs3;
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

    public static final Block DB_KOMPAKTVORSIGNAL_HEAD = new BlockKompaktvorsignalHead();
    public static final Block DB_KOMPAKTVORSIGNAL_FRAME = new BlockKompaktvorsignalFrame();
    public static final Block DB_KOMPAKTVORSIGNAL_PLATE = new BlockKompaktvorsignalPlate();
    public static final Block DB_KOMPAKTVORSIGNAL_POLE = new BlockKompaktvorsignalPole();
    public static final Block DB_KOMPAKTVORSIGNAL_STAND = new BlockKompaktvorsignalStand();

    public static final Block DB_KS_MEHRFACHSSIGNAL_HEAD = new BlockKsMehrfachssignalHead();
    public static final Block DB_KS_MEHRFACHSSIGNAL_ZS3 = new BlockKsMehrfachssignalZs3();

}
