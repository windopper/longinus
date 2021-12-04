package Hologram;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class loop {
	
//	static File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "hologram.yml");
//	static FileConfiguration hologram = YamlConfiguration.loadConfiguration(file);
//	static ConfigurationSection section= hologram.getConfigurationSection("Holograms");
	
	public void loop() {
		
		tutorialfinish1();
		tutorialfinish2();
	}
	
	public void tutorialfinish1() {
		
		String name = "§c재시험 보러가기§c";
		World world = Bukkit.getWorld("world");
		Location loc = new Location(world, -70.5, 52, 89);
		
		armorstand(name, world, loc);
		
	}
	public void tutorialfinish2() {
		
		String name = "§a클래스 선택하러 가기§a";
		World world = Bukkit.getWorld("world");
		Location loc = new Location(world, -66.5, 52, 86.5);
		
		armorstand(name, world, loc);
		
	}
	
	
	
	
	public void armorstand(String name, World world, Location loc) {
		
		for(Entity e : Bukkit.getWorld("world").getEntities()) {
			if(e.getCustomName() != null) {
				if(e.getCustomName().equals(name)) return;
			}
			
		}
		
		int i=0;
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getWorld() == loc.getWorld()) {
				if(loc.distance(p.getLocation())<100) {
					i=1;
					break;
				}
			}

		}
		if(i==0) return;
		
		
		ArmorStand ar = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);
		ar.setVisible(true);
		ar.setGravity(false);
		ar.setSilent(true);
		ar.setCollidable(false);
		ar.setCustomName(name);
		ar.setCustomNameVisible(true);
		ar.setInvisible(true);
		ar.setInvulnerable(true);
	}
}
