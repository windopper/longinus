package UserChip;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import userdata.UserManager;

public class UserChipEvent implements Listener {
	
	private static UserChipEvent UserChipEvent;
	
	private UserChipEvent()	{
		
	}
	
	public static UserChipEvent getinstance() {
		if(UserChipEvent == null) UserChipEvent = new UserChipEvent();
		return UserChipEvent;
	}
	
	
	@EventHandler
	public void UserChipDropEvent(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItemDrop().getItemStack();
		if(item==null) return;
		if(item.getItemMeta()==null) return;
		if(item.getItemMeta().getDisplayName()==null) return;
		if(ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals(p.getName()+"의 메모리 카드")) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void UserChipInventoryEvent(InventoryClickEvent e) {
		
		
		if(e.getClick().isKeyboardClick()) e.setCancelled(true);
		
		Player p = (Player) e.getWhoClicked();
		ItemStack item = e.getCurrentItem();
		if(item==null) return;
		if(item.getItemMeta()==null) return;
		if(item.getItemMeta().getDisplayName()==null) return;
		if(ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals(p.getName()+"의 메모리 카드")) {
			e.setCancelled(true);
		}
	}
	
	
}
