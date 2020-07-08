package betterStone.events;

import basemod.ReflectionHacks;
import betterStone.BetterStone;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.blue.EchoForm;
import com.megacrit.cardcrawl.cards.blue.GeneticAlgorithm;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.cards.colorless.RitualDagger;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.cards.green.WraithForm;
import com.megacrit.cardcrawl.cards.purple.DevaForm;
import com.megacrit.cardcrawl.cards.red.DemonForm;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
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
    private String eventChoice;
    private static int healthLoss;
    private CurScreen screen;
    private AbstractCard card, obtainCard = null;
    private int choice, actNum;
    private String memory, randMem;
    private boolean pickCard;
    private ArrayList<AbstractCard> cards;
    public boolean remCard = false;
    private List<String> cardsObtained;

    public BetterStoneEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.screen = CurScreen.INTRO;
        this.actNum = AbstractDungeon.actNum;
        this.noCardsInRewards = true;
        this.cardsObtained = new ArrayList<>();
        classCard();
        this.choice = Math.min(this.actNum, 2);
        this.cards = new ArrayList<>();
        this.pickCard = false;
        this.imageEventText.setDialogOption(OPTIONS[6]);

        if(AbstractDungeon.ascensionLevel >= 15){
            healthLoss = (int) (0.25F * AbstractDungeon.player.maxHealth);
        }
        else{
            healthLoss = (int) (0.2F * AbstractDungeon.player.maxHealth);
        }
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
                getRandomMemory(1);
                break;
            case THE_SILENT:
                card = new WraithForm();
                memory = MEMORY_2_TEXT;
                getRandomMemory(2);
                break;
            case DEFECT:
                card = new EchoForm();
                memory = MEMORY_3_TEXT;
                getRandomMemory(3);
                break;
            case WATCHER:
                card = new DevaForm();
                memory = MEMORY_4_TEXT;
                getRandomMemory(4);
                break;
            default:
                card = new Madness();
                card.upgrade();
                memory = MEMORY_DEF_TEXT;
                getRandomMemory(0);
                break;
        }
    }

    private void getRunInfo(){
        CardCrawlGame.mainMenuScreen.runHistoryScreen.refreshData();
        ArrayList<RunData> runList = (ArrayList<RunData>) ReflectionHacks.getPrivate(
                CardCrawlGame.mainMenuScreen.runHistoryScreen, RunHistoryScreen.class, "unfilteredRuns");
        for(RunData run: runList){
            if(run.character_chosen.equals(AbstractDungeon.player.chosenClass.name())){
                for(String id: run.master_deck){
                    AbstractCard card;
                    card = this.cardForName(run.character_chosen, id);
                    if (card != null) {
                        this.cards.add(card);
                    }
                }
                break;
            }
        }

        //If no previous run, obtain Madness
        if(this.cards.isEmpty()){
            for(int i = 0; i < 20; i++){
                this.cards.add(new Madness());
            }
        }
    }

    private AbstractCard cardForName(String charClass, String cardID) {
        String libraryLookupName = cardID;
        if (cardID.endsWith("+")) {
            libraryLookupName = cardID.substring(0, cardID.length() - 1);
        }

        if (libraryLookupName.equals("Defend") || libraryLookupName.equals("Strike")) {
            libraryLookupName = libraryLookupName + CardCrawlGame.mainMenuScreen.runHistoryScreen.baseCardSuffixForCharacter(charClass);
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

    private void initializeObtainCard() {
        this.obtainCard = CardLibrary.getCard(CardCrawlGame.playerPref.getString("NOTE_CARD", "None"));
        if (this.obtainCard == null) {
            return;
        }

        this.obtainCard = this.obtainCard.makeCopy();

        for(int i = 0; i < CardCrawlGame.playerPref.getInteger("NOTE_UPGRADE", 0); ++i) {
            this.obtainCard.upgrade();
        }

        this.obtainCard.misc = CardCrawlGame.playerPref.getInteger("NOTE_MISC", 0);
        if(this.obtainCard instanceof RitualDagger){
            this.obtainCard.applyPowers();
            this.obtainCard.baseDamage = this.obtainCard.misc;
            this.obtainCard.isDamageModified = false;
        } else if(this.obtainCard instanceof GeneticAlgorithm){
            this.obtainCard.applyPowers();
            this.obtainCard.isBlockModified = false;
        }
    }

    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {
            case INTRO:
                this.imageEventText.updateBodyText(INTRO_TEXT_2);
                this.imageEventText.updateDialogOption(0, OPTIONS[0] + choice + OPTIONS[1]);
                if(actNum == 2) {
                    this.imageEventText.setDialogOption(OPTIONS[2] + card.name + OPTIONS[3]
                            + healthLoss + OPTIONS[9], CardLibrary.getCopy(card.cardID));
                    this.imageEventText.setDialogOption(OPTIONS[7], true);
                } else if(actNum == 3){
                    this.imageEventText.setDialogOption(OPTIONS[2] + card.name + OPTIONS[3]
                            + healthLoss + OPTIONS[9], CardLibrary.getCopy(card.cardID));
                    this.imageEventText.setDialogOption(OPTIONS[4], CardLibrary.getCopy(AscendersBane.ID));

                } else{
                    this.imageEventText.setDialogOption(OPTIONS[7], true);
                    this.imageEventText.setDialogOption(OPTIONS[7], true);
                }
                if(BetterStone.hasBetterNote && (AbstractDungeon.ascensionLevel < 15 || BetterStone.disableAscLimit)){
                    initializeObtainCard();
                    if(obtainCard != null) {
                        this.imageEventText.setDialogOption(OPTIONS[11] + obtainCard.name + OPTIONS[12], obtainCard);
                    }
                    else{
                        this.imageEventText.setDialogOption(OPTIONS[10], true);
                    }
                }
                this.screen = CurScreen.INTRO_2;
                break;
            case INTRO_2:
                switch(buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(randMem);
                        this.eventChoice = "1";
                        this.reward(choice);
                        logMetricHeal(ID, this.eventChoice, choice);
                        this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                        break;
                    case 1:
                        this.imageEventText.updateBodyText(memory);
                        this.eventChoice = "2";
                        this.screen = CurScreen.ACCEPT;
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.card, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, healthLoss, DamageInfo.DamageType.HP_LOSS));
                        logMetricTakeDamage(ID, this.eventChoice, healthLoss);
                        this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                        break;
                    case 2:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[7]);
                        this.eventChoice = "3";
                        this.screen = CurScreen.ACCEPT;
                        this.pickCard = true;
                        getRunInfo();
                        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        for(AbstractCard card: cards){
                            group.addToTop(card);
                        }
                        AbstractDungeon.gridSelectScreen.open(group, 1, OPTIONS[8], false);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new AscendersBane(),
                                (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        logMetricObtainCards(ID, this.eventChoice, cardsObtained);
                        this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                        break;
                    case 3:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[8]);
                        this.eventChoice = "4";
                        this.cardsObtained.add(obtainCard.cardID);
                        logMetricObtainCards(ID, this.eventChoice, cardsObtained);
                        this.screen = CurScreen.ACCEPT;
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(obtainCard,
                                (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        this.remCard = true;
                        this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                        break;
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

    private void reward(int num) {
        AbstractDungeon.getCurrRoom().rewards.clear();

        for(int i = 0; i < num; ++i) {
            AbstractDungeon.getCurrRoom().addCardReward(new RewardItem(AbstractCard.CardColor.COLORLESS));
        }

        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
        this.screen = CurScreen.LEAVE;
    }

    private void getRandomMemory(int avoid) {
        ArrayList<String> memories = new ArrayList<>();
        if(avoid != 1){
            memories.add(MEMORY_1_TEXT);

        }
        if(avoid != 2){
            memories.add(MEMORY_2_TEXT);

        }
        if(avoid != 3){
            memories.add(MEMORY_3_TEXT);

        }
        if(avoid != 4){
            memories.add(MEMORY_4_TEXT);

        }
        Collections.shuffle(memories, new Random(AbstractDungeon.miscRng.randomLong()));
        this.randMem = memories.get(0);
    }

    @Override
    public void update() {
        super.update();
        if (this.pickCard && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = (AbstractDungeon.gridSelectScreen.selectedCards.get(0)).makeCopy();
            this.cardsObtained.add(c.cardID);
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
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
