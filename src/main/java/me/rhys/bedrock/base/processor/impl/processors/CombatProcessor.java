package me.rhys.bedrock.base.processor.impl.processors;

import lombok.Getter;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.processor.api.Processor;
import me.rhys.bedrock.base.processor.api.ProcessorInformation;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.tinyprotocol.packet.out.WrappedOutVelocityPacket;
import me.rhys.bedrock.util.EventTimer;

@ProcessorInformation(name = "Combat")
@Getter
public class CombatProcessor extends Processor {

    private EventTimer preVelocityTimer;

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getType().equalsIgnoreCase(Packet.Server.ENTITY_VELOCITY)) {
            WrappedOutVelocityPacket wrappedOutVelocityPacket = new WrappedOutVelocityPacket(event.getPacket(),
                    event.getUser().getPlayer());

            if (wrappedOutVelocityPacket.getId() == event.getUser().getPlayer().getEntityId()) {
                this.preVelocityTimer.reset();
                user.getActionProcessor().add(ActionProcessor.Actions.VELOCITY);
            }
        }
    }

    @Override
    public void setupTimers(User user) {
        this.preVelocityTimer = new EventTimer(20, user);
    }
}
