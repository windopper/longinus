package PlayParticle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static PlayParticle.Rotate.rotateAroundAxisX;
import static PlayParticle.Rotate.rotateAroundAxisZ;

public class PlayParticle {

    private final Particle particle;
    private final Particle.DustOptions dustOptions;

    public PlayParticle(Particle particle) {
        this.particle = particle;
        this.dustOptions = null;
    }

    public PlayParticle(Particle particle, Particle.DustOptions dustOptions) {
        this.particle = particle;
        this.dustOptions = dustOptions;
    }


    public void Circle(Player player, double radius) {

        new BukkitRunnable(){

            double yaw = Math.toRadians(player.getLocation().getYaw());
            double pitch = Math.toRadians(player.getLocation().getPitch());

            Location loc = player.getEyeLocation();
            Vector vec = loc.getDirection().normalize();
            int amount = 40;
            float radius = 1f;
            int count = 0;
            public void run() {
                count++;
                loc.add(vec);
                for (int i = 0; i < amount; i++) {
                    double angle, x, y, z;
                    angle = 2 * Math.PI * i / amount;

                    z = 0;
                    y = radius * Math.cos(Math.PI / 20 * i);
                    x = radius * Math.sin(Math.PI / 20 * i);
                    Vector v = new Vector(x, y, z);
                    v = transform(v, yaw, pitch, 0);

                    //loc.add(x, y, z);
                    loc.add(v);
                    Particle(loc);
                    loc.subtract(v);
                    //loc.subtract(x, y, z);
                }
                if (count>30){
                    this.cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
    }



    public void Circle1(Player player, double radius) {

        new BukkitRunnable() {
            Location loc = player.getLocation().add(0, 1.5, 0);
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

                    double x = radius * Math.cos(t);
                    double y = 0;
                    double z = radius * Math.sin(t);
                    Vector v = new Vector(x, 0, z);
                    v = rotateAroundAxisZ(v, zAxisCos, zAxisSin);
                    v = rotateAroundAxisX(v, xAxisCos, xAxisSin);
                    loc.add(v.getX(), v.getY(), v.getZ());

                    loc.getWorld().spawnParticle(Particle.CRIT, loc, 1, 0, 0, 0, 0);
                    loc.subtract(v.getX(), v.getY(), v.getZ());
                }

                if(angle > 1800) cancel();

                angle += 5;
            }

        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
    }

    public void zoomcritsmall(Player player) {

        Location location = player.getEyeLocation();
        Vector vec = location.getDirection().normalize().multiply(0.7);
        Vector vec2 = location.getDirection().normalize().multiply(3.5);
        location.add(vec2);
        int Max = 7;

        new BukkitRunnable() {
            int t = 3;
            @Override
            public void run() {

                for(int i=3; i<=t; i++) {
                    location.subtract(vec);
                }

                Verticalcircle(player, location, (double)t/5);


                for(int i=3; i<=t; i++) {
                    location.add(vec);
                }

                if(t>Max) cancel();
                t++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
    }

    public void CircleVerticalImpact1(Player player) {

        Location location = player.getEyeLocation();


        new BukkitRunnable() {

            int t = 0;

            @Override
            public void run() {

                Verticalcircle(player, location, ((double)t+1)/1.2);

                if(t>5) cancel();
                t++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

    }

    public void CirCleHorizontalImpact1(Player player) {

        Location location = player.getEyeLocation();

        new BukkitRunnable() {

            int t = 0;

            @Override
            public void run() {

                HorizontalCircle(player, location, ((double)t+1)/1.2);

                if(t>5) cancel();
                t++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
    }


    public void Verticalcircle(Player player, Location location, double radius) {
        location.setPitch(0);
        Vector vec = location.getDirection().normalize();

        for(int i=0; i<50; i++) {
            double angle = 2 * Math.PI * i / 50;
            Vector offset = vec.clone().multiply(Math.cos(angle) * radius);
            offset.setY(Math.sin(angle) * radius);

            double yangle = Math.toRadians(90);
            double cosx = Math.cos(yangle);
            double sinx = Math.sin(yangle);

            offset = Rotate.rotateAroundAxisY(offset, cosx, sinx);


            location.add(offset);
            Particle(location);
            // play particle at yourLocation
            location.subtract(offset);
        }
    }

    public void HorizontalCircle(Player player, Location location, double radius) {

        for(double i = 0; i<Math.PI * 2; i+=Math.PI /32) {

            double x = Math.cos(i) * radius;
            double y = location.getY();
            double z = Math.sin(i) * radius;


            location.add(x, 0, z);
            Particle(location);
            location.subtract(x, 0, z);

        }

    }

    private void Particle(Location location) {
        if (dustOptions == null)
            location.getWorld().spawnParticle(this.particle, location, 1, 0, 0, 0, 0);
        else
            location.getWorld().spawnParticle(this.particle, location, 1, 0, 0, 0, 0, dustOptions);
    }


    public void InCircle(Player player, Location location, double radius) {
        location.setPitch(0);
        Vector vec = location.getDirection().normalize();

        new BukkitRunnable() {

            double angle = player.getLocation().getYaw();
            double r = 0;

            @Override
            public void run() {

                for(int i=0; i<50; i++) {
                    double angle = 2 * Math.PI * i / 50;
                    Vector offset = vec.clone().multiply(Math.cos(angle) * radius);
                    offset.setY(Math.sin(angle) * radius);

                    double yangle = Math.toRadians(90);
                    double cosx = Math.cos(yangle);
                    double sinx = Math.sin(yangle);

                    offset = Rotate.rotateAroundAxisY(offset, cosx, sinx);


                    location.add(offset);
                    player.getWorld().spawnParticle(Particle.CRIT, location, 1, 0, 0, 0, 0);
                    // play particle at yourLocation
                    location.subtract(offset);
                }

                if(r>3) cancel();
                r+=0.5;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
    }


    /**
     * Intrinsic matrix rotation for MC coordinate system
     * @author Michel_0
     * @param direction The directional vector to be transformed
     * @param yaw The desired yaw angle for rotation
     * @param pitch The desired pitch angle for rotation
     * @param roll The desired roll angle for rotation
     * @return The transformed directional vector
     */
    public static Vector transform(Vector direction, double yaw, double pitch, double roll) {
        double[] vec = new double[] { direction.getX(), direction.getY(), direction.getZ() };
        direction.setX(
                vec[0] * (Math.cos(-yaw) * Math.cos(roll) + Math.sin(-yaw) * Math.sin(pitch) * Math.sin(roll))
                        + vec[1] * (Math.cos(roll) * Math.sin(-yaw) * Math.sin(pitch) - Math.cos(-yaw) * Math.sin(roll))
                        + vec[2] * Math.cos(pitch) * Math.sin(-yaw));
        direction.setY(
                vec[0] * Math.cos(pitch) * Math.sin(roll)
                        + vec[1] * Math.cos(pitch) * Math.cos(roll)
                        - vec[2] * Math.sin(pitch));
        direction.setZ(
                vec[0] * (Math.cos(-yaw) * Math.sin(pitch) * Math.sin(roll) - Math.cos(roll) * Math.sin(-yaw))
                        + vec[1] * (Math.cos(-yaw) * Math.cos(roll) * Math.sin(pitch) + Math.sin(-yaw) * Math.sin(roll))
                        + vec[2] * Math.cos(-yaw) * Math.cos(pitch));
        return direction;
    }

}
