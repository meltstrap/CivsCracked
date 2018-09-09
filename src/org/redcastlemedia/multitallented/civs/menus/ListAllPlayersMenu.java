package org.redcastlemedia.multitallented.civs.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.redcastlemedia.multitallented.civs.LocaleManager;
import org.redcastlemedia.multitallented.civs.civilians.Civilian;
import org.redcastlemedia.multitallented.civs.civilians.CivilianManager;
import org.redcastlemedia.multitallented.civs.util.CVItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ListAllPlayersMenu extends Menu {
    public static String MENU_NAME = "CivsPlayers";
    public ListAllPlayersMenu() {
        super(MENU_NAME);
    }

    @Override
    void handleInteract(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.STONE
                || event.getCurrentItem().getType() == Material.AIR || !event.getCurrentItem().hasItemMeta()) {
            return;
        }
        ItemStack itemStack = event.getInventory().getItem(2);
        Civilian civilian = CivilianManager.getInstance().getCivilian(UUID.fromString(itemStack.getItemMeta().getLore().get(0)));

        if (isBackButton(event.getCurrentItem(), civilian.getLocale())) {
            clickBackButton(event.getWhoClicked());
            return;
        }

        int page = Integer.parseInt(itemStack.getItemMeta().getLore().get(1));
        String id = null;
        List<Player> blackList = new ArrayList<>();
        if (itemStack.getItemMeta().getLore().size() > 2) {
            id = itemStack.getItemMeta().getLore().get(2);
        }
        if (itemStack.getItemMeta().getLore().size() > 3) {
            for (String s : itemStack.getItemMeta().getLore().get(3).split(",")) {
                Player player = Bukkit.getPlayer(s);
                if (player != null) {
                    blackList.add(player);
                }
            }
        }
        String name = itemStack.getItemMeta().getDisplayName();

        if (event.getCurrentItem().getType() == Material.EMERALD) {
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().openInventory(ListAllPlayersMenu.createMenu(civilian, name, blackList, page + 1, id));
            return;
        }
        if (event.getCurrentItem().getType() == Material.REDSTONE) {
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().openInventory(ListAllPlayersMenu.createMenu(civilian, name, blackList, page - 1, id));
            return;
        }

        String playerName = event.getCurrentItem().getItemMeta().getDisplayName();
        if (event.getWhoClicked() instanceof Player) {
            if (name.equals("Player List") || name.equals("Friend List")) {
                if (id == null) {
                    appendHistory(civilian.getUuid(), MENU_NAME);
                } else {
                    appendHistory(civilian.getUuid(), MENU_NAME + "," + id);
                }
                event.getWhoClicked().closeInventory();
                UUID uuid = UUID.fromString(event.getCurrentItem().getItemMeta().getLore().get(0));
                event.getWhoClicked().openInventory(PlayerProfileMenu.createMenu(civilian, uuid));
            } else {
                event.getWhoClicked().closeInventory();
                clearHistory(civilian.getUuid());
                ((Player) event.getWhoClicked()).performCommand("cv " + name + " " + playerName + " " + id);
            }
        }
    }

    public static Inventory createMenu(Civilian civilian, int page) {
        return createMenu(civilian, null, null, page, null);
    }

    public static Inventory createMenu(Civilian civilian, int page, UUID id) {
        Inventory inventory = Bukkit.createInventory(null, 45, MENU_NAME);

        LocaleManager localeManager = LocaleManager.getInstance();

        //0 Prev button
        if (page > 0) {
            CVItem cvItem = CVItem.createCVItemFromString("REDSTONE");
            cvItem.setDisplayName(localeManager.getTranslation(civilian.getLocale(),
                    "prev-button"));
            inventory.setItem(0, cvItem.createItemStack());
        }

        //2 Icon
        CVItem cvItem = CVItem.createCVItemFromString("STONE");
        cvItem.setDisplayName("Friend List");
        List<String> lore = new ArrayList<>();
        lore.add(civilian.getUuid().toString());
        lore.add(page + "");
        if (id != null) {
            lore.add(id.toString());
        }
        cvItem.setLore(lore);
        inventory.setItem(2, cvItem.createItemStack());

        //6 Back button
        inventory.setItem(6, getBackButton(civilian));

        List<OfflinePlayer> players = new ArrayList<>();
        Civilian cCivilian = CivilianManager.getInstance().getCivilian(id);
        for (UUID uuid : cCivilian.getFriends()) {
            players.add(Bukkit.getOfflinePlayer(uuid));
        }
        int startIndex = page * 36;
        //8 Next button
        if (startIndex + 36 < players.size()) {
            CVItem cvItem1 = CVItem.createCVItemFromString("EMERALD");
            cvItem1.setDisplayName(localeManager.getTranslation(civilian.getLocale(),
                    "next-button"));
            inventory.setItem(8, cvItem1.createItemStack());
        }

        int i=9;
        Collections.sort(players, new Comparator<OfflinePlayer>() {
            @Override
            public int compare(OfflinePlayer player1, OfflinePlayer player2) {
                return player1.getName().compareTo(player2.getName());
            }
        });
        for (int k=startIndex; k<players.size() && k<startIndex+36; k++) {
            OfflinePlayer player = players.get(k);
            ItemStack is = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta isMeta = (SkullMeta) is.getItemMeta();
            isMeta.setDisplayName(player.getName());
            ArrayList<String> lore1 = new ArrayList<>();
            lore1.add(player.getUniqueId().toString());
            isMeta.setLore(lore1);
            isMeta.setOwningPlayer(player);
            is.setItemMeta(isMeta);
            inventory.setItem(i, is);
            i++;
        }

        return inventory;
    }

    public static Inventory createMenu(Civilian civilian, String name, List<Player> blackList, int page, String id) {
        Inventory inventory = Bukkit.createInventory(null, 45, MENU_NAME);

        LocaleManager localeManager = LocaleManager.getInstance();

        //0 Prev button
        if (page > 0) {
            CVItem cvItem = CVItem.createCVItemFromString("REDSTONE");
            cvItem.setDisplayName(localeManager.getTranslation(civilian.getLocale(),
                    "prev-button"));
            inventory.setItem(0, cvItem.createItemStack());
        }

        //2 Icon
        CVItem cvItem = CVItem.createCVItemFromString("STONE");
        cvItem.setDisplayName(name == null ? "Player List" : name);
        List<String> lore = new ArrayList<>();
        lore.add(civilian.getUuid().toString());
        lore.add(page + "");
        if (id != null) {
            lore.add(id);
        }
        if (blackList != null) {
            StringBuilder blackListString = new StringBuilder();
            for (Player b : blackList) {
                blackListString.append(b.getName());
                blackListString.append(",");
            }
            blackListString.substring(blackListString.length() - 1);
            lore.add(blackListString.toString());
        }
        cvItem.setLore(lore);
        inventory.setItem(2, cvItem.createItemStack());

        //6 Back button
        inventory.setItem(6, getBackButton(civilian));

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (blackList != null) {
            players.removeAll(blackList);
        }
        int startIndex = page * 36;
        //8 Next button
        if (startIndex + 36 < players.size()) {
            CVItem cvItem1 = CVItem.createCVItemFromString("EMERALD");
            cvItem1.setDisplayName(localeManager.getTranslation(civilian.getLocale(),
                    "next-button"));
            inventory.setItem(8, cvItem1.createItemStack());
        }

        int i=9;
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player player1, Player player2) {
                return player1.getName().compareTo(player2.getName());
            }
        });
        for (int k=startIndex; k<players.size() && k<startIndex+36; k++) {
            Player player = players.get(k);
            ItemStack is = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta isMeta = (SkullMeta) is.getItemMeta();
            isMeta.setDisplayName(player.getName());
            isMeta.setOwningPlayer(player);
            is.setItemMeta(isMeta);
            inventory.setItem(i, is);
            i++;
        }

        return inventory;
    }
}
