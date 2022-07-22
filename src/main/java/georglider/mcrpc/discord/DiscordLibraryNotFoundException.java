package georglider.mcrpc.discord;

public class DiscordLibraryNotFoundException extends Error {

    public DiscordLibraryNotFoundException() {
        super("Discord library not found");
    }
}
