package UserStorage;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class Event implements Listener {
	
	private final int cancelslot[] = {7, 8, 16, 17, 25, 26, 34, 35, 43, 44, 52, 53};
	
	
	@EventHandler
	public void BankClickEvent(PlayerInteractEvent e) {
		Player player = (Player) e.getPlayer();
		if(e.getClickedBlock() == null) return;
		
		if(e.getClickedBlock().getType() == Material.ENDER_CHEST && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			e.setCancelled(true);
			
			BankGui.getinstance().Open(player, "1");
			
		}
	}
	
	@EventHandler
	public void BankItemMoveCancelEvent(InventoryClickEvent e) {
		
		Player player = (Player) e.getWhoClicked();

		String name[] = e.getView().getTitle().split("#");
		
		if(name.length < 2) return;
		
		int rawslot = e.getRawSlot();
		
		if(name[0].equals("가상창고 ")) {
			
			if(e.getAction()==InventoryAction.SWAP_WITH_CURSOR) {
				e.setCancelled(true);
			}
			
			if(rawslot == 8) {
				
				DetermineJumptoNextPage(player, Integer.parseInt(name[1]), e.getCurrentItem(), e.getClickedInventory());
				e.setCancelled(true);
				return;
			}
			if(rawslot == 17) {
				
				DetermineJumptoPreviousPage(player, Integer.parseInt(name[1]), e.getCurrentItem(), e.getClickedInventory());
				e.setCancelled(true);
				return;
			}
			
			for(int i = 0; i<cancelslot.length; i++) {
				if(rawslot == cancelslot[i]) {
					e.setCancelled(true);
				}
			}
			
		}
		

	}
	
	@EventHandler
	public void BankItemDragCancelEvent(InventoryDragEvent e) {
		
		Player player = (Player) e.getView().getPlayer();
		
		String name[] = e.getView().getTitle().split("#");
		
		if(name.length < 2) return;
		Set<Integer> rawslots = e.getRawSlots();
		
		for(int rawslot : rawslots) {
			for(int i = 0; i<cancelslot.length; i++) {
				if(name[0].equals("가상창고 ") && rawslot == cancelslot[i]) {
					e.setCancelled(true);
				}
			}
		}

	}
	
	@EventHandler
	public void BankItemCloseEvent(InventoryCloseEvent e) {
		
		Player player = (Player) e.getPlayer();
		String name[] = e.getView().getTitle().split("#");
		
		if(name.length < 2) return;
		
		if(name[0].equals("가상창고 ")) {
			UserStorageManager.getinstance().Save(player, e.getInventory(), name[1]);
		}
	}
	
	public boolean ChecItemHasAMetaData(ItemStack item) {
		
		if(item == null) return false;
		if(item.getItemMeta() == null) return false;
		if(item.getItemMeta().getDisplayName() == null) return false;
		return true;
	}
	
	
	public void DetermineJumptoNextPage(Player player, int page, ItemStack item, Inventory inv) {
		
		if(!ChecItemHasAMetaData(item)) return;
		
		
		String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
		
		boolean value = UserStorageManager.getinstance().CheckBankPage(player, Integer.toString(page+1));
		
		if(ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("다음페이지")) {
				
			if(value == true) {
				UserStorageManager.getinstance().Save(player, inv, Integer.toString(page));
				BankGui.getinstance().Open(player, Integer.toString(page+1));
			}
			else {
				player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
			}
			
		}
//		else { // 다음페이지 아닐때
//			
//			if(UserDataManager.getinstance().getGold(player) > 500 * page) {
//				
//				BankSave.getinstance().Save(player, inv, Integer.toString(page));
//				UserDataManager.getinstance().addStorage(player);
//				BankGui.getinstance().Open(player, Integer.toString(page+1));
//				UserDataManager.getinstance().setGold(player, UserDataManager.getinstance().getGold(player)-(500*page));
//
//			}
//			else {
//				
//				player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
//				BankSave.getinstance().Save(player, inv, Integer.toString(page));
//				BankGui.getinstance().Open(player, Integer.toString(page));
//				
//			}
//			
//
//				
//			
//		}
			
	}
	
	public void DetermineJumptoPreviousPage(Player player, int page, ItemStack item, Inventory inv) {
		
		if(!ChecItemHasAMetaData(item)) return;
		
		String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
		
		boolean value = UserStorageManager.getinstance().CheckBankPage(player, Integer.toString(page));
		
		
		if(ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("이전페이지")) {
			
			UserStorageManager.getinstance().Save(player, inv, Integer.toString(page));
			BankGui.getinstance().Open(player, Integer.toString(page-1));
			
		}
		
	}

	

}
