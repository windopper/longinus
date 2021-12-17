package PlayerManager;

import Mob.EntityManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class EntityHealthBossBar {
	
	static final BarFlag flag = BarFlag.PLAY_BOSS_MUSIC;
	static final BarStyle style = org.bukkit.boss.BarStyle.SEGMENTED_10;
	static final BarColor color = org.bukkit.boss.BarColor.RED;
	
	public static HashMap<Player, EntityHealthBossBar> instance = new HashMap<>();
	
	private Player p;
	private BossBar BossBarCurrentShow;
	private LivingEntity BossBarCurrentEntity;
	private int PreviousEntityHealth = 0;
	private int BossBarShowTime = 0;
	
	
	private EntityHealthBossBar(Player p) {
		this.p = p;
	}
	
	public static EntityHealthBossBar getinstance(Player p) {
		if(!instance.containsKey(p)) instance.put(p, new EntityHealthBossBar(p));
		return instance.get(p);
	}

	public void removeinstance() {
		instance.remove(p);
	}
	
	public void EntityShowHealthBossbar(final Player p, final LivingEntity entity) {
		
		int CurrentHealth = EntityManager.getinstance(entity).getCurrentHealth();
		int MaxHealth = EntityManager.getinstance(entity).getMaxHealth();

		BossBar bar;
		
		if(entity.getCustomName() == null) return;

		ArmorStand ar = EntityManager.getinstance(entity).getNameArmorStand();
		if(ar == null) return;
		bar = Bukkit.createBossBar(EntityManager.getinstance(entity).getCustomName() +" §c♥ "+CurrentHealth+"/"+MaxHealth, color, style, flag);

		if((double)CurrentHealth /(double)MaxHealth >= 0 && (double)CurrentHealth /(double)MaxHealth <= 1) {
			bar.setProgress((double)CurrentHealth /(double)MaxHealth);
		}
		else if((double)CurrentHealth /(double)MaxHealth >= 1){
			bar.setProgress(1);
		}

		if(BossBarCurrentShow == null || BossBarShowTime == 0 || BossBarCurrentEntity == null) { // 현재 보여주는게 없으면


			if(entity.getCustomName() != null) { //엔티티 이름이 있으면

				BossBarCurrentShow = bar;  // 보스바 추가
				BossBarCurrentEntity = entity; // 엔티티 추가

				bar.addPlayer(p); // 보스바 보여줌

				BossBarShowTime = 1; // 시간 설정
				return;
			}

		}
		else { // 보여주는 게 있으면
			
			if(EntityManager.getinstance(BossBarCurrentEntity).getMaxHealth() < MaxHealth) { // 지금 들어온 엔티티가 최대체력이 더 많다면
				
				BossBarCurrentShow.removePlayer(p);
				BossBarCurrentShow = bar;
				BossBarCurrentEntity = entity; // 엔티티 교체
				BossBarShowTime = 1; // 시간 초기화
				bar.addPlayer(p);
				return;
				
			}
			else {
				BossBarShowTime = 1; // 시간 초기화
				return;
				
			}
				
		}
		bar.removeAll();
		return;
	
	}


	public void healthBarLoop() {
		
		
		
		if(BossBarCurrentShow != null && BossBarCurrentEntity != null && BossBarShowTime != 0) { // 보스바가 떠 있다면
			
			int CurrentHealth = EntityManager.getinstance(BossBarCurrentEntity).getCurrentHealth();
			int MaxHealth = EntityManager.getinstance(BossBarCurrentEntity).getMaxHealth();
			
			if(CurrentHealth <= 0 || !EntityManager.checkinstance(BossBarCurrentEntity)) { // 엔티티가 없으면
				BossBarCurrentShow.removeAll();
				BossBarCurrentShow = null;
				BossBarCurrentEntity = null;
				BossBarShowTime = 0;
				return;
			}
			
			
			BossBarCurrentShow.setTitle(EntityManager.getinstance(BossBarCurrentEntity).getCustomName()
					+" §c♥ "+CurrentHealth+"/"+MaxHealth);
			
			if((double)CurrentHealth/(double)MaxHealth>1){
				BossBarCurrentShow.setProgress(1);
			}
			else if((double)CurrentHealth/(double)MaxHealth<=1 && (double)CurrentHealth/(double)MaxHealth>=0) {
				BossBarCurrentShow.setProgress((double)CurrentHealth/(double)MaxHealth);
			}
			else {
				BossBarCurrentShow.setProgress(0);
			}
			
			
			if(BossBarShowTime>60) { // 3초 지나면
				BossBarCurrentShow.removeAll();
				BossBarCurrentShow = null;
				BossBarCurrentEntity = null;
				BossBarShowTime = 0;
			}
			
			
			// 보스바 타이틀 재 설정
			BossBarShowTime++;
			
		}
			


	
	}

}
