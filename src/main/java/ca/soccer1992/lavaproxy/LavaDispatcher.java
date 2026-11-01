package ca.soccer1992.lavaproxy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class LavaDispatcher<S> extends CommandDispatcher<S> {
    
    public LiteralCommandNode<S> register(Command command) {
        LiteralCommandNode<S> mainNode = super.register(command.create());

        for (String alias : command.aliases()) {
            super.register(LiteralArgumentBuilder.<S>literal(alias).redirect(mainNode));
        }
        
        return mainNode;
    }
}
