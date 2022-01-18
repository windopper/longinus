package PlayerChip.SkillTalent;

import utils.GUICancelHandler;
import PlayerChip.Maingui;
import PlayerManager.PlayerManager;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import spellinteracttest.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TalentUI implements Listener {

    @EventHandler
    public void UIClick(InventoryClickEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        if(itemStack == null) return;
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        if(title.equals("특성을 선택할 스킬을 선택해주세요")) {
            int slot = event.getRawSlot();
            try {
                if(slot == 22) {
                    PlayerManager pm = PlayerManager.getinstance(player);
                    pm.resetAllTalent();
                    (new TalentUI(player)).openGUI();
                    return;
                }
                else if(slot == 18) {
                    Maingui.getinstance().chipitemguiopen(player);
                    return;
                }

                net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
                NBTTagCompound tag = nmsStack.getTag();
                (new TalentUI(player)).openTalentGUI(tag.getString("skill"));
            }
            catch(Exception e) {
                e.printStackTrace();
                return;
            }
        }
        else if(title.contains("특성을 선택해주세요")) {

            int slot = event.getRawSlot();

            try {
                PlayerManager pm = PlayerManager.getinstance(player);
                net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
                NBTTagCompound tag = nmsStack.getTag();

                String skill = tag.getString("skill");

                if(slot == 40) {
                    pm.resetTalent(skill);
                    (new TalentUI(player)).openTalentGUI(tag.getString("skill"));
                    return;
                }
                else if(slot == 36) {
                    (new TalentUI(player)).openGUI();
                    return;
                }

                int talent = tag.getInt("talent");
                int tier = tag.getInt("tier");


                if(tier != 1) {
                    if(pm.getTalent(skill, tier-1) == 0) {
                        player.sendMessage("§c아직 이전 단계의 특성을 선택하지 않았습니다");
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        return;
                    }
                }

                pm.setTalent(skill, tier, talent);
                (new TalentUI(player)).openTalentGUI(tag.getString("skill"));
            }
            catch(Exception e) {
                return;
            }
        }
    }

    private Player player;
    private PlayerManager pm;
    private String currentClass;

    public TalentUI() {

    }

    public TalentUI(Player player) {
        this.player = player;
        this.pm = PlayerManager.getinstance(player);
        this.currentClass = pm.CurrentClass;
    }

    public void openGUI() {
        GUICancelHandler.setClickCanceller("특성을 선택할 스킬을 선택해주세요");
        Inventory inv = Bukkit.createInventory(null, 27, "특성을 선택할 스킬을 선택해주세요");
        inv.setItem(10, selectSkill("RL"));
        inv.setItem(12, selectSkill("RR"));
        inv.setItem(14, selectSkill("FR"));
        inv.setItem(16, selectSkill("SR"));
        inv.setItem(22, resetAll());
        inv.setItem(18, fallBack());

        player.openInventory(inv);
    }

    public void openTalentGUI(String skill) {
        GUICancelHandler.setClickCanceller("특성을 선택해주세요");
        Inventory inv = Bukkit.createInventory(null, 45, "특성을 선택해주세요 [남은 포인트: §c"+pm.getTalentPoint()+"§r]");

        inv.setItem(10, selectTalent(skill, 1, 1));
        inv.setItem(19, selectTalent(skill, 2, 1));
        inv.setItem(28, selectTalent(skill, 3, 1));

        inv.setItem(12, selectTalent(skill, 1, 2));
        inv.setItem(21, selectTalent(skill, 2, 2));
        inv.setItem(30, selectTalent(skill, 3, 2));

        inv.setItem(14, selectTalent(skill, 1, 3));
        inv.setItem(23, selectTalent(skill, 2, 3));
        inv.setItem(32, selectTalent(skill, 3, 3));

        inv.setItem(16, selectTalent(skill, 1, 4));
        inv.setItem(25, selectTalent(skill, 2, 4));
        inv.setItem(34, selectTalent(skill, 3, 4));

        inv.setItem(40, reset(skill));
        inv.setItem(36, fallBack());

        player.openInventory(inv);
    }

    public ItemStack selectSkill(String skill) {
        ItemStack itemStack = Materials.getInstance().selected();
        ItemMeta itemMeta = itemStack.getItemMeta();
        File file = new File(Main.getPlugin(Main.class).getDataFolder()+"\\ClassDescription.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        itemMeta.setDisplayName(yaml.getString(currentClass+".Skill."+skill+".Name"));
        itemMeta.setLore(yaml.getStringList(currentClass+".Skill."+skill+".Lore"));

        itemStack.setItemMeta(itemMeta);

        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsStack.getOrCreateTag();
        tag.setString("skill", skill);
        nmsStack.setTag(tag);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public ItemStack selectTalent(String skill, int talent, int tier) {
        int selectedTalent = pm.getTalent(skill, tier);
        ItemStack itemStack;
        if(pm.getNextTier(skill) < tier) itemStack = Materials.getInstance().notOpened();
        else if(selectedTalent == talent) itemStack = Materials.getInstance().selected();
        else itemStack = Materials.getInstance().unselected();
        ItemMeta itemMeta = itemStack.getItemMeta();

        File file = new File(Main.getPlugin(Main.class).getDataFolder()+"\\ClassDescription.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        itemMeta.setDisplayName(yaml.getString(currentClass+".Talent."+skill+"."+tier+"."+talent+".Name"));

        List<String> list = new ArrayList<>();
        list.addAll(yaml.getStringList(currentClass + ".Talent." + skill + "." + tier + "." + talent + ".Lore"));
        for(int i=0; i<list.size(); i++) {
            list.set(i, "§7"+list.get(i));
        }

        if(pm.getNextTier(skill) < tier) {
            list.add("§c아직 선택할 수 없습니다 "+(tier-1)+"티어 특성 선택시 해금");
            list.add(0, "");
        }
        else {
            list.add(0, "");
            if(selectedTalent == talent) list.add(0, "§a>>선택됨<<");
            else list.add(0, "§7>>선택되지 않음<<");

        }

        itemMeta.setLore(list);

        itemStack.setItemMeta(itemMeta);

        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsStack.getOrCreateTag();
        tag.setInt("talent", talent);
        tag.setInt("tier", tier);
        tag.setString("skill", skill);
        nmsStack.setTag(tag);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public ItemStack reset(String skill) {
        ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName("§c해당 스킬의 특성을 초기화합니다");
        itemStack.setItemMeta(itemMeta);

        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsStack.getOrCreateTag();
        tag.setString("skill", skill);
        nmsStack.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public ItemStack resetAll() {
        ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName("§c모든 스킬의 특성을 초기화합니다");
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemStack fallBack() {
        ItemStack itemStack = new ItemStack(Material.ARROW, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName("§a뒤로 가기");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
