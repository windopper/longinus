package Auction;

import PlayerManager.PlayerFileManager;
import Shop.RightClickNPC;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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

    //private final static ConcurrentHashMap<Player, Integer> ReadInteger = new ConcurrentHashMap<>();
    private final static Set<Player> ListenChat = new HashSet<>();
    private final static ConcurrentHashMap<Player, ItemStack> preRegisterItem = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<Player, Long> preRegisterAltera = new ConcurrentHashMap<>();

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
                long value = Long.parseLong(msg);
                preRegisterAltera.put(player, value);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"),
                        () -> (new Window()).Register(player), 1);

                if(value <= 0) {
                    throw new Exception();
                }

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
                Window.mySellList(player);
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
            if(slot == 24) {
                MarketOpen(player, 1);
                preRegisterAltera.remove(player);
                preRegisterItem.remove(player);
            }
            else if(slot == 13) {
                ListenChat.add(player);
                player.closeInventory();
                player.sendMessage("§6책정 값을 입력해주세요. 현재 보유 알테라: "+ PlayerFileManager.getinstance().getGold(player));
            }
            else if(slot == 20) {
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
                    } /* -------------------------------------------- */
                }
            }
            event.setCancelled(true);
        }
        else if(event.getView().getTitle().contains("판매 중인 아이템")) {
            event.setCancelled(true);
            ItemStack itemStack = event.getCurrentItem();
            Inventory inv = event.getClickedInventory();
            if(itemStack == null) return;
            if(itemStack.getItemMeta() == null) return;
            if(itemStack.getType() != Material.AIR && !itemStack.getItemMeta().getDisplayName().equals("§c다시 한번 클릭하여 판매 중단")) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName("§c다시 한번 클릭하여 판매 중단");
                itemStack.setItemMeta(itemMeta);
                inv.setItem(slot, itemStack);
                return;
            }

            try {
                if(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).contains("판매 완료!")) {
                    getAlteraFromMySalesStand(player, itemStack);
                }
                else if(itemStack.getItemMeta().getDisplayName().equals("§c다시 한번 클릭하여 판매 중단")) {
                    getItemFromMySalesStand(player, itemStack);
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        else if(event.getView().getTitle().contains("구매하시겠습니까?")) {
            if(slot == 20) {
                ItemStack item = event.getClickedInventory().getItem(22);
                MarketBuy(player, item);
            }
            else if(slot == 24) {
                MarketOpen(player, 1);
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

    public void MarketUpdate() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getOpenInventory().getTitle().contains("MarketPlace")) {

                int page = Integer.parseInt(player.getOpenInventory().getTitle().replaceAll("[^0-9]",""));

                Inventory inv = player.getOpenInventory().getTopInventory();

                inv.setItem(8, (new UI()).register(player));
                inv.setItem(17, (new UI()).SeeMySellList(player));

                File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "Market.yml");
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                if(!config.contains("mainMarket")) {
                    player.openInventory(inv);
                    return;
                }
                List<Long> paths = new ArrayList<>();
                for(String i : config.getConfigurationSection("mainMarket").getKeys(false)) {
                    paths.add(Long.parseLong(i));
                }

                long[] SortedPath = paths.stream().mapToLong(i->i).toArray();
                Long[] arr = Arrays.stream(SortedPath).boxed().toArray(Long[]::new);
                Arrays.sort(arr, Collections.reverseOrder());

                int j = (page - 1) * 42;
                for(int i=0; i<54; i++) {
                    if(i%9 <= 6) {
                        if(arr.length <= j) {
                            inv.setItem(i, new ItemStack(Material.AIR, 1));
                            continue;
                        }
                        String path = "mainMarket."+arr[j];
                        if(!config.contains(path)) continue;
                        ItemStack itemStack = config.getItemStack(path+".item");
                        long price = config.getLong(path+".altera");
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
            }
            else {
            }
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

        if(!config.contains("mainMarket")) {
            player.openInventory(inv);
            return;
        }
        List<Long> paths = new ArrayList<>();
        for(String i : config.getConfigurationSection("mainMarket").getKeys(false)) {
            paths.add(Long.parseLong(i));
        }

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
                long price = config.getLong(path+".altera");
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

    private static synchronized void MarketRegister(Player player, ItemStack itemStack, long altera, int count) {

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

        File UserFile = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
        FileConfiguration UserConfig = YamlConfiguration.loadConfiguration(UserFile);

        String pattern = "yyyyMMddHHmmssSSS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.KOREA);
        long date = Long.parseLong(simpleDateFormat.format(new Date()));

        String path = "mainMarket."+date;
        config.set(path+".item", itemStack);
        config.set(path+".count", count);
        config.set(path+".altera", altera);
        config.set(path+".uuid", Iuuid);
        config.set(path+".seller", player.getUniqueId().toString());


        config.set(path+".date", date);

        path = "mainMarket."+date;
        UserConfig.set(path+".item", itemStack);
        UserConfig.set(path+".count", count);
        UserConfig.set(path+".uuid", Iuuid);
        UserConfig.set(path+".altera", altera);
        UserConfig.set(path+".date", date);
        try {
            config.save(file);
            UserConfig.save(UserFile);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        (new Auction()).MarketUpdate();
    }

    private static synchronized void MarketBuy(Player player, ItemStack itemStack) {

        if(player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§c인벤토리에 공간이 없습니다! 인벤토리를 먼저 비워주세요");
            return;
        }

        File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "Market.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbtTagCompound = nmsStack.getTag();
        String Iuuid = nbtTagCompound.getString("UUID");

        boolean isExist = false;
        for(String s : config.getConfigurationSection("mainMarket").getKeys(false)) {
            if(config.getString("mainMarket."+s+".uuid").equals(Iuuid)) {
                isExist = true;
                long altera = config.getLong("mainMarket."+s+".altera");
                if(altera <= PlayerFileManager.getinstance().getGold(player)) {

                    /* -------------------------------------------- */
                    File file_ = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(),
                            config.getString("mainMarket."+s+".seller")+".yml");
                    FileConfiguration config_ = YamlConfiguration.loadConfiguration(file_);

                    // 판매자의 파일에 접근
                    for(String s_ : config_.getConfigurationSection("mainMarket").getKeys(false)) {
                        if(config_.getString("mainMarket."+s_+".uuid").equals(Iuuid)) {
                            config_.set("mainMarket."+s_+".item", UI.itemSold(itemStack.getItemMeta().getDisplayName()));
                            break;
                        }
                    }

                    UUID selleruuid = UUID.fromString(config.getString("mainMarket."+s+".seller"));
                    for(Player p_ : Bukkit.getOnlinePlayers()) {
                        if(p_.getUniqueId().equals(selleruuid)) {
                            p_.sendMessage("§5>> "+itemStack.getItemMeta().getDisplayName()+" §d마켓에서 판매됨. 마켓플레이스에서 알테라를 수령해주세요");
                            break;
                        }
                    }
                    /* -------------------------------------------- */

                    PlayerFileManager.getinstance().setGold(player, PlayerFileManager.getinstance().getGold(player) - altera);
                    ItemStack item = config.getItemStack("mainMarket."+s+".item");
                    player.getInventory().addItem(item);
                    player.sendMessage("§a구매 완료!");
                    player.closeInventory();

                    registerItemAverage(item, altera);
                    config.set("mainMarket."+s, null);

                    try {
                        config_.save(file_);
                    }
                    catch(Exception e) {
                        player.sendMessage("§c예기치 않은 오류가 발생했습니다");
                    }
                }
                else {
                    player.closeInventory();
                    player.sendMessage("§c알테라가 부족합니다!");
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 2);
                }
            }
        }
        if(!isExist) {
            player.sendMessage("§c마켓에 등록되지 않은 아이템입니다");
        }

        try {
            config.save(file);
        }
        catch(Exception e) {

        }

        (new Auction()).MarketUpdate();
    }

    private void getAlteraFromMySalesStand(Player player, ItemStack itemStack) {

        File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), player.getUniqueId().toString()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        String UUID = nmsStack.getTag().getString("UUID");

        try {
            if(config.contains("mainMarket")) {
                for(String s : config.getConfigurationSection("mainMarket").getKeys(false)) {
                    ItemStack itemStack_ = config.getItemStack("mainMarket."+s+".item");
                    net.minecraft.world.item.ItemStack nmsStack_ = CraftItemStack.asNMSCopy(itemStack_);
                    if(nmsStack_.getTag().getString("UUID").equals(UUID)) {

                        long altera = config.getLong("mainMarket."+s+".altera");
                        long sumAltera = 0;

                        try {
                            sumAltera = Math.addExact(config.getLong("Gold"), altera);
                        }
                        catch(ArithmeticException e) {
                            sumAltera = Long.MAX_VALUE;
                        }

                        config.set("mainMarket."+s, null);
                        config.set("Gold", config.getLong("Gold")+altera);
                        config.save(file);
                        player.sendMessage("§a알테라 수령 완료! +"+altera+" §7보유 알테라: "
                                +config.getLong("Gold"));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);

                        break;
                    }
                }
            }
        }
        catch(Exception e) {

        }

//        try {
//            config.save(file);
//        }
//        catch(Exception e) {
//
//        }

        Window.mySellList(player);
    }

    private static synchronized void getItemFromMySalesStand(Player player, ItemStack itemStack) {

        File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), player.getUniqueId().toString()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        File file_ = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "Market.yml");
        FileConfiguration config_ = YamlConfiguration.loadConfiguration(file_);

        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        String UUID = nmsStack.getTag().getString("UUID");

        for(String s : config.getConfigurationSection("mainMarket").getKeys(false)) {
            if(config.getString("mainMarket."+s+".uuid").equals(UUID)) {
                config.set("mainMarket."+s, null);
                break;
            }
        }
        for(String s_ : config_.getConfigurationSection("mainMarket").getKeys(false)) {
            if(config_.getString("mainMarket." + s_ + ".uuid").equals(UUID)) {
                config_.set("mainMarket."+s_, null);
                break;
            }
        }

        try {
            config.save(file);
            config_.save(file_);
        }
        catch(Exception e) {

        }

        Window.mySellList(player);

    }

    private static synchronized void registerItemAverage(ItemStack itemStack, long altera) {

        String itemName = itemStack.getItemMeta().getDisplayName();

        File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "MarketPriceAverage.yml");
        if(!file.exists()) {
            try{
                file.createNewFile();
            }
            catch(Exception e) {

            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if(config.contains("itemAverage."+itemName)) {
            List<Long> list = config.getLongList("itemAverage."+itemName);
            list.add(0, altera);
            while(list.size() > 10) {
                list.remove(10);
            }
            config.set("itemAverage."+itemName, list);
        }
        else {
            List<Long> list = new ArrayList<>();
            list.add(altera);
            config.set("itemAverage."+itemName, list);
        }

        try {
            config.save(file);
        }
        catch(Exception e) {

        }
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

            long average = 0;
            long min = 0;
            long max = 0;

            if(preRegisterItem.containsKey(player)) {
                ItemStack itemStack = preRegisterItem.get(player);
                String itemName = itemStack.getItemMeta().getDisplayName();
                File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "MarketPriceAverage.yml");
                if(!file.exists()) {
                    try{
                        file.createNewFile();
                    }
                    catch(Exception e) {

                    }
                }
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                if(config.contains("itemAverage." + itemName)) {
                    long sum = 0;
                    for(long i : config.getLongList("itemAverage." + itemName)) {
                        sum += i;
                    }
                    average = sum / config.getLongList("itemAverage." + itemName).size();


                    min = Collections.min(config.getLongList("itemAverage." + itemName));
                    max = Collections.max(config.getLongList("itemAverage." + itemName));
                }
            }

            ItemStack itemStack = new ItemStack(Material.DIAMOND_BLOCK, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§6클릭하여 아이템의 가격을 책정해주세요");

            List<String> list = new ArrayList<>();

            String s = average <= 0 ? "§e최근 아이템 거래가 없습니다" : "§e최근 아이템 평균거래가: "+average;
            String s_ = preRegisterAltera.containsKey(player) ? "§6등록된 가격 : "+preRegisterAltera.get(player) : "§6등록된 가격 : 0";
            String s__ = (max > 0 || min > 0) ? "§e최소거래가: "+min+"  ||  최대거래가: "+max : null;
            list.add(s_);
            list.add(s__);
            list.add(s);


            itemMeta.setLore(list);
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

        static ItemStack itemSold(String itemName) {
            ItemStack itemStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);

            net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
            NBTTagCompound tag = nmsStack.getOrCreateTag();
            tag.setString("UUID", UUID.randomUUID().toString());
            nmsStack.setTag(tag);
            itemStack = CraftItemStack.asBukkitCopy(nmsStack);

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(itemName+" §a판매 완료! §l✔");
            itemMeta.setLore(Arrays.asList("§7클릭하여 알테라 획득"));
            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

    }

    private class Window {

        void Register(Player player) {

            File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), player.getUniqueId().toString()+".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            if(config.contains("mainMarket")) {
                int j=0;
                for(String s : config.getConfigurationSection("mainMarket").getKeys(false)) {
                    j++;
                }
                if(j>=5) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
                    player.sendMessage("§c아이템 판매는 최대 5개까지 가능합니다");
                    return;
                }
            }

            Inventory inv = Bukkit.createInventory(null, 45, "등록할 아이템을 선택하세요");

            UI ui = new UI();
            inv.setItem(24, ui.cancel());
            inv.setItem(20, ui.acceptRegistering());
            inv.setItem(13, ui.registerAltera(player));
            if(preRegisterItem.containsKey(player)) inv.setItem(22, preRegisterItem.get(player));
            else inv.setItem(22, ui.EmptyItem());

            player.openInventory(inv);

        }

        static void mySellList(Player player) {
            Inventory inv = Bukkit.createInventory(null, 27, "판매 중인 아이템");

            String uuid = player.getUniqueId().toString();
            File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            List<Long> paths = new ArrayList<>();

            if(!config.contains("mainMarket")) {
                player.openInventory(inv);
                return;
            }
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
            Inventory inv = Bukkit.createInventory(null, 45, "구매하시겠습니까?");

            UI ui = new UI();

            inv.setItem(24, ui.cancel());
            inv.setItem(22, itemStack);
            inv.setItem(20, ui.acceptBuying());

            player.openInventory(inv);
        }
    }



}
