package PlayParticle;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static PlayParticle.Rotate.rotateAroundAxisX;
import static PlayParticle.Rotate.rotateAroundAxisZ;

public class PlayParticle {

    public void Circle(Player player, double radius) {
        Location location = player.getLocation();
        //location.add(0, 2, 0);
        Vector vec = location.getDirection();

//        for(int i=0; i<50; i++) {
//            double angle = 2 * Math.PI * i / 50;
//            Vector offset = vec.clone().multiply(Math.cos(angle) * radius);
//
//            offset.setY(Math.sin(angle) * radius);
//            location.add(offset);
//
//            player.getWorld().spawnParticle(Particle.CRIT_MAGIC, location, 1, 0, 0, 0, 0);
//            // play particle at yourLocation
//            location.setX();
//            location.subtract(offset);
//        }

        new BukkitRunnable() {
            Location loc = player.getLocation().add(0, 1, 0);


            double t = 0;
            double r = 4;
            int angle = 0;

            public void run() {



                for(t=0; t<Math.PI * 2; t+=Math.PI/32)  {
                    double zangle = Math.toRadians(angle);
                    double zAxisCos = Math.cos(zangle);
                    double zAxisSin = Math.sin(zangle);

                    double xangle = Math.toRadians(angle/2);
                    double xAxisCos = Math.cos(xangle);
                    double xAxisSin = Math.sin(xangle);

                    double x = r * Math.cos(t);
                    double y = 0;
                    double z = r * Math.sin(t);
                    Vector v = new Vector(x, 0, z);
                    v = rotateAroundAxisZ(v, zAxisCos, zAxisSin);
                    v = rotateAroundAxisX(v, xAxisCos, xAxisSin);
                    loc.add(v.getX(), v.getY(), v.getZ());

                    loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 1));
                    loc.subtract(v.getX(), v.getY(), v.getZ());
                }

                if(angle > 1800) cancel();

                angle += 5;
            }

        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
    }

    public static void Circle2(Player player, double radius) {
        Location location = player.getLocation();

        new BukkitRunnable() {

            double angle = 0;
            double r = 0;

            @Override
            public void run() {

                for(double t = 0; t<Math.PI*2; t+=Math.PI/16) {
                    double zangle = Math.toRadians(45);
                    double zaxiscos = Math.cos(zangle);
                    double zaxissin = Math.sin(zangle);

                    double xangle = Math.toRadians(45);
                    double xaxiscos = Math.cos(xangle);
                    double xaxissin = Math.sin(xangle);

                    double x = r * Math.cos(t);
                    double y = 0;
                    double z = r * Math.sin(t);
                    Vector vec = new Vector(x, 0, z);

                    vec = Rotate.rotateAroundAxisX(vec, xaxiscos, xaxissin);
                    location.add(vec.getX(), vec.getY(), vec.getZ());
                    location.getWorld().spawnParticle(Particle.CRIT_MAGIC, location, 1, 0, 0, 0, 0 );
                    location.subtract(vec.getX(), vec.getY(), vec.getZ());
                }

                if(r>3) cancel();
                r+=0.5;


            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
    }


}
