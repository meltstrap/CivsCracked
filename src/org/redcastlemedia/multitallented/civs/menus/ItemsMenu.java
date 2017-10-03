package org.redcastlemedia.multitallented.civs.menus;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.redcastlemedia.multitallented.civs.civilians.Civilian;
import org.redcastlemedia.multitallented.civs.util.CVItem;

public class ItemsMenu extends Menu {
    private static final String MENU_NAME = "CivsItems";
    public ItemsMenu() {
        super(MENU_NAME);
    }

    @Override
    void handleInteract(InventoryClickEvent event) {
        //Do nothing
    }

    public static Inventory createMenu(Civilian civilian) {
        Inventory inventory = Bukkit.createInventory(null, getInventorySize(civilian.getItems().size()), MENU_NAME);

        int i=0;
        for (CVItem cvItem : civilian.getItems()) {
            inventory.setItem(i, cvItem.createItemStack());
            i++;
        }

        return inventory;
    }
}
