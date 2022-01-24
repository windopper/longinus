package PlayerManager;

import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import spellinteracttest.Main;

import java.lang.reflect.Field;
import java.util.*;

public class PlayerPacketHandler implements Listener {

    private final static HashMap<Player, Set<Handlers>> queries = new HashMap<>();
    private static PlayerPacketHandler playerPacketHandler;
    private boolean glowDelay = false;
    private PlayerPacketHandler() {

    }
    public static PlayerPacketHandler getInstance() {
        if(playerPacketHandler == null) playerPacketHandler = new PlayerPacketHandler();
        return playerPacketHandler;
    }

    @EventHandler
    public void onEnable(ServerLoadEvent event) {
        LoopHandler();
    }

    @EventHandler
    public void Quit(PlayerQuitEvent event) {
        queries.remove(event.getPlayer());
    }

    public enum HandleType {
        Sneaking((byte) 0x02),
        Sprinting((byte) 0x08),
        Swimming((byte) 0x10),
        Invisible((byte) 0x20),
        Flying((byte) 0x80),
        Glowing((byte) 0x40);

        byte bitMask;

        HandleType(byte bitMask) {
            this.bitMask = bitMask;
        }

        byte getBitMask() {
            return bitMask;
        }
    }

    public enum QueryType {
        REMOVE,
        ADD,
    }

    public enum HandleOption {
        ON,
        OFF,
        DEFAULT,
    }

    public Handlers createOrGetQuery(Player player, String handleName) {
        Handlers handlers;
        if(queries.containsKey(player)) {
            if(queries.get(player).stream().map(Handlers::getHandleName).toList().contains(handleName)) {
                return queries.get(player).stream().filter(q->q.getHandleName().equals(handleName)).toList().get(0);
            }
            else {
                handlers = new Handlers(handleName);
                queries.get(player).add(handlers);
            }
        }
        else {
            handlers = new Handlers(handleName);
            Set<Handlers> set = new HashSet<>();
            set.add(handlers);
            queries.put(player, set);
        }
        return handlers;
    }

    public void removeQuery(Player player, String handleName) {
        if(queries.containsKey(player)) {
            Handlers handlers = queries.get(player)
                    .stream()
                    .filter((q)->q.handleName.equals(handleName)).toList().get(0);
            if(handlers != null) queries.get(player).remove(handlers);
            if(queries.get(player).size() == 0) queries.remove(player);
        }
    }

    public void LoopHandler() {

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {

                    byte base = 0x00;
                    for(HandleType handleType : HandleType.values()) {
                        base |= getBitMaskFromPlayersAction(handleType, player);
                    }

                    DataWatcher dataWatcher_ = ((CraftPlayer) player).getHandle().getDataWatcher();
                    dataWatcher_.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), base);
                    for(Player others : Bukkit.getOnlinePlayers()) {
                        PlayerConnection connection = ((CraftPlayer) others).getHandle().b;
                        connection.sendPacket(new PacketPlayOutEntityMetadata(player.getEntityId(), dataWatcher_, true));
                    }

                    Set<Handlers> handlersSet = queries.get(player);
                    if(handlersSet == null) continue;

                    for(Handlers handler : handlersSet) {
                        byte bitmask = handler.getBitMask(player);
                        DataWatcher dataWatcher = ((CraftPlayer) player).getHandle().getDataWatcher();
                        dataWatcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), bitmask);
                        for(Player showTo : handler.getShowTo()) {
                            PlayerConnection connection = ((CraftPlayer) showTo).getHandle().b;
                            connection.sendPacket(new PacketPlayOutEntityMetadata(player.getEntityId(), dataWatcher, true));
                        }
                    }
                }

                glowDelay = !glowDelay;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);


    }

    public class Handlers {

        private HandleOption Sneaking = HandleOption.DEFAULT;
        private HandleOption Sprinting = HandleOption.DEFAULT;
        private HandleOption Swimming = HandleOption.DEFAULT;
        private HandleOption Invisible = HandleOption.DEFAULT;
        private HandleOption Flying = HandleOption.DEFAULT;
        private HandleOption Glowing = HandleOption.DEFAULT;

        private List<Player> showTo = new ArrayList<>();
        private String handleName;

        Handlers(String handleName) {
            this.handleName = handleName;
        }

        public void setHandlers(HandleType handleType, HandleOption handleOption) {
            try {
                Field field = this.getClass().getDeclaredField(handleType.name());
                field.set(this, handleOption);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        public void addShowTo(Player player) {
            showTo.add(player);
        }

        public void addShowTo(List<Player> players) {
            showTo.addAll(players);
        }

        public void updateShowTo(List<Player> players) {
            showTo.clear();
            showTo.addAll(players);
        }

        public void removeShowTo(Player player) {
            showTo.remove(player);
        }

        public void removeShowTo(List<Player> players) {
            showTo.removeAll(players);
        }

        public String getHandleName() {
            return handleName;
        }

        public List<Player> getShowTo() {
            return showTo;
        }

        public byte getBitMask(Player player) {
            byte base = 0x00;

            try {
                List<String> handleTypes = Arrays.stream(HandleType.values()).map(m->m.name()).toList();
                for(String handleType : handleTypes) {
                    Field field = this.getClass().getDeclaredField(handleType);
                    HandleOption handleOption = (HandleOption) field.get(this);
                    switch (handleOption) {
                        case DEFAULT -> base |= getBitMaskFromPlayersAction(HandleType.valueOf(handleType), player);
                        case OFF -> base |= 0x00;
                        case ON -> base |= HandleType.valueOf(handleType).getBitMask();
                    }
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return base;
        }
    }

    private byte getBitMaskFromPlayersAction(HandleType handleType, Player player) {
        if(handleType == HandleType.Flying) return player.isGliding() ? handleType.getBitMask() : 0x00;
        else if(handleType == HandleType.Invisible) return player.isInvisible() ? handleType.getBitMask() : 0x00;
        else if(handleType == HandleType.Sneaking) return player.isSneaking() ? handleType.getBitMask() : 0x00;
        else if(handleType == HandleType.Sprinting) return player.isSprinting() ? handleType.getBitMask() : 0x00;
        else if(handleType == HandleType.Swimming) return player.isSwimming() ? handleType.getBitMask() : 0x00;
        else if(handleType == HandleType.Glowing) return player.isGlowing() && glowDelay ? handleType.getBitMask() : 0x00;
        else return 0x00;
    }
}
