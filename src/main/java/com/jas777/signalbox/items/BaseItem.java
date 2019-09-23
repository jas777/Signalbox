package com.jas777.signalbox.items;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.init.ModItems;
import com.jas777.signalbox.util.HasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class BaseItem extends Item implements HasModel {

    public BaseItem(String name) {
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.TRANSPORTATION);

        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Signalbox.proxy.registerItemRenderer(this, 0, "inventory");
    }

}
