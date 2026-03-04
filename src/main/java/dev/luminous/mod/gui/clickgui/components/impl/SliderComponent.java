package dev.luminous.mod.gui.clickgui.components.impl;

import dev.luminous.api.utils.math.Timer;
import dev.luminous.api.utils.render.Render2DUtil;
import dev.luminous.api.utils.render.TextUtil;
import dev.luminous.core.impl.GuiManager;
import dev.luminous.mod.gui.clickgui.ClickGuiScreen;
import dev.luminous.mod.gui.clickgui.components.Component;
import dev.luminous.mod.gui.clickgui.tabs.ClickGuiTab;
import dev.luminous.mod.modules.impl.client.ClickGui;
import dev.luminous.mod.modules.impl.client.Colors;
import dev.luminous.mod.modules.settings.impl.SliderSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class SliderComponent extends Component {

	private final ClickGuiTab parent;
	private double currentSliderPosition;
	final SliderSetting setting;

	// 圆角配置
	private static final float BUTTON_RADIUS = 5.0f;
	private static final float SLIDER_KNOB_RADIUS = 5.0f;  // 滑块半径（圆形）

	public SliderComponent(ClickGuiTab parent, SliderSetting setting) {
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

	private boolean clicked = false;
	private boolean hover = false;
	private boolean firstUpdate = true;

	@Override
	public void update(int offset, double mouseX, double mouseY) {
		if (firstUpdate || setting.update) {
			this.currentSliderPosition = (float) ((setting.getValue() - setting.getMinimum()) / setting.getRange());
			firstUpdate = false;
		}
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();

		if ((mouseX >= ((parentX)) && mouseX <= (((parentX)) + parentWidth - 2)) && (mouseY >= (((parentY + offset))) && mouseY <= ((parentY + offset) + defaultHeight - 2))) {
			hover = true;
			if (GuiManager.currentGrabbed == null && isVisible()) {
				if (ClickGuiScreen.clicked) {
					sound();
				}
				if (ClickGuiScreen.clicked || ClickGuiScreen.hoverClicked && clicked) {
					if (setting.isListening()) {
						setting.setListening(false);
						ClickGuiScreen.clicked = false;
					} else {
						clicked = true;
						ClickGuiScreen.hoverClicked = true;
						ClickGuiScreen.clicked = false;
						this.currentSliderPosition = (float) Math.min((mouseX - (parentX)) / (parentWidth - 4), 1f);
						this.currentSliderPosition = Math.max(0f, this.currentSliderPosition);
						this.setting.setValue((this.currentSliderPosition * this.setting.getRange()) + this.setting.getMinimum());
					}
				}
				if (ClickGuiScreen.rightClicked) {
					sound();
					setting.setListening(!setting.isListening());
					ClickGuiScreen.rightClicked = false;
				}
			}
		} else {
			clicked = false;
			hover = false;
		}
	}

	public double renderSliderPosition = 0;
	private final Timer timer = new Timer();
	boolean b;

	@Override
	public boolean draw(int offset, DrawContext drawContext, float partialTicks, Color color, boolean back) {
		if (back) {
			setting.setListening(false);
		}
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();
		MatrixStack matrixStack = drawContext.getMatrices();

		// 获取当前主题颜色
		Colors colors = Colors.INSTANCE;
		Color themeColor = colors.getCategoryColor(parent.getTitle());

		renderSliderPosition = animation.get(Math.floor((parentWidth - 2) * currentSliderPosition));

		// 背景区域 - 使用圆角
		Color bgColor = hover ? ClickGui.INSTANCE.settingHover.getValue() : ClickGui.INSTANCE.setting.getValue();
		Render2DUtil.drawRound(matrixStack, (float) parentX + 1, (float) (parentY + offset - 1), (float) parentWidth - 2, (float) defaultHeight - (ClickGui.INSTANCE.maxFill.getValue() ? 0 : 1), BUTTON_RADIUS, bgColor);

		// 绘制滑动条
		drawSlider(drawContext, matrixStack, parentX, parentY, parentWidth, offset, color, themeColor);

		// 绘制数值文本
		if (this.setting == null) return true;
		if (setting.isListening()) {
			if (timer.passed(1000)) {
				b = !b;
				timer.reset();
			}
			TextUtil.drawString(drawContext, setting.temp + (b ? "_" : ""), parentX + 4,
					(float) (parentY + getTextOffsetY() + offset - 2), 0xFFFFFF);
		} else {
			String value;
			if (setting.getValueInt() == setting.getValue()) {
				value = String.valueOf(setting.getValueInt());
			} else {
				value = String.valueOf(this.setting.getValueFloat());
			}
			value = value + setting.getSuffix();
			TextUtil.drawString(drawContext, setting.getName(), (float) (parentX + 4),
					(float) (parentY + getTextOffsetY() + offset - 2), 0xFFFFFF);
			TextUtil.drawString(drawContext, value, parentX + parentWidth - TextUtil.getWidth(value) - 5,
					(float) (parentY + getTextOffsetY() + offset - 2), 0xFFFFFF);
		}
		return true;
	}

	/**
	 * 绘制圆形滑块滑动条
	 */
	private void drawSlider(DrawContext drawContext, MatrixStack matrixStack, int x, int y, int width, int offset, Color color, Color themeColor) {
		float sliderY = (float) (y + offset + defaultHeight - 3);
		float sliderHeight = ClickGui.INSTANCE.uiType.getValue() == ClickGui.Type.New ? 1 : defaultHeight - (ClickGui.INSTANCE.maxFill.getValue() ? 0 : 1);

		// 计算滑块位置
		float sliderX = (float) (x + 1 + renderSliderPosition);

		if (ClickGui.INSTANCE.uiType.getValue() == ClickGui.Type.New) {
			// 新版UI：绘制圆形滑块
			// 滑动条轨道（细线）
			Color trackColor = new Color(60, 60, 60, 150);
			Render2DUtil.drawRound(matrixStack, x + 1, sliderY, width - 2, sliderHeight, sliderHeight / 2, trackColor);

			// 已填充部分
			float filledWidth = (float) renderSliderPosition;
			Render2DUtil.drawRound(matrixStack, x + 1, sliderY, filledWidth, sliderHeight, sliderHeight / 2, themeColor);

			// 圆形滑块
			Render2DUtil.drawCircle(matrixStack, sliderX, sliderY + sliderHeight / 2, SLIDER_KNOB_RADIUS, new Color(255, 255, 255));
		} else {
			// 旧版UI：保持原有风格，但添加圆角
			if (ClickGui.INSTANCE.mainEnd.booleanValue) {
				Render2DUtil.drawRectHorizontal(matrixStack, x + 1, (float) (y + offset - 1), (int) renderSliderPosition, sliderHeight, hover ? ClickGui.INSTANCE.mainHover.getValue() : color, ClickGui.INSTANCE.mainEnd.getValue());
			} else {
				Render2DUtil.drawRound(matrixStack, x + 1, (float) (y + offset - 1), (int) renderSliderPosition, sliderHeight, BUTTON_RADIUS, hover ? ClickGui.INSTANCE.mainHover.getValue() : color);
			}
		}
	}
}