package UserStorage;

import SQL.PlayerStorage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class BankGui {
	
	private static BankGui BankGui;
	
	private BankGui() {
		
	}
	
	public static BankGui getinstance() {
		if(BankGui == null) BankGui = new BankGui();
		return BankGui;
	}
	
	public void Open(Player p, String page) {
		
		Inventory gui = Bukkit.createInventory(null, 54, "가상창고 #"+page);
		
		gui.setItem(7, containerdetach_2());
		gui.setItem(8, NextPage(p));
		
		gui.setItem(16, containerdetach_2());
		if(!page.equals("1")) {
			gui.setItem(17, PrevPage());
		}
		else {
			gui.setItem(17, containerdetach());
		}
		
		gui.setItem(26, containerdetach());
		gui.setItem(35, containerdetach());
		gui.setItem(44, containerdetach());
		gui.setItem(53, containerdetach());
		
		gui.setItem(25, containerdetach_2());
		gui.setItem(34, containerdetach_2());
		gui.setItem(43, containerdetach_2());
		gui.setItem(52, containerdetach_2());
		
		
		p.openInventory((new PlayerStorage(p)).storageCall(gui, page));
		//p.openInventory(UserStorageManager.getinstance().Call(p, gui, page));
	}
	
	public void Open_Ask(Player p, String page) {
		
		Inventory gui = Bukkit.createInventory(null, 54, "가상창고 #"+page);
		
		gui.setItem(7, containerdetach_2());
		gui.setItem(8, NextPage_Ask(page));
		
		gui.setItem(16, containerdetach_2());
		gui.setItem(17, PrevPage());
		gui.setItem(26, containerdetach());
		gui.setItem(35, containerdetach());
		gui.setItem(44, containerdetach());
		gui.setItem(53, containerdetach());
		
		gui.setItem(25, containerdetach_2());
		gui.setItem(34, containerdetach_2());
		gui.setItem(43, containerdetach_2());
		gui.setItem(52, containerdetach_2());
		
		
		p.openInventory((new PlayerStorage(p)).storageCall(gui, page));
		//p.openInventory(UserStorageManager.getinstance().Call(p, gui, page));
	}
	
	private ItemStack containerdetach() {
		
		ItemStack item = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		
		return item;
	}
	
	private ItemStack containerdetach_2() {
		
		ItemStack item = new ItemStack(Material.POWERED_RAIL, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(" ");
		item.setItemMeta(meta);
		
		return item;
	}
	
	private ItemStack NextPage(Player p) {
		
		ItemStack item = new ItemStack(Material.LIGHT_BLUE_DYE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&o다음페이지"));
		meta.setLore(Arrays.asList(
				"§7현재 소유 공간 ( "+(new PlayerStorage(p)).maxStoragePage()+" / 10 )",
				"",
				"§3여러 조건들을 해금하여 가상공간을 확장할 수 있습니다!",
				"§3자세한 정보는 메모리칩에서 확인할 수 있습니다"));
		item.setItemMeta(meta);
		
		return item;
	}
	
	private ItemStack NextPage_Ask(String page) {
		
		int pricerate = Integer.parseInt(page);
		
		ItemStack item = new ItemStack(Material.LIGHT_BLUE_DYE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("다음 창고를 열기위해 "+500*Integer.parseInt(page)+" 알테라를 지불하시겠습니까?");
		item.setItemMeta(meta);
		
		return item;
	}
	
	private ItemStack PrevPage() {
		
		ItemStack item = new ItemStack(Material.PINK_DYE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&o이전페이지"));
		item.setItemMeta(meta);
		
		return item;
	}
}
