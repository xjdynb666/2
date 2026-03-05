// ... 其他代码保持不变 ...

@Override
public boolean draw(int offset, DrawContext drawContext, float partialTicks, Color color, boolean back) {
    if (popped) {
        pickerHeight = Math.min(animation3.get(45), 45);
        setHeight(defaultHeight + 45);
    } else {
        pickerHeight = Math.max(animation3.get(0), 0);
        setHeight(defaultHeight);
    }

    int x = parent.getX();
    int y = (parent.getY() + offset) - 2;
    int width = parent.getWidth();
    MatrixStack matrixStack = drawContext.getMatrices();

    // 修复：调用 .getValue()
    Render2DUtil.drawRound(matrixStack, (float) x + 1, (float) y + 1, (float) width - 2,
            (float) defaultHeight - (ClickGui.INSTANCE.maxFill.getValue() ? 0 : 1),
            BUTTON_RADIUS, hover ? ClickGui.INSTANCE.settingHover.getValue() : ClickGui.INSTANCE.setting.getValue());

    boolean unShift = !hover || mc == null || !InputUtil.isKeyPressed(
            mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT);

    // 修复：调用 .getValue()
    if (colorSetting.injectBoolean) {
        currentWidth = animation.get(colorSetting.booleanValue ? (width - 2D) : 0D);
        switch (ClickGui.INSTANCE.uiType.getValue()) {
            case Old -> {
                // 修复：调用 .getValue()
                if (ClickGui.INSTANCE.mainEnd.getValue()) {
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
                            (float) (y + getTextOffsetY()), -1);
                }
            }
            case New -> {
                if (unShift) {
                    TextUtil.drawString(drawContext, colorSetting.getName(), x + 4,
                            (float) (y + getTextOffsetY()),
                            colorSetting.booleanValue ? ClickGui.INSTANCE.enableTextS.getValue()
                                    : ClickGui.INSTANCE.disableText.getValue());
                }
            }
        }
    } else if (unShift) {
        TextUtil.drawString(drawContext, colorSetting.getName(), x + 4,
                (float) (y + getTextOffsetY()), -1);
    }

    // ... 其他代码保持不变 ...
}
