package CustomScoreboard;

import PlayerManager.PlayerManager;
import com.mojang.brigadier.Message;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.network.chat.ChatBaseComponent;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.ScoreboardScore;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import java.awt.*;

public class ObjectiveDisplay {

    private Player player;
    private PlayerManager pm;

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

    public ObjectiveDisplay setTitle(String var) {

        //Objective objective = getScoreboard().registerNewObjective(var, var, "dummy", RenderType.INTEGER);
        ScoreboardObjective scoreboardObjective = ((CraftScoreboard) getScoreboard()).getHandle()
                .registerObjective(var, IScoreboardCriteria.a
                , IChatBaseComponent.ChatSerializer.b(var),
                        IScoreboardCriteria.EnumScoreboardHealthDisplay.a);

        scoreboardObjective.a().setDisplaySlot(1, scoreboardObjective);

//        for (ScoreboardScore score : scoreboardObjective.a().getScoresForObjective(scoreboardObjective)) {
//            score.c();
//            scoreboardObjective.a().handleScoreChanged(score);
//        }

        //scoreboardObjective.a().setDisplaySlot(1, scoreboardObjective);
//        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
//        objective.setDisplayName(var);
//        objective.getScore("Nice").setScore(1);
//
//        //ScoreboardObjective scoreboardObjective = ((CraftScoreboard) getScoreboard()).getHandle().getObjective(var);
//        objective.unregister();

        PacketPlayOutScoreboardObjective packetPlayOutScoreboardObjective =
                new PacketPlayOutScoreboardObjective(scoreboardObjective, 0);

        PacketPlayOutScoreboardDisplayObjective packetPlayOutScoreboardDisplayObjective =
                new PacketPlayOutScoreboardDisplayObjective(1, scoreboardObjective);



//        PacketPlayOutScoreboardObjective packetPlayOutScoreboardObjective =
//                new PacketPlayOutScoreboardObjective(scoreboardObjective, 0);

        //getConnection().sendPacket(packetPlayOutScoreboardDisplayObjective);
        getConnection().sendPacket(packetPlayOutScoreboardObjective);

        return this;
    }


}
