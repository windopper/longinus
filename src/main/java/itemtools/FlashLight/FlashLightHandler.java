package itemtools.FlashLight;

import PlayParticle.Rotate;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import spellinteracttest.Main;

import java.util.*;

public class FlashLightHandler {

    private static HashMap<Player, FlashLightHandler> handlers = new HashMap<>();

    private Player player;
    private List<Location> preFlashLights = new ArrayList<>();
    private ItemStack itemStack;
    private boolean power = false;
    private boolean isRun = false;
    private FlashLightHandler(Player player) {
        this.player = player;
        this.itemStack = player.getInventory().getItemInMainHand();
    }
    public static FlashLightHandler getHandler(Player player) {
        if(!handlers.containsKey(player)) handlers.put(player, new FlashLightHandler(player));
        if(!handlers.get(player).isRun) {
            handlers.get(player).turnOn();
            handlers.get(player).isRun = true;
        }
        return handlers.get(player);
    }
    public void unregister() {
        handlers.remove(player);
    }
    public boolean getPower() {
        return power;
    }
    public void setPower(boolean power) {
        this.power = power;
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, 0.5f, 2);
    }
    public void turnOn() {

        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack item = player.getInventory().getItemInMainHand();
                for(Location location : preFlashLights) {
                    location.getWorld().getBlockAt(location).setBlockData(Material.AIR.createBlockData());
                }
                preFlashLights.clear();
                if(power) {

                    double pitch = player.getLocation().getPitch();
                    double yaw = player.getLocation().getYaw();

                    for(double i=0; i<50; i+=0.5) {
                        double x = 0;
                        double y = 0;
                        double z = i;
                        double rpitch = Math.toRadians(pitch);
                        double ryaw = Math.toRadians(yaw);
                        Vector v = new Vector(x, y, z);
                        v = Rotate.transform(v, ryaw, rpitch, 0);
                        Location loc = player.getEyeLocation();
                        loc.add(v);
                        if(!loc.getBlock().isEmpty()) {
                            loc.subtract(v);
                            Vector v_ = new Vector(x, y, z-1);
                            v_ = Rotate.transform(v_, ryaw, rpitch, 0);
                            loc.add(v_);
                            if(loc.getBlock().isEmpty()) {
                                setLight(loc, (int)(15- i / 50 * 15));
                                preFlashLights.add(loc);
                                break;
                            }
                        }
                    }
                }

                try {
                    if(!ChatColor.stripColor(item.getItemMeta().getDisplayName()).contains("손전등")
                            || item.getType() != Material.SPYGLASS) {
                        for(Location location : preFlashLights) {
                            location.getWorld().getBlockAt(location).setBlockData(Material.AIR.createBlockData());
                        }
                        preFlashLights.clear();
                        unregister();
                        cancel();
                        return;
                    }
                }
                catch(Exception e) {
                    for(Location location : preFlashLights) {
                        location.getWorld().getBlockAt(location).setBlockData(Material.AIR.createBlockData());
                    }
                    preFlashLights.clear();
                    unregister();
                    cancel();
                    return;
                }

            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }

    public Light setLight(Location location, int level) {
        Light light = (Light) Material.LIGHT.createBlockData();
        light.setLevel(level);
        location.getWorld().setBlockData(location, light);
//        location.getWorld().setBlockData(location, light);
        return light;
    }

    public void deleteBlock() {
        for(Location location : preFlashLights) {
            location.getWorld().getBlockAt(location).setBlockData(Material.AIR.createBlockData());
        }
    }
}
