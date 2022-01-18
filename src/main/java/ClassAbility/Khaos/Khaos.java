package ClassAbility.Khaos;

import ClassAbility.Combination;
import utils.DuraAbilityHandler;
import utils.targetBuilder;
import Mob.EntityStatusManager;
import PlayParticle.Rotate;
import PlayerManager.PlayerEnergy;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerManager;
import PlayerManager.PlayerHealthShield;
import Mob.EntityManager;
import com.google.common.base.Enums;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import spellinteracttest.Main;

import java.util.*;
import java.util.function.Function;

public class Khaos {

    private ArmorStand FRA;

    private static Khaos khaos;
    private Player player;
    private PlayerFunction playerFunction;
    private PlayerManager pm;
    private int CurrentMana;
    private int ManaDecrease;
    private targetBuilder tb;

    public Khaos(Player player) {
        this.player = player;
        this.playerFunction = PlayerFunction.getinstance(player);
        this.CurrentMana = PlayerEnergy.getinstance(player).getEnergy();
        this.ManaDecrease = PlayerManager.getinstance(player).ManaDecrease;
        this.pm = PlayerManager.getinstance(player);
        this.tb = targetBuilder.builder(player);
    }

    private Khaos() {

    }

    public static Khaos getInstance() {
        if(khaos == null) khaos = new Khaos();
        return khaos;
    }

    private enum ENUM {
        RL(4, "§o§l반월참§l§o §3§l-⚡§l"),
        SHIFTR(4, "§o§l공간전이§l§o §3§l-⚡§l"),
        RR(6, "§o§lRR스킬§l§o §3§l-⚡§l"),
        FR(8, "§o§lFR스킬§l§o §3§l-⚡§l");

        private int mana;
        private String title;
        private String method;

        ENUM(int mana, String title) {
            this.mana = mana;
            this.title = title;
        }

        int getMana() {
            return mana;
        }

        String getTitle() {
            return title;
        }
    }

