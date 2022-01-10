package ClassAbility.Shield;

import EntityPlayerManager.EntityPlayerManager;
import EntityPlayerManager.EntityPlayerWatcher;
import Mob.EntityManager;
import Mob.MobListManager;
import PlayerManager.PlayerManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.level.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.UUID;

public class Illusion extends EntityVillager {

    private final String texture;
    private final String signature;
    private final Player player;
    private final GameProfile gameProfile;

    public Illusion(EntityTypes<? extends EntityVillager> entitytypes, World world, Location loc, Player player) {

        super(entitytypes, world);
        Villager illusion = (Villager) this.getBukkitEntity();
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        this.getWorld().addEntity(this);
        illusion.setAI(false);

        for(Player p : Bukkit.getOnlinePlayers()) {
            PlayerConnection conn = ((CraftPlayer) p).getHandle().b;
            conn.sendPacket(new PacketPlayOutEntityDestroy(illusion.getEntityId()));
        }

        GameProfile profile = ((CraftPlayer) player).getHandle().getProfile();
        Property property = profile.getProperties().get("textures").iterator().next();
        this.texture = property.getValue();
        this.signature = property.getSignature();
        this.player = player;
        this.gameProfile = new GameProfile(UUID.randomUUID(), "");
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));

        MobListManager.MobList mobList = MobListManager.MobList.illusion;

        EntityPlayer eP = EntityPlayerManager.getInstance().dummyNetworkNPC(loc.getWorld(), loc, texture, signature);
        EntityPlayerWatcher.EntityWrapper(eP, illusion, mobList).setCustomName("§e§o"+player.getName()+"의 환영");

        PlayerManager pM = PlayerManager.getinstance(player);

        EntityManager.getinstance(illusion).setMaxHealth((int)((double)pM.Health / 4));
        EntityManager.getinstance(illusion).setCurrentHealth((int)((double)pM.Health / 4));
        EntityManager.getinstance(illusion).setCanDamagedByPlayer(false);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.aZ;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesrouce) {
        return SoundEffects.gL;
    }

    @Override
    public SoundEffect getSoundDeath() {
        return SoundEffects.gK;
    }

    @Override
    public void shakeHead() {
        return;
    }

}
