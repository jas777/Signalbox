package com.jas777.signalbox.init;

import com.jas777.signalbox.blocks.ch.*;
import com.jas777.signalbox.blocks.controller.BlockController;
import com.jas777.signalbox.blocks.controller.ControllerType;
import com.jas777.signalbox.blocks.de.BlockDeSperrsignalHead;
import com.jas777.signalbox.blocks.de.hv.hauptsignal.*;
import com.jas777.signalbox.blocks.de.ksmehrfachssignal.BlockKsMehrfachssignalHead;
import com.jas777.signalbox.blocks.de.ksmehrfachssignal.BlockKsMehrfachssignalZs3;
import com.jas777.signalbox.blocks.de.vorsignal.*;
import com.jas777.signalbox.blocks.de.vorsignal.hv.BlockHvVorsignalHead;
import com.jas777.signalbox.blocks.de.vorsignal.kompakt.BlockKompaktvorsignalHead;
import com.jas777.signalbox.blocks.fi.BlockMainSignalHead;
import com.jas777.signalbox.blocks.hu.*;
import com.jas777.signalbox.blocks.it.BlockItDistantHead;
import com.jas777.signalbox.blocks.it.BlockItPole;
import com.jas777.signalbox.blocks.it.BlockItStand;
import com.jas777.signalbox.blocks.nl.BlockNlPole;
import com.jas777.signalbox.blocks.nl.BlockNlSignalDisplay;
import com.jas777.signalbox.blocks.nl.BlockNlSignalHead;
import com.jas777.signalbox.blocks.nl.BlockNlStand;
import com.jas777.signalbox.blocks.pl.*;
import com.jas777.signalbox.blocks.uk.BlockUkPole;
import com.jas777.signalbox.blocks.uk.BlockUkSignalHead;
import com.jas777.signalbox.blocks.uk.BlockUkStand;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {

    public static final List<Block> BLOCKS = new ArrayList<Block>();

    // GENERAL

    public static final Block CONTROLLER_MASTER = new BlockController(ControllerType.SIGNAL_CONTROLLER); //
    public static final Block CONTROLLER_DISPLAY = new BlockController(ControllerType.DISPLAY_CONTROLLER); //

    // PL

    public static final Block PL_POLE_AUTOMATIC = new BlockPlPoleAutomatic(); //
    public static final Block PL_STAND_AUTOMATIC = new BlockPlStandAutomatic(); //

    public static final Block PL_POLE_SHIELD = new BlockPlPoleShield(); //
    public static final Block PL_STAND_SHIELD = new BlockPlStandShield(); //

    public static final Block PL_POLE = new BlockPlPole(); //
    public static final Block PL_STAND = new BlockPlStand(); //

    public static final Block PL_SIGNAL_5_HEAD_TOP = new BlockPlSignal5ChambersHeadTop(); //
    public static final Block PL_SIGNAL_5_HEAD_MIDDLE = new BlockPlSignal5ChambersHeadMiddle(); //
    public static final Block PL_SIGNAL_5_HEAD_BOTTOM = new BlockPlSignal5ChambersHeadBottom(); //

    public static final Block PL_SIGNAL_4_HEAD_TOP = new BlockPlSignal4ChambersHeadTop();

    public static final Block PL_SIGNAL_4_HEAD_MIDDLE_ORANGE = new BlockPlSignal4ChambersHeadMiddleOrange();
    public static final Block PL_SIGNAL_4_HEAD_BOTTOM_ORANGE = new BlockPlSignal4ChambersHeadBottomOrange();

    public static final Block PL_SIGNAL_4_HEAD_MIDDLE_WHITE = new BlockPlSignal4ChambersHeadMiddleWhite();
    public static final Block PL_SIGNAL_4_HEAD_BOTTOM_WHITE = new BlockPlSignal4ChambersHeadBottomWhite();

    public static final Block PL_SIGNAL_3_HEAD_TOP_ORANGE = new BlockPlSignal3ChambersHeadTopOrange();
    public static final Block PL_SIGNAL_3_HEAD_BOTTOM_ORANGE = new BlockPlSignal3ChambersHeadBottomOrange();

    public static final Block PL_SIGNAL_3_HEAD_TOP_WHITE = new BlockPlSignal3ChambersHeadTopWhite();
    public static final Block PL_SIGNAL_3_HEAD_BOTTOM_WHITE = new BlockPlSignal3ChambersHeadBottomWhite();

    public static final Block PL_SIGNAL_2_HEAD_TOP = new BlockPlSignal2ChambersHeadTop();
    public static final Block PL_SIGNAL_2_HEAD_BOTTOM = new BlockPlSignal2ChambersHeadBottom();

    public static final Block PL_SIGNAL_1_HEAD = new BlockPlSignal1ChambersHead();

    public static final Block PL_SIGNAL_SBL_3_HEAD_TOP = new BlockPlSignal3ChambersSBLHeadTop();
    public static final Block PL_SIGNAL_SBL_3_HEAD_BOTTOM = new BlockPlSignal3ChambersSBLHeadBottom();

    public static final Block PL_SIGNAL_REPEATER_3_HEAD_TOP = new BlockPlSignal3ChambersRepeaterHeadTop();
    public static final Block PL_SIGNAL_REPEATER_3_HEAD_BOTTOM = new BlockPlSignal3ChambersRepeaterHeadBottom();

    public static final Block PL_SIGNAL_2_DISTANT_HEAD_TOP = new BlockPlSignal2ChambersDistantHeadTop();
    public static final Block PL_SIGNAL_2_DISTANT_HEAD_BOTTOM = new BlockPlSignal2ChambersDistantHeadBottom();

    public static final Block PL_SIGNAL_2_SHUNTING_HEAD_TOP = new BlockPlSignal2ChambersShuntingHeadTop();
    public static final Block PL_SIGNAL_2_SHUNTING_HEAD_BOTTOM = new BlockPlSignal2ChambersShuntingHeadBottom();

    // SBL
//    public static final Block PL_2STATE_HEAD = new BlockPl2StateHead();
//    public static final Block PL_3STATE_HEAD = new BlockPl3StateHead();
//    public static final Block PL_4STATE_HEAD = new BlockPl4StateHead();
//    public static final Block PL_REPEATER_HEAD = new BlockPlRepeaterHead();
//    public static final Block PL_DISTANT_HEAD = new BlockPlDistantHead();
    //

    public static final Block PL_TM_GROUND_HEAD = new BlockTmGroundHead(); //

    // DE

    public static final Block DE_SPERRSIGNAL_HEAD = new BlockDeSperrsignalHead(); //

    public static final Block DE_VORSIGNAL_ZS3V = new BlockVorsignalZs3v(); //
    public static final Block DE_VORSIGNAL_FRAME = new BlockVorsignalFrame(); //
    public static final Block DE_VORSIGNAL_SIGN = new BlockVorsignalSign(); //
    public static final Block DE_VORSIGNAL_POLE = new BlockVorsignalPole(); //
    public static final Block DE_VORSIGNAL_STAND = new BlockVorsignalStand(); //

    public static final Block DE_KOMPAKTVORSIGNAL_HEAD = new BlockKompaktvorsignalHead(); //

    public static final Block DE_HV_VORSIGNAL_HEAD = new BlockHvVorsignalHead(); //
    public static final Block DE_HV_HAUPTSIGNAL_HEAD_TOP = new BlockHvHauptsignalHeadTop(); //
    public static final Block DE_HV_HAUPTSIGNAL_HEAD_BOTTOM = new BlockHvHauptsignalHeadBottom(); //
    public static final Block DE_HV_HAUPTSIGNAL_SIGN_TOP = new BlockHvHauptsignalSignTop(); //
    public static final Block DE_HV_HAUPTSIGNAL_SIGN_BOTTOM_RED = new BlockHvHauptsignalSignRed(); //
    public static final Block DE_HV_HAUPTSIGNAL_SIGN_BOTTOM_YELLOW = new BlockHvHauptsignalSignYellow(); //
    public static final Block DE_HV_HAUPTSIGNAL_STAND_YELLOW = new BlockHvHauptsignalStandYellow(); //
    public static final Block DE_HV_HAUPTSIGNAL_STAND_WHITE = new BlockHvHauptsignalStandWhite(); //

    public static final Block DE_KS_MEHRFACHSSIGNAL_HEAD = new BlockKsMehrfachssignalHead(); //
    public static final Block DE_KS_MEHRFACHSSIGNAL_ZS3 = new BlockKsMehrfachssignalZs3(); //

    // FI

    public static final Block FI_MAIN_SIGNAL_HEAD = new BlockMainSignalHead(); //

    // CH

    public static final Block CH_POLE = new BlockChPole(); //
    public static final Block CH_STAND = new BlockChStand(); //

    public static final Block CH_LV_SIGNAL_HEAD = new BlockChLvSignalHead(); //

    public static final Block CH_OEPNV_HEAD = new BlockChOepnvHead(); //
    public static final Block CH_OEPNV_POLE = new BlockChOepnvPole(); //

    public static final Block CH_ZWERGSIGNAL_HEAD = new BlockChZwergsignalHead(); //

    // UK

    public static final Block UK_POLE = new BlockUkPole(); //
    public static final Block UK_STAND = new BlockUkStand(); //

    public static final Block UK_SIGNAL_HEAD = new BlockUkSignalHead(); //

    // HU

    public static final Block HU_POLE = new BlockHuPole(); //
    public static final Block HU_STAND = new BlockHuStand(); //

    public static final Block HU_POLE_REPEATER = new BlockHuPoleRepeater(); //
    public static final Block HU_STAND_REPEATER = new BlockHuStandRepeater(); //

    public static final Block HU_MAIN_SIGNAL_HEAD_TOP = new BlockHuMainSignalHeadTop(); //
    public static final Block HU_MAIN_SIGNAL_HEAD_BOTTOM = new BlockHuMainSignalHeadBottom(); //

    public static final Block HU_REPEATER = new BlockHuRepeaterHead(); //

    // AT

//    public static final Block AT_POLE = new BlockAtPole();
//    public static final Block AT_STAND = new BlockAtStand();
//
//    public static final Block AT_VORSIGNAL_HEAD = new BlockAtVorsignalHead();

    // IT

    public static final Block IT_POLE = new BlockItPole(); //
    public static final Block IT_STAND = new BlockItStand(); //

    public static final Block IT_DISTANT_SIGNAL_HEAD = new BlockItDistantHead(); //

    // NL

    public static final Block NL_POLE = new BlockNlPole(); //
    public static final Block NL_STAND = new BlockNlStand(); //

    public static final Block NL_SIGNAL_DISPLAY = new BlockNlSignalDisplay();

    public static final Block NL_SIGNAL_HEAD = new BlockNlSignalHead();

}
