package me.rhys.bedrock.base.user;

import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.utils.BoundingBox;
import lombok.Getter;
import lombok.Setter;
import me.rhys.bedrock.base.check.impl.CheckManager;
import me.rhys.bedrock.base.event.EventManager;
import me.rhys.bedrock.base.packet.PacketHandler;
import me.rhys.bedrock.base.processor.ConnectionProcessor;
import me.rhys.bedrock.base.processor.MovementProcessor;
import me.rhys.bedrock.base.user.objects.BlockData;
import me.rhys.bedrock.util.EvictingMap;
import me.rhys.bedrock.util.PlayerLocation;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter @Setter
public class User {
    private final Player player;
    private final UUID uuid;
    private final CheckManager checkManager = new CheckManager();
    private final EventManager eventManager;
    private final PacketHandler packetHandler;
    private final ExecutorService executorService;
    private final BlockData blockData = new BlockData();

    private ConnectionProcessor connectionProcessor;
    private MovementProcessor movementProcessor;

    private int tick;
    private BoundingBox boundingBox = new BoundingBox(0f, 0f, 0f, 0f, 0f, 0f);

    private final Map<Long, Long> connectionMap = new EvictingMap<>(100);

    private PlayerLocation currentLocation = new PlayerLocation(null, 0, 0, 0, 0, 0,
            false);
    private PlayerLocation lastLocation = currentLocation;

    private boolean alerts;

    public User(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.checkManager.setupChecks();
        this.packetHandler = new PacketHandler(this);
        this.eventManager = new EventManager(this);
        this.setupProcessors();
        this.alerts = player.isOp() || player.hasPermission("anticheat.alerts");
    }

    void setupProcessors() {
        this.connectionProcessor = new ConnectionProcessor(this);
        this.movementProcessor = new MovementProcessor(this);
    }

    public void sendPacket(Object packet) {
        TinyProtocolHandler.sendPacket(this.player, packet);
    }

    public boolean shouldCancel() {
        return this.player.getAllowFlight() || this.player.isFlying()
                || this.player.getGameMode() == GameMode.CREATIVE || this.player.getGameMode() == GameMode.CREATIVE;
    }
}
