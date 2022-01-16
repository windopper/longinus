package ClassAbility.Shield;

import ClassAbility.Combination;
import PlayParticle.PlayParticle;
import PlayerManager.PlayerEnergy;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerManager;
import com.google.common.base.Enums;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import spellinteracttest.Main;

public class Shield {

    private static Shield shield;
    private Player player;
    private PlayerFunction playerFunction;
    private int CurrentMana;
    private int ManaDecrease;

    public Shield(Player player) {
        this.player = player;
        this.playerFunction = PlayerFunction.getinstance(player);
        this.CurrentMana = PlayerEnergy.getinstance(player).getEnergy();
        this.ManaDecrease = PlayerManager.getinstance(player).ManaDecrease;
    }

    private Shield() {

    }

    public static Shield getInstance() {
        if(shield == null) shield = new Shield();
        return shield;
    }

    private enum ENUM {
        RL(4, "§o§l반월참§l§o §3§l-⚡§l"),
        SHIFTR(4, "§o§l공간전이§l§o §3§l-⚡§l"),
        RR(6, "§o§lRR스킬§l§o §3§l-⚡§l"),
        FR(8, "§o§l환영소환§l§o §3§l-⚡§l");

        private int mana;
        private String title;
        private String method;

        ENUM(int mana, String title) {
            this.mana = mana;
            this.title = title;
        }

        int getMana() {
            return mana;
        }

        String getTitle() {
            return title;
        }
    }

    public void Skill(String combo) {

        if(!Enums.getIfPresent(ENUM.class, combo).isPresent()) return;

        int mana = ENUM.valueOf(combo).getMana() - ManaDecrease <= 0 ? 1 : ENUM.valueOf(combo).getMana() - ManaDecrease
                + PlayerEnergy.getinstance(player).getEnergyOverload();
        String title = ENUM.valueOf(combo).getTitle()+mana;

        if(mana <= CurrentMana) {
            PlayerEnergy.getinstance(player).removeEnergy(mana);
            if(combo.equals("SHIFTR")) {}
            if(combo.equals("RL")) {}
            if(combo.equals("FR")) FR();
            if(combo.equals("RR")) RR();


            Combination.getinstance().Sound(player);
            player.sendTitle(" ", Combination.blank+title, 5, 20, 10);
            PlayerEnergy.getinstance(player).energyOverload(combo);
        }
        else {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 1f);
            player.sendTitle(" ", Combination.blank+Combination.manaexhaustion, 0, 20, 10);
        }
    }

    public void RR() {

        Location loc = player.getEyeLocation();
        (new PlayParticle(Particle.CRIT)).CirCleHorizontalImpact1(player);

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1.5f);

    }

    public void FR() {

        Location loc = player.getLocation();

        net.minecraft.world.level.World nmsWorld = ((CraftWorld) player.getWorld()).getHandle();
        Illusion illusion = new Illusion(EntityTypes.aV, nmsWorld, player.getLocation(), player);

        for(Entity entity : player.getNearbyEntities(8, 8, 8)) {
            if(entity instanceof LivingEntity eL) {
                if(((CraftEntity) eL).getHandle() instanceof EntityInsentient nmsE) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class),
                            () -> nmsE.setGoalTarget(illusion, EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY, true), 0);
                    //nmsE.setGoalTarget(null, EntityTargetEvent.TargetReason.FORGOT_TARGET, true);
                }

            }
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class)
                , () -> illusion.setRemoved(net.minecraft.world.entity.Entity.RemovalReason.a), 200);
    }

}
