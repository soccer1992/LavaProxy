package ca.soccer1992.lavaproxy.commands;

import ca.soccer1992.lavaproxy.Main;
import ca.soccer1992.lavaproxy.Player;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import static ca.soccer1992.lavaproxy.utils.ComponentUtils.parser;

public final class ServerCommand {

  private ServerCommand() {}

  public static LiteralArgumentBuilder<Player> create() {
    return LiteralArgumentBuilder.<Player>literal("server")
            .executes(ServerCommand::usage)
            .then(RequiredArgumentBuilder.<Player, String>argument("server", StringArgumentType.word())
                    .executes(ServerCommand::transfer));
  }

  private static int usage(CommandContext<Player> ctx) {
    Player plr = ctx.getSource();
    plr.sendMessage(parser.deserialize(plr.con.fillPlaceholders("command.server.default_msg", "", plr.brand)), false);
    if (Main.servers.size() > 50){
      plr.sendMessage(parser.deserialize(plr.con.fillPlaceholders("command.server.too_many", "", plr.brand)), false);
      return 1;
    }
    ParsedCommandNode<?> rootNode = ctx.getNodes().getFirst();

    // This extracts the EXACT characters used for the command word
    String commandName = rootNode.getRange().get(ctx.getInput());
    Component serverList = Component.empty();
    int idx=0;
    for (String i : Main.servers.keySet()){
      Component serverText =
              Component.text(i);
      if (i.equalsIgnoreCase(plr.con.connectedServer.name)){
        serverText = serverText.color(NamedTextColor.GREEN);
      }
      serverText = serverText.hoverEvent(
                      HoverEvent.showText(
                              parser.deserialize(plr.con.fillPlaceholders("command.server.hover_msg", "", plr.brand, "", 0, "", i,"/" + commandName + " " + i))
                      )
              )
              .clickEvent(
                      ClickEvent.runCommand(
                              "/" + commandName + " " + i
                      )
              );

      serverList = serverList.append(
              serverText
      );
      if (idx < Main.servers.size()-1){
        serverList = serverList.append(Component.text(", "));
      }
      idx++;
    }
    plr.sendMessage(serverList, false);
    //ctx.getSource().sendMessage(Component.text("/server <name>").color(NamedTextColor.RED), false);
    return 1;
  }

  private static int transfer(CommandContext<Player> ctx) {
    String server = StringArgumentType.getString(ctx, "server");
    ctx.getSource().transferToServer(server);
    return 1;
  }
}