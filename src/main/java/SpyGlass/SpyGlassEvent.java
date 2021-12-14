package SpyGlass;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpyGlassEvent implements Listener {


    @EventHandler
    public void SpyGlassEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {

            if(itemStack == null) return;
            if(itemStack.getType() != Material.SPYGLASS) return;
            SpyGlassManager.getinstance(player, itemStack);

            if(itemStack.getItemMeta() != null) return;
            if(itemStack.getItemMeta().getDisplayName() != null) return;
            if(itemStack.getItemMeta().getLore() != null) return;

            SpyGlassManager.getinstance(player, itemStack);
        }
    }

}
