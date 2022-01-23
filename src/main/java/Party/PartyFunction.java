package Party;

import PlayerManager.PlayerPacketHandler;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import spellinteracttest.Main;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PartyFunction {

    private static PartyFunction partyfunction;
    private PartyFunction() {}

    public static PartyFunction getInstance() {
        if(partyfunction == null) partyfunction = new PartyFunction();
        return partyfunction;
    }
    public void createParty(Player player) {
        if(PartyHandler.hasParty(player)) {
            hasPartyMessage(player);
            return;
        }
        else {
            PartyHandler.getInstance(player).setUpParty();

            PlayerPacketHandler playerPacketHandler = PlayerPacketHandler.getInstance();
            PlayerPacketHandler.Handlers handlers = playerPacketHandler.createOrGetQuery(player, "party");
            handlers.setHandlers(PlayerPacketHandler.HandleType.Glowing, PlayerPacketHandler.HandleOption.ON);
            handlers.addShowTo(player);

            player.sendMessage(partyStandardMessage("파티를 성공적으로 만들었습니다"));
        }
    }

    public void quitParty(Player player) {
        if(PartyHandler.hasParty(player)) {
            PartyHandler partyHandler = PartyHandler.getInstance(player);

            UUID partyCode = partyHandler.getPartyCode();
            sendMessageToAllPartyMember(player, "§6"+player.getName()+"§e님이 파티를 나갔습니다");

            if(PartyHandler.isMaster(player) && partyHandler.getCurrentPartySize() > 1) {
                Player nextMaster = partyHandler.getPartyMembers()
                        .stream()
                        .filter(p->p!=player)
                        .toList()
                        .get(0);

                PartyHandler.getInstance(nextMaster).setMaster();
            }
            partyHandler.remove();

            PartyHandler.updatePartyPacket(partyCode);
        }
        else {
            noPartyMessage(player);
        }
    }

    public void inviteParty(Player player, Player target) {
        if(PartyHandler.hasParty(player)) {
            if(!PartyHandler.isMaster(player)) {
                reqMasterMessage(player);
                return;
            }
            if(PartyHandler.hasParty(target)) {
                alreadyHasPartyMessage(player);
                return;
            }
            PartyHandler partyHandler = PartyHandler.getInstance(player);
            partyHandler.registerRequest(target);

            target.sendMessage(partyStandardMessage("§6"+player.getName()+"가 당신을 파티에 초대했습니다"));
            TextComponent textComponent = new net.md_5.bungee.api.chat.TextComponent(partyStandardMessage("§6여기§e를 클릭하여 파티 초대를 수락할 수 있습니다. 해당 초대는 60초후 만료됩니다"));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join "+player.getName()));
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("/party join "+player.getName())));
            target.spigot().sendMessage(textComponent);

            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                if(PartyHandler.hasParty(target)) return;
                target.sendMessage(partyStandardMessage("§6"+player.getName()+"§e님이 보낸 파티 초대가 만료되었습니다"));
                partyHandler.expireRequest(target);
            }, 1200);
        }
        else {
            noPartyMessage(player);
        }
    }

    public void acceptParty(Player player, Player target) {
        HashMap<Player, List<UUID>> requests = PartyHandler.getRequests();
        if(!PartyHandler.hasParty(target)) {
            targetHasntPartyMessage(player);
            return;
        }
        if(PartyHandler.hasParty(player)) {
            hasPartyMessage(player);
            return;
        }
        if(requests.containsKey(player)) {
            List<UUID> lists = PartyHandler.getPlayerRequests(player);
            PartyHandler partyHandler = PartyHandler.getInstance(target);

            if(lists.contains(partyHandler.getPartyCode())) {
                PartyHandler.getInstance(player).setPartyCode(partyHandler.getPartyCode());

                PartyHandler.updatePartyPacket(partyHandler.getPartyCode());

                TextComponent textComponent = new TextComponent(partyStandardMessage("§6"+player.getName()+"§e님이 파티에 입장했습니다"));
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("파티식별코드: "+partyHandler.getPartyCode().toString())));
                sendMessageToAllPartyMember(player, textComponent);

                PartyHandler.getRequests().remove(player);
            }
            else {
                player.sendMessage(partyStandardMessage("해당 유저로부터 파티 초대를 받지 않았습니다"));
            }
        }
        else {
            player.sendMessage(partyStandardMessage("현재 받은 파티 초대가 없습니다"));
        }
    }

    public void kickParty(Player player, Player target) {
        if(PartyHandler.hasParty(player)) {
            if(!PartyHandler.isMaster(player)) {
                reqMasterMessage(player);
                return;
            }
            if(PartyHandler.hasParty(target)) {
                if(PartyHandler.getInstance(target).getPartyCode().equals(PartyHandler.getInstance(player).getPartyCode())) {
                    UUID partyCode = PartyHandler.getInstance(target).getPartyCode();
                    sendMessageToAllPartyMember(player, "§6"+target.getName()+"§e님이 파티에서 추방당했습니다");

                    PartyHandler.getInstance(target).remove();
                    PartyHandler.updatePartyPacket(partyCode);
                }
                else {
                    player.sendMessage(partyStandardMessage("§6"+target.getName()+"§e님은 같은 파티에 속해 있지 않습니다"));
                }
            }
        }
        else {
            noPartyMessage(player);
        }
    }

    public void promoteMaster(Player player, Player target) {
        if(PartyHandler.hasParty(player)) {
            if(!PartyHandler.isMaster(player)) {
                reqMasterMessage(player);
                return;
            }
            if(PartyHandler.hasParty(target)) {
                if(PartyHandler.getInstance(target).getPartyCode().equals(PartyHandler.getInstance(player).getPartyCode())) {
                    PartyHandler.getInstance(player).giveUpMaster();
                    PartyHandler.getInstance(target).setMaster();
                    sendMessageToAllPartyMember(player, "§6"+player.getName()+"§e님이 §6"+target.getName()+"§e님에게 파티장을 양도하였습니다");
                }
                else {
                    player.sendMessage(partyStandardMessage("§6"+target.getName()+"§e님은 같은 파티에 속해 있지 않습니다"));
                }
            }
        }
        else {
            noPartyMessage(player);
        }
    }

    public void sendPartyMessage(String s) {

    }

    private String partyStandardMessage(String content) {
        return "§5>> §e"+content;
    }
    private TextComponent partyStandardMessage(TextComponent textComponent) {
        textComponent.setText("§5>> §e"+textComponent);
        return textComponent;
    }
    private void sendMessageToAllPartyMember(Player player, String message) {
        PartyHandler partyHandler = PartyHandler.getInstance(player);
        partyHandler.getPartyMembers()
                .forEach(p->p.sendMessage(partyStandardMessage(message)));
    }
    private void sendMessageToAllPartyMember(Player player, TextComponent textComponent) {
        PartyHandler partyHandler = PartyHandler.getInstance(player);
        partyHandler.getPartyMembers()
                .forEach(p->p.spigot().sendMessage(textComponent));
    }
    private void noPartyMessage(Player player) {
        player.sendMessage(partyStandardMessage("파티에 속해있지 않습니다"));
    }
    private void hasPartyMessage(Player player) {
        player.sendMessage(partyStandardMessage("이미 파티에 속해있습니다"));
    }
    private void reqMasterMessage(Player player) {
        player.sendMessage(partyStandardMessage("파티장만이 사용할 수 있는 명령어 입니다"));
    }
    private void alreadyHasPartyMessage(Player player) {
        player.sendMessage(partyStandardMessage("해당 유저는 이미 파티에 속해있습니다"));
    }
    private void targetHasntPartyMessage(Player player) {
        player.sendMessage(partyStandardMessage("해당 유저는 현재 파티가 없습니다"));
    }
}
