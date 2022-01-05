package PlayerChip;

import Items.ItemSetter;
import Mob.MobListManager;
import PlanetSelect.PlanetList;
import SQL.PlayerSample;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectGui {

    public final String name1 = "수집품을 둘러 볼 행성을 선택하세요";
    public final String name2 = "수집품들";

    public final int back1 = 9;
    public final int back2 = 45;

    public void OpenCollectPlanetGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 18, name1);

        planets(inventory);
        inventory.setItem(back1, (new DefaultItem().backToMenuItem()));


        player.openInventory(inventory);
    }

    public void OpenCollectGuiGui(Player player, int rawslot) {
        Inventory inventory = Bukkit.createInventory(null, 54, name2);


        Collectings(inventory, rawslot, player);
        inventory.setItem(back2, (new DefaultItem().backToMenuItem()));

        player.openInventory(inventory);
    }

    private Inventory Collectings(Inventory inventory, int rawslot, Player player) {

        String uuid = player.getUniqueId().toString();
        String username = player.getName();

        YamlConfiguration yaml = (new PlayerSample(player)).getSampleFile();
        String Planet = PlanetList.values()[rawslot].getRawName();

        int count = 0;
        for(MobListManager.MobList mobList : MobListManager.MobList.values()) {
            if(mobList.getPlanet().equals(Planet)) {

                int num = yaml.getInt(mobList.getPlanet()+"."+mobList.name()+".count");
                String firstSeen = yaml.getString(mobList.getPlanet()+"."+mobList.name()+".firstSeen");
                String lastSeen = yaml.getString(mobList.getPlanet()+"."+mobList.name()+".lastSeen");
                inventory.setItem(count, setMetaData(mobList, num, firstSeen, lastSeen));
                count++;

            }
        }

        return inventory;
    }

    private ItemStack setMetaData(MobListManager.MobList mobList, int num, String firstSeen, String lastSeen) {

        ItemStack itemStack;

        if(num == 0) {
            itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            int namespace = mobList.getRawName().length();
            String name = "§7";
            for(int i=0; i<namespace; i++) name+="?";
            itemMeta.setDisplayName(name);
            itemMeta.setLore(Arrays.asList("§8§o아직 발견되지 않았습니다!"));

            itemStack.setItemMeta(itemMeta);
        }
        else {
            itemStack = mobList.getItemStack();
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§c"+mobList.getRawName()+"");

            List<String> strings = new ArrayList<>();
            strings.addAll(Arrays.asList(
                    "",
                    "§3>> 레벨: §6Lv."+mobList.getLevel(),
                    "§3>> 체력: §d♥ "+mobList.getHealth(),
                    "§3>> 피해량: §4"+mobList.getMindamage()+"-"+mobList.getMaxdamage(),
                    "",
                    "§7최초발견: "+firstSeen));
            strings.addAll((new ItemSetter()).setGrayLore(mobList.getDescription()));

            itemMeta.setLore(strings);
            itemStack.setItemMeta(itemMeta);

            itemStack.setItemMeta(itemMeta);
        }




        return itemStack;
    }


    private Inventory planets(Inventory inventory) {

        int count = 0;

        for(PlanetList planetList : PlanetList.values()) {

            inventory.setItem(count, setMetaData(planetList));
            count++;
        }


        return inventory;
    }

    private ItemStack setMetaData(PlanetList planetList) {

        ItemStack itemStack = planetList.getPlanet();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§6"+planetList.getRawName()+"§d의 수집품 둘러보기");
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
