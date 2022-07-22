package georglider.mcrpc.mixin;

import de.jcm.discordgamesdk.activity.Activity;
import georglider.mcrpc.Main;
import georglider.mcrpc.discord.Discord;
import georglider.mcrpc.discord.KnownServers;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.networking.client.ClientPlayNetworkAddon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Mixin(ClientPlayNetworkAddon.class)
public abstract class ServerJoinMixin implements ClientPlayConnectionEvents.Join, ClientPlayConnectionEvents.Disconnect {

    @Shadow @Final private MinecraftClient client;
    private final Random r = new Random();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        Main.LOGGER.info("D " + Objects.requireNonNull(client.getServer()).getServerIp());
    }

    @Override
    public void onPlayReady(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        Main.LOGGER.info("C " + Objects.requireNonNull(client.getServer()).getServerIp());
    }

    @Inject(method = "onServerReady", at = @At("HEAD"), remap = false)
    protected void onClientReady(CallbackInfo info) {
        try (var activity = new Activity()) {
            Discord instance = Discord.getInstance();
            if (this.client.getCurrentServerEntry() != null) {
                String address = Objects.requireNonNull(this.client.getCurrentServerEntry()).address;
                address = address.replace(":25565", "");
                activity.setDetails("Playing on " + address);

                // instance.setTime(null);

                List<String> collect = Arrays.stream(address.split("\\."))
                        .filter(s -> !s.equals("mc"))
                        .filter(s -> !s.equals("play"))
                        .map(String::toLowerCase).collect(Collectors.toList());
                collect.remove(collect.size() - 1);

                for (String serverName : collect) {
                    if (knownServer(serverName)) {
                        activity.assets().setLargeImage(serverName);
                        break;
                    }
                }

                if (activity.assets().getLargeImage().isBlank()) {
                    HttpRequest request = HttpRequest.newBuilder()
                            .GET()
                            .uri(URI.create("https://eu.mc-api.net/v3/server/favicon/" + address))
                            .build();
                    CompletableFuture<HttpResponse<String>> iconFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                    int iconStatus = iconFuture.get().statusCode();
                    if (iconStatus == 200) {
                        activity.assets().setLargeImage("https://customrp.xyz/p/?u=https://eu.mc-api.net/v3/server/favicon/" + address);
                    } else {
                        if (r.nextInt(100) == 99) {
                            activity.assets().setLargeImage("packpngdraw");
                        } else {
                            activity.assets().setLargeImage("packpng");
                        }
                    }
                }
                instance.putData("ping", String.valueOf(this.client.getCurrentServerEntry().ping));
                instance.putData("serverName", this.client.getCurrentServerEntry().name);
                instance.putData("serverIp", this.client.getCurrentServerEntry().address);
            } else {
                activity.assets().setLargeImage(instance.getData().get("headUrl"));
                activity.assets().setLargeText(instance.getData().get("username"));
                activity.assets().setSmallImage("default");
                instance.putData("ping", String.valueOf(0));
                instance.putData("serverIp", "localhost");
                instance.putData("serverName", Objects.requireNonNull(this.client.getServer()).getName());
            }
            // instance.putData("serverMotd", Objects.requireNonNull(this.client.getServer()).getServerMotd());
            instance.setActivity(activity, true);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        Main.LOGGER.info(info.toString());
    }

    private Boolean knownServer(String serverName) {
        for (KnownServers c : KnownServers.values()) {
            if (c.name().equals(serverName)) {
                return true;
            }
        }
        return false;
    }

}
