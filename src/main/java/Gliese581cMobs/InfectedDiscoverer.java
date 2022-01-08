package Gliese581cMobs;

import EntityPlayerManager.EntityPlayerManager;
import EntityPlayerManager.EntityPlayerWatcher;
import Mob.MobListManager;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.level.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class InfectedDiscoverer extends EntityZombie {

    public InfectedDiscoverer(EntityTypes<? extends EntityZombie> entitytypes, World world, Location loc
            , String texture, String signature, String encoded) {

        super(entitytypes, world);
        MobListManager.MobList mobList = MobListManager.MobList.감염된_개척자;
        Zombie infDcr = (Zombie) this.getBukkitEntity();
        infDcr.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 10));
        world.addEntity(this);
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());

        EntityPlayer eP = EntityPlayerManager.getInstance().dummyNetworkNPC(loc.getWorld(), loc, texture, signature);
        EntityPlayerWatcher.EntityWrapper(eP, infDcr, mobList)
                .setHeight(eP.getHeight())
                .setShowNameTag(true);

        YamlConfiguration yaml = (new SQL.Converter()).decodeYaml(encoded);
        ItemStack weapon = yaml.getItemStack("weapon");
        ItemStack boots = yaml.getItemStack("boots");
        ItemStack leggings = yaml.getItemStack("leggings");
        ItemStack chestplate = yaml.getItemStack("chest");
        ItemStack helmet = yaml.getItemStack("helmet");

        Pair<EnumItemSlot, net.minecraft.world.item.ItemStack> pair =
                new Pair<>(EnumItemSlot.a, CraftItemStack.asNMSCopy(weapon));
        Pair<EnumItemSlot, net.minecraft.world.item.ItemStack> pair2 =
                new Pair<>(EnumItemSlot.c, CraftItemStack.asNMSCopy(boots));
        Pair<EnumItemSlot, net.minecraft.world.item.ItemStack> pair3 =
                new Pair<>(EnumItemSlot.d, CraftItemStack.asNMSCopy(leggings));
        Pair<EnumItemSlot, net.minecraft.world.item.ItemStack> pair4 =
                new Pair<>(EnumItemSlot.e, CraftItemStack.asNMSCopy(chestplate));
        Pair<EnumItemSlot, net.minecraft.world.item.ItemStack> pair5 =
                new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(helmet));

        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                PlayerConnection conn = ((CraftPlayer) player).getHandle().b;
                conn.sendPacket(new PacketPlayOutEntityEquipment(eP.getId(), Arrays.asList(pair, pair2, pair3, pair4, pair5)));
            }

        }, 1);
    }

    @Override
    public void initPathfinder() {

        this.bP.a(0, new PathfinderGoalMeleeAttack(this, 1D, false));
        this.bP.a(1, new PathfinderGoalFloat(this));
        this.bP.a(2, new PathfinderGoalRandomStroll(this, 0.6D));
        this.bP.a(3, new PathfinderGoalLookAtPlayer(this, EntityPlayer.class, 8.0F));
        this.bP.a(4, new PathfinderGoalRandomLookaround(this));

//        this.bQ.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityLiving.class,10, true, true, entityliving -> {
//            if(((net.minecraft.world.entity.Entity) entityliving).isInvulnerable()) return false;
//            if(entityliving instanceof Parasite || entityliving instanceof InfectedDiscoverer) return false;
//            return true;
//        }));

        this.bQ.a(2, new PathfinderGoalNearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true, true));

    }
}
