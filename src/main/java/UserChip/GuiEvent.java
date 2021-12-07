package UserChip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import CustomEvents.PlayerClassChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ClassAbility.entitycheck;
import returns.ReturnMech;
import spellinteracttest.Main;
import userdata.UserFileManager;
import userdata.UserManager;
import userdata.UserStatManager;

public class GuiEvent implements Listener {
	
	private final List<Player> GoldAllowReadingChatPlayerName = new ArrayList<>();
	private final HashMap<Player, Player> GoldAllowReadingChatAmount = new HashMap<>();
	
	@EventHandler
	public void InventoryClick(InventoryClickEvent e) {

		String invname = e.getView().getTitle();
		ItemStack item = e.getCurrentItem();
		int rawslot = e.getRawSlot();
		if(invname.equals("메모리카드")) {
			
			if(rawslot == 4) {
				if(e.getClick().isLeftClick()) GoldSendPlayerAllowChat(e);
			}
			if(rawslot == 11) AlarmClickEvent(e);
			if(rawslot == 12) QuestsOpenEvent(e);
			if(rawslot == 13) ClassSelectEvent(e);
			if(rawslot == 14) StatClickEvent(e);
			
			if(rawslot == 31) ReturnEvent(e);
	
			e.setCancelled(true);
		}
		
		if(invname.equals("Alarm")) {

			if(rawslot ==53) AlarmDeleteEvent(e);
			if(rawslot ==45) BacktoMain(e);
			
			
			
			e.setCancelled(true);
		}
		
		if(invname.equals("Stat")) {
			
			StatUpgradeEvent(e);
			
			e.setCancelled(true);
		}
		if(invname.equals("클래스 선택")) {
			
			Player player = (Player) e.getWhoClicked();
			
			ClassChangeEvent(e);
			
			e.setCancelled(true);
		}
		if(invname.equals("클래스 추가")) {
			
			Player player = (Player) e.getWhoClicked();
			
			ClassAddEvent(e);
			
			e.setCancelled(true);
		}
		if(invname.equals("정말로 클래스를 삭제하시겠습니까?")) {
			
			Player player = (Player) e.getWhoClicked();
			
			ClassDeleteSureEvent(e);
			
			e.setCancelled(true);
		}
		
		if(invname.equals("퀘스트")) {
			
			
			if(rawslot == 45) BacktoMain(e);
			
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void GettingChatFromPlayer(AsyncPlayerChatEvent e) {

		Player player = e.getPlayer();
		if(GoldAllowReadingChatPlayerName.contains(player)) {
			
			GoldAllowReadingChatPlayerName.remove(player);
			String content = e.getMessage();
			if(content.equals("취소")) {
				e.setCancelled(true);
				return;
			}
			
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(p.getName().equals(content)) {
					GoldSetPlayerAmount(player, p);
					e.setCancelled(true);
					return;
				}
			}
			
			e.setCancelled(true);
			player.sendMessage(content+"§c는 현재 존재하지 않는 플레이어입니다");
			
		}
		if(GoldAllowReadingChatAmount.containsKey(player)) {
			
			
			try {	
				int currentgold = UserFileManager.getinstance().getGold(player);
				int content = Integer.parseInt(e.getMessage());
				
				if(currentgold < content) {
					player.sendMessage("§c알테라가 충분하지 않습니다");
					GoldAllowReadingChatAmount.remove(player);
					return;
				}
				else if(content <= 0) {
					player.sendMessage("§c잘못된 형식입니다");
				}
				else {
					
					GoldSendPlayertoPlayer(player, GoldAllowReadingChatAmount.get(player), content);
					GoldAllowReadingChatAmount.remove(player);
					
				}
			}
			catch(NumberFormatException i) {
				player.sendMessage("§c잘못된 형식입니다");
			}
			
			e.setCancelled(true);
			
	
		}	
	}
	
	public void AlarmClickEvent(InventoryClickEvent e) {
		
		Player p = (Player) e.getWhoClicked();
		p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
		Alarmgui.getinstance().AlarmGuiOpen(p);
		
	}
	
	public void AlarmDeleteEvent(InventoryClickEvent e) {
		
		Player p = (Player) e.getWhoClicked();
		InventoryAction action = e.getAction();

		p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
		
		if(action == InventoryAction.PICKUP_HALF) {
			UserAlarmManager.instance().removeallalarms(p);
			Alarmgui.getinstance().AlarmGuiOpen(p);
			
		}
		else if(action == InventoryAction.PICKUP_ALL) {
			UserAlarmManager.instance().removeoldonealarm(p);
			Alarmgui.getinstance().AlarmGuiOpen(p);
		}
		
		
	}
	
	public void BacktoMain(InventoryClickEvent e) {
		
		Player p = (Player) e.getWhoClicked();

		p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
		
		Maingui.getinstance().chipitemguiopen(p);
		
		
	}
	
	public void StatClickEvent(InventoryClickEvent e) {
		
		Player p = (Player) e.getWhoClicked();

		p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
		
		Statgui.getinstance().StatGuiOpen(p);
		
	}
	
	public void StatUpgradeEvent(InventoryClickEvent e) {
		
		Player p = (Player) e.getWhoClicked();
		int rawslot = e.getRawSlot();
		
		if(rawslot == 11) {
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
			if(e.getClick().isLeftClick()) {
				UserStatManager.getinstance(p).statadd(p, "str", 1);
			}
			if(e.getClick().isRightClick()) {
				UserStatManager.getinstance(p).statadd(p, "str", 5);
			}
		}
		if(rawslot == 12) {
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
			if(e.getClick().isLeftClick()) {
				UserStatManager.getinstance(p).statadd(p, "dex", 1);
			}
			if(e.getClick().isRightClick()) {
				UserStatManager.getinstance(p).statadd(p, "dex", 5);
			}
		}
		if(rawslot == 14) {
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
			if(e.getClick().isLeftClick()) {
				UserStatManager.getinstance(p).statadd(p, "def", 1);
			}
			if(e.getClick().isRightClick()) {
				UserStatManager.getinstance(p).statadd(p, "def", 5);
			}
		}
		if(rawslot == 15) {
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
			if(e.getClick().isLeftClick()) {
				UserStatManager.getinstance(p).statadd(p, "agi", 1);
			}
			if(e.getClick().isRightClick()) {
				UserStatManager.getinstance(p).statadd(p, "agi", 5);
			}
		}
		if(rawslot == 27) {
			Maingui.getinstance().chipitemguiopen(p);
			return;
		}
		if(rawslot == 31) {
			if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equals("스탯 초기화")) {
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
				Inventory gui = e.getClickedInventory();
				gui.setItem(31, Statgui.getinstance().ResetStatAreYouSure(p));
				return;
			}
			if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equals("정말로 초기화 하시겠습니까?")) {
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0);
				UserStatManager.getinstance(p).statreset();
			}	
		}
		
		Statgui.getinstance().StatGuiOpen(p);
	}
	
	public void ClassSelectEvent(InventoryClickEvent e) {
		
		Player p = (Player) e.getWhoClicked();

		p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
		UserFileManager.getinstance().UserDetailClassDataSave(p);
		
		Classgui.getinstance().ClassSelectGuiOpen(p);
		
		

	}
	
	public void ClassAddEvent(InventoryClickEvent e) {
		
		int rawslot = e.getRawSlot();
		Player p = (Player) e.getWhoClicked();

		p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
		
		if(rawslot == 11) {
			UserFileManager.getinstance().UserDetailClassDataSave(p);
			
			UserFileManager.getinstance().UserDetailClassCallData(p, 
					UserFileManager.getinstance().UserDetailClassRegister(p, "아이테르"));
			
			UserFileManager.getinstance().UserDetailClassDataSave(p);
		}
		if(rawslot == 12) {
			UserFileManager.getinstance().UserDetailClassDataSave(p);
			
			UserFileManager.getinstance().UserDetailClassCallData(p, 
					UserFileManager.getinstance().UserDetailClassRegister(p, "엑셀러레이터"));
			
			UserFileManager.getinstance().UserDetailClassDataSave(p);
		}
		if(rawslot == 13) {
			UserFileManager.getinstance().UserDetailClassDataSave(p);
			
			UserFileManager.getinstance().UserDetailClassCallData(p, 
					UserFileManager.getinstance().UserDetailClassRegister(p, "블래스터"));
			
			UserFileManager.getinstance().UserDetailClassDataSave(p);
		}
		if(rawslot == 14) {
			UserFileManager.getinstance().UserDetailClassDataSave(p);
			
			UserFileManager.getinstance().UserDetailClassCallData(p, 
					UserFileManager.getinstance().UserDetailClassRegister(p, "바이V"));
			
			UserFileManager.getinstance().UserDetailClassDataSave(p);
		}
		if(rawslot == 15) {
			UserFileManager.getinstance().UserDetailClassDataSave(p);
			
			UserFileManager.getinstance().UserDetailClassCallData(p, 
					UserFileManager.getinstance().UserDetailClassRegister(p, "플록스"));
			
			UserFileManager.getinstance().UserDetailClassDataSave(p);
		}
		if(rawslot == 27) {
			Classgui.getinstance().ClassSelectGuiOpen(p);
		}
		
		
	}
	
	public void ClassChangeEvent(InventoryClickEvent e) {
		
		int rawslot = e.getRawSlot();
		Player player = (Player) e.getWhoClicked();
		InventoryAction action = e.getAction();
		String CurrentClass = UserManager.getinstance(player).CurrentClass+"/"+
				Integer.toString(UserManager.getinstance(player).CurrentClassNumber);

		player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
		
		if(e.getCurrentItem() == null) return;
		if(!e.getCurrentItem().hasItemMeta()) return;
		if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equals("클래스 추가")) {
			Classgui.getinstance().ClassAddGuiOpen(player);
			return;
		}
		
		if(rawslot == 27) {
			Maingui.getinstance().chipitemguiopen(player);
		}
		
		
		if(rawslot >= 11 && rawslot <= 15) rawslot -= 10;
		if(rawslot >= 20 && rawslot <= 24) rawslot -= 14;
		if(rawslot >= 29 && rawslot <= 33) rawslot -= 18;
		
		
		if(action==InventoryAction.PICKUP_ALL) {
			
			for(String Class : UserFileManager.getinstance().getClasses(player)) {
				
				if(rawslot == 1) {
					UserFileManager.getinstance().UserDetailClassDataSave(player);
					
					UserFileManager.getinstance().UserDetailClassCallData(player, Class);

					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
						@Override
						public void run() {
							Bukkit.getPluginManager().callEvent(new PlayerClassChangeEvent(player));
						}
					}, 0);
					
					UserFileManager.getinstance().UserDetailClassDataSave(player);
				}
				rawslot --;
				
			}
		}
		if(action==InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			
			for(String Class : UserFileManager.getinstance().getClasses(player)) {
				
				if(rawslot == 1) {
					if(CurrentClass.equals(Class)) {
						player.closeInventory();
						player.sendMessage("§c현재 클래스는 삭제 할 수 없습니다");
						return;
					}
					UserManager.getinstance(player).AskDeleteClassName = Class;
					Classgui.getinstance().ClassDeleteAskAreYouSureGuiOpen(player, e.getCurrentItem());
				}
				rawslot --;
				
			}
		}
		
		
	}
	
	public void ClassDeleteSureEvent(InventoryClickEvent e) {
		int rawslot = e.getRawSlot();
		Player player = (Player) e.getWhoClicked();

		player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
		
		if(rawslot == 11) {
			UserFileManager.getinstance().UserDetailClassDelete(player, UserManager.getinstance(player).AskDeleteClassName);
			player.closeInventory();
		}
		if(rawslot == 15) {
			Classgui.getinstance().ClassSelectGuiOpen(player);
		}
	}
	
	public void QuestsOpenEvent(InventoryClickEvent e) {

		Player player = (Player) e.getWhoClicked();

		player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
		
		Questgui.getinstance().QuestGuiOpen(player);
		
	}
	
	public void GoldSendPlayerAllowChat(InventoryClickEvent e) {
		
		Player player = (Player) e.getWhoClicked();

		player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
		GoldAllowReadingChatPlayerName.add(player);
		player.sendMessage("§6송금할 사람의 닉네임을 입력해주세요. 취소하시려면 '취소' 라고 입력해주세요");
		player.closeInventory();
	}
	
	public void GoldSetPlayerAmount(Player sender, Player receiver) {
		
		int gold = UserFileManager.getinstance().getGold(sender);
		sender.sendMessage("§6"+receiver.getName()+"님에게 보낼 알테라를 입력해주세요. 현재 알테라 "+gold);
		GoldAllowReadingChatAmount.put(sender, receiver);
		
		
	}
	
	public void GoldSendPlayertoPlayer(Player sender, Player receiver, int gold) {
		
		int sendergold = UserFileManager.getinstance().getGold(sender);
		UserFileManager.getinstance().setGold(sender, sendergold - gold);
		
		int receivergold = UserFileManager.getinstance().getGold(receiver);
		UserFileManager.getinstance().setGold(receiver, receivergold + gold);
		
		UserAlarmManager.instance().addalarm(sender, "§d"+receiver.getName()+"§7님에게 §a"+gold+"§d 알테라를 보냈습니다", "alterasend");
		UserAlarmManager.instance().addalarm(receiver, "§d"+sender.getName()+"§7님으로 부터 §a"+gold+"§d 알테라를 받았습니다", "alterareceive");
		
		sender.sendMessage("§a"+receiver.getName()+"님에게 성공적으로 §6"+gold+" §a알테라를 보냈습니다");
		receiver.sendMessage("§a"+sender.getName()+"님으로 부터 §6"+gold+" §a알테라를 받았습니다");

	}		
	
	public void ReturnEvent(InventoryClickEvent e) {
		Player p = (Player) e.getView().getPlayer();

		p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
		
		for(Entity entity : p.getWorld().getNearbyEntities(p.getLocation(), 10, 10, 10)) {

			if(entitycheck.entitycheck(entity)) {

			}
			if(!entitycheck.entitycheck(entity) && !(entity instanceof Player) && entity != p) {
				if(entitycheck.duelcheck(entity, p)) {
					p.sendMessage("§cpvp상태에서는 귀환을 할 수 없습니다");
					return;
				}
				p.sendMessage("§c근처에 적대적인 몹이 있습니다");
				return;
			}

		}
		
		ReturnMech.getinstance().ReturnSequence(p);
			
		
	}

}
