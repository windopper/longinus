package Gliese581cMobs;

import Mob.EntityManager;
import Mob.EntityStatusManager;
import Mob.MobListManager;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntitySilverfish;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Silverfish;

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

        EntityManager.getinstance(craftsilverfish, mobList);
        EntityStatusManager.getinstance(craftsilverfish).setCanKnockback(false);
    }

    //TODO 좀벌레 나오면 가까운 엔티티로 이동
    // 플레이어를 죽이면 시체 만들고 숙주가 됨
    //

    @Override
    public void initPathfinder() {

//        this.bP.a(4, new PathfinderGoalRandomLookaround(this));
//        this.bP.a(2, new PathfinderGoalRandomStrollLand(this, 1D));
        this.bP.a(0, new PathfinderGoalFloat(this));
        this.bP.a(1, new PathfinderGoalMeleeAttack(this, 1D, false));
        this.bP.a(2, new PathfinderGoalRandomStroll(this, 0.6D));
        this.bP.a(3, new PathfinderGoalLookAtPlayer(this, EntityPlayer.class, 8.0F));
        this.bP.a(4, new PathfinderGoalRandomLookaround(this));

        //this.bQ.a(2, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.bQ.a(3, new PathfinderGoalNearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));

    }



}
