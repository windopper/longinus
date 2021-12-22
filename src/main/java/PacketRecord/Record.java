package PacketRecord;

import PlayerManager.PlayerManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Record implements Listener {

    private static Record record;
    private boolean RecordOn = false;
    final private List<Player> armswing = new ArrayList<>();
    final private HashMap<Player, String> Skill = new HashMap<>();
    private int combo = 1;

    private Record() {

    }

    public static Record getInstance() {
        if(record == null) record = new Record();
        return record;
    }

    public void recordSkill(Player player, String combo) {
        if(!RecordOn) return;
        Skill.put(player, combo);
    }
    public void recordCombo(int Combo) {
        this.combo = Combo;
    }
    private int getCombo() {
        return combo;
    }

    public boolean isRecording() {
        return RecordOn;
    }

    @EventHandler
    public void armanimation(PlayerAnimationEvent event) {
        if(event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
            armswing.add(event.getPlayer());
        }
    }

    public void RecordStart(final String filename) {

        RecordOn = true;

        File recordfile = new File(filename+".yml");
        if(!recordfile.exists()) {
            try {
                recordfile.createNewFile();
            }
            catch(Exception e) {

            }
            Bukkit.broadcastMessage("녹화를 시작합니다");
        }
        else {
            Bukkit.broadcastMessage("§c이미 같은 이름의 파일이 존재합니다!");
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(recordfile);

        for(Player player : Bukkit.getOnlinePlayers()) {

            GameProfile profile = ((CraftPlayer) player).getHandle().getProfile();
            Property property = profile.getProperties().get("textures").iterator().next();
            String texture = property.getValue();
            String signature = property.getSignature();
            ItemStack handitem = player.getItemInHand();

            config.set(filename+".info."+player.getName()+".texture", texture);
            config.set(filename+".info."+player.getName()+".signature", signature);
            config.set(filename+".info."+player.getName()+".handitem", handitem);
        }

        new BukkitRunnable() {

            int time = 0;

            @Override
            public void run() {

                for(Player player : Bukkit.getOnlinePlayers()) {

                    String pname = player.getName();
                    String uuid = player.getUniqueId().toString();
                    String world = player.getWorld().getName();
                    double x = player.getLocation().getX();
                    double y = player.getLocation().getY();
                    double z = player.getLocation().getZ();
                    double yaw = player.getLocation().getYaw();
                    double pitch = player.getLocation().getPitch();
                    boolean swing = armswing.contains(player);
                    boolean sneaking = player.isSneaking();
                    int combo = getCombo();
                    String skill = Skill.containsKey(player) ? Skill.get(player) : "";
                    String Class = PlayerManager.getinstance(player).CurrentClass;

                    String filepath = filename+"."+Integer.toString(time)+"."+pname;

                    config.set(filepath+".uuid", uuid);
                    config.set(filepath+".world", world);
                    config.set(filepath+".x", x);
                    config.set(filepath+".y", y);
                    config.set(filepath+".z", z);
                    config.set(filepath+".yaw", yaw);
                    config.set(filepath+".pitch", pitch);
                    config.set(filepath+".armswing", swing);
                    config.set(filepath+".sneaking", sneaking);
                    config.set(filepath+".combo", combo);
                    config.set(filepath+".skill", skill);
                    config.set(filepath+".class", Class);



                }
                if(!RecordOn) {
                    try {
                        config.save(recordfile);
                    }
                    catch(Exception e) {

                    }
                    Bukkit.broadcastMessage("녹화 종료");
                    cancel();
                }

                time++;
                armswing.clear();
                Skill.clear();
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"),0, 1);

    }

    public void RecordStop() {
        RecordOn = false;
    }
}
