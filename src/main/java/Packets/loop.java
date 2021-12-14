package Packets;

import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
						PlayerConnection conn = player.getHandle().b;
						
						conn.sendPacket(new PacketPlayOutEntityDestroy(e.getEntityId()));
					}

				}
				
			}
			
		}
			

	}
}
