package PlayerManager;

import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class PlayerFunction implements Listener {
	
	private static final HashMap<Player, PlayerFunction> instance = new HashMap<>();
	
	private Player p;
	private int melee = 0;
	private int meleedelay = 0;
	private int meleemode = 0;

	/*
	Accelerator Functions
	 */
	public int ACPassiveCoolDown = 0;
	public double ACRate = 1;
	/*
	Phlox Functions
	 */
	public int PHNanoRobot = -1;
	public ShulkerBullet PHMeleeRobot;
	public int PHMeleeRobotCount = 0;

	/*
	Aether Functions
	 */
	public double AEImpulse = 0;

	/*
	ByV Functions
	 */
	public boolean takedown = false;
	public int essence = 0;

	private PlayerFunction(Player p) {
		this.p = p;
	}
	
	public static PlayerFunction getinstance(Player p) {
		if(!instance.containsKey(p)) instance.put(p, new PlayerFunction(p));
		return instance.get(p);
	}

	public void resetFunctions() {
		instance.remove(p);
		instance.put(p, new PlayerFunction(p));
		return;
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
