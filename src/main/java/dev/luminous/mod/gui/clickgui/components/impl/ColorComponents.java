package dev.luminous.mod.gui.clickgui.components.impl;
import dev.luminous.api.utils.math.Animation;
import dev.luminous.api.utils.math.MathUtil;
import dev.luminous.api.utils.math.Timer;
import dev.luminous.api.utils.render.ColorUtil;
import dev.luminous.api.utils.render.Render2DUtil;
import dev.luminous.api.utils.render.TextUtil;
import dev.luminous.core.impl.GuiManager;
import dev.luminous.mod.gui.clickgui.ClickGuiScreen;
import dev.luminous.mod.gui.clickgui.components.Component;
import dev.luminous.mod.gui.clickgui.tabs.ClickGuiTab;
import dev.luminous.mod.modules.impl.client.ClickGui;
import dev.luminous.mod.modules.settings.impl.ColorSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw org.lwjgl.glfw.GLFW;
import java.awt.*;

public class ColorComponents extends Component {
    private float hue;
    private float saturation;
    private float brightness;
    private int alpha;
    private boolean afocused;
    private boolean hfocused;
    private boolean sbfocused;
    private float spos, bpos, hpos, apos;
    private Color prevColor;
    // 圆角半径配置
    private static final float BUTTON_RADIUS = 5.0f;
    private static final float PICKER_RADIUS = 6.0f;
    private final ColorSetting colorSetting;
    private static final Minecraft mc = Minecraft.getInstance();
    // 修复：移除static，改为实例变量，解决多实例颜色覆盖问题
    private int copyColor = -1;
    // 动画速率常量，统一插值速率，提升流畅度
    private static final float ANIMATE_SPEED = 0.15f;

    public ColorSetting getColorSetting() {
        return colorSetting;
    }

    public ColorComponents(ClickGuiTab parent, ColorSetting setting) {
        super();
        this.parent = parent;
        this.colorSetting = setting;
        prevColor = getColorSetting().getValue();
        updatePos();
    }

    @Override
    public boolean isVisible() {
        return colorSetting.visibility == null || colorSetting.visibility.getAsBoolean();
    }

    private void updatePos() {
        float[] hsb = Color.RGBtoHSB(getColorSetting().getValue().getRed(),
                getColorSetting().getValue().getGreen(),
                getColorSetting().getValue().getBlue(), null);
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
        alpha = getColorSetting().getValue().getAlpha();
    }

    private void setColor(Color color) {
        getColorSetting().setValue(color.getRGB());
        prevColor = color;
    }

    private final Timer clickTimer = new Timer();
    private double lastMouseX;
    private double lastMouseY;
    boolean clicked = false;
    boolean popped = false;
    boolean hover = false;
    public Animation animation3 = new Animation();
    double pickerHeight = 0;

    @Override
    public int getCurrentHeight() {
        return (int) (defaultHeight + pickerHeight);
    }

