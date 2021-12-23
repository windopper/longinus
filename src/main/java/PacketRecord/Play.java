package PacketRecord;

import ClassAbility.Combination;
import PacketRecord.Skill.PacketAetherMelee;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.v1_17_R1.scoreboard.CraftScoreboardManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import spellinteracttest.DummyNetworkManager;

import java.io.File;
import java.util.*;

public class Play {

    private static HashMap<Player, Play> instance = new HashMap<>();

    private HashMap<String, EntityPlayer> entityPlayers = new HashMap<>();
    private HashMap<String, Entity> entities = new HashMap<>();


    private Player player;
    private String filename;
    private PlayerConnection conn;

    private ItemStack pauseitem;
    private ItemStack tickforwarditem;
    private ItemStack tickbackwarditem;
    private ItemStack quititem;

    private boolean editmode = false;
    private boolean pause = false;
    private int tickmove = 0;
    private boolean quit = false;
    private int PlayTime = 0;
    private String titleShow = " ";
    private int titletick = 0;

    public Play(Player player, String filename) {
        this.player = player;
        this.filename = filename;
        this.conn = ((CraftPlayer) player).getHandle().b;
        instance.put(player, this);
    }

    public static Play getInstance(Player player) {
        if(instance.containsKey(player)) return instance.get(player);
        return null;
    }

    private void removeinstance() {
        instance.remove(player);
    }

    public void Pause() {
        this.pause = !pause;
        if(!pause) {
            titleShow = "§a재생";
        }
        else {
            titleShow = "§c일시정지";
        }

        titletick = 10;
    }
    public void TickMove(int tick) {
        tickmove = tick;
        if(tick<0) titleShow = "§c<<< "+(-tick)+"틱 이동";
        else titleShow = "§9"+(tick)+"틱 이동 >>>";
        titletick = 10;
    }
    public void Quit() {
        this.quit = true;
        player.getInventory().removeItem(pauseitem);
        player.getInventory().removeItem(tickforwarditem);
        player.getInventory().removeItem(tickbackwarditem);
        player.getInventory().removeItem(quititem);
    }


    public void Edit() {

        ItemStack Pause = new ItemStack(Material.RED_DYE, 1);
        ItemMeta Pausemeta = Pause.getItemMeta();
        Pausemeta.setDisplayName("일시정지");
        Pause.setItemMeta(Pausemeta);

        ItemStack tickforward = new ItemStack(Material.GREEN_DYE, 1 );
        ItemMeta tickforwardmeta = tickforward.getItemMeta();
        tickforwardmeta.setDisplayName("좌클릭시 1틱앞으로 | 우클릭시 20틱앞으로");
        tickforward.setItemMeta(tickforwardmeta);

        ItemStack tickbackward = new ItemStack(Material.BLUE_DYE, 1);
        ItemMeta tickbackwardmeta = tickbackward.getItemMeta();
        tickbackwardmeta.setDisplayName("좌클릭시 1틱뒤로 | 우클릭시 20틱뒤로");
        tickbackward.setItemMeta(tickbackwardmeta);

        ItemStack quit = new ItemStack(Material.YELLOW_DYE, 1);
        ItemMeta quitmeta = quit.getItemMeta();
        quitmeta.setDisplayName("나가기");
        quit.setItemMeta(quitmeta);

        this.pauseitem = Pause;
        this.tickforwarditem = tickforward;
        this.tickbackwarditem = tickbackward;
        this.quititem = quit;

        player.getInventory().addItem(Pause);
        player.getInventory().addItem(tickforward);
        player.getInventory().addItem(tickbackward);
        player.getInventory().addItem(quit);


        editmode = true;
        Play();


    }

