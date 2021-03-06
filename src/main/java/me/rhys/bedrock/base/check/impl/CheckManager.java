package me.rhys.bedrock.base.check.impl;

import lombok.Getter;
import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.checks.badpackets.BadPacketsA;
import me.rhys.bedrock.checks.combat.Reach;
import me.rhys.bedrock.checks.combat.killaura.KillauraA;
import me.rhys.bedrock.checks.combat.killaura.KillauraB;
import me.rhys.bedrock.checks.combat.velocity.VelocityA;
import me.rhys.bedrock.checks.movement.flight.FlightA;
import me.rhys.bedrock.checks.movement.flight.FlightB;
import me.rhys.bedrock.checks.movement.flight.FlightC;
import me.rhys.bedrock.checks.movement.nofall.NoFallA;
import me.rhys.bedrock.checks.movement.speed.SpeedA;
import me.rhys.bedrock.checks.movement.speed.SpeedB;

import java.util.LinkedList;
import java.util.List;

@Getter
public class CheckManager {
    private final List<Check> checkList = new LinkedList<>();

    public void setupChecks(User user) {
        this.checkList.add(new VelocityA());
        this.checkList.add(new KillauraA());
        this.checkList.add(new KillauraB());
        this.checkList.add(new FlightA());
        this.checkList.add(new FlightB());
        this.checkList.add(new FlightC());
        this.checkList.add(new NoFallA());
        this.checkList.add(new SpeedA());
        this.checkList.add(new SpeedB());
        this.checkList.add(new BadPacketsA());
        this.checkList.add(new Reach());


        this.checkList.forEach(check -> {
            check.setup();
            check.setupTimers(user);
        });
    }
}
