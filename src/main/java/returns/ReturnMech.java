package returns;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ReturnMech implements Listener {

	static final BarFlag flag = BarFlag.PLAY_BOSS_MUSIC;
	static final BarStyle style = org.bukkit.boss.BarStyle.SOLID;
	static final BarColor color = BarColor.BLUE;
	
	public static final HashMap<Player, Integer> Return = new HashMap<>();
	
	private final Location longinuspoint = new Location(Bukkit.getWorld("world"), -17.5, 51.5, -26.5, 180, 0);
	private final Location gliese581cpoint = new Location(Bukkit.getWorld("gliese581c"), -197.5, 124, 237.5, -90, 0);
	
	private static ReturnMech ReturnEffects;
	
	private ReturnMech() {
		
	}
	
	public static ReturnMech getinstance() {
		if(ReturnEffects == null) ReturnEffects = new ReturnMech();
		return ReturnEffects;
	}
	
	public void ReturnSequence(Player p) {

		if(Return.containsKey(p)) {
			return;
		}

		final BossBar bar = Bukkit.createBossBar("귀환중.", color, style, flag);
		bar.addPlayer(p);
		bar.setVisible(true);
		final Location originalLoc = p.getLocation();
		
		new BukkitRunnable() {
			
			@Override
			public void run() {

				Location afterLoc = p.getLocation();
				double dist = afterLoc.distance(originalLoc);
				if(!Return.containsKey(p)) Return.put(p, 0);
				else {

					String ReturnSec = String.format("§b%.1f", (140-(double)Return.get(p))/20);
					
					if(Return.get(p) % 13 >= 0 && Return.get(p) % 13 < 4) {
						bar.setTitle("§a귀환 중.");
						p.sendTitle("§a귀환 중.", ReturnSec, 0, 20, 10);
					}
					if(Return.get(p) % 13 >= 4 && Return.get(p) % 13 < 8) {
						bar.setTitle("§a귀환 중..");
						p.sendTitle("§a귀환 중..", ReturnSec, 0, 20, 10);
					}
					if(Return.get(p) % 13 >= 8 && Return.get(p) % 13 < 13) {
						bar.setTitle("§a귀환 중...");
						p.sendTitle("§a귀환 중...", ReturnSec, 0, 20, 10);
					}

					if(Return.get(p) == 140) {
						bar.setTitle("§a귀환 완료!");
						p.sendTitle("§a귀환 완료!", "", 0, 20, 10);
						bar.removeAll();
						TeleportPointChecker(p);
						p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
						Return.remove(p);
						cancel();
						return;
					}

					if(Return.get(p) == 60) {
						p.playSound(p.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, 1, 1);
					}
					
					if(Return.get(p)>200 || p.isSprinting() || dist>2) {
						p.sendTitle("§c귀환 취소됨", "", 0, 40, 20);
						bar.removeAll();
						Return.remove(p);
						cancel();
						return;
					}

					if(Return.get(p)<140) bar.setProgress((140-(double)Return.get(p))/140);
					if(Return.get(p)>=140) bar.setProgress(0);

					Return.replace(p, Return.get(p)+1);
				}
				
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
	}

	public void ReturnCancel(Player p) {
		if(Return.containsKey(p)) {
			Return.replace(p, 201);
		}
	}


	
	public void TeleportPointChecker(Player p) {
		
		if(p.getWorld().getName().equals("gliese581c")) {
			p.teleport(gliese581cpoint);
		}
		
	}

}
