package PlanetSelect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class planetDetect {

    private static planetDetect planetDetect;

    private final static List<Player> areaList = new ArrayList<>();
    private final Location longinusloc = new Location(Bukkit.getWorld("world"), -17.5, 54, -17.5);

    private planetDetect() {

    }

    public static planetDetect getinstance() {
        if(planetDetect == null) planetDetect = new planetDetect();
        return planetDetect;
    }

    public void detectArea() {

        mainGui mainGui = new mainGui();

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getWorld().getName().equals(longinusloc.getWorld().getName())) {
                double dist = player.getLocation().distance(longinusloc);
                if(dist<2) {
                    if(!areaList.contains(player)) mainGui.openGui(player);
                    Inventory inventory = player.getOpenInventory().getTopInventory();


                    areaList.add(player);
                }
                else {
                    areaList.remove(player);
                }
            }
            else {
                areaList.remove(player);
            }
        }

    }
}
