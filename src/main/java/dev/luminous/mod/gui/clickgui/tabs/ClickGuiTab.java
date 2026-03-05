// ... 其他代码保持不变 ...

@Override
public void draw(DrawContext drawContext, float partialTicks, Color color) {
    int tempHeight = 1;
    for (ModuleComponent child : children) {
        tempHeight += child.getHeight();
    }
    this.height = tempHeight;
    MatrixStack matrixStack = drawContext.getMatrices();

    RenderSystem.disableDepthTest();
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    currentHeight = animation.get(height);
    Colors colors = Colors.INSTANCE;
    Color themeColor = colors.getCategoryColor(title);
    Color bgStart = colors.getBackgroundStart();
    Color bgEnd = colors.getBackgroundEnd();

    if (drawBorder) {
        // 修复：调用 .getValue()
        if (ClickGui.INSTANCE.barEnd.getValue()) {
            Render2DUtil.drawRectVertical(matrixStack, x, y - 1, width, 15, themeColor, ClickGui.INSTANCE.barEnd.getValue());
        } else {
            Render2DUtil.drawRect(matrixStack, x, y - 1, width, 15, themeColor);
        }

        if (popped) {
            float panelHeight = (float) currentHeight;
            if (colors.theme.getValue() != Colors.Theme.DARK) {
                Render2DUtil.drawRoundGradient(matrixStack, x, y + 14, width, panelHeight,
                        CATEGORY_RADIUS, bgStart, bgEnd, true);
            } else {
                Render2DUtil.drawRect(matrixStack, x, y + 14, width, panelHeight,
                        ClickGui.INSTANCE.background.getValue());
            }

            int i = defaultHeight;
            for (Component child : children) {
                child.draw(i, drawContext, partialTicks, color, false);
                i += child.getHeight();
            }
        }
    }

    TextUtil.drawString(drawContext, this.title, x + 4, y + 3, new Color(255, 255, 255));

    RenderSystem.enableDepthTest();
    RenderSystem.disableBlend();
}
