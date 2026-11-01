public interface Command {
  LiteralArgumentBuilder<CommandSender> create();
  String[] aliases();
}
