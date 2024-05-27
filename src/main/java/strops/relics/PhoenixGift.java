package strops.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import strops.helpers.ModHelper;
import strops.utilities.IntSliderSetting;
import strops.utilities.RelicSetting;

import java.util.ArrayList;

public class PhoenixGift extends StropsAbstractRelic {
    public static final String ID = ModHelper.makePath(PhoenixGift.class.getSimpleName());
    private static final String IMG_PATH = ModHelper.makeIPath(PhoenixGift.class.getSimpleName());
    private static final String IMG_PATH_O = ModHelper.makeOPath(PhoenixGift.class.getSimpleName());
    private static final RelicTier RELIC_TIER = RelicTier.COMMON;
    private static final LandingSound LANDING_SOUND = LandingSound.SOLID;

    public boolean isEnabled = false;
    //public boolean isWaiting = false;

    public static final int NUM1=35;

    public static final IntSliderSetting THRESHOLD=new IntSliderSetting("PhoenixGift_THRESHOLD", "N1", NUM1, 10,50);
    public static final IntSliderSetting MH=new IntSliderSetting("PhoenixGift_MH","MH",0,-20,20);
    public static final IntSliderSetting G=new IntSliderSetting("PhoenixGift_G","G",0,-100,100);
    public ArrayList<RelicSetting> BuildRelicSettings() {
        ArrayList<RelicSetting> settings = new ArrayList<>();
        settings.add(THRESHOLD);
        settings.add(MH);
        settings.add(G);
        return settings;
    }

    public PhoenixGift() {
        super(ID, ImageMaster.loadImage(IMG_PATH), ImageMaster.loadImage(IMG_PATH_O), RELIC_TIER, LANDING_SOUND);
        showMHaG(MH,G);
        this.tips.add(new PowerTip(this.DESCRIPTIONS[1], this.DESCRIPTIONS[2]));
    }

    @Override
    public void onEquip(){
        onEquipMods(MH,G);
        counter=THRESHOLD.value;
    }

    @Override
    public void atBattleStart(){
        if(counter==0&&!isAct3Boss()){
            AbstractDungeon.getCurrRoom().addRelicToRewards(RelicTier.RARE);
            setCounter(-2);
        }
    }

    @Override
    public void setCounter(int setCounter) {
        this.counter=setCounter;
        if (setCounter == -2) {
            usedUp();
            this.counter = -2;
        }
    }

    @Override
    public void atTurnStart(){
        isEnabled=false;
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(this.DESCRIPTIONS[0], THRESHOLD.value);
    }


    public ArrayList<String> getUpdatedDescription2() {
        ArrayList<String> str_out=new ArrayList<>();
        str_out.add(String.format(this.DESCRIPTIONS[0], THRESHOLD.value));
        str_out.add("");
        str_out.add(getMHaG(MH,G));
        str_out.add(this.DESCRIPTIONS[1]);
        str_out.add(this.DESCRIPTIONS[2]);
        return str_out;
    }

    public void setDescriptionAfterDeath() {
        String cry=DESCRIPTIONS[5];
        tips.clear();
        tips.add(new PowerTip(cry, description));
        showMHaG(MH,G);
        tips.add(new PowerTip(DESCRIPTIONS[1], DESCRIPTIONS[2]));
        initializeTips();
    }

    public AbstractRelic makeCopy() {return new PhoenixGift();}

    static boolean isAct3Boss() {
        return AbstractDungeon.actNum == 3 && AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss;
    }
}
