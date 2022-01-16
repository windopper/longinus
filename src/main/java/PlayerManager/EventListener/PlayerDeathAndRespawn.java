package PlayerManager.EventListener;

import CustomEvents.PlayerDeathEvent;
import Duel.DuelManager;
import EntityPlayerManager.EntityPlayerManager;
import EntityPlayerManager.EntityPlayerWatcher;
import PlayerManager.PlayerDeadBodySetter;
import Mob.EntityManager;
import PlayerManager.PlayerHealthShield;
import PlayerManager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerDeathAndRespawn implements Listener {

    @EventHandler
    public void PlayerDeath(PlayerDeathEvent e) {

        Player player = e.getPlayer();

        if (DuelManager.checkInDuel(player)) {
            DuelManager.getDuelManager(player).setLoser(player);
            PlayerHealthShield.getinstance(player).setCurrentHealth(PlayerManager.getinstance(player).Health);
            return;
        }

        if(!player.isDead()) {
            player.setHealth(0); // 안 죽었을때 체력 0
            PlayerHealthShield.getinstance(player).setCurrentHealth(0);
        }
        else
        {
            //player.spigot().respawn();
        }

        Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
            EntityManager.getDisguiseEntitiesPlayer().stream().forEach(value -> EntityPlayerWatcher.Remove(value, player));
        }, 5);

        Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
            EntityManager.getDisguiseEntitiesPlayer().stream().forEach(value -> EntityPlayerManager.getInstance().showTo(value, player));
        },20);

        (new PlayerDeadBodySetter(player)).init();

    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        PlayerHealthShield.getinstance(player).setCurrentHealth(PlayerManager.getinstance(player).Health);
    }
}
