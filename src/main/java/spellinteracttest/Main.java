package spellinteracttest;

import Auction.Auction;
import Auction.AuctionNPC;
import ClassAbility.Accelerator;
import ClassAbility.Aether.Aether;
import ClassAbility.Cheiron.CheironArrowEvent;
import ClassAbility.Phlox.Phlox;
import Duel.DuelManager;
import DynamicData.Damage;
import PacketRecord.Recording.PacketRecordCommands;
import Party.PartyFunction;
import utils.GUICancelHandler;
import Mob.Gliese581cMobs.Gliese581cEntitySummon;
import Items.ItemManager;
import Items.ModuleChip.ModuleChips;
import Items.WeaponManager;
import itemtools.Map.Map;
import Mob.EntityManager;
import Mob.EventListener;
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
import PlayerChip.SkillTalent.TalentUI;
import PlayerChip.UserChipEvent;
import PlayerManager.*;
import PlayerManager.EventListener.*;
import QuestFunctions.LeavingWhileQuestAndJoinAgain;
import QuestFunctions.QuestNPCManager;
import QuestFunctions.UserQuestManager;
import ReturnToBase.ReturnMech;
import SQL.*;
import Shop.RightClickNPC;
import Shop.ShopNPCManager;
import SpyGlass.SpyGlassEvent;
import SpyGlass.SpyGlassItemManager;
import UserStorage.Event;
import Watchers.ArrowWatcher.ArrowWatcher;
import itemtools.FlashLight.FlashLightListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatComponentText;
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
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
import java.util.*;

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
		getServer().getPluginManager().registerEvents(new PlayerArrowPassable(), this);
		getServer().getPluginManager().registerEvents(new PlayerClassChange(), this);
		getServer().getPluginManager().registerEvents(new PlayerDeathAndRespawn(), this);
		getServer().getPluginManager().registerEvents(new PlayerDodge(), this);
		getServer().getPluginManager().registerEvents(new PlayerTakeDamage(), this);

		getServer().getPluginManager().registerEvents(ReturnMech.getinstance(), this);
		getServer().getPluginManager().registerEvents(UserQuestManager.Singleton(), this);
		getServer().getPluginManager().registerEvents(new ItemManager(), this);
		getServer().getPluginManager().registerEvents(new planetSelectEvent(), this);
		getServer().getPluginManager().registerEvents(new EventProcess(), this);
		getServer().getPluginManager().registerEvents(new SpyGlassEvent(), this);
		getServer().getPluginManager().registerEvents(new Gliese581cEntitySummon(), this);
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		//getServer().getPluginManager().registerEvents(new PacketListener(), this);
		getServer().getPluginManager().registerEvents(new EditEventListener(), this);
		getServer().getPluginManager().registerEvents(PacketRecord.Record.getInstance(), this);
		getServer().getPluginManager().registerEvents(new Map(), this);
		getServer().getPluginManager().registerEvents(CheironArrowEvent.getInstance(), this);
		getServer().getPluginManager().registerEvents(new Auction(), this);
		getServer().getPluginManager().registerEvents(new ModuleChips(), this);
		getServer().getPluginManager().registerEvents(new GUICancelHandler(), this);
		getServer().getPluginManager().registerEvents(new TalentUI(), this);

		getServer().getPluginManager().registerEvents(new FlashLightListener(), this);

		getServer().getPluginManager().registerEvents(PlayerPacketHandler.getInstance(), this);


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

					registerInstance(player);
				}
			}

			mainLoop();

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

			Connector.closeConnection();

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

		registerInstance(p);

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

    public static Main getInstance() {
        return instance;
    }
    
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

		Player player = (Player) sender;

		String cmdName = command.getName().toLowerCase();

		(new PacketRecordCommands(player)).commandListener(cmdName, args);

		if(cmdName.equals("duel")) {

			if(args.length == 1) {
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(p.getName().equals(args[0])) {
						DuelManager.getDuelManager(player).sendDuelRequest(player, p);
					}
				}

			}
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
					PartyFunction.getInstance().createParty(player);
					//PartyManager.getinstance().createParty(player);
					break;
				}
				case "invite": {
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(args[1].equals(p.getName())) {
							PartyFunction.getInstance().inviteParty(player, p);
							//PartyManager.getinstance().inviteParty(player, p);
						}
					}
					break;
				}
				case "join": {
					//PartyManager.getinstance().JoinParty(player);
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(args[1].equals(p.getName())) {
							PartyFunction.getInstance().acceptParty(player, p);
						}
					}
					break;
				}
				case "leave": {
					PartyFunction.getInstance().quitParty(player);
					//PartyManager.getinstance().QuitParty(player);
					break;
				}
				case "promote": {
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(args[1].equals(p.getName())) {
							PartyFunction.getInstance().promoteMaster(player, p);
							//PartyManager.getinstance().ChangeMaster(player, p);
						}
					}
					break;
				}
				case "kick": {
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(args[1].equals(p.getName())) {
							PartyFunction.getInstance().kickParty(player, p);
							//PartyManager.getinstance().KickMember(player, p);
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

			case "font": {
				String c = "\ue238\ue239";
				player.sendMessage(""+c+"\ue238");
				break;
			}

			case "damage": {
				int dmg = Integer.parseInt(args[1]);
				Damage.getinstance().taken(dmg, player, player);
				break;
			}

			case "installChip" : {
				ModuleChips moduleChips = new ModuleChips();
				(moduleChips.new install()).openGUI(player);
				break;
			}

			case "getChip": {
				ModuleChips moduleChips = new ModuleChips();
				player.getInventory().addItem(moduleChips.getChip(args[1]));
				break;
			}

			case "body": {
				(new PlayerDeadBodySetter(player)).init();
				break;
			}

			case "exp": {
				PlayerManager.getinstance(player).setexp(Integer.parseInt(args[1]));
				break;
			}

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

			case "summonre": {
				(new Gliese581cEntitySummon()).summonRageEagle(player.getLocation());
				break;
			}

			case "summonmb": {
				(new Gliese581cEntitySummon()).summonMushBone(player.getLocation());
				break;
			}

			case "summonmob": {
				(new Gliese581cEntitySummon()).summonMob(player.getLocation(), args[1]);
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
				PlayerManager.getinstance(player).updatePlayerInfo();
				break;
			}

			case "stats":{
				player.sendMessage("Level:" +Integer.toString(PlayerManager.getinstance(player).getlvl()));
				player.sendMessage("EXP:"+Integer.toString(PlayerManager.getinstance(player).getexp()));
				player.sendMessage("Damage:" + Integer.toString(PlayerManager.getinstance(player).MinDamage)+"-"+Integer.toString(PlayerManager.getinstance(player).MaxDamage));
				player.sendMessage("Health: " + Integer.toString(PlayerManager.getinstance(player).Health));
				player.sendMessage("Shield: " + Integer.toString(PlayerManager.getinstance(player).MaxShield));
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
			case "dungeon": {
				Set<Player> set = new HashSet<>();
				set.add(player);
				Dungeon.Dungeon.init(set, player.getWorld());
				break;
			}
			case "flashlight": {
				player.getInventory().addItem((new FlashLightListener()).getFlashLight());
				break;
			}
			case "optChat" : {

				ArmorStand as = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
				as.setCustomNameVisible(true);
				as.setCustomName("nknknk");


				for(Player p : Bukkit.getOnlinePlayers()) {
					PlayerConnection conn = ((CraftPlayer) p).getHandle().b;
					DataWatcher dataWatcher = ((CraftEntity) as).getHandle().getDataWatcher();
					dataWatcher.set(new DataWatcherObject<>(2, DataWatcherRegistry.f), Optional.of(new ChatComponentText("hi! "+p.getName())));
					conn.sendPacket(new PacketPlayOutEntityMetadata(as.getEntityId(), dataWatcher, true));
				}
				break;
			}
		}
		return true;
	}
	
	public void mainLoop() {

		
		new BukkitRunnable() {
			
			@Override
			public void run() {

				try {

				}
				catch(Exception e) {

				}

				PlayerManager.updateloop();


				for(Entity entity : EntityManager.getEntityManagerEntities()) {
					EntityManager.getinstance(entity).EntityWatcher();
				}

				for(Player p: Bukkit.getOnlinePlayers()) {
					PlayerHealthShield.getinstance(p).HealthWatcher();
					PlayerHealthShield.getinstance(p).ShieldRegeneration();
					PlayerEnergy.getinstance(p).OverloadCoolDown();
					PlayerEnergy.getinstance(p).watchPreviousManaUsed();
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
				
				ArrowWatcher.ArrowWatcher();

				MobMechManager.getInstance().RunGliese581cMobMech();
				SpyGlass.SpyGlassManager.watchSpyGlassEnable();
				//PartyManager.getinstance().partyGlowingLoop();
				DuelManager.duelLoop();
				Map.updateMap();

				PlayerLevelManager.getInstance().expWatcher();
				
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
				mob.mobDelete();
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
		PlayerEnergy.getinstance(p).remove();
		EntityHealthBossBar.getinstance(p).removeinstance();
		PlayerCombination.getinstance(p).removeinstance();
		PlayerFunction.getinstance(p).removeinstance();
		PartyManager.getinstance().removeinstance(p);
//		DuelManager.getDuelManager(p).setLoser(p);
		
	}
	
	public void registerInstance(Player p) {

		(new SQL.SQLManager(p)).updateData();
		(new SQL.SQLManager(p)).initData();

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
