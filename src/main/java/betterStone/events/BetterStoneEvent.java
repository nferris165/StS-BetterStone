package betterStone.events;

import basemod.ReflectionHacks;
import betterStone.BetterStone;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.blue.EchoForm;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.cards.green.WraithForm;
import com.megacrit.cardcrawl.cards.purple.DevaForm;
import com.megacrit.cardcrawl.cards.red.DemonForm;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import com.megacrit.cardcrawl.screens.runHistory.TinyCard;
import com.megacrit.cardcrawl.screens.stats.RunData;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.*;


public class BetterStoneEvent extends AbstractImageEvent {

    public static final String ID = BetterStone.makeID("BetterStone");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/sensoryStone.jpg";

    private static final String INTRO_TEXT_2;
    private static final String MEMORY_1_TEXT;
    private static final String MEMORY_2_TEXT;
    private static final String MEMORY_3_TEXT;
    private static final String MEMORY_4_TEXT;
    private static final String MEMORY_DEF_TEXT;
    private CurScreen screen;
    private AbstractCard card;
    private int choice, actNum;
    private String memory;
    private ArrayList<AbstractCard> cards;


    public BetterStoneEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.screen = CurScreen.INTRO;
        this.actNum = AbstractDungeon.actNum;
        this.noCardsInRewards = true;
        classCard();
        this.choice = Math.min(this.actNum, 3);
        this.imageEventText.setDialogOption(OPTIONS[6]);
    }

    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_SENSORY");
        }

    }
    private void classCard(){
        switch (AbstractDungeon.player.chosenClass){
            case IRONCLAD:
                card = new DemonForm();
                memory = MEMORY_1_TEXT;
                break;
            case THE_SILENT:
                card = new WraithForm();
                memory = MEMORY_2_TEXT;
                break;
            case DEFECT:
                card = new EchoForm();
                memory = MEMORY_3_TEXT;
                break;
            case WATCHER:
                card = new DevaForm();
                memory = MEMORY_4_TEXT;
                break;
            default:
                card = new Madness();
                card.upgrade();
                //memory = MEMORY_DEF_TEXT;
                ArrayList<String> memories = new ArrayList<>();
                memories.add(MEMORY_1_TEXT);
                memories.add(MEMORY_2_TEXT);
                memories.add(MEMORY_3_TEXT);
                memories.add(MEMORY_4_TEXT);
                Collections.shuffle(memories, new Random(AbstractDungeon.miscRng.randomLong()));
                memory = memories.get(0);
                break;
        }
    }

    private void testRuns(){
        CardCrawlGame.mainMenuScreen.runHistoryScreen.refreshData();
        ArrayList<RunData> x = (ArrayList<RunData>) ReflectionHacks.getPrivate(
                CardCrawlGame.mainMenuScreen.runHistoryScreen, RunHistoryScreen.class, "unfilteredRuns");
        //BetterStone.logger.info(x + "\n\n");
        for(RunData run: x){
            if(run.character_chosen.equals(AbstractDungeon.player.chosenClass.name())){
                reloadCards(run);
                BetterStone.logger.info(run.master_deck + "\n");
                break;
            }
        }
    }

    private AbstractCard cardForName(RunData runData, String cardID) {
        String libraryLookupName = cardID;
        if (cardID.endsWith("+")) {
            libraryLookupName = cardID.substring(0, cardID.length() - 1);
        }

        if (libraryLookupName.equals("Defend") || libraryLookupName.equals("Strike")) {
            libraryLookupName = libraryLookupName + CardCrawlGame.mainMenuScreen.runHistoryScreen.baseCardSuffixForCharacter(runData.character_chosen);
        }

        AbstractCard card = CardLibrary.getCard(libraryLookupName);
        int upgrades = 0;
        if (card != null) {
            if (cardID.endsWith("+")) {
                upgrades = 1;
            }
        } else if (libraryLookupName.contains("+")) {
            String[] split = libraryLookupName.split("\\+", -1);
            libraryLookupName = split[0];
            upgrades = Integer.parseInt(split[1]);
            card = CardLibrary.getCard(libraryLookupName);
        }

        if (card == null) {
            BetterStone.logger.info("Could not find card named: " + cardID);
            return null;
        } else {
            card = card.makeCopy();

            for(int i = 0; i < upgrades; ++i) {
                card.upgrade();
            }
            return card;
        }
    }

    private void reloadCards(RunData runData) {
        for(String id: runData.master_deck){
            AbstractCard card;
            card = this.cardForName(runData, id);
            if (card != null) {
                this.cards.add(card);
            }
        }
    }

    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {
            case INTRO:
                this.imageEventText.updateBodyText(INTRO_TEXT_2);
                this.imageEventText.updateDialogOption(0, OPTIONS[0] + choice + OPTIONS[1]);
                if(actNum == 2) {
                    this.imageEventText.setDialogOption(OPTIONS[2] + card.name + OPTIONS[3], CardLibrary.getCopy(card.cardID));
                    this.imageEventText.setDialogOption(OPTIONS[7], true);
                } else if(actNum == 3){
                    this.imageEventText.setDialogOption(OPTIONS[2] + card.name + OPTIONS[3], CardLibrary.getCopy(card.cardID));
                    this.imageEventText.setDialogOption(OPTIONS[4]);

                } else{
                    this.imageEventText.setDialogOption(OPTIONS[7], true);
                    this.imageEventText.setDialogOption(OPTIONS[7], true);
                }
                this.screen = CurScreen.INTRO_2;
                //TODO
                testRuns();
                break;
            case INTRO_2:
                this.getRandomMemory();
                switch(buttonPressed) {
                    case 0:
                        this.reward(choice);
                        this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                        break;
                    case 1:
                        this.screen = CurScreen.ACCEPT;
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.card, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                        break;
                    case 2:
                        this.screen = CurScreen.ACCEPT;
                        this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                }

                this.imageEventText.clearRemainingOptions();
                break;
            case ACCEPT:
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                this.screen = CurScreen.LEAVE;
            default:
                this.openMap();
        }
    }

    private void getRandomMemory() {
        this.imageEventText.updateBodyText(memory);
    }

    private void reward(int num) {
        AbstractDungeon.getCurrRoom().rewards.clear();

        for(int i = 0; i < num; ++i) {
            AbstractDungeon.getCurrRoom().addCardReward(new RewardItem(AbstractCard.CardColor.COLORLESS));
        }

        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
        this.screen = CurScreen.LEAVE;
    }

    static {
        INTRO_TEXT_2 = DESCRIPTIONS[1];
        MEMORY_1_TEXT = DESCRIPTIONS[2];
        MEMORY_2_TEXT = DESCRIPTIONS[3];
        MEMORY_3_TEXT = DESCRIPTIONS[4];
        MEMORY_4_TEXT = DESCRIPTIONS[5];
        MEMORY_DEF_TEXT = DESCRIPTIONS[6];
    }

    private enum CurScreen {
        INTRO,
        INTRO_2,
        ACCEPT,
        LEAVE;

        CurScreen() {
        }
    }
}
