package com.jas777.signalbox.init;

import com.jas777.signalbox.blocks.controllers.BlockMasterController;
import com.jas777.signalbox.blocks.de.BlockDEPole;
import com.jas777.signalbox.blocks.de.BlockKompaktvorsignalHead;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {

    // List of all blocks from Signalbox
    public static final List<Block> BLOCKS = new ArrayList<Block>();

    // Controllers

    public static final BlockMasterController MASTER_CONTROLLER = new BlockMasterController();

    // DE

    public static final BlockDEPole DE_POLE = new BlockDEPole();

    public static final BlockKompaktvorsignalHead DE_KOMPAKTVORSIGNAL_HEAD = new BlockKompaktvorsignalHead();

}
