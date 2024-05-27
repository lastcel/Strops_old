package strops.relics;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.BodySlam;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import strops.helpers.ModHelper;
import strops.utilities.IntSliderSetting;
import strops.utilities.RelicSetting;

import java.util.ArrayList;

public class Fractal extends StropsAbstractRelic {
    public static final String ID = ModHelper.makePath(Fractal.class.getSimpleName());
    private static final String IMG_PATH = ModHelper.makeIPath(Fractal.class.getSimpleName());
    private static final String IMG_PATH_O = ModHelper.makeOPath(Fractal.class.getSimpleName());
    private static final RelicTier RELIC_TIER = RelicTier.SHOP;
    private static final LandingSound LANDING_SOUND = LandingSound.SOLID;

    public static int NUM1=3;

    public static final IntSliderSetting DURATION=new IntSliderSetting("Fractal_Duration", "N1", NUM1, 1, 10);
    public static final IntSliderSetting MH=new IntSliderSetting("Fractal_MH","MH",0,-20,20);
    public static final IntSliderSetting G=new IntSliderSetting("Fractal_G","G",0,-100,100);
    public ArrayList<RelicSetting> BuildRelicSettings() {
        ArrayList<RelicSetting> settings = new ArrayList<>();
        settings.add(DURATION);
        settings.add(MH);
        settings.add(G);
        return settings;
    }

    public Fractal() {
        super(ID, ImageMaster.loadImage(IMG_PATH), ImageMaster.loadImage(IMG_PATH_O), RELIC_TIER, LANDING_SOUND);
        showMHaG(MH,G);
        AbstractCard c=new BodySlam();
        c.upgrade();
        this.cardToPreview=c;
    }

    AbstractCard BodySlamAdded;

    public void atBattleStartPreDraw(){
        counter=0;
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        flash();
        addToBot(new MakeTempCardInHandAction(BodySlamAdded=new BodySlam(), 1, false));
        BodySlamAdded.upgrade();
    }

    @Override
    public void atTurnStart()
    {
        counter++;
        if((counter>=2)&&(counter<=DURATION.value)){
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            flash();
            addToBot(new MakeTempCardInHandAction(BodySlamAdded=new BodySlam(), 1, false));
            BodySlamAdded.upgrade();
        }
    }

    @Override
    public void onEquip(){
        onEquipMods(MH,G);
    }

    public void onVictory() {
        counter = -1;
    }

    public String getUpdatedDescription() {
        return String.format(this.DESCRIPTIONS[0], DURATION.value);
    }

    public ArrayList<String> getUpdatedDescription2() {
        ArrayList<String> str_out=new ArrayList<>();
        str_out.add(String.format(this.DESCRIPTIONS[0], DURATION.value));
        str_out.add("");
        str_out.add(getMHaG(MH,G));
        return str_out;
    }
}
