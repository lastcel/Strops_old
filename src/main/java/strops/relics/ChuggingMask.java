package strops.relics;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import strops.helpers.ModHelper;
import strops.utilities.IntSliderSetting;
import strops.utilities.RelicSetting;

import java.util.ArrayList;

public class ChuggingMask extends StropsAbstractRelic{
    public static final String ID = ModHelper.makePath(ChuggingMask.class.getSimpleName());
    private static final String IMG_PATH = ModHelper.makeIPath(ChuggingMask.class.getSimpleName());
    //private static final String IMG_PATH_O = ModHelper.makeOPath(FTLEngines.class.getSimpleName());
    private static final RelicTier RELIC_TIER = RelicTier.COMMON;
    private static final LandingSound LANDING_SOUND = LandingSound.FLAT;

    public static final int NUM1=2;
    public static final int TIMES_LIMIT_PER_FLOOR=5;

    public static final IntSliderSetting BONUS=new IntSliderSetting("ChuggingMask_Bonus", "N1", NUM1, 1,5);
    public static final IntSliderSetting MH=new IntSliderSetting("ChuggingMask_MH","MH",0,-20,20);
    public static final IntSliderSetting G=new IntSliderSetting("ChuggingMask_G","G",0,-100,100);
    public ArrayList<RelicSetting> BuildRelicSettings() {
        ArrayList<RelicSetting> settings = new ArrayList<>();
        settings.add(BONUS);
        settings.add(MH);
        settings.add(G);
        return settings;
    }

    public ChuggingMask() {
        super(ID, ImageMaster.loadImage(IMG_PATH), RELIC_TIER, LANDING_SOUND);
        showMHaG(MH,G);
        this.tips.add(new PowerTip(this.DESCRIPTIONS[1], this.DESCRIPTIONS[2]));
    }

    @Override
    public void onEquip(){
        onEquipMods(MH,G);
        counter=0;
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        counter=0;
    }

    @Override
    public boolean canSpawn() {
        return (Settings.isEndless || (AbstractDungeon.actNum <= 3));
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(this.DESCRIPTIONS[0], BONUS.value);
    }


    public ArrayList<String> getUpdatedDescription2() {
        ArrayList<String> str_out=new ArrayList<>();
        str_out.add(String.format(this.DESCRIPTIONS[0], BONUS.value));
        str_out.add("");
        str_out.add(getMHaG(MH,G));
        str_out.add(this.DESCRIPTIONS[1]);
        str_out.add(this.DESCRIPTIONS[2]);
        return str_out;
    }
}
