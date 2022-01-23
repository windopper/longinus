package CustomScoreboard;

import PlayerManager.PlayerManager;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SBManager {

    private Player player;
    private PlayerManager pm;

    public ScoreboardManager scoreboardManager;
    public net.minecraft.world.scores.Scoreboard scoreboard;
    public ScoreboardObjective objective;

    public SBManager(Player player) {
        this.player = player;
        this.pm = PlayerManager.getinstance(player);
        scoreboardManager = Bukkit.getScoreboardManager();
        scoreboard = ((CraftScoreboard) scoreboardManager.getNewScoreboard()).getHandle();
        scoreboard.createTeam(player.getName());
    }

    public static SBManager getInstance(Player player) {
        if(PlayerManager.getinstance(player).sbManager == null) {
            PlayerManager.getinstance(player).sbManager = new SBManager(player);
        }
        return PlayerManager.getinstance(player).sbManager;
    }

    public boolean hasTeam(String teamName) {
        return scoreboard.getTeams()
                .stream()
                .map(ScoreboardTeam::getName)
                .toList()
                .contains(teamName);
    }

    public ScoreboardTeam getOrCreateTeam(String teamName) {
        if(hasTeam(teamName)) {
            return scoreboard.getTeams()
                    .stream()
                    .filter(t->t.getName().equals(teamName))
                    .toList()
                    .get(0);
        }
        else {
            return scoreboard.createTeam(teamName);
        }
    }

    public SBManager setTeamColor(String teamName, ChatColor color) {
        scoreboard.getTeams()
                .stream()
                .filter(t->t.getName().equals(teamName))
                .toList()
                .get(0)
                .setColor(EnumChatFormat.valueOf(color.name()));
        return this;
    }

    public void addPlayerToTeam(String teamName, Player target) {
        scoreboard.addPlayerToTeam(target.getName(), getOrCreateTeam(teamName));
    }

}
