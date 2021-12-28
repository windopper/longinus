package ClassAbility.Cheiron;

import PlayerManager.PlayerFunction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

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
            if(!PlayerManager.PlayerManager.getinstance(player).CurrentClass.equals("케이론")) return;
            if(event.getItem().getType() == Material.BOW && (event.getAction() == Action.RIGHT_CLICK_BLOCK
                    || event.getAction() == Action.RIGHT_CLICK_AIR)) {
                ItemStack bow = event.getItem();
                int slot = event.getPlayer().getInventory().getHeldItemSlot();

                event.setUseItemInHand(Event.Result.DENY);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {

                    event.setUseItemInHand(Event.Result.DENY);
                    event.setCancelled(true);
                    (new CheironMelee(player)).Melee("R");

                    player.getWorld().dropItem(player.getLocation(), bow);
                    player.getInventory().setItem(slot, bow);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {


                    }, 1);
                }, 5);
            }
        }
        catch(Exception e) {

        }
    }

    @EventHandler
    public void test1(PlayerItemConsumeEvent event) {
        Bukkit.broadcastMessage("...");
    }

    public void Melee(String combo) {
        int MeleeCombo = playerFunction.getMeleeCombo();

        if(playerFunction.getMeleeDelay() != 0) return;

        if(MeleeCombo==1) {

            SingleShot();
        }
    }

    private void SingleShot() {

        Bukkit.broadcastMessage("hi");
        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize();

    }

}
