package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.placeables.HotpotPlaceables;

public class HotpotLongPlateItem extends HotpotPlaceableBlockItem {
    public HotpotLongPlateItem() {
        super(HotpotPlaceables.getPlaceableOrElseEmpty("LongPlate"), new Properties().stacksTo(64).tab(HotpotModEntry.HOTPOT_ITEM_GROUP));
    }
}
