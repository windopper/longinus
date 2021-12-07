package PlanetSelect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class planetSelectEvent implements Listener {

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getView().getPlayer();
        String inventoryName = event.getView().getTitle();


        if(inventoryName.equals("이동할 행성을 선택하세요")) {

            int Slot = event.getRawSlot();

            InventoryClickPlanetSelect(Slot, player);

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void InventoryEvent(InventoryDragEvent event) {
        Player player = (Player) event.getView().getPlayer();
        String inventoryName = event.getView().getTitle();

        if(inventoryName.equals("이동할 행성을 선택하세요")) {

            event.setCancelled(true);
        }
    }

    public void InventoryClickPlanetSelect(int Slot, Player player) {

        player.closeInventory();

        if(Slot==10) {
            final Location gliese581cpoint = new Location(Bukkit.getWorld("gliese581c"), -197.5, 124, 237.5, -90, 0);
            player.teleport(gliese581cpoint);
        }

    }

}
