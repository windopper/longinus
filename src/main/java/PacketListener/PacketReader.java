package PacketListener;

import QuestFunctions.QuestNPCManager;
import Shop.RightClickNPC;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import spellinteracttest.Main;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PacketReader {

	public final static Map<UUID, Channel> channels = new HashMap<>();
	Channel channel;
	private int count = 0;
	private Player player;

	public PacketReader(Player player) {
		this.player = player;
	}

	public void inject(Player player) {
		CraftPlayer craftPlayer = (CraftPlayer) player;
		channel = craftPlayer.getHandle().b.a.k;
		channels.put(player.getUniqueId(), channel);
		
		if(channel.pipeline().get("PacketInjector") != null)
			return;
		
		channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<PacketPlayInUseEntity>() {

			@Override
			protected void decode(ChannelHandlerContext channelHandlerContext, PacketPlayInUseEntity packetPlayInUseEntity, List<Object> list) throws Exception {
				list.add(packetPlayInUseEntity);
				readPacket(packetPlayInUseEntity);
			}

		});


	}
	
	public void uninject(Player player) {
		channel = channels.get(player.getUniqueId());
		if(channel.pipeline().get("PacketInjector") != null)
			channel.pipeline().remove("PacketInjector");
	}
	
	public void readPacket(PacketPlayInUseEntity packetPlayInUseEntity) {

		count++;
		if(count == 4) {
			count = 0;
			int entityID = (int) getValue(packetPlayInUseEntity, "a");

			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(new RightClickNPC(player, QuestNPCManager.getQuestNPCID(entityID)));
				}
			}, 0);

		}
	}
	
	private Object getValue(Object instance, String name) {

		Object result = null;
		
		try {
			
			Field field = instance.getClass().getDeclaredField(name);
			field.setAccessible(true);
			
			result = field.get(instance);
			
			field.setAccessible(false);
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
