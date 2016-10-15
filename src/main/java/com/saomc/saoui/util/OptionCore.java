package com.saomc.saoui.util;

import com.saomc.saoui.GLCore;
import com.saomc.saoui.events.ConfigHandler;
import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
public enum OptionCore {

    //Main Categories
    VANILLA_OPTIONS(I18n.format("guiOptions"), false, false, null, false),
    UI(I18n.format("optCatUI"), false, true, null, false),
    THEME(I18n.format("optTheme"), false, true, null, false),
    HEALTH_OPTIONS(I18n.format("optCatHealth"), false, true, null, false),
    HOTBAR_OPTIONS(I18n.format("optCatHotBar"), false, true, null, false),
    EFFECTS(I18n.format("optCatEffects"), false, true, null, false),
    MISC(I18n.format("optCatMisc"), false, true, null, false),
    //General UI Settings
    UI_ONLY(I18n.format("optionUIOnly"), false, false, UI, false),
    DEFAULT_INVENTORY(I18n.format("optionDefaultInv"), true, false, UI, false),
    DEFAULT_DEATH_SCREEN(I18n.format("optionDefaultDeath"), false, false, UI, false),
    DEFAULT_DEBUG(I18n.format("optionDefaultDebug"), false, false, UI, false),
    FORCE_HUD(I18n.format("optionForceHud"), false, false, UI, false),
    LOGOUT(I18n.format("optionLogout"), false, false, UI, false),
    GUI_PAUSE(I18n.format("optionGuiPause"), true, false, UI, false),
    // Themes
    VANILLA_UI(I18n.format("optionDefaultUI"), false, false, THEME, true),
    ALO_UI(I18n.format("optionALOUI"), false, false, THEME, true),
    SAO_UI(I18n.format("optionSAOUI"), true, false, THEME, true),
    // Health Options
    SMOOTH_HEALTH(I18n.format("optionSmoothHealth"), true, false, HEALTH_OPTIONS, false),
    HEALTH_BARS(I18n.format("optionHealthBars"), true, false, HEALTH_OPTIONS, false),
    REMOVE_HPXP(I18n.format("optionLightHud"), false, false, HEALTH_OPTIONS, false),
    //DEFAULT_HEALTH(I18n.format("optionDefaultHealth"), false, false, HEALTH_OPTIONS, false),
    ALT_ABSORB_POS(I18n.format("optionAltAbsorbPos"), false, false, HEALTH_OPTIONS, false),
    //Hotbar
    DEFAULT_HOTBAR(I18n.format("optionDefaultHotbar"), false, false, HOTBAR_OPTIONS, true),
    HOR_HOTBAR(I18n.format("optionHorHotbar"), false, false, HOTBAR_OPTIONS, true),
    VER_HOTBAR(I18n.format("optionVerHotbar"), true, false, HOTBAR_OPTIONS, true),
    //Effects
    CURSOR_TOGGLE(I18n.format("optionCursorToggle"), true, false, EFFECTS, false),
    COLOR_CURSOR(I18n.format("optionColorCursor"), true, false, EFFECTS, false),
    SPINNING_CRYSTALS(I18n.format("optionSpinning"), true, false, EFFECTS, false),
    PARTICLES(I18n.format("optionParticles"), true, false, EFFECTS, false),
    LESS_VISUALS(I18n.format("optionLessVis"), false, false, EFFECTS, false),
    SOUND_EFFECTS(I18n.format("optionSounds"), true, false, EFFECTS, false),
    //Misc
    CROSS_HAIR(I18n.format("optionCrossHair"), false, false, MISC, false),
    AGGRO_SYSTEM(I18n.format("optionAggro"), true, false, MISC, false),
    CLIENT_CHAT_PACKETS(I18n.format("optionCliChatPacks"), true, false, MISC, false),
    MOUNT_STAT_VIEW(I18n.format("optionMountStatView"), true, false, MISC, false),
    CUSTOM_FONT(I18n.format("optionCustomFont"), false, false, MISC, false),
    DEBUG_MODE(I18n.format("optionDebugMode"), false, false, MISC, false),
    COMPACT_INVENTORY(I18n.format("optionCompatInv"), false, false, MISC, false),
    TEXT_SHADOW(I18n.format("optionTextShadow"), true, false, MISC, false),
    //Debug
    DISABLE_TICKS(I18n.format("optionDisableTicks"), false, false, MISC, false);

    private final String name;
    private final boolean isCategory;
    private final OptionCore category;
    private boolean value;
    private boolean restricted;

    OptionCore(String optionName, boolean defaultValue, boolean isCat, OptionCore category, boolean onlyOne) {
        name = optionName;
        value = defaultValue;
        isCategory = isCat;
        this.category = category;
        restricted = onlyOne;
    }

    public static OptionCore fromString(String str) {
        return Stream.of(values()).filter(option -> option.toString().equals(str)).findAny().orElse(null);
    }

    @Override
    public final String toString() {
        return name;
    }

    /**
     * This will flip the enabled state of the option and return the new value
     * @return Returns the newly set value
     */
    @Setter
    public boolean flip() {
        this.value = !this.isEnabled();
        ConfigHandler.setOption(this);
        if (this == CUSTOM_FONT) GLCore.setFont(Minecraft.getMinecraft(), this.value);
        return this.value;
    }

    /**
     * @return Returns true if the Option is selected/enabled
     */
    @Getter
    public boolean isEnabled() {
        return this.value;
    }

    /**
     * This checks if the Option is restricted or not.
     * Restricted Options can only have one option enabled
     * in their Category.
     *
     * @return Returns true if restricted
     */
    @Getter
    public boolean isRestricted() {
        return this.restricted;
    }

    /**
     * @return Returns the Category
     */
    @Getter
    public OptionCore getCategory() {
        return this.category;
    }

    /**
     * @return Returns the Category name in String format
     */
    @Getter
    public String getCategoryName() {
        return this.category.toString();
    }

    /**
     * This will disable the Option when called
     */
    @Setter
    public void disable() {
        if (this.value) this.flip();
    }

    /**
     * This will enable the Option when called
     */
    @Setter
    public void enable() {
        if (!this.value) this.flip();
    }

    /**
     * @return Returns the Option name in String format
     */
    @Getter
    public String getName() {
        return name;
    }

    /**
     * @return Returns true if this is a Category or not
     */
    @Getter
    public boolean isCategory() {
        return isCategory;
    }
}