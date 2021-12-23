package PacketRecord;

import CustomEvents.CustomMobDeathEvent;
import PlayerManager.PlayerManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
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
    final private List<LivingEntity> takeDamage = new ArrayList<>();
    final private List<Entity> death = new ArrayList<>();

    final private HashMap<Player, String> Skill = new HashMap<>();
    final private HashMap<Player, Integer> combo = new HashMap<>();

    private Record() {

    }

    private void resetfield() {
        RecordOn = false;
        armswing.clear();
        takeDamage.clear();
        Skill.clear();
        combo.clear();
    }

    public static Record getInstance() {
        if(record == null) record = new Record();
        return record;
    }

    public void recordSkill(Player player, String combo) {
        if(!RecordOn) return;
        Skill.put(player, combo);
    }
    public void recordCombo(Player player, int Combo) {

        this.combo.put(player, Combo);
    }
    private int getCombo(Player player) {
        if(!combo.containsKey(player)) return 1;
        return this.combo.get(player);
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
    @EventHandler
    public void TakeDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            takeDamage.add((LivingEntity) event.getEntity());
        }
    }
    @EventHandler
    public void MobDeathEvent(CustomMobDeathEvent event) {
        Entity entity = event.getEntity();
        death.add(entity);
    }

    public void RecordStart(final String filename, final Location Center) {

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

            if(!Center.getWorld().getName().equals(player.getWorld().getName())) continue;
            if(Center.distance(player.getLocation())>50) continue;
            if(player.getGameMode() == GameMode.SPECTATOR) continue;

            GameProfile profile = ((CraftPlayer) player).getHandle().getProfile();
            Property property = profile.getProperties().get("textures").iterator().next();
            String texture = property.getValue();
            String signature = property.getSignature();
            ItemStack handitem = player.getItemInHand();

            config.set(filename+".info."+player.getName()+".texture", texture);
            config.set(filename+".info."+player.getName()+".signature", signature);
            config.set(filename+".info."+player.getName()+".handitem", handitem);
            config.set(filename+".info."+player.getName()+".world", Center.getWorld().getName());
            config.set(filename+".info."+player.getName()+".uuid", player.getUniqueId().toString());
        }
//        for(Entity actor : Center.getWorld().getEntities()) {
//
//            if (actor instanceof Player) continue;
//            if (!Center.getWorld().getName().equals(actor.getWorld().getName())) continue;
//            if (Center.distance(actor.getLocation()) > 50) continue;
//
//            String entity = ((CraftEntity) actor).getHandle().getClass().getName();
//            String uuid = actor.getUniqueId().toString();
//
//            config.set(filename+".info."+uuid+".entity", entity);
//            config.set(filename+".info."+uuid+".uuid", uuid);
//            config.set(filename+".info."+uuid+".world", Center.getWorld().getName());
//
//        }

        new BukkitRunnable() {

            int time = 0;

            @Override
            public void run() {

                for(Player actor : Bukkit.getOnlinePlayers()) {

                    if(!Center.getWorld().getName().equals(actor.getWorld().getName())) continue;
                    if(Center.distance(actor.getLocation())>50) continue;
                    if(actor.getGameMode() == GameMode.SPECTATOR) continue;

                    String pname = actor.getName();
                    String uuid = actor.getUniqueId().toString();

                    double x = (double)Math.round(actor.getLocation().getX() * 1000) / 1000;
                    double y = (double)Math.round(actor.getLocation().getY() * 1000) / 1000;
                    double z = (double)Math.round(actor.getLocation().getZ() * 1000) / 1000;
                    double yaw = (double)Math.round(actor.getLocation().getYaw() * 1000) / 1000;
                    double pitch = (double)Math.round(actor.getLocation().getPitch() * 1000) / 1000;
                    boolean swing = armswing.contains(actor);
                    boolean takedamage = takeDamage.contains(actor);
                    boolean sneaking = actor.isSneaking();
                    int combo = getCombo(actor);
                    String skill = Skill.containsKey(actor) ? Skill.get(actor) : "";
                    String Class = PlayerManager.getinstance(actor).CurrentClass;

                    String filepath = filename+"."+Integer.toString(time)+".players."+pname;

                    config.set(filepath+".x", x);
                    config.set(filepath+".y", y);
                    config.set(filepath+".z", z);
                    config.set(filepath+".yaw", yaw);
                    config.set(filepath+".pitch", pitch);
                    config.set(filepath+".armswing", swing);
                    config.set(filepath+".takedamage", takedamage);
                    config.set(filepath+".sneaking", sneaking);
                    config.set(filepath+".combo", combo);
                    config.set(filepath+".skill", skill);
                    config.set(filepath+".class", Class);
                }
                for(LivingEntity actor : Center.getWorld().getLivingEntities()) {

                    if(actor instanceof Player) continue;
                    if(!Center.getWorld().getName().equals(actor.getWorld().getName())) continue;
                    if(Center.distance(actor.getLocation())>50) continue;


                    String entity = ((CraftEntity) actor).getHandle().getClass().getName();
                    String uuid = actor.getUniqueId().toString();

                    // 등장엔티티 초기설정
                    config.set(filename+".info."+uuid+".entity", entity);
                    config.set(filename+".info."+uuid+".uuid", uuid);
                    config.set(filename+".info."+uuid+".world", Center.getWorld().getName());


                    // 매틱마다 상태저장
                    double x = (double)Math.round(actor.getLocation().getX() * 1000) / 1000;
                    double y = (double)Math.round(actor.getLocation().getY() * 1000) / 1000;
                    double z = (double)Math.round(actor.getLocation().getZ() * 1000) / 1000;
                    double yaw = (double)Math.round(actor.getLocation().getYaw() * 1000) / 1000;
                    double pitch = (double)Math.round(actor.getLocation().getPitch() * 1000) / 1000;
                    boolean takedamage = takeDamage.contains(actor);
                    boolean dead = death.contains(actor);

                    String filepath = filename+"."+Integer.toString(time)+".entities."+uuid;

                    config.set(filepath+".x", x);
                    config.set(filepath+".y", y);
                    config.set(filepath+".z", z);
                    config.set(filepath+".yaw", yaw);
                    config.set(filepath+".pitch", pitch);
                    config.set(filepath+".takedamage", takedamage);
                    config.set(filepath+".death", dead);

                }

                if(!RecordOn) {
                    try {
                        config.save(recordfile);
                    }
                    catch(Exception e) {

                    }
                    Bukkit.broadcastMessage("녹화 종료");
                    resetfield();
                    cancel();
                }

                time++;
                armswing.clear();
                takeDamage.clear();
                Skill.clear();
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"),0, 1);


    }

    public void RecordStop() {
        RecordOn = false;
    }
}
