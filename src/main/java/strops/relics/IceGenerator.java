package strops.relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import strops.cards.GetBlizzard;
import strops.cards.HealToFull;
import strops.helpers.ModHelper;
import strops.utilities.IntSliderSetting;
import strops.utilities.RelicSetting;

import java.util.ArrayList;

public class IceGenerator extends StropsAbstractRelic{
    public static final String ID = ModHelper.makePath(IceGenerator.class.getSimpleName());
    private static final String IMG_PATH = ModHelper.makeIPath(IceGenerator.class.getSimpleName());
    private static final String IMG_PATH_O = ModHelper.makeOPath(IceGenerator.class.getSimpleName());
    private static final RelicTier RELIC_TIER = RelicTier.RARE;
    private static final LandingSound LANDING_SOUND = LandingSound.SOLID;

    public static final int NUM1=10;

    public static final IntSliderSetting PENALTY=new IntSliderSetting("IceGenerator_Penalty", "N1", NUM1, 20);
    public static final IntSliderSetting MH=new IntSliderSetting("IceGenerator_MH","MH",0,-20,20);
    public static final IntSliderSetting G=new IntSliderSetting("IceGenerator_G","G",0,-100,100);
    public ArrayList<RelicSetting> BuildRelicSettings() {
        ArrayList<RelicSetting> settings = new ArrayList<>();
        settings.add(PENALTY);
        settings.add(MH);
        settings.add(G);
        return settings;
    }

    public IceGenerator() {
        super(ID, ImageMaster.loadImage(IMG_PATH), ImageMaster.loadImage(IMG_PATH_O), RELIC_TIER, LANDING_SOUND);
        showMHaG(MH,G);
        this.tips.add(new PowerTip(this.DESCRIPTIONS[1], this.DESCRIPTIONS[2]));
    }

    @Override
    public void onEquip(){
        onEquipMods(MH,G);
        //logger.info("当前幕数="+AbstractDungeon.actNum+"，层数="+AbstractDungeon.floorNum);
        InputHelper.moveCursorToNeutralPosition();
        ArrayList<AbstractCard> iceGenChoices = new ArrayList<>();
        iceGenChoices.add(new HealToFull());
        iceGenChoices.add(new GetBlizzard());
        if(AbstractDungeon.isScreenUp){
            AbstractDungeon.dynamicBanner.hide();
            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        AbstractDungeon.getCurrRoom().phase=AbstractRoom.RoomPhase.INCOMPLETE;
        AbstractDungeon.cardRewardScreen.chooseOneOpen(iceGenChoices);
    }

    @Override
    public boolean canSpawn() {
        return ((Settings.isEndless || (AbstractDungeon.actNum <= 3))&&(AbstractDungeon.floorNum>=1));
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(this.DESCRIPTIONS[0], PENALTY.value);
    }


    public ArrayList<String> getUpdatedDescription2() {
        ArrayList<String> str_out=new ArrayList<>();
        str_out.add(String.format(this.DESCRIPTIONS[0], PENALTY.value));
        str_out.add("");
        str_out.add(getMHaG(MH,G));
        str_out.add(this.DESCRIPTIONS[1]);
        str_out.add(this.DESCRIPTIONS[2]);
        return str_out;
    }

    public AbstractRelic makeCopy() {return new IceGenerator();}
}
