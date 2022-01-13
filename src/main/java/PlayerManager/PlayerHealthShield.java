package PlayerManager;

import CustomEvents.PlayerDeathEvent;
import DynamicData.Damage;
import DynamicData.HologramIndicator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import spellinteracttest.Main;

import java.awt.*;
import java.util.HashMap;

public class PlayerHealthShield {
	
	private int shieldregentime = 8;
	private int blastershieldregentime = 4;
	
	public static final HashMap<Player, PlayerHealthShield> instance = new HashMap<>();
	
	private final Player player;
	
	private int ShieldRegenerateStop = 0;
	private int ShieldRegenerateCooldown = 0;
	private int CurrentShield;
	private int CurrentHealth;
	private double immortality = 0;

	static int i=0;
	
	private PlayerHealthShield(Player player) {
		this.player = player;
		CurrentShield = PlayerManager.getinstance(player).MaxShield;
		CurrentHealth = PlayerManager.getinstance(player).Health;
	}
	
	public static PlayerHealthShield getinstance(Player p) {
		if(!instance.containsKey(p)) instance.put(p, new PlayerHealthShield(p));
		return instance.get(p);
	}
	
	public void removeinstance() {
		instance.remove(player);
	}
	
	public int getShieldRegenerateCooldown() {
		return ShieldRegenerateCooldown;
	}
	
	public int getShieldRegenerateStop() {
		return ShieldRegenerateStop;
	}
	
	public int getCurrentHealth() {
		return CurrentHealth;
	}
	
	public int getCurrentShield() {
		return CurrentShield;
	}
	
	public void setShieldRegenerateStop() {
		ShieldRegenerateStop = 1;
	}
	
	public void setCurrentShield(int currentShield) {
		CurrentShield = currentShield;
	}
	
	public void setCurrentHealth(int currentHealth) {
		CurrentHealth = currentHealth;
	}
	
	public void setShieldRegenerateCooldown(int shieldRegenerateCooldown) {
		ShieldRegenerateCooldown = shieldRegenerateCooldown;
	}

