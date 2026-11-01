package ca.soccer1992.lavaproxy.commands;

import ca.soccer1992.lavaproxy.CommandSender;
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

public final class EndCommand implements Command {

  public EndCommand() {}

  public LiteralArgumentBuilder<CommandSender> create() {
    return LiteralArgumentBuilder.<CommandSender>literal("end")
            .requires(sender -> sender.hasPermission("lavaproxy.command.end"))
            .executes(EndCommand::usage);
  }
  public String[] aliases(){
    return new String[]{"shutdown"};
  }
  private static int usage(CommandContext<CommandSender> ctx) {
    CommandSender sender = ctx.getSource();
    System.out.println(ansi.serialize(parser.deserialize("Shutting down")), false);
    Main.server.stop();
    return 1;
  }

}
