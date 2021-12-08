package Party;

import DynamicData.PlayerHealth;
import UserData.UserManager;
import UserData.UserStatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PartyManager {

    private static PartyManager PartyManager;

    private final static HashMap<Player, PartyManager> partyInstance = new HashMap<>();
    private final static HashMap<Player, PartyManager> partyRequest = new HashMap<>();
    private final static HashMap<Player, String> objectiveString = new HashMap<>();

    private final ScoreboardManager scoreboardManager = Bukkit.getServer().getScoreboardManager();
    private final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
    private final Objective objective = scoreboard.registerNewObjective("objective", "dummy", "objective", RenderType.INTEGER);


    private PartyManager party = this;

    private Player master;
    private List<Player> members = new ArrayList<>();

    private PartyManager() {

    }

    private PartyManager(Player player) {
        this.master = player;
        members.add(player);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    public List<Player> getMembers() {
        return this.members;
    }

    public static PartyManager getinstance() {
        if(PartyManager == null) PartyManager = new PartyManager();
        return PartyManager;
    }

    public void removeinstance(Player player) {

        if(partyInstance.containsKey(player)) {
            partyInstance.get(player).QuitParty(player);
        }

        partyInstance.remove(player);
        partyRequest.remove(player);
        objectiveString.remove(player);
    }

    public void partyMemberList(Player player) {
        boolean playerHasParty = partyInstance.containsKey(player);
        if(playerHasParty == false) {
            player.sendMessage("파티가 없습니다");
            return;
        }

        String names = "";

        for(Player p : getMembers()) {
            names += " " + p.getName();
        }

        player.sendMessage(names);

    }

    public void deleteSideBar(Player player) {
        if(!partyInstance.containsKey(player)) return;
        PartyManager partyManager = partyInstance.get(player);
        partyManager.scoreboard.resetScores(partyManager.getObjectiveString(player));
        player.setScoreboard(scoreboardManager.getNewScoreboard());
    }

    public void setMemberDeath(Player player) {
        if(partyInstance.containsKey(player) == false) return;
        PartyManager partyManager = partyInstance.get(player);
        partyManager.scoreboard.resetScores(player.getName());
        partyManager.objective.getScore("§7§m"+player.getName()).setScore(0);
    }

    public void setMemberAlive(Player player) {
        if(partyInstance.containsKey(player) == false) return;
        PartyManager partyManager = partyInstance.get(player);

        Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), ()-> {

//            partyManager.scoreboard.resetScores("§7§m"+player.getName());
//            partyManager.objective.getScore(player.getName()).setScore(0);
//            player.setScoreboard(scoreboard);

            }, 40);
    }

    public void createParty(Player player) {
        if(!partyInstance.containsKey(player)) {
            player.sendMessage("파티가 성공적으로 생성되었습니다");
            partyInstance.put(player, new PartyManager(player));
        }
        else {
            player.sendMessage("이미 파티가 있습니다!");
        }
    }

    public void inviteParty(Player commander, Player target) {

        boolean CommanderHasParty = partyInstance.containsKey(commander);
        boolean TargetHasParty = partyInstance.containsKey(target);

        if(commander.getName().equals(target.getName())) {
            commander.sendMessage("자기 자신을 초대할 수 없습니다");
            return;
        }

        if(TargetHasParty) {
            commander.sendMessage("해당 유저는 이미 파티가 있습니다");
        }
        else if(CommanderHasParty && !TargetHasParty) {
            partyRequest.put(target, partyInstance.get(commander));
            commander.sendMessage(target.getName()+"님에게 파티 초대 메시지를 보냈습니다");
            target.sendMessage(commander.getName()+"님이 당신을 파티로 초대하였습니다");
        }
        else if(!CommanderHasParty && !TargetHasParty) {
            createParty(commander);
            partyRequest.put(target, partyInstance.get(commander));
            commander.sendMessage(target.getName()+"님에게 파티 초대 메시지를 보냈습니다");
            target.sendMessage(commander.getName()+"님이 당신을 파티로 초대하였습니다");
        }

    }

    public void JoinParty(Player commander) {

        Boolean requestBoolean = partyRequest.containsKey(commander);
        if(requestBoolean == false) {
            commander.sendMessage("파티 초대를 받지 않았습니다");
            return;
        }
        else {
            partyInstance.put(commander, partyRequest.get(commander));
            partyInstance.get(commander).members.add(commander);

            partyRequest.remove(commander);

            for(Player members : partyInstance.get(commander).getMembers()) {
                members.sendMessage(commander.getName()+"님이 파티에 참가하였습니다");
            }
        }

        //partyInstance.get(commander).objective.getScore(getObjectiveString(commander)).setScore(0);
        commander.setScoreboard(partyInstance.get(commander).scoreboard);

    }

    public void QuitParty(Player player) {

        boolean hasParty = partyInstance.containsKey(player);

        if(!hasParty) {
            player.sendMessage("이미 파티를 떠났습니다");
            return;
        }

        PartyManager PM = partyInstance.get(player);

        if(!PM.master.getName().equals(player.getName())) {

            if(PM.members.size()==1) {
                deleteSideBar(player);
                partyInstance.remove(player);
                return;
            }


            ChangeMaster(player, PM.members.get(0));
        }

        PM.members.remove(player);
        deleteSideBar(player);
        partyInstance.remove(player);
        player.sendMessage("당신은 파티에서 떠났습니다");


    }

    public void ChangeMaster(Player commander, Player target) {

        boolean commanderHasParty = partyInstance.containsKey(commander);
        boolean targetHasParty = partyInstance.containsKey(target);

        if(!commanderHasParty) {
           commander.sendMessage("당신은 현재 파티에 소속되어 있지 않습니다");
            return;
        }
        else if(!targetHasParty) {
            commander.sendMessage(target.getName()+"님은 파티에 소속되어 있지 않습니다");
            return;
        }

        PartyManager commanderParty = partyInstance.get(commander);
        PartyManager targetParty = partyInstance.get(target);

        if(commanderParty != targetParty) {
            commander.sendMessage(target.getName()+"님은 당신과 같은 파티가 아닙니다");
            return;
        }

        if(!commanderParty.master.getName().equals(commander.getName())) {
            commander.sendMessage("파티장만 사용할 수 있는 권한입니다");
            return;
        }

        commanderParty.master = target;

        SendMessageToMembers(commanderParty, target.getName()+"님이 파티장이 되었습니다");


    }

    public void KickMember(Player commander, Player target) {

        if(commander.getName().equals(target.getName())) {
            commander.sendMessage("자기 자신을 추방할 수 없습니다");
            return;
        }

        boolean commanderHasParty = partyInstance.containsKey(commander);
        boolean targetHasParty = partyInstance.containsKey(target);
        if(!commanderHasParty) {
            commander.sendMessage("당신은 현재 파티에 소속되어 있지 않습니다");
            return;
        }
        else if(!targetHasParty) {
            commander.sendMessage(target.getName()+"님은 파티에 소속되어 있지 않습니다");
            return;
        }

        PartyManager commanderParty = partyInstance.get(commander);
        PartyManager targetParty = partyInstance.get(target);

        if(commanderParty != targetParty) {
            commander.sendMessage(target.getName()+"님은 당신과 같은 파티가 아닙니다");
            return;
        }
        if(!commanderParty.master.getName().equals(commander.getName())) {
            commander.sendMessage("파티장만 사용할 수 있는 권한입니다");
            return;
        }

        commanderParty.members.remove(target);
        deleteSideBar(target);
        partyInstance.remove(target);

        SendMessageToMembers(commanderParty, target.getName()+"님이 파티에서 추방당했습니다");
    }



    public void SendMessageToMembers(PartyManager party, String Contents) {
        for(Player members : party.getMembers()) {
            members.sendMessage(Contents);
        }
    }

    public void partyObjectiveLoop() {

        for(Player p : Bukkit.getOnlinePlayers()) {

            if(partyInstance.containsKey(p)) {

                PartyManager partyManager = partyInstance.get(p);
                partyManager.scoreboard.resetScores(objectiveString.get(p));
                objectiveString.put(p, getObjectiveString(p));
                partyManager.objective.getScore(getObjectiveString(p)).setScore(0);

            }
            objectiveString.put(p, getObjectiveString(p));
        }
    }

    private String getObjectiveString(Player player) {

        int MaxHealth = UserManager.getinstance(player).Health;
        int CurrentHealth = PlayerHealth.getinstance(player).getCurrentHealth();
        int CurrentShield = PlayerHealth.getinstance(player).getCurrentShield();

        String Shield = "§5§l§m[🛡]";
        if(CurrentShield == 0) Shield = "§7§l§m[🛡]";

        String health = "§6["+CurrentHealth+"]";
        List<Character> arrlist = new ArrayList<>();
        char[] arr = health.toCharArray();
        for(int i=0; i<arr.length; i++) {
            arrlist.add(arr[i]);
        }
        double rate = (double) CurrentHealth / (double) MaxHealth;
        int index = (int)(arr.length * rate);
        if(index<=1) index = 2;
        arrlist.add(index, '7');
        arrlist.add(index, '§');

        String objectiveString = "";

        for(char ch : arrlist) {
            objectiveString += ch;
        }

        objectiveString += "§r "+Shield+"§r ";
        int charlimit = 40 - objectiveString.length();

        String playerName = player.getName().substring(0, player.getName().length());
        if(player.getName().length() > charlimit)
            playerName = player.getName().substring(0, charlimit);

        objectiveString += playerName;

        return objectiveString;
    }




}
