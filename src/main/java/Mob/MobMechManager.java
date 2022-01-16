package Mob;

import Mob.Gliese581cMobs.BloodRoot;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MobMechManager {

    private static MobMechManager mobMechManager;

    private int time = 0;

    private MobMechManager() {

    }

    public static MobMechManager getInstance() {
        if(mobMechManager == null) mobMechManager = new MobMechManager();
        return mobMechManager;
    }

    public void RunGliese581cMobMech() {

        List<Entity> entitylist = new CopyOnWriteArrayList<>(EntityManager.getEntityHealthManager().keySet());

//        try {
            for (Entity entity : entitylist) {
                if (entity == null) continue;
                if (EntityManager.getEntityHealthManager().get(entity).getMobList() == null) continue;

                MobListManager.MobList mobList = EntityManager.getEntityHealthManager().get(entity).getMobList();

                if (mobList.equals(MobListManager.MobList.블러드루트) && EntityManager.getEntityHealthManager().get(entity).getPatterntime() % 20 == 0) {

                    Entity target = GetNearestPlayerFromEntity(entity, 15);
                    if (target == null) continue;
                    BloodRoot.BloodRootSkill1(entity, target);
                }
            }
//        }
//        catch(ConcurrentModificationException e) {
//
//        }
        time++;
    }

    private Player GetPlayerFromLineOfSight(LivingEntity entity, double distance) {

        Location location = entity.getLocation();
        Vector vector = location.getDirection().normalize().multiply(0.2);

        for(int i=0; i<distance*5; i++) {

            for(Entity target : entity.getWorld().getLivingEntities()) {
                if(target instanceof Player) {
                    Player targetPlayer = (Player) target;
                    if(targetPlayer.getGameMode().equals(GameMode.ADVENTURE)) {
                        Location targetlocation = targetPlayer.getLocation();
                        //targetlocation.add(0, -1, 0);
                        if(targetlocation.distance(location) < 1.5) {
                            return targetPlayer;
                        }
                    }
                }
            }
            location.add(vector);

        }
        return null;
    }

    private Entity GetNearestPlayerFromEntity(Entity entity, double distance) {

        Location location = entity.getLocation();
        double nearestdist = distance;
        LivingEntity nearestplayer = null;

        for(Entity target : entity.getWorld().getNearbyEntities(location, distance, 5, distance)) {
            if(EntityCheck(target, entity)) {
                LivingEntity lE = (LivingEntity) target;
                    Location targetlocation = lE.getLocation();
                    if(targetlocation.distance(location) < nearestdist) {
                        nearestdist = targetlocation.distance(location);
                        nearestplayer = lE;
                    }
            }
        }
        return nearestplayer;
    }

    public boolean EntityCheck(Entity target, Entity ME) {

        if(target instanceof LivingEntity && !(target instanceof ArmorStand) && !(target instanceof FallingBlock) && ME != target) {

            if(target instanceof Player) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(target.getName().equals(player.getName())) return true;
                }
                return false;
            }

            MobListManager.MobList TmobList = EntityManager.getinstance((LivingEntity) target).getMobList();
            MobListManager.MobList MmobList = EntityManager.getinstance((LivingEntity) ME).getMobList();

            if(TmobList == null || MmobList == null) return false;
            if(TmobList.name().equals(MmobList.name())) return false;

            return true;
        }

        return false;
    }
}
