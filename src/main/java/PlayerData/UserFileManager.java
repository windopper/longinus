package PlayerData;

import Mob.MobListManager;
import QuestFunctions.QuestList;
import PlayerChip.Maingui;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class UserFileManager {
	
	private static UserFileManager UserFile;
	
	final static String[] data = {".str", ".dex", ".def", ".agi", ".lvl", ".exp", ".coord"};
	private final static String[] Storage = {
			"0","1","2","3","4","5","6",
			"9","10","11","12","13","14","15",
			"18","19","20","21","22","23","24",
			"27","28","29","30","31","32","33",
			"36","37","38","39","40","41","42",
			"45","46","47","48","49","50","51"};

	
	private UserFileManager() {
		
	}
	
	public static UserFileManager getinstance() {
		if(UserFile == null) UserFile = new UserFileManager();
		return UserFile;
	}
	
	public String[] getStorageSlot() {
		return Storage;
	}
	
	
	public void joinedplayerlistregister(Player p) {
		
		String uuid = p.getUniqueId().toString();
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "joinedplayer.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if(!file.exists()) {
			
			try {
				file.createNewFile();
			}
			catch(IOException e) {
			}
			
		}
		
		config.set("Player."+uuid, p.getName());
		
		try {
			config.save(file);
		}
		catch(Exception e) {
			
		}
	}
	
	
	
	public int getClassLevel(Player p, String classname) {
		
		String uuid = p.getUniqueId().toString();
		String classaddress = "Class."+classname;
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		if(!(config.contains(classaddress+".lvl"))) return 1;
		
		return config.getInt(classaddress+".lvl");
	}
	
	public int getGold(Player p) {
		
		String uuid = p.getUniqueId().toString();
		String address = "Gold";
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		return config.getInt(address);
	}
	
	public boolean setGold(Player p, int amount) {
		
		String uuid = p.getUniqueId().toString();
		String address = "Gold";
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		config.set(address, amount);
		try {
			config.save(file);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return true;
		
	}
	
	public void addStorage(Player p) {
		
		String uuid = p.getUniqueId().toString();
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		for(int i=1; i<=20; i++) {
			if(!config.contains("storage.storage"+Integer.toString(i))) {
				for(int j=0; j<Storage.length; j++) {
					config.set("storage.storage"+Integer.toString(i)+"."+Storage[j], 0);
				}
				
				try {
					config.save(file);
				}
				catch(Exception e) {
					e.printStackTrace();
				}

				break;

			}
		}
	}

	public String getPreviousClass(Player p) {
		
		String uuid = p.getUniqueId().toString();
		String classaddress = "PreviousClass";
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		return config.getString(classaddress);
	}
	
	public Set<String> getClasses(Player p) {
		
		String uuid = p.getUniqueId().toString();
		String classaddress = "Class";
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		ConfigurationSection classes = config.getConfigurationSection(classaddress);
		
		if(classes == null) return null;
		
		return classes.getKeys(false);

	}
	
	public int getClassesAmount(Player p) {
		
		int amount = 0;
		
		if(getClasses(p) == null) return 0;
		
		for(String Class : getClasses(p)) {
			amount ++;
		}
		
		return amount;
	}
	
	
	
	
	
	public void UserDetailRegister(final Player p) {
		
		/*
		 * 이펙트 0일때 없음
		 * 1일때 소유하고 있음
		 * 2일때 현재 사용중
		 * 
		 * 
		 * 
		 */
		
		String uuid = p.getUniqueId().toString();
		String username = p.getName();
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if(!file.exists()) {
			
			try {
				file.createNewFile();
				p.sendMessage("§a데이터를 성공적으로 저장하였습니다");
			}
			catch(IOException e) {
				p.sendMessage("§c데이터를 저장하지 못하였습니다");
			}
			
		}
		
		config.set("Name", username);
		if(!config.contains("Name")) config.set("Name", "없음");
		if(!config.contains("Gold")) config.set("Gold", 0);
		if(!config.contains("storage")) {
			for(int i= 0; i<Storage.length; i++) {
				config.set("storage.storage1."+Storage[i], 0);
			}
			for(int i= 0; i<Storage.length; i++) {
				config.set("storage.storage2."+Storage[i], 0);
			}
			for(int i= 0; i<Storage.length; i++) {
				config.set("storage.storage3."+Storage[i], 0);
			}

		}
		if(!config.contains("effects")) {
			config.set("effects.return.default", 1);
		}

		// 클래스
		if(config.contains("Class")) {
			for(String Class : config.getConfigurationSection("Class").getKeys(false)) {
				Arrays.stream(QuestList.values()).forEach(value-> {
					if(!config.contains("Class."+Class+".quests."+value.name()))
						config.set("Class."+Class+".quests."+value.name()+".progress", 0);
				});
			}
		}

		// 샘플
		Arrays.stream(MobListManager.MobList.values()).forEach(value -> {
			if(!config.contains("Sample."+value.getPlanet()+"."+value.name()+".count"))
				config.set("Sample."+value.getPlanet()+"."+value.name()+".count", 0);
		});

		
		
		p.sendMessage("Your data was successfully saved");
		
		try {
			config.save(file);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public String UserDetailClassRegister(final Player p, String classname) {
		
		String uuid = p.getUniqueId().toString();
		String username = p.getName();
		String classpath = "Class."+classname;
		
		if(getClassesAmount(p) == 10) {
			p.sendMessage("§c최대 클래스 생성 제한에 도달했습니다");
			return null;
		}
		
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		for(int i = 1; i<=10; i++) {
			String number = Integer.toString(i);
			if(!config.contains(classpath+"/"+number)) {
				
				
				for(String d : data) {
					if(!config.contains(classpath+"/"+number+"."+d)) config.set(classpath+"/"+number+"."+d,0);
				}
				
				for(int j=0; j<40; j++) { // 인벤토리 저장
					config.set(classpath+"/"+number+".inv."+Integer.toString(j), 0); // 인벤토리 저장
				}
				
				Arrays.stream(QuestList.values()).forEach(value-> {
					config.set(classpath+"/"+number+".quests."+value.name()+".progress", 0);
					Bukkit.broadcastMessage(value.name()+"  "+Integer.toString(value.getLevelReq()));
				});
				
				try {
					config.save(file);
				}
				catch(Exception e){
					e.printStackTrace();
				}

				return classname+"/"+number;
			}
			
		}
		
		
		p.sendMessage("§c최대 클래스 생성 제한에 도달했습니다");
		
		try {
			config.save(file);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return null;

	}
	
	public void UserDetailClassDelete(final Player p, String classname) {
		
		String uuid = p.getUniqueId().toString();
		String username = p.getName();
		String classaddress = "Class."+classname;
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		if(config.contains(classaddress)) {
			config.set(classaddress, null);
			p.sendMessage("§c성공적으로 클래스를 삭제하였습니다");
		}
		
		
		try {
			config.save(file);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void UserDetailClassCallData(final Player p, String classname) {
		
		if(classname == null) return;
		if(classname.equals("없음")) return;
		
		String uuid = p.getUniqueId().toString();
		String username = p.getName();
		String classaddress = "Class."+classname;

		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		if(!config.contains(classaddress)) {
			p.sendMessage("§c파일 오류로 선택한 클래스의 정보를 불러오지 못하였습니다");
		}
		
		p.updateInventory();
		p.getInventory().clear();
		p.updateInventory();
		
		
		UserManager.getinstance(p).CurrentClass = classname.split("/")[0];
		UserManager.getinstance(p).CurrentClassNumber = Integer.parseInt(classname.split("/")[1]);
		UserStatManager.getinstance(p).setStr(config.getInt(classaddress+".str"));
		UserStatManager.getinstance(p).setDex(config.getInt(classaddress+".dex"));
		UserStatManager.getinstance(p).setDef(config.getInt(classaddress+".def"));
		UserStatManager.getinstance(p).setAgi(config.getInt(classaddress+".agi"));
		UserStatManager.getinstance(p).setlvl(config.getInt(classaddress+".lvl") <= 0 ? 1 : config.getInt(classaddress+".lvl"));
		UserStatManager.getinstance(p).setexp(config.getInt(classaddress+".exp"));
		
		Classlocation.getinstance().classchangeteleport(p, classname); // 텔레포트
		
		
		ItemStack air = new ItemStack(org.bukkit.Material.AIR, 1);
		ItemStack[] items = new ItemStack[40];	
		Inventory inv = p.getInventory();
		Arrays.fill(items, air);
		
		for(int i=0; i<40; i++) {
			
			if(config.getItemStack(classaddress+".inv."+Integer.toString(i)) == null){
				items[i] = air;
			}
			else {
				items[i] = config.getItemStack(classaddress+".inv."+Integer.toString(i));	
			}

		}
		
		inv.setContents(items);
		
		p.getInventory().setItem(8, Maingui.getinstance().chipitemget(p));
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				p.updateInventory();
				cancel();
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 1, 1);
		
		try {
			config.save(file);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void UserDetailClassDataSave(final Player p) {
		
		if(UserManager.getinstance(p).CurrentClass.equals("없음")) return;
		
		String uuid = p.getUniqueId().toString();
		String username = p.getName();
		String classaddress = "Class."+UserManager.getinstance(p).CurrentClass+"/"
		+Integer.toString(UserManager.getinstance(p).CurrentClassNumber);

		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		config.set(classaddress+".str", UserStatManager.getinstance(p).getStr());
		config.set(classaddress+".dex", UserStatManager.getinstance(p).getDex());
		config.set(classaddress+".def", UserStatManager.getinstance(p).getDef());
		config.set(classaddress+".agi", UserStatManager.getinstance(p).getAgi());
		config.set(classaddress+".lvl", UserStatManager.getinstance(p).getlvl());
		config.set(classaddress+".exp", UserStatManager.getinstance(p).getexp());
		config.set(classaddress+".coord", Classlocation.getinstance().coordtostring(p));
		config.set("PreviousClass", UserManager.getinstance(p).CurrentClass+"/"
		+Integer.toString(UserManager.getinstance(p).CurrentClassNumber));
	
		for(int i=0; i<41; i++) {
			
			if(p.getInventory().getItem(i) != null) { // 인벤토리가 null가 아닌 상태면
				if(p.getInventory().getItem(i).getType() == org.bukkit.Material.AIR) { // 타입이 공기라면
					config.set(classaddress+".inv."+Integer.toString(i),0); // 0으로 변경
				}
				else {
					config.set(classaddress+".inv."+Integer.toString(i),p.getInventory().getItem(i));
				}
			}
			else {
				config.set(classaddress+".inv."+Integer.toString(i),0);
			}
		}
		
		try {
			config.save(file);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		p.sendMessage("Your data was successfully saved");
	
	}
	
	
	
	
	
}
