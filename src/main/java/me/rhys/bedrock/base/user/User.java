package me.rhys.bedrock.base.user;

import lombok.Getter;
import lombok.Setter;
import me.rhys.bedrock.base.check.impl.CheckManager;
import me.rhys.bedrock.base.event.EventManager;
import me.rhys.bedrock.base.processor.impl.ProcessorManager;
import me.rhys.bedrock.base.processor.impl.processors.*;
import me.rhys.bedrock.base.user.objects.BlockData;
import me.rhys.bedrock.tinyprotocol.api.TinyProtocolHandler;
import me.rhys.bedrock.util.PlayerLocation;
import me.rhys.bedrock.util.box.BoundingBox;
import me.rhys.bedrock.util.evicting.EvictingMap;
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
    private final ExecutorService executorService;
    private final BlockData blockData = new BlockData();

    private ProcessorManager processorManager;
    private MovementProcessor movementProcessor;
    private ConnectionProcessor connectionProcessor;
    private ActionProcessor actionProcessor;
    private PotionProcessor potionProcessor;
    private CombatProcessor combatProcessor;
    private ElytraProcessor elytraProcessor;

    private final Map<Long, Long> connectionMap = new EvictingMap<>(100);
    private int tick;

    private BoundingBox boundingBox = new BoundingBox(0f, 0f, 0f, 0f, 0f, 0f);
    private PlayerLocation currentLocation = new PlayerLocation(null, 0, 0, 0, 0, 0,
            false);
    private PlayerLocation lastLocation = currentLocation;
    public int cancelAttackTicks;

    public User(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.checkManager.setupChecks(this);
        this.eventManager = new EventManager(this);
        this.processorManager = new ProcessorManager(this);
        this.processorManager.setup();
        this.setupProcessors();
        this.blockData.setupTimers(this);
    }

    private void setupProcessors() {
        this.connectionProcessor = (ConnectionProcessor) this.processorManager.forClass(ConnectionProcessor.class);
        this.movementProcessor = (MovementProcessor) this.processorManager.forClass(MovementProcessor.class);
        this.actionProcessor = (ActionProcessor) this.processorManager.forClass(ActionProcessor.class);
        this.potionProcessor = (PotionProcessor) this.processorManager.forClass(PotionProcessor.class);
        this.combatProcessor = (CombatProcessor) this.processorManager.forClass(CombatProcessor.class);
        this.elytraProcessor = (ElytraProcessor) this.processorManager.forClass(ElytraProcessor.class);
    }

    public void sendPacket(Object packet) {
        TinyProtocolHandler.sendPacket(this.player, packet);
    }

    public boolean shouldCancel() {
        return this.player.getAllowFlight() || this.player.isFlying()
                || this.player.getGameMode() == GameMode.CREATIVE || this.player.getGameMode() == GameMode.CREATIVE;
    }
}
