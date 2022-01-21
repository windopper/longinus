package Items.ModuleChip;

import PlayerManager.PlayerManager;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import utils.CoolDownAbilityHandler;
import utils.DuraAbilityHandler;
import PlayerManager.PlayerEnergy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ModuleChipAbilities {

    Player player;
    PlayerManager pm;

    public ModuleChipAbilities(Player player) {

        this.player = player;
        this.pm = PlayerManager.getinstance(player);
    }

    public void invokeChipAbility() {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsStack.getTag();
        try {
            int[] chips = tag.getIntArray("chips");
            List<ModuleChipList> list = Arrays.stream(ModuleChipList.values())
                    .filter((a) -> Arrays.stream(chips).anyMatch((b) -> b == a.getCode())).toList();
            for(ModuleChipList chip : list) {
                try {
                    Class c = this.getClass();
                    Method method = c.getMethod(chip.name());
                    method.invoke(this);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void NULL() {

    }

    public void 테스트_칩() {

    }
    public void 프로토타입() {
        Bukkit.broadcastMessage("hi");
    }

    public void 고성능수냉쿨러() {
        CoolDownAbilityHandler cah = CoolDownAbilityHandler.getHandler(player, "고성능 수냉 쿨러");
        PlayerEnergy pe = PlayerEnergy.getinstance(player);
        cah.setAbility(()->pe.setEnergyOverload(0))
                .setCoolDown(200)
                .setOnUse(() -> {
                    ModuleChipMessage.getInstance().moduleUseMessage(player, "§e고성능 수냉 쿨러§6를 사용하였습니다. 스킬 과부하가 제거됩니다");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                })
                .run();
    }

    public void 과충전() {

    }

    public void 긴급탈출모듈() {

    }

    public void 에너지환원모듈() {

    }

    public void 동력전환모듈() {

    }
}
