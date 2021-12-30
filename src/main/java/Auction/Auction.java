package Auction;

import PlayerManager.PlayerFileManager;
import Shop.RightClickNPC;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Auction implements Listener {

    private final static ConcurrentHashMap<Player, Integer> ReadInteger = new ConcurrentHashMap<>();
    private final static Set<Player> ListenChat = new HashSet<>();
    private final static ConcurrentHashMap<Player, ItemStack> preRegisterItem = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<Player, Integer> preRegisterAltera = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<Player, Integer> preBuyItem = new ConcurrentHashMap<>();

    @EventHandler
    public void NPCRightClicked(RightClickNPC event) {
        EntityPlayer npc = event.getNPC();
        Player player = event.getPlayer();
        if(npc == null) return;
        if(npc.getScoreboardTags().contains("Auction")) {
            MarketOpen(player, 1);
        }
    }

    @EventHandler
    public void ChatListener(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String msg = event.getMessage();
        if(ListenChat.contains(player)) {
            try {
                int value = Integer.parseInt(msg);
                preRegisterAltera.put(player, value);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"),
                        () -> (new Window()).Register(player), 1);

            }
            catch(Exception e) {
                player.sendMessage("§c오류가 발생했습니다");
                e.printStackTrace();
                preRegisterItem.remove(player);
                preRegisterAltera.remove(player);
            }
            finally {
                ListenChat.remove(player);
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void UIClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        if(event.getView().getTitle().contains("MarketPlace")) {
            if(slot == 8) {
                preRegisterItem.remove(player);
                preRegisterAltera.remove(player);
                (new Window()).Register(player);
            }
            else if(slot == 17) {
                (new Window()).mySellList(player);
            }
            else if(slot % 9 <= 6 && slot <= 53) {
                ItemStack clickedItem = event.getCurrentItem();
                if(clickedItem != null) {
                    (new Window()).BuyItem(player, clickedItem);
                }
            }
            event.setCancelled(true);
        }
        else if(event.getView().getTitle().contains("등록할 아이템을 선택하세요")) {
            if(slot == 20) {
                MarketOpen(player, 1);
                preRegisterAltera.remove(player);
                preRegisterItem.remove(player);
            }
            else if(slot == 13) {
                ListenChat.add(player);
                player.closeInventory();
                player.sendMessage("§6책정 값을 입력해주세요. 현재 보유 알테라: "+ PlayerFileManager.getinstance().getGold(player));
            }
            else if(slot == 24) {
                if(preRegisterAltera.containsKey(player) && preRegisterItem.containsKey(player)) {
                    MarketRegister(player, preRegisterItem.get(player), preRegisterAltera.get(player), 1);
                    player.getInventory().removeItem(preRegisterItem.get(player));
                    MarketOpen(player, 1);
                    preRegisterItem.remove(player);
                    preRegisterAltera.remove(player);
                }
            }
            else if(slot >= 45) {
                ItemStack item = event.getCurrentItem();
                net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
                if(!nmsStack.hasTag()) {
                    player.sendMessage("§c마켓에 등록할 수 없는 아이템입니다");
                }
                else {
                    NBTTagCompound tag = nmsStack.getTag();
                    if(tag.getBoolean("교환")) {
                        preRegisterItem.put(player, item);
                        (new Window()).Register(player);
                    }
                    else {
                        player.sendMessage("§c마켓에 등록할 수 없는 아이템입니다");
                    }
                }
            }
            event.setCancelled(true);
        }
        else if(event.getView().getTitle().contains("판매 중인 아이템")) {
            event.setCancelled(true);
        }
        else if(event.getView().getTitle().contains("구매하시겠습니까?")) {
            if(slot == 24) {
                ItemStack item = event.getClickedInventory().getItem(22);
                MarketBuy(player, item);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void UIClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
    }

    @EventHandler
    public void UIClick2(InventoryDragEvent event) {
        if(event.getView().getTitle().contains("MarketPlace")) {
            event.setCancelled(true);
        }
        else if(event.getView().getTitle().contains("등록할 아이템을 선택하세요")) {
            event.setCancelled(true);
        }
        else if(event.getView().getTitle().contains("판매 중인 아이템")) {
            event.setCancelled(true);
        }
        else if(event.getView().getTitle().contains("구매하시겠습니까?")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void UIClick3(InventoryInteractEvent event) {
        if(event.getView().getTitle().contains("MarketPlace")) {
            event.setCancelled(true);
        }
        else if(event.getView().getTitle().contains("등록할 아이템을 선택하세요")) {
            event.setCancelled(true);
        }
        else if(event.getView().getTitle().contains("판매 중인 아이템")) {
            event.setCancelled(true);
        }
        else if(event.getView().getTitle().contains("구매하시겠습니까?")) {
            event.setCancelled(true);
        }
    }


    private void MarketOpen(Player player, int page) {

        Inventory inv = Bukkit.createInventory(null, 54, "MarketPlace [페이지 "+page+"]");

        inv.setItem(8, (new UI()).register(player));
        inv.setItem(17, (new UI()).SeeMySellList(player));

        File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "Market.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            }
            catch(Exception e) {

            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        int k = 1;
        while(config.contains("mainMarket."+k)) {
            k++;
        }


        List<Long> paths = new ArrayList<>();
        for(String i : config.getConfigurationSection("mainMarket").getKeys(false)) {
            paths.add(Long.parseLong(i));
        }

        Bukkit.broadcastMessage(paths.toString());

        long[] SortedPath = paths.stream().mapToLong(i->i).toArray();
        Long[] arr = Arrays.stream(SortedPath).boxed().toArray(Long[]::new);
        Arrays.sort(arr, Collections.reverseOrder());

        int j = (page - 1) * 42;
        for(int i=0; i<54; i++) {
            if(i%9 <= 6) {
                if(arr.length == j) break;
                String path = "mainMarket."+arr[j];
                if(!config.contains(path)) break;
                ItemStack itemStack = config.getItemStack(path+".item");
                int price = config.getInt(path+".altera");
                ItemMeta itemMeta = itemStack.getItemMeta();
                List<String> lore = itemMeta.getLore();
                lore.add(0, "");
                lore.add(0, "§5- §7가격: §f"+price);
                lore.add(0, "");
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);

                inv.setItem(i, itemStack);

                j++;
            }
        }

        player.openInventory(inv);
    }

    private synchronized void MarketRegister(Player player, ItemStack itemStack, int altera, int count) {

        String uuid = player.getUniqueId().toString();


        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbtTagCompound = nmsStack.getTag();
        String Iuuid = nbtTagCompound.getString("UUID");

        File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "Market.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            }
            catch(Exception e) {

            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        String pattern = "yyyyMMddHHmmssSSS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.KOREA);
        long date = Long.parseLong(simpleDateFormat.format(new Date()));

        String path = "mainMarket."+date;
        config.set(path+".item", itemStack);
        config.set(path+".count", count);
        config.set(path+".altera", altera);
        config.set(path+".uuid", Iuuid);
        config.set(path+".seller", player.getUniqueId().toString());
        config.set(path+".milisec", System.currentTimeMillis());


        config.set(path+".date", date);

        File UserFile = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
        FileConfiguration UserConfig = YamlConfiguration.loadConfiguration(UserFile);


        path = "mainMarket."+date;

        UserConfig.set(path+".item", itemStack);
        UserConfig.set(path+".count", count);
        UserConfig.set(path+".uuid", Iuuid);
        UserConfig.set(path+".altera", altera);
        UserConfig.set(path+".milisec", System.currentTimeMillis());
        UserConfig.set(path+".date", date);
        try {
            config.save(file);
            UserConfig.save(UserFile);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void MarketBuy(Player player, ItemStack itemStack) {

        File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "Market.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbtTagCompound = nmsStack.getTag();
        String Iuuid = nbtTagCompound.getString("UUID");



    }

    private void BuyItem(Player player, ItemStack itemStack) {

    }

    private class UI {

        ItemStack register(Player player) {

            String uuid = player.getUniqueId().toString();

            ItemStack itemstack = new ItemStack(Material.GREEN_CONCRETE, 1);
            ItemMeta itemMeta = itemstack.getItemMeta();
            itemMeta.setDisplayName("§a아이템 등록하기");
            itemMeta.setLore(Arrays.asList("§7아이템을 마켓에 등록합니다"));
            itemstack.setItemMeta(itemMeta);

            File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            return itemstack;
        }

        ItemStack SeeMySellList(Player player) {
            ItemStack itemStack = new ItemStack(Material.CHEST, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§b나의 아이템 판매 목록");
            itemMeta.setLore(Arrays.asList("§7현재 판매 중인 아이템 목록"));
            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

        ItemStack cancel() {
            ItemStack itemStack = new ItemStack(Material.RED_CONCRETE, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§c취소");
            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

        ItemStack acceptRegistering() {
            ItemStack itemStack = new ItemStack(Material.GREEN_CONCRETE, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§a등록하기");
            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

        ItemStack registerAltera(Player player) {
            ItemStack itemStack = new ItemStack(Material.DIAMOND_BLOCK, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§6클릭하여 아이템의 가격을 책정해주세요");
            itemMeta.setLore(preRegisterAltera.containsKey(player) ? Arrays.asList("§6등록된 가격 : "+preRegisterAltera.get(player))
            : Arrays.asList("§6등록된 가격 : 0"));
            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

        ItemStack EmptyItem() {
            ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§7인벤토리의 아이템을 클릭하여 아이템 등록");
            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

        ItemStack acceptBuying() {

            ItemStack itemStack = new ItemStack(Material.GREEN_CONCRETE, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§a구매하기");
            //itemMeta.setLore(Arrays.asList("§5판매 가격 - §d"+price));
            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

    }

    private class Window {

        void Register(Player player) {
            Inventory inv = Bukkit.createInventory(null, 45, "등록할 아이템을 선택하세요");

            UI ui = new UI();
            inv.setItem(20, ui.cancel());
            inv.setItem(24, ui.acceptRegistering());
            inv.setItem(13, ui.registerAltera(player));
            if(preRegisterItem.containsKey(player)) inv.setItem(22, preRegisterItem.get(player));
            else inv.setItem(22, ui.EmptyItem());

            player.openInventory(inv);

        }

        void mySellList(Player player) {
            Inventory inv = Bukkit.createInventory(null, 27, "판매 중인 아이템");

            String uuid = player.getUniqueId().toString();
            File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            List<Long> paths = new ArrayList<>();
            for(String i : config.getConfigurationSection("mainMarket").getKeys(false)) {
                paths.add(Long.parseLong(i));
            }

            try {
                for(int i=11; i<=15; i++) {
                    String path = "mainMarket."+paths.get(i-11);
                    if(config.contains(path)) {
                        ItemStack itemStack = config.getItemStack(path+".item");
                        if(itemStack == null) inv.setItem(i, new ItemStack(Material.AIR, 1));
                        else inv.setItem(i, itemStack);
                    }
                }
            }
            catch(Exception e) {

            }


            player.openInventory(inv);
        }

        void BuyItem(Player player, ItemStack itemStack) {
            Inventory inv = Bukkit.createInventory(null, 27, "구매하시겠습니까?");

            UI ui = new UI();

            inv.setItem(20, ui.cancel());
            inv.setItem(22, itemStack);
            inv.setItem(24, ui.acceptBuying());
        }
    }



}
