package me.rhys.bedrock;

import lombok.Getter;
import me.rhys.bedrock.tinyprotocol.api.TinyProtocolHandler;
import me.rhys.bedrock.util.BlockUtil;
import me.rhys.bedrock.util.box.BlockBoxManager;
import me.rhys.bedrock.base.connection.KeepaliveHandler;
import me.rhys.bedrock.base.listener.BukkitListener;
import me.rhys.bedrock.base.user.UserManager;
import me.rhys.bedrock.util.box.impl.BoundingBoxes;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
    private BoundingBoxes boundingBoxes;
    private BlockBoxManager blockBoxManager;
    public String bukkitVersion;

    @Override
    public void onEnable() {
        instance = this;

        this.tinyProtocolHandler = new TinyProtocolHandler();
        new BlockUtil();
        this.boundingBoxes = new BoundingBoxes();
        this.blockBoxManager = new BlockBoxManager();
        this.bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        this.keepaliveHandler = new KeepaliveHandler();
        this.userManager = new UserManager();
        getServer().getPluginManager().registerEvents(new BukkitListener(), this);

        //Resets violations after 1 minute
        this.executorService.scheduleAtFixedRate(() -> this.getUserManager().getUserMap().forEach((uuid, user) ->
                user.getCheckManager().getCheckList().forEach(check -> check.setViolation(0))),
                1L, 1L, TimeUnit.MINUTES);
    }

    @Override
    public void onDisable() {
        this.userManager.getUserMap().forEach((uuid, user) ->
                user.getExecutorService().shutdownNow());
        this.executorService.shutdownNow();
    }
}
