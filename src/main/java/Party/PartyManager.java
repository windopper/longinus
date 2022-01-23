package Party;

import PlayerManager.PlayerHealthShield;
import PlayerManager.PlayerManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PartyManager {

    public int XPLoop = 0;

    private static PartyManager PartyManager;

    private final static HashMap<Player, PartyManager> partyInstance = new HashMap<>();
    private final static HashMap<Player, PartyManager> partyRequest = new HashMap<>();
    private final static HashMap<Player, String> objectiveString = new HashMap<>();

    private final ScoreboardManager scoreboardManager = Bukkit.getServer().getScoreboardManager();
    private final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
    private final Objective objective = scoreboard.registerNewObjective("§e파티 정보", "dummy", "§e파티 정보", RenderType.INTEGER);
    private final Team team = scoreboard.registerNewTeam("party");

    private PartyManager party = this;

    private Player master;
    private List<Player> members = new ArrayList<>();
    private boolean glowingDelay = false;

    private final String XPAlarmReady = "§d파티 경험치 집계중..";
    private String XPAlarm = "";
    private int PartyXP = 0;

    private PartyManager() {

    }

    private PartyManager(Player player) {

        //init Partymanager
        this.master = player;
        members.add(player);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);

        objective.getScore(" ").setScore(1);
        objective.getScore(XPAlarm).setScore(2);
        team.setColor(ChatColor.GREEN);
    }

    public List<Player> getMembers() {
        return this.members;
    }

    public static PartyManager getParty(Player player) {
        if(partyInstance.containsKey(player)) return partyInstance.get(player);
        return null;
    }

    public static PartyManager getinstance() {
        if(PartyManager == null) PartyManager = new PartyManager();
        return PartyManager;
    }

    public void addPartyXP(int partyXP) {
        this.PartyXP += partyXP;
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

    public Player getMaster() {
        return this.master;
    }

    public void deleteSideBar(Player player) {
        if(!partyInstance.containsKey(player)) return;
        PartyManager partyManager = partyInstance.get(player);
        partyManager.scoreboard.resetScores(partyManager.getObjectiveString(player));
        player.setScoreboard(scoreboardManager.getNewScoreboard());
        partyManager.team.removeEntry(player.getName());
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
            player.sendMessage(partyStandardMessage("파티가 성공적으로 생성되었습니다"));
            partyInstance.put(player, new PartyManager(player));
        }
        else {
            player.sendMessage(partyStandardMessage("이미 파티가 있습니다!"));
        }
    }

    public void inviteParty(Player commander, Player target) {

        boolean CommanderHasParty = partyInstance.containsKey(commander);
        boolean TargetHasParty = partyInstance.containsKey(target);

        if(commander.getName().equals(target.getName())) {
            commander.sendMessage("§5>> §e자기 자신을 초대할 수 없습니다");
            return;
        }

        if(TargetHasParty) {
            commander.sendMessage("§5>> §e해당 유저는 이미 파티가 있습니다");
        }
        else if(CommanderHasParty && !TargetHasParty) {
            partyRequest.put(target, partyInstance.get(commander));
            commander.sendMessage("§5>> §6"+target.getName()+"§e님에게 파티 초대 메시지를 보냈습니다");

            target.sendMessage("§5>> §6"+commander.getName()+"§e님이 당신을 파티로 초대하였습니다");
            TextComponent component = new TextComponent(TextComponent.fromLegacyText("§5>> §b§n여기§r§e를 클릭하여 수락하거나 §b§n/파티 참가§r§e 명령어를 입력하세요"));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/파티 참가"));
            target.spigot().sendMessage(component);

        }

        else if(!CommanderHasParty && !TargetHasParty) {
            createParty(commander);
            partyRequest.put(target, partyInstance.get(commander));
            commander.sendMessage("§5>> §6"+target.getName()+"§e님에게 파티 초대 메시지를 보냈습니다");
            target.sendMessage("§5>> §6"+commander.getName()+"§e님이 당신을 파티로 초대하였습니다");
            TextComponent component = new TextComponent(TextComponent.fromLegacyText("§5>> §b§n여기§r§e를 클릭하여 수락하거나 §b§n/파티 참가§r§e 명령어를 입력하세요"));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/파티 참가"));
            target.spigot().sendMessage(component);
        }

    }

    public void JoinParty(Player commander) {

        Boolean requestBoolean = partyRequest.containsKey(commander);
        if(requestBoolean == false) {
            commander.sendMessage(partyStandardMessage("파티 초대를 받지 않았습니다"));
            return;
        }
        else {
            partyInstance.put(commander, partyRequest.get(commander));
            partyInstance.get(commander).members.add(commander);

            partyRequest.remove(commander);

            for(Player members : partyInstance.get(commander).getMembers()) {
                members.sendMessage("§5>> §6"+commander.getName()+"§e님이 파티에 참가하였습니다");
            }
        }

        //partyInstance.get(commander).objective.getScore(getObjectiveString(commander)).setScore(0);
        commander.setScoreboard(partyInstance.get(commander).scoreboard);

    }

    public void QuitParty(Player player) {

        boolean hasParty = partyInstance.containsKey(player);

        if(!hasParty) {
            player.sendMessage("§5>> §e현재 파티가 없습니다");
            return;
        }

        PartyManager PM = partyInstance.get(player);
        PM.glowingDelay = true;

        if(PM.master.getName().equals(player.getName())) {

            if(PM.members.size()==1) {
                deleteSideBar(player);
                PM.team.removeEntry(player.getName());
                partyInstance.remove(player);

                SendMessageToMembers(PM, "§5>> §6"+player.getName()+"§e님이 파티에서 떠났습니다");
                player.sendMessage("§5>> §e당신은 파티에서 떠났습니다");
                return;
            }

            deleteSideBar(player);
            PM.members.remove(player);
            PM.team.removeEntry(player.getName());
            ChangeMaster(player, PM.members.get(0));
            partyInstance.remove(player);

            SendMessageToMembers(PM, "§5>> §6"+player.getName()+"§e님이 파티에서 떠났습니다");
            player.sendMessage("§5>> §e당신은 파티에서 떠났습니다");

            return;
        }
        else {



            PM.members.remove(player);
            PM.team.removeEntry(player.getName());
            deleteSideBar(player);
            partyInstance.remove(player);
            SendMessageToMembers(PM, "§5>> §6"+player.getName()+"§e님이 파티에서 떠났습니다");
            player.sendMessage("§5>> §e당신은 파티에서 떠났습니다");

            return;
        }
    }

    public void ChangeMaster(Player commander, Player target) {

        boolean commanderHasParty = partyInstance.containsKey(commander);
        boolean targetHasParty = partyInstance.containsKey(target);

        if(!commanderHasParty) {
           commander.sendMessage("§5>> §e당신은 현재 파티에 소속되어 있지 않습니다");
            return;
        }
        else if(!targetHasParty) {
            commander.sendMessage("§5>> §6"+target.getName()+"§e님은 파티에 소속되어 있지 않습니다");
            return;
        }

        PartyManager commanderParty = partyInstance.get(commander);
        PartyManager targetParty = partyInstance.get(target);

        if(commanderParty != targetParty) {
            commander.sendMessage("§5>> §6"+target.getName()+"§e님은 당신과 같은 파티가 아닙니다");
            return;
        }

        if(!commanderParty.master.getName().equals(commander.getName())) {
            commander.sendMessage("§5>> §e파티장만 사용할 수 있는 권한입니다");
            return;
        }

        if(commander.getName().equals(target.getName())) {
            commander.sendMessage("§5>> §e당신은 이미 파티장입니다");
            return;
        }

        commanderParty.master = target;

        SendMessageToMembers(commanderParty, "§5>> §6"+target.getName()+"§e님이 파티장이 되었습니다");


    }

    public void KickMember(Player commander, Player target) {

        if(commander.getName().equals(target.getName())) {
            commander.sendMessage(partyStandardMessage("자기 자신을 추방할 수 없습니다"));
            return;
        }

        boolean commanderHasParty = partyInstance.containsKey(commander);
        boolean targetHasParty = partyInstance.containsKey(target);
        if(!commanderHasParty) {
            commander.sendMessage(partyStandardMessage("당신은 현재 파티에 소속되어 있지 않습니다"));
            return;
        }
        else if(!targetHasParty) {
            commander.sendMessage("§5>> §6"+target.getName()+"§e님은 파티에 소속되어 있지 않습니다");
            return;
        }

        PartyManager commanderParty = partyInstance.get(commander);
        PartyManager targetParty = partyInstance.get(target);

        if(commanderParty != targetParty) {
            commander.sendMessage("§5>> §6"+target.getName()+"§e님은 당신과 같은 파티가 아닙니다");
            return;
        }
        if(!commanderParty.master.getName().equals(commander.getName())) {
            commander.sendMessage(partyStandardMessage("파티장만 사용할 수 있는 권한입니다"));
            return;
        }

        commanderParty.glowingDelay = true;
        commanderParty.members.remove(target);
        deleteSideBar(target);
        partyInstance.remove(target);

        SendMessageToMembers(commanderParty, "§5>> §6"+target.getName()+"§e님이 파티에서 추방당했습니다");
        target.sendMessage(partyStandardMessage("당신은 파티에서 추방당했습니다"));
    }



    public void SendMessageToMembers(PartyManager party, String Contents) {
        for(Player members : party.getMembers()) {
            members.sendMessage(Contents);
        }
    }

    public static void partyObjectiveLoop() {

        for(Player p : Bukkit.getOnlinePlayers()) {

            if(partyInstance.containsKey(p)) {

                PartyManager partyManager = partyInstance.get(p);
                partyManager.scoreboard.resetScores(objectiveString.get(p));
                objectiveString.put(p, getObjectiveString(p));
                partyManager.objective.getScore(getObjectiveString(p)).setScore(0);


                if(partyManager.PartyXP == 0 && partyManager.XPLoop == 0) {
                    partyManager.scoreboard.resetScores(partyManager.XPAlarm);
                    partyManager.objective.getScore(partyManager.XPAlarmReady).setScore(2);
                }
                else if(partyManager.XPLoop == 0) {
                    partyManager.scoreboard.resetScores(partyManager.XPAlarm);
                    partyManager.scoreboard.resetScores(partyManager.XPAlarmReady);
                    partyManager.XPAlarm = "§b +EXP "+partyManager.PartyXP;
                    partyManager.objective.getScore(partyManager.XPAlarm).setScore(2);
                    partyManager.XPLoop = 1;
                    partyManager.PartyXP = 0;
                }

                if(!partyManager.team.hasEntry(p.getName())) {
                    partyManager.team.addEntry(p.getName());
                }

                if(partyManager.XPLoop <= 4 && partyManager.XPLoop >=1) partyManager.XPLoop++;
                else partyManager.XPLoop = 0;

            }
            objectiveString.put(p, getObjectiveString(p));
        }
    }

    public void partyGlowingLoop() {
        for(Player p : Bukkit.getOnlinePlayers()) {

            byte info = 0x00;
            boolean sneaking = p.isSneaking();
            boolean sprinting = p.isSprinting();
            boolean swimming = p.isSwimming();
            boolean invisible = p.isInvisible();
            boolean flying = p.isGliding();

            if(sneaking) info |= 0x02;
            if(sprinting) info |= 0x08;
            if(swimming) info |= 0x10;
            if(invisible) info |= 0x20;
            if(flying) info |= 0x80;

            for(Player player : Bukkit.getOnlinePlayers()) {
                //if(p==player) continue;
                CraftPlayer EP = (CraftPlayer) p;

                DataWatcher dataWatcher = EP.getHandle().getDataWatcher();
                dataWatcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte) info);

                PlayerConnection connection = ((CraftPlayer) player).getHandle().b;

                connection.sendPacket(new PacketPlayOutEntityMetadata(EP.getEntityId(), dataWatcher, true));
            }

            if(partyInstance.containsKey(p)) {

                PartyManager partyManager = partyInstance.get(p);


                if (partyManager.glowingDelay == true) {
                    continue;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (p == player) continue;

                    if (partyManager.members.contains(player)) {

                        CraftPlayer EP = (CraftPlayer) p;

                        DataWatcher dataWatcher = EP.getHandle().getDataWatcher();
                        dataWatcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), info |= 0x40);
                        PlayerConnection connection = ((CraftPlayer) player).getHandle().b;

                        connection.sendPacket(new PacketPlayOutEntityMetadata(EP.getEntityId(), dataWatcher, true));
                    } else {
                        CraftPlayer EP = (CraftPlayer) p;
                        DataWatcher dataWatcher = EP.getHandle().getDataWatcher();
                        dataWatcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), info);
                        PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
                        connection.sendPacket(new PacketPlayOutEntityMetadata(EP.getEntityId(), dataWatcher, true));
                    }
                }
            }
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(partyInstance.containsKey(p)) {
                PartyManager partyManager = partyInstance.get(p);

                if(partyManager.glowingDelay == true) {
                    partyManager.glowingDelay = false;
                    continue;
                }
            }
        }
    }

    private static String getObjectiveString(Player player) {

        int MaxHealth = PlayerManager.getinstance(player).Health;
        int CurrentHealth = PlayerHealthShield.getinstance(player).getCurrentHealth();
        int CurrentShield = PlayerHealthShield.getinstance(player).getCurrentShield();

        String Shield = "§5§l[🛡]";
        if(CurrentShield == 0) Shield = "§8§l[🛡]";

        String health = "[|"+CurrentHealth+"|]";
        List<Character> arrlist = new ArrayList<>();
        char[] arr = health.toCharArray();
        for(int i=0; i<arr.length; i++) {
            arrlist.add(arr[i]);
        }
        double rate = (double) CurrentHealth / (double) MaxHealth;
        int index = (int)(arr.length * rate);
        arrlist.add(index, '7');
        arrlist.add(index, '§');

        String objectiveString = "§6";

        for(char ch : arrlist) {
            objectiveString += ch;
        }

        objectiveString += "§r "+Shield+"§r ";
        int charlimit = 40 - objectiveString.length();

        String playerName = player.getName();
        if(player.getName().length() > charlimit)
            playerName = player.getName().substring(0, charlimit);

        objectiveString += playerName;

        return objectiveString;
    }

    private String partyStandardMessage(String content) {
        String string = "§5>> §e"+content;
        return string;
    }
}
