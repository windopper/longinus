package itemtools.Map;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

public class Map implements Listener {

    private final static String mapName = "엄청난 지도!";

    @EventHandler
    public void Click(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        try {
            if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).equals(mapName)) {
                    MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
                    int scale = mapMeta.getMapView().getScale().ordinal();
                    if(scale < 4) {
                        player.getInventory().removeItem(itemStack);
                        getMap(player, MapView.Scale.values()[scale + 1]);
                    }

                    event.setCancelled(true);
                }
            }
            else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).equals(mapName)) {
                    MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
                    int scale = mapMeta.getMapView().getScale().ordinal();
                    if(scale > 0) {
                        player.getInventory().removeItem(itemStack);
                        getMap(player, MapView.Scale.values()[scale - 1]);
                    }

                    event.setCancelled(true);
                }
            }
        }
        catch(Exception e) {

        }
    }

    public void getMap(Player player, MapView.Scale scale) {
        ItemStack itemStack = new ItemStack(Material.FILLED_MAP, 1);
        MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
        mapMeta.setDisplayName(mapName);
        MapView mapView = Bukkit.createMap(player.getWorld());
        mapView.setScale(scale);
        mapView.setCenterX((int) player.getLocation().getX());
        mapView.setCenterZ((int) player.getLocation().getZ());


        mapMeta.setMapView(mapView);
        mapMeta.getMapView().setTrackingPosition(true);

        itemStack.setItemMeta(mapMeta);
        player.getInventory().addItem(itemStack);
    }

    public static void updateMap() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            try {
                if(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).equals(mapName)) {
                    MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
                    MapView mapView = mapMeta.getMapView();
                    mapView.setCenterX((int) player.getLocation().getX());
                    mapView.setCenterZ((int) player.getLocation().getZ());

                    itemStack.setItemMeta(mapMeta);
                }
            }
            catch(Exception e) {

            }
        }
    }
}
