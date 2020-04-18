package betterStone.patches;

import betterStone.BetterStone;
import betterStone.events.BetterStoneEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.beyond.SensoryStone;
import com.megacrit.cardcrawl.helpers.EventHelper;

public class EventHelperPatch {
    @SpirePatch(
            clz = EventHelper.class,
            method = "getEvent"
    )

    public static class EventSwapPatch {
        public static AbstractEvent Postfix(AbstractEvent __result, String key){
            BetterStone.logger.info(AbstractDungeon.eventList + "\n\n");

            if (__result instanceof SensoryStone) {

                return new BetterStoneEvent();
            }
            return __result;
        }
    }
}