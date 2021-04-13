package me.rhys.bedrock.base.connection;

import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutKeepAlivePacket;
import me.rhys.bedrock.Bedrock;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class KeepaliveHandler implements Runnable {
    public KeepaliveHandler() {
        this.start();
    }

    private long time = 90000L;
    private BukkitTask bukkitTask;

    public void start() {
        if (this.bukkitTask == null) {
            this.bukkitTask = Bukkit.getScheduler().runTaskTimer(Bedrock.getInstance(), this, 0L, 0L);
        }
    }

    @Override
    public void run() {
        if (this.time-- < 1) {
            this.time = 90000L;
        }

        WrappedOutKeepAlivePacket wrappedOutKeepAlivePacket = new WrappedOutKeepAlivePacket(this.time);
        Bedrock.getInstance().getUserManager().getUserMap().forEach((uuid, user) -> {
            user.getConnectionMap().put(this.time, System.currentTimeMillis());
            user.sendPacket(wrappedOutKeepAlivePacket.getObject());
        });
    }
}
