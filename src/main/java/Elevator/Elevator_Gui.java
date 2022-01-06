package Elevator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class Elevator_Gui {

    public void EL_Icon(String display, Material ID, int amount, List<String> Lore, int loc, Inventory inv_EL)
    {	//엘리베이터 아이콘을 아이템으로 불러오는 메소드이다.
        ItemStack Icon = new ItemStack(ID, amount);
        ItemMeta Icon_Info = Icon.getItemMeta();
        Icon_Info.setDisplayName(display);
        Icon_Info.setLore(Lore);
        Icon.setItemMeta(Icon_Info);
        inv_EL.setItem(loc, Icon);
    }

    public void EL_GUI(Player p)
    {	//엘리베이터의 GUI이다.
        Inventory inv_EL = Bukkit.createInventory(null, 9, ChatColor.DARK_PURPLE + "엘리베이터");
        EL_Icon("§b§l격납고로 이동", Material.END_CRYSTAL, 1, Arrays.asList("§3§n클릭시 이동합니다"), 4, inv_EL);
        EL_Icon("§b§l로비1로 이동", Material.END_CRYSTAL, 1, Arrays.asList("§3§n클릭시 이동합니다"), 0, inv_EL);
        EL_Icon("§b§l로비2로 이동", Material.END_CRYSTAL, 1, Arrays.asList("§3§n클릭시 이동합니다"), 1, inv_EL);
        EL_Icon("§d§l<함내시설>", Material.BOOK, 1, Arrays.asList("§3§n클릭시 함내시설 메뉴를 보여줍니다"), 8, inv_EL);
        p.openInventory(inv_EL);
    }

    public void EL_GUI_facility(Player p)
    {	//엘리베이터의 GUI 함내시설이다.
        Inventory inv_EL_facility = Bukkit.createInventory(null, 9, ChatColor.DARK_PURPLE + "<함내시설>");
        EL_Icon("§b§l회의실로 이동", Material.BEACON, 1, Arrays.asList("§3§n클릭시 이동합니다"), 0, inv_EL_facility);
        EL_Icon("§b§l연구실로 이동", Material.BEACON, 1, Arrays.asList("§3§n클릭시 이동합니다"), 1, inv_EL_facility);
        EL_Icon("§b§l함장실로 이동", Material.BEACON, 1, Arrays.asList("§3§n클릭시 이동합니다"), 2, inv_EL_facility);
        EL_Icon("§d§l<메인메뉴>", Material.BOOK, 1, Arrays.asList("§3§n클릭시 메인 메뉴로 돌아갑니다"), 8, inv_EL_facility);
        p.openInventory(inv_EL_facility);
    }
}
