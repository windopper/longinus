package Mob;

import DynamicData.EntityManager;
import Gliese581cMobs.BloodRoot;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;

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

        HashMap<LivingEntity, EntityManager> ehms = EntityManager.getEntityHealthManager();

        for(LivingEntity entity : ehms.keySet()) {
            if(entity == null) continue;
            if(ehms.get(entity).getMobList() == null) continue;

            MobListManager.MobList mobList = ehms.get(entity).getMobList();

            if(mobList.equals(MobListManager.MobList.블러드루트) && ehms.get(entity).getPatterntime() % 20 == 0) {

                Player target = GetNearestPlayerFromEntity(entity, 10);
                if(target == null) continue;
                BloodRoot.BloodRootSkill1(entity, target);

            }







        }
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

    private Player GetNearestPlayerFromEntity(LivingEntity entity, double distance) {

        Location location = entity.getLocation();
        double nearestdist = distance;
        Player nearestplayer = null;

        for(Entity target : entity.getWorld().getNearbyEntities(location, distance, 2, distance)) {
            if(target instanceof Player) {
                Player targetPlayer = (Player) target;
                if(targetPlayer.getGameMode().equals(GameMode.ADVENTURE)) {
                    Location targetlocation = targetPlayer.getLocation();
                    if(targetlocation.distance(location) < nearestdist) {
                        nearestdist = targetlocation.distance(location);
                        nearestplayer = targetPlayer;
                    }
                }
            }
        }
        return nearestplayer;
    }
}
