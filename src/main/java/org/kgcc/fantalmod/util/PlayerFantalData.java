package org.kgcc.fantalmod.util;

public class PlayerFantalData {
    private int fantalPollution = 0;
    public int getFantalPollution() {
        return fantalPollution;
    }
    public void setFantalPollution(int fantalPollution) {
        if(fantalPollution < 0) {
            fantalPollution = 0;
        }
        this.fantalPollution = fantalPollution;
    }
}
