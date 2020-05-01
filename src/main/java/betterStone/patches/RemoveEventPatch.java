package betterStone.patches;

import basemod.BaseMod;
import betterStone.BetterStone;
import betterStone.events.BetterStoneEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.events.beyond.SensoryStone;

@SpirePatch(
        clz=AbstractDungeon.class,
        method="initializeCardPools"
)
public class RemoveEventPatch {

    public static void Prefix(AbstractDungeon dungeon_instance) {
        AbstractDungeon.eventList.remove(SensoryStone.ID);
        if(BetterStone.actLimit && AbstractDungeon.eventList.contains(BetterStoneEvent.ID)){
            AbstractDungeon.eventList.remove(BetterStoneEvent.ID);
            BaseMod.addEvent(BetterStoneEvent.ID, BetterStoneEvent.class, TheBeyond.ID);
        }
        //BetterStone.logger.info(AbstractDungeon.eventList + "\n\n");
    }
}