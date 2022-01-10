package Elevator;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class Elevator_Events implements Listener
{
    boolean s = false;
    Elevator_Gui GUI = new Elevator_Gui();
    Location hangar = new Location(Bukkit.getServer().getWorld("world"), 15.5, 52, -17.5);
    Location hangar_exit = new Location(Bukkit.getServer().getWorld("world"), 9.5, 52, -17.5, 90, 0);

    Location lobby1 = new Location(Bukkit.getServer().getWorld("world"), 14.5, 145, 22.5);
    Location lobby1_exit = new Location(Bukkit.getServer().getWorld("world"), 10.5, 145, 22.5, -180, 0);

    Location lobby2 = new Location(Bukkit.getServer().getWorld("world"), -14.5, 145, 10.5);
    Location lobby2_exit = new Location(Bukkit.getServer().getWorld("world"), -14.5, 145, 6.5, -90, 0);

    Location inf_ctr = new Location(Bukkit.getServer().getWorld("world"), -136.5, 126, -98.5);
    Location inf_ctr_exit = new Location(Bukkit.getServer().getWorld("world"), -140.5, 126, -98.5, 180, 0);
    //<함내시설>
    Location meeting_room = new Location(Bukkit.getServer().getWorld("world"), -51.5, 125, -46);
    Location meeting_room_exit = new Location(Bukkit.getServer().getWorld("world"), -55.5, 125, -46.5, 180, 0);

    Location lab = new Location(Bukkit.getServer().getWorld("world"), -47.5, 126, 9.5);
    Location lab_exit = new Location(Bukkit.getServer().getWorld("world"), -51.5, 126, 9.5, -180, 0);

    Location captain_room = new Location(Bukkit.getServer().getWorld("world"), -52.5, 235, -12.5);
    Location captain_room_exit = new Location(Bukkit.getServer().getWorld("world"), -52.5, 235, -16, -90, 0);

    /*Location LocArr[] = {hangar, hangar_exit, lobby1, lobby1_exit, lobby2, lobby2_exit, inf_ctr, inf_ctr_exit, meeting_room, meeting_room_exit,
            lab, lab_exit, captain_room, captain_room_exit};

    List<String> inv_list = Arrays.asList(new String[]{"격납고로 이동","로비1로 이동","로비2로 이동", "<함내시설>", "회의실로 이동", "연구실로 이동", "함장실로 이동"});*/

    @EventHandler
    public void EL_B(InventoryClickEvent event)
    {	//엘리베이터 버튼 클릭 이벤트.
        Player p = (Player) event.getWhoClicked();

        if(ChatColor.stripColor(event.getView().getTitle()).equals("엘리베이터")) event.setCancelled(true);
        if(ChatColor.stripColor(event.getView().getTitle()).equals("<함내시설>")) event.setCancelled(true);

        if((event.getCurrentItem()==null) || !event.getCurrentItem().hasItemMeta() || (event.getCurrentItem().getType() == null)) return;

        EL_Teleport(event, p, "격납고", hangar, hangar_exit);
        EL_Teleport(event, p, "로비1", lobby1, lobby1_exit);
        EL_Teleport(event, p, "로비2", lobby2, lobby2_exit);
        EL_Teleport(event, p, "정보실", inf_ctr, inf_ctr_exit);

        if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equals("<함내시설>")) GUI.EL_GUI_facility(p);
        if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equals("<메인메뉴>")) GUI.EL_GUI(p);

        EL_Teleport(event, p, "연구실", lab, lab_exit);
        EL_Teleport(event, p, "회의실", meeting_room, meeting_room_exit);
        EL_Teleport(event, p, "함장실", captain_room, captain_room_exit);
    }

    public void EL_Teleport(InventoryClickEvent event, Player p, String Loc_Name, Location Loc, Location Loc_exit)
    {
        if(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equals(Loc_Name + "로 이동"))
        {
            if(p.getLocation().distance(Loc) < 1.5)
            {
                p.sendMessage(ChatColor.RED + ("이미 현재 " + Loc_Name + "입니다"));
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.7f, 1);
            }
            else
            {
                p.sendMessage(ChatColor.YELLOW + (Loc_Name + "로 이동합니다"));
                p.teleport(Loc_exit);
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            }
        }
    }
    public static final List<Player> Player_list = new ArrayList<>();
    public static final List<String> inv_list = new ArrayList<>();
    public void runnable()
    {
        //플레이어의 위치를 실시간으로 받아와 GUI를 보여준다
        List<ItemStack> Player_items = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers())
        {
            for (int i = 0; i < 40; i++) {
                Player_items.add(p.getInventory().getItem(i));
            }
            for (ItemStack item : Player_items) {
                if (item == null) continue;
                if (item.getItemMeta() == null) continue;
                for (String inv_items : inv_list)
                {
                    if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals(inv_items))
                        p.getInventory().remove(item);
                }
            }
            if (p.getWorld().getName().equals(Bukkit.getServer().getWorld("world").getName()))
            {
                if ((p.getLocation().distance(hangar) < 1.5) || (p.getLocation().distance(lobby1) < 1.5) || (p.getLocation().distance(lobby2) < 1.5) || (p.getLocation().distance(inf_ctr) < 1.5) ||
                        (p.getLocation().distance(meeting_room) < 1.5) || (p.getLocation().distance(lab) < 1.5) || (p.getLocation().distance(captain_room) < 1.5))
                {
                    if (!Player_list.contains(p))
                    {
                        GUI.EL_GUI(p);
                        Player_list.add(p);
                    }
                } else Player_list.remove(p);
            }
        }
    }
}
