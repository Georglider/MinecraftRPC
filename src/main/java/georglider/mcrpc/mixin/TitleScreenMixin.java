package georglider.mcrpc.mixin;

import de.jcm.discordgamesdk.activity.Activity;
import georglider.mcrpc.discord.Discord;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {

	@Shadow private String splashText;

	@Inject(at = @At("RETURN"), method = "init")
	private void init(CallbackInfo info) {
		Discord instance = Discord.getInstance();

		try (var activity = new Activity()) {
			activity.setDetails("In Main Menu");
			if (splashText != null) activity.setState(splashText);
			activity.assets().setLargeImage("default");
			instance.setActivity(activity, false);

			System.out.println(instance.getData().toString());
		}
	}

}
