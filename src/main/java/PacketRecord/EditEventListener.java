package PacketRecord;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EditEventListener implements Listener {

    @EventHandler
    public void RLClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(Play.getInstance(player) != null) {

            Play play = Play.getInstance(player);

            if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                try {
                    String name = ChatColor.stripColor(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName());

                    if(name.equals("일시정지")) play.Pause();
                    else if(name.equals("좌클릭시 1틱앞으로 | 우클릭시 20틱앞으로")) play.TickMove(20);
                    else if(name.equals("좌클릭시 1틱뒤로 | 우클릭시 20틱뒤로")) play.TickMove(-20);
                    else if(name.equals("나가기")) play.Quit();

                }
                catch(Exception e) {

                }
            }
            else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {

                try {
                    String name = ChatColor.stripColor(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName());

                    if(name.equals("일시정지")) play.Pause();
                    else if(name.equals("좌클릭시 1틱앞으로 | 우클릭시 20틱앞으로")) play.TickMove(1);
                    else if(name.equals("좌클릭시 1틱뒤로 | 우클릭시 20틱뒤로")) play.TickMove(-1);
                    else if(name.equals("나가기")) play.Quit();

                }
                catch(Exception e) {

                }

            }


        }
    }

    @EventHandler
    public void DamageEvent(EntityDamageEvent event) {

    }
}
