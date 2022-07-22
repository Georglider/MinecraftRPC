package georglider.mcrpc.discord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import georglider.mcrpc.util.Pair;
import net.fabricmc.loader.api.FabricLoader;

public class Discord {

    private static final Discord instance = new Discord(818863471587360788L);
    final Core core;
    final Pair<Instant, Boolean> time = new Pair<>(null, true);
    final HashMap<String, String> data = new HashMap<>();
    private Discord(Long appId) {
        try {
            Files.createDirectories(Paths.get(FabricLoader.getInstance().getGameDir() + "\\.discord"));
            File file = Optional.ofNullable(MCore.downloadDiscordLibrary
                    (Paths.get(FabricLoader.getInstance().getGameDir() + "\\.discord"))
            ).orElseThrow(DiscordLibraryNotFoundException::new);

            Core.init(file);
            var params = new CreateParams();
            params.setClientID(appId);
            params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);

            this.core = new Core(params);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Runnable helloRunnable = core::runCallbacks;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 16, TimeUnit.MILLISECONDS);
    }

    public static Discord getInstance() {
        return instance;
    }
    public Core getCore() {
        return core;
    }
    public void setActivity(Activity activity, Boolean reset) {
//        if (data.containsKey("username")) {
//            String username = data.get("username");
//            // activity.assets().setLargeImage(username, "default");
//        }

        if (reset || time.getRight()) {
            time.setLeft(Instant.now());
            time.setRight(reset);
        }

        if (data.containsKey("headUrl")) {
            activity.assets().setSmallImage(data.get("headUrl"));
            activity.assets().setSmallText(data.get("username"));
        }

        activity.timestamps().setStart(time.getLeft());

        core.activityManager().updateActivity(activity);
    }

//    public void setTime(Instant menuTime) {
//        Optional.ofNullable(menuTime).ifPresentOrElse(time -> {
//            if (this.time == null) {
//                this.time = time;
//            }
//        }, () -> this.time = Instant.now());
//    }

    public void putData(String key, String value) {
        this.data.putIfAbsent(key, value);
    }

    public HashMap<String, String> getData() {
        return data;
    }
}
