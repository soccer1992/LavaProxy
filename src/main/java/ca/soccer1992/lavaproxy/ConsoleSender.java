package ca.soccer1992.lavaproxy;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;

import static ca.soccer1992.lavaproxy.utils.ComponentUtils.ansi;

public class ConsoleSender implements CommandSender{

  @Override
  public boolean executeCommand(String command) {
    String rootCmd = command.split(" ")[0];
    if (Main.getCommand(rootCmd) == null) {
      return false;
    }
    ParseResults<CommandSender> results = Main.dispatcher.parse(command, this);
    try {
      return Main.dispatcher.execute(results) == 1;
    } catch (CommandSyntaxException e) {
      sendMessage(Component.text(e.getMessage()), false);
      return true;
    }
  }

  @Override
  public void sendMessage(Component message, boolean isActionBar) {
    System.out.println(ansi.serialize(message));
  }

  @Override
  public boolean hasPermission(String permission) {
    return true; // console should always have every permission
  }
}
