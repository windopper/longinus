package itemtools.FlashLight;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import spellinteracttest.Main;

import java.util.HashSet;
import java.util.Set;

public class FlashLightListener implements Listener {

    private static Set<Player> cancel = new HashSet<>();

    @EventHandler
    public void itemClick(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if(item==null) return;
        if(item.getItemMeta() == null) return;
        if(ChatColor.stripColor(item.getItemMeta().getDisplayName()).contains("손전등")
        && item.getType() == Material.SPYGLASS) {
            event.setCancelled(true);
            if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if(cancel.contains(player)) return;
                else {
                    cancel.add(player);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                        cancel.remove(player);
                    }, 1);
                }
                if(FlashLightHandler.getHandler(player).getPower()) {
                    FlashLightHandler.getHandler(player).setPower(false);
                }
                else {
                    FlashLightHandler.getHandler(player).setPower(true);
                }
            }
        }
    }

    @EventHandler
    public void unregister(PlayerQuitEvent event) {
        FlashLightHandler.getHandler(event.getPlayer()).unregister();
    }

    public ItemStack getFlashLight() {
        ItemStack itemStack = new ItemStack(Material.SPYGLASS, 1);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.of("#BDECB6") + "손전등");
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
