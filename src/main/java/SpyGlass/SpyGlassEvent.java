package SpyGlass;

import net.md_5.bungee.api.ChatColor;
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
            //SpyGlassManager.getinstance(player, itemStack);
            if(itemStack.getItemMeta() == null) return;
            if(!ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).equals("고성능 스캔망원경")) return;
            if(itemStack.getItemMeta().getLore() == null) return;

            SpyGlassManager.getinstance(player, itemStack);
        }
    }

}
