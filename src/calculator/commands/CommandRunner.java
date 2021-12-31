package calculator.commands;

public class CommandRunner {
    static boolean isCommand(String input) {
        return input != null && input.startsWith("/");
    }

    public enum CommandType {
        BLANK,
        EXIT,
        HELP
    }

    public static CommandType getCommandType(String input) {
        if (!isCommand(input)) {
            return null;
        }
        switch (input) {
            case "/exit":
                return CommandType.EXIT;
            case "/help":
                return CommandType.HELP;
            default:
                return CommandType.BLANK;
        }
    }

    private final Command command;

    public CommandRunner(CommandType commandType) {
        switch (commandType) {
            case EXIT:
                command = new Exit();
                break;
            case HELP:
                command = new Help();
                break;
            default:
                command = new Blank();
        }
    }

    public boolean run() {
        return command.run();
    }
}
