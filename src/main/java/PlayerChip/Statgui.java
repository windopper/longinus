package PlayerChip;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import PlayerManager.PlayerStatManager;

public class Statgui {

	private static Statgui Statgui;
	private static final String blank = "          ";
	
	private Statgui() {
		
	}
	
	public static Statgui getinstance() {
		if(Statgui == null) Statgui = new Statgui();
		return Statgui;
	}
	
	public void StatGuiOpen(Player p) {
		
		Inventory gui = Bukkit.createInventory(null, 36, "Stat");
		
		gui.setItem(11, StrItem(p));
		gui.setItem(12, DexItem(p));
		gui.setItem(14, DefItem(p));
		gui.setItem(15, AgiItem(p));
		
		gui.setItem(27, backtomenuitem());
		
		gui.setItem(31, ResetStat(p));
		
			
		p.openInventory(gui);
	}
	public ItemStack backtomenuitem() {
		ItemStack item = new ItemStack(Material.ARROW, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c뒤로 가기"));
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	
	public ItemStack StrItem(Player p) {
		
		String str = Integer.toString(PlayerStatManager.getinstance(p).getStr());
		String str_2 = Integer.toString(PlayerStatManager.getinstance(p).getStr()+1);
		
		String per = stattopercentage(PlayerStatManager.getinstance(p).getStr());
		String per_2 = stattopercentage(PlayerStatManager.getinstance(p).getStr()+1);
		
		
		ItemStack item = new ItemStack(Material.IRON_SWORD, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c무기강화 Lv."+str));
		
		if(PlayerStatManager.getinstance(p).getStr()<100) {
			
			meta.setLore(Arrays.asList(
					"",
					"       §7현재 레벨             다음 레벨      ",
					"        §6§lLv."+str+"    §r§0>§8>§7>§f>     §6§lLv."+str_2+"      ",
					"         §d"+per+"%                "+per_2+"%      ",
					"",
					"§c무기강화§7는 기본공격과 특수공격의 공격력을 증가시킵니다",
					"",
					"§7- §3좌클릭으로 1씩 추가",
					"§7- §3우클릭으로 5씩 추가",
					"",
					"§7남은 스탯 : §a"+ PlayerStatManager.getinstance(p).getremainstat()));
		}
		else {
			
			meta.setLore(Arrays.asList(
					"",
					"        §7현재 레벨                다음 레벨      ",
					"        §6§lLv."+str+"    §r§0>§8>§7>§f>     §6§lLv."+str_2+"      ",
					"        §d"+per+"%                    "+per_2+"%      ",
					"",
					"§c무기강화§7는 기본공격과 특수공격의 공격력을 증가시킵니다",
					"",
					"§7- §3좌클릭으로 1씩 추가",
					"§7- §3우클릭으로 5씩 추가",
					"",
					"§7남은 스탯 : §a"+ PlayerStatManager.getinstance(p).getremainstat()));
			
		}
		

		
		
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack DexItem(Player p) {
		
		String str = Integer.toString(PlayerStatManager.getinstance(p).getDex());
		String str_2 = Integer.toString(PlayerStatManager.getinstance(p).getDex()+1);
		
		String per = stattopercentage(PlayerStatManager.getinstance(p).getDex());
		String per_2 = stattopercentage(PlayerStatManager.getinstance(p).getDex()+1);
		
		ItemStack item = new ItemStack(Material.ENDER_EYE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e감각강화 Lv."+str));

		if(PlayerStatManager.getinstance(p).getDex()<100) {
			
			meta.setLore(Arrays.asList(
					"",
					"       §7현재 레벨             다음 레벨      ",
					"        §6§lLv."+str+"    §r§0>§8>§7>§f>     §6§lLv."+str_2+"      ",
					"         §d"+per+"%                "+per_2+"%      ",
					"",
					"§e감각강화§7는 기본공격과 특수공격의 치명타 확률을 증가시킵니다",
					"",
					"§7- §3좌클릭으로 1씩 추가",
					"§7- §3우클릭으로 5씩 추가",
					"",
					"§7남은 스탯 : §a"+ PlayerStatManager.getinstance(p).getremainstat()));
		}
		else {
			
			meta.setLore(Arrays.asList(
					"",
					"        §7현재 레벨                다음 레벨      ",
					"        §6§lLv."+str+"    §r§0>§8>§7>§f>     §6§lLv."+str_2+"      ",
					"        §d"+per+"%                    "+per_2+"%      ",
					"",
					"§e감각강화§7는 기본공격과 특수공격의 치명타 확률을 증가시킵니다",
					"",
					"§7- §3좌클릭으로 1씩 추가",
					"§7- §3우클릭으로 5씩 추가",
					"",
					"§7남은 스탯 : §a"+ PlayerStatManager.getinstance(p).getremainstat()));
			
		}
		
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack DefItem(Player p) {
		
		String str = Integer.toString(PlayerStatManager.getinstance(p).getDef());
		String str_2 = Integer.toString(PlayerStatManager.getinstance(p).getDef()+1);
		
		String per = stattopercentage(PlayerStatManager.getinstance(p).getDef());
		String per_2 = stattopercentage(PlayerStatManager.getinstance(p).getDef()+1);
		
		ItemStack item = new ItemStack(Material.IRON_CHESTPLATE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&5외피강화 Lv."+str));
		
		if(PlayerStatManager.getinstance(p).getDef()<100) {
			
			meta.setLore(Arrays.asList(
					"",
					"       §7현재 레벨             다음 레벨      ",
					"        §6§lLv."+str+"    §r§0>§8>§7>§f>     §6§lLv."+str_2+"      ",
					"         §d"+per+"%                "+per_2+"%      ",
					"",
					"§5외피강화§7는 받는 피해량을 줄입니다",
					"",
					"§7- §3좌클릭으로 1씩 추가",
					"§7- §3우클릭으로 5씩 추가",
					"",
					"§7남은 스탯 : §a"+ PlayerStatManager.getinstance(p).getremainstat()));
		}
		else {
			
			meta.setLore(Arrays.asList(
					"",
					"        §7현재 레벨                다음 레벨      ",
					"        §6§lLv."+str+"    §r§0>§8>§7>§f>     §6§lLv."+str_2+"      ",
					"        §d"+per+"%                    "+per_2+"%      ",
					"",
					"§5외피강화§7는 받는 피해량을 줄입니다",
					"",
					"§7- §3좌클릭으로 1씩 추가",
					"§7- §3우클릭으로 5씩 추가",
					"",
					"§7남은 스탯 : §a"+ PlayerStatManager.getinstance(p).getremainstat()));
			
		}		

		
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack AgiItem(Player p) {
		
		String str = Integer.toString(PlayerStatManager.getinstance(p).getAgi());
		String str_2 = Integer.toString(PlayerStatManager.getinstance(p).getAgi()+1);
		
		String per = stattopercentage(PlayerStatManager.getinstance(p).getAgi());
		String per_2 = stattopercentage(PlayerStatManager.getinstance(p).getAgi()+1);
		
		ItemStack item = new ItemStack(Material.SPLASH_POTION, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b기동강화 Lv."+str));
		
		if(PlayerStatManager.getinstance(p).getAgi()<100) {
			
			meta.setLore(Arrays.asList(
					"",
					"       §7현재 레벨             다음 레벨      ",
					"        §6§lLv."+str+"    §r§0>§8>§7>§f>     §6§lLv."+str_2+"      ",
					"         §d"+per+"%                "+per_2+"%      ",
					"",
					"§b기동강화§7는 이동속도를 증가시킵니다",
					"",
					"§7- §3좌클릭으로 1씩 추가",
					"§7- §3우클릭으로 5씩 추가",
					"",
					"§7남은 스탯 : §a"+ PlayerStatManager.getinstance(p).getremainstat()));
		}
		else {
			
			meta.setLore(Arrays.asList(
					"",
					"        §7현재 레벨                다음 레벨      ",
					"        §6§lLv."+str+"    §r§0>§8>§7>§f>     §6§lLv."+str_2+"      ",
					"        §d"+per+"%                    "+per_2+"%      ",
					"",
					"§b기동강화§7는 이동속도를 증가시킵니다",
					"",
					"§7- §3좌클릭으로 1씩 추가",
					"§7- §3우클릭으로 5씩 추가",
					"",
					"§7남은 스탯 : §a"+ PlayerStatManager.getinstance(p).getremainstat()));
			
		}
		
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	
	public ItemStack ResetStat(Player p) {
		ItemStack item = new ItemStack(Material.NETHER_STAR, 1);
		ItemMeta meta= item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c스탯 초기화"));
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack ResetStatAreYouSure(Player p) {
		ItemStack item = new ItemStack(Material.NETHER_STAR, 1);
		ItemMeta meta= item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c정말로 초기화 하시겠습니까?"));
		item.setItemMeta(meta);
		
		return item;
	}
	
	
	public String stattopercentage(int stat) {
		
		double result = 0;
		
		if(stat == 0) return "0";
		else {
			
			for(int i=1; i<=stat; i++) {
				double multiply = 1;
				for(int j=1; j<=i; j++) {
					multiply *= 0.99;
				}
				result += multiply;
			}
			
			String format = String.format("%.1f", result);
			
			return format;
			
		}
		
	}
	
	
	
}
