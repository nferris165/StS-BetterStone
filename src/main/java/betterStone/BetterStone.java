package betterStone;

import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.ReflectionHacks;
import basemod.interfaces.*;
import betterStone.events.BetterStoneEvent;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.audio.Sfx;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.*;

import basemod.BaseMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import betterStone.util.TextureLoader;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;

@SuppressWarnings("unused")

@SpireInitializer
public class BetterStone implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        EditCharactersSubscriber,
        PostInitializeSubscriber {

    public static final Logger logger = LogManager.getLogger(BetterStone.class.getName());

    //mod settings
    public static Properties defaultSettings = new Properties();
    public static final String ascension_limit_settings = "ascensionLimit";
    public static final String act_limit_settings = "actLimit";
    public static boolean disableAscLimit = false;
    public static boolean actLimit = false;

    public static final boolean hasBetterNote;

    private static final String MODNAME = "Better Stone";
    private static final String AUTHOR = "Nichilas";
    private static final String DESCRIPTION = "A mod to make the Sensory Stone event better";

    private static final String BADGE_IMAGE = "betterStoneResources/images/Badge.png";

    private static final String AUDIO_PATH = "betterStoneResources/audio/";

    private static final String modID = "betterStone";


    //Image Directories
    public static String makeCardPath(String resourcePath) {
        return modID + "Resources/images/cards/" + resourcePath;
    }

    public static String makeEventPath(String resourcePath) {
        return modID + "Resources/images/events/" + resourcePath;
    }

    public static String makeMonsterPath(String resourcePath) {
        return modID + "Resources/images/monsters/" + resourcePath;
    }

    public static String makeOrbPath(String resourcePath) {
        return modID + "Resources/images/orbs/" + resourcePath;
    }

    public static String makePowerPath(String resourcePath) {
        return modID + "Resources/images/powers/" + resourcePath;
    }

    public static String makeRelicPath(String resourcePath) {
        return modID + "Resources/images/relics/" + resourcePath;
    }

    public static String makeRelicOutlinePath(String resourcePath) {
        return modID + "Resources/images/relics/outline/" + resourcePath;
    }

    public static String makeUIPath(String resourcePath) {
        return modID + "Resources/images/ui/" + resourcePath;
    }

    public static String makeVfxPath(String resourcePath) {
        return modID + "Resources/images/vfx/" + resourcePath;
    }


    public BetterStone() {
        BaseMod.subscribe(this);

        logger.info("Adding mod settings");
        defaultSettings.setProperty(ascension_limit_settings, "FALSE");
        defaultSettings.setProperty(act_limit_settings, "FALSE");
        try {
            SpireConfig config = new SpireConfig("betterStone", "betterStoneConfig", defaultSettings);
            config.load();
            disableAscLimit = config.getBool(ascension_limit_settings);
            actLimit = config.getBool(act_limit_settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static
    {
        hasBetterNote = Loader.isModLoaded("betterNote");
        if (hasBetterNote) {
            logger.info("Detected Better Note");
        }
    }

    public static void initialize() {
        BetterStone betterStone = new BetterStone();
    }

    public void receiveEditPotions() {
        //BaseMod.addPotion(NewPotion.class, SLUMBERING_POTION_RUST, SLUMBERING_TEAL, SLUMBERING_POTION_RUST, NewPotion.POTION_ID, TheSlumbering.Enums.THE_SLUMBERING);
    }

    @Override
    public void receiveEditCards() {

    }

    @Override
    public void receiveEditCharacters() {
        receiveEditPotions();
    }

    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String json = Gdx.files.internal(modID + "Resources/localization/eng/Keyword-Strings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(modID.toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    @Override
    public void receiveEditRelics() {

    }
    private static String getLanguageString() {
        switch (Settings.language) {
            case ZHS:
                return "zhs";
            default:
                return "eng";
        }
    }

    @Override
    public void receiveEditStrings() {
        // Get Localization
        String language = getLanguageString();

        BaseMod.loadCustomStringsFile(CardStrings.class,
                modID + "Resources/localization/" + language + "/Card-Strings.json");
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                modID + "Resources/localization/" + language + "/Character-Strings.json");
        BaseMod.loadCustomStringsFile(EventStrings.class,
                modID + "Resources/localization/" + language + "/Event-Strings.json");
        BaseMod.loadCustomStringsFile(MonsterStrings.class,
                modID + "Resources/localization/" + language + "/Monster-Strings.json");
        BaseMod.loadCustomStringsFile(OrbStrings.class,
                modID + "Resources/localization/" + language + "/Orb-Strings.json");
        BaseMod.loadCustomStringsFile(PotionStrings.class,
                modID + "Resources/localization/" + language + "/Potion-Strings.json");
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                modID + "Resources/localization/" + language + "/Power-Strings.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                modID + "Resources/localization/" + language + "/Relic-Strings.json");
        BaseMod.loadCustomStringsFile(UIStrings.class,
                modID + "Resources/localization/" + language + "/UI-Strings.json");
    }

    private void loadAudio() {
        HashMap<String, Sfx> map = ReflectionHacks.getPrivate(CardCrawlGame.sound, SoundMaster.class, "map");
        //map.put("Pop", new Sfx(AUDIO_PATH + "pop.ogg", false));
    }

    public static String makeID(String idText) {
        return modID + ":" + idText;
    }

    @Override
    public void receivePostInitialize() {
        UIStrings configStrings = CardCrawlGame.languagePack.getUIString(makeID("ConfigMenuText"));
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        ModPanel settingsPanel = new ModPanel();

        ModLabeledToggleButton ascLimitButton = new ModLabeledToggleButton(configStrings.TEXT[0],
                350.0f, 750.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                disableAscLimit,
                settingsPanel,
                (label) -> {},
                (button) -> {

                    disableAscLimit = button.enabled;
                    try {
                        SpireConfig config = new SpireConfig("betterStone", "betterStoneConfig", defaultSettings);
                        config.setBool(ascension_limit_settings, disableAscLimit);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        ModLabeledToggleButton actLimitButton = new ModLabeledToggleButton(configStrings.TEXT[1],
                350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                actLimit,
                settingsPanel,
                (label) -> {},
                (button) -> {

                    actLimit = button.enabled;
                    try {
                        SpireConfig config = new SpireConfig("betterStone", "betterStoneConfig", defaultSettings);
                        config.setBool(act_limit_settings, actLimit);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        settingsPanel.addUIElement(ascLimitButton);
        settingsPanel.addUIElement(actLimitButton);
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        //events
        if(BetterStone.actLimit){
            BaseMod.addEvent(BetterStoneEvent.ID, BetterStoneEvent.class, TheBeyond.ID);
        } else {
            BaseMod.addEvent(BetterStoneEvent.ID, BetterStoneEvent.class);
        }

        //audio
        loadAudio();
    }
}