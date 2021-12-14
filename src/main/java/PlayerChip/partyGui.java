package PlayerChip;

import Party.PartyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class partyGui {

    public void openPartyGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 36, "파티 관리");

        inventory.setItem(27, backToMenuItem());
        inventory.setItem(35, partyLeaveItem());
        setPartyGui(inventory, player);



        player.openInventory(inventory);
    }

    public void openPartyPlayerOptionGui(Player commander, Player target) {
        Inventory inventory = Bukkit.createInventory(null, 36, target.getName()+"의 파티 설정");

        inventory.setItem(13, playerHead(target));
        inventory.setItem(21, partyPromoteItem(target));
        inventory.setItem(23, partyKickItem(target));
        inventory.setItem(27, backToMenuItem());


        commander.openInventory(inventory);

    }

    public ItemStack backToMenuItem() {
        ItemStack item = new ItemStack(Material.ARROW, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c뒤로 가기"));

        item.setItemMeta(meta);

        return item;
    }

    public Inventory setPartyGui(Inventory inventory, Player player) {

        PartyManager partyManager = PartyManager.getParty(player);
        int setSlot = 0;
        if(partyManager != null) {

            List<Player> partyMembers = partyManager.getMembers();

            for(Player members : partyMembers) {
                ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
                itemMeta.setOwningPlayer(members);
                itemMeta.setDisplayName("§e"+members.getName());
                itemMeta.setLore(Arrays.asList("",
                        "§7클릭하여 해당 플레이어 파티 설정"));
                itemStack.setItemMeta(itemMeta);

                inventory.setItem(setSlot, itemStack);
                setSlot ++;
            }

        }

        ItemStack itemStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§a플레이어 초대 하기");
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(setSlot, itemStack);

        return inventory;
    }

    public ItemStack playerHead(Player player) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.setDisplayName("§b"+player.getName());
        itemStack.setItemMeta(skullMeta);

        return itemStack;
    }

    public ItemStack partyKickItem(Player player) {
        ItemStack itemStack= new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(player.getName()+"§e님을 파티에서 추방합니다");

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack partyPromoteItem(Player player) {
        ItemStack itemStack= new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(player.getName()+"§b님을 파티장으로 승급시킵니다");

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack partyLeaveItem() {
        ItemStack itemStack = new ItemStack(Material.BARRIER, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§c파티 나가기");
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }


}
