public class LavaDispatcher<S> extends CommandDispatcher<S>{
    public LiteralCommandNode<S> register(Command command) {
        LiteralCommandNode<S> mainNode = super.register(command.create());

            for (String alias : command.aliases()) {
                super.register(LiteralArgumentBuilder.<S>literal(alias).redirect(mainNode));
            }
        
        
        return mainNode;
    }
}
