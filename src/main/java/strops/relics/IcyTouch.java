package strops.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.orbs.Frost;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import strops.helpers.ModHelper;
import strops.utilities.IntSliderSetting;
import strops.utilities.RelicSetting;

import java.util.ArrayList;

public class IcyTouch extends StropsAbstractRelic {
    public static final String ID = ModHelper.makePath(IcyTouch.class.getSimpleName());
    private static final String IMG_PATH = ModHelper.makeIPath(IcyTouch.class.getSimpleName());
    private static final RelicTier RELIC_TIER = RelicTier.UNCOMMON;
    private static final LandingSound LANDING_SOUND = LandingSound.SOLID;

    public static final int NUM1=3,NUM2=1;

    public static final IntSliderSetting THRESHOLD= new IntSliderSetting("IcyTouch_Threshold", "N1", NUM1, 1,5);
    public static final IntSliderSetting BONUS= new IntSliderSetting("IcyTouch_Bonus", "N2", NUM2, 1,3);
    public static final IntSliderSetting MH=new IntSliderSetting("IcyTouch_MH","MH",0,-20,20);
    public static final IntSliderSetting G=new IntSliderSetting("IcyTouch_G","G",0,-100,100);
    public ArrayList<RelicSetting> BuildRelicSettings() {
        ArrayList<RelicSetting> settings = new ArrayList<>();
        settings.add(THRESHOLD);
        settings.add(BONUS);
        settings.add(MH);
        settings.add(G);
        return settings;
    }

    public IcyTouch() {
        super(ID, ImageMaster.loadImage(IMG_PATH), RELIC_TIER, LANDING_SOUND);
        showMHaG(MH,G);
    }

    @Override
    public void onEquip(){
        onEquipMods(MH,G);
    }

    @Override
    public void atTurnStart() {
        this.counter = 0;
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.ATTACK) {
            this.counter++;
            if (this.counter % THRESHOLD.value == 0) {
                flash();
                this.counter = 0;
                addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
                for(int ctr=1;ctr<= BONUS.value;ctr++){
                    addToBot(new ChannelAction(new Frost()));
                }
            }
        }
    }

    public void onVictory() {
        this.counter = -1;
    }

    public String getUpdatedDescription() {
        return String.format(this.DESCRIPTIONS[0], THRESHOLD.value, BONUS.value);
    }

    public ArrayList<String> getUpdatedDescription2() {
        ArrayList<String> str_out=new ArrayList<>();
        str_out.add(String.format(this.DESCRIPTIONS[0], THRESHOLD.value, BONUS.value));
        str_out.add("");
        str_out.add(getMHaG(MH,G));
        return str_out;
    }

    public AbstractRelic makeCopy() {
        return new IcyTouch();
    }
}
