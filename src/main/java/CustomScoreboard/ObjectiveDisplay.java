package CustomScoreboard;

import PlayerManager.PlayerManager;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.ScoreboardScore;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import spellinteracttest.Main;

import java.util.ArrayList;
import java.util.List;

public class ObjectiveDisplay {

    private Player player;
    private PlayerManager pm;

    private String title;
    private final List<String> lines = new ArrayList<>();

    private ObjectiveDisplay(Player player) {
        this.player = player;
        this.pm = PlayerManager.getinstance(player);
    }

    public static ObjectiveDisplay getBuilder(Player player) {
        PlayerManager pm = PlayerManager.getinstance(player);
        if(PlayerManager.getinstance(player).objectiveDisplay == null) pm.objectiveDisplay = new ObjectiveDisplay(player);
        return pm.objectiveDisplay;
    }

    private Scoreboard getScoreboard() {
        return player.getScoreboard();
    }

    private PlayerConnection getConnection() {
        return ((CraftPlayer) player).getHandle().b;
    }

    public ObjectiveDisplay setTitle(String title) {
        if(title.length()>16) title = title.substring(0, 15);
        this.title = title;
        return this;
    }
    public ObjectiveDisplay addLine(String line) {
        if(line.length()>40) line = line.substring(0, 39);
        lines.add(line);
        return this;
    }

    public void build() {
        net.minecraft.world.scores.Scoreboard scoreboard = new net.minecraft.world.scores.Scoreboard();
        ScoreboardObjective obj = scoreboard.registerObjective(title, IScoreboardCriteria.a
                , IChatBaseComponent.ChatSerializer.b(title),
                IScoreboardCriteria.EnumScoreboardHealthDisplay.a);

        obj.a().setDisplaySlot(1, obj);

        PacketPlayOutScoreboardObjective removePacket =
                new PacketPlayOutScoreboardObjective(obj, 1);

        PacketPlayOutScoreboardObjective createPacket =
                new PacketPlayOutScoreboardObjective(obj, 0);

        PacketPlayOutScoreboardObjective updatePacket =
                new PacketPlayOutScoreboardObjective(obj, 2);

        PacketPlayOutScoreboardDisplayObjective display =
                new PacketPlayOutScoreboardDisplayObjective(1, obj);

        getConnection().sendPacket(removePacket);
        getConnection().sendPacket(createPacket);
        getConnection().sendPacket(updatePacket);
        for(int i=0; i<lines.size(); i++) {
            getConnection().sendPacket(new PacketPlayOutScoreboardScore(ScoreboardServer.Action.a, title, lines.get(i), (lines.size())-i-1));
        }
        lines.clear();
        getConnection().sendPacket(display);
    }


}
