package QuestClasses;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import DynamicData.EntityStatusManager;

public class Tutorial {
	
	public final static HashMap<Player, Integer> jumpfail = new HashMap<>();
	public final static List<Player> elevatordetect = new ArrayList<>();
	public final static HashMap<Player, Integer> trainerbothit = new HashMap<>();
	public final static HashMap<Player, Integer> examset = new HashMap<>();
	public final static HashMap<Player, int[]> exambothit = new HashMap<>();
	public final static HashMap<Player, Integer> exambothitcount = new HashMap<>();
	
	public final static HashMap<Player, Integer> time = new HashMap<>();
	public final static HashMap<Player, Integer> SCORE = new HashMap<>();
	public final static HashMap<Player, String> GRADE = new HashMap<>();
	
	public static void areadetect() {
		
				for(final Player p : Bukkit.getOnlinePlayers()) {
					
					if(p.getWorld().getName().equals("world")) {
						
						Location ploc = p.getLocation();
						
						Location elevator0 = new Location(Bukkit.getServer().getWorld("world"), -71.5, 25, 152.5); // 엘리베이터 입구
						Location elevator1 = new Location(Bukkit.getServer().getWorld("world"), -71.5, 12, 152.5); // 엘리베이터 입구
						
						
						
						if(ploc.distance(elevator0) < 1.5) {
							
							if(!elevatordetect.contains(p)) {
								
								elevatordetect.add(p);
								
								
								p.playSound(ploc, "meme.tut1", 50, 1);
								
								new BukkitRunnable() {
									int i=0;
									@Override
									public void run() {
										if(i==0) p.sendMessage("§e시험 진행 A.I:§e §f환영합니다. ");
										if(i==20) p.sendMessage("§e시험 진행 A.I:§e §7§6" +p.getName()+" §6§f당신은 롱기누스 170번째 개척자 시험에 참가하였습니다. ");
										if(i==120) p.sendMessage("§e시험 진행 A.I:§e §7§6"+ p.getName()+" §6§f님의 총 응시 횟수는 5회이며 이번 시험에 탈락 시 응시 자격이 박탈되니 이 점 유의하시길 바랍니다.");
										if(i==300) {
											p.sendMessage("§e시험 진행 A.I:§e§7§6 §6§f준비가 되시면 앞에 보이는 문으로 이동해주시길 바랍니다");
											elevatordetect.remove(p);
											cancel();
										}
										
										i++;
									}
								}.runTaskTimerAsynchronously(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
							}

						}
						
						
						
						for(int i=0; i<50; i++) {
							if(elevator1.distance(ploc) < 1.5) {
								p.playSound(ploc, Sound.BLOCK_BEACON_AMBIENT, 1, 1);
								p.setVelocity(new org.bukkit.util.Vector(0, 0.1, 0));
							}
							elevator1.add(0, 1, 0);			
						}
						
						
						
						

						
						
						Location elevator3 = new Location(Bukkit.getServer().getWorld("world"), -72, 53, 152);
		
						if(ploc.distance(elevator3) < 2) {  // 엘베 타고 텔포
							float yaw = p.getEyeLocation().getYaw();
							float pitch = p.getEyeLocation().getPitch();
							Location tutorial1 = new Location(Bukkit.getServer().getWorld("world"), -121.5, 53, 69.5, yaw, pitch);
							p.teleport(tutorial1);
							p.setVelocity(new org.bukkit.util.Vector(0, 0, 0));
												

						}
						
						
						
						Location jump1 = new Location(Bukkit.getServer().getWorld("world"), -109.5, 53, 69.5); // 입구
						Location jump2 = new Location(Bukkit.getServer().getWorld("world"), -107.5, 53, 69.5, -90, 0); // 점프맵1
						if(ploc.distance(jump1)<=1.5) {
							p.teleport(jump2);
							p.stopSound("meme.tut1");
							p.playSound(ploc, "meme.tut2", 50, 1);
							p.sendMessage("§e시험 진행 A.I:§e §f앞에 보이는 초록색 블럭까지 이동하십시오§f");
						}
						
						BoundingBox box = new BoundingBox(-104, 47, 64, -88, 49, 74);
						if(box.contains(ploc.getX(), ploc.getY(), ploc.getZ())) {
							if(!jumpfail.containsKey(p)) jumpfail.put(p, 1);
							else jumpfail.replace(p, jumpfail.get(p)+1);
							
							p.sendMessage("§e시험 진행 A.I:§e §f다시 시도하십시오§f");
							
							p.playSound(p.getLocation(),"meme.tut3", 50, 1);
							p.teleport(jump2);
						}
						
						Location jump3 = new Location(Bukkit.getServer().getWorld("world"), -107.5, 53, 86.5, -90, 0); //점프맵 2
						
						if(jumpfail.containsKey(p)) {
							if(jumpfail.get(p)>=3) {
								p.sendMessage("§e시험 진행 A.I:§e §f실망스럽지만, 형식적인 절차일 뿐이니 생략하도록 하겠습니다§f");
								
								p.teleport(jump3);
								p.stopSound("meme.tut3");
								jumpfail.remove(p);
								
								p.playSound(p.getLocation(),"meme.tut4", 50, 1);
							}
						}
						
						BoundingBox box2 = new BoundingBox(-88, 47, 91, -104, 49, 81);
						if(box2.contains(ploc.getX(), ploc.getY(), ploc.getZ())) {
							
							p.sendMessage("§e시험 진행 A.I:§e §f다시 시도하십시오§f");
							p.playSound(p.getLocation(),"meme.tut3", 50, 1);
							p.teleport(jump3);
						}
						
						
						Location jump4 = new Location(Bukkit.getServer().getWorld("world"), -83.5, 53, 69.5);
						Location jump5 = new Location(Bukkit.getServer().getWorld("world"), -83.5, 53, 86.5);
						
						Location weapon = new Location(Bukkit.getServer().getWorld("world"), -69.5, 53, 69.5, 90, 0); // 훈련소
						
						if(ploc.distance(jump5)<=1.5 || ploc.distance(jump4)<=1.5) {
							p.teleport(weapon);
							p.sendMessage("§e시험 진행 A.I:§e §f두 번째 테스트를 시작하겠습니다. 지급받은 무기로 앞에 보이는 적을 공격하십시오.§f");
							Items.WeaponManager data = new Items.WeaponManager();
							p.getInventory().addItem(data.getitem("검"));	
							p.stopSound("meme.tut4");
							p.playSound(p.getLocation(),"meme.tut5", 50, 1);
						}
						
						
						Location weaponexit = new Location(Bukkit.getServer().getWorld("world"), -79.5, 52, 69.5); // 훈련소 출구
						Location ffentrance = new Location(Bukkit.getServer().getWorld("world"), -35.5, 57, 107.5, 90, 0); // 과녁 입구
						
						if(trainerbothit.containsKey(p) && ploc.distance(weaponexit)<1.5) {
							trainerbothit.remove(p);
							p.teleport(ffentrance);
							p.sendMessage("§e시험 진행 A.I:§e §f이번에는 기술 시전을 연습해 보겠습니다. F키를 두 번 입력하여 기술을 사용해 과녁을 타격하십시오. §f");
							p.stopSound("meme.tut5");
							p.playSound(p.getLocation(), "meme.tut24", 50, 1);
						}
						else if(!trainerbothit.containsKey(p) && ploc.distance(weaponexit)<1.5) {
							
							p.teleport(weapon);
							p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
							p.sendMessage("§c샌드백을 아직 때리지 않았습니다!§c");

						}
						
						
						
						Location ffexit = new Location(Bukkit.getServer().getWorld("world"), -45.5, 57, 107.5); // 과녁 출구
						Location dashentrance = new Location(Bukkit.getServer().getWorld("world"), -59.5, 51, 66.5, -90, 0); // 대쉬 입구
						
						if(trainerbothit.containsKey(p) && ploc.distance(ffexit)<1.5) {
							trainerbothit.remove(p);
							p.teleport(dashentrance);
							p.stopSound("meme.tut24");
							p.playSound(p.getLocation(),"meme.tut7", 50, 1);
							new BukkitRunnable() {
								
								int i=0;
								@Override
								public void run() {
									
									if(i==0) p.sendMessage("§e시험 진행 A.I:§e §f우클릭을 두 번 입력하여 대쉬를 사용할 수 있습니다.§f");
									if(i==80) p.sendMessage("§e시험 진행 A.I:§e §f대쉬를 연속으로 사용하여 대상 지역에 도달하십시오. 기술은 연속으로 사용하면 과부하되어 드는 에너지가 증가합니다. §f");
									if(i==280) {
										p.sendMessage("§e시험 진행 A.I:§e §f과부하는 스킬을 쓰지 않고 3초가 지나거나 다른 스킬을 사용하면 과부하가 풀리게 됩니다. §f");
										cancel();
									}
									
									i++;
									
								}
							}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

							
							
							//p.playSound(p.getLocation(), "meme.tut5", 1, 1);
						}
						else if(!trainerbothit.containsKey(p) && ploc.distance(ffexit)<1.5) { 
							p.teleport(ffentrance);
							p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
							p.sendMessage("§c과녁을 아직 때리지 않았습니다!§c");
						}
						
						
						BoundingBox box3 = new BoundingBox(-56, 46, 61, -40, 48, 71);
						if(box3.contains(ploc.getX(), ploc.getY(), ploc.getZ())){
							
							p.teleport(dashentrance);
							p.sendMessage("§e시험 진행 A.I:§e §f다시 시도하십시오§f");
							p.playSound(p.getLocation(),"meme.tut3", 50, 1);
						}
						
						
						Location dashexit = new Location(Bukkit.getServer().getWorld("world"), -35.5, 51, 66.5); // 대쉬 출구
						Location examentrance = new Location(Bukkit.getServer().getWorld("world"), -4.5, 53, 186.5, -90, 0); // 시험 입구
						
						
						
						
						
						
						
						Location examagainentrance = new Location(Bukkit.getServer().getWorld("world"), -70.5, 52, 91.5);
						if(dashexit.distance(ploc)<1.5 || examagainentrance.distance(ploc)<1) {
							if(examentrance.getChunk().isLoaded()) {
								p.teleport(examentrance);
								p.stopSound("meme.tut7");
								
								
								time.put(p, 0);
								GRADE.put(p, "none");
								SCORE.put(p, 0);	
							}
							


						}
						
						if(time.containsKey(p)) finalexam(p, examentrance);
						
						
						
						Location tutorialcomplete = new Location(Bukkit.getServer().getWorld("world"), -63.5, 52, 86.5);
						Location selectclass = new Location(Bukkit.getServer().getWorld("world"), -172.5, 111, 100.5, -90, 0); // 튜토리얼 완료 및 전직소 이동
						
						if(tutorialcomplete.distance(ploc) < 1.5) {
							p.teleport(selectclass);
							p.sendMessage("§e시험 진행 A.I:§e §f이제 당신의 개척자 파벌을 선택해야 합니다. 천천히 읽어보시고 신중히 결정해 주시길 바랍니다.§f");
							p.playSound(p.getLocation(), "meme.tut20", 5, 1);
							
							file.quest.getinstance().set(p, "tutorial", 1);
						}

						
						
						
					}
					

				
		
				}
		

		
	}
	
	
	public static void exambot() {
		
		HashMap<Location, String> name = new HashMap<>();

		final Location exam1 = new Location(Bukkit.getServer().getWorld("world"), 0.5, 54, 178.5);
		final Location exam2 = new Location(Bukkit.getServer().getWorld("world"), 3.5, 54, 177.5);
		final Location exam3 = new Location(Bukkit.getServer().getWorld("world"), 8.5, 57, 174.5);
		final Location exam4 = new Location(Bukkit.getServer().getWorld("world"), 7.5, 56, 166.5);
		final Location exam5 = new Location(Bukkit.getServer().getWorld("world"), -0.5, 57, 170.5);
		final Location exam6 = new Location(Bukkit.getServer().getWorld("world"), 2.5, 54, 166.5);
		final Location exam7 = new Location(Bukkit.getServer().getWorld("world"), -2.5, 54, 168.5);
		final Location exam8 = new Location(Bukkit.getServer().getWorld("world"), 5.5, 54, 160.5);
		final Location exam9 = new Location(Bukkit.getServer().getWorld("world"), 10.5, 60, 156.5);
		final Location exam10 = new Location(Bukkit.getServer().getWorld("world"), 3.5, 57, 156.5);
		final Location exam11 = new Location(Bukkit.getServer().getWorld("world"), -6.5, 58, 156.5);
		final Location exam12 = new Location(Bukkit.getServer().getWorld("world"), -8.5, 56, 163.5);
		final Location exam13 = new Location(Bukkit.getServer().getWorld("world"), -10.5, 57, 169.5);
		final Location exam14 = new Location(Bukkit.getServer().getWorld("world"), -16.5, 58, 166.5);
		final Location exam15 = new Location(Bukkit.getServer().getWorld("world"), -14.5, 54, 161.5);
		final Location exam16 = new Location(Bukkit.getServer().getWorld("world"), -14.5, 56, 157.5);
		final Location exam17 = new Location(Bukkit.getServer().getWorld("world"), -23.5, 56, 168.5);
		final Location exam18 = new Location(Bukkit.getServer().getWorld("world"), -21.5, 57, 161.5);
		final Location exam19 = new Location(Bukkit.getServer().getWorld("world"), -32.5, 59, 171.5);
		final Location exam20 = new Location(Bukkit.getServer().getWorld("world"), -29.5, 54, 165.5);
		final Location exam21 = new Location(Bukkit.getServer().getWorld("world"), -33.5, 57, 164.5);
		final Location exam22 = new Location(Bukkit.getServer().getWorld("world"), -26.5, 56, 154.5);
		final Location exam23 = new Location(Bukkit.getServer().getWorld("world"), -34.5, 57, 150.5);
		final Location exam24 = new Location(Bukkit.getServer().getWorld("world"), -34.5, 57, 148.5);
		final Location exam25 = new Location(Bukkit.getServer().getWorld("world"), -22.5, 54, 147.5);
		final Location exam26 = new Location(Bukkit.getServer().getWorld("world"), -25.5, 54, 147.5);
		final Location exam27 = new Location(Bukkit.getServer().getWorld("world"), -24.5, 56, 144.5);
		final Location exam28 = new Location(Bukkit.getServer().getWorld("world"), -22.5, 56, 144.5);
		final Location exam29 = new Location(Bukkit.getServer().getWorld("world"), -31.5, 54, 144.5);
		final Location exam30 = new Location(Bukkit.getServer().getWorld("world"), -32.5, 58, 140.5);
		
		Location examloc[] = {
				exam1,
				exam2,
				exam3,
				exam4,
				exam5,
				exam6,
				exam7,
				exam8,
				exam9,
				exam10,
				exam11,
				exam12,
				exam13,
				exam14,
				exam15,
				exam16,
				exam17,
				exam18,
				exam19,
				exam20,
				exam21,
				exam22,
				exam23,
				exam24,
				exam25,
				exam26,
				exam27,
				exam28,
				exam29,
				exam30};
		
		for(int i=0; i<=29; i++) {
			name.put(examloc[i], "exam"+Integer.toString(i+1)); // 위치 / 이름
		}
		
		
		for(Location loc : examloc) {
			exambotentity(loc, name.get(loc)); // 위치 이름으로 몹 소환
		}
		
		
		for(Player p : Bukkit.getOnlinePlayers()) { // 때리지 않은 몹 파티클 소환
			if(exambothit.containsKey(p)) {
				
				
				for(int k=0; k<30; k++) { // 전체 번호중
					
					if(exambothit.get(p)[k] == k+1) { // 맞았다면
						p.spawnParticle(Particle.REDSTONE, examloc[exambothit.get(p)[k]-1], 10, 0.5, 0.5, 0.5, new DustOptions(Color.GREEN, 2)); // 그 번호 해당하는 몹 파티클
					}
					
					if(exambothit.get(p)[k]!=k+1) { // 맞았다는 번호가 아니면
						p.spawnParticle(Particle.REDSTONE, examloc[k], 5, 0.25, 0.25, 0.25, new DustOptions(Color.RED, 2));
					}
					
				}
			}

		}
		
		
		
		
		
	}
	
	
	public static void exambotentity(Location loc, String name) {
		
		//Bukkit.broadcastMessage("hi1");
		
		
		for(LivingEntity le : Bukkit.getServer().getWorld("world").getLivingEntities()) {
			if(le.getCustomName() != null) {
				if(le.getCustomName().equals(name)) {
					return;
				}
			}

		}
		
		int i=0;
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(loc.getWorld() == p.getWorld()) {
				if(loc.distance(p.getLocation())<100) {
					i=1;
					break;
				}
			}
		}
		if(i==0) return;
		
		//Bukkit.broadcastMessage("hi");
		
		Slime slime = (Slime) Bukkit.getServer().getWorld("world").spawnEntity(loc, EntityType.SLIME);
		slime.setMaxHealth(2048);
		slime.setHealth(2048);
		slime.setCustomName(name);
		slime.setCustomNameVisible(true);
		slime.setAI(false);
		slime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 200), true);
		slime.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 200), true);
		//slime.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 99999, 200), true);
		slime.setSilent(true);
		slime.setSize(2);
		slime.setLootTable(null);
		
		
