package me.rhys.bedrock;

import cc.funkemunky.api.Atlas;
import lombok.Getter;
import me.rhys.bedrock.base.connection.KeepaliveHandler;
import me.rhys.bedrock.base.listener.BukkitListener;
import me.rhys.bedrock.base.listener.PacketListener;
import me.rhys.bedrock.base.user.UserManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Bedrock extends JavaPlugin {
    @Getter private static Bedrock instance;
    private UserManager userManager;

    @Override
    public void onEnable() {
        instance = this;
        this.userManager = new UserManager();
        getServer().getPluginManager().registerEvents(new BukkitListener(), this);
        Atlas.getInstance().getEventManager().registerListeners(new PacketListener(), this);
        new KeepaliveHandler();
    }

    @Override
    public void onDisable() {
        this.userManager.getUserMap().forEach((uuid, user) ->
                user.getExecutorService().shutdownNow());
        Atlas.getInstance().getEventManager().unregisterAll(this);
    }
}
