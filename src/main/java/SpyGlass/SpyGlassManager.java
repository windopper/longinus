package SpyGlass;

import ClassAbility.entitycheck;
import Mob.EntityManager;
import Mob.MobListManager;
import Packets.SendEntityPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SpyGlassManager {

    private static final HashMap<Player, SpyGlassManager> instance = new HashMap<>();
    private final List<Entity> glowingEntity = new ArrayList<>();

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
        bossbar.removePlayer(player);
        bossbar.removeAll();
    }

    public static void watchSpyGlassEnable() {

        SpyGlassManager safedelete = null;

        for(SpyGlassManager spyGlass : instance.values()) {

            if(safedelete != null) {
                safedelete.removeinstance();
                safedelete = null;
            }

            Player player = spyGlass.player;
            if(player.isHandRaised()) {
                spyGlass.unregisterGlowEntity();
                spyGlass.observeEntity();
            }
            else {
                safedelete = spyGlass;
            }

        }

        if(safedelete != null) {
            safedelete.removeinstance();
            safedelete = null;
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

                        String Name = EntityManager.getinstance(livingEntity).getCustomName();
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

            if(!analyzingEntityName.equals(EntityManager.getinstance(target).getCustomName()))
                analyzingTime = 0;
        }
        else {
            analyzingTime = 0;
        }

        analyzingEntityName = EntityManager.getinstance(target).getCustomName();
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


        // 투명화 상태면 wrapper한 몹 보여주기
        if(target.hasPotionEffect(PotionEffectType.INVISIBILITY) || target.isInvisible()) {

            // wrapper한 몹이 없으면 원래 몹 발광
            if(EntityManager.getinstance(target).getDisguises() == null) {
                SendEntityPacket.MaintainOriginOne(target, player, (byte) 0x40);
                glowingEntity.add(target);
            }
            else {
                for(Entity disguise : EntityManager.getinstance(target).getDisguises()) {
                    SendEntityPacket.MaintainOriginOne(disguise, player, (byte) 0x40);
                    glowingEntity.add(disguise);
                }
            }
        }
        else {
            SendEntityPacket.MaintainOriginOne((Entity) target, player, (byte) 0x40);
            glowingEntity.add(target);
        }


        // 분석 시간 증가
        analyzingTime ++;

    }

    public void updateAnalyzedEntity(LivingEntity target) {

        String uuid = player.getUniqueId().toString();
        String username = player.getName();

        File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        String name = EntityManager.getinstance(target).getCustomName();

        Arrays.stream(MobListManager.MobList.values()).forEach(value -> {
            if(value.getName().equals(name) && value.isScannable()) {
                int intvalue = config.getInt("Sample."+value.getPlanet()+"."+value.name()+".count");
                intvalue ++;

                checkHadAnalyzed(intvalue, value.getRawName());

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
        for(Entity E : glowingEntity) {
            SendEntityPacket.MaintainOriginOne((Entity) E, player, (byte) 0x00);
        }
    }




}
