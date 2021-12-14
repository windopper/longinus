package Party;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

        String cmdName = command.getName();

        if(cmdName.equals("party") && strings.length == 1) {
            return Arrays.asList("create",
                    "invite",
                    "join",
                    "kick",
                    "leave",
                    "promote");
        }
        else if(cmdName.equals("파티") && strings.length == 1) {
            return Arrays.asList("생성",
                    "초대",
                    "참가",
                    "추방",
                    "떠나기",
                    "승급");
        }


        return null;
    }
}
