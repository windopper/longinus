package CustomScoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;

public class SBManager {

    private final static HashMap<Player, SBManager> instance = new HashMap<>();

    private ScoreboardManager scoreboardManager;
    private Scoreboard scoreboard;

    private Player player;

    private SBManager(Player player) {
        this.player = player;
        scoreboardManager = Bukkit.getScoreboardManager();
        scoreboard = scoreboardManager.getNewScoreboard();
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
