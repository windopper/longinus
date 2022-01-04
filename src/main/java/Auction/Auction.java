package Auction;

import SQL.MainMarket;
import SQL.PlayerAltera;
import SQL.PlayerMarket;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Auction implements Listener {

    //private final static ConcurrentHashMap<Player, Integer> ReadInteger = new ConcurrentHashMap<>();
    private final static Set<Player> ListenChat = new HashSet<>();
    private final static Set<Player> OnSearching = new HashSet<>();
    private final static ConcurrentHashMap<Player, ItemStack> preRegisterItem = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<Player, Long> preRegisterAltera = new ConcurrentHashMap<>();

    private final static long saleLimit = 1209600000;

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
        else if(OnSearching.contains(player)) {
            if(msg.equals("취소")) {
                OnSearching.remove(player);
            }
            else {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
                    MarketOpen(player, 1, msg);
                }, 0);

                OnSearching.remove(player);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void UIClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        if(event.getView().getTitle().contains("MarketPlace")) {
            int page = Integer.parseInt(player.getOpenInventory().getTitle().replaceAll("[^0-9]",""));
            if(slot == 8) {
                preRegisterItem.remove(player);
                preRegisterAltera.remove(player);
                (new Window()).Register(player);
            }
            else if(slot == 17) {
                Window.mySellList(player);
            }
            else if(slot == 26) {
                MarketOpen(player, page+1);
            }
            else if(slot == 35) {
                MarketOpen(player, page == 1 ? 1 : page - 1);
            }
            else if(slot == 53) {
                OnSearching.add(player);
                player.closeInventory();
                player.sendMessage("§6아이템을 검색해주세요. '취소'라고 입력하면 검색이 취소됩니다.");
            }
            else if(slot % 9 <= 6 && slot <= 53) {
                ItemStack clickedItem = event.getCurrentItem();
                if(clickedItem != null) {
                    (new Window()).BuyItem(player, clickedItem);
                }
            }
            event.setCancelled(true);
        }
        else if(event.getView().getTitle().contains("검색결과")) {
            String search = player.getOpenInventory().getTitle().split("\"")[1];
            int page = Integer.parseInt(player.getOpenInventory().getTitle().replaceAll("[^0-9]",""));
            if(slot == 8) {
                preRegisterItem.remove(player);
                preRegisterAltera.remove(player);
                (new Window()).Register(player);
            }
            else if(slot == 17) {
                Window.mySellList(player);
            }
            else if(slot == 26) {
                MarketOpen(player, page+1, search);
            }
            else if(slot == 35) {
                MarketOpen(player, page == 1 ? 1 : page - 1, search);
            }
            else if(slot == 53) {
                OnSearching.add(player);
                player.closeInventory();
                player.sendMessage("§6아이템을 검색해주세요. '취소'라고 입력하면 검색이 취소됩니다.");
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
                player.sendMessage("§6책정 값을 입력해주세요. 현재 보유 알테라: "+(new SQL.PlayerAltera(player)).getAltera());
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
            if(event.getRawSlot() > 15) return;
            ItemStack itemStack = event.getCurrentItem();
            Inventory inv = event.getClickedInventory();
            if(itemStack == null) return;
            if(itemStack.getItemMeta() == null) return;

            try {

                if(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).contains("판매 완료!")) {
                    getAlteraFromMySalesStand(player, itemStack);
                }
                else if(itemStack.getItemMeta().getDisplayName().equals("§c다시 클릭하여 아이템 회수")) {
                    getItemFromMySalesStand(player, itemStack);
                }
                else if(itemStack.getType() != Material.AIR && !itemStack.getItemMeta().getDisplayName().equals("§c다시 클릭하여 아이템 회수")) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName("§c다시 클릭하여 아이템 회수");
                    itemStack.setItemMeta(itemMeta);
                    inv.setItem(slot, itemStack);
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

    public static void MarketSalesTimeWatcher() {

        try {
            Connection con = SQL.sqlData.getConnection();
            Statement statement = con.createStatement();
            ResultSet set = statement.executeQuery("select * from longinus.mainmarket");

            while (set.next()) {
                long date = set.getLong("milli");
                long remain = date + saleLimit - System.currentTimeMillis();
                if(remain<0) {
                    //String selleruuid = set.getString("seller");
                    (new MainMarket(set.getString("uuid"))).deleteItem();
                    //statement.executeUpdate("delete from longinus.mainmarket where uuid = '"+set.getString("uuid")+"'");
                }
            }

            statement.close();
            set.close();
            //con.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void MarketUpdate() {

        Auction.MarketSalesTimeWatcher();

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getOpenInventory().getTitle().contains("MarketPlace")) {

                int page = Integer.parseInt(player.getOpenInventory().getTitle().replaceAll("[^0-9]",""));

                Inventory inv = player.getOpenInventory().getTopInventory();

                inv.setItem(8, UI.register(player));
                inv.setItem(17, UI.SeeMySellList(player));

                try {

                    int j = (page - 1) * 42;

                    Connection conn = SQL.sqlData.getConnection();
                    Statement statement = conn.createStatement();
                    ResultSet set = statement.executeQuery("Select * from longinus.mainmarket order by milli desc limit "+j+", "+42);

                    if(!set.next()) {
                        player.openInventory(inv);
                        return;
                    }

                    for(int i=0; i<54; i++) {
                        if(i%9 <= 6) {
                            if(!set.next()) {
                                inv.setItem(i, new ItemStack(Material.AIR, 1));
                                continue;
                            }
                            else {
                                ItemStack itemStack = (new SQL.Converter()).decodeItem(set.getString("item"));
                                long price = set.getLong("altera");

                                ItemMeta itemMeta = itemStack.getItemMeta();
                                List<String> lore = itemMeta.getLore();
                                lore.add(0, "");
                                lore.add(0, "§5- §7가격: §f"+price);
                                lore.add(0, "");
                                itemMeta.setLore(lore);
                                itemStack.setItemMeta(itemMeta);

                                inv.setItem(i, itemStack);
                            }
                        }
                    }

                    set.close();
                    statement.close();
                    //conn.close();

                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
            else {
            }
        }
    }

    private void MarketOpen(Player player, int page) {
        MarketOpen(player, page, "");
    }

    private void MarketOpen(Player player, int page, String searchName) {

        Auction.MarketSalesTimeWatcher();

        Inventory inv = Bukkit.createInventory(null, 54, "MarketPlace [페이지 "+page+"]");
        if(searchName.equals("")) {

        }
        else {
            inv = Bukkit.createInventory(null, 54, "\""+searchName+"\" 검색결과 [페이지 "+page+"]");
            searchName = "where itemname like '%"+searchName+"%'";
        }

        inv.setItem(8, UI.register(player));
        inv.setItem(17, UI.SeeMySellList(player));
        inv.setItem(26, (new UI()).nextPage());
        inv.setItem(35, (new UI()).prePage());
        inv.setItem(53, (new UI()).Search());

        try {
            int j = (page - 1) * 42;

            Connection connection = SQL.sqlData.getConnection();
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery("select * from longinus.mainmarket "+searchName+" order by milli desc limit "+j+", "+42);


            for(int i=0; i<54; i++) {
                if(i%9 <= 6) {
                    if(!set.next()) {
                        inv.setItem(i, new ItemStack(Material.AIR, 1));
                        continue;
                    }
                    else {
                        ItemStack itemStack = (new SQL.Converter()).decodeItem(set.getString("item"));
                        long price = set.getLong("altera");

                        ItemMeta itemMeta = itemStack.getItemMeta();
                        List<String> lore = itemMeta.getLore();
                        lore.add(0, "");
                        lore.add(0, "§5- §7가격: §f"+price);
                        lore.add(0, "");
                        itemMeta.setLore(lore);
                        itemStack.setItemMeta(itemMeta);

                        inv.setItem(i, itemStack);
                    }
                }
            }
            player.openInventory(inv);
            set.close();
            statement.close();
            //connection.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }


    }

    public void MarketRegister(Player player, ItemStack itemStack, long altera, int count) {

        String uuid = player.getUniqueId().toString();
        String itemName = itemStack.getItemMeta().getDisplayName();

        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbtTagCompound = nmsStack.getTag();
        String Iuuid = nbtTagCompound.getString("UUID");


        String pattern = "yyyyMMddHHmmssSSS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.KOREA);
        long date = Long.parseLong(simpleDateFormat.format(new Date()));

        String path = "mainMarket."+date;

        (new SQL.MainMarket(Iuuid)).registerItem(Iuuid, altera, (new SQL.Converter()).encodeItem(itemStack), count, player.getUniqueId().toString(), itemName);
        (new SQL.PlayerMarket(player)).registerItem(date, itemStack, count, Iuuid, altera, System.currentTimeMillis());

        MarketUpdate();
    }

    private void MarketBuy(Player player, ItemStack itemStack) {

        if(player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§c인벤토리에 공간이 없습니다! 인벤토리를 먼저 비워주세요");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            return;
        }

        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbtTagCompound = nmsStack.getTag();
        String Iuuid = nbtTagCompound.getString("UUID");

        boolean isExist = false;


        try {
            Connection connection = SQL.sqlData.getConnection();
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery("select * from longinus.mainmarket where uuid = '"+Iuuid+"'");

            while(set.next()) {
                if(set.getString("uuid").equals(Iuuid)) {
                    isExist = true;

                    if(set.getString("seller").equals(player.getUniqueId().toString())) {
                        player.sendMessage("§c자신의 아이템을 구매할 수 없습니다");
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        break;
                    }
                    long altera = set.getLong("altera");
                    if(altera <= (new SQL.PlayerAltera(player)).getAltera()) {

                        try {

                            YamlConfiguration yaml = (new PlayerMarket(set.getString("seller"))).getMarketItemsFile();
                            for(String s_ : yaml.getConfigurationSection("mainMarket").getKeys(false)) {
                                if(yaml.getString("mainMarket."+s_+".uuid").equals(Iuuid)) {
                                    yaml.set("mainMarket."+s_+".item", UI.itemSold(itemStack.getItemMeta().getDisplayName()));
                                    break;
                                }
                            }

                            (new PlayerMarket(set.getString("seller"))).sendToSQLServer(yaml);

                            UUID selleruuid = UUID.fromString(set.getString("seller"));
                            for(Player p_ : Bukkit.getOnlinePlayers()) {
                                if(p_.getUniqueId().equals(selleruuid)) {
                                    p_.sendMessage("§5>> "+itemStack.getItemMeta().getDisplayName()+" §d마켓에서 판매됨. 마켓플레이스에서 알테라를 수령해주세요");
                                    break;
                                }
                            }
                            //config_.save(file_);
                        }
                        catch(Exception e) {
                            player.sendMessage("§c예기치 않은 오류가 발생했습니다");
                        }


                        (new SQL.PlayerAltera(player)).setAltera( (new SQL.PlayerAltera(player)).getAltera() - altera);
                        ItemStack item = (new SQL.Converter()).decodeItem(set.getString("item"));
                        player.getInventory().addItem(item);
                        player.sendMessage("§a구매 완료!");
                        player.closeInventory();

                        (new SQL.sqlData()).QueryLogMarket(altera, item, item.getAmount(), set.getString("seller"),
                                player.getUniqueId().toString(), set.getString("selltime"));
                        //registerItemAverage(item, altera);

                        int z = statement.executeUpdate("delete from longinus.mainmarket where uuid = '"+Iuuid+"'");

                    }
                    else {
                        player.closeInventory();
                        player.sendMessage("§c알테라가 부족합니다!");
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 2);

                    }
                    break;
                }
            }
            if(!isExist) {
                player.sendMessage("§c마켓에 등록되지 않은 아이템입니다");
            }
            set.close();
            statement.close();
            //connection.close();
        }
        catch(Exception e) {

        }
        MarketUpdate();
    }

    private void MarketSearch(Player player, String search) {

    }

    private void getAlteraFromMySalesStand(Player player, ItemStack itemStack) {

        PlayerMarket pM = new PlayerMarket(player);
        PlayerAltera pA = new PlayerAltera(player);

        YamlConfiguration yaml = pM.getMarketItemsFile();

        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        String UUID = nmsStack.getTag().getString("UUID");

        for(String s : yaml.getConfigurationSection("mainMarket").getKeys(false)) {
            ItemStack itemStack_ = yaml.getItemStack("mainMarket."+s+".item");
            net.minecraft.world.item.ItemStack nmsStack_ = CraftItemStack.asNMSCopy(itemStack_);
            if(nmsStack_.getTag().getString("UUID").equals(UUID)); {
                long altera = yaml.getLong("mainMarket."+s+".altera");
                long sumAltera = 0;
                try {
                    sumAltera = Math.addExact(pA.getAltera(), altera);
                }
                catch(ArithmeticException e) {
                    sumAltera = Long.MAX_VALUE;
                }
                yaml.set("mainMarket."+s, null);
                pM.sendToSQLServer(yaml);

                pA.setAltera(sumAltera);
                player.sendMessage("§a알테라 수령 완료! +"+altera+" §7보유 알테라: "
                        +pA.getAltera());
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);

                break;
            }
        }


        Window.mySellList(player);
    }

    private void getItemFromMySalesStand(Player player, ItemStack itemStack) {

        if(player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§c인벤토리에 공간이 없습니다! 인벤토리를 먼저 비워주세요");
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            return;
        }
        try {

            net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
            String UUID = nmsStack.getTag().getString("UUID");

            PlayerMarket pM = new PlayerMarket(player);
            YamlConfiguration yaml = pM.getMarketItemsFile();

            if(yaml.getConfigurationSection("mainMarket") != null) {
                for(String s : yaml.getConfigurationSection("mainMarket").getKeys(false)) {
                    if(yaml.getString("mainMarket."+s+".uuid").equals(UUID)) {
                        ItemStack item = yaml.getItemStack("mainMarket."+s+".item");
                        player.getInventory().addItem(item);
                        break;
                    }
                }
            }

            try {
                Connection conn = SQL.sqlData.getConnection();
                Statement statement = conn.createStatement();
//                ResultSet set = statement.executeQuery("select item from longinus.mainmarket where uuid = '"+UUID+"'");
//                while(set.next()) {
//                    ItemStack item = (new SQL.Converter()).decodeItem(set.getString("item"));
//                    player.getInventory().addItem(item);
//                }
                statement.executeUpdate("delete from longinus.mainmarket where uuid = '"+UUID+"'");
                pM.deleteItem(UUID);

//                set.close();
                statement.close();
                //conn.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            //config.save(file);
        }
        catch(Exception e) {

        }
        Window.mySellList(player);
        MarketUpdate();
    }

    public void noticeYourItemsWereSoldWhenJoin(Player player) {

        int count = 0;
        PlayerMarket pM = new PlayerMarket(player);
        YamlConfiguration yaml = pM.getMarketItemsFile();

        try {
            for(String s : yaml.getConfigurationSection("mainMarket").getKeys(false)) {
                ItemStack itemStack = yaml.getItemStack("mainMarket."+s+".item");
                ItemMeta itemMeta = itemStack.getItemMeta();
                if(itemMeta.getDisplayName().contains("판매 완료!")) {
                    count++;
                }
            }
        }
        catch(NullPointerException e) {

        }
        if(count != 0) {
            player.sendMessage("§5>> §6"+count+"§a개의 아이템이 판매되었습니다! 마켓플레이스에서 알테라를 수령해 주세요");
        }


    }

    private void BuyItem(Player player, ItemStack itemStack) {

    }

    private class UI {

        static ItemStack register(Player player) {

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

        static ItemStack SeeMySellList(Player player) {
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


                try {
                    Connection conn = SQL.sqlData.getConnection();
                    Statement stmt = conn.createStatement();
                    Statement stmt_ = conn.createStatement();
                    Statement stmt__ = conn.createStatement();
                    ResultSet set = stmt.executeQuery("select avg(altera), max(altera), min(altera) from longinus.mainmarketlog where name = '"+itemName+"' order by buytime desc limit 0, 10");
                    if(set.next()) {
                        average = set.getLong("avg(altera)");
                        min = set.getLong("min(altera)");
                        max = set.getLong("max(altera)");
                    }

                    set.close();
                    stmt.close();
                    //conn.close();
                }
                catch(Exception e) {
                    e.printStackTrace();
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
            ItemStack itemStack = new ItemStack(Material.EMERALD_BLOCK, 1);

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

        ItemStack nextPage() {
            ItemStack itemStack = new ItemStack(Material.LIGHT_BLUE_DYE, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§a다음 페이지");
            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

        ItemStack prePage() {
            ItemStack itemStack = new ItemStack(Material.PINK_DYE, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§c이전 페이지");
            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

        ItemStack Search() {
            ItemStack itemStack = new ItemStack(Material.COMPASS, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§e아이템 검색");
            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }
    }

    private class Window {

        void Register(Player player) {

            YamlConfiguration yaml = (new PlayerMarket(player)).getMarketItemsFile();

            if(yaml.getConfigurationSection("mainMarket") != null) {
                int j=0;
                for(String s : yaml.getConfigurationSection("mainMarket").getKeys(false)) {
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

            PlayerMarket pM = new PlayerMarket(player);

            YamlConfiguration yaml = pM.getMarketItemsFile();

            List<Long> paths = new ArrayList<>();

            if(yaml.getConfigurationSection("mainMarket") != null) {
                for(String i : yaml.getConfigurationSection("mainMarket").getKeys(false)) {
                    paths.add(Long.parseLong(i));
                }
            }

            try {
                for(int i=11; i<=15; i++) {
                    if(paths.size() == i-11) break;
                    String path = Long.toString(paths.get(i-11));
                    if(yaml.contains("mainMarket."+path)) {
                        ItemStack itemStack = yaml.getItemStack("mainMarket."+path+".item");
                        if(itemStack == null) inv.setItem(i, new ItemStack(Material.AIR, 1));
                        else {
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            long millis = yaml.getLong("mainMarket."+paths.get(i-11)+".millis");
                            long remain =  millis + saleLimit  - System.currentTimeMillis();
                            List<String> lore = itemMeta.getLore();

                            if(itemMeta.getDisplayName().contains("판매 완료!")) {
                                lore.add(0, "§5- §7판매가: §f"+yaml.getLong("mainMarket."+paths.get(i-11)+".altera"));
                                lore.add(0, "");
                            }
                            else if(remain < 0) {
                                lore.clear();
                                lore.add(0, "");
                                lore.add(0, "§7클릭하여 아이템을 회수해 주세요");
                                lore.add(0, "§7남은 판매기간: §c판매가 종료되었습니다");
                                lore.add(0, "");
                                lore.add(0, "§5- §7판매가: §f"+yaml.getLong("mainMarket."+paths.get(i-11)+".altera"));
                                lore.add(0, "");

                            }
                            else {
                                String day = remain / 86400000 > 0 ? remain / 86400000+"일 " : "";
                                String hour = remain % 86400000 / 3600000 > 0 ? remain % 86400000 / 3600000+"시 " : "";
                                String minute = remain % 86400000 % 3600000 / 60000 > 0 ? remain % 86400000 % 3600000 / 60000+"분 " : "";
                                String second = minute.equals("") ? remain % 86400000 % 3600000 % 60000 / 1000+"초 " : "";

                                lore.add(0, "");
                                lore.add(0, "§7남은 판매기간: "+day+hour+minute+second+"남음");
                                lore.add(0, "§5- §7판매가: §f"+yaml.getLong("mainMarket."+paths.get(i-11)+".altera"));
                                lore.add(0, "");
                            }

                            itemMeta.setLore(lore);
                            itemStack.setItemMeta(itemMeta);
                            inv.setItem(i, itemStack);
                        }
                    }
                }
            }
            catch(Exception e) {
                e.printStackTrace();
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
