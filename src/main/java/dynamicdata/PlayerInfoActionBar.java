package dynamicdata;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ClassAbility.Aether;
import ClassAbility.ByV;
import ClassAbility.Phlox;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import userdata.UserManager;

public class PlayerInfoActionBar {
	
	static int i=0;

	public static void actionbar() {
		
		
		if(i>20) i=0;
		i++;

		
		for(Player player : Bukkit.getOnlinePlayers()) {

			PlayerFunction PF = PlayerFunction.getinstance(player);


			
			int CurrentHealth = PlayerHealth.getinstance(player).getCurrentHealth();
			int CurrentShield = PlayerHealth.getinstance(player).getCurrentShield();
			int MaxShield = UserManager.getinstance(player).ShieldRaw;
			int MaxHealth = UserManager.getinstance(player).Health;
			String CurrentClass = UserManager.getinstance(player).CurrentClass;
			
			// 아이테르 전용
			String m = "§c§l☈ §l§c"+Aether.impulse.get(player)+"/1000 "+"§6§l♥ §l§r§6"+CurrentHealth+"/"+MaxHealth+"§6  §5§l🛡 §l§r§5"+CurrentShield+
					"/"+MaxShield+"§5  §3§l⚡ §l§r§3"+PlayerEnergy.getinstance(player).getEnergy()+"/20§3";
			
			// 플록스 전용
			String f = "§9§l◈ §l§9"+PF.nanorobot+"/100  "+"§6§l♥ §l§r§6"+CurrentHealth+"/"+MaxHealth+"§6  §5§l🛡 §l§r§5"+CurrentShield+
					"/"+MaxShield+"§5  §3§l⚡ §l§r§3"+PlayerEnergy.getinstance(player).getEnergy()+"/20§3";
			
			// 바이V 전용
			String h = "§4§l▲ §l"+ByV.essence.get(player)+"  "+"§6§l♥ §l§r§6"+CurrentHealth+"/"+MaxHealth+"§6  §5§l🛡 §l§r§5"+CurrentShield+
					"/"+MaxShield+"§5  §3§l⚡ §l§r§3"+PlayerEnergy.getinstance(player).getEnergy()+"/20§3";
			
			// 기본
			String n = "§6§l♥ §l§r§6"+CurrentHealth+"/"+MaxHealth+"§6  §5§l🛡 §l§r§5"+CurrentShield+
					"/"+MaxShield+"§5  §3§l⚡ §l§r§3"+PlayerEnergy.getinstance(player).getEnergy()+"/20§3";
			
			
			
			if(PlayerEnergy.getinstance(player).getEnergyRate()>1 && i<=11) {  // 에너지 배수가 있을때 
				n = "§6§l♥ §l§r§6"+CurrentHealth+"/"+MaxHealth+"§6  §5§l🛡 §l§r§5"+CurrentShield+
						"/"+MaxShield+"§5  §3§l⚡ §l§r§3"+PlayerEnergy.getinstance(player).getEnergy()+"/20§3";
			}
			else if(PlayerEnergy.getinstance(player).getEnergyRate()>1 && i>11) {  // 에너지 배수가 있을때
				n = "§6§l♥ §l§r§6"+CurrentHealth+"/"+MaxHealth+"§6  §5§l🛡 §l§r§5"+CurrentShield+
						"/"+MaxShield+"§5  §b§l⚡ §l§r§b"+PlayerEnergy.getinstance(player).getEnergy()+"/20§3";
			}
			
			if(CurrentClass.equals("아이테르")) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(m)); //액션바 코드
			}
			else if(CurrentClass.equals("플록스")) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(f)); //액션바 코드
			}
			else if(CurrentClass.equals("바이V")) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(h)); 
			}
			else {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(n));
			}
		}
				
	}

}
