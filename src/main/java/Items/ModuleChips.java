package Items;

import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import spellinteracttest.Main;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModuleChips implements Listener {

    private static String guiName = "설치할 모듈을 선택해주세요";
    private final static Set<Player> installing = new HashSet<>();

    @EventHandler
    public void clickListener(InventoryClickEvent event) {
        if(event.getView().getTitle().equals(guiName)) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();
            Inventory inv = event.getInventory();
            install install = new install();
            ItemStack clickedItem  = event.getCurrentItem();
            ItemMeta itemMeta = null;
            String itemName = "";
            try {
                itemMeta = clickedItem.getItemMeta();
                itemName = itemMeta.getDisplayName();
            }
            catch(Exception e) {
                return;
            }



            if(slot >= 27) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                inv.setItem(22, install.GREEN_WOOL());
                for(int i=15; i>=11; i--) {
                    inv.setItem(i, install.BLACK_STAINED_GLASS_PANE());
                }
                installing.remove(player);
                if(install.isValidChip(clickedItem)) {
                    if(inv.getItem(10) != null) {
                        ItemStack itemStack = inv.getItem(10);
                        if(itemStack == clickedItem) return;

                        inv.removeItem(itemStack);
                        if(player.getInventory().firstEmpty() == -1) {
                            player.sendMessage("§c인벤토리에 공간이 부족하여 아이템이 드랍되었습니다");
                            Item item = (Item) player.getWorld().spawnEntity(player.getLocation(), EntityType.DROPPED_ITEM);
                            item.setItemStack(itemStack);
                            item.setOwner(player.getUniqueId());
                        }
                        else {
                            player.getInventory().addItem(itemStack);
                        }
                    }

                    int originamount = clickedItem.getAmount();
                    ItemStack copyitem = clickedItem.clone();
                    copyitem.setAmount(1);
                    inv.setItem(10, copyitem);

                    player.getInventory().removeItem(clickedItem);
                    clickedItem.setAmount(originamount - 1);
                    player.getInventory().addItem(clickedItem);

                }
                else if(install.slotCheck(clickedItem, player)) {

                    if(inv.getItem(16) != null) {
                        ItemStack itemStack = inv.getItem(16);
                        inv.removeItem(itemStack);
                        if(player.getInventory().firstEmpty() == -1) {
                            player.sendMessage("§c인벤토리에 공간이 부족하여 아이템이 드랍되었습니다");
                            Item item = (Item) player.getWorld().spawnEntity(player.getLocation(), EntityType.DROPPED_ITEM);
                            item.setItemStack(itemStack);
                            item.setOwner(player.getUniqueId());
                        }
                        else {
                            player.getInventory().addItem(itemStack);
                            player.updateInventory();
                        }
                    }

                    inv.setItem(16, clickedItem);
                    player.getInventory().removeItem(clickedItem);

                }
            }
            else if(itemName.contains("설치시작")) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                if(inv.getItem(10) != null && inv.getItem(16) != null) {
                    if(install.slotCheck(inv.getItem(16), player)) {
                        install.startInstalling(player, inv);
                        installing.add(player);
                    }
                    else {
                        player.sendMessage("§c슬롯이 부족합니다");
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 2);
                    }
                }
                else if(inv.getItem(10) == null) {

                }
                else if(inv.getItem(16) == null) {

                }
            }
            else if(itemName.contains("설치중단")) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                inv.setItem(22, install.GREEN_WOOL());
                for(int i=15; i>=11; i--) {
                    inv.setItem(i, install.BLACK_STAINED_GLASS_PANE());
                }
                installing.remove(player);
            }
            else if(slot == 10 || slot == 16) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                inv.setItem(22, install.GREEN_WOOL());
                for(int i=15; i>=11; i--) {
                    inv.setItem(i, install.BLACK_STAINED_GLASS_PANE());
                }
                installing.remove(player);
                if(inv.getItem(10) != null && slot == 10) {
                    ItemStack itemStack = inv.getItem(10);
                    inv.removeItem(itemStack);
                    if(player.getInventory().firstEmpty() == -1) {
                        player.sendMessage("§c인벤토리에 공간이 부족하여 아이템이 드랍되었습니다");
                        Item item = (Item) player.getWorld().spawnEntity(player.getLocation(), EntityType.DROPPED_ITEM);
                        item.setItemStack(itemStack);
                        item.setOwner(player.getUniqueId());
                    }
                    else {
                        player.getInventory().addItem(itemStack);
                    }
                }
                else if(inv.getItem(16) != null && slot == 16) {
                    ItemStack itemStack = inv.getItem(16);
                    inv.removeItem(itemStack);
                    if(player.getInventory().firstEmpty() == -1) {
                        player.sendMessage("§c인벤토리에 공간이 부족하여 아이템이 드랍되었습니다");
                        Item item = (Item) player.getWorld().spawnEntity(player.getLocation(), EntityType.DROPPED_ITEM);
                        item.setItemStack(itemStack);
                        item.setOwner(player.getUniqueId());
                    }
                    else {
                        player.getInventory().addItem(itemStack);
                    }
                }
            }
        }
    }

    @EventHandler
    public void clickCanceller(InventoryDragEvent event) {
        if(event.getView().getTitle().equals(guiName)) event.setCancelled(true);
    }

    @EventHandler
    public void clickCanceller_(InventoryInteractEvent event) {
        if(event.getView().getTitle().equals(guiName)) event.setCancelled(true);
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent event) {
        if(event.getView().getTitle().equals(guiName)) {
            Player player = (Player) event.getPlayer();
            Inventory inv = event.getInventory();

            installing.remove(player);

            ItemStack itemStack = inv.getItem(10);
            ItemStack itemStack_ = inv.getItem(16);

            if(itemStack != null) {
                if(player.getInventory().firstEmpty() == -1) {
                    player.sendMessage("§c인벤토리에 공간이 부족하여 아이템이 드랍되었습니다");
                    Item item = (Item) player.getWorld().spawnEntity(player.getLocation(), EntityType.DROPPED_ITEM);
                    item.setItemStack(itemStack);
                    item.setOwner(player.getUniqueId());
                }
                else player.getInventory().addItem(itemStack);
            }
            if(itemStack_ != null) {
                if(player.getInventory().firstEmpty() == -1) {
                    player.sendMessage("§c인벤토리에 공간이 부족하여 아이템이 드랍되었습니다");
                    Item item = (Item) player.getWorld().spawnEntity(player.getLocation(), EntityType.DROPPED_ITEM);
                    item.setItemStack(itemStack_);
                    item.setOwner(player.getUniqueId());
                }
                else player.getInventory().addItem(itemStack_);
            }

        }
    }

    public enum ChipList {

        NULL("NULL", Arrays.asList("NULL"), 0),
        테스트_칩("테스트 칩", Arrays.asList("§7프로토타입"), 1),
        프로토타입("프로토타입V2", Arrays.asList("§7YEEEEEEEE"), 2);


        String name;
        List<String> lore;
        int code;

        ChipList(String name, List<String> lore, int code) {
            this.name = name;
            this.lore = lore;
            this.code = code;
        }

        public String getName() { return name; }
        public List<String> getLore() { return lore; }
        public int getCode() { return code; }

    }

    public class install {

        public boolean slotCheck(ItemStack itemStack, Player player) {
            net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
            NBTTagCompound tag = nmsStack.getTag();
            int remainSlot = 0;
            try {
                int[] array = tag.getIntArray("chips");
                for(int i=0; i<array.length; i++) {
                    if(array[i] == 0) {
                        return true;
                    }
                }
            }
            catch(NullPointerException e) {
                return false;
            }

            player.sendMessage("§c슬롯이 부족합니다");
            return false;
        }

        public boolean isValidChip(ItemStack itemStack) {
            net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
            NBTTagCompound tag = nmsStack.getTag();
            try {
                int code = tag.getInt("chipCode");
                if(code == 0) return false;
                return true;
            }
            catch(NullPointerException e) {
                return false;
            }
        }

        public ItemStack installModule(ItemStack item, ItemStack chip) {
            net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(chip);
            NBTTagCompound tag = nmsStack.getTag();
            int code = tag.getInt("chipCode");

            net.minecraft.world.item.ItemStack nmsStack_ = CraftItemStack.asNMSCopy(item);
            NBTTagCompound tag_ = nmsStack_.getTag();
            int[] chips = tag_.getIntArray("chips");
            for(int i=0; i<chips.length; i++) {
                if(chips[i] == 0) {
                    chips[i] = code;
                    break;
                }
            }
            tag_.setIntArray("chips", chips);
            nmsStack_.setTag(tag_);
            ItemStack itemStack = CraftItemStack.asBukkitCopy(nmsStack_);
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore =itemMeta.getLore();
            int loc = 0;
            boolean modulelore = false;
            for(String s : lore) {
                if(s.contains("모듈")) {
                    modulelore = true;
                    break;
                }
                loc++;
            }

            if(!modulelore) {
                lore.add(loc,"§6- §5"+ Arrays.stream(ChipList.values())
                        .filter((a) -> a.getCode() == code)
                        .toList()
                        .get(0).getName());
                lore.add(loc,"§7현재 장착된 모듈: ");
                lore.add(loc, "");
            }
            else {
                lore.add(loc+1,"§6- §5"+ Arrays.stream(ChipList.values())
                        .filter((a) -> a.getCode() == code)
                        .toList()
                        .get(0).getName());
            }


            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

        public void startInstalling(final Player player, final Inventory inv) {

            inv.setItem(22, RED_WOOL());

            new BukkitRunnable() {

                int time = 0;
                int slot = 11;
                @Override
                public void run() {
                    if(!installing.contains(player)) {
                        cancel();
                        return;
                    }
                    for(int i=11; i<=slot; i++) {
                        inv.setItem(i, GREEN_STAINED_GLASS_PANE(time));
                    }

                    if(slot == 15) {
                        ItemStack completeItem = installModule(inv.getItem(16), inv.getItem(10));
                        inv.setItem(16, completeItem);
                        inv.removeItem(inv.getItem(10));
                        inv.setItem(22, GREEN_WOOL());
                        installing.remove(player);
                        for(int i=11; i<=15; i++) inv.setItem(i, BLACK_STAINED_GLASS_PANE());
                        player.playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 1, 1);
//                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
//                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
//                        }, 20);
                        cancel();

                    }
                    time++;
                    if(time%4==0)
                        slot++;
                }
            }.runTaskTimer(Main.getPlugin(Main.class), 0, 10);
        }

        public void openGUI(Player player) {
            Inventory inv = Bukkit.createInventory(null, 27, "설치할 모듈을 선택해주세요");
            for(int i=0; i<27; i++) {
                if(i==10 || i==16) continue;
                if(i==22) {
                    inv.setItem(i, GREEN_WOOL());
                    continue;
                }
                inv.setItem(i, BLACK_STAINED_GLASS_PANE());
            }

            player.openInventory(inv);
        }

        private ItemStack BLACK_STAINED_GLASS_PANE() {
            ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(" ");
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }

        private ItemStack GREEN_STAINED_GLASS_PANE(int time) {
            ItemStack itemStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            StringBuilder j = new StringBuilder();
            for(int i=time%4; i>0; i--) {
                j.append(".");
            }
            itemMeta.setDisplayName("§a설치중"+j);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }

        private ItemStack GREEN_WOOL() {
            ItemStack itemStack = new ItemStack(Material.GREEN_WOOL);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§a설치시작");
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }

        private ItemStack RED_WOOL() {
            ItemStack itemStack = new ItemStack(Material.RED_WOOL);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§c설치중단");
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
    }

    public ItemStack getChip(String name) {

        ChipList chip = Arrays.stream(ChipList.values()).filter((a) -> a.name().equals(name)).toList().get(0);
        ItemStack itemStack = new ItemStack(Material.COMPARATOR, 1);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§5"+chip.getName());
        itemMeta.setLore(chip.getLore());
        itemStack.setItemMeta(itemMeta);

        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsStack.getOrCreateTag();
        tag.setInt("chipCode", chip.getCode());
        nmsStack.setTag(tag);
        itemStack = CraftItemStack.asBukkitCopy(nmsStack);

        return itemStack;
    }

    public class functions {

        Player player;

        public functions(Player player) {
            this.player = player;
        }

        public void invokeChipAbility() {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
            NBTTagCompound tag = nmsStack.getTag();
            try {
                int[] chips = tag.getIntArray("chips");
                List<ChipList> list = Arrays.stream(ChipList.values())
                        .filter((a) -> Arrays.stream(chips).anyMatch((b) -> b == a.getCode())).toList();


            }
            catch(Exception e) {

            }

        }

        public void 테스트_칩() {

        }
        public void 프로토타입V2() {

        }
    }
}
