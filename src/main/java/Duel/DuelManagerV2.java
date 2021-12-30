package Duel;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DuelManagerV2 {

    private final static ConcurrentHashMap<Player, DuelManagerV2> instances = new ConcurrentHashMap<>();

    private List<Player> players = new ArrayList<>();
    private int second = 0;
    private ConcurrentHashMap<Player, Integer> duelRequests = new ConcurrentHashMap<>();
    private boolean duelEnableStatus = false;
    private boolean duelReadyStatus = false;

    private DuelManagerV2(Player player) {
        this.players.add(player);
        instances.put(player, this);
    }

    public static DuelManagerV2 getInstance(Player player) {
        if(!instances.containsKey(player)) instances.put(player, new DuelManagerV2(player));
        return instances.get(player);
    }

    public final static boolean checkInSameDuel(Player p1, Player p2) {
        try {
            if(instances.get(p1) == instances.get(p2)) {
                if(instances.get(p1).duelEnableStatus)
                    return true;
            }
            else return false;
        }
        catch(Exception e) {
            return false;
        }
        return false;
    }

    public final static boolean checkInDuel(Player player) {
        if(instances.containsKey(player)) {
            if(instances.get(player).duelEnableStatus) return true;
        }
        return false;
    }

    public final void removeDuelManager(Player player) {
        if(instances.containsKey(player)) instances.remove(player);
    }

    public final void sendDuelRequest(Player player, Player target) {

        if(duelEnableStatus) {
            player.sendMessage("§5>> §3PVP도중에 PVP신청 명령어를 사용할 수 없습니다");
            return;
        }
        else if(instances.containsKey(target)) {
            if(instances.get(target) != this) {
                if(instances.get(target).duelReadyStatus) {
                    player.sendMessage("§5>> §b"+target.getName()+"§3님은 이미 PVP중입니다");
                    return;
                }
                else {

                }
            }
        }

    }

    public static void Loop() {
        for(DuelManagerV2 instance : instances.values()) {

        }
    }



}
