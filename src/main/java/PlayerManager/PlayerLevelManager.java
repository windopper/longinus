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
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import spellinteracttest.Centermsg;
import spellinteracttest.Main;

import java.util.*;

public class PlayerLevelManager {

    private static PlayerLevelManager playerLevelManager;

    private static int[] exptable = {70, 133, 229, 360, 515, 671, 873, 1135, 1474, 1912, 2478, 3209, 4151, 5365, 6927, 8935, 10949, 13410, 16416, 20086, 24565, 30028, 36687, 44801, 54682, 66709, 81341, 99132, 120754, 147019, 178908, 217605, 264539,
            321436, 390375, 473863, 574918, 697175, 845008, 1023676, 1239502, 1500081, 1814534, 2193806, 2651027, 3201938, 3865400, 4664003, 5624786, 6780098};

    private int MAXLEVEL = 50;

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

    public void expWatcher() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            PlayerManager pM = PlayerManager.getinstance(player);
            if(pM.getlvl() >= MAXLEVEL) {
                player.setLevel(MAXLEVEL);
                player.setExp(0);
                continue;
            }

            while(exptable[pM.getlvl()-1] < pM.getexp()) {
                pM.setexp(pM.getexp() - exptable[pM.getlvl()-1]);
                pM.setlvl(pM.getlvl()+1);
                levelUpEvent(player, pM.getlvl());
            }
            player.setExp((float)pM.getexp() / (float)exptable[pM.getlvl()-1]);
            player.setLevel(pM.getlvl());
        }
    }

    private void levelUpEvent(Player player, int lvl) {

        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);

        for(Entity lE : player.getNearbyEntities(15, 15, 15)) {
            if(lE instanceof Player) {
                ((Player) lE).sendMessage("§d"+player.getName()+"님이 "+lvl+"레벨에 달성하였습니다");
            }
        }

        Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fireworkMeta = fw.getFireworkMeta();
        fireworkMeta.setPower(1);
        fireworkMeta.addEffect(FireworkEffect.builder().flicker(true).withColor(Color.WHITE).withTrail().build());
        fw.setFireworkMeta(fireworkMeta);

        PlayerManager pM = PlayerManager.getinstance(player);

        Centermsg.CenteredMessage(player, "");
        Centermsg.CenteredMessage(player, "");
        Centermsg.CenteredMessage(player, "§e§l§o레벨업! "+pM.getlvl()+"레벨에 도달하였습니다!");
        Centermsg.CenteredMessage(player, "");
        Centermsg.CenteredMessage(player, "");

        //TODO 직업 능력 해금, 퀘스트 해금, 레벨 상승으로 얻은 것들 표기

        PlayerHealthShield pHS = PlayerHealthShield.getinstance(player);
        pHS.setCurrentHealth(pM.Health);
        PlayerEnergy pE = PlayerEnergy.getinstance(player);
        pE.setEnergy(20);

    }


}
