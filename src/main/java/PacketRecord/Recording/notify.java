package PacketRecord.Recording;

import org.bukkit.entity.Player;

public class notify {
    public static void Warning(String s, Player player) {
        player.sendMessage("§5>> §e"+s);
    }
    public static void Successful(String s, Player player) {
        player.sendMessage("§5>> §a"+s);
    }
}
