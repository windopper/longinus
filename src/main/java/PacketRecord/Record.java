package PacketRecord;

import CustomEvents.CustomMobDeathEvent;
import PlayerManager.PlayerManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import spellinteracttest.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Record implements Listener {

    private static Record record;
    private boolean RecordOn = false;

    final private List<Player> armswing = new ArrayList<>();
    final private List<LivingEntity> takeDamage = new ArrayList<>();
    final private List<Entity> death = new ArrayList<>();
    final private HashMap<Player, String> Skill = new HashMap<>();
    final private HashMap<Player, Integer> combo = new HashMap<>();

    final private HashMap<Entity, Location> originloc = new HashMap<>();
    final private ConcurrentHashMap<Entity, Integer> IDPlayer = new ConcurrentHashMap<>();

    private int ID = 0;

    private Location loc1 = null;
    private Location loc2 = null;
    private BoundingBox box = null;

    private Record() {

    }

    private void resetfield() {
        RecordOn = false;
        armswing.clear();
        takeDamage.clear();
        Skill.clear();
        combo.clear();
        originloc.clear();
        IDPlayer.clear();
        loc1 = null;
        loc2 = null;
        box = null;
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
    @EventHandler
    public void Click(PlayerInteractEvent event) {
        try {
            ItemStack itemStack = event.getItem();
            if(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).equals("우클릭하여 두 모서리의 좌표를 설정해주세요")) {
                if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if(loc1 == null) {
                        loc1 = event.getClickedBlock().getLocation();
                        Bukkit.broadcastMessage("좌표 설정 : X:"+(int)loc1.getX()+"Y:"+(int)loc1.getY()+"Z:"+(int)loc1.getZ());
                    }
                    else {
                        loc2 = event.getClickedBlock().getLocation();
                        Bukkit.broadcastMessage("좌표 설정 : X:"+(int)loc2.getX()+"Y:"+(int)loc2.getY()+"Z:"+(int)loc2.getZ());
                        event.getPlayer().getInventory().removeItem(itemStack);
                    }
                }
            }
        }
        catch(Exception e) {

        }
    }

    public void SetRecordField(Player player, final String filename) {

        Bukkit.broadcastMessage("두 모서리의 좌표를 설정해주세요.");
        ItemStack itemStack = new ItemStack(Material.GOLDEN_HOE, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("우클릭하여 두 모서리의 좌표를 설정해주세요");
        itemStack.setItemMeta(itemMeta);

        player.getInventory().addItem(itemStack);

        new BukkitRunnable() {

            int time = 0;

            @Override
            public void run() {

                if(loc1 != null && loc2 != null && box == null) {
                    box = BoundingBox.of(loc1, loc2);
                    time = 900;
                    Bukkit.broadcastMessage("5초뒤 시작");
                }

                if(time>900 && box != null) {
                    BorderParticle();
                }
                if(time==1000 && box != null) {
                    RecordStart(filename, player.getLocation());
                    cancel();
                }

                if(time>1000) {
                    Bukkit.broadcastMessage("시간 초과");
                    resetfield();
                    cancel();
                }
                time++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
    }

    public void RecordStart(final String filename, final Location Center) {

        RecordOn = true;

        File recordfile = new File(Main.getPlugin(Main.class).getDataFolder().getAbsolutePath()+"\\Records", filename+".yml");
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
            resetfield();
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(recordfile);

        for(Player player : Bukkit.getOnlinePlayers()) {

            if(!Center.getWorld().getName().equals(player.getWorld().getName())) continue;
            if(Center.distance(player.getLocation())>50) continue;
            if(player.getGameMode() == GameMode.SPECTATOR) continue;
        }

        new BukkitRunnable() {

            int time = 0;

            @Override
            public void run() {

                config.set(filename+"."+time+".players", "");
                config.set(filename+"."+time+".entities", "");

                for(Player actor : Bukkit.getOnlinePlayers()) {

                    if(!Center.getWorld().getName().equals(actor.getWorld().getName())) continue;
                    if(Center.distance(actor.getLocation())>50) continue;
                    if(!box.contains(actor.getLocation().getX(), actor.getLocation().getY(), actor.getLocation().getZ())) continue;
                    if(actor.getGameMode() == GameMode.SPECTATOR) continue;

                    // 첫 등장시 저장
                    if(!IDPlayer.containsKey(actor)) {
                        String infopath = filename+".info.players."+Integer.toString(ID);
                        IDPlayer.put(actor, ID);
                        ID++;

                        GameProfile profile = ((CraftPlayer) actor).getHandle().getProfile();
                        Property property = profile.getProperties().get("textures").iterator().next();
                        String texture = property.getValue();
                        String signature = property.getSignature();
                        ItemStack handitem = actor.getInventory().getItemInMainHand();

                        config.set(infopath+".texture", texture);
                        config.set(infopath+".signature", signature);
                        config.set(infopath+".handitem", handitem);
                        config.set(infopath+".name", actor.getName());
                        config.set(infopath+".class", PlayerManager.getinstance(actor).CurrentClass);

                        config.set(infopath+".x", Math.round(actor.getLocation().getX() * 100) / 100.0);
                        config.set(infopath+".y", Math.round(actor.getLocation().getY() * 100) / 100.0);
                        config.set(infopath+".z", Math.round(actor.getLocation().getZ() * 100) / 100.0);
                        config.set(infopath+".yaw", Math.round(actor.getLocation().getYaw() * 100) / 100.0);
                        config.set(infopath+".pitch", Math.round(actor.getLocation().getPitch() * 100) / 100.0);

                        originloc.put(actor, actor.getLocation());
                    }

                    double x = actor.getLocation().getX();
                    double y = actor.getLocation().getY();
                    double z = actor.getLocation().getZ();
                    double yaw = actor.getLocation().getYaw();
                    double pitch = actor.getLocation().getPitch();
                    boolean takedamage = takeDamage.contains(actor);
                    boolean sneaking = actor.isSneaking();
                    int combo = getCombo(actor);
                    String skill = Skill.containsKey(actor) ? Skill.get(actor) : "";

                    String filepath = filename+"."+Integer.toString(time)+".players."+ IDPlayer.get(actor);

                    if(Math.round((x - originloc.get(actor).getX()) * 10) != 0)
                        config.set(filepath+".x", Math.round((x - originloc.get(actor).getX()) * 10));
                    if(Math.round((y - originloc.get(actor).getY()) * 10) != 0)
                        config.set(filepath+".y", Math.round((y - originloc.get(actor).getY()) * 10));
                    if(Math.round((z - originloc.get(actor).getZ()) * 10) != 0)
                        config.set(filepath+".z", Math.round((z - originloc.get(actor).getZ()) * 10));
                    if(Math.round((yaw - originloc.get(actor).getYaw()) * 10) != 0)
                        config.set(filepath+".yaw", Math.round((yaw - originloc.get(actor).getYaw()) * 10));
                    if(Math.round((pitch - originloc.get(actor).getPitch()) * 10) != 0)
                        config.set(filepath+".pitch", Math.round((pitch - originloc.get(actor).getPitch()) * 10));

                    config.set(filepath+".td", takedamage);
                    config.set(filepath+".sn", sneaking); // sneaking
                    config.set(filepath+".c", combo); // combo
                    config.set(filepath+".s", skill); // skill

                }
                for(LivingEntity actor : Center.getWorld().getLivingEntities()) {

                    if(actor instanceof Player) continue;
                    if(!Center.getWorld().getName().equals(actor.getWorld().getName())) continue;
                    if(!box.contains(actor.getLocation().getX(), actor.getLocation().getY(), actor.getLocation().getZ())) continue;
                    if(Center.distance(actor.getLocation())>50) continue;

                    String entity = ((CraftEntity) actor).getHandle().getClass().getName();
                    if(!PacketSummonEntity.Type.contains(entity)) continue;

                    // 등장엔티티 초기설정
                    if(!IDPlayer.containsKey(actor)) {

                        String infopath = filename+".info.entities."+Integer.toString(ID);
                        IDPlayer.put(actor, ID);
                        ID++;

                        String uuid = actor.getUniqueId().toString();
                        config.set(infopath+".entity", entity);
                        config.set(infopath+".uuid", uuid);
                        config.set(infopath+".x", Math.round(actor.getLocation().getX() * 100) / 100.0);
                        config.set(infopath+".y", Math.round(actor.getLocation().getY() * 100) / 100.0);
                        config.set(infopath+".z", Math.round(actor.getLocation().getZ() * 100) / 100.0);
                        config.set(infopath+".yaw", Math.round(actor.getLocation().getYaw() * 100) / 100.0);
                        config.set(infopath+".pitch", Math.round(actor.getLocation().getPitch() * 100) / 100.0);

                        originloc.put(actor, actor.getLocation());
                    }

                    String filepath = filename+"."+Integer.toString(time)+".entities."+IDPlayer.get(actor);

                    // 매틱마다 상태저장
                    double x = actor.getLocation().getX();
                    double y = actor.getLocation().getY();
                    double z = actor.getLocation().getZ();
                    double yaw = actor.getLocation().getYaw();
                    double pitch = actor.getLocation().getPitch();
                    boolean takedamage = takeDamage.contains(actor);
                    boolean dead = death.contains(actor);

                    if(Math.round((x - originloc.get(actor).getX()) * 10) != 0)
                        config.set(filepath+".x", Math.round((x - originloc.get(actor).getX()) * 10));
                    if(Math.round((y - originloc.get(actor).getY()) * 10) != 0)
                        config.set(filepath+".y", Math.round((y - originloc.get(actor).getY()) * 10));
                    if(Math.round((z - originloc.get(actor).getZ()) * 10) != 0)
                        config.set(filepath+".z", Math.round((z - originloc.get(actor).getZ()) * 10));
                    if(Math.round((yaw - originloc.get(actor).getYaw()) * 10) != 0)
                        config.set(filepath+".yaw", Math.round((yaw - originloc.get(actor).getYaw()) * 10));
                    if(Math.round((pitch - originloc.get(actor).getPitch()) * 10) != 0)
                        config.set(filepath+".pitch", Math.round((pitch - originloc.get(actor).getPitch()) * 10));
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

                BorderParticle();
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

    private void BorderParticle() {
        if(loc1 != null && loc2 != null) {
            double x1 = loc1.getX() > loc2.getX() ? -0.2 : 0.2;
            double y1 = loc1.getY() > loc2.getY() ? -0.2 : 0.2;
            double z1 = loc1.getZ() > loc2.getZ() ? -0.2 : 0.2;
            double x2 = loc1.getX() > loc2.getX() ? 0.2 : -0.2;
            double y2 = loc1.getY() > loc2.getY() ? 0.2 : -0.2;
            double z2 = loc1.getZ() > loc2.getZ() ? 0.2 : -0.2;

            for(double i=0; i<50; i++) {

                loc1.getWorld().spawnParticle(Particle.FLAME, loc1.clone().add(x1 * i, 0, 0), 1, 0, 0, 0, 0);
            }
            for(double i=0; i<50; i++) {
                loc1.getWorld().spawnParticle(Particle.FLAME, loc1.clone().add(0, y1 * i, 0), 1, 0, 0, 0, 0);
            }
            for(double i=0; i<50; i++) {


                loc1.getWorld().spawnParticle(Particle.FLAME, loc1.clone().add(0, 0, z1 * i), 1, 0, 0, 0, 0);
            }
            for(double i=0; i<50; i++) {

                loc2.getWorld().spawnParticle(Particle.FLAME, loc2.clone().add(x2 * i, 0, 0), 1, 0, 0, 0, 0);
            }
            for(double i=0; i<50; i++) {

                loc2.getWorld().spawnParticle(Particle.FLAME, loc2.clone().add(0, y2 * i, 0), 1, 0, 0, 0, 0);
            }
            for(double i=0; i<50; i++) {

                loc2.getWorld().spawnParticle(Particle.FLAME, loc2.clone().add(0, 0, z2 * i), 1, 0, 0, 0, 0);
            }

        }
    }
}