	public void setImmortality(double healthRate, int tick) {
		this.immortality = healthRate;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
			this.immortality = 0;
		}, tick);
	}

	public void HealthAdd(int addhealth, Player giver) {
		if(CurrentHealth > 0) {
			if(CurrentHealth + addhealth > PlayerManager.getinstance(player).Health) {
				CurrentHealth = PlayerManager.getinstance(player).Health;

				HologramIndicator.getinstance().HealIndicator(addhealth, player.getLocation());
				return;
			}
			if(giver != player)
				player.sendMessage("§e"+giver.getName()+" §6§l♥§r§6 "+addhealth+"§e 치유");
			CurrentHealth += addhealth;
			HologramIndicator.getinstance().HealIndicator(addhealth, player.getLocation());
		}
	}

	public void ShieldAdd(int addshield, Player giver) {
		if(CurrentHealth > 0) {
			CurrentShield += addshield;
			if(giver != player)
				player.sendMessage("§d"+giver.getName()+" §d§5§l🛡§l§5§r §5"+addshield+"§5§d 부여§d");
		}
	}
	public void ShieldAdd(double rate, Player giver) {
		ShieldAdd((int)(PlayerManager.getinstance(player).MaxShield * rate), giver);
	}

	public void setDamage(int damage) {

		player.damage(0.01d);

		Player AEtIV2Player = PlayerFunction.getinstance(player).getNearbyAERLtIV2Player();
		if(AEtIV2Player != null) {
			damage = (int)((double) damage / 2);
			Damage.getinstance().taken(damage, AEtIV2Player, player);
		}


		if(getShieldRegenerateStop()==0) //피해 받으면 보호막 재생이 멈춤
			setShieldRegenerateStop();

		// 쉴드가 있을때
		if(getCurrentShield() > 0) {
			if(getCurrentShield()-damage <= 0) { //쉴드가 깨짐
				setCurrentShield(0);
				setShieldRegenerateCooldown(0);
				player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation(), 50, 0.5, 0.5, 0.5, Material.PURPLE_GLAZED_TERRACOTTA.createBlockData());
				HologramIndicator.getinstance().ShieldBroken(player);
				PlayerEffectEvent.getInstance().ShieldBrokenEffect(player);
			}
			else {
				setCurrentShield(getCurrentShield()-damage);
			}
		}
		// 쉴드가 없을때
		else {
			PlayerManager pm = PlayerManager.getinstance(player);

			if(immortality != 0) {
				if(getCurrentHealth() - damage <= (double)pm.Health * immortality) {
					setCurrentHealth((int)((double)pm.Health * immortality));
					HologramIndicator.getinstance().Indicator(ChatColor.of("#87CEFA")+"피해무시!", player.getLocation(), 30);
				}
				else {
					setCurrentHealth(getCurrentHealth() - damage);
				}
				return;
			}

			if(getCurrentHealth() - damage>0) {
				setCurrentHealth(getCurrentHealth() - damage);
			}
			else {
				setCurrentHealth(0);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void HealthWatcher() {
		
		final int MaxHealth = PlayerManager.getinstance(player).Health;
		
		String CurrentClass = PlayerManager.getinstance(player).CurrentClass;
		double Heart = player.getMaxHealth() * ((double)CurrentHealth/MaxHealth);


		if(CurrentShield>0) { // 쉴드
			player.setAbsorptionAmount((double)CurrentShield/100);
		}
		else if(CurrentShield==0) {
			player.setAbsorptionAmount(0);
		}

		if(CurrentHealth > MaxHealth) {
			CurrentHealth = MaxHealth;
		}

		if(Heart > player.getMaxHealth()) { // 체력은 20이상 할 수 없다
			Heart = player.getMaxHealth();
		}

		if(Heart > 0) {
			player.setHealth(Heart);
		}
		else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(new PlayerDeathEvent(player));
				}
			}, 0);
		}
	}
	
	public void ShieldRegeneration() {
		
		final int MaxShield = PlayerManager.getinstance(player).MaxShield;
		String CurrentClass = PlayerManager.getinstance(player).CurrentClass;
		
		if(CurrentShield > MaxShield) { // 현재 보호막이 최대를 넘을때
			
			if(CurrentShield-(int)(CurrentShield/400) < MaxShield) { // 다음에 줄어들 보호막이 최대보다 작으면 최대로 고정
				CurrentShield = MaxShield;
			}
			else {
				CurrentShield = CurrentShield-(int)(CurrentShield/400); // 최대보다 크면 줄어듬
			}

			ShieldRegenerateCooldown = 0; // 보호막 쿨다운 제거
			return;
		}
		
		if(CurrentShield < MaxShield) { // 현재 보호막이 쉴드 최대보다 작을때
			if(ShieldRegenerateCooldown == 0) { // 쉴드 재생 쿨다운이 없을때
				ShieldRegenerateCooldown = 1; // 0초부터 시작
			}
			if(ShieldRegenerateCooldown < 400) { // 쉴드 재생 쿨다운이 400이하 일때
				
				if(ShieldRegenerateCooldown > blastershieldregentime * 20 && CurrentClass.equals("블래스터")) { // 블래스터 보호막 재생
					if((int)(MaxShield/200)+CurrentShield > MaxShield) // 다음에 추가할 보호막이 최대를 넘으면 최대로 고정
						CurrentShield = MaxShield;
					else
						CurrentShield = (int)(MaxShield/200)+CurrentShield; // 재생
					
				}	
				else if(ShieldRegenerateCooldown > shieldregentime * 20) { // 8초이후 6분의 1씩 재생 
					if((int)(MaxShield/200)+CurrentShield > MaxShield) // 다음에 추가할 보호막이 최대를 넘으면 최대로 고정
						CurrentShield = MaxShield;
					else
						CurrentShield = (int)(MaxShield/200)+CurrentShield; // 재생
					
				}
				
				if(CurrentShield >= MaxShield) { // 재생 쿨다운 중일때 현재 쉴드가 최대쉴드를 넘으면
					ShieldRegenerateCooldown = 0; // 초기화
					ShieldRegenerateStop = 0;

				}

				
				ShieldRegenerateCooldown++;
			}
			if(ShieldRegenerateStop == 1) { //피해 받으면 쿨다운 초기화
				
				ShieldRegenerateCooldown = 0;
				ShieldRegenerateStop = 0;
				return;
			}
									

		}
	}


}
