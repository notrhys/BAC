package me.rhys.bedrock;

import lombok.Getter;
import me.rhys.bedrock.base.connection.KeepaliveHandler;
import me.rhys.bedrock.base.listener.BukkitListener;
import me.rhys.bedrock.base.user.UserManager;
import me.rhys.bedrock.config.ConfigLoader;
import me.rhys.bedrock.config.ConfigValues;
import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.api.TinyProtocolHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.FloodgateAPI;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class Bedrock extends JavaPlugin {
    @Getter private static Bedrock instance;
    private UserManager userManager;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private KeepaliveHandler keepaliveHandler;
    private TinyProtocolHandler tinyProtocolHandler;
    public String bukkitVersion;
    private final ConfigValues configValues = new ConfigValues();
    private final ConfigLoader configLoader = new ConfigLoader();

    @Override
    public void onEnable() {
        instance = this;
        this.tinyProtocolHandler = new TinyProtocolHandler();

        if (ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_12_2)) {
            getServer().getPluginManager().disablePlugin(this);
            getLogger().warning("BAC is only compatible with 1.8.* to 1.12.2 spigot's");
            return;
        }

        this.configLoader.load();
        this.bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        this.keepaliveHandler = new KeepaliveHandler();
        this.userManager = new UserManager();
        getServer().getPluginManager().registerEvents(new BukkitListener(), this);
        getServer().getOnlinePlayers().stream().filter(FloodgateAPI::isBedrockPlayer)
                .forEach(player -> TinyProtocolHandler.getInstance().addChannel(player));

        //Resets violations after 1 minute
        this.executorService.scheduleAtFixedRate(() -> this.getUserManager().getUserMap().forEach((uuid, user) ->
                user.getCheckManager().getCheckList().forEach(check -> check.setViolation(0))),
                1L, 1L, TimeUnit.MINUTES);
    }

    @Override
    public void onDisable() {
        this.userManager.getUserMap().forEach((uuid, user) -> {
            TinyProtocolHandler.getInstance().removeChannel(user.getPlayer());
            user.getExecutorService().shutdownNow();
        });
        this.executorService.shutdownNow();
    }
}
