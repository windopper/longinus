package SpyGlass;

import ClassAbility.entitycheck;
import DynamicData.EntityHealthManager;
import Mob.MobManager;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SpyGlassManager {

    private static final HashMap<Player, SpyGlassManager> instance = new HashMap<>();
    private final List<LivingEntity> glowingEntity = new ArrayList<>();

    private Player player;
    private boolean isOnZoom = false;

    private ItemStack SpyGlass;
    private double MaxDistance = 20;
    private int analyzingTimeMax = 40;


    private int analyzingTime = 0;
    private boolean GlowTerm = false;
    private final BossBar bossbar = Bukkit.createBossBar(" ", BarColor.YELLOW, BarStyle.SEGMENTED_10, BarFlag.PLAY_BOSS_MUSIC);
    private String analyzingEntityName;
    private LivingEntity analyzingEntity;


    private SpyGlassManager(Player player, ItemStack itemStack) {
        this.player = player;
        this.SpyGlass = itemStack;

    }

    public static SpyGlassManager getinstance(Player player, ItemStack itemStack) {
        if(!instance.containsKey(player)) {
            instance.put(player, new SpyGlassManager(player, itemStack));
        }
        return instance.get(player);
    }

    public void removeinstance() {

        unregisterGlowEntity();
        instance.remove(player);
        bossbar.removeAll();
    }

    public static void watchSpyGlassEnable() {

        for(SpyGlassManager spyGlass : instance.values()) {

            Player player = spyGlass.player;
            if(player.isHandRaised()) {
                spyGlass.unregisterGlowEntity();
                spyGlass.observeEntity();
            }
            else {
                spyGlass.removeinstance();
            }

        }
    }

    public void observeEntity() {

        double multiply = 0.2;

        Location location = player.getEyeLocation();
        Vector vector = location.getDirection().normalize().multiply(multiply);

        for(int i=0; i<(int)(MaxDistance / multiply); i++) {

            if(!location.getBlock().isPassable()) return;

            for(LivingEntity livingEntity : player.getWorld().getLivingEntities()) {

                Location livingEntityLocation = livingEntity.getLocation();
                BoundingBox livingEntityBoundingBox = livingEntity.getBoundingBox();
                if(livingEntityLocation.distance(location) < 1.5
                        || livingEntityBoundingBox.contains(location.getX(), location.getY(), location.getZ())) {

                    if(entitycheck.entitycheck(livingEntity) && !(livingEntity instanceof Player)) {

                        String Name = EntityHealthManager.getinstance(livingEntity).getCustomName();
                        //player.sendTitle(" ", Name, 0, 20, 0);
                        analyzing(livingEntity);
                        return;
                    }

                }

            }


            location.add(vector);
        }



    }

    public void analyzing(LivingEntity target) {

        if(analyzingEntityName != null) {

            if(!analyzingEntityName.equals(EntityHealthManager.getinstance(target).getCustomName()))
                analyzingTime = 0;
        }

        analyzingEntityName = EntityHealthManager.getinstance(target).getCustomName();
        analyzingEntity = target;


        bossbar.addPlayer(player);
        bossbar.setVisible(true);


        if(analyzingTime > 40) {
            bossbar.setProgress(1);

            bossbar.setTitle("§5 >> §a분석 완료! §5 <<");
            updateAnalyzedEntity(target);
        }
        else {
            bossbar.setProgress((double)analyzingTime/40);
            bossbar.setTitle("§5 >> §r"+analyzingEntityName+"§d 분석 중.. §5 <<");
        }

        DataWatcher dataWatcher =((CraftEntity) target).getHandle().getDataWatcher();
        dataWatcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte) 0x40);
        PlayerConnection conn = ((CraftPlayer) player).getHandle().b;
        conn.sendPacket(new PacketPlayOutEntityMetadata(target.getEntityId(), dataWatcher, true));

        glowingEntity.add(target);

        analyzingTime ++;

    }

    public void updateAnalyzedEntity(LivingEntity target) {

        String uuid = player.getUniqueId().toString();
        String username = player.getName();

        File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        String name = EntityHealthManager.getinstance(target).getCustomName();

        Arrays.stream(MobManager.MobList.values()).forEach(value -> {
            if(value.getName().equals(name)) {
                int intvalue = config.getInt("Sample."+value.getPlanet()+"."+value.name()+".count");
                intvalue ++;

                checkHadAnalyzed(intvalue, value.name());



                config.set("Sample."+value.getPlanet()+"."+value.name()+".count", intvalue);
            }
        });

        try {
            config.save(file);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void checkHadAnalyzed(int intvalue, String name) {

        if(intvalue == 1) {
            player.sendMessage("§a>> "+"§e처음으로 §6["+name+"] §e관찰에 성공하셨습니다. 표본도감에 등록됩니다");
        }

    }




    public void unregisterGlowEntity() {
        for(LivingEntity LE : glowingEntity) {
            DataWatcher dataWatcher =((CraftEntity) LE).getHandle().getDataWatcher();
            dataWatcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte) 0x00);
            PlayerConnection conn = ((CraftPlayer) player).getHandle().b;
            conn.sendPacket(new PacketPlayOutEntityMetadata(LE.getEntityId(), dataWatcher, true));
        }
    }




}
