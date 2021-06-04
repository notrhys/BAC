package me.rhys.bedrock.util;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class PastLocation {
    private final List<PlayerLocation> previousLocations = new LinkedList<>();

    @SuppressWarnings({"ForLoopReplaceableByForEach"})
    public List<PlayerLocation> getEstimatedLocation(long time, long delta, long currentMS) {
        long prevTimeStamp = currentMS - time;

        //Better on performance than stream.
        List<PlayerLocation> copy = new LinkedList<>();

        int size = this.previousLocations.size();
        for (int i = 0; i < size; i++) {
            PlayerLocation customLocation = this.previousLocations.get(i);
            if (Math.abs(prevTimeStamp - customLocation.getTime()) < delta) {
                copy.add(customLocation);
            }
        }

        return copy;
    }

    public void addLocation(PlayerLocation location) {
        if (previousLocations.size() >= 20) {
            previousLocations.remove(0);
        }

        previousLocations.add(location.clone());
    }
}