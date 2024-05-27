package strops.relics;

import basemod.abstracts.CustomBottleRelic;
import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.mod.stslib.relics.OnAfterUseCardRelic;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import strops.actions.ExcludeAction;
import strops.helpers.ModHelper;
import strops.patch.PatchTranscendence;
import strops.utilities.IntSliderSetting;
import strops.utilities.RelicSetting;

import java.util.ArrayList;
import java.util.function.Predicate;

public class Transcendence extends StropsAbstractRelic implements OnAfterUseCardRelic,
        CustomBottleRelic, CustomSavable<Integer> {
    public static final String ID = ModHelper.makePath(Transcendence.class.getSimpleName());
    private static final String IMG_PATH = ModHelper.makeIPath(Transcendence.class.getSimpleName());
    private static final RelicTier RELIC_TIER = RelicTier.SHOP;
    private static final LandingSound LANDING_SOUND = LandingSound.SOLID;

    public static final int NUM1=2;

    public static final IntSliderSetting THRESHOLD=new IntSliderSetting("Transcendence_Threshold", "N1", NUM1, 1,10);
    public static final IntSliderSetting MH=new IntSliderSetting("Transcendence_MH","MH",0,-20,20);
    public static final IntSliderSetting G=new IntSliderSetting("Transcendence_G","G",0,-100,100);
    public ArrayList<RelicSetting> BuildRelicSettings() {
        ArrayList<RelicSetting> settings = new ArrayList<>();
        settings.add(THRESHOLD);
        settings.add(MH);
        settings.add(G);
        return settings;
    }

    public Transcendence() {
        super(ID, ImageMaster.loadImage(IMG_PATH), RELIC_TIER, LANDING_SOUND);
        showMHaG(MH,G);
    }

    private boolean cardSelected = true;
    public static AbstractCard card = null;

    @Override
    public String getUpdatedDescription() {
        return String.format(this.DESCRIPTIONS[0], THRESHOLD.value);
    }

    @Override
    public Predicate<AbstractCard> isOnCard() {
        return PatchTranscendence.PatchTool1.inTranscendence::get;
    }

    @Override
    public Integer onSave() {
        if (card != null) {
            return AbstractDungeon.player.masterDeck.group.indexOf(card);
        } else {
            return -1;
        }
    }
    @Override
    public void onLoad(Integer cardIndex) {
        if (cardIndex == null) {
            return;
        }
        if (cardIndex >= 0 && cardIndex < AbstractDungeon.player.masterDeck.group.size()) {
            card = AbstractDungeon.player.masterDeck.group.get(cardIndex);
            if (card != null) {
                PatchTranscendence.PatchTool1.inTranscendence.set(card, true);
                setDescriptionAfterLoading();
            }
        }
    }

    @Override
    public void onEquip() { // 1. When we acquire the relic
        onEquipMods(MH,G);
        cardSelected = false;
        CardGroup group = CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck);
        if (group.isEmpty()) {
            cardSelected = true;
        } else {
            if (AbstractDungeon.isScreenUp) {
                AbstractDungeon.dynamicBanner.hide();
                AbstractDungeon.overlayMenu.cancelButton.hide();
                AbstractDungeon.previousScreen = AbstractDungeon.screen;
            }
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
            AbstractDungeon.gridSelectScreen.open(group, 1, DESCRIPTIONS[1] + name + LocalizedStrings.PERIOD, false, false, false, false);
        }
    }

    @Override
    public void onUnequip() { // 1. On unequip
        if (card != null) {
            AbstractCard cardInDeck = AbstractDungeon.player.masterDeck.getSpecificCard(card);
            if (cardInDeck != null) {
                PatchTranscendence.PatchTool1.inTranscendence.set(cardInDeck, false);
            }
        }
    }

    @Override
    public void update() {
        super.update();

        if (!cardSelected && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            cardSelected = true;
            card = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            PatchTranscendence.PatchTool1.inTranscendence.set(card, true);
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            setDescriptionAfterLoading();
        }
    }

    @Override
    public boolean canSpawn() {
        return !CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck).isEmpty();
    }

    @Override
    public void atTurnStart(){
        counter=0;
    }

    @Override
    public void onAfterUseCard(AbstractCard cardIn, UseCardAction action){
        if((counter<=THRESHOLD.value-1)&&(PatchTranscendence.PatchTool1.inTranscendence.get(cardIn))){
            if (AbstractDungeon.player.getRelic(ID) != this){
                return;
            }
            flash();
            int handsize = AbstractDungeon.player.hand.size();
            for (int i = 0; i < handsize; i++) {
                addToTop(new ExcludeAction(1, true, true, false, Settings.ACTION_DUR_XFAST));
            }
        }
        counter++;
    }

    public void setDescriptionAfterLoading() {
        description = String.format(DESCRIPTIONS[2] + FontHelper.colorString(card.name, "y") + DESCRIPTIONS[3],THRESHOLD.value);
        tips.clear();
        tips.add(new PowerTip(name, description));
        initializeTips();
        tips.subList(1, tips.size()).clear(); // remove keyword tips from words in the card's name
    }

    public ArrayList<String> getUpdatedDescription2() {
        ArrayList<String> str_out=new ArrayList<>();
        str_out.add(String.format(this.DESCRIPTIONS[0], THRESHOLD.value));
        str_out.add("");
        str_out.add(getMHaG(MH,G));
        return str_out;
    }

    @Override
    public void updateDesc() {
        ArrayList<String> s=getUpdatedDescription2();

        this.description = s.get(0);
        this.tips = new ArrayList();
        this.tips.add(new PowerTip(this.name, this.description));

        for(int i=1;i<s.size();i+=2)
        {
            this.tips.add(new PowerTip(s.get(i),s.get(i+1)));
        }

        initializeTips();
    }

    public AbstractRelic makeCopy() {
        return new Transcendence();
    }
}
