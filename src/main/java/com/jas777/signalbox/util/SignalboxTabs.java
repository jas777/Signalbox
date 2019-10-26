package com.jas777.signalbox.util;

import com.jas777.signalbox.init.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class SignalboxTabs {

    public static CreativeTabs DE_CREATIVE_TAB = new CreativeTabs("tabSignalboxDE") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ModBlocks.DE_SPERRSIGNAL_HEAD);
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }

    }.setBackgroundImageName("item_search.png");

    public static CreativeTabs PL_CREATIVE_TAB = new CreativeTabs("tabSignalboxPL") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ModBlocks.PL_TM_GROUND_HEAD);
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }

    }.setBackgroundImageName("item_search.png");

    public static CreativeTabs FI_CREATIVE_TAB = new CreativeTabs("tabSignalboxFI") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ModBlocks.FI_MAIN_SIGNAL_HEAD);
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }

    }.setBackgroundImageName("item_search.png");

    public static CreativeTabs CH_CREATIVE_TAB = new CreativeTabs("tabSignalboxCH") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ModBlocks.CH_ZWERGSIGNAL_HEAD);
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }

    }.setBackgroundImageName("item_search.png");

    public static CreativeTabs UK_CREATIVE_TAB = new CreativeTabs("tabSignalboxUK") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ModBlocks.UK_SIGNAL_HEAD);
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }

    }.setBackgroundImageName("item_search.png");

    public static CreativeTabs HU_CREATIVE_TAB = new CreativeTabs("tabSignalboxHU") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ModBlocks.HU_MAIN_SIGNAL_HEAD_TOP);
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }

    }.setBackgroundImageName("item_search.png");

    public static CreativeTabs IT_CREATIVE_TAB = new CreativeTabs("tabSignalboxIT") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ModBlocks.IT_DISTANT_SIGNAL_HEAD);
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }

    }.setBackgroundImageName("item_search.png");

//    public static CreativeTabs AT_CREATIVE_TAB = new CreativeTabs("tabSignalboxAT") {
//        @Override
//        public ItemStack getTabIconItem() {
//            return new ItemStack(ModBlocks.D);
//        }
//
//        @Override
//        public boolean hasSearchBar() {
//            return true;
//        }
//
//    }.setBackgroundImageName("item_search.png");

    public static CreativeTabs NL_CREATIVE_TAB = new CreativeTabs("tabSignalboxNL") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ModBlocks.NL_SIGNAL_HEAD);
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }

    }.setBackgroundImageName("item_search.png");

}
