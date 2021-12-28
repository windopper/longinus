package PlayerManager;

import CustomEvents.PlayerDeathEvent;
import DynamicData.HologramIndicator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import spellinteracttest.Main;

import java.util.HashMap;

public class PlayerHealthShield {
	
	private int shieldregentime = 8;
	private int blastershieldregentime = 4;
	
	public static HashMap<Player, PlayerHealthShield> instance = new HashMap<>();
	
	private Player p;
	
	private int ShieldRegenerateStop = 0;
	private int ShieldRegenerateCooldown = 0;
	private int CurrentShield;
	private int CurrentHealth; 

	static int i=0;
	
	private PlayerHealthShield(Player p) {
		this.p = p;
		CurrentShield = PlayerManager.getinstance(p).ShieldRaw;
		CurrentHealth = PlayerManager.getinstance(p).Health;
	}
	
	public static PlayerHealthShield getinstance(Player p) {
		if(!instance.containsKey(p)) instance.put(p, new PlayerHealthShield(p));
			
		return instance.get(p);
	}
	
	public void removeinstance() {
		instance.remove(p);
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

	public void HealthAdd(int addhealth) {

		if(CurrentHealth > 0) {
			if(CurrentHealth + addhealth > PlayerManager.getinstance(p).Health) {
				CurrentHealth = PlayerManager.getinstance(p).Health;

				HologramIndicator.getinstance().HealIndicator(addhealth, p.getLocation());
				return;
			}

			CurrentHealth += addhealth;
			HologramIndicator.getinstance().HealIndicator(addhealth, p.getLocation());
		}
	}

	public void ShieldAdd(int addshield) {
		if(CurrentHealth > 0) {
			CurrentShield += addshield;
		}
	}

	public void setDamage(int damage) {

		p.damage(0.0001);

		if(getShieldRegenerateStop()==0) //피해 받으면 보호막 재생이 멈춤
			setShieldRegenerateStop();

		// 쉴드가 있을때
		if(getCurrentShield() > 0) {
			if(getCurrentShield()-damage <= 0) { //쉴드가 깨짐
				setCurrentShield(0);
				setShieldRegenerateCooldown(0);
				p.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getLocation(), 50, 0.5, 0.5, 0.5, Material.PURPLE_GLAZED_TERRACOTTA.createBlockData());
				HologramIndicator.getinstance().ShieldBroken(p);
				PlayerEffectEvent.getInstance().ShieldBrokenEffect(p);

			}
			else {
				setCurrentShield(getCurrentShield()-damage);
			}

		}
		// 쉴드가 없을때
		else {
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
		
		final int MaxHealth = PlayerManager.getinstance(p).Health;
		
		String CurrentClass = PlayerManager.getinstance(p).CurrentClass;
		double Heart = p.getMaxHealth() * ((double)CurrentHealth/MaxHealth);
		
		if(CurrentHealth > MaxHealth) {
			CurrentHealth = MaxHealth;
		}

		if(Heart > p.getMaxHealth()) { // 체력은 20이상 할 수 없다
			Heart = p.getMaxHealth();
		}

		if(Heart > 0) {
			p.setHealth(Heart);
		}
		else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(new PlayerDeathEvent(p));
				}
			}, 0);
		}

		if(CurrentShield>0) { // 쉴드
			p.setAbsorptionAmount(CurrentShield/100);
		}
		else if(CurrentShield==0) {
			p.setAbsorptionAmount(0);
		}

	}
	
	public void ShieldRegeneration() {
		
		final int MaxShield = PlayerManager.getinstance(p).ShieldRaw;
		String CurrentClass = PlayerManager.getinstance(p).CurrentClass;
		
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
