package dev.luminous.mod.gui.clickgui.components.impl;

import dev.luminous.api.utils.render.Render2DUtil;
import dev.luminous.api.utils.render.TextUtil;
import dev.luminous.core.impl.GuiManager;
import dev.luminous.mod.gui.clickgui.ClickGuiScreen;
import dev.luminous.mod.gui.clickgui.components.Component;
import dev.luminous.mod.gui.clickgui.tabs.ClickGuiTab;
import dev.luminous.mod.modules.impl.client.ClickGui;
import dev.luminous.mod.modules.impl.client.Colors;
import dev.luminous.mod.modules.settings.impl.BooleanSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class BooleanComponent extends Component {

    final BooleanSetting setting;

    // 圆角半径配置
    private static final float BUTTON_RADIUS = 5.0f;
    private static final float SWITCH_RADIUS = 8.0f;  // 开关按钮半径（圆形）
    private static final int SWITCH_WIDTH = 16;
    private static final int SWITCH_HEIGHT = 10;

    public BooleanComponent(ClickGuiTab parent, BooleanSetting setting) {
        super();
        this.parent = parent;
        this.setting = setting;
    }

    @Override
    public boolean isVisible() {
        if (setting.visibility != null) {
            return setting.visibility.getAsBoolean();
        }
        return true;
    }

    boolean hover = false;

    @Override


    public void update(int offset, double mouseX, double mouseY) {
        int parentX = parent.getX();
        int parentY = parent.getY();
        int parentWidth = parent.getWidth();
        if (GuiManager.currentGrabbed == null && isVisible() && (mouseX >= ((parentX + 1)) && mouseX <= (((parentX)) + parentWidth - 1)) && (mouseY >= (((parentY + offset))) && mouseY <= ((parentY + offset) + defaultHeight - 2))) {
            hover = true;
            if (ClickGuiScreen.clicked) {
                ClickGuiScreen.clicked = false;
                sound();
                setting.toggleValue();
            }
            if (ClickGuiScreen.rightClicked) {
                ClickGuiScreen.rightClicked = false;
                sound();
                setting.popped = !setting.popped;
            }
        } else {
            hover = false;
        }
    }

    public double currentWidth = 0;

    @Override


    public boolean draw(int offset, DrawContext drawContext, float partialTicks, Color color, boolean back) {
        int x = parent.getX();
        int y = parent.getY() + offset - 2;
        int width = parent.getWidth();
        MatrixStack matrixStack = drawContext.getMatrices();

        // 获取当前主题颜色
        Colors colors = Colors.INSTANCE;
        Color themeColor = colors.getCategoryColor(parent.getTitle());

        // 背景区域 - 使用圆角
        Color bgColor = hover ? ClickGui.INSTANCE.settingHover.getValue() : ClickGui.INSTANCE.setting.getValue();
        Render2DUtil.drawRound(matrixStack, (float) x + 1, (float) y + 1, (float) width - 2, (float) defaultHeight - (ClickGui.INSTANCE.maxFill.getValue() ? 0 : 1), BUTTON_RADIUS, bgColor);

        currentWidth = animation.get(setting.getValue() ? (width - 2D) : 0D);

        switch (ClickGui.INSTANCE.uiType.getValue()) {
            case New -> {
                TextUtil.drawString(drawContext, setting.getName(), x + 4, y + getTextOffsetY(), setting.getValue() ? ClickGui.INSTANCE.enableTextS.getValue() : ClickGui.INSTANCE.disableText.getValue());

                // 添加圆形开关按钮
                drawCircularSwitch(drawContext, matrixStack, x, y, width, themeColor);
            }
            case Old -> {
                if (ClickGui.INSTANCE.mainEnd.booleanValue) {
                    Render2DUtil.drawRectHorizontal(matrixStack, (float) x + 1, (float) y + 1, (float) currentWidth, (float) defaultHeight - (ClickGui.INSTANCE.maxFill.getValue() ? 0 : 1), hover ? ClickGui.INSTANCE.mainHover.getValue() : color, ClickGui.INSTANCE.mainEnd.getValue());
                } else {
                    Render2DUtil.drawRect(matrixStack, (float) x + 1, (float) y + 1, (float) currentWidth, (float) defaultHeight - (ClickGui.INSTANCE.maxFill.getValue() ? 0 : 1), hover ? ClickGui.INSTANCE.mainHover.getValue() : color);
                }
                TextUtil.drawString(drawContext, setting.getName(), x + 4, y + getTextOffsetY(), new Color(-1).getRGB());
            }
        }

        if (setting.parent) {
            TextUtil.drawString(drawContext, setting.popped ? "-" : "+", x + width - 11,
                    y + getTextOffsetY(), new Color(255, 255, 255).getRGB());
        }
        return true;
    }

    /**
     * 绘制圆形开关按钮
     */
    private void drawCircularSwitch(DrawContext drawContext, MatrixStack matrixStack, int x, int y, int width, Color themeColor) {
        // 开关位置
        int switchX = x + width - 20;
        int switchY = y + 4;

        // 背景圆角矩形
        Color bgColor = setting.getValue() ? themeColor : new Color(60, 60, 60, 200);
        Render2DUtil.drawRound(matrixStack, switchX, switchY, SWITCH_WIDTH, SWITCH_HEIGHT, SWITCH_RADIUS / 2, bgColor);

        // 开关圆圈（圆形）
        float circleRadius = SWITCH_HEIGHT / 2.0f - 1;
        float circleX = setting.getValue() ? switchX + SWITCH_WIDTH - circleRadius - 2 : switchX + circleRadius + 1;
        float circleY = switchY + SWITCH_HEIGHT / 2.0f;

        Color circleColor = new Color(255, 255, 255);
        Render2DUtil.drawCircle(matrixStack, circleX, circleY, circleRadius, circleColor);
    }
}