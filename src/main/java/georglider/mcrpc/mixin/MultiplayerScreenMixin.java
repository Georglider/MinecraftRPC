package georglider.mcrpc.mixin;

import de.jcm.discordgamesdk.activity.Activity;
import georglider.mcrpc.discord.Discord;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin {

    @Inject(at = @At("HEAD"), method = "init")
    private void init(CallbackInfo info) {
        try (var activity = new Activity()) {
            activity.setDetails("In Multiplayer Menu");
            activity.assets().setLargeImage("default");
            // Discord.getInstance().setTime(Instant.now());
            Discord.getInstance().setActivity(activity, false);
        }
    }

}