    public int Skill(String combo) {

        if(!Enums.getIfPresent(ENUM.class, combo).isPresent()) return 0;

        int RLtII = pm.getTalent("RL", 2);
        int FRtI = pm.getTalent("FR", 1);
        int FRtIII = pm.getTalent("FR", 3);
        int originmana = ENUM.valueOf(combo).getMana();

        if(RLtII == 1 && combo.equals("RL")) originmana -= 1;
        if(FRtI == 2 && combo.equals("FR")) originmana -= 1;
        if(FRtIII == 2 && combo.equals("FR")) originmana -= 2;

        int mana = originmana - ManaDecrease <= 0 ? 1 : originmana - ManaDecrease
                + PlayerEnergy.getinstance(player).getEnergyOverload();
        String title = ENUM.valueOf(combo).getTitle()+mana;

        if(mana <= CurrentMana) {
            PlayerEnergy.getinstance(player).useEnergy(mana);
            if(combo.equals("SHIFTR")) SHIFTR(player);
            if(combo.equals("RL")) HalfMoon();
            if(combo.equals("FR")) FR();
            if(combo.equals("RR")) RR(mana);

            Combination.getinstance().Sound(player);
            player.sendTitle(" ", Combination.blank+title, 5, 20, 10);
            PlayerEnergy.getinstance(player).energyOverload(combo);
            return mana;
        }
        else {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 1f);
            player.sendTitle(" ", Combination.blank+Combination.manaexhaustion, 0, 20, 10);
        }
        return 0;
    }

    public void SHIFTR(Player player) {

        Set<Entity> Hit = new HashSet<>();

        if(KhaosMelee.getThrown(player) == null) {

        }
        /*
        단검이 있다면 단검으로 순간이동

         */
        else if(KhaosMelee.getThrown(player) != null) {

            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0)
                    , 500, 0, 0, 0, 1);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 1);
            player.teleport(KhaosMelee.getThrown(player));

            Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"),
                    () -> {

                        Location loc = player.getLocation();

                        targetBuilder tb = targetBuilder.builder(player)
                                .setRadius(4)
                                .setDamage(() -> PlayerManager.getinstance(player).spelldmgcalculate(player, 1.5))
                                .addPlaySound(() -> player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f ))
                                .addPlaySound(() -> player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1.5f))
                                .addStatus((aE) -> EntityStatusManager.getinstance(aE).KnockBack(player, 0.5))
                                .entityExcept(Hit)
                                .setLocation(loc)
                                .build();

                        Hit.addAll(tb.getHitEntity());

                        new BukkitRunnable() {

                            double time = 0;
                            double r = 0 / 255D;
                            double g = 127 / 255D;
                            double b = 255 / 255D;

                            @Override
                            public void run() {

                                for(double j=0; j<1; j+=0.2) {
                                    for(double i = 0; i<Math.PI*2; i+=Math.PI/16) {
                                        double x = Math.cos(i) * (time + j);
                                        double y = 0;
                                        double z = Math.sin(i) * (time + j);

                                        Vector v = new Vector(x, y, z);
                                        loc.add(v);
                                        player.getWorld().spawnParticle(Particle.SPELL_INSTANT, loc, 1, 0, 0, 0, 0);
                                        loc.subtract(v);
                                    }
                                }

                                if(time >= 4) cancel();
                                time++;
                            }
                        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);


                player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, player.getLocation().add(0, 1, 0)
                                , 500, 0, 0, 0, 1);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

                    }, 1);

        }
    }

    public void HalfMoon() {

        int RLtI = pm.getTalent("RL", 1);
        int RLtII = pm.getTalent("RL", 2);
        int RLtIII = pm.getTalent("RL", 3);
        int RLtIV = pm.getTalent("RL", 4);

        Location loc = player.getEyeLocation();
        PlayerEnergy pe = PlayerEnergy.getinstance(player);
        boolean isPreSkillSame = pe.getPreviousSkill().equals("RL");
        int overload = pe.getEnergyOverload();
        double rate = (RLtI == 2 ? 1.75 : 1.5) + 0.25 * (isPreSkillSame ? overload : 0);

        if(RLtIV == 1) rate *= 1 + 2 * (1 - (double)PlayerHealthShield.getinstance(player).getCurrentHealth() / (double)pm.Health);

        final double finalrate = rate;

        Set<Entity> Hit = new HashSet<>();
        player.getWorld().playSound(loc, Sound.ENTITY_IRON_GOLEM_ATTACK, 1, 1);
        player.getWorld().playSound(loc, Sound.ENTITY_EVOKER_CAST_SPELL, 1, 1);
        player.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
        player.getWorld().playSound(loc, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1, 1);
        double rpitch = Math.toRadians(loc.getPitch());
        double ryaw = Math.toRadians(loc.getYaw());
        double rroll = Math.toRadians(Math.random() * 40 - 20);

        targetBuilder tb = targetBuilder.builder(player)
                .setDamage(() -> PlayerManager.getinstance(player).spelldmgcalculate(player, finalrate))
                .addStatus((e) -> EntityStatusManager.getinstance(e).KnockBack(player, 0.5))
                .addPlaySound(() -> player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f))
                .setRadius(1.5);

        if (RLtII == 2) {
            tb.addStatus((e) -> {
                e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 0));
            });
        }
        if(RLtIII == 2) {
            tb.addStatus((e) -> {
                EntityManager em = EntityManager.getinstance(e);
                if(em.dummyCount.stream().filter((a)->a.contains("KHRLtIII2")).toList().size()<4) {
                    em.damageTakenRate += 0.05;
                    em.dummyCount.add("KHRLtIII2");
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                            em.damageTakenRate -= 0.05;
                            em.dummyCount.remove("KHRLtIII2");
                    }, 200);
                }

            });
        }
        else if(RLtIII == 3) {

            DuraAbilityHandler.getHandler(player, "KHRLtIII3")
                    .setMaximumStack(8)
                    .setTick(120)
                    .setRunnable(()->pm.addiWalkSpeed += 5, ()->pm.addiWalkSpeed -= 5)
                    .isPersistent(true)
                    .setStopCondition(() -> !PlayerEnergy.getinstance(player).getPreviousSkill().equals("RL"))
                    .run();
        }

        if(RLtIV == 3) {

            DuraAbilityHandler.getHandler(player, "KHRLtIV3")
                    .setMaximumStack(10)
                    .setTick(60)
                    .setRunnable(()->pm.damageTakenRate-=0.05, ()->pm.damageTakenRate+=0.05)
                    .isPersistent(true)
                    .run();


            if(pm.dummyCount.stream().filter((a)->a.contains("KHRRLtIV3")).toList().size()<10) {
                pm.damageTakenRate -= 0.05;
                pm.dummyCount.add("KHRRLtIV3");
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                    pm.damageTakenRate += 0.05;
                    pm.dummyCount.remove("KHRRLtIV3");
                }, 60);
            }
        }

        new BukkitRunnable() {

            int time = 0;
            double angle = -60;

            @Override
            public void run() {


                for(int i=0; i<10; i++) {

                    for(double j =1.5; j<4; j+=0.3) {

                        double yangle = Math.toRadians(angle);
                        double ycos = Math.cos(-yangle);
                        double ysin = Math.sin(-yangle);

                        double x = 0;
                        double y = 0;
                        double z = j;
                        Vector v = new Vector(x, y, z);
                        v = Rotate.rotateAroundAxisY(v, ycos, ysin);
                        v = Rotate.transform(v, ryaw, rpitch, rroll);
                        loc.add(v);
                        if(j>3.1)
                            player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.CRIT, loc, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0, 0, 0, 0);

                        tb.setLocation(loc).build();
                        loc.subtract(v);
                    }

                    angle+=4;
                }

                if(time >= 3) {
                    if(RLtIV == 2) {
                        double healrate = ((double)tb.getHitEntity().size()) * 3 / 100;
                        PlayerHealthShield.getinstance(player).HealthAdd((int) (pm.Health * healrate), player);
                    }
                    cancel();
                }

                time++;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);

    }

    public void RR(int usedMana) {

        int RRtI = pm.getTalent("RR", 1);
        int RRtII = pm.getTalent("RR", 2);
        int RRtIII = pm.getTalent("RR", 3);
        int RRtIV = pm.getTalent("RR", 4);

        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize().multiply(4);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1, 1);

        double spellrate = 1;
        if(RRtI == 1) spellrate = 1.1;
        else if(RRtI == 2) {
            DuraAbilityHandler.getHandler(player, "KHRRtI2")
                    .isPersistent(true)
                    .setTick(100)
                    .setMaximumStack(1)
                    .setRunnable(()->pm.addiWalkSpeed+=10, ()->pm.addiWalkSpeed-=10)
                    .run();
        }
        if(RRtIV == 1)
            spellrate *= 1 + 2 *
                    ( 1 - (double)PlayerHealthShield.getinstance(player).getCurrentHealth() / (double)pm.Health);

        if(RRtII == 1) {
            spellrate *= 3;
            tb.setHitOnlyOne(true);
        }

        final double finalspellrate = spellrate;

        tb.setRadius(2.5)
                .setDamage(() -> PlayerManager.getinstance(player).spelldmgcalculate(player, finalspellrate))
                .addStatus((e) -> EntityStatusManager.getinstance(e).KnockBack(player, 1.2))
                .addPlaySound(() -> player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f));

        if(RRtIII == 1) {
            tb.addTargetAfterDamage((e, d) -> {
                if(EntityManager.getinstance(e).getCurrentHealth()-d<0)
                    PlayerEnergy.getinstance(player).addEnergy(usedMana);
            });
        }
        else if(RRtIII == 3) {
            pm.dummyCount.add("KHRRtIII3");
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
                    pm.dummyCount.remove("KHRRtIII3"), 30);
        }

        if(RRtIV == 2) {
            tb.addRunOnlyOnce(() -> {
                if(pm.evasion.stream().filter((a)->a.contains("KHRRtIV2")).toList().size()<4){
                    pm.evasion.add("KHRRtIV2");
                }
                if(pm.evasion.stream().filter((a)->a.contains("KHRRtIV2")).toList().size()<4){
                    pm.evasion.add("KHRRtIV2");
                }
            });
        }


        new BukkitRunnable() {

            double yA = -45;
            double yB = 45;

            int time = 0;

            @Override
            public void run() {

                player.setVelocity(dir);

                if(time>=1) player.setVelocity(new Vector(0, 0, 0));
                if(time>1) {

                    //player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1, 0);
                    for(int i=0; i<5; i++) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, ((float)i/2));
                    }
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1.5f);

                    Location loc_ = player.getEyeLocation().add(0, -0.3, 0);
                    double rP = Math.toRadians(loc_.getPitch());
                    double rY = Math.toRadians(loc_.getYaw());

                    for(double j = -60; j<60; j+=2) {
                        for(double z = 1.5; z<4; z+=0.3) {
                            double radian = Math.toRadians(j);
                            double x = 0;
                            double y = 0;
                            Vector v = new Vector(x, y, z);
                            v = Rotate.rotateAroundAxisY(v, Math.cos(radian), Math.sin(radian));
                            v = Rotate.transform(v, rY, rP, 0);
                            loc_.add(v);
                            player.getWorld().spawnParticle(Particle.BLOCK_DUST, loc_, 1, 0.2, 0.2, 0.2, 0
                            ,Material.AMETHYST_BLOCK.createBlockData());

                            tb.setLocation(loc_).build();

                            loc_.subtract(v);
                        }
                    }

                    for(double j=0; j<Math.PI*2; j+=Math.PI/32) {
                        double x = Math.cos(j);
                        double y = Math.sin(j);
                        double z = 1;
                        Vector v = new Vector(x, y, z);
                        v = Rotate.transform(v, rY, rP, 0);
                        loc_.add(v);
                        player.getWorld().spawnParticle(Particle.CLOUD, loc_, 0, v.getX(), v.getY(), v.getZ(), 0.25);
                        Vector v_ = new Vector(x, y, 0);
                        v_ = Rotate.transform(v_, rY, 0,0);
                        player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,
                                player.getLocation().add(0, 1, 0), 0, v_.getX(), v_.getY(), v_.getZ(), 0.2);
                        Vector v__ = new Vector(x, z, y);
                        player.getWorld().spawnParticle(Particle.SNOWFLAKE,
                                player.getLocation().add(0, 1, 0), 0, v__.getX(), v__.getY(), v__.getZ(), 0.2);
                        loc_.subtract(v);
                    }
                    cancel();
                }
                time++;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }

    public void FR() {

        playerFunction.KhaosFR = 60;

        int FRtI = pm.getTalent("FR", 1);
        int FRtII = pm.getTalent("FR", 2);
        int FRtIII = pm.getTalent("FR", 3);
        int FRtIV = pm.getTalent("FR", 4);

        double def = 0.6;
        double nomanadef = 0.9;
        int lastsec = 30;
        int manaLost = 3;
        int manaTake = 1;
        if(FRtI == 1) def = 0.5;
        if(FRtII == 1) lastsec = 40;
        else if(FRtII == 2) nomanadef = 0.7;
        if(FRtIII == 3) manaLost = 2;

        if(FRtIV == 1) manaTake = 2;

        final int finallastsec = lastsec;
        final double finaldef = def;
        final double finalnomanadef = nomanadef;
        final int finalmanaLost = manaLost;
        final int finalmanaTake = manaTake;

        if(FRtIV == 3) {
            Function<Integer, Integer> giveDamageFunction = (dmg) -> (int)((double)dmg*1.5);
            pm.giveDamageModifier.add(giveDamageFunction);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
                    pm.giveDamageModifier.remove(giveDamageFunction), finallastsec);
        }
        else if(FRtIV == 2) {

            new BukkitRunnable() {
                int time = 0;
                @Override
                public void run() {
                    if(time%8 == 0) {
                        Location loc = player.getLocation();
                        targetBuilder.builder(player)
                                .setRadius(3)
                                .setDamage(() -> pm.spelldmgcalculate(player, 0.1))
                                .setLocation(loc).build();

                        for(double i = 0; i<Math.PI * 2; i+= Math.PI/32) {
                            double x = Math.cos(i);
                            double y = 0.2;
                            double z = Math.sin(i);
                            Vector v = new Vector(x, y, z);
                            loc.add(v);
                            player.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 0, v.getX(), v.getY(),
                                    v.getZ(), 0.1);
                            loc.subtract(v);
                        }
                    }
                    if(time > finallastsec) cancel();
                    time++;
                }
            }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
        }

        Function<Integer, Integer> modf = (dmg) -> {
            if(PlayerEnergy.getinstance(player).getEnergy()>1) {
                dmg = (int)((double)dmg*finaldef);
            }
            else {
                dmg = (int)((double)dmg*finalnomanadef);
            }

            return dmg;
        };

        // 피해를 줄때마다 마나 1
        Runnable runnable = () -> {
            if(PlayerEnergy.getinstance(player).getEnergy()<20) {
                DynamicData.HologramIndicator.getinstance().ManaIndicator(finalmanaTake, player.getLocation());
                PlayerEnergy.getinstance(player).addEnergy(finalmanaTake);
            }
        };

        // 피해를 받을 때마다 마나를 잃음
        Runnable manaLostRunnable = () -> {
            if(PlayerEnergy.getinstance(player).getEnergy()>1) {
                DynamicData.HologramIndicator.getinstance().ManaIndicator(-finalmanaLost, player.getLocation());
                PlayerEnergy.getinstance(player).setEnergy(PlayerEnergy.getinstance(player).getEnergy() - finalmanaLost);
            }
        };

        pm.takeDamageModifier.add(modf);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
                pm.takeDamageModifier.remove(modf), finallastsec);

        pm.runWhenAttack.add(runnable);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
                pm.runWhenAttack.remove(runnable), finallastsec);

        pm.runWhenDamaged.add(manaLostRunnable);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
                pm.runWhenDamaged.remove(manaLostRunnable), finallastsec);

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1.5f);
        FREffect(finallastsec);
    }

    private void FREffect(int tick) {
        new BukkitRunnable() {

            int time = 0;

            @Override
            public void run() {

                //FRA.teleport(player.getEyeLocation().add(0, 0.5, 0));

                player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation()
                        , 3, 0.3, 0.3, 0.3, 0, Material.BLUE_ICE.createBlockData());
                player.getWorld().spawnParticle(Particle.SNOW_SHOVEL, player.getLocation()
                        , 3, 0.3, 0.3, 0.3, 0);

                if(time>tick) {
                    if(FRA != null) FRA.remove();
                    cancel();
                }
                time++;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }
}
