package betterStone.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.beyond.SensoryStone;

@SpirePatch(
        clz=AbstractDungeon.class,
        method="initializeCardPools"
)
public class RemoveEventPatch {

    public static void Prefix(AbstractDungeon dungeon_instance) {
        AbstractDungeon.eventList.remove(SensoryStone.ID);
        //BetterStone.logger.info(AbstractDungeon.eventList + "\n\n");
    }
}