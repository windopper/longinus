package SQL;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerQuest {

    private String uuid;
    private Player player;

    public PlayerQuest(Player player) {
        this.uuid = player.getUniqueId().toString();
        this.player = player;
    }

    public PlayerQuest(String uuid) {
        this.uuid = uuid;
    }

    public int getQuestProgress(String className, String questName) {
        PlayerClass pC = new PlayerClass(player);
        YamlConfiguration yaml = pC.getClassFile();
        return yaml.getInt(className+".quests."+questName+".progress");
    }

    public void setQuestProgress(String className, String questName, int progress) {
        PlayerClass pC = new PlayerClass(player);
        YamlConfiguration yaml = pC.getClassFile();
        yaml.set(className+".quests."+questName+".progress", progress);
        String encoded = (new Converter()).encodeYaml(yaml);
        pC.sendToSQLServer(encoded);
    }

    public void addQuestProgress(String className, String questName) {
        PlayerClass pC = new PlayerClass(player);
        YamlConfiguration yaml = pC.getClassFile();
        yaml.set(className+".quests."+questName+".progress", yaml.getInt(className+".quests."+questName+".progress") + 1);
        String encoded = (new Converter()).encodeYaml(yaml);
        pC.sendToSQLServer(encoded);
    }

}
