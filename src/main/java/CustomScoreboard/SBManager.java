package CustomScoreboard;

import PlayerManager.PlayerManager;
import net.minecraft.EnumChatFormat;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_17_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Optional;

public class SBManager {

    private Player player;
    private PlayerManager pm;

    private final static Scoreboard LonginusScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    public ScoreboardManager scoreboardManager;
    //public net.minecraft.world.scores.Scoreboard scoreboard;
    public ScoreboardObjective objective;

    private SBManager(Player player) {
        this.player = player;
        this.pm = PlayerManager.getinstance(player);
        scoreboardManager = Bukkit.getScoreboardManager();
        player.setScoreboard(LonginusScoreboard);
    }

    public static SBManager getInstance(Player player) {
        if(PlayerManager.getinstance(player).sbManager == null) {
            PlayerManager.getinstance(player).sbManager = new SBManager(player);
        }
        return PlayerManager.getinstance(player).sbManager;
    }

    public static Scoreboard getLonginusScoreboard() {
        return LonginusScoreboard;
    }

    private void moveToOtherScoreboard(Scoreboard scoreboard, Player target) {
        target.setScoreboard(player.getScoreboard());
    }

    private Scoreboard getNewScoreboard() {
        return scoreboardManager.getNewScoreboard();
    }

    private String checkValidTeamName(String teamName) {
        return (teamName).length() > 10 ? (teamName).substring(0, 9) : teamName;
    }

    public boolean hasTeam(String teamName) {
        teamName = checkValidTeamName(teamName);
        return player.getScoreboard().getTeam(teamName) != null;
    }

    public Team getOrCreateTeam(String teamName) {
        final String fteamName = checkValidTeamName(teamName);
        if(hasTeam(teamName)) {
            return player.getScoreboard().getTeam(teamName);
        }
        else {
            player.setScoreboard(getNewScoreboard());
            Team team = player.getScoreboard().registerNewTeam(fteamName);
            return team;
        }
    }

    public void removeTeam(String teamName) {
        teamName = checkValidTeamName(teamName);
        if(hasTeam(teamName)) {
            player.getScoreboard().getTeam(teamName).unregister();
        }
    }

    public SBManager setTeamColor(String teamName, ChatColor color) {
        final String fteamName = checkValidTeamName(teamName);
        player.getScoreboard().getTeam(fteamName).setColor(color);
        return this;
    }

    public void addPlayerToTeam(String teamName, Player target) {
        teamName = checkValidTeamName(teamName);
        moveToOtherScoreboard(player.getScoreboard(), target);
        player.getScoreboard().getTeam(teamName).addEntry(target.getName());
    }

    public void removePlayerFromTeam(String teamName, Player target) {
        teamName = checkValidTeamName(teamName);
        if(hasTeam(teamName)) {
            if(getOrCreateTeam(teamName).getEntries().contains(target.getName())) {
                player.getScoreboard().getTeam(teamName).removeEntry(target.getName());
                target.setScoreboard(getNewScoreboard());
            }
        }
    }

    public void updateTeam(String teamName) {
        Team team = player.getScoreboard().getTeam(teamName);
        team.setColor(team.getColor());
    }

}
