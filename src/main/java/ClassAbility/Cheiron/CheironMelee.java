package ClassAbility.Cheiron;

import PlayerManager.PlayerFunction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CheironMelee implements Listener {

    private Player player;
    private PlayerFunction playerFunction;
    private static CheironMelee cheironMelee;

    public CheironMelee(Player player) {
        this.player = player;
        playerFunction = PlayerFunction.getinstance(player);
    }
    public CheironMelee() {

    }

    public static CheironMelee getInstance() {
        if(cheironMelee == null) cheironMelee = new CheironMelee();
        return cheironMelee;
    }

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        try {
            if(event.getItem().getType() == Material.BOW && (event.getAction() == Action.RIGHT_CLICK_BLOCK
                    || event.getAction() == Action.RIGHT_CLICK_AIR)) {
                Bukkit.broadcastMessage("a");
                ItemStack bow = event.getItem();
                event.setUseItemInHand(Event.Result.DENY);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
                    event.setUseItemInHand(Event.Result.DENY);

                }, 5);
            }
        }
        catch(Exception e) {

        }

    }


    public void Melee(String combo) {
        int MeleeCombo = playerFunction.getMeleeCombo();

        if(playerFunction.getMeleeDelay() != 0) return;

        if(MeleeCombo==1) {

        }
    }

}
