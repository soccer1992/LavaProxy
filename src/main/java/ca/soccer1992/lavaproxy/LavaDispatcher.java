package ca.soccer1992.lavaproxy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import ca.soccer1992.lavaproxy.commands.Command;

public class LavaDispatcher extends CommandDispatcher<CommandSender> {
    
    public LiteralCommandNode<CommandSender> register(Command command) {
        LiteralCommandNode<CommandSender> mainNode = super.register(command.create());

        for (String alias : command.aliases()) {
            super.register(LiteralArgumentBuilder.<CommandSender>literal(alias).redirect(mainNode));
        }
        
        return mainNode;
    }
}
