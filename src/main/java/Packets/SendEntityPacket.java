package Packets;

import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SendEntityPacket {

    public static void MaintainOriginOne(Entity target, Player wanttoshow, byte bitmask) {

        Boolean onfire = false;
        Boolean iscrouching = false;
        Boolean issprinting = false;
        Boolean isswimming = false;
        Boolean isinvisible = false;
        Boolean hasglowing = false;
        Boolean isflyingwithelytra = false;

        if(target instanceof Player) {
            Player ptarget = (Player) target;
            onfire = ptarget.isVisualFire();
            iscrouching = ptarget.isSneaking();
            isswimming = ptarget.isSwimming();
            isinvisible = ptarget.isInvisible();
            hasglowing = ptarget.isGlowing();
            isflyingwithelytra = ptarget.isGliding();
        }
        if(target instanceof LivingEntity) {
            LivingEntity ltarget = (LivingEntity) target;
            onfire = ltarget.isVisualFire();
            isswimming = ltarget.isSwimming();
            isinvisible = ltarget.isInvisible();
            hasglowing = ltarget.isGlowing();
            isflyingwithelytra = ltarget.isGliding();
        }

        byte sum = bitmask;

        sum |= onfire ? 0x01 : 0x00;
        sum |= iscrouching ? 0x02 : 0x00;
        sum |= issprinting ? 0x08 : 0x00;
        sum |= isswimming ? 0x10 : 0x00;
        sum |= isinvisible ? 0x20 : 0x00;
        sum |= hasglowing ? 0x40 : 0x00;
        sum |= isflyingwithelytra ? 0x80 : 0x00;

        PlayerConnection conn = ((CraftPlayer) wanttoshow).getHandle().b;
        DataWatcher dataWatcher = ((CraftEntity) target).getHandle().getDataWatcher();
        dataWatcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), sum);
        conn.sendPacket(new PacketPlayOutEntityMetadata(target.getEntityId(), dataWatcher, true));

    }

}
