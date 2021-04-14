package me.rhys.bedrock.base.processor.impl.processors;

import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.processor.api.Processor;
import me.rhys.bedrock.base.processor.api.ProcessorInformation;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.tinyprotocol.packet.out.WrappedOutVelocityPacket;

@ProcessorInformation(name = "Combat")
public class CombatProcessor extends Processor {

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getType().equalsIgnoreCase(Packet.Server.ENTITY_VELOCITY)) {
            WrappedOutVelocityPacket wrappedOutVelocityPacket = new WrappedOutVelocityPacket(event.getPacket(),
                    event.getUser().getPlayer());

            if (wrappedOutVelocityPacket.getId() == event.getUser().getPlayer().getEntityId()) {
                user.getActionProcessor().add(ActionProcessor.Actions.VELOCITY);
            }
        }
    }
}
