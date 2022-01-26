package CustomScoreboard;

import Party.PartyHandler;
import PlayerManager.PlayerManager;
import PlayerManager.PlayerHealthShield;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import spellinteracttest.Main;

import java.util.HashMap;
import java.util.List;

public class ObjectiveDisplayLoop {
    private static ObjectiveDisplayLoop objectiveDisplayLoop;
    private ObjectiveDisplayLoop() {

    }
    public static ObjectiveDisplayLoop getInstance() {
        if(objectiveDisplayLoop == null) objectiveDisplayLoop = new ObjectiveDisplayLoop();
        return objectiveDisplayLoop;
    }

    public void mainLoop() {

        new BukkitRunnable() {

            @Override
            public void run() {
                displayBuffs.getInstances().entrySet().forEach(db-> {
                    if(!db.getKey().isOnline()) db.getValue().remove();
                });

                Bukkit.getOnlinePlayers().forEach((player)-> {
                    ObjectiveDisplay obj = ObjectiveDisplay.getBuilder(player);
                    obj.setTitle("§e타이틀");

                    if(PartyHandler.hasParty(player)) {
                        PartyHandler partyHandler = PartyHandler.getInstance(player);
                        partyHandler.getPartyMembers().forEach(m-> {
                            PlayerManager pm = PlayerManager.getinstance(m);
                            PlayerHealthShield phs = PlayerHealthShield.getinstance(m);

                            StringBuilder sb = new StringBuilder("§5- §r");
                            double healthRate = (double)phs.getCurrentHealth() / (double)pm.Health;
                            double shieldRate = (double)phs.getCurrentShield() / (double)pm.MaxShield;
                            if(healthRate < 0.33) {
                                sb.append("§c"+getGauge(healthRate, phs.getCurrentHealth()));
                            }
                            else if(healthRate < 0.7) {
                                sb.append("§e"+getGauge(healthRate, phs.getCurrentHealth()));
                            }
                            else {
                                sb.append("§a"+getGauge(healthRate, phs.getCurrentHealth()));
                            }

                            sb.append(" ");

                            if(phs.getCurrentShield() == 0) {
                                sb.append("§7"+getGauge(shieldRate, phs.getCurrentShield()));
                            }
                            else {
                                sb.append("§5"+getGauge(shieldRate, phs.getCurrentShield()));
                            }

                            obj.addLine("§6"+m.getName()+" §7[Lv."+pm.getlvl()+", "+pm.CurrentClass+"]");
                            obj.addLine(sb.toString());
                        });
                    }
                    else {
                        obj.addLine("안녕하세요!");
                    }

                    obj.addLine("=");
                    if(displayBuffs.hasInstance(player)) {
                        displayBuffs db = displayBuffs.getInstance(player);
                        StringBuilder sb = new StringBuilder();
                        db.getBuffs().entrySet().forEach(bf-> {
                            sb.append(bf.getKey()+" : "+bf.getValue()/20+"s ");
                            obj.addLine(bf.getKey()+" : "+bf.getValue()/20+"s ");
                        });
                    }

                    obj.build();
                });
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 2);
    }

    private String getGauge(double rate, int var) {
        StringBuilder sb = new StringBuilder();
        sb.append("[|||"+var+"|||]");
        if(rate>1) rate = 1;
        sb.insert((int)(sb.length()*rate), "§7");
        return sb.toString();
    }

    public static class displayBuffs {
        private static HashMap<Player, displayBuffs> instance = new HashMap<>();
        private HashMap<String, Integer> buffs = new HashMap<>();
        private Player player;
        private displayBuffs(Player player) {
            this.player = player;
        }
        public static displayBuffs getInstance(Player player) {
            if(!instance.containsKey(player)) instance.put(player, new displayBuffs(player));
            return instance.get(player);
        }
        public static boolean hasInstance(Player player) {
            return instance.containsKey(player);
        }
        public static HashMap<Player, displayBuffs> getInstances() {
            return instance;
        }
        public void remove() {
            instance.remove(player);
        }
        public HashMap<String, Integer> getBuffs() {
            return buffs;
        }

        public void showBuffs(String content, int tick) {
            buffs.put(content, tick);
        }

        public void removeBuffs(String content) {
            buffs.remove(content);
        }
    }
}
