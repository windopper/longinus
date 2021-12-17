package PlayerManager;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerEffectEvent {

    private static PlayerEffectEvent playerEffectEvent;

    private PlayerEffectEvent() {

    }

    public static PlayerEffectEvent getInstance() {
        if(playerEffectEvent == null) playerEffectEvent = new PlayerEffectEvent();
        return playerEffectEvent;
    }

    public void ShieldBrokenEffect(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 0));
//        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 2000, 0));
//
//        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
//            @Override
//            public void run() {
//                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
//            }
//        }, 10);
    }

}
