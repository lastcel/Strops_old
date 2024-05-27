package strops.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import strops.helpers.ModHelper;
import strops.utilities.IntSliderSetting;
import strops.utilities.RelicSetting;

import java.util.ArrayList;

public class Zan extends StropsAbstractRelic implements ClickableRelic {
    public static final String ID = ModHelper.makePath(Zan.class.getSimpleName());
    private static final String IMG_PATH = ModHelper.makeIPath(Zan.class.getSimpleName());
    private static final String IMG_PATH_O = ModHelper.makeOPath(Zan.class.getSimpleName());
    private static final RelicTier RELIC_TIER = RelicTier.UNCOMMON;
    private static final LandingSound LANDING_SOUND = LandingSound.SOLID;

    public static final int NUM1=10;
    public static final int NUM2=20;
    public static final int NUM3=30;
    public static final int NUM4=20;
    public static final int BLIZZARD_BONUS=70;

    public static final IntSliderSetting COMMON_BONUS=new IntSliderSetting("Zan_Common_Bonus", "N1", NUM1, 6,15);
    public static final IntSliderSetting UNCOMMON_BONUS=new IntSliderSetting("Zan_Uncommon_Bonus", "N2", NUM2, 6,30);
    public static final IntSliderSetting RARE_BONUS=new IntSliderSetting("Zan_Rare_Bonus", "N3", NUM3, 6,45);
    public static final IntSliderSetting STORAGE=new IntSliderSetting("Zan_Storage", "N4", NUM4, 0,45);
    public static final IntSliderSetting MH=new IntSliderSetting("ChuggingMask_MH","MH",0,-20,20);
    public static final IntSliderSetting G=new IntSliderSetting("ChuggingMask_G","G",0,-100,100);
    public ArrayList<RelicSetting> BuildRelicSettings() {
        ArrayList<RelicSetting> settings = new ArrayList<>();
        settings.add(COMMON_BONUS);
        settings.add(UNCOMMON_BONUS);
        settings.add(RARE_BONUS);
        settings.add(STORAGE);
        settings.add(MH);
        settings.add(G);
        return settings;
    }

    public Zan() {
        super(ID, ImageMaster.loadImage(IMG_PATH), ImageMaster.loadImage(IMG_PATH_O), RELIC_TIER, LANDING_SOUND);
        showMHaG(MH,G);
        this.tips.add(new PowerTip(this.DESCRIPTIONS[1], this.DESCRIPTIONS[2]));
    }

    @Override
    public void onEquip(){
        onEquipMods(MH,G);
        counter=(STORAGE.value==0)?-1:0;
    }

    @Override
    public void onRightClick(){
        if(counter>0&&AbstractDungeon.getCurrRoom().phase!=AbstractRoom.RoomPhase.COMBAT){
            AbstractDungeon.player.heal(counter,true);
            counter=0;
        }
    }

    @Override
    public boolean canSpawn() {
        return (Settings.isEndless || (AbstractDungeon.actNum <= 3));
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(this.DESCRIPTIONS[0], COMMON_BONUS.value,
                UNCOMMON_BONUS.value, RARE_BONUS.value, STORAGE.value);
    }


    public ArrayList<String> getUpdatedDescription2() {
        ArrayList<String> str_out=new ArrayList<>();
        str_out.add(String.format(this.DESCRIPTIONS[0], COMMON_BONUS.value,
                UNCOMMON_BONUS.value, RARE_BONUS.value, STORAGE.value));
        str_out.add("");
        str_out.add(getMHaG(MH,G));
        str_out.add(this.DESCRIPTIONS[1]);
        str_out.add(this.DESCRIPTIONS[2]);
        return str_out;
    }

    public AbstractRelic makeCopy() {return new Zan();}
}
