package ca.soccer1992.lavaproxy;

// 1. Missing imports added here:
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import ca.soccer1992.lavaproxy.CommandSender;

public interface Command {
  LiteralArgumentBuilder<CommandSender> create();
  String[] aliases();
}
