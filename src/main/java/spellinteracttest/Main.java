package spellinteracttest;

import Auction.Auction;
import Auction.AuctionNPC;
import ClassAbility.Accelerator;
import ClassAbility.Aether.Aether;
import ClassAbility.Cheiron.CheironArrowEvent;
import ClassAbility.Phlox;
import Duel.DuelManager;
import Gliese581cMobs.Gliese581cEntitySummon;
import Items.ItemManager;
import Items.WeaponManager;
import Map.Map;
import Mob.EntityManager;
import Mob.MobEventManager;
import Mob.MobMechManager;
import Mob.mob;
import PacketListener.PacketReader;
import PacketRecord.EditEventListener;
import Party.EventProcess;
import Party.PartyManager;
import Party.TabCompleter;
import PlanetSelect.planetDetect;
import PlanetSelect.planetSelectEvent;
import PlayParticle.PlayParticle;
import PlayerChip.Goldgui;
import PlayerChip.GuiEvent;
import PlayerChip.UserChipEvent;
import PlayerManager.*;
import QuestFunctions.LeavingWhileQuestAndJoinAgain;
import QuestFunctions.QuestNPCManager;
import QuestFunctions.UserQuestManager;
import ReturnToBase.ReturnMech;
import SQL.PlayerAlarm;
import SQL.PlayerAltera;
import SQL.PlayerClass;
import SQL.SQLManager;
import Shop.RightClickNPC;
import Shop.ShopNPCManager;
import SpyGlass.SpyGlassEvent;
import SpyGlass.SpyGlassItemManager;
import UserStorage.Event;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {
	
	private static Main instance;

	ConsoleCommandSender consol = Bukkit.getConsoleSender();
	
	@Override
	public void onEnable() {

		SQLManager.setUpMySQL();
		instance = this;
		consol.sendMessage(ChatColor.AQUA + "Plugin Online v4");
		this.getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new GuiEvent(), this);
		getServer().getPluginManager().registerEvents(UserChipEvent.getinstance(), this);
		getServer().getPluginManager().registerEvents(new Event(), this);
		getServer().getPluginManager().registerEvents(new PlayerActionListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerCustomEventListener(), this);
		getServer().getPluginManager().registerEvents(ReturnMech.getinstance(), this);
		getServer().getPluginManager().registerEvents(UserQuestManager.Singleton(), this);
		getServer().getPluginManager().registerEvents(new ItemManager(), this);
		getServer().getPluginManager().registerEvents(new planetSelectEvent(), this);
		getServer().getPluginManager().registerEvents(new EventProcess(), this);
		getServer().getPluginManager().registerEvents(new SpyGlassEvent(), this);
		getServer().getPluginManager().registerEvents(new Gliese581cEntitySummon(), this);
		getServer().getPluginManager().registerEvents(new MobEventManager(), this);
		//getServer().getPluginManager().registerEvents(new PacketListener(), this);
		getServer().getPluginManager().registerEvents(new EditEventListener(), this);
		getServer().getPluginManager().registerEvents(PacketRecord.Record.getInstance(), this);
		getServer().getPluginManager().registerEvents(new Map(), this);
		getServer().getPluginManager().registerEvents(CheironArrowEvent.getInstance(), this);
		getServer().getPluginManager().registerEvents(new Auction(), this);


		getCommand("party").setTabCompleter(new TabCompleter());
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

		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
			if(!Bukkit.getOnlinePlayers().isEmpty()) {
				for(Player player : Bukkit.getOnlinePlayers()) {

					ServerJoinToDo(player);
				}
			}

			loop();

			QuestNPCManager.getinstance().addnpctolist();
			ShopNPCManager.getinstance().addnpctolist(); // NPC 목록 서버에 추가
			(new AuctionNPC()).Register();
		}, 20);

	}
	
	@Override
	public void onDisable() {
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			
			(new PlayerClass(p)).classSave();
			
			PacketReader reader = new PacketReader(p);
			reader.uninject(p);
			
			UnregisterInstance(p);

		}
		
		ShopNPCManager.getinstance().removeNPCPacketallplayer();
		QuestNPCManager.getinstance().removeNPCPacketallplayer();
		(new AuctionNPC()).RemoveAll();
		
		consol.sendMessage(ChatColor.YELLOW + "Plugin Offline");
		EntityManager.DeleteAllEntity();
		
		
		
	}

    @EventHandler
    public void DisableXPOrb(EntityTargetEvent event) {
        Entity entity = event.getEntity();
        EntityType entityType = event.getEntityType();
        if(entityType == EntityType.EXPERIENCE_ORB) {
            event.setCancelled(true);
        }
    }
	
	@EventHandler
	public void serverjoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		ServerJoinToDo(p);

	}
	
	@EventHandler
	public void serverquit(PlayerQuitEvent e) {
		Player p = e.getPlayer();

		(new PlayerClass(p)).classSave();
		//PlayerFileManager.getinstance().UserDetailClassDataSave(p);
		
		PacketReader reader = new PacketReader(p);
		reader.uninject(e.getPlayer());
		
		UnregisterInstance(p);
	}
	
	@EventHandler
	public void slimesplitevent(SlimeSplitEvent e) {
		e.setCancelled(true);
	}
	
