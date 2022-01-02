package PlayerChip;

import ClassAbility.entitycheck;
import CustomEvents.PlayerClassChangeEvent;
import Party.PartyManager;
import PlayerManager.PlayerAlarmManager;
import PlayerManager.PlayerManager;
import PlayerManager.PlayerStatManager;
import ReturnToBase.ReturnMech;
import SQL.PlayerAltera;
import SQL.PlayerClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import spellinteracttest.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuiEvent implements Listener {
	
	private final List<Player> GoldAllowReadingChatPlayerName = new ArrayList<>();
	private final HashMap<Player, Player> GoldAllowReadingChatAmount = new HashMap<>();
	private final List<Player> AllowReadingChatPlayerInvite= new ArrayList<>();
	private final Maingui Mg = Maingui.getinstance();
	
	@EventHandler
	public void InventoryClick(InventoryClickEvent e) {

		String invname = e.getView().getTitle();
		ItemStack item = e.getCurrentItem();
		int rawslot = e.getRawSlot();
		if(invname.equals("메모리카드")) {
			
			if(rawslot == Mg.alteraslot) {
				if(e.getClick().isLeftClick()) GoldSendPlayerAllowChat(e);
			}
			if(rawslot == Mg.alarmslot) AlarmClickEvent(e);
			if(rawslot == Mg.questbookslot) QuestsOpenEvent(e);
			if(rawslot == Mg.classitemslot) ClassSelectEvent(e);
			if(rawslot == Mg.statsettingslot) StatClickEvent(e);
			if(rawslot == Mg.partymanageitemslot) partyGuiClickEvent(e);
			if(rawslot == Mg.collectingitemslot) openCollectingGui(e);
			//if(rawslot == Mg.biochips)
			if(rawslot == Mg.returnitemslot) ReturnEvent(e);
	
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
		if(invname.equals("파티 관리")) {

			Player player = (Player) e.getWhoClicked();

			try {
				if(rawslot == 27) BacktoMain(e);
				else if(rawslot == 35) {
					PartyManager.getinstance().QuitParty(player);
					player.closeInventory();
				}
				else partyPlayerClickEvent(e);

				e.setCancelled(true);
			}
			catch(Exception exception) {

			}
		}
		if(invname.contains("의 파티 설정")) {

			Player player = (Player) e.getWhoClicked();


			if(rawslot == 27) (new partyGui()).openPartyGui(player);
			else partyPlayerOptionClickEvent(e);

			e.setCancelled(true);

		}
		if(invname.contains((new CollectGui()).name1)) {

			CollectGui a = new CollectGui();

			Player player = (Player) e.getWhoClicked();
			if(rawslot == a.back1) Maingui.getinstance().chipitemguiopen(player);
			else a.OpenCollectGuiGui(player, rawslot);
			clicksound(player);

			e.setCancelled(true);

		}
		if(invname.contains((new CollectGui()).name2)) {

			CollectGui a = new CollectGui();

			Player player = (Player) e.getWhoClicked();
			if(rawslot == a.back2) a.OpenCollectPlanetGui(player);

			e.setCancelled(true);
		}
		
	}

	public void clicksound(Player player) {
		player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
	}
	
	@EventHandler
	public void GettingChatFromPlayer(PlayerChatEvent e) {

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
				long currentgold = (new PlayerAltera(player)).getAltera();
				int content = Integer.parseInt(e.getMessage());
				
				if(currentgold < content) {
					player.sendMessage("§c알테라가 충분하지 않습니다");
					GoldAllowReadingChatAmount.remove(player);
					return;
				}
				else if(content <= 0) {
					player.sendMessage("§c잘못된 형식입니다");
					GoldAllowReadingChatAmount.remove(player);
				}
				else {
					
					GoldSendPlayertoPlayer(player, GoldAllowReadingChatAmount.get(player), content);
					GoldAllowReadingChatAmount.remove(player);
					
				}
			}
			catch(NumberFormatException i) {
				player.sendMessage("§c잘못된 형식입니다");
				GoldAllowReadingChatAmount.remove(player);
			}
			
			e.setCancelled(true);
			
	
		}
		if(AllowReadingChatPlayerInvite.contains(player)) {
			String content = e.getMessage();
			e.setCancelled(true);
			if(content.equals("취소")) {
				AllowReadingChatPlayerInvite.remove(player);
			}
			for(Player onlineUser : Bukkit.getOnlinePlayers()) {
				if(content.equals(onlineUser.getName())) {
					PartyManager.getinstance().inviteParty(player, onlineUser);
					AllowReadingChatPlayerInvite.remove(player);
					return;
				}
			}
			player.sendMessage("§c현재 존재 하지 않는 유저입니다");
			AllowReadingChatPlayerInvite.remove(player);

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
			PlayerAlarmManager.instance().removeallalarms(p);
			Alarmgui.getinstance().AlarmGuiOpen(p);
			
		}
		else if(action == InventoryAction.PICKUP_ALL) {
			PlayerAlarmManager.instance().removeoldonealarm(p);
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
				PlayerStatManager.getinstance(p).statadd(p, "str", 1);
			}
			if(e.getClick().isRightClick()) {
				PlayerStatManager.getinstance(p).statadd(p, "str", 5);
			}
		}
		if(rawslot == 12) {
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
			if(e.getClick().isLeftClick()) {
				PlayerStatManager.getinstance(p).statadd(p, "dex", 1);
			}
			if(e.getClick().isRightClick()) {
				PlayerStatManager.getinstance(p).statadd(p, "dex", 5);
			}
		}
		if(rawslot == 14) {
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
			if(e.getClick().isLeftClick()) {
				PlayerStatManager.getinstance(p).statadd(p, "def", 1);
			}
			if(e.getClick().isRightClick()) {
				PlayerStatManager.getinstance(p).statadd(p, "def", 5);
			}
		}
		if(rawslot == 15) {
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
			if(e.getClick().isLeftClick()) {
				PlayerStatManager.getinstance(p).statadd(p, "agi", 1);
			}
			if(e.getClick().isRightClick()) {
				PlayerStatManager.getinstance(p).statadd(p, "agi", 5);
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
				PlayerStatManager.getinstance(p).statreset();
			}	
		}
		
		Statgui.getinstance().StatGuiOpen(p);
	}
	
	public void ClassSelectEvent(InventoryClickEvent e) {
		
		Player p = (Player) e.getWhoClicked();

		p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
		(new SQL.PlayerClass(p)).classSave();
		//PlayerFileManager.getinstance().UserDetailClassDataSave(p);
		
		Classgui.getinstance().ClassSelectGuiOpen(p);
		
		

	}
	
	public void ClassAddEvent(InventoryClickEvent e) {
		
		int rawslot = e.getRawSlot();
		Player p = (Player) e.getWhoClicked();

		p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
		
		if(rawslot == 11) {
			PlayerClass pC = new PlayerClass(p);
			pC.classSave();
			pC.classCall(pC.classRegister("아이테르"));
			pC.classSave();
		}
		else if(rawslot == 12) {
			PlayerClass pC = new PlayerClass(p);
			pC.classSave();
			pC.classCall(pC.classRegister("엑셀러레이터"));
			pC.classSave();
		}
		else if(rawslot == 13) {
			PlayerClass pC = new PlayerClass(p);
			pC.classSave();
			pC.classCall(pC.classRegister("블래스터"));
			pC.classSave();
		}
		else if(rawslot == 14) {
			PlayerClass pC = new PlayerClass(p);
			pC.classSave();
			pC.classCall(pC.classRegister("바이V"));
			pC.classSave();
		}
		else if(rawslot == 15) {
			PlayerClass pC = new PlayerClass(p);
			pC.classSave();
			pC.classCall(pC.classRegister("플록스"));
			pC.classSave();
		}
		else if(rawslot == 20) {
			PlayerClass pC = new PlayerClass(p);
			pC.classSave();
			pC.classCall(pC.classRegister("카오스"));
			pC.classSave();
		}
		else if(rawslot == 21) {
			PlayerClass pC = new PlayerClass(p);
			pC.classSave();
			pC.classCall(pC.classRegister("케이론"));
			pC.classSave();
		}
		else if(rawslot == 27) {
			Classgui.getinstance().ClassSelectGuiOpen(p);
		}
		
		
	}
	
	public void ClassChangeEvent(InventoryClickEvent e) {
		
		int rawslot = e.getRawSlot();
		Player player = (Player) e.getWhoClicked();
		InventoryAction action = e.getAction();
		String CurrentClass = PlayerManager.getinstance(player).CurrentClass+"/"+
				Integer.toString(PlayerManager.getinstance(player).CurrentClassNumber);

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
			
			for(String Class : (new PlayerClass(player)).getClasses()) {
				
				if(rawslot == 1) {
					PlayerClass pC = new PlayerClass(player);
					pC.classSave();
					pC.classCall(Class);

					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
						@Override
						public void run() {
							Bukkit.getPluginManager().callEvent(new PlayerClassChangeEvent(player));
						}
					}, 0);
					
					pC.classSave();
				}
				rawslot --;
				
			}
		}
		if(action==InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			
			for(String Class : (new PlayerClass(player)).getClasses()) {
				
				if(rawslot == 1) {
					if(CurrentClass.equals(Class)) {
						player.closeInventory();
						player.sendMessage("§c현재 클래스는 삭제 할 수 없습니다");
						return;
					}
					PlayerManager.getinstance(player).AskDeleteClassName = Class;
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
			(new PlayerClass(player)).classDelete(PlayerManager.getinstance(player).AskDeleteClassName);
			//PlayerFileManager.getinstance().UserDetailClassDelete(player, PlayerManager.getinstance(player).AskDeleteClassName);
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
		if(!GoldAllowReadingChatPlayerName.contains(player)) {
			GoldAllowReadingChatPlayerName.add(player);

		}
		player.sendMessage("§6송금할 사람의 닉네임을 입력해주세요. 취소하시려면 '취소' 라고 입력해주세요");
		player.closeInventory();
	}
	
	public void GoldSetPlayerAmount(Player sender, Player receiver) {
		
		long gold = (new PlayerAltera(sender)).getAltera();
		sender.sendMessage("§6"+receiver.getName()+"님에게 보낼 알테라를 입력해주세요. 현재 알테라 "+gold);
		GoldAllowReadingChatAmount.put(sender, receiver);
		
		
	}
	
	public void GoldSendPlayertoPlayer(Player sender, Player receiver, int gold) {

		PlayerAltera senderpA = new PlayerAltera(sender);
		PlayerAltera receiverpA = new PlayerAltera(receiver);

		long sendergold = (new PlayerAltera(sender)).getAltera();
		senderpA.setAltera(sendergold - gold);
		//PlayerFileManager.getinstance().setGold(sender, sendergold - gold);

		long receivergold = (new PlayerAltera(receiver)).getAltera();
		receiverpA.setAltera(receivergold + gold);
		//PlayerFileManager.getinstance().setGold(receiver, receivergold + gold);
		
		PlayerAlarmManager.instance().addalarm(sender, "§d"+receiver.getName()+"§7님에게 §a"+gold+"§d 알테라를 보냈습니다", "alterasend");
		PlayerAlarmManager.instance().addalarm(receiver, "§d"+sender.getName()+"§7님으로 부터 §a"+gold+"§d 알테라를 받았습니다", "alterareceive");
		
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

	private final void partyGuiClickEvent(InventoryClickEvent e) {

		Player player = (Player) e.getView().getPlayer();

		player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
		partyGui partyGui = new partyGui();
		partyGui.openPartyGui(player);

	}

	private final void partyPlayerClickEvent(InventoryClickEvent e) {
		Player player = (Player) e.getView().getPlayer();

		PartyManager partyManager = PartyManager.getParty(player);
		ItemStack clickedItem = e.getCurrentItem();

		// 플레이어 초대
		if(clickedItem.getType().equals(Material.GREEN_STAINED_GLASS_PANE)) {

			if(!AllowReadingChatPlayerInvite.contains(player)) {
				AllowReadingChatPlayerInvite.add(player);
				player.sendMessage("§6초대 할 플레이어의 이름을 입력하세요. 취소하려면 '취소'를 입력하세요");
			}
			player.closeInventory();
			return;
		}

		if(partyManager != null)
			if(!partyManager.getMaster().getName().equals(player.getName())) {
				player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 1);
				player.sendMessage("§c파티장만 사용 할 수 있는 기능입니다");
				player.closeInventory();
				return;
			}

		// 플레이어 설정

		if(clickedItem.getItemMeta() == null) return;

		Player target = (Player) ((SkullMeta) clickedItem.getItemMeta()).getOwningPlayer();

		player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);
		partyGui partyGui = new partyGui();
		partyGui.openPartyPlayerOptionGui(player, target);

	}

	private final void partyPlayerOptionClickEvent(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Inventory inventory = e.getInventory();
		Player target = (Player) (((SkullMeta) (inventory.getItem(13).getItemMeta())).getOwningPlayer());

		int clickedSlot = e.getRawSlot();
		if(clickedSlot == 21) {
			PartyManager.getinstance().ChangeMaster(player, target);
		}
		else if(clickedSlot == 23) {
			PartyManager.getinstance().KickMember(player, target);
		}

		(new partyGui()).openPartyGui(player);
	}

	private final void openCollectingGui(InventoryClickEvent e) {

		Player player = (Player) e.getWhoClicked();
		player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 2);

		CollectGui collectGui = new CollectGui();
		collectGui.OpenCollectPlanetGui(player);

	}
}
