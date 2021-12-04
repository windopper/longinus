package Packets;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PlayerConnection;

public class loop {
	
	public static void packetloop() {
		
		TrainerArrowInVisible();
				
		
		
	}
	
	
	
	public static void TrainerArrowInVisible() {
		
		Location loc = new Location(Bukkit.getWorld("world"), -17, 63, 156);
		
		for(Entity e : Bukkit.getServer().getWorld("world").getEntities()) {
			
			if(e.getCustomName() == null) continue;
			
			if(e.getLocation().distance(loc)<60 && e instanceof Arrow) {
				
				String split[] = e.getCustomName().split(":");
				
				
				for(Player pl : Bukkit.getOnlinePlayers()) {
					
					if(!split[1].equals(pl.getName())) {
						CraftPlayer player = (CraftPlayer) pl;
						PlayerConnection conn = player.getHandle().playerConnection;
						
						conn.sendPacket(new PacketPlayOutEntityDestroy(e.getEntityId()));
					}

				}
				
			}
			
		}
			

	}
}
