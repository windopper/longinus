package PlayerManager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerEnergy extends PlayerManager {
	
	private static final HashMap<Player, PlayerEnergy> PlayerEnergy = new HashMap<>();
	
	private Player p;
	public ConcurrentHashMap<Player, Double> getUsedManaFromPlayer = new ConcurrentHashMap<>();

	private int Energy = 4;
	private String PreviousSkill = "none";
	private int PreviousManaUsed = 0;
	private int EnergyOverload = 0;
	private int EnergyOverloadCooldown = 0;
	private int EnergyRate = 1;

	
	private PlayerEnergy(Player p) {
		this.p = p;
	}
	
	public static PlayerEnergy getinstance(Player p) {
		if(!PlayerEnergy.containsKey(p)) PlayerEnergy.put(p, new PlayerEnergy(p));
		return PlayerEnergy.get(p);
	}

	public int getEnergy() {
		return Energy;
	}
	public String getPreviousSkill() {
		return PreviousSkill;
	}
	public int getEnergyOverload() {
		return EnergyOverload;
	}
	public int getEnergyOverloadCooldown() {
		return EnergyOverloadCooldown;
	}
	public int getEnergyRate() {
		return EnergyRate;
	}
	
	public void removeEnergy(int energy) {
		Energy -= energy;
	}
	public void useEnergy(int energy) {
		Energy -= energy;
		setPreviousManaUsed(energy);
	}
	public void setEnergy(int energy) {
		Energy = energy;
	}
	public void addEnergy(int energy) {
		if(this.Energy + energy <= 20) this.Energy += energy;
		else this.Energy = 20;
	}

	public void setEnergyOverload(int energyOverload) {
		EnergyOverload = energyOverload;
	}
	public void setEnergyOverloadCooldown(int energyOverloadCooldown) {
		EnergyOverloadCooldown = energyOverloadCooldown;
	}
	public void setEnergyRate(int energyRate) {
		EnergyRate = energyRate;
	}
	public void setPreviousSkill(String previousSkill) {
		PreviousSkill = previousSkill;
	}
	public void setPreviousManaUsed(int manaUsed) { this.PreviousManaUsed = manaUsed; }
	public int getPreviousManaUsed() { return this.PreviousManaUsed; }

	public void Regeneration() {
		
		if(Energy<=20) {
			Energy += PlayerManager.getinstance(p).EnergyPerSecond * EnergyRate;
			p.setFoodLevel((int)Energy);
		}
		if(Energy>20) {
			Energy = 20;
			p.setFoodLevel((int)Energy);
		}
	}
	
	public void OverloadCoolDown() {
		
		if(EnergyOverloadCooldown >= 1) { // 에너지 과부하 쿨다운 활성화 시
			EnergyOverloadCooldown++;// 쿨다운 시작
			
			if(EnergyOverloadCooldown>60) { // 쿨다운 지나면
				EnergyOverloadCooldown = 0;
				EnergyOverload = 0;
				PreviousSkill = "none";
			}
		}
	}

	public void watchPreviousManaUsed() {
		for(Player player : getUsedManaFromPlayer.keySet()) {
			getinstance(player).addEnergy((int) (this.PreviousManaUsed * this.getUsedManaFromPlayer.get(player)));
		}
		getUsedManaFromPlayer.clear();
		PreviousManaUsed = 0;
	}
}
