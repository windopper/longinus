package PacketRecord;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.EntityFox;
import net.minecraft.world.level.World;
import org.bukkit.entity.Fox;

import java.util.UUID;

public class PacketSummonEntity {

    private final String entitytype;
    private final UUID uuid;
    private final World world;

    public PacketSummonEntity(String entitytype, UUID uuid, World world) {
        this.entitytype = entitytype;
        this.uuid = uuid;
        this.world = world;
    }

    public Entity getEntity() {

        if(entitytype.equals("Gliese581cMobs.FoxRat")) {
            EntityFox entityFox = new EntityFox(EntityTypes.E, world);
            entityFox.setSilent(true);
            entityFox.setCustomNameVisible(false);
            entityFox.setNoAI(false);

            Fox fox = (Fox) entityFox.getBukkitEntity();
            fox.setAI(false);
            fox.setSilent(true);
            fox.setInvulnerable(true);

            return entityFox;
            //world.addEntity(entityFox);
        }


        return null;
    }
}
