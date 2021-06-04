package me.rhys.bedrock.util;

import me.rhys.bedrock.base.user.User;

public class EventTimer {
    private int tick;
    private final int max;
    private final User user;

    public EventTimer(int max, User user) {
        this.tick = 0;
        this.max = max;
        this.user = user;
        this.reset();
    }

    public boolean hasNotPassed() {
        return (this.user.getTick() > this.max && (this.user.getTick() - tick) < this.max);
    }

    public boolean passed() {
        return (this.user.getTick() > this.max && (this.user.getTick() - tick) > this.max);
    }

    public boolean hasNotPassed(int ctick) {
        return (this.user.getTick() > this.max && (this.user.getTick() - tick) < this.max);
    }

    public boolean passed(int ctick) {
        return (this.user.getTick() > this.max && (this.user.getTick() - tick) > this.max);
    }

    public void reset() {
        this.tick = this.user.getTick();
    }
}
