package PacketListener;

import io.netty.channel.*;
import net.minecraft.network.protocol.game.PacketPlayInFlying;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PacketListener implements Listener {

    @EventHandler
    public void onjoin(PlayerJoinEvent event){
        injectPlayer(event.getPlayer());
    }

    @EventHandler
    public void onleave(PlayerQuitEvent event){
        removePlayer(event.getPlayer());
    }
    private void removePlayer(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().b.a.k;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }

    private void injectPlayer(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {

            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                //Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "PACKET READ: " + ChatColor.RED + packet.toString());
                if(packet instanceof PacketPlayInFlying.PacketPlayInPosition) {
                    PacketPlayInFlying.PacketPlayInPosition packetPlayInPosition = (PacketPlayInFlying.PacketPlayInPosition) packet;
                    //Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "PACKET READ: " + ChatColor.RED + packet.toString());
                    double x = packetPlayInPosition.a;
                    double y = packetPlayInPosition.b;
                    double z = packetPlayInPosition.c;
                    //int id = packetPlayInPosition.

                    //Bukkit.broadcastMessage(Double.toString(x) +" "+Double.toString(y)+" "+Double.toString(z));
//
//                    for(EntityPlayer npc : Defender.npclist.keySet()) {
//                        if(id == npc.getId()) {
//                            Location loc = new Location(npc.getBukkitEntity().getWorld(), x, y, z);
//                            Defender.npclist.replace(npc, loc);
//                        }
//                    }
                }

                if(packet instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook) {
                    PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packetPlayOutPosition = (PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook) packet;
                    //Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "PACKET READ: " + ChatColor.RED + packet.toString());
                }

                if(packet instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMove) {
                    PacketPlayOutEntity.PacketPlayOutRelEntityMove packetPlayOutPosition = (PacketPlayOutEntity.PacketPlayOutRelEntityMove) packet;
                    //Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "PACKET READ: " + ChatColor.RED + packet.toString());
                }

                super.channelRead(channelHandlerContext, packet);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
                //Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "PACKET WRITE: " + ChatColor.GREEN + packet.toString());

                if(packet instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook) {
                    PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook packetPlayOutPosition = (PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook) packet;
                    //Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "PACKET READ: " + ChatColor.RED + packet.toString());
                }

                if(packet instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMove) {
                    PacketPlayOutEntity.PacketPlayOutRelEntityMove packetPlayOutPosition = (PacketPlayOutEntity.PacketPlayOutRelEntityMove) packet;
                    //Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "PACKET READ: " + ChatColor.RED + packet.toString());
                }




                super.write(channelHandlerContext, packet, channelPromise);
            }


        };

        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().b.a.k.pipeline();
        pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);

    }
}
