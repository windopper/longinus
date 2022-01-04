package PlayerManager;

import Mob.EntityManager;
import Mob.MobListManager;
import Party.PartyManager;
import net.minecraft.network.chat.ChatBaseComponent;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import spellinteracttest.Main;

import java.util.*;

public class PlayerLevelManager {

    private static PlayerLevelManager playerLevelManager;

    private PlayerLevelManager() {

    }

    public static PlayerLevelManager getInstance() {
        if(playerLevelManager == null) playerLevelManager = new PlayerLevelManager();
        return playerLevelManager;
    }

    public void XPContribute(Entity entity, MobListManager.MobList mobList) {

        int exp = mobList.getEXP();
        int MaxHealth = EntityManager.getinstance(entity).getMaxHealth();

        HashMap<Player, Integer> CBList = EntityManager.getinstance(entity).getContribute();
        Set<Player> getContributor = CBList.keySet();
        List<Map.Entry<Player, Integer>> entryList = new LinkedList<>(CBList.entrySet());

        //오름차순
        entryList.sort((o1, o2) -> o2.getValue() - o1.getValue());

//        for(Map.Entry<Player, Integer> en : entryList) {
//            Bukkit.broadcastMessage(en.getKey().getName()+"  "+en.getValue());
//        }

        int sum = 0;
        double average = 0;
        int size = entryList.size();

        // 상위 5명의 평균
        for(int i=0; i<size; i++) {
            sum += entryList.get(i).getValue();
            if(i==4) break;
        }
        if(size > 5 ) average = (double)sum / 5;
        else average = (double)sum / size;

        double halfaverage = average / 2;


        for(Player player : getContributor) {

            //Bukkit.broadcastMessage(player.getName());
            // 기여도가 평균의 절반 보다 적으면
            if(CBList.get(player) < halfaverage) continue;
            // 아니면
            else {
                PlayerManager psm = PlayerManager.getinstance(player);
                double contributeRate = (double)CBList.get(player) / MaxHealth > 1 ? 1 : (double)CBList.get(player) / MaxHealth;
                int GiveExp = (int) (exp * contributeRate);

                GiveExp = DecreaseExpPerLevel(GiveExp, mobList.getLevel(), psm.getlvl());
                ShowEXPArmorStand(entity, player, GiveExp);
                DistributePartyXP(GiveExp, player);


                psm.setexp(psm.getexp() + GiveExp);
            }
        }
    }

    private void ShowEXPArmorStand(Entity entity, Player showTo, int EXP) {

        Location location = entity.getLocation().clone().add(0, 1, 0);
        Location location_y = entity.getLocation().clone().add( 0, 1.25, 0);

        ChatBaseComponent chatBaseComponent = new ChatComponentText(showTo.getName());
        ChatBaseComponent chatBaseComponent2 = new ChatComponentText("[ EXP +"+EXP+" ]");

        World nmsWorld = ((CraftWorld) entity.getWorld()).getHandle();

        EntityArmorStand craftArmorStand = new EntityArmorStand(EntityTypes.c, nmsWorld);
        craftArmorStand.setPosition(location.getX(), location.getY(), location.getZ());
        craftArmorStand.setCustomName(chatBaseComponent);
        craftArmorStand.setInvisible(true);
        craftArmorStand.setMarker(true);
        craftArmorStand.setCustomNameVisible(true);
        craftArmorStand.setSmall(true);

        EntityArmorStand nameArmorStand = new EntityArmorStand(EntityTypes.c, nmsWorld);
        nameArmorStand.setPosition(location_y.getX(), location_y.getY(), location_y.getZ());
        nameArmorStand.setCustomName(chatBaseComponent2);
        nameArmorStand.setInvisible(true);
        nameArmorStand.setMarker(true);
        nameArmorStand.setCustomNameVisible(true);
        nameArmorStand.setSmall(true);

        PlayerConnection connection = ((CraftPlayer) showTo).getHandle().b;
        connection.sendPacket(new PacketPlayOutSpawnEntity(craftArmorStand));
        connection.sendPacket(new PacketPlayOutSpawnEntity(nameArmorStand));
        connection.sendPacket(new PacketPlayOutEntityMetadata(craftArmorStand.getId(), craftArmorStand.getDataWatcher(), false));
        connection.sendPacket(new PacketPlayOutEntityMetadata(nameArmorStand.getId(), nameArmorStand.getDataWatcher(), false));

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            connection.sendPacket(new PacketPlayOutEntityDestroy(craftArmorStand.getId()));
            craftArmorStand.setRemoved(net.minecraft.world.entity.Entity.RemovalReason.a);
            craftArmorStand.getBukkitEntity().remove();

            connection.sendPacket(new PacketPlayOutEntityDestroy(nameArmorStand.getId()));
            nameArmorStand.setRemoved(net.minecraft.world.entity.Entity.RemovalReason.a);
            nameArmorStand.getBukkitEntity().remove();
        }, 20);


    }

    private int DecreaseExpPerLevel(int GivenExp, int mobLevel, int playerLevel) {
        int absLevel = Math.abs(mobLevel - playerLevel);

        if(10-absLevel<0) return 0;
        else {
            GivenExp = GivenExp * (absLevel / 10);
            return GivenExp;
        }
    }

    private void DistributePartyXP(int GivenExp, Player Distributor) {
        if(PartyManager.getParty(Distributor) == null) return;

        int distributorLevel = PlayerManager.getinstance(Distributor).getlvl();
        List<Player> members = PartyManager.getParty(Distributor).getMembers();
        int membersize = members.size();
        int BonusExp = (int)(GivenExp * ((membersize >= 4) ? 0.2 : 0.05 * membersize));

        for(Player member : members) {
            int memberlevel = PlayerManager.getinstance(member).getlvl();
            if(distributorLevel - memberlevel >= 10) {
                continue;
            }
            else {
                PlayerManager.getinstance(member).setexp(PlayerManager.getinstance(member).getexp() + BonusExp);
                PartyManager.getParty(member).addPartyXP(BonusExp);
            }
        }


    }


}
