package PlayerChip;

import PlayerManager.PlayerFileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Classgui {
	
	private static Classgui Classgui;
	
	private Classgui() {
		
	}
	
	public static Classgui getinstance() {
		if(Classgui == null) Classgui = new Classgui();
		return Classgui;
	}
	
	public void ClassSelectGuiOpen(Player p) {
		
		Inventory gui = Bukkit.createInventory(null, 36, "클래스 선택");
		
		SettingSelectClassGui(gui, p);
		gui.setItem(27, backtomenuitem());
		
		p.openInventory(gui);
	}
	
	public void ClassAddGuiOpen(Player p) {
		
		Inventory gui = Bukkit.createInventory(null, 36, "클래스 추가");
		
		gui.setItem(11, AetherItem(p));
		gui.setItem(12, AcceleratorItem(p));
		gui.setItem(13, BlasterItem(p));
		gui.setItem(14, ByVItem(p));
		gui.setItem(15, PhloxItem(p));
		gui.setItem(20, KhaosItem(p));
		gui.setItem(27, backtomenuitem());
		
		
		p.openInventory(gui);
	}
	
	public void ClassDeleteAskAreYouSureGuiOpen(Player p, ItemStack item) {
		
		Inventory gui = Bukkit.createInventory(null,  36, "정말로 클래스를 삭제하시겠습니까?");
		
		gui.setItem(11, ClassDeleteYesItem(p));
		gui.setItem(13, item);
		gui.setItem(15, ClassDeleteNoItem(p));
		
		p.openInventory(gui);
		
		
	}
	public ItemStack backtomenuitem() {
		ItemStack item = new ItemStack(Material.ARROW, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c뒤로 가기"));
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack ClassDeleteYesItem(Player p) {
		
		ItemStack item = new ItemStack(Material.GREEN_WOOL, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§a네. 클래스를 삭제하겠습니다");
		
		item.setItemMeta(meta);
		return item;
		
	}
	public ItemStack ClassDeleteNoItem(Player p) {
		
		ItemStack item = new ItemStack(Material.RED_WOOL, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§c아니요. 삭제를 취소하겠습니다");
		
		item.setItemMeta(meta);
		return item;
		
	}
	
	
	public ItemStack ClassAddItem(Player p) {
		
		ItemStack item = new ItemStack(Material.GREEN_WOOL, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&o클래스 추가"));
		meta.setLore(Arrays.asList(
				"§7클릭하여 다른 클래스를 만들어보세요"));
		
		item.setItemMeta(meta);
		return item;
	}


	public ItemStack KhaosItem(Player p) {
		ItemStack item = new ItemStack(Material.NETHERITE_SWORD, 1);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName("§6§o§l클래스 : 카오스§l§o§6");
		itemMeta.setLore(Arrays.asList(
				"",
				"§b클릭하여 클래스 생성하기",
				"",
				"§7테스트 버전 클래스",
				"",
				""));
		item.setItemMeta(itemMeta);
		return item;
	}

	public ItemStack AetherItem(Player p) {
		
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6§o§l클래스 : 아이테르§l§o§6");
		meta.setLore(Arrays.asList(
				"",
				"§b클릭하여 클래스 생성하기",
				"",
				"§7밸런스형",
				"§e특수자원 : §6§c'충격량'",
				"§7전장의 전위를 책임지며 수호자 역할을 함",
				"",
				"§9RL : 충격량전환: 보호막",
				"§9RF : 충격량전환: 레이저",
				"§9RR : 보호막전환: 돌진",
				"§9FR : 무기 모드 변경",
				"§9FF : 충격량전환: 에너지",
				"",
				"§d패시브 : 충격량흡수",
				"",
				"§7데미지 : §a▒▒▒▒▒§7▒▒▒▒▒",
				"§7사거리 : §a▒▒▒▒§7▒▒▒▒▒▒",
				"§7유틸성 : §a▒▒▒▒▒▒▒§7▒▒▒",
				"§7생존력 : §a▒▒▒▒▒▒▒▒§7▒▒"));
		meta.setCustomModelData(1);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		
		item.setItemMeta(meta);
		return item;
	}
	
	public ItemStack AcceleratorItem(Player p) {
		
		ItemStack item = new ItemStack(Material.DIAMOND_HOE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6§o§l클래스 : 엑셀러레이터§l§o§6");
		meta.setCustomModelData(2);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.setLore(Arrays.asList(
				"",
				"§b클릭하여 클래스 생성하기",
				"",
				"§7극딜형",
				"§7빠른 속도로 전장을 누비며 딜러 역할을 함",
				"",
				"§9RL : 기동 타격",
				"§9RF : 플라즈마 고폭탄",
				"§9RR : 아드레날린",
				"§9FR : 난사",
				"§9FF : 입자 가속",
				"",
				"§d패시브 : 가속",
				"",
				"§7데미지 : §a▒▒▒▒▒▒▒§7▒▒▒",
				"§7사거리 : §a▒▒▒▒▒▒§7▒▒▒▒",
				"§7유틸성 : §a▒▒▒§7▒▒▒▒▒▒▒",
				"§7생존력 : §a▒▒▒▒§7▒▒▒▒▒▒"));
		
		
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack BlasterItem(Player p) {
		
		ItemStack item = new ItemStack(Material.DIAMOND_SHOVEL, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6§o§l클래스 : 블래스터§l§o§6");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.setLore(Arrays.asList(
				"",
				"§b클릭하여 클래스 생성하기",
				"",
				"§7평타지속형",
				"§§7전장의 중심에서 지속적인 딜링을 할 수 있음",
				"",
				"§9RL : 모드전환: 레일건",
				"§9RF : 모드전환: 기관총",
				"§9RR : 모드전환: 유탄발사기",
				"§9FR : 에너지 전환",
				"§9FF : 자기장",
				"",
				"§d패시브 : 보호막 재생",
				"",
				"§7데미지 : §a▒▒▒▒▒▒▒§7▒▒▒",
				"§7사거리 : §a▒▒▒▒▒▒▒§7▒▒▒",
				"§7유틸성 : §a▒▒▒▒§7▒▒▒▒▒▒",
				"§7생존력 : §a▒▒▒▒▒§7▒▒▒▒▒"));
		
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack ByVItem(Player p) {
		
		ItemStack item = new ItemStack(Material.DIAMOND_AXE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6§o§l클래스 : 바이V§l§o§6");
		meta.setCustomModelData(1);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.setLore(Arrays.asList(
				"",
				"§b클릭하여 클래스 생성하기",
				"",
				"§7근접 누킹형",
				"§§e특수자원 : §6§c'정수'",
				"§§7다양한 군중제어기를 활용하여 근접한 상대를 학살함",
				"",
				"§9RL : 회복",
				"§9RF : 아광속 펀치",
				"§9RR : 내려찍기",
				"§9FR : 사슬 발사",
				"§9FF : 충격파",
				"",
				"§d패시브 : 정수 수집",
				"",
				"§7데미지 : §a▒▒▒▒▒▒▒▒§7▒▒",
				"§7사거리 : §a▒▒§7▒▒▒▒▒▒▒▒",
				"§7유틸성 : §a▒▒▒▒▒▒§7▒▒▒▒",
				"§7생존력 : §a▒▒▒▒▒§7▒▒▒▒▒"));
		
		
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack PhloxItem(Player p) {
		
		ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6§o§l클래스 : 플록스§l§o§6");
		meta.setCustomModelData(1);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.setLore(Arrays.asList(
				"",
				"§b클릭하여 클래스 생성하기",
				"",
				"§7유틸 및 섭딜형",
				"§§e특수자원 : §6§c'나노로봇'",
				"§§7전장의 후위에서 팀원들을 지원하거나 먼거리에서도 높은 딜량을 보여줌",
				"",
				"§9RL : 정밀치료",
				"§9RF : 섬멸개시",
				"§9RR : 긴급탈출",
				"§9FR : 방해장 가동",
				"§9FF : 로봇급조",
				"",
				"§d패시브 : 나노로봇 재생",
				"",
				"§7데미지 : §a▒▒▒▒▒▒§7▒▒▒▒",
				"§7사거리 : §a▒▒▒▒▒▒▒▒▒§7▒",
				"§7유틸성 : §a▒▒▒▒▒▒▒§7▒▒▒",
				"§7생존력 : §a▒▒▒▒§7▒▒▒▒▒▒"));
		
		
		item.setItemMeta(meta);
		return item;
	}
	
	private void SettingSelectClassGui(Inventory gui, final Player p) {
		
		int GUIlocation = 11;
		
		if(PlayerFileManager.getinstance().getClasses(p) == null) { // 등록된 클래스가 없으면
			
			gui.setItem(GUIlocation, ClassAddItem(p));
			return;
		}
		
		
		for(String Class : PlayerFileManager.getinstance().getClasses(p)) {
			
			if(GUIlocation == 16) GUIlocation = 20;
			if(GUIlocation == 25) GUIlocation = 29;
			
			if(Class.split("/")[0].equals("아이테르")) gui.setItem(GUIlocation, AetherSelectItem(p, Class));
			else if(Class.split("/")[0].equals("엑셀러레이터")) gui.setItem(GUIlocation, AcceleratorSelectItem(p, Class));
			else if(Class.split("/")[0].equals("바이V")) gui.setItem(GUIlocation, ByVSelectItem(p, Class));
			else if(Class.split("/")[0].equals("블래스터")) gui.setItem(GUIlocation, BlasterSelectItem(p, Class));
			else if(Class.split("/")[0].equals("플록스")) gui.setItem(GUIlocation, PhloxSelectItem(p, Class));
			else if(Class.split("/")[0].equals("카오스")) gui.setItem(GUIlocation, KhaosSelectItem(p, Class));
			
			GUIlocation ++;
		}	
		
		if(GUIlocation == 16) GUIlocation = 20;
		if(GUIlocation == 25) GUIlocation = 29;
		
		if(GUIlocation <=33) gui.setItem(GUIlocation, ClassAddItem(p));
		
		
	}

	private ItemStack KhaosSelectItem(Player p, String classname) {
		int lvl = PlayerFileManager.getinstance().getClassLevel(p, classname);

		ItemStack item = new ItemStack(Material.NETHERITE_SWORD, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6§o§l클래스 : 카오스§l§o§6");
		meta.setLore(Arrays.asList(
				"",
				"§o§bLevel : "+lvl,
				"",
				"",
				"§7좌클릭 : 클래스 선택",
				"§7쉬프트 + 좌클릭 : 클래스 삭제"));


		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack AetherSelectItem(Player p, String classname) {
		
		int lvl = PlayerFileManager.getinstance().getClassLevel(p, classname);
		
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6§o§l클래스 : 아이테르§l§o§6");
		meta.setLore(Arrays.asList(
				"",
				"§o§bLevel : "+lvl,
				"",
				"§9RL : 충격량전환: 보호막 "+(lvl >= 1 ? "§7UNLOCK" : "§cLOCKED"),
				"§9RF : 충격량전환: 레이저 "+(lvl >= 5 ? "§7UNLOCK" : "§cLOCKED"),
				"§9RR : 보호막전환: 돌진 "+(lvl >= 10 ? "§7UNLOCK" : "§cLOCKED"),
				"§9FR : 무기 모드 변경 "+(lvl >= 15 ? "§7UNLOCK" : "§cLOCKED"),
				"§9FF : 충격량전환: 에너지 "+(lvl >= 20 ? "§7UNLOCK" : "§cLOCKED"),
				"",
				"§7좌클릭 : 클래스 선택",
				"§7쉬프트 + 좌클릭 : 클래스 삭제"));
		meta.setCustomModelData(1);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack AcceleratorSelectItem(Player p, String classname) {
		
		int lvl = PlayerFileManager.getinstance().getClassLevel(p, classname);
		
		ItemStack item = new ItemStack(Material.DIAMOND_HOE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6§o§l클래스 : 엑셀러레이터§l§o§6");
		meta.setCustomModelData(2);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.setLore(Arrays.asList(
				"",
				"§o§bLevel : "+lvl,
				"",
				"§9RL : 기동 타격 "+(lvl >= 1 ? "§7UNLOCK" : "§cLOCKED"),
				"§9RF : 플라즈마 고폭탄 "+(lvl >= 5 ? "§7UNLOCK" : "§cLOCKED"),
				"§9RR : 아드레날린 "+(lvl >= 10 ? "§7UNLOCK" : "§cLOCKED"),
				"§9FR : 난사 "+(lvl >= 15 ? "§7UNLOCK" : "§cLOCKED"),
				"§9FF : 입자 가속 "+(lvl >= 20 ? "§7UNLOCK" : "§cLOCKED"),
				"",
				"§7좌클릭 : 클래스 선택",
				"§7쉬프트 + 좌클릭 : 클래스 삭제"));
		
		
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack BlasterSelectItem(Player p, String classname) {
		
		int lvl = PlayerFileManager.getinstance().getClassLevel(p, classname);
		
		ItemStack item = new ItemStack(Material.DIAMOND_SHOVEL, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6§o§l클래스 : 블래스터§l§o§6");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.setLore(Arrays.asList(
				"",
				"§o§bLevel : "+lvl,
				"",
				"§9RL : 모드전환: 레일건 "+(lvl >= 1 ? "§7UNLOCK" : "§cLOCKED"),
				"§9RF : 모드전환: 기관총 "+(lvl >= 5 ? "§7UNLOCK" : "§cLOCKED"),
				"§9RR : 모드전환: 유탄발사기 "+(lvl >= 10 ? "§7UNLOCK" : "§cLOCKED"),
				"§9FR : 에너지 전환 "+(lvl >= 15 ? "§7UNLOCK" : "§cLOCKED"),
				"§9FF : 자기장 "+(lvl >= 20 ? "§7UNLOCK" : "§cLOCKED"),
				"",
				"§7좌클릭 : 클래스 선택",
				"§7쉬프트 + 좌클릭 : 클래스 삭제"));
		
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack ByVSelectItem(Player p, String classname) {
		
		int lvl = PlayerFileManager.getinstance().getClassLevel(p, classname);
		
		ItemStack item = new ItemStack(Material.DIAMOND_AXE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setCustomModelData(1);
		meta.setDisplayName("§6§o§l클래스 : 바이V§l§o§6");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.setLore(Arrays.asList(
				"",
				"§o§bLevel : "+lvl,
				"",
				"§9RL : 회복 "+(lvl >= 1 ? "§7UNLOCK" : "§cLOCKED"),
				"§9RF : 아광속 펀치 "+(lvl >= 5 ? "§7UNLOCK" : "§cLOCKED"),
				"§9RR : 내려찍기 "+(lvl >= 10 ? "§7UNLOCK" : "§cLOCKED"),
				"§9FR : 사슬 발사 "+(lvl >= 15 ? "§7UNLOCK" : "§cLOCKED"),
				"§9FF : 충격파 "+(lvl >= 20 ? "§7UNLOCK" : "§cLOCKED"),
				"",
				"§7좌클릭 : 클래스 선택",
				"§7쉬프트 + 좌클릭 : 클래스 삭제"));
		
		
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack PhloxSelectItem(Player p, String classname) {
		
		int lvl = PlayerFileManager.getinstance().getClassLevel(p, classname);
		
		ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6§o§l클래스 : 플록스§l§o§6");
		meta.setCustomModelData(1);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.setLore(Arrays.asList(
				"",
				"§o§bLevel : "+lvl,
				"",
				"§9RL : 정밀치료 "+(lvl >= 1 ? "§7UNLOCK" : "§cLOCKED"),
				"§9RF : 섬멸개시 "+(lvl >= 5 ? "§7UNLOCK" : "§cLOCKED"),
				"§9RR : 긴급탈출 "+(lvl >= 10 ? "§7UNLOCK" : "§cLOCKED"),
				"§9FR : 방해장 가동 "+(lvl >= 15 ? "§7UNLOCK" : "§cLOCKED"),
				"§9FF : 로봇급조 "+(lvl >= 20 ? "§7UNLOCK" : "§cLOCKED"),
				"",
				"§7좌클릭 : 클래스 선택",
				"§7쉬프트 + 좌클릭 : 클래스 삭제"));
		
		
		item.setItemMeta(meta);
		return item;
	}
	
	
	
}
