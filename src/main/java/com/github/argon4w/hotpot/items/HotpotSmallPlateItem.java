package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.placeables.HotpotPlaceables;

public class HotpotSmallPlateItem extends HotpotPlaceableBlockItem {
    public HotpotSmallPlateItem() {
        super(HotpotPlaceables.getPlaceableOrElseEmpty("SmallPlate"), new Properties().stacksTo(64).tab(HotpotModEntry.HOTPOT_ITEM_GROUP));
    }
}
