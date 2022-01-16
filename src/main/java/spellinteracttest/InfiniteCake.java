package spellinteracttest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Cake;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class InfiniteCake {

    private static InfiniteCake infiniteCake;
    boolean turnon = false;

    private InfiniteCake() {

    }

    public static InfiniteCake getInstance() {
        if(infiniteCake == null) infiniteCake = new InfiniteCake();
        return infiniteCake;
    }

    public void Set(Player player) {
        final Location location = player.getLocation().add(0, 1, 0);

        Cake cake = (Cake) Material.CAKE.createBlockData();
        turnon = true;


        new BukkitRunnable() {
            @Override
            public void run() {
                location.getBlock().setType(Material.CAKE);
                if(!turnon) cancel();
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);

    }

    public void stop() {
        turnon = false;
    }
}
