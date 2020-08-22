package betterStone.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;

public class MetricsPatch {
    @SpirePatch(
            clz = VictoryScreen.class,
            method = "submitVictoryMetrics"
    )

    public static class victoryPrefixPatch{

        public static void Prefix(VictoryScreen __instance){
            customMetrics metrics = new customMetrics();

            metrics.setValues(false, true, null);
            Thread t = new Thread(metrics);
            t.setName("Metrics");
            t.start();
        }
    }

    @SpirePatch(
            clz = DeathScreen.class,
            method = "submitDefeatMetrics"
    )

    public static class defeatPatch{

        public static void Postfix(DeathScreen __instance, MonsterGroup m){
            customMetrics metrics = new customMetrics();

            metrics.setValues(true, false, m);
            Thread t = new Thread(metrics);
            t.setName("Metrics");
            t.start();
        }
    }

    @SpirePatch(
            clz = DeathScreen.class,
            method = "submitVictoryMetrics"
    )

    public static class defeatVicPrefixPatch{

        public static void Prefix(DeathScreen __instance){
            customMetrics metrics = new customMetrics();

            metrics.setValues(false, false, null);
            Thread t = new Thread(metrics);
            t.setName("Metrics");
            t.start();
        }
    }
}
