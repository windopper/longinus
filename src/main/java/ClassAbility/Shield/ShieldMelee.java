package ClassAbility.Shield;

import PlayerManager.PlayerFunction;
import org.bukkit.entity.Player;

public class ShieldMelee {

    private Player player;
    private PlayerFunction playerFunction;

    public ShieldMelee(Player player) {
        this.player = player;
        playerFunction = PlayerFunction.getinstance(player);
    }

    public void Melee(String combo) {

        int MeleeCombo = playerFunction.getMeleeCombo();

        if (playerFunction.getMeleeDelay() != 0) return;

    }
}
