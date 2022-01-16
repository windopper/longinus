package Watchers.ArrowWatcher;

import CustomEvents.ArrowFlyingEvent;
import CustomEvents.ArrowOnGroundEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;

public class ArrowWatcher {

	public static void ArrowWatcher() {
		for(World worlds : Bukkit.getWorlds()) {
			for(Entity entity : Bukkit.getWorld(worlds.getName()).getEntities()) {
				if(entity instanceof Arrow) {				
					Arrow ar = (Arrow) entity;
					if(ar.isOnGround()) {
						ar.remove();
						if(ar.getCustomName() == null) continue;

						Bukkit.getPluginManager().callEvent(new ArrowOnGroundEvent(ar, ar.getCustomName()));
					}
					else {
						if(ar.getCustomName() == null) continue;

						Bukkit.getPluginManager().callEvent(new ArrowFlyingEvent(ar, ar.getCustomName()));
					}
				}
			}
		}
	}
}
