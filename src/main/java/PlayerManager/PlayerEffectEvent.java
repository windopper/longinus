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
    }

}
