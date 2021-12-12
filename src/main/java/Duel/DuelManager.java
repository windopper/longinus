package Duel;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class DuelManager {

    private final static HashMap<Player, DuelManager> duelInstance = new HashMap<>();

    private Player me;
    private Player target;
    private int second = 0;
    private int requestTime = 0;
    private boolean duelEnableStatus = false;
    private boolean pvpStatus = false;

    private DuelManager(Player player) {
        this.me = player;
    }

    public final static DuelManager getDuelManager(Player player) {
        if(!duelInstance.containsKey(player)) duelInstance.put(player, new DuelManager(player));
        return duelInstance.get(player);
    }
    public final static boolean checkInSameDuel(Player player1, Player player2) {
        if(!duelInstance.containsKey(player1) || !duelInstance.containsKey(player2)) {
            return false;
        }

        DuelManager player1DuelManager = duelInstance.get(player1);
        DuelManager player2DuelManager = duelInstance.get(player2);

        String player1sName = player1.getName();
        String player2sName = player2.getName();

        if(player1DuelManager.target.getName().equals(player2sName)
                && player2DuelManager.target.getName().equals(player1sName)) {

            if(player1DuelManager.pvpStatus == true && player2DuelManager.pvpStatus == true) {
                return true;
            }
            else {
                return false;
            }
        }
        else return false;

    }

    public final static boolean checkInDuel(Player player) {

        if(!duelInstance.containsKey(player)) {
            return false;
        }

        DuelManager playerDuelManager = duelInstance.get(player);

        if(playerDuelManager.duelEnableStatus) return true;
        else return false;

    }

    public final void removeDuelManager(Player player) {
        if(duelInstance.containsKey(player)) duelInstance.remove(player);
    }

    public final void sendDuelRequest(Player commander, Player target) {

        DuelManager targetDuelManager = getDuelManager(target);

        if(duelEnableStatus == true) {
            commander.sendMessage("§5>> §3PVP도중에 PVP신청 명령어를 사용할 수 없습니다");
            return;
        }
        if(targetDuelManager.duelEnableStatus == true) {
            commander.sendMessage("§5>> §b"+target.getName()+"§3님은 이미 PVP중입니다");
            return;
        }

        // target플레이어가 이미 자신을 PVP상대로 가지고 있으면
        if(targetDuelManager.target != null) {
            if(targetDuelManager.target.getName().equals(commander.getName())) {

                this.target = target;
                runDuel(commander, target);

                return;
            }
        }


        target.sendMessage("§5>> §b"+commander.getName()+"§3님이 당신에게 PVP를 신청하였습니다.");
        TextComponent component = new TextComponent(TextComponent.fromLegacyText("§5>> §6여기§3를 클릭하여 수락해 주세요"));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel "+commander.getName()));
        target.sendMessage(component);
        commander.sendMessage("§5>> §b"+target.getName()+"§3님에게 PVP를 신청하였습니다");

        this.target = target;

    }

    private final void runDuel(Player commander, Player target) {

        DuelManager commanderDuelManager = getDuelManager(commander);
        DuelManager targetDuelManager = getDuelManager(target);

        if(commanderDuelManager.target.getName().equals(target.getName())
                && targetDuelManager.target.getName().equals(commander.getName())) {

            commanderDuelManager.duelEnableStatus = true;
            targetDuelManager.duelEnableStatus = true;
        }

    }

    public final void setLoser(Player loser) {

        if(checkInSameDuel(loser, this.target)) {
            loser.sendMessage("§5>> §b"+this.target.getName()+"§3님이 승리하였습니다");
            this.target.sendMessage("§5>> §b"+this.target.getName()+"§3님이 승리하였습니다");

            removeDuelManager(loser);
            removeDuelManager(this.target);
        }

    }

    public static final void duelLoop() {

        for(DuelManager duels : duelInstance.values()) {

            final Player player = duels.me;
            final Player target = duels.target;
            int second = duels.second;

            if(duels.duelEnableStatus == true) {

                if(second == 0) {
                    player.sendMessage("§5>> §ePVP가 10초뒤에 시작됩니다");
                }
                else if(second>=80 && second<180) {
                    if(second%20 == 0) player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                    player.sendTitle("§9"+Integer.toString((int)((200-second)/20)), "§c"+player.getName()+" VS "+target.getName(), 0, 20, 0);
                }
                else if(second == 180) {
                    player.sendTitle("§c시작!", "", 0, 20, 10);
                    duels.pvpStatus = true;
                }
                else if(second > 180) {

                }


                duels.second++;

            }
            else {
                if(duels.requestTime == 600) {
                    duels.removeDuelManager(player);
                }
                duels.requestTime++;
            }


        }

    }



}
