package bplusdevelopment.modules.hud.stats;

import bplusdevelopment.utils.StatsUtils;
import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.hud.modules.DoubleTextHudElement;

public class Deaths extends DoubleTextHudElement {
    public Deaths(HUD hud) {
        super(hud, "Deaths", "Displays your total death count", "Deaths: ");
    }

    @Override
    protected String getRight() {
        return String.valueOf(StatsUtils.deaths);
    }
}
