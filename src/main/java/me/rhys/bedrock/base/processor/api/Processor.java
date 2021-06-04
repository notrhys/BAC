package me.rhys.bedrock.base.processor.api;

import lombok.Getter;
import me.rhys.bedrock.base.event.CallableEvent;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import org.bukkit.event.entity.EntityDamageEvent;

@Getter
public class Processor implements CallableEvent, ProcessorInterface {
    public User user;

    public void setup(User user) {
        this.user = user;
        this.setupTimers(user);
    }

    @Override
    public void onPacket(PacketEvent event) {
        //
    }

    @Override
    public void setupTimers(User user) {
        //
    }

    @Override
    public void onConnection(User user) {
        //
    }
}
