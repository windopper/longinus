package PlayParticle;

import org.bukkit.util.Vector;

public class Rotate {

    public static Vector rotateAroundAxisX(Vector v, double cos, double sin) {
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public static Vector rotateAroundAxisY(Vector v, double cos, double sin) {
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public static Vector rotateAroundAxisZ(Vector v, double cos, double sin) {
        double x = v.getX() * cos - v.getY() * sin;
        double y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
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
