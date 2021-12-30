package Auction;

import NPCCreateManager.NPCManager;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AuctionNPC {

    private final static List<EntityPlayer> NPCs = new ArrayList<>();

    public void Register() {
        NPCs.add(NPCManager.getinstance().createNPC(new Location(Bukkit.getWorld("gliese581c"), -122.5, 129, 246.5)
                , "edison1304", "ewogICJ0aW1lc3RhbXAiIDogMTYxNzk1MTkyNzUzOCwKICAicHJvZmlsZUlkIiA6ICI3MzlkYzg4OThmODg0OTRmOGNkNDE4NDI4NjUxNzBkYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJlZGlzb24xMzA0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzM4MjYwNjkzNjQwYzIxYWJjZDNiYTQwYjI4N2ZmMzhiMGIxNGMyYTljM2FlMmFlOGVhYmIwZTNiNDU3YzJiMmUiCiAgICB9CiAgfQp9", "N0/34BBcnfe8x2gF8xwdhQ1fpFuU5bT1y3/uh0NsSja0UwTblI0qK0UzF7EPpye+O+ZrbVAp82DuDioC6LH/Al0dQRqUFRETgQuJRSJRNavpgikDCKE7TRqFclMELvxQ5xika0HpoR6+bI80H82+9H+4ePrhL8W9JVacCDiq9m8/TEG9SlUKsHxbg0cjXKi7xfOouk6LvIZl68PtdZlkVCmOzgTDZgX3fJ6lXjl0gSmu+afLZ7bKumoKBFWYddacwlBLIqnuxHK+byd9wb5Kg45Lle0CH2edcNxVydcPgEG9wSwf8aHJbryQQFtJMjRooZQgGBn/aFFM7hpo+CuG7w2B2kZ6YPMyTzRhJoEvJDdyeweAPssyTqTkLn32/cJ2Mot18PJHJSnekp/CFJaqIKRbGkNBkYZIzuy/IuC5noAftI41J4Ty3IumEIeLyRRD4w2Bh68pIBSwOe5rxmGrkF4USfumdejUtHo4C6AxhQ/N9kbvv6Yn/Z8+wX7srIhDuqBtYBv/31q30G/cGI0aq3MIFR2dueTNO3Oj2+4XVlp7Dpz5g2K5Cg0UhS8xdHsj38SjLJA+TWaT9fnXAkBUnNLoQ8McXUOe9WKwqleDmSszQiMBR66t3zWE17XeGvOznIMYmBfW//GN1VYQPNjUyGr3T2vPgmQF8AMV72YlLYQ="));

        for(EntityPlayer npc : NPCs) {
            npc.addScoreboardTag("Auction");
        }
    }

    public void Show(Player player) {
        for(EntityPlayer npc : NPCs) {
            NPCManager.getinstance().showTo(npc, player);
        }
    }

    public void HeadRotation() {
        for(EntityPlayer npc : NPCs) {
            NPCManager.getinstance().sendHeadRotationPacket(npc);
        }
    }

    public void Remove(Player player) {
        for(EntityPlayer npc : NPCs) {
            NPCManager.getinstance().removeNPCPacket(player, npc);
        }
    }

    public void RemoveAll() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            for(EntityPlayer npc : NPCs) {
                NPCManager.getinstance().removeNPCPacket(player, npc);
            }
        }
    }

    public EntityPlayer getNPC(int entityID) {
        for(EntityPlayer npc : NPCs) {
            if(npc.getId() == entityID) {
                return npc;
            }
        }
        return null;
    }


}
