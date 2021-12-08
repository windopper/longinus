package UserData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.bukkit.entity.Player;

import spellinteracttest.Centermsg;

public class SeveralMessages {
	
	public static void ClassChangeMessage(Player p) {
		
		String pattern = "HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.KOREA);
		String date = simpleDateFormat.format(new Date());
		String split[] = date.split(":");
		String hour = split[0];
		String minute = split[1];
		
		String world = p.getWorld().getName();
		if(world.equals("world")) {
			world = "Longinus";
		}
		
		Centermsg.CenteredMessage(p, "");
		Centermsg.CenteredMessage(p, "");
		Centermsg.CenteredMessage(p, "§e§l§o"+UserManager.getinstance(p).CurrentClass+"로 접속하였습니다!");
		Centermsg.CenteredMessage(p, "");
		Centermsg.CenteredMessage(p, "§7현재 위치 §9§l"+world+"   §7현재 시각 §9§l"+hour+"시 "+minute+"분");
		Centermsg.CenteredMessage(p, "");
		Centermsg.CenteredMessage(p, "");
	}

}