//		ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
//		ItemMeta meta = item.getItemMeta();
//		meta.setCustomModelData(1);
//		item.setItemMeta(meta);
//		
//		skeleton.getEquipment().setItemInMainHand(item);
		
		EntityStatusManager.getinstance(slime).setCanKnockback(false);
		
		//Bukkit.broadcastMessage("hi2");
	}
	
	
	public static void tutorialbotgreensign() {
		
		
	}
	
	
	public static void finalexam(Player p, Location examentrance) {
		
		Location ploc = p.getLocation();


		
		if(examset.containsKey(p)) { // 라인 소환
			
			for(double k=-4; k<11; k+=0.5) {
				p.spawnParticle(Particle.SMOKE_NORMAL, k, 54d, 182d, 1, 0, 0, 0, 0, null);
			
			}
		}



		
		if(time.get(p)==0) {
			
			for(Player pl : Bukkit.getOnlinePlayers()) {
				pl.hidePlayer(Bukkit.getPluginManager().getPlugin("spellinteract"), p);
			}
			examset.put(p, 1);
			p.sendMessage("§e시험 진행 A.I:§e §f훌륭합니다. 이젠 마지막 시험입니다.§f");
			p.playSound(p.getLocation(),"meme.tut8", 50, 1);
		}
		if(time.get(p)==60) {
			p.sendMessage("§e시험 진행 A.I:§e §f배운 것들을 이용하여 10초 동안 이 훈련장에 있는 과녁들을 될 수 있는 만큼 격파하면 됩니다.§f");
			p.playSound(p.getLocation(),"meme.tut10", 50, 1);
			int entitynumbers[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
			exambothit.put(p, entitynumbers); // 봇 활성화
			Tutorial.exambothitcount.put(p, 0); // 봇 개수 활성화
			
		}
		if(time.get(p)==180) {
			p.sendMessage("§e시험 진행 A.I:§e §f3초 후에 시험을 시작하겠습니다.§f");

		}
		if(time.get(p)==260) {
			p.playSound(p.getLocation(),"meme.3", 50, 1);
		}
		if(time.get(p)==280) {
			p.playSound(p.getLocation(),"meme.2", 50, 1);
		}
		if(time.get(p)==300) {
			p.playSound(p.getLocation(),"meme.1", 50, 1);
		}
		if(time.get(p)==320) {
			
			p.sendMessage("§e시험 진행 A.I:§e§f 시작§f");
			examset.remove(p);

		}
		if(time.get(p)%20==0 && time.get(p)>=320 && time.get(p)<520) {
			int time1 = (int)((520-time.get(p))/20);
			p.playSound(ploc, Sound.UI_BUTTON_CLICK, 1, 2);
			p.sendMessage("§e남은시간:§e §f"+time1+"§f");
		}
		
		if(time.get(p)>=520) {
			
			if(time.get(p)==520) { // 10초 지나면
				
				SCORE.replace(p, Tutorial.exambothitcount.get(p));
				
				File questfile = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "userquest.yml");
				FileConfiguration quest  = YamlConfiguration.loadConfiguration(questfile);
				
				if(quest.contains(p.getUniqueId().toString()+".tutorial")) {
					if(file.quest.getinstance().getint(p, "tutorialscore") < SCORE.get(p)) // 기존 기록을 경신하면
						file.quest.getinstance().set(p, "tutorialscore", SCORE.get(p));
				}
				
				for(Player pl : Bukkit.getOnlinePlayers()) {
					pl.showPlayer(Bukkit.getPluginManager().getPlugin("spellinteract"), p);
				}
				
				
				if(!quest.contains(p.getUniqueId().toString()+"."+"tutorialscore")) quest.set(p.getUniqueId().toString()+"."+"tutorialscore", SCORE);

				if(SCORE.get(p)<=1) {
					GRADE.replace(p, "§4F§4");
					
					p.sendMessage("§6과녁 §a "+Integer.toString(SCORE.get(p))+"§a§6개를 처치하여 성적 "+GRADE.get(p)+"§6를 받았습니다");
					p.teleport(examentrance);
					p.sendMessage("§e시험 진행 A.I:§e §f긴장하신건가요? 설명을 다시 하겠습니다.§f");
					
					
					
					p.playSound(p.getLocation(), "meme.tut11", 1, 1);
					
				}
				if(SCORE.get(p)>=2 && SCORE.get(p)<=5) {
					GRADE.replace(p, "§cD§c");
					
					Location finishloc = new Location(Bukkit.getWorld("world"), -70.5, 52, 86.5);
					p.sendMessage("§6과녁 §a "+Integer.toString(SCORE.get(p))+"§a§6개를 처치하여 성적 "+GRADE.get(p)+"§6를 받았습니다");
					p.sendMessage("§e시험 진행 A.I:§e §f아슬아슬하게 합격점이군요. 축하합니다.§f");
					p.sendMessage("§e시험 진행 A.I:§e §f성적이 만족되지 않는다면 재시험을 치르실 수 있습니다.§f");
					p.teleport(finishloc);
					p.playSound(p.getLocation(), "meme.tut12", 1, 1);
					
					time.remove(p);
					GRADE.remove(p);
					SCORE.remove(p);
					return;
					
				}
				if(SCORE.get(p)>=6 && SCORE.get(p)<=10) {
					GRADE.replace(p, "§cC§c");
					
					Location finishloc = new Location(Bukkit.getWorld("world"), -70.5, 52, 86.5, 137, 0);
					p.sendMessage("§6과녁 §a "+Integer.toString(SCORE.get(p))+"§a§6개를 처치하여 성적 "+GRADE.get(p)+"§6를 받았습니다");
					p.sendMessage("§e시험 진행 A.I:§e §f합격하셨습니다. 축하합니다. §f");
					p.sendMessage("§e시험 진행 A.I:§e §f성적이 만족되지 않는다면 재시험을 치르실 수 있습니다.§f");
					p.teleport(finishloc); 
					p.playSound(p.getLocation(), "meme.tut13", 1, 1);
					
					time.remove(p);
					GRADE.remove(p);
					SCORE.remove(p);
					return;
				}
				if(SCORE.get(p)>=11 && SCORE.get(p)<=16) {
					GRADE.replace(p, "§9B§9");
					
					Location finishloc = new Location(Bukkit.getWorld("world"), -70.5, 52, 86.5);
					p.sendMessage("§6과녁 §a "+Integer.toString(SCORE.get(p))+"§a§6개를 처치하여 성적 "+GRADE.get(p)+"§6를 받았습니다");
					p.sendMessage("§e시험 진행 A.I:§e §f꽤나 훌륭한 성적이군요. 축하합니다.§f");
					p.sendMessage("§e시험 진행 A.I:§e §f성적이 만족되지 않는다면 재시험을 치르실 수 있습니다.§f");
					p.teleport(finishloc);
					p.playSound(p.getLocation(), "meme.tut14", 1, 1);
					
					time.remove(p);
					GRADE.remove(p);
					SCORE.remove(p);
					return;
				}
				if(SCORE.get(p)>=17 && SCORE.get(p)<=25) {
					GRADE.replace(p, "§aA§a");
					
					Location finishloc = new Location(Bukkit.getWorld("world"), -70.5, 52, 86.5);
					p.sendMessage("§6과녁 §a "+Integer.toString(SCORE.get(p))+"§a§6개를 처치하여 성적 "+GRADE.get(p)+"§6를 받았습니다");
					p.sendMessage("§e시험 진행 A.I:§e §f훌륭하군요, 미래가 기대되는 개척자가 한 명 늘었군요.§f");
					p.sendMessage("§e시험 진행 A.I:§e §f성적이 만족되지 않는다면 재시험을 치르실 수 있습니다.§f");
					p.teleport(finishloc);
					p.playSound(p.getLocation(), "meme.tut15", 1, 1);
					
					time.remove(p);
					GRADE.remove(p);
					SCORE.remove(p);
					return;
				}
				if(SCORE.get(p)>=26) {
					GRADE.replace(p, "§6S§6");
					
					Location finishloc = new Location(Bukkit.getWorld("world"), -70.5, 52, 86.5);
					p.sendMessage("§6과녁 §a"+Integer.toString(SCORE.get(p))+"§a§6개를 처치하여 성적"+GRADE.get(p)+"§6를 받았습니다");
					p.sendMessage("§e시험 진행 A.I:§e §f카르세우스급 개척자의 시작을 목격하게 되어 영광입니다. 알 테라께서 당신을 보고 싶어 하시는군요.§f");
					p.teleport(finishloc);
					p.playSound(p.getLocation(), "meme.tut16", 1, 1);
					
					time.remove(p);
					GRADE.remove(p);
					SCORE.remove(p);
					return;
				}
				
				
				Tutorial.exambothitcount.remove(p);
				Tutorial.exambothit.remove(p);
				
				if(time.get(p)==520) {
					if(SCORE.get(p)<=1) {
						for(Player pl : Bukkit.getOnlinePlayers()) {
							pl.hidePlayer(Bukkit.getPluginManager().getPlugin("spellinteract"), p);
						}
						int entitynumbers[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
						Tutorial.exambothit.put(p, entitynumbers); // 봇 활성화
						Tutorial.exambothitcount.put(p, 0); // 봇 개수 활성화
						examset.put(p, 1);
					}
				}
			}

			
			
			if(time.get(p)==580 && GRADE.get(p).equals("§4F§4")) {
				p.sendMessage("§e시험 진행 A.I:§e §f과녁은 피해를 입히면 사라집니다.§f");
			}
			if(time.get(p)==660 && GRADE.get(p).equals("§4F§4")) {
				p.sendMessage("§e시험 진행 A.I:§e §f배운 기술들과 무기를 이용하여 10초 동안 될 수 있는 만큼 과녁을 타격하면 됩니다.§f");
			}
			if(time.get(p)==780 && GRADE.get(p).equals("§4F§4")) {
				p.sendMessage("§e시험 진행 A.I:§e §f3초 후에 시험을 시작하겠습니다.§f");
				time.replace(p, 260);

			}
		}
		

		
		//tutorialbotsign();
		
		
		BoundingBox box5 = new BoundingBox(-5, 52, 182, 12, 62, 181);
		Location back = new Location(Bukkit.getServer().getWorld("world"), 2.5, 53, 187.5, 180, 0);
		if(examset.containsKey(p)) {
			if(box5.contains(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ())) {
				p.teleport(back);
				p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
				p.sendMessage("§c시험이 시작되지 않았습니다!§c");
			}
		}
		
		
		
		time.replace(p, time.get(p)+1);
		
		
		
	}
}


