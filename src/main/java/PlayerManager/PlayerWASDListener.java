package PlayerManager;

import PlayParticle.Rotate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class PlayerWASDListener {

    private static HashMap<Player, PlayerWASDListener> instance = new HashMap<>();

    private enum MOVE {
        D,
        S,
        A,
        W;
    }

    private Player player;
    private Location prev = null;
    private String move = "";

    private PlayerWASDListener(Player player) {
        this.player = player;
        this.prev = player.getLocation();
    }

    public static PlayerWASDListener getInstance(Player player) {
        if(!instance.containsKey(player)) instance.put(player, new PlayerWASDListener(player));
        return instance.get(player);
    }

    public void WASDListener() {

        Vector dir = player.getLocation().getDirection().multiply(new Vector(1, 0, 1));
        Vector v = prev.toVector().subtract(player.getLocation().toVector()).multiply(new Vector(1, 0, 1));

        for(int i=0; i<4; i++) {
            double yangle = Math.toRadians(90);
            double yaxiscos = Math.cos(yangle);
            double yaxissin = Math.sin(yangle);

            v = Rotate.rotateAroundAxisY(v, yaxiscos, yaxissin);

            try {
                double angle = dir.angle(v);
                if(angle>2.2) {
                    if(move.equals(MOVE.values()[i].name())) break;
                    move = MOVE.values()[i].name();
                    break;
                }
            }
            catch(Exception e) {

            }
        }

        Vector velocity = player.getVelocity();
        prev = player.getLocation();
    }

}