    @Override
    public void update(int offset, double mouseX, double mouseY) {
        clicked = false;
        int x = parent.getX();
        int y = (parent.getY() + offset) - 2;
        int width = parent.getWidth();
        double cx = x + 3;
        double cy = y + defaultHeight;
        double cw = width - 19;
        double ch = getHeight() - 17;

        // 优化：ch>0判空，避免鼠标hover判定异常
        hover = ch > 0 && Render2DUtil.isHovered(mouseX, mouseY, (float) x + 1, (float) y + 1,
                (float) width - 2, (float) defaultHeight - 1);
        boolean copyOrPaste = hover && mc != null && InputUtil.isKeyPressed(
                mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT);

        if (copyOrPaste) {
            if (GuiManager.currentGrabbed == null && isVisible()) {
                if (ClickGuiScreen.clicked) {
                    ClickGuiScreen.clicked = false;
                    sound();
                    copyColor = colorSetting.getValue().getRGB();
                }
                if (ClickGuiScreen.rightClicked) {
                    ClickGuiScreen.rightClicked = false;
                    if (copyColor != -1) {
                        sound();
                        colorSetting.setValue(copyColor);
                    }
                }
            }
            return;
        }

        if (hover) {
            if (GuiManager.currentGrabbed == null && isVisible()) {
                if (ClickGuiScreen.rightClicked) {
                    ClickGuiScreen.rightClicked = false;
                    sound();
                    this.popped = !this.popped;
                }
            }
        }

        if (popped) {
            animation3.set(45);
            pickerHeight = animation3.get(45);
            setHeight(defaultHeight + 45);
        } else {
            animation3.set(0);
            pickerHeight = animation3.get(0);
            setHeight(defaultHeight);
        }

        if ((ClickGuiScreen.clicked || ClickGuiScreen.hoverClicked) && isVisible() && ch > 0) {
            if (!clicked) {
                if (Render2DUtil.isHovered(mouseX, mouseY, cx + cw + 9, cy, 4, ch)) {
                    afocused = true;
                    ClickGuiScreen.hoverClicked = true;
                    ClickGuiScreen.clicked = false;
                }
                if (Render2DUtil.isHovered(mouseX, mouseY, cx + cw + 4, cy, 4, ch)) {
                    hfocused = true;
                    ClickGuiScreen.hoverClicked = true;
                    ClickGuiScreen.clicked = false;
                    if (colorSetting.isRainbow) {
                        colorSetting.setRainbow(false);
                        lastMouseX = 0;
                        lastMouseY = 0;
                    } else {
                        if (!clickTimer.passedMs(400) && mouseX == lastMouseX && mouseY == lastMouseY) {
                            colorSetting.setRainbow(!colorSetting.isRainbow);
                        }
                        clickTimer.reset();
                        lastMouseX = mouseX;
                        lastMouseY = mouseY;
                    }
                }
                if (Render2DUtil.isHovered(mouseX, mouseY, cx, cy, cw, ch)) {
                    sbfocused = true;
                    ClickGuiScreen.hoverClicked = true;
                    ClickGuiScreen.clicked = false;
                }
                if (GuiManager.currentGrabbed == null && isVisible()) {
                    if (hover && getColorSetting().injectBoolean) {
                        getColorSetting().booleanValue = !getColorSetting().booleanValue;
                        sound();
                        ClickGuiScreen.clicked = false;
                    }
                }
            }
            clicked = true;
        } else {
            sbfocused = false;
            afocused = false;
            hfocused = false;
        }

        if (!popped || ch <= 0) return;

        if (GuiManager.currentGrabbed == null && isVisible()) {
            Color value = Color.getHSBColor(hue, saturation, brightness);
            if (sbfocused) {
                saturation = (float) ((MathUtil.clamp((float) (mouseX - cx), 0f, (float) cw)) / cw);
                brightness = (float) ((ch - MathUtil.clamp((float) (mouseY - cy), 0, (float) ch)) / ch);
                value = Color.getHSBColor(hue, saturation, brightness);
                setColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), alpha));
            }
            if (hfocused) {
                hue = (float) ((ch - MathUtil.clamp((float) (mouseY - cy), 0, (float) ch)) / ch);
                value = Color.getHSBColor(hue, saturation, brightness);
                setColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), alpha));
            }
            if (afocused) {
                alpha = (int) (((ch - MathUtil.clamp((float) (mouseY - cy), 0, (float) ch)) / ch) * 255);
                setColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), alpha));
            }
        }
    }

    public double currentWidth = 0;

    @Override
    public boolean draw(int offset, DrawContext drawContext, float partialTicks, Color color, boolean back) {
        if (popped) {
            animation3.set(45);
            pickerHeight = animation3.get(45);
            setHeight(defaultHeight + 45);
        } else {
            animation3.set(0);
            pickerHeight = animation3.get(0);
            setHeight(defaultHeight);
        }

        int x = parent.getX();
        int y = parent.getY() + offset - 2;
        int width = parent.getWidth();
        MatrixStack matrixStack = drawContext.getMatrices();

        Render2DUtil.drawRound(matrixStack, (float) x + 1, (float) y + 1, (float) width - 2,
                (float) defaultHeight - (ClickGui.INSTANCE.maxFill.getValue() ? 0 : 1),
                BUTTON_RADIUS, hover ? ClickGui.INSTANCE.settingHover.getValue() : ClickGui.INSTANCE.setting.getValue());

        boolean unShift = !hover || mc == null || !InputUtil.isKeyPressed(
                mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT);

        if (colorSetting.injectBoolean) {
            currentWidth = animation.get(colorSetting.booleanValue ? (width - 2D) : 0D);
            switch (ClickGui.INSTANCE.uiType.getValue()) {
                case Old -> {
                    if (ClickGui.INSTANCE.mainEnd.booleanValue) {
                        Render2DUtil.drawRectHorizontal(matrixStack, (float) x + 1, (float) y + 1,
                                (float) currentWidth,
                                (float) defaultHeight - (ClickGui.INSTANCE.maxFill.getValue() ? 0 : 1),
                                hover ? ClickGui.INSTANCE.mainHover.getValue() : color, ClickGui.INSTANCE.mainEnd.getValue());
                    } else {
                        Render2DUtil.drawRound(matrixStack, (float) x + 1, (float) y + 1, (float) currentWidth,
                                (float) defaultHeight - (ClickGui.INSTANCE.maxFill.getValue() ? 0 : 1),
                                BUTTON_RADIUS, hover ? ClickGui.INSTANCE.mainHover.getValue() : color);
                    }
                    if (unShift) {
                        TextUtil.drawString(drawContext, colorSetting.getName(), x + 4,
                                y + getTextOffsetY(), -1);
                    }
                }
                case New -> {
                    if (unShift) {
                        TextUtil.drawString(drawContext, colorSetting.getName(), x + 4,
                                y + getTextOffsetY(),
                                colorSetting.booleanValue ? ClickGui.INSTANCE.enableTextS.getValue()
                                        : ClickGui.INSTANCE.disableText.getValue());
                    }
                }
            }
        } else if (unShift) {
            TextUtil.drawString(drawContext, colorSetting.getName(), x + 4,
                    y + getTextOffsetY(), -1);
        }

        if (!unShift) {
            TextUtil.drawString(drawContext, "§aL-Copy §cR-Paste", x + 4,
                    y + getTextOffsetY(), -1);
        }

        Render2DUtil.drawCircle(matrixStack, (float) (x + width - 10),
                (float) (y + getTextOffsetY() + 4), 6,
                ColorUtil.injectAlpha(getColorSetting().getValue(), 255));

        if (pickerHeight <= 1) {
            return true;
        }

        double cy = y + defaultHeight + 1;
        double cw = width - 15;
        double ch = defaultHeight - 17 + pickerHeight;

        // 优化：统一动画插值速率为ANIMATE_SPEED，提升过渡流畅度
        spos = (float) animate(spos, (float) (((double) x + cw) - (cw - (cw * saturation))), ANIMATE_SPEED);
        bpos = (float) animate(bpos, (float) (cy + (ch - (ch * brightness))), ANIMATE_SPEED);
        hpos = (float) animate(hpos, (float) (cy + (ch - 3 + ((ch - 3) * hue))), ANIMATE_SPEED);
        apos = (float) animate(apos, (float) (cy + (ch - 3 - ((ch - 3) * (alpha / 255f)))), ANIMATE_SPEED);

        Color colorA = Color.getHSBColor(hue, 0.0F, 1.0F);
        Color colorB = Color.getHSBColor(hue, 1.0F, 1.0F);
        Color colorC = new Color(0, 0, 0, 0), colorD = new Color(0, 0, 0);

        Render2DUtil.horizontalGradient(matrixStack, (float) (double) x + 2, (float) cy,
                (float) ((double) x + cw), (float) (cy + ch), colorA, colorB);
        Render2DUtil.verticalGradient(matrixStack, (float) ((double) x + 2), (float) cy,
                (float) ((double) x + cw), (float) (cy + ch), colorC, colorD);

        // 修复：色相条curHue计算逻辑，保证0~1完整色相覆盖
        for (float i = 1f; i < ch - 2f; i += 1f) {
            float curHue = i / (ch - 2f);
            Render2DUtil.drawRect(matrixStack, (float) ((double) x + cw + 4),
                    (float) (cy + i), 4, 1, Color.getHSBColor(curHue, 1f, 1f));
        }

        // 修复：透明度条宽度统一为4f（9+4=13），与指示条对齐
        Render2DUtil.verticalGradient(matrixStack, (float) ((double) x + cw + 9),
                (float) (cy + 0.8f), (float) ((double) x + cw + 13),
                (float) (cy + ch - 2),
                new Color(getColorSetting().getValue().getRed(),
                        getColorSetting().getValue().getGreen(),
                        getColorSetting().getValue().getBlue(), 255),
                new Color(0, 0, 0, 0));

        // 色相/透明度指示条与色条严格对齐
        Render2DUtil.drawRect(matrixStack, (float) ((double) x + cw + 4), hpos + 0.5f,
                4, 1, Color.WHITE);
        Render2DUtil.drawRect(matrixStack, (float) ((double) x + cw + 9), apos + 0.5f,
                4, 1, Color.WHITE);

        Render2DUtil.drawCircle(matrixStack, spos, bpos, 2, new Color(-1));
        return true;
    }
}
