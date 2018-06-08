package com.saomc.saoui.neo.screens

import com.saomc.saoui.GLCore
import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.elements.neo.NeoIconElement
import com.saomc.saoui.api.elements.neo.basicAnimation
import com.saomc.saoui.util.ColorIntent
import com.saomc.saoui.util.ColorUtil
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import kotlin.math.max

open class Popup<T : Any>(var title: String, var text: String, private val buttons: Map<NeoIconElement, T>) : NeoGui<T>(Vec2d.ZERO) {

    private val rl = ResourceLocation(SAOCore.MODID, "textures/menu/parts/alertbg.png")
    internal /*private*/ var expansion = 0f
    internal /*private*/ var currheight = h * 0.625
    internal /*private*/ var eol = 1f

    companion object {
        private const val w = 220.0
        private const val h = 160.0
    }

    override fun initGui() {
        val animDuration = 20f

        elements.clear()

        pos = vec(width / 2.0, height / 2.0)
        destination = pos

        val childrenXSeparator = w / buttons.size
        val childrenXOffset = -childrenXSeparator / buttons.size - 9
        val childrenYOffset = h * 0.3125 - 9

        var i = 0
        buttons.forEach { button, result ->
            button.onClick {
                this@Popup.result = result
                onGuiClosed()
                true
            }
            button.pos = vec(childrenXOffset + childrenXSeparator * i++, childrenYOffset)
            button.destination = button.pos
            +basicAnimation(button, "opacity") {
                duration = animDuration
                from = 0f
                easing = Easing.easeInQuint
            }
            +basicAnimation(button, "pos") {
                duration = animDuration
                from = vec(button.pos.x, h * 0.125 - 9)
                easing = Easing.easeInQuint
            }
            +basicAnimation(button, "scale") {
                duration = animDuration
                from = Vec2d.ZERO
                easing = object : Easing() {
                    override fun invoke(progress: Float): Float {
                        val t = Easing.easeInQuint(progress)
                        return if (t < 0.2f) t * 4 + 0.2f
                        else 1f
                    }

                }
            }
            +button
        }

        +basicAnimation(this, "expansion") {
            to = 1f
            duration = animDuration
            easing = Easing.easeInQuint
        }
        +basicAnimation(this, "currheight") {
            to = h
            duration = animDuration
            easing = Easing.easeInQuint
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        // TODO: these could be moved to Liblib's Sprites. Maybe.

        GlStateManager.pushMatrix()
        GlStateManager.translate(pos.x, pos.y, 0.0)
        if (expansion < 0.2f) {
            GLCore.glScalef(expansion * 4 + 0.2f, expansion * 4 + 0.2f, 1f)
        }
        if (eol < 1f) {
            GLCore.glScalef(eol, 1f, 1f)
        }

        val shadows = if (expansion > 0.66f) {
            h * 0.125
        } else {
            h * 0.1875 * expansion
        }

        val step1 = h * 0.250
        val step3 = max(h * 0.125 - h * 0.375 * (1 - expansion), 0.0) // * lines // TODO: handle multiline text
        val step5 = h * 0.375

//        if (shadows < 20.0) SAOCore.LOGGER.info("h=$h; shadows=$shadows; step3=$step3; expansion=$expansion")

        val h = h - h * 0.375 * (1 - expansion)

        val alpha = if (expansion < 1f) expansion else eol

        GLCore.glBindTexture(rl)
        GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR.multiplyAlpha(alpha))
        GLCore.glTexturedRect(-w / 2.0, -h / 2.0, w, step1, 0.0, 0.0, 256.0, 64.0) // Title bar
        GLCore.glTexturedRect(-w / 2.0, -h / 2.0 + step1, w, shadows, 0.0, 64.0, 256.0, 32.0) // Top shadow
        GLCore.glTexturedRect(-w / 2.0, -h / 2.0 + step1 + shadows, w, step3, 0.0, 96.0, 256.0, 32.0) // Text lines
        GLCore.glTexturedRect(-w / 2.0, -h / 2.0 + step1 + shadows + step3, w, shadows, 0.0, 128.0, 256.0, 32.0) // Bottom shadowr
        GLCore.glTexturedRect(-w / 2.0, -h / 2.0 + step1 + shadows + step3 + shadows, w, step5, 0.0, 160.0, 256.0, 96.0) // Button bar

        if (alpha > 0.03f) GLCore.glString(title, -GLCore.glStringWidth(title) / 2, (-h / 2.0 + step1 / 2).toInt(), ColorUtil.DEFAULT_BOX_FONT_COLOR.multiplyAlpha(alpha), centered = true)
        if (alpha > 0.56f) GLCore.glString(text, -GLCore.glStringWidth(text) / 2, (-h / 2.0 + step1 + shadows + step3 / 2).toInt(), ColorUtil.DEFAULT_FONT_COLOR.multiplyAlpha((alpha - 0.5f) / 0.5f), centered = true)

        // Guides
        /*GLCore.glBindTexture(StringNames.gui)
        GLCore.glColorRGBA(ColorUtil.DEAD_COLOR.multiplyAlpha(0.5f))
        for (i in 1..15) {
            GLCore.glTexturedRect(-w / 2.0 + i * w / 16, -h / 2.0, 1.0, h, 5.0, 120.0, 2.0, 2.0)
            GLCore.glTexturedRect(-w / 2.0, -h / 2.0 + i * h / 16.0, w, 1.0, 5.0, 120.0, 2.0, 2.0)
        }
        GLCore.glColor(0f, 1f, 0f, 0.5f)
        GLCore.glTexturedRect(-w / 2.0, h * 0.3125, w, 1.0, 5.0, 120.0, 2.0, 2.0)

        val childrenXSeparator = w / buttons.size
        val childrenXOffset = -childrenXSeparator / buttons.size

        GLCore.glTexturedRect(childrenXOffset, -h / 2, 1.0, h, 5.0, 120.0, 2.0, 2.0)
        GLCore.glTexturedRect(childrenXOffset + childrenXSeparator, -h / 2, 1.0, h, 5.0, 120.0, 2.0, 2.0)*/

        GlStateManager.popMatrix()

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun onGuiClosed() {
        +basicAnimation(this, "eol") {
            to = 0f
            easing = Easing.linear
            duration = 10f
            completion = Runnable {
                super.onGuiClosed()
            }
        }
        elements.forEach { button ->
            +basicAnimation(button, "opacity") {
                duration = 10f
                to = 0f
                easing = Easing.linear
            }
            +basicAnimation(button, "scale") {
                duration = 10f
                to = vec(0.0, 1.0)
                easing = Easing.linear
            }
        }
    }
}

class PopupYesNo(title: String, text: String) : Popup<PopupYesNo.Result>(title, text, mapOf(
        NeoIconElement(IconCore.CONFIRM)
                .setBgColor(ColorIntent.NORMAL, ColorUtil.CONFIRM_COLOR)
                .setBgColor(ColorIntent.HOVERED, ColorUtil.CONFIRM_COLOR_LIGHT)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                to Result.YES,
        NeoIconElement(IconCore.CANCEL)
                .setBgColor(ColorIntent.NORMAL, ColorUtil.CANCEL_COLOR)
                .setBgColor(ColorIntent.HOVERED, ColorUtil.CANCEL_COLOR_LIGHT)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                to Result.NO
)) {

    init {
        result = Result.NO
    }

    enum class Result {
        YES,
        NO
    }
}