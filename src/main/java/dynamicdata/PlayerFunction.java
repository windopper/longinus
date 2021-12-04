package dynamicdata;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mojang.datafixers.FunctionType.Instance;

public class PlayerFunction {
	
	private static final HashMap<Player, PlayerFunction> instance = new HashMap<>();
	
	private Player p;
	private int melee = 0;
	private int meleedelay = 0;
	private int meleemode = 0;
	
	
	
	private PlayerFunction(Player p) {
		this.p = p;
	}
	
	public static PlayerFunction getinstance(Player p) {
		if(!instance.containsKey(p)) instance.put(p, new PlayerFunction(p));
		return instance.get(p);
	}
	
	public void removeinstance() {
		instance.remove(p);
	}
	
	public int getMelee() {
		return melee;
	}
	public void increaseMelee() {
		this.melee ++;
	}
	public void setMeleeDelay(int meleedelay) {
		this.melee = 1;
		this.meleedelay = meleedelay;
	}
	public void setMelee(int melee) {
		this.melee = melee;
	}
	public int getMeleemode() {
		return meleemode;
	}
	public void setMeleemode(int meleemode) {
		this.meleemode = meleemode;
	}
	
	
	public void MeleeDelayControlLoop() {
		
		if(meleedelay == melee) setMelee(0);
		if(melee != 0) melee++;
		
	}

}
