package Items;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemManager implements Listener {

    private static ItemManager itemManager;

    public ItemManager() {

    }

    public static ItemManager getinstance() {
        if(itemManager==null) itemManager = new ItemManager();
        return itemManager;
    }

    @EventHandler
    public void DropItemListener(PlayerDropItemEvent event) {

        ItemStack itemStack = event.getItemDrop().getItemStack();

        if(itemStack.getItemMeta() == null) return;
        if(itemStack.getItemMeta().getLore() == null) return;
        List<String> lores = itemStack.getItemMeta().getLore();


        if(lores.contains("§c퀘스트 아이템")) event.setCancelled(true);
    }

    public void removeItemFromPlayer(String itemName, Player player) {

        if(!checkItemFromPlayer(itemName, player)) return;

        Inventory inventory = player.getInventory();
        ItemStack[] itemStacks = inventory.getStorageContents();
        for(int i=0; i<itemStacks.length; i++) {
            if(itemStacks[i] == null) continue;
            if(itemStacks[i].getItemMeta() == null) continue;
            if(ChatColor.stripColor(itemStacks[i].getItemMeta().getDisplayName()).equals(itemName)) {
                inventory.remove(itemStacks[i]);
                return;
            }
        }
    }

    public boolean checkItemFromPlayer(String itemName, Player player) {

        Inventory inventory = player.getInventory();
        ItemStack[] itemStacks = inventory.getStorageContents();
        for(int i=0; i<itemStacks.length; i++) {
            if(itemStacks[i] == null) continue;
            if(itemStacks[i].getItemMeta() == null) continue;
            if(ChatColor.stripColor(itemStacks[i].getItemMeta().getDisplayName()).equals(itemName)) {
                return true;
            }
        }
        return false;
    }
}
