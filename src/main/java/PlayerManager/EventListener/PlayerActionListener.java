package PlayerManager.EventListener;

import PlayerChip.Maingui;
import PlayerManager.Filter;
import PlayerManager.PlayerCombination;
import PlayerManager.PlayerFunction;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class PlayerActionListener implements Listener {

	@EventHandler
	public void Test(PlayerAnimationEvent e) {
		if(e.getAnimationType() == PlayerAnimationType.ARM_SWING) {
			if(!PlayerCombination.getinstance(e.getPlayer()).getKey1().equals("none")) e.setCancelled(true);
			if(PlayerFunction.getinstance(e.getPlayer()).getMeleeDelay()!=0) e.setCancelled(true);
		}
	}


	@EventHandler
	public void RightClickOrLeftClick(PlayerInteractEvent e) { // 우클릭 좌클릭
		
		Filter.FilterType filter = Filter.Require(e.getPlayer());
		
		if(filter == Filter.FilterType.sameWpClassAndCurrentClass){
			
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				PlayerCombination.getinstance(e.getPlayer()).setKeybind("R");
				return;

			}
			if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {

				e.setCancelled(true);
				PlayerCombination.getinstance(e.getPlayer()).setKeybind("L");

				return;

			}
		}
		else if(filter == Filter.FilterType.hasWpClassButDiffCurrentClass){
			
			
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
		else if(filter == Filter.FilterType.generalItem) {
			
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
		
		Filter.FilterType filter = Filter.Require(e.getPlayer());
		
		if(filter == Filter.FilterType.sameWpClassAndCurrentClass){
			
			PlayerCombination.getinstance(e.getPlayer()).setKeybind("F");
			//Bukkit.broadcastMessage("F");
		
		}
		else if(filter == Filter.FilterType.hasWpClassButDiffCurrentClass){
			e.getPlayer().sendMessage("§c다른 클래스의 무기입니다§c");

		}
		e.setCancelled(true);
	}
	
	@EventHandler
	public void PressLeft(EntityDamageByEntityEvent e) {

		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			Player pl = (Player) e.getDamager();
			
			if(Filter.Require(pl) == Filter.FilterType.sameWpClassAndCurrentClass) {
			
				PlayerCombination.getinstance(pl).setKeybind("L");

			}	
			e.setCancelled(true); 
		}
		else if(e.getDamager() instanceof Player && e.getEntity() instanceof Entity) {
			Player pl = (Player) e.getDamager();
			
			if(Filter.Require(pl) == Filter.FilterType.sameWpClassAndCurrentClass) {
				PlayerCombination.getinstance(pl).setKeybind("L");		
			}
			e.setCancelled(true);
			
		}
		
	}


}
