package spellinteracttest;

import ClassAbility.*;
import Interact.Damage;
import Mob.NPCManager;
import Mob.PacketReader;
import Mob.RightClickNPC;
import Mob.mob;
import Quest.LeavingWhileQuestAndJoinAgain;
import Quest.Tutorial;
import UserChip.Goldgui;
import UserChip.GuiEvent;
import UserChip.UserAlarmManager;
import UserChip.UserChipEvent;
import UserStorage.Event;
import dynamicdata.*;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import userdata.UserFileManager;
import userdata.UserManager;
import userdata.UserStatManager;
import weapons.WeaponManager;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin implements Listener {
	
	private static Main instance;

	ConsoleCommandSender consol = Bukkit.getConsoleSender();
	
	@Override
	public void onEnable() {
		
		
		instance = this;
		consol.sendMessage(ChatColor.AQUA + "Plugin Online v4");
		this.getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new GuiEvent(), this);
		getServer().getPluginManager().registerEvents(UserChipEvent.getinstance(), this);
		getServer().getPluginManager().registerEvents(new Event(), this);
		getServer().getPluginManager().registerEvents(new MeleeMotionCancel(), this);
		getServer().getPluginManager().registerEvents(new PlayerActionEvent(), this);


		saveConfig();
		
		
		File cfile = new File(getDataFolder(), "config.yml");
		if (cfile.length() == 0) {
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		
		File dfile = new File(getDataFolder(), "NPC.yml");
		if(!dfile.exists()) {
			try {
				dfile.createNewFile();
			}
			catch(IOException e	) {
				e.printStackTrace();
			}
		}
		
		
		NPCManager.getinstance().addnpctolist(); // NPC 목록 서버에 추가
		
		
		
		if(!Bukkit.getOnlinePlayers().isEmpty()) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				
				UserFileManager.getinstance().joinedplayerlistregister(player);
				
				// npc 우클릭 감지
				PacketReader reader = new PacketReader();
				reader.inject(player);
			
				UserChip.UserAlarmManager.instance().register(player); // 알람 파일 등록
				NPCManager.getinstance().addJoinPacket(player); // npc 보이게 하기
				
				RegisterInstance(player); // 유저매니저, 파일매니저, 스탯매니저 부르기
				
				UserFileManager.getinstance().UserDetailClassCallData(player, UserFileManager.getinstance().getPreviousClass(player));
				
			
			}
		}

		loop();

	}
	
	@Override
	public void onDisable() {
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			
			UserFileManager.getinstance().UserDetailClassDataSave(p);
			
			PacketReader reader = new PacketReader();
			reader.uninject(p);
			
			UnregisterInstance(p);
		}
		
		NPCManager.getinstance().removeNPCPacketallplayer();
		
		consol.sendMessage(ChatColor.YELLOW + "Plugin Offline");
		
		
		
	}
	
	
	@EventHandler
	public void serverjoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		ServerJoinToDo(p);
		
		
		
	}
	
	@EventHandler
	public void serverquit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		UserFileManager.getinstance().UserDetailClassDataSave(p);
		
		removeplayerinfo(p);
		
		PacketReader reader = new PacketReader();
		reader.uninject(e.getPlayer());
		
		UnregisterInstance(p);
	}
	
	@EventHandler
	public void slimesplitevent(SlimeSplitEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void fallingblock(EntityChangeBlockEvent e) {
		Location loc = e.getBlock().getLocation();
		Block b = loc.getBlock();
		
		if(b.getType() == Material.REDSTONE_ORE) return;
		
		loc.getBlock().setType(Material.AIR);
		e.setCancelled(true);
	}
	
	@EventHandler
	public void PlayerWorldChangeEvent(PlayerChangedWorldEvent e) { // 월드 이동 
		Player player = (Player) e.getPlayer();
		
		NPCManager.getinstance().removeNPCPacket(player);
		
		this.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
			NPCManager.getinstance().addJoinPacket(player);
		}, 20);
		
	
		
		
		////////////////////////////////////
	}
	
	@EventHandler
	public void Inventory(InventoryClickEvent event) { // 인벤토리 왼손키
		
		//Bukkit.broadcastMessage(Integer.toString(event.getSlot()));
		
		if(event.getSlotType() == SlotType.QUICKBAR && event.getSlot() == 40) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void InventoryDragEvent(InventoryDragEvent event) { // 인벤토리 왼손키
		
		if(event.getView().getSlotType(40) == SlotType.QUICKBAR) {
			event.setCancelled(true);
		}
	}
	
	//////////////////////////////////////////
	
	
	@EventHandler
	public void onClick(RightClickNPC event) {
		Player player = event.getPlayer();
		player.sendMessage("nice nice nice");
	}
	

	
	
	@EventHandler
	public void hitentity(EntityDamageByEntityEvent e) { // 좌
		
		if(e.getDamager() instanceof Arrow && e.getEntity() instanceof LivingEntity) {
			if(e.getDamager().getCustomName() != null) {
				String split[] = e.getDamager().getCustomName().split(":");
				
				String skillname = split[0];
				String name = split[1];
				
				if(split[1] != null) {
					

					if(e.getEntity().getCustomName() != null) {
						
						if(e.getEntity().getCustomName().equals("샌드백")) {  // 화살로 친에가 샌드백이면
							for(Player p : Bukkit.getOnlinePlayers()) {
								if(p.getName().equals(name)) {
									if(PlayerHealth.getinstance(p).getCurrentShield()>0) {  // 플레이어가 1이상의 보호막을 가지고 있으면
										Damage.getinstance().taken(2000, (LivingEntity) p);
										p.sendMessage("§e시험 진행 A.I:§e §f시간이 지나면 보호막은 자동으로 채워지니 염려하지 않으셔도 됩니다.");
										p.playSound(p.getLocation(), "meme.tut6", 5, 1);
										Quest.Tutorial.trainerbothit.put(p, 1);
									}
								}
							}
						}				
						else if(e.getEntity().getCustomName().equals("과녁")) {  // 화살로 친에가 과녁이면
							for(Player p : Bukkit.getOnlinePlayers()) {
								if(p.getName().equals(name)) {								
									Quest.Tutorial.trainerbothit.put(p, 1);
								}
							}
						}
					}

					for(Player p : Bukkit.getOnlinePlayers()) {
						if(p.getName().equals(name)) {
							
							if(!Tutorial.examset.containsKey(p)) {
								
								if(Tutorial.exambothit.containsKey(p)) {// 튜토리얼 활성화?
									
									if(e.getEntity().getCustomName() != null && e.getEntity() instanceof Slime) {  // 슬라임 봇 때릴 때
										String splitslime[] = e.getEntity().getCustomName().split("m");
										if(splitslime[1] != null) {
												
												int number = Integer.parseInt(splitslime[1]);
												
												if(Tutorial.exambothit.get(p)[number-1] == 0) { // 때린 봇의 번혿가 0번이면

													Tutorial.exambothit.get(p)[number-1] = number; // 때린 봇 번호 추가
													Tutorial.exambothitcount.replace(p, Tutorial.exambothitcount.get(p)+1); // 횟수 추가
													break; // 번호 넣으면 탈출
												}						
											}
											
										}

								}
							}
										
							if(skillname.equals("dart")) {
								int dmg = UserManager.getinstance(p).spelldmgcalculate(p, 1);
								Damage.getinstance().taken(dmg, (LivingEntity) e.getEntity(), p);
							}
							else if(skillname.equals("bomb")) {
								
								Blaster.getinstance().grenadelauncherbomb(e.getEntity().getLocation(), p);
			
							}
							
							
							
							break;
						}
					}	
				}
			}
		}
	}
	
	@EventHandler
	public void respawn(PlayerRespawnEvent e) {
		Player player = (Player) e.getPlayer();
		PlayerHealth.getinstance(player).setCurrentHealth(UserManager.getinstance(player).Health);


	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent e) {
		
		Action action = e.getAction();
		Player player = e.getPlayer();
		
	}
	
	
    public static Main getInstance() {
        return instance;
    }
    
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		
		
		
		
		Player player = (Player) sender;
		
		switch (args[0]) {
		
		case "npclist":{
			for(EntityPlayer npc : NPCManager.getinstance().getNPCs()) {
				Bukkit.broadcastMessage(npc.getName());
			}
			break;
		}
		
		case "getskull":{
			Goldgui a = new Goldgui();
			player.getInventory().addItem(a.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2I0NjhmNTU5OGFmN2M2NmYxYzFkNzM0NjVlYzMxZGNkNjdhODhkOTAwNTFiODk3ZGFkODRiYjM2MGIzMzc5OSJ9fX0="));
			
		}
		
		case "getgold":{
			UserFileManager.getinstance().setGold(player, Integer.parseInt(args[1]));
			break;
		}
		
		case "alarmdb":{
			SQLiteManager sql = new SQLiteManager();
			sql.addalarm(player, "test", "type");
			
			
			break;
		}
		
		case "save":{
			for(Player p : Bukkit.getOnlinePlayers()) {
				
				UserFileManager.getinstance().UserDetailClassDataSave(p);
			}
			break;
		}
		
		case "item":{
			WeaponManager data = new WeaponManager();
			if(data.checkname(args[1]) == true)	{
				Bukkit.broadcastMessage("아이템 "+args[1]+"가 존재합니다");
				player.getInventory().addItem(data.getitem(args[1]));
			}
			else {
				Bukkit.broadcastMessage("아이템 "+args[1]+"가 존재하지 않습니다");
			}
			break;
		}
		
		case "level":{
			
			UserStatManager.getinstance(player).setlvl(Integer.parseInt(args[1]));
			break;
		}
		
		case "alarm":{
			
			args[1].replace("_", " ");
			
			UserAlarmManager.instance().addalarmtoallplayers(args[1], "notification");
			break;
		}
		
		case "hand":{
			UserManager.getinstance(player).equipmentsetting();
			break;
		}
		
		case "stats":{
			player.sendMessage("Damage:" + Integer.toString(UserManager.getinstance(player).MinDamage)+"-"+Integer.toString(UserManager.getinstance(player).MaxDamage));
			player.sendMessage("Health: " + Integer.toString(UserManager.getinstance(player).Health));
			player.sendMessage("Shield: " + Integer.toString(UserManager.getinstance(player).ShieldRaw));
			player.sendMessage("CurrentClass " + UserManager.getinstance(player).CurrentClass);
			player.sendMessage("WeaponClass " + UserManager.getinstance(player).WeaponClass);
			player.sendMessage("WeaponLevel " + UserManager.getinstance(player).WeaponLevelreq);
			player.sendMessage("WeaponStr " + UserManager.getinstance(player).WeaponStrreq);
			player.sendMessage("WeaponDex " + UserManager.getinstance(player).WeaponDexreq);
			player.sendMessage("WeaponDef " + UserManager.getinstance(player).WeaponDefreq);
			player.sendMessage("WeaponAgi " + UserManager.getinstance(player).WeaponAgireq);
			player.sendMessage("equipments " + UserManager.getinstance(player).getplayerequipments(player).size());
			break;
		}
		
		case "statcheck":{
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(p.getName().equals(args[1])) {
					
					player.sendMessage(Integer.toString(UserStatManager.getinstance(p).getStr()));
					player.sendMessage(Integer.toString(UserStatManager.getinstance(p).getDex()));
					player.sendMessage(Integer.toString(UserStatManager.getinstance(p).getDef()));
					player.sendMessage(Integer.toString(UserStatManager.getinstance(p).getAgi()));
					player.sendMessage(Integer.toString(UserStatManager.getinstance(p).getlvl()));
					
					
				}
				
				
			}
			
			break;
		}
		
		
		case "heal":{
			PlayerHealth.getinstance(player).setCurrentHealth(UserManager.getinstance(player).Health);
			break;
		}
		case "userchip":{
			player.getInventory().addItem(UserChip.Maingui.getinstance().chipitemget(player));
			break;
		}
		
		
		case "impulse":{
			Aether.impulse.replace(player, 1000d);
			break;
		}
				
		case "essence":{
			ByV.essence.replace(player, 1000);
			break;
		}
		
		case "userfile":{
			for(Player p : Bukkit.getOnlinePlayers()) {
				//save.getinstance().firstfile(p);
			}
			break;

		}
		
		case "duel":{
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(p.getName().equals(args[1])) {
					UserManager.dual.put(player, p);
				}
			}
			Bukkit.broadcastMessage(player.getName()+" "+UserManager.dual.get(player).getName());
			break;
			
		}
		
		case "duelclose":{
			UserManager.dual.clear();
			break;
		}
		
		
		case "score":{
			Tutorial.exambothitcount.replace(player, Integer.parseInt(args[1]));
			break;
		}
		
		case "registerdata":{
			UserFileManager.getinstance().UserDetailRegister(player);
			break;
		}
		
		}
		return true;
	}
	
	public void loop() {
		

		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				UserManager.updateloop();
				
				NPCManager.getinstance().sendHeadRotationPacket();
				
				for(World world : Bukkit.getWorlds()) {
					for(LivingEntity entity : world.getLivingEntities()) {
						if(entity instanceof Player) continue;
						EntityHealth.getinstance(entity).EntityHealthWatcher();
						EntityStatus.getinstance(entity).BurnsLoop();
					}
				}
				
				
				for(Player p: Bukkit.getOnlinePlayers()) {
					PlayerHealth.getinstance(p).HealthWatcher();
					PlayerHealth.getinstance(p).ShieldRegeneration();
					PlayerEnergy.getinstance(p).OverloadCoolDown();
					PlayerCombination.getinstance(p).KeyBind();
					PlayerFunction.getinstance(p).MeleeDelayControlLoop();
					
					
					EntityHealthBossBar.getinstance(p).healthbossbarloop();
					
				}
				
				PlayerInfoActionBar.actionbar();
				

				
				Aether.getinstance().AetherPassive();
				
				Accelerator.getinstance().Passive1();
				
				Phlox.getinstance().PhloxPassive();
				Phlox.getinstance().meleerobotcountloop();
				
				ByV.getinstance().ByVPassive();
				
				
				Quest.Loop.loop();
				
				Packets.loop.packetloop();
				
				ArrowCheck.onGround();
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
		
		mob mob = new mob();
		Hologram.loop loop = new Hologram.loop();
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				for(Player p : Bukkit.getOnlinePlayers()) {
					PlayerEnergy.getinstance(p).Regeneration();
				}
				mob.loop();
				mob.mobdelete();
				loop.loop();
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 20);
		
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1200);
		
	}
	
	
	public void removeplayerinfo(Player p) {
		
		Accelerator.getinstance().removemaps(p);
		Aether.getinstance().removemaps(p);
		Blaster.getinstance().removemaps(p);
		ByV.getinstance().removemaps(p);
		Phlox.getinstance().removemaps(p);
		
		ClassAbility.Combination.getinstance().removemaps(p);
		
		
		
		PlayerHealth.getinstance(p).removeinstance();
		PlayerEnergy.getinstance(p).removeinstance();
		EntityHealthBossBar.getinstance(p).removeinstance();
		PlayerCombination.getinstance(p).removeinstance();
		PlayerFunction.getinstance(p).removeinstance();
		
		
		
		
		
	}
	
	
	public void RegisterInstance(Player p) {
		
		UserFileManager.getinstance().UserDetailRegister(p);
		UserStatManager.getinstance(p);
		UserManager.getinstance(p);
		
		
		
	}
	
	public void UnregisterInstance(Player p) {
		
		UserStatManager.getinstance(p).removeinstance(p);
		UserManager.getinstance(p).removeinstance();
		
	}
	
	public void ServerJoinToDo(Player p) {
		
		UserFileManager.getinstance().joinedplayerlistregister(p);
		
		LeavingWhileQuestAndJoinAgain leavingwhilequestandjoinagain = new LeavingWhileQuestAndJoinAgain();
		leavingwhilequestandjoinagain.restore(p); // 튜토리얼 도중 포기 감지

		this.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
			NPCManager.getinstance().addJoinPacket(p);
		}, 20); // npc 소환
		
		PacketReader reader = new PacketReader();
		reader.inject(p); // npc 우클릭 감지 등록
		
		UserChip.UserAlarmManager.instance().register(p); // 유저 알람 파일 등록
		
		RegisterInstance(p); // 
		
		UserFileManager.getinstance().UserDetailClassCallData(p, UserFileManager.getinstance().getPreviousClass(p));
	}


	

}
