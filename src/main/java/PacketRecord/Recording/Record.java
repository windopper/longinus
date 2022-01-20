package PacketRecord.Recording;

import com.google.common.base.Enums;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import spellinteracttest.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Record {

    private final static HashMap<Player, Record> instances = new HashMap<>();

    private final Player player;

    private File file;
    private FileConfiguration config;
    private List<Entity> actors = new ArrayList<>();
    private HashMap<Player, Entity> control = new HashMap<>();


    private Record(Player player) {
        this.player = player;
    }
    public static Record getInstance(Player player) {
        if(instances.containsKey(player)) return instances.put(player, new Record(player));
        return instances.get(player);
    }
    public static boolean hasInstance(Player player) {
        return instances.containsKey(player);
    }
    public void removeInstance() {
        instances.remove(player);
    }
    public boolean load(String filename) {
        file = new File(Main.getPlugin(Main.class).getDataFolder().getAbsolutePath()+"\\Records", filename+".yml");
        config = YamlConfiguration.loadConfiguration(file);
        if(!file.exists()) {
            try {
                notify.Warning("'"+filename+"' 은(는) 존재하지 않는 파일입니다", player);
                notify.Warning("/pr create [FILENAME]을 통하여 파일을 만들어주세요", player);

            }
            catch(Exception e) {

            }
            return false;
        }
        notify.Successful("파일을 성공적으로 불러왔습니다", player);
        return true;
    }
    public void create(String filename) {
        file = new File(Main.getPlugin(Main.class).getDataFolder().getAbsolutePath()+"\\Records", filename+".yml");
        config = YamlConfiguration.loadConfiguration(file);
        if(!file.exists()) {
            try {
                file.createNewFile();
                config.set("characters", "");
                config.set("records", "");
                notify.Successful("파일을 성공적으로 만들었습니다", player);
            }
            catch(Exception e) {
                notify.Warning("오류가 발생했습니다", player);
                removeInstance();
                e.printStackTrace();
            }
        }
    }
    public void save() {
        if(file != null && config != null) {
            try {
                config.save(file);
                notify.Successful("저장완료!", player);
            }
            catch(Exception e) {
                notify.Warning("저장 실패", player);
            }
        }
        else {
            notify.Successful("저장 할 파일이 없습니다!", player);
            removeInstance();
        }
    }
    public void delete(String filename) {
        File configfile = new File(Main.getPlugin(Main.class).getDataFolder().getAbsolutePath()+"\\Records", filename+".yml");
        if(!configfile.exists()) {
            notify.Warning("해당 파일은 존재하지 않습니다", player);
            return;
        }

        boolean result = configfile.delete();
        if(result) {
            notify.Successful("파일 "+filename+"을(를) 성공적으로 삭제했습니다", player);
        }
        else {
            notify.Warning("해당 파일 삭제에 실패하였습니다", player);
        }
    }
    public void seeList() {
        player.sendMessage("====== 녹화파일 리스트 ======");
        for(File file : (new File(Main.getPlugin(Main.class).getDataFolder().getAbsolutePath()+"\\Records")).listFiles()) {
            player.sendMessage("- "+file.getName());
        }
        player.sendMessage("=========================");
    }

    private void addCharacter(Entity entity) {

    }

    public void appear(String entityType, String playerName) {
        Player controller = null;
        for(Player target : Bukkit.getOnlinePlayers()) {
            if(target.getName().equals(playerName)) {
                controller = target;
                break;
            }
        }
        if(controller == null) {
            notify.Warning("선택한 플레이어가 현재 존재하지 않습니다", player);
            return;
        }
        if(!Enums.getIfPresent(EntityType.class, entityType).isPresent()) {
            notify.Warning("존재 하지 않는 엔티티 타입입니다", player);
            return;
        }

        Entity entity = controller.getWorld().spawnEntity(controller.getLocation(), EntityType.valueOf(entityType));
        addCharacter(entity);
        control.put(player, entity);
    }
}
