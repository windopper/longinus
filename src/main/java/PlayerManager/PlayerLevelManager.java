package PlayerManager;

import Mob.EntityManager;
import Mob.MobListManager;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Set;

public class PlayerLevelManager {

    public static void XPContribute(Entity entity, MobListManager.MobList mobList) {

        int exp = mobList.getEXP();
        int MaxHealth = EntityManager.getinstance(entity).getMaxHealth();

        Set<Player> getContributor = EntityManager.getinstance(entity).getContribute().keySet();
        for(Player player : getContributor) {
            double ContributeRate = EntityManager.getinstance(entity).getContribute().get(player) / MaxHealth;
            int GiveExp = (int) (exp * ContributeRate);

            PlayerStatManager psm = PlayerStatManager.getinstance(player);
            psm.setexp(psm.getexp() + GiveExp);
        }
    }

    private static void ShowEXPArmorStand(Entity entity, Player showTo, int EXP) {
        Location location = entity.getLocation();

        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setCustomNameVisible(true);
        armorStand.setMarker(true);
        armorStand.setVisible(false);
        armorStand.setCustomName("§7"+Integer.toString(EXP));

    }
}
