package Gliese581cMobs;

import Mob.EntityManager;
import Mob.EntityStatusManager;
import Mob.MobListManager;
import PlayerManager.PlayerDeadBodySetter;
import PlayerManager.PlayerManager;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntitySilverfish;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Zombie;
import spellinteracttest.Main;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class Parasite extends EntitySilverfish {

    public static List<String> description = Arrays.asList(
            "대부분 지역에서 출연. 글리제 581c에서 최초로 발견된 생명체",
            "성격이 포악하여 자신의 영역에 들어오면 상대가 누구든 공격함");


    public Parasite(World world, Location location) {
        super(EntityTypes.aA,((CraftWorld) world).getHandle());

        Silverfish craftsilverfish = (Silverfish) this.getBukkitEntity();

        MobListManager.MobList mobList = MobListManager.MobList.패러싯;

        IChatBaseComponent name = IChatBaseComponent.a(mobList.getName());
        this.setCustomName(name);
        this.setCustomNameVisible(true);

        this.getWorld().addEntity(this);
        this.setPosition(location.getX(), location.getY(), location.getZ());

        Method method = null;
        try {
            Class c = this.getClass();
            method = c.getMethod("attackTarget");
        }
        catch(Exception e) {

        }

        EntityManager.getinstance(craftsilverfish, mobList).setAttackAbility(method, this);

        EntityStatusManager.getinstance(craftsilverfish).setCanKnockback(false);
    }

    //TODO 좀벌레 나오면 가까운 엔티티로 이동
    // 플레이어를 죽이면 시체 만들고 숙주가 됨

    @Override
    public void initPathfinder() {

        this.bP.a(1, new PathfinderGoalFloat(this));
        this.bP.a(0, new PathfinderGoalMeleeAttack(this, 1D, false));
        this.bP.a(2, new PathfinderGoalRandomStroll(this, 0.6D));
        this.bP.a(3, new PathfinderGoalLookAtPlayer(this, EntityPlayer.class, 8.0F));
        this.bP.a(4, new PathfinderGoalRandomLookaround(this));

        this.bQ.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityLiving.class,10, true, true, entityliving -> {
            if(((net.minecraft.world.entity.Entity) entityliving).isInvulnerable()) return false;
            if(entityliving instanceof Parasite || entityliving instanceof InfectedDiscoverer) return false;
            return true;
        }));
    }

    public void attackTarget() {

        if(this.getGoalTarget() == null) return;

        org.bukkit.entity.Entity entity = this.getGoalTarget().getBukkitEntity();
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 0);

        if(PlayerDeadBodySetter.playerMarker.containsKey(entity)) {

            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {

            InfectedDiscoverer infectedDiscoverer = new InfectedDiscoverer(EntityTypes.be, ((CraftEntity) entity).getHandle().getWorld(),
                    entity.getLocation(),
                    entity.getScoreboardTags()
                            .stream()
                            .filter((i) -> i.contains("texture")).toList().get(0).split(":")[1],
                    entity.getScoreboardTags()
                            .stream()
                            .filter((i) -> i.contains("signature")).toList().get(0).split(":")[1]);

                Zombie infEntity = (Zombie) infectedDiscoverer.getBukkitEntity();
                EntityManager eM = EntityManager.getinstance(infEntity);

                Player player = PlayerDeadBodySetter.playerMarker.get(entity);
                PlayerDeadBodySetter.playerMarker.remove(entity);
                PlayerManager pM = PlayerManager.getinstance(player);
                EntityManager.getinstance(entity).setCurrentHealth(0);
                EntityManager.getinstance(this.getBukkitEntity()).setCurrentHealth(0);
                eM.Level = pM.getlvl();
                eM.setMaxHealth(pM.Health);
                eM.updateCustomName();
            }, 1);


            return;
        }

        EntityManager eM = EntityManager.getinstance(entity);

        eM.setMaxHealth((int)((double)eM.getMaxHealth() * 1.3));
        eM.setCurrentHealth((int)((double)eM.getCurrentHealth() * 1.3));
        eM.minDmg *= 1.3;
        eM.maxDmg *= 1.3;
        eM.Level += 10;
        eM.updateCustomName();

        EntityManager.getinstance(this.getBukkitEntity()).setCurrentHealth(0);
    }
}
