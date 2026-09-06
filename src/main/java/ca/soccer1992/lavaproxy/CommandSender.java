package ca.soccer1992.lavaproxy;

import net.kyori.adventure.text.Component;

public interface CommandSender {
  boolean executeCommand(String command);
  void sendMessage(Component message, boolean isActionBar);
  boolean hasPermission(String permission);

}