    public void Play() {


        File file = new File(filename+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();


        Bukkit.broadcastMessage("플레이 타임 계산 중..");

        int FileLength = 0;
        while(config.contains(filename+"."+FileLength)) {
            FileLength++;
        }

        Bukkit.broadcastMessage("플레이 타임은 총 "+FileLength+"틱 입니다");
        this.PlayTime = FileLength;

        for(String name : config.getConfigurationSection(filename+".1.players").getKeys(false)) {
            //Bukkit.broadcastMessage(name);

            if(name.equals("entities")) continue;

            String world = config.getString(filename+".info."+name+".world");

            double x = config.getDouble(filename+".1.players."+name+"."+"x");
            double y = config.getDouble(filename+".1.players."+name+"."+"y");
            double z = config.getDouble(filename+".1.players."+name+"."+"z");
            float yaw = (float) config.getDouble(filename+".1.players."+name+"."+"yaw");
            float pitch = (float) config.getDouble(filename+".1.players."+name+"."+"pitch");

            Location location = config.getLocation(filename+".1.players."+name+".location");
            String texture = config.getString(filename+".info."+name+".texture");
            String signature = config.getString(filename+".info."+name+".signature");
            ItemStack handitem = config.getItemStack(filename+".info."+name+".handitem");

            WorldServer nmsWorld = ((CraftWorld) Bukkit.getWorld(world)).getHandle();
            GameProfile egameProfile = new GameProfile(UUID.randomUUID(), name);
            Property eproperty = new Property("textures", texture, signature);
            egameProfile.getProperties().put("textures", eproperty);

            EntityPlayer entityPlayer = new EntityPlayer(nmsServer, nmsWorld, egameProfile);

            entityPlayer.b = new PlayerConnection(nmsServer, new DummyNetworkManager(EnumProtocolDirection.a), entityPlayer);
            entityPlayer.setInvulnerable(true);
            entityPlayer.setLocation(x, y, z, yaw, pitch);
            nmsWorld.addEntity(entityPlayer);

            if(handitem != null) {
                CraftItemStack cis = CraftItemStack.asCraftCopy(handitem);
                Pair<EnumItemSlot, net.minecraft.world.item.ItemStack> pair =
                        new Pair<>(EnumItemSlot.a, CraftItemStack.asNMSCopy(cis));
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
                    sendpacket(player, new PacketPlayOutEntityEquipment(entityPlayer.getId(), Arrays.asList(pair)));
                }, 5);

            }

            entityPlayers.put(name, entityPlayer);
        }

        // 엔티티들
        if(config.contains(filename+".0.entities")) {
            for(String uuid : config.getConfigurationSection(filename+".0.entities").getKeys(false)) {
//                double x = config.getDouble(filename+".0.entities."+uuid+"."+"x");
//                double y = config.getDouble(filename+".0.entities."+uuid+"."+"y");
//                double z = config.getDouble(filename+".0.entities."+uuid+"."+"z");
//                float yaw = (float) config.getDouble(filename+".0.entities."+uuid+"."+"yaw");
//                float pitch = (float) config.getDouble(filename+".0.entities."+uuid+"."+"pitch");
                String world = config.getString(filename+".info."+uuid+".world");

                String entitytype = config.getString(filename+".info."+uuid+".entity");

                Entity entity = (new PacketSummonEntity(entitytype, UUID.fromString(uuid)
                        , ((CraftWorld) Bukkit.getWorld(world)).getHandle()).getEntity());

                if(entity!=null) {
                    entities.put(uuid, entity);
                    ((CraftWorld) Bukkit.getWorld(world)).getHandle().addEntity(entity);
                }
            }
        }

        showEntityPlayer();
        showEntities();

