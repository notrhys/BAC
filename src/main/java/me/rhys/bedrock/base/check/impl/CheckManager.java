package me.rhys.bedrock.base.check.impl;

import lombok.Getter;
import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.checks.movement.flight.FlightA;
import me.rhys.bedrock.checks.movement.flight.FlightB;
import me.rhys.bedrock.checks.movement.nofall.NoFallA;

import java.util.LinkedList;
import java.util.List;

@Getter
public class CheckManager {
    private final List<Check> checkList = new LinkedList<>();

    public void setupChecks() {
        this.checkList.add(new FlightA());
        this.checkList.add(new FlightB());
        this.checkList.add(new NoFallA());

        this.checkList.forEach(Check::setup);
    }
}
