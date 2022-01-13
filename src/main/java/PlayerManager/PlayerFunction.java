package PlayerManager;

import ClassAbility.entitycheck;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class PlayerFunction implements Listener {
	
	private static final HashMap<Player, PlayerFunction> instance = new HashMap<>();
	
	private Player player;
	private int melee = 0;
	private int meleecombo = 1;
	private int meleecombodelay = 0;
	private int meleedelay = 0;
	private int meleemode = 0;
	private boolean meleerot = false;
	private String meleecommand = "";

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
	public boolean AERLtIV2 = false; // 스킬 사용 후 3초간 주변 아군이 받는 피해의 50%를 대신 받습니다

	/*
	ByV Functions
	 */
	public boolean takedown = false;
	public int essence = 0;

	/*
	Khaos Functions
	 */
	public int KhaosFR = 0;

	private PlayerFunction(Player player) {
		this.player = player;
	}
	
	public static PlayerFunction getinstance(Player p) {
		if(!instance.containsKey(p)) instance.put(p, new PlayerFunction(p));
		return instance.get(p);
	}

	public void resetFunctions() {
		instance.remove(player);
		instance.put(player, new PlayerFunction(player));
		return;
	}

	public void addAEImpulse(double var) {
		if(AEImpulse + var >= 1000) {
			AEImpulse = 1000;
		}
		else AEImpulse += var;
	}
	
	public void removeinstance() {
		instance.remove(player);
	}
	
	public int getMeleeDelay() {
		return meleedelay;
	}
	public void  setMeleeDelay(int meleedelay) {
		this.meleedelay = meleedelay;
		this.meleecombodelay = 15;
	}
	public int getMeleemode() {
		return meleemode;
	}
	public void setMeleemode(int meleemode) {
		this.meleemode = meleemode;
	}
	public void addMeleeCommand(String command) {
		if(command.equals("SHIFTL")) command = "§o§7[SL]";
		else if(command.equals("L")) command = "§o§f[L]";
		else if(command.equals("R")) command = "§o§8[R]";
		this.meleecommand += command + " ";
	}
	public String getMeleecommand() {
		return this.meleecommand;
	}

	public void addMeleeCombo() {
		this.meleecombo++;
	}
	public void setMeleeCombo(int combo) {
		this.meleecombo = combo;
	}
	public void removeMeleeCombo() {
		if(this.meleecombo>0) this.meleecombo--;
	}
	public int getMeleeCombo() {
		return meleecombo;
	}
	public boolean getMeleeRot() { return meleerot; }

	public void PlayerFunctionLoop() {

		if(meleecombodelay == 0 && meleedelay == 0) {
			meleecombo = 1;
		}
		if(meleecombo == 1) meleecommand = "";

		if(meleedelay == 1) this.meleerot = !meleerot;
		if(meleedelay != 0) meleedelay--;
		else if(meleecombodelay != 0) meleecombodelay--;

		if(KhaosFR > 0) KhaosFR--;

	}

	public void removeAllAbnormalStatus() {
		Arrays.stream(PotionEffectType.values()).forEach(player::removePotionEffect);
	}

	public Player getNearbyAERLtIV2Player() {
		Location ploc = player.getLocation();
		double dist = 10;
		Player target = null;
		for(LivingEntity lE : player.getWorld().getLivingEntities()) {
			Location lloc = lE.getLocation();
			if(lE instanceof Player pl && lloc.distance(ploc)<6 && !entitycheck.duelcheck(lE, player) && lE != player) {
				if(!PlayerFunction.getinstance(pl).AERLtIV2) continue;
				if(lloc.distance(ploc) < dist) {
					dist = lloc.distance(ploc);
					target = pl;
				}
			}
		}
		if(dist != 10 && target != null) {
			return target;
		}
		return null;
	}

}
