package georglider.mcrpc;

import georglider.mcrpc.discord.Discord;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//penis
public class Main implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("mcrpc");

	@Override
	public void onInitialize() {
		LOGGER.info("MCRPC Started!");

		Discord instance = Discord.getInstance();
		MinecraftClient minecraft = MinecraftClient.getInstance();
		instance.putData("uuid", minecraft.getSession().getUuid());
		instance.putData("headUrl" , "https://customrp.xyz/p/?u=https://crafatar.com/avatars/"
				+ minecraft.getSession().getUuid() + "?size=512&default=MHF_Steve&overlay");
		instance.putData("username", minecraft.getSession().getUsername());
	}
}
