package me.rhys.bedrock.base.connection;

import lombok.Getter;
import me.rhys.bedrock.Bedrock;
import me.rhys.bedrock.tinyprotocol.packet.out.WrappedOutKeepAlivePacket;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

@Getter
public class KeepaliveHandler implements Runnable {
    public KeepaliveHandler() {
        this.start();
    }

    private long time = 999L;
    private BukkitTask bukkitTask;

    public void start() {
        if (this.bukkitTask == null) {
            this.bukkitTask = Bukkit.getScheduler().runTaskTimer(Bedrock.getInstance(),
                    this, 0L, 0L);
        }
    }

    @Override
    public void run() {
        if (this.time-- < 1) {
            this.time = 999L;
        }

        /*
            Why use keepalives you say? transactions (COF) don't exist on the bedrock client
            in fact keepalives are called ping packets in bedrock
         */
        WrappedOutKeepAlivePacket wrappedOutKeepAlivePacket = new WrappedOutKeepAlivePacket(this.time);
        Bedrock.getInstance().getUserManager().getUserMap().forEach((uuid, user) -> {
            user.getConnectionMap().put(this.time, System.currentTimeMillis());
        //    user.sendPacket(wrappedOutKeepAlivePacket.getObject());
        });
    }
}
