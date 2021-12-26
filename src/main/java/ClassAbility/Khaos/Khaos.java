package ClassAbility.Khaos;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Khaos {

    private static Khaos khaos;

    private Khaos() {

    }

    public static Khaos getInstance() {
        if(khaos == null) khaos = new Khaos();
        return khaos;
    }

    public void SHIFTR(Player player) {

        /*
        단검이 있다면 단검으로 순간이동

         */

        if(KhaosMelee.getThrown(player) != null) {
            Location loc = KhaosMelee.getThrown(player).getLocation().add(0, 1, 0);
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0)
                    , 200, 0, 0, 0, 1);
            player.teleport(KhaosMelee.getThrown(player));

            Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"),
                    () -> player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, player.getLocation().add(0, 1, 0)
                            , 200, 0, 0, 0, 1), 1);


//            for(double i = 0; i<2 * Math.PI; i += Math.PI / 32) {
//
//                double x = 1.5 * Math.cos(i);
//                double y = 0;
//                double z = 1.5 * Math.sin(i);
//
//                Vector v = new Vector(x, y, z);
//                v = Rotate.rotateAroundAxisX(v, Math.cos(Math.toRadians(45)), Math.sin(Math.toRadians(45)));
//                loc.add(v);
//                for(int j=0; j<10; j++) {
//                    player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc
//                            , 1, 0.1, 0.1, 0.1, 0);
//                }
//
//                loc.subtract(v);
//            }


        }

    }


}
