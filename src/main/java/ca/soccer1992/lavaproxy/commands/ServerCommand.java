package ca.soccer1992.lavaproxy.commands;

import ca.soccer1992.lavaproxy.Player;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class ServerCommand {

  private ServerCommand() {}

  public static LiteralArgumentBuilder<Player> create() {
    return LiteralArgumentBuilder.<Player>literal("server")
            .executes(ServerCommand::usage)
            .then(RequiredArgumentBuilder.<Player, String>argument("server", StringArgumentType.word())
                    .executes(ServerCommand::transfer));
  }

  private static int usage(CommandContext<Player> ctx) {
    ctx.getSource().sendMessage(Component.text("/server <name>").color(NamedTextColor.RED), false);
    return 1;
  }

  private static int transfer(CommandContext<Player> ctx) {
    String server = StringArgumentType.getString(ctx, "server");
    ctx.getSource().transferToServer(server);
    return 1;
  }
}