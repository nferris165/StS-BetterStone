package betterStone.patches;

import betterStone.events.BetterStoneEvent;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.beyond.SensoryStone;
import com.megacrit.cardcrawl.random.Random;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class EventSelectionPatch {
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getEvent"
    )

    public static class EventOdds {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<AbstractEvent> Insert(Random __rng, @ByRef String[] ___tmpKey) {
            Float roll, odds;

            if(AbstractDungeon.actNum == 1){
                if(___tmpKey[0].equals(SensoryStone.ID) || ___tmpKey[0].equals(BetterStoneEvent.ID)){
                    roll = AbstractDungeon.eventRng.random(0.0F, 1.0F);
                    odds = 0.4F;
                    //EventTweaks.logger.info(roll +" / "+ odds + " \n ");
                    if(roll < odds){
                        return SpireReturn.Return(AbstractDungeon.getEvent(__rng));
                    }
                }
            }

            return SpireReturn.Continue();
        }
    }

    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "remove");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }
}

