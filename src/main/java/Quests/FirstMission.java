package Quests;

import Items.ItemFunctions;
import Items.ItemManager;
import PlayerManager.PlayerManager;
import QuestFunctions.QuestFunctions;
import QuestFunctions.QuestList;
import QuestFunctions.QuestNPCManager;
import QuestFunctions.UserQuestManager;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EnumItemSlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FirstMission {

    private static final HashMap<Player, FirstMission> instance = new HashMap<>();

    private final String Questname = "첫번째_임무";
    private int detailStep = 0;
    private Player p;

    private final List<String> Script_1 = Arrays.asList(
            "데이즈: 오, 왔구나! 여기 이 종자를 가지고 §e글리제581c§r에서 §b아르안§r에게 가져다주면 돼",
            "데이즈: 쉽게 상하진 않는 물건이지만, 그래도 조심히 운반해 줘.",
            "데이즈: 오늘이 처음이라고 했지? 개척자 구역은 처음이라 신기한 게 많을 테지만 급한 일이라 수고 좀 해줘",
            "데이즈: 저기 보이는 우주선에 들어가",
            "",
            "데이즈: 장거리 비행의 경우에는 숙지가 필수지만 네가 지금 갈 행성은 행선지만 정하면 데려다줄 거야");

    private final List<String> Script_2 = Arrays.asList(
            "아르안: 종자를 가지고 왔다고? 상태는 괜찮겠지?",
            "아르안: 나쁘진 않군. 좋아",
            "아르안 : 저기 잠시 쉬어있어라. 필요하면 부를 테니",
            "",
            "아르안: 일단 들어보지",
            "",
            "아르안: 무엇을 묻고 싶은 거지? 그럼 내가 연구자로 보이나?",
            "아르안: 네가 개척자에 대해 무슨 환상을 품고 있는지 안다.",
            "아르안: 모험하고, 위기를 극복하고, 매번 도전하며 롱기누스에 새로운 빛을 가져다주는 사람",
            "아르안: 개척자는 그것이 전부가 아니다.",
            "아르안: 길을 여는 자가 있으면, 길을 닦는 자도 필요한 법이지.",
            "아르안: 그만 가서 쉬어라 급히 부를 일이 생길듯하니"
    );

    private FirstMission() {

    }

    private FirstMission(Player p) {
        this.p = p;
        Script_1.set(4, p.getName()+": 우주선 조종은 어떻게 하는거죠?");
        Script_2.set(3, p.getName()+": 저기 질문하나 해도 될까요?");
        Script_2.set(5, p.getName()+": 당신은 개척자인가요?");
    }

    public static FirstMission getinstance(Player p) {
        if(!instance.containsKey(p)) instance.put(p, new FirstMission(p));
        return instance.get(p);
    }

    public void removeinstance() {
        instance.remove(p);
    }

    public static void initQuestNPCs() {
        Location StartLoc = new Location(Bukkit.getWorld("world"), 2.5, 52, -37.5, 39, 3);
        QuestNPCManager.getinstance().createNPC(StartLoc, "데이즈", "", "");

        Location StartLoc2 = new Location(Bukkit.getWorld("gliese581c"), -155.5, 124, 245.5, 146, 11);
        QuestNPCManager.getinstance().createNPC(StartLoc2, "아르안", "", "");
    }

    public final void QuestProgress(EntityPlayer NPC, String NPCname) {

        QuestFunctions QNF = new QuestFunctions(p);

        int QuestStep = UserQuestManager.Singleton().getQuestStep(Questname, p);

        // When Progress is 0
        if(QuestStep == 0 && NPCname.equals("데이즈")) {

            if(QuestList.valueOf(Questname).getLevelReq() > PlayerManager.getinstance(p).getlvl()) {
                p.sendMessage("레벨부족");
            }
            else {
                if(!QNF.ShowScripts(Script_1, detailStep)) {

                    // 종자 아이템 데이즈로부터 받기
                    if(detailStep == 1) {

                        ItemStack itemStack = new ItemStack(Material.BEETROOT_SEEDS, 1);
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName("§6데이즈의 종자");
                        itemMeta.setLore(Arrays.asList("",
                                "§7글리제581c의 아르안에게 전달해야 할 종자다"));
                        itemStack.setItemMeta(itemMeta);
                        ItemFunctions itemFunctions = new ItemFunctions();
                        itemFunctions.setQuestItem(itemStack);

                        // Swing Main hand
                        PlayerConnection conn = ((CraftPlayer)p).getHandle().b;
                        PacketPlayOutAnimation packet_2 = new PacketPlayOutAnimation(NPC, 0);
                        conn.sendPacket(packet_2);


                        p.getInventory().addItem(itemStack);
                        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);

                    }

                    detailStep++;
                    return;
                }

                UserQuestManager.Singleton().AcceptQuest(Questname, p);
                detailStep = 0;

            }
        }


        // 종자를 잃어버렸을 때
        if(QuestStep == 2 && NPCname.equals("데이즈") && !ItemManager.getinstance().checkItemFromPlayer("데이즈의 종자", p)) {
            ItemStack itemStack = new ItemStack(Material.BEETROOT_SEEDS, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§6데이즈의 종자");
            itemMeta.setLore(Arrays.asList("",
                    "§7글리제581c의 아르안에게 전달해야 할 종자다"));
            itemStack.setItemMeta(itemMeta);
            ItemFunctions itemFunctions = new ItemFunctions();
            itemFunctions.setQuestItem(itemStack);

            p.getInventory().addItem(itemStack);
            QNF.ShowScript("데이즈: 종자 잃어버리지 마셈");
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
        }


        // Progress 2
        if(QuestStep == 2 && NPCname.equals("아르안")) {

            if(!ItemManager.getinstance().checkItemFromPlayer("데이즈의 종자", p) && detailStep == 1) {
                QNF.ShowScript("아르안: 뭐? 종자가 없다고? 다시 가져와라");
                detailStep = 0;
                return;
            }

            if(!QNF.ShowScripts(Script_2, detailStep)) {

                // 종자 아이템 아르안에게 전달하기
                if(detailStep == 1) {


                    ItemStack stack = new ItemStack(Material.BEETROOT_SEEDS, 1);
                    CraftItemStack cis = CraftItemStack.asCraftCopy(stack);

                    PlayerConnection conn = ((CraftPlayer)p).getHandle().b;
                    Pair<EnumItemSlot, net.minecraft.world.item.ItemStack> pair =
                            new Pair<>(EnumItemSlot.a, CraftItemStack.asNMSCopy(cis));

                    PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(NPC.getId(), Arrays.asList(pair));
                    PacketPlayOutAnimation packet_2 = new PacketPlayOutAnimation(NPC, 0);

                    conn.sendPacket(packet);
                    conn.sendPacket(packet_2);

                    ItemManager.getinstance().removeItemFromPlayer("데이즈의 종자", p);

                    NPC.setSlot(EnumItemSlot.a, CraftItemStack.asNMSCopy(cis));
                    NPC.updateEquipment();

                }
                detailStep++;
                return;
            }

            ItemStack air = new ItemStack(Material.AIR, 1);
            CraftItemStack craftair = CraftItemStack.asCraftCopy(air);

            PlayerConnection conn = ((CraftPlayer)p).getHandle().b;
            Pair<EnumItemSlot, net.minecraft.world.item.ItemStack> pair = new Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>(EnumItemSlot.a,
                    CraftItemStack.asNMSCopy(craftair));
            PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(NPC.getId(), Arrays.asList(pair));

            conn.sendPacket(packet);

            UserQuestManager.Singleton().CompleteQuest(Questname, p);
            detailStep = 0;
        }
    }
}
