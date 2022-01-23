package Party;

import PacketRecord.Play;
import PlayerManager.PlayerPacketHandler;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PartyHandler {

    private static HashMap<Player, PartyHandler> instances = new HashMap<>();
    private static HashMap<Player, List<UUID>> partyInviteRequest = new HashMap<>();

    private Player player;
    private UUID partyCode;
    private boolean isMaster = false;

    private PartyHandler(Player player) {
        this.player = player;
    }

    public static PartyHandler getInstance(Player player) {
        if(!instances.containsKey(player)) instances.put(player, new PartyHandler(player));
        return instances.get(player);
    }

    public void remove() {
        instances.remove(player);
        PlayerPacketHandler.getInstance().removeQuery(player, "party");
    }

    public static boolean hasParty(Player player) {
        return instances.containsKey(player);
    }

    public static boolean isMaster(Player player) {
        if(!hasParty(player)) return false;
        return instances.get(player).isMaster;
    }

    public void setUpParty() {
        partyCode = UUID.randomUUID();
        isMaster = true;

        PlayerPacketHandler playerPacketHandler = PlayerPacketHandler.getInstance();
        PlayerPacketHandler.Handlers handlers = playerPacketHandler.createOrGetQuery(player, "party");
        handlers.setHandlers(PlayerPacketHandler.HandleType.Glowing, PlayerPacketHandler.HandleOption.ON);
        handlers.addShowTo(player);
    }

    public void setMaster() {
        isMaster = true;
    }

    public void giveUpMaster() {
        isMaster = false;
    }

    public void setPartyCode(UUID partyCode) {
        this.partyCode = partyCode;
    }

    public List<Player> getPartyMembers() {
        return instances
                .keySet()
                .stream()
                .filter((i)-> partyCode.equals(instances.get(i).partyCode))
                .toList();
    }

    public static List<Player> getPartyMembers(UUID partyCode) {
        return instances
                .keySet()
                .stream()
                .filter((i)-> partyCode.equals(instances.get(i).partyCode))
                .toList();
    }

    public int getCurrentPartySize() {
        return getPartyMembers().size();
    }

    public void registerRequest(Player target) {
        if(partyInviteRequest.containsKey(target)) {
            partyInviteRequest.get(target).add(partyCode);
        }
        else {
            List<UUID> list = new ArrayList<>();
            list.add(partyCode);
            partyInviteRequest.put(target, list);
        }
    }

    public void expireRequest(Player target) {
        if(partyInviteRequest.containsKey(target)) {
            if(partyInviteRequest.get(target).size()>1) {
                partyInviteRequest.get(target).remove(partyCode);
            }
            else {
                partyInviteRequest.remove(player);
            }
        }
    }
    public static HashMap<Player, List<UUID>> getRequests() {
        return partyInviteRequest;
    }
    public static List<UUID> getPlayerRequests(Player player) {
        return getRequests().get(player);
    }
    public UUID getPartyCode() {
        return partyCode;
    }

    public static void updatePartyPacket(UUID partyCode) {
        List<Player> members = getPartyMembers(partyCode);

        PlayerPacketHandler playerPacketHandler = PlayerPacketHandler.getInstance();

        for(Player member : members) {
            PlayerPacketHandler.Handlers handlers = playerPacketHandler.createOrGetQuery(member, "party");
            handlers.setHandlers(PlayerPacketHandler.HandleType.Glowing, PlayerPacketHandler.HandleOption.ON);
            handlers.updateShowTo(members);
        }
    }
}
