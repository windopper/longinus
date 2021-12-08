package DynamicData;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import UserChip.Maingui;
import UserData.Filter;

public class PlayerActionEvent implements Listener {
	
	@EventHandler
	public void RightClickOrLeftClick(PlayerInteractEvent e) { // 우클릭 좌클릭
		
		int filter = Filter.Require(e.getPlayer());
		
		if(filter==1){
			
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				PlayerCombination.getinstance(e.getPlayer()).setKeybind("R");
				return;
				
				
				
			}
			if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
				
				PlayerCombination.getinstance(e.getPlayer()).setKeybind("L");	
				return;

			}

		
		}
		else if(filter==2){
			
			
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				
				e.getPlayer().sendMessage("§c다른 클래스의 무기입니다§c");
				e.setCancelled(true);
				return;
				
			}
			if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
				
				
				e.getPlayer().sendMessage("§c다른 클래스의 무기입니다§c");
				e.setCancelled(true);
				return;
			}

		}
		else if(filter == 0) {
			
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				
				if(e.getItem() == null) return;
				if(e.getItem().getItemMeta() == null) return;
				if(ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName()).equals(e.getPlayer().getName()+"의 메모리 카드")) {

					Maingui.getinstance().chipitemguiopen(e.getPlayer());
					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
					return;
				}
				

				
			}
		}
	}
	
	@EventHandler
	public void PressF(PlayerSwapHandItemsEvent e) { // F키
		
		int filter = Filter.Require(e.getPlayer());
		
		if(filter==1){
			
			PlayerCombination.getinstance(e.getPlayer()).setKeybind("F");
			//Bukkit.broadcastMessage("F");
		
		}
		else if(filter==2){		
			e.getPlayer().sendMessage("§c다른 클래스의 무기입니다§c");

		}
		e.setCancelled(true);
	}
	
	@EventHandler
	public void PressLeft(EntityDamageByEntityEvent e) {
		
		
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			Player pl = (Player) e.getDamager();
			
			if(Filter.Require(pl) == 1) {
			
				PlayerCombination.getinstance(pl).setKeybind("L");

			}	
			e.setCancelled(true); 
		}
		else if(e.getDamager() instanceof Player && e.getEntity() instanceof Entity) {
			Player pl = (Player) e.getDamager();
			
			if(Filter.Require(pl) == 1) {
				PlayerCombination.getinstance(pl).setKeybind("L");		
			}
			e.setCancelled(true);
			
		}
		
	}
	

}