        new BukkitRunnable() {

            int time = 0;

            int minute = 0;
            int second = 0;
            int mili = 0;

            final int endminute = (PlayTime-1) / 20 / 60;
            final int endsecond = ((PlayTime-1) / 20) % 60;
            final int endmili = (PlayTime-1) % 20 * 5;
            String endtime = "§a"+endminute+" : "+endsecond+" : "+endmili;

            @Override
            public void run() {

                showEntities();

                //플레이어 보여주기
                if(config.contains(filename+"."+Integer.toString(time)+".players")) {

                    for(String name : config.getConfigurationSection(filename+".1.players").getKeys(false)) {

                        if(name.equals("entities")) continue;

                        String filepath = filename+"."+Integer.toString(time)+".players."+name;

                        double x = config.getDouble(filepath+".x");
                        double y = config.getDouble(filepath+".y");
                        double z = config.getDouble(filepath+".z");
                        float yaw = (float) config.getDouble(filepath+".yaw");
                        float pitch = (float) config.getDouble(filepath+".pitch");

                        boolean swing = config.getBoolean(filepath+".armswing");
                        boolean takedamage = config.getBoolean(filepath+".takedamage");
                        boolean sneaking = config.getBoolean(filepath+".sneaking");
                        int combo = config.getInt(filepath+".combo");
                        String skill = config.getString(filepath+".skill");
                        String Class = config.getString(filepath+".class");

                        EntityPlayer entityPlayer = entityPlayers.get(name);
                        //entityPlayer.setPositionRotation(x, y, z, yaw, pitch);
                        org.bukkit.entity.Entity eP = entityPlayer.getBukkitEntity();
                        eP.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));

                        if(swing) {
                            sendpacket(player, new PacketPlayOutAnimation(entityPlayer, 0));
                        }
                        if(takedamage) {
                            sendpacket(player, new PacketPlayOutAnimation(entityPlayer, 1));
                            player.playSound(eP.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
                        }
                        if(sneaking) {
                            entityPlayer.setSneaking(true);
                            DataWatcher dataWatcher = new DataWatcher(null);
                            dataWatcher.register(new DataWatcherObject<>(6, DataWatcherRegistry.s), EntityPose.f);
                            sendpacket(player, new PacketPlayOutEntityMetadata(entityPlayer.getId(), dataWatcher, true));
                        }
                        else {
                            entityPlayer.setSneaking(false);
                            DataWatcher dataWatcher = new DataWatcher(null);
                            dataWatcher.register(new DataWatcherObject<>(6, DataWatcherRegistry.s), EntityPose.a);
                            sendpacket(player, new PacketPlayOutEntityMetadata(entityPlayer.getId(), dataWatcher, true));
                        }
                        if(skill != null) {
                            if(!skill.equals(""))
                                CallSkill(Class, skill, combo, entityPlayer);
                        }
                    }
                }

                // 엔티티 보여주기
                if(config.contains(filename+"."+Integer.toString(time)+".entities")) {

                    for(String uuid : config.getConfigurationSection(filename+"."+Integer.toString(time)+".entities").getKeys(false)) {

                        if(!entities.containsKey(uuid)) continue;

                        if(entities.get(uuid).isRemoved()) {
                            String entitytype = config.getString(filename+".info."+uuid+".entity");
                            Entity entity = (new PacketSummonEntity(entitytype, UUID.fromString(uuid)
                                    , ((CraftWorld) player.getWorld()).getHandle()).getEntity());
                            entities.replace(uuid, entity);

                            ((CraftWorld) player.getWorld()).getHandle().addEntity(entity);
                        }

                        Entity entity = entities.get(uuid);

                        // 다음 틱에 엔티티가 없는 상태면 == 죽은 상태라고 간주하고 제거
                        if(!config.contains(filename+"."+Integer.toString(time+1)+".entities."+uuid)) {
                            PlayerConnection conn = ((CraftPlayer) player).getHandle().b;
                            conn.sendPacket(new PacketPlayOutEntityDestroy(entity.getId()));
                            org.bukkit.entity.Entity eE = (org.bukkit.entity.Entity) (entity.getBukkitEntity());
                            if(eE instanceof LivingEntity) ((LivingEntity) eE).setHealth(0);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
                                entity.setRemoved(Entity.RemovalReason.a);
                                entity.getBukkitEntity().remove();
                            }, 10);
                        }

                        String filepath = filename+"."+Integer.toString(time)+".entities."+uuid;

                        double x = config.getDouble(filepath+".x");
                        double y = config.getDouble(filepath+".y");
                        double z = config.getDouble(filepath+".z");
                        float yaw = (float) config.getDouble(filepath+".yaw");
                        float pitch = (float) config.getDouble(filepath+".pitch");
                        boolean takedamage = config.getBoolean(filepath+".takedamage");
                        boolean death = config.getBoolean(filepath+".death");

                        entity.getBukkitEntity().teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));

                        if(takedamage) {
                            sendpacket(player, new PacketPlayOutAnimation(entity, 1));
                            player.playSound(entity.getBukkitEntity().getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
                        }
                        if(death) {
                            PlayerConnection conn = ((CraftPlayer) player).getHandle().b;
                            conn.sendPacket(new PacketPlayOutEntityDestroy(entity.getId()));
                            entity.setRemoved(Entity.RemovalReason.a);
                            entity.getBukkitEntity().remove();
                        }
                    }
                }

                if(!pause)
                    time++;
                if(Math.abs(tickmove)>=1 && time + tickmove >0 && time + tickmove < PlayTime-1) {
                    time += tickmove;
                    tickmove = 0;
                }
                if(quit) {
                    PlayerConnection conn = ((CraftPlayer) player).getHandle().b;

                    for(EntityPlayer eP : entityPlayers.values()) {
                        conn.sendPacket(new PacketPlayOutEntityDestroy(eP.getId()));
                        eP.setRemoved(Entity.RemovalReason.a);
                    }
                    for(Entity eE : entities.values()) {
                        conn.sendPacket(new PacketPlayOutEntityDestroy(eE.getId()));
                        eE.setRemoved(Entity.RemovalReason.a);
                        eE.getBukkitEntity().remove();
                    }
                    removeinstance();
                    cancel();
                }

                // 보여줄 영상이 없으면
                if(!config.contains(filename+"."+Integer.toString(time))) {
                    // 편집모드라면 고정
                    if(editmode) {
                        time-=1;
                    }
                    // 감상모드라면 제거
                    else {
                        PlayerConnection conn = ((CraftPlayer) player).getHandle().b;

                        for(EntityPlayer eP : entityPlayers.values()) {
                            conn.sendPacket(new PacketPlayOutEntityDestroy(eP.getId()));
                            eP.setRemoved(Entity.RemovalReason.a);
                        }
                        for(Entity eE : entities.values()) {
                            conn.sendPacket(new PacketPlayOutEntityDestroy(eE.getId()));
                            eE.setRemoved(Entity.RemovalReason.a);
                            eE.getBukkitEntity().remove();
                        }

                        removeinstance();
                        cancel();
                    }
                }

                // 시간 타이틀
                if(editmode) {
                    minute = time / 20 / 60;
                    second = (time / 20) % 60;
                    mili = time % 20 * 5;

                    player.sendTitle(titleShow, minute+" : "+second+" : "+mili + " / " + endtime, 0, 10, 0);
                    if(titletick>0) titletick--;
                    else if(titletick == 0) titleShow = " ";
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 10, 1);
    }

    private void showEntityPlayer() {

        for(EntityPlayer npc : entityPlayers.values()) {


            PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(
                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a,
                    npc
            );
            sendpacket(player, packetPlayOutPlayerInfo);

            PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn(
                    npc
            );
            sendpacket(player, packetPlayOutNamedEntitySpawn);

            CraftScoreboardManager scoreboardManager = ((CraftServer) Bukkit.getServer()).getScoreboardManager();
            assert scoreboardManager != null;

            CraftScoreboard mainScoreboard = scoreboardManager.getNewScoreboard();
            Scoreboard scoreboard = mainScoreboard.getHandle();

            ScoreboardTeam scoreboardTeam = scoreboard.getPlayerTeam(npc.getName());


            if (scoreboardTeam == null) {
                scoreboardTeam = scoreboard.createTeam("Actors");
                scoreboard.addPlayerToTeam(npc.getName(), scoreboardTeam);
            }
            else {
                scoreboard.addPlayerToTeam(npc.getName(), scoreboardTeam);
            }


            Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
                try {
                    PacketPlayOutPlayerInfo removeFromTabPacket = new PacketPlayOutPlayerInfo(
                            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e,
                            npc
                    );
                    sendpacket(player, removeFromTabPacket);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }, 20);

            Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
                fixSkinHelmetLayerForPlayer(npc, player);
            }, 8);

        }

    }

    private void showEntities() {

        for(Entity entity : entities.values()) {

            for(Player p : Bukkit.getOnlinePlayers()) {

                if(p.getName().equals(player.getName())) continue;

                sendpacket(p, new PacketPlayOutEntityDestroy(entity.getBukkitEntity().getEntityId()));
            }
        }
    }

    private void fixSkinHelmetLayerForPlayer(EntityPlayer npc, Player player) {

        DataWatcher dataWatcher = npc.getDataWatcher();
        dataWatcher.set(new DataWatcherObject<>(17, DataWatcherRegistry.a), (byte) 127);

        conn.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), dataWatcher, true));
    }


    private void sendpacket(Player player, Packet<?> packet) {

        PlayerConnection conn = ((CraftPlayer) player).getHandle().b;
        conn.sendPacket(packet);
    }

    private void CallSkill(String Class, String Skill, int Combo, EntityPlayer entityPlayer) {

        if(Class.equals(Combination.Classes.아이테르.name())) {
            (new PacketAetherMelee(entityPlayer, player)).Melee(Skill, Combo);
        }


    }

}
