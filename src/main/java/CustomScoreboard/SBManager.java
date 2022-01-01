package CustomScoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;

public class SBManager {

    private final static HashMap<Player, SBManager> instance = new HashMap<>();

    private ScoreboardManager scoreboardManager;
    private Scoreboard scoreboard;
    private Objective objective;

    private Player player;

    private SBManager(Player player) {
        this.player = player;
        scoreboardManager = Bukkit.getScoreboardManager();
        scoreboard = scoreboardManager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("§e"+player.getName(), "dummy", "§e"+player.getName(), RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

    }

    public static SBManager getInstance(Player player) {
        if(!instance.containsKey(player)) instance.put(player, new SBManager(player));
        return instance.get(player);
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void removeinstance() {
        instance.remove(player);
    }


}