//	@EventHandler
//	public void fallingblock(EntityChangeBlockEvent e) {
//		Location loc = e.getBlock().getLocation();
//		Block b = loc.getBlock();
//
//		if(b.getType() == Material.REDSTONE_ORE) return;
//
//		loc.getBlock().setType(Material.AIR);
//		e.setCancelled(true);
//	}
	
	@EventHandler
	public void PlayerWorldChangeEvent(PlayerChangedWorldEvent e) { // 월드 이동 
		Player player = (Player) e.getPlayer();
		
		ShopNPCManager.getinstance().removeNPCPacket(player);
		QuestNPCManager.getinstance().removeNPCPacket(player);
		
		this.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
			ShopNPCManager.getinstance().addJoinPacket(player);
			QuestNPCManager.getinstance().addJoinPacket(player);
			(new AuctionNPC()).Show(player);
		}, 20);

	}
	
	@EventHandler
	public void Inventory(InventoryClickEvent event) { // 인벤토리 왼손키
		
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
	
	
	@EventHandler
	public void onClick(RightClickNPC event) {
		Player player = event.getPlayer();
	}

//	@EventHandler
//	public void hitentity(EntityDamageByEntityEvent e) { // 좌
//
//		if(e.getDamager() instanceof Arrow && e.getEntity() instanceof LivingEntity) {
//			if(e.getDamager().getCustomName() != null) {
//				String split[] = e.getDamager().getCustomName().split(":");
//
//				String skillname = split[0];
//				String name = split[1];
//
//				if(split[1] != null) {
//
//
//					if(e.getEntity().getCustomName() != null) {
//
//						if(e.getEntity().getCustomName().equals("샌드백")) {  // 화살로 친에가 샌드백이면
//							for(Player p : Bukkit.getOnlinePlayers()) {
//								if(p.getName().equals(name)) {
//									if(PlayerHealthShield.getinstance(p).getCurrentShield()>0) {  // 플레이어가 1이상의 보호막을 가지고 있으면
//										Damage.getinstance().taken(2000, (LivingEntity) p, (LivingEntity) e.getEntity());
//										p.sendMessage("§e시험 진행 A.I:§e §f시간이 지나면 보호막은 자동으로 채워지니 염려하지 않으셔도 됩니다.");
//										p.playSound(p.getLocation(), "meme.tut6", 5, 1);
//										Tutorial.trainerbothit.put(p, 1);
//									}
//								}
//							}
//						}
//						else if(e.getEntity().getCustomName().equals("과녁")) {  // 화살로 친에가 과녁이면
//							for(Player p : Bukkit.getOnlinePlayers()) {
//								if(p.getName().equals(name)) {
//									Tutorial.trainerbothit.put(p, 1);
//								}
//							}
//						}
//					}
//
//					for(Player p : Bukkit.getOnlinePlayers()) {
//						if(p.getName().equals(name)) {
//
//							if(!Tutorial.examset.containsKey(p)) {
//
//								if(Tutorial.exambothit.containsKey(p)) {// 튜토리얼 활성화?
//
//									if(e.getEntity().getCustomName() != null && e.getEntity() instanceof Slime) {  // 슬라임 봇 때릴 때
//										String splitslime[] = e.getEntity().getCustomName().split("m");
//										if(splitslime[1] != null) {
//
//												int number = Integer.parseInt(splitslime[1]);
//
//												if(Tutorial.exambothit.get(p)[number-1] == 0) { // 때린 봇의 번혿가 0번이면
//
//													Tutorial.exambothit.get(p)[number-1] = number; // 때린 봇 번호 추가
//													Tutorial.exambothitcount.replace(p, Tutorial.exambothitcount.get(p)+1); // 횟수 추가
//													break; // 번호 넣으면 탈출
//												}
//											}
//
//										}
//
//								}
//							}
//
//							if(skillname.equals("dart")) {
//								int dmg = PlayerManager.getinstance(p).spelldmgcalculate(p, 1);
//								Damage.getinstance().taken(dmg, (LivingEntity) e.getEntity(), p);
//							}
//							else if(skillname.equals("bomb")) {
//
//								Blaster.getinstance().grenadelauncherbomb(e.getEntity().getLocation(), p);
//
//							}
//
//
//
//							break;
//						}
//					}
//				}
//			}
//		}
//	}
	
	@EventHandler
	public void respawn(PlayerRespawnEvent e) {
		Player player = (Player) e.getPlayer();
		PlayerHealthShield.getinstance(player).setCurrentHealth(PlayerManager.getinstance(player).Health);

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

		String cmdName = command.getName().toLowerCase();

		if(cmdName.equals("duel")) {

			if(args.length == 1) {
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(p.getName().equals(args[0])) {
						DuelManager.getDuelManager(player).sendDuelRequest(player, p);
					}
				}

			}
//			if(args[0].equals("accept")) {
//				DuelManager.getDuelManager();
//			}
		}


		if(cmdName.equals("party")) {
			if(args.length == 0) {
				player.sendMessage("§b/party create §7파티를 만듭니다");
				player.sendMessage("§b/party invite <유저 이름> §7해당 유저를 파티로 초대합니다");
				player.sendMessage("§b/party join §7초대받은 파티에 참가합니다");
				player.sendMessage("§b/party leave §7현재 파티에서 나갑니다");
				player.sendMessage("§b/party promote <유저 이름> §7해당 유저를 파티장으로 승급시킵니다");
				player.sendMessage("§b/party kick <유저 이름> §7해당 유저를 파티에서 추방시킵니다");
				return true;
			}
			switch (args[0]) {

				case "create": {
					PartyManager.getinstance().createParty(player);
					break;
				}
				case "invite": {
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(args[1].equals(p.getName())) {
							PartyManager.getinstance().inviteParty(player, p);
						}
					}
					break;
				}
				case "join": {
					PartyManager.getinstance().JoinParty(player);
					break;
				}
				case "leave": {
					PartyManager.getinstance().QuitParty(player);
					break;
				}
				case "promote": {
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(args[1].equals(p.getName())) {
							PartyManager.getinstance().ChangeMaster(player, p);
						}
					}
					break;
				}
				case "kick": {
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(args[1].equals(p.getName())) {
							PartyManager.getinstance().KickMember(player, p);
						}
					}
					break;
				}
			}
		}

		if(cmdName.equals("파티")) {
			if(args.length == 0) {
				player.sendMessage("§b/파티 생성 §7파티를 만듭니다");
				player.sendMessage("§b/파티 초대 <유저 이름> §7해당 유저를 파티로 초대합니다");
				player.sendMessage("§b/파티 참가 §7초대받은 파티에 참가합니다");
				player.sendMessage("§b/파티 나가기 §7현재 파티에서 나갑니다");
				player.sendMessage("§b/파티 승급 <유저 이름> §7해당 유저를 파티장으로 승급시킵니다");
				player.sendMessage("§b/파티 추방 <유저 이름> §7해당 유저를 파티에서 추방시킵니다");
				return true;
			}
			switch (args[0]) {

				case "생성": {
					PartyManager.getinstance().createParty(player);
					break;
				}
				case "초대": {
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(args[1].equals(p.getName())) {
							PartyManager.getinstance().inviteParty(player, p);
						}
					}
					break;
				}
				case "참가": {
					PartyManager.getinstance().JoinParty(player);
					break;
				}
				case "나가기": {
					PartyManager.getinstance().QuitParty(player);
					break;
				}
				case "승급": {
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(args[1].equals(p.getName())) {
							PartyManager.getinstance().ChangeMaster(player, p);
						}
					}
					break;
				}
				case "추방": {
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(args[1].equals(p.getName())) {
							PartyManager.getinstance().KickMember(player, p);
						}
					}
					break;
				}
			}
		}
		
		

		
		switch (args[0]) {

			case "itemmarket": {
				File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "config.yml");
				FileConfiguration config = YamlConfiguration.loadConfiguration(file);

				new BukkitRunnable() {

					int time = 0;

					@Override
					public void run() {
						for(String s_ : config.getConfigurationSection("").getKeys(false)) {
							WeaponManager data = new WeaponManager(s_);
							if(data.checkname(s_) && Math.random() * 2 < 1)	{
								(new Auction()).MarketRegister(player, data.getitem(), (int) (Math.random() * 2000), 1);
								break;
							}
						}

						time++;
						if(time>4) cancel();
					}
				}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

				break;
			}

			case "nbtcheck": {
				ItemStack itemStack = player.getInventory().getItemInMainHand();
				net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
				NBTTagCompound tag = nmsStack.getTag();
				for(String stats : WeaponManager.statlist) {
					Bukkit.broadcastMessage(stats+" : "+tag.getIntArray(stats)[0]+" "+tag.getIntArray(stats)[1]+" "+
							tag.getIntArray(stats)[2]+" ");
				}

				break;
			}

			case "entitysize": {
				Bukkit.broadcastMessage(Integer.toString(EntityManager.getEntityManagerInstanceSize()));
				break;
			}

			case "entitylist": {
				for(String string : EntityManager.getEntityClassList()) {
					Bukkit.broadcastMessage(string);
				}
				break;
			}

			case "record": {
				if(args[1] == null) break;
				PacketRecord.Record.getInstance().SetRecordField(player, args[1]);
				break;
			}

			case "play": {
				if(args[1] == null) break;
				(new PacketRecord.Play(player, args[1])).Play();
			}

			case "recordstop": {
				PacketRecord.Record.getInstance().RecordStop();
				break;
			}

			case "recordlist" : {
				(new PacketRecord.FileManage()).List();
				break;
			}
			case "recorderase" : {
				if(args[1] == null) break;
				(new PacketRecord.FileManage()).Erase(args[1]);
				break;
			}

			case "edit": {
				if(args[1] == null) break;
				(new PacketRecord.Play(player, args[1])).Edit();
				break;
			}

			case "summonbr" : {
				(new Gliese581cEntitySummon()).summonBloodRoot(player);
				break;
			}

			case "summonfr" : {
				(new Gliese581cEntitySummon()).summonFoxRat(player);
				break;
			}

			case "summonho" : {
				(new Gliese581cEntitySummon()).summonHiddenOasis(player);
				break;
			}

			case "summond" : {
				(new Gliese581cEntitySummon()).summonGuard(player);
				break;
			}

			case "summongb" : {
				(new Gliese581cEntitySummon()).summonGlowingButterFly(player);
				break;
			}

			case "playparticle": {
				(new PlayParticle(Particle.CRIT)).Circle(player, 2);
				break;
			}

			case "spyglass": {
				player.getInventory().addItem(((new SpyGlassItemManager()).getSpyGlassItem(SpyGlassItemManager.SpyGlassPlanet.Gliese581c, 1)));

				break;
			}

			case "glowingon": {

				DataWatcher dataWatcher = ((CraftPlayer) player).getHandle().getDataWatcher();
				dataWatcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte) 0x40);
				PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
				for(Player member : Bukkit.getOnlinePlayers()) {
					CraftPlayer p = (CraftPlayer) player;
					connection.sendPacket(new PacketPlayOutEntityMetadata(p.getEntityId(), dataWatcher, true));
				}
				break;
			}

			case "glowingoff": {
				DataWatcher dataWatcher = ((CraftPlayer) player).getHandle().getDataWatcher();
				dataWatcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte) 0x40);
				PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
				for(Player member : Bukkit.getOnlinePlayers()) {
					CraftPlayer p = (CraftPlayer) player;
					connection.sendPacket(new PacketPlayOutEntityMetadata(p.getEntityId(), dataWatcher, false));
				}
				break;
			}

			case "resetquest":{
				UserQuestManager.Singleton().QuestReset(player);
				break;
			}


			case "getskull":{
				Goldgui a = new Goldgui();
				player.getInventory().addItem(a.getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2I0NjhmNTU5OGFmN2M2NmYxYzFkNzM0NjVlYzMxZGNkNjdhODhkOTAwNTFiODk3ZGFkODRiYjM2MGIzMzc5OSJ9fX0="));
				break;

			}

			case "getgold":{
				(new PlayerAltera(player)).setAltera(Long.parseLong(args[1]));
				//PlayerFileManager.getinstance().setGold(player, Long.parseLong(args[1]));
				break;
			}

			case "save":{
				for(Player p : Bukkit.getOnlinePlayers()) {

					(new PlayerClass(p)).classSave();
					//PlayerFileManager.getinstance().UserDetailClassDataSave(p);
				}
				break;
			}

			case "item":{
				if(args[1] == null) break;
				WeaponManager data = new WeaponManager(args[1]);
				if(data.checkname(args[1]) == true)	{
					Bukkit.broadcastMessage("아이템 "+args[1]+"가 존재합니다");
					player.getInventory().addItem(data.getitem());
				}
				else {
					Bukkit.broadcastMessage("아이템 "+args[1]+"가 존재하지 않습니다");
				}
				break;
			}

			case "level":{

				PlayerManager.getinstance(player).setlvl(Integer.parseInt(args[1]));
				break;
			}

			case "alarm":{

				args[1].replace("_", " ");

				PlayerAlarm.broadcastAlarm(args[1], "notification");
				//PlayerAlarmManager.instance().addalarmtoallplayers(args[1], "notification");
				break;
			}

			case "hand":{
				PlayerManager.getinstance(player).equipmentsetting();
				break;
			}

			case "stats":{
				player.sendMessage("Level:" +Integer.toString(PlayerManager.getinstance(player).getlvl()));
				player.sendMessage("EXP:"+Integer.toString(PlayerManager.getinstance(player).getexp()));
				player.sendMessage("Damage:" + Integer.toString(PlayerManager.getinstance(player).MinDamage)+"-"+Integer.toString(PlayerManager.getinstance(player).MaxDamage));
				player.sendMessage("Health: " + Integer.toString(PlayerManager.getinstance(player).Health));
				player.sendMessage("Shield: " + Integer.toString(PlayerManager.getinstance(player).ShieldRaw));
				player.sendMessage("WalkSpeed: " + PlayerManager.getinstance(player).WalkSpeed);
				player.sendMessage("CurrentClass " + PlayerManager.getinstance(player).CurrentClass);
				player.sendMessage("WeaponClass " + PlayerManager.getinstance(player).WeaponClass);
				player.sendMessage("WeaponLevel " + PlayerManager.getinstance(player).WeaponLevelreq);
				player.sendMessage("WeaponStr " + PlayerManager.getinstance(player).WeaponStrreq);
				player.sendMessage("WeaponDex " + PlayerManager.getinstance(player).WeaponDexreq);
				player.sendMessage("WeaponDef " + PlayerManager.getinstance(player).WeaponDefreq);
				player.sendMessage("WeaponAgi " + PlayerManager.getinstance(player).WeaponAgireq);
				player.sendMessage("equipments " + PlayerManager.getinstance(player).getplayerequipments(player).size());
				break;
			}

			case "statcheck":{
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(p.getName().equals(args[1])) {

						player.sendMessage(Integer.toString(PlayerManager.getinstance(p).getStr()));
						player.sendMessage(Integer.toString(PlayerManager.getinstance(p).getDex()));
						player.sendMessage(Integer.toString(PlayerManager.getinstance(p).getDef()));
						player.sendMessage(Integer.toString(PlayerManager.getinstance(p).getAgi()));
						player.sendMessage(Integer.toString(PlayerManager.getinstance(p).getlvl()));
					}
				}
				break;
			}

			case "getws": {
				Bukkit.broadcastMessage(Float.toString(player.getWalkSpeed()));
				break;
			}


			case "heal":{
				PlayerHealthShield.getinstance(player).setCurrentHealth(PlayerManager.getinstance(player).Health);
				break;
			}
			case "userchip":{
				player.getInventory().addItem(PlayerChip.Maingui.getinstance().chipitemget(player));
				break;
			}

			case "impulse":{
				PlayerFunction.getinstance(player).AEImpulse = 1000;
				break;
			}

			case "essence":{
				PlayerFunction.getinstance(player).essence = 1000;
				break;
			}
			case "header": {
				List<String> header = new ArrayList<>();
				String blank = "                                             ";
				player.setPlayerListHeader(blank+"\n"+"header1\n"+"header1\n"+"header1\n"+"header1\n"+"header1\n"+blank);
				player.setPlayerListFooter(blank+"\n"+"footer\n"+"footer\n"+"footer\n"+"footer\n"+"footer\n"+"footer\n"+blank);
				break;
			}
			case "map": {
				(new Map()).getMap(player, MapView.Scale.CLOSEST);
				break;
			}
		}
		return true;
	}
	
	public void loop() {

		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				PlayerManager.updateloop();
				

				
//				for(World world : Bukkit.getWorlds()) {
//					for(LivingEntity entity : world.getLivingEntities()) {
//						if(entity instanceof Player) continue;
//						if(entity instanceof ArmorStand) continue;
//						if(entity.isInvulnerable()) continue;
//						if(EntityManager.isRegister(entity)) {
//							EntityManager.getinstance(entity).EntityWatcher();
//							EntityStatusManager.getinstance(entity).BurnsLoop();
//						}
//					}
//				}
				for(Entity entity : EntityManager.getEntityManagerEntities()) {
					EntityManager.getinstance(entity).EntityWatcher();
				}

				for(Player p: Bukkit.getOnlinePlayers()) {
					PlayerHealthShield.getinstance(p).HealthWatcher();
					PlayerHealthShield.getinstance(p).ShieldRegeneration();
					PlayerEnergy.getinstance(p).OverloadCoolDown();
					PlayerCombination.getinstance(p).KeyBind();
					PlayerFunction.getinstance(p).PlayerFunctionLoop();
					EntityHealthBossBar.getinstance(p).healthBarLoop();
					//PlayerWASDListener.getInstance(p).WASDListener();
					//(new Auction.Auction()).MarketUpdate(p);
				}
				
				PlayerInfoActionBar.actionbar();

				Accelerator.getinstance().Passive1();
				Phlox.getinstance().PhloxPassive();
				Phlox.getinstance().meleerobotcountloop();

				QuestFunctions.Loop.loop();
				
				Packets.loop.packetloop();
				
				ArrowCheck.ArrowWatcher();

				MobMechManager.getInstance().RunGliese581cMobMech();
				SpyGlass.SpyGlassManager.watchSpyGlassEnable();
				PartyManager.getinstance().partyGlowingLoop();
				DuelManager.duelLoop();
				Map.updateMap();
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

		new BukkitRunnable() {
			@Override
			public void run() {

				EntityManager.CraftNPCRefresh();

			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"),0, 60);


		new BukkitRunnable() {
			@Override
			public void run() {

				ShopNPCManager.getinstance().sendHeadRotationPacket();
				QuestNPCManager.getinstance().sendHeadRotationPacket();
				(new AuctionNPC()).HeadRotation();
				planetDetect.getinstance().detectArea();
				PartyManager.partyObjectiveLoop();



			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 5);

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
	
	public void UnregisterInstance(Player p) {

		//PlayerManager.getinstance(p).removeinstance();
		PlayerManager.getinstance(p).removeinstance();
		UserQuestManager.Singleton().RemoveQuestsInstances(p);
		PlayerHealthShield.getinstance(p).removeinstance();
		PlayerEnergy.getinstance(p).removeinstance();
		EntityHealthBossBar.getinstance(p).removeinstance();
		PlayerCombination.getinstance(p).removeinstance();
		PlayerFunction.getinstance(p).removeinstance();
		PartyManager.getinstance().removeinstance(p);
		DuelManager.getDuelManager(p).setLoser(p);
		
	}
	
	public void ServerJoinToDo(Player p) {

		(new SQL.SQLManager(p)).updateData();
		(new SQL.SQLManager(p)).initData();

		//PlayerStatManager.getinstance(p);
		PlayerManager.getinstance(p);
		LeavingWhileQuestAndJoinAgain leavingwhilequestandjoinagain = new LeavingWhileQuestAndJoinAgain();
		leavingwhilequestandjoinagain.restore(p); // 튜토리얼 도중 포기 감지

		this.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
			//p.setResourcePack("https://www.dropbox.com/s/yavjepjt7q273mq/%EC%84%9C%EB%B2%84%ED%85%8D%EC%8A%A4%EC%B3%90.zip?dl=1");
			ShopNPCManager.getinstance().addJoinPacket(p);
			QuestNPCManager.getinstance().addJoinPacket(p);
			(new AuctionNPC()).Show(p);
			EntityManager.showEntityPlayerNPC(p);
		}, 40); // npc 소환
		
		PacketReader reader = new PacketReader(p);
		reader.inject(p); // npc 우클릭 감지 등록
		
		PlayerAlarmManager.instance().register(p); // 유저 알람 파일 등록

		PlayerClass pC = new PlayerClass(p);
		pC.classCall(pC.getPreviousClass());
		(new Auction()).noticeYourItemsWereSoldWhenJoin(p);
		Aether.getinstance().PassiveEffect();
	}
}
