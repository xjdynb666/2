package dev.luminous.api.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.luminous.api.utils.Wrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.awt.*;

public class Render2DUtil implements Wrapper {
    public static void horizontalGradient(MatrixStack matrices, float x1, float y1, float x2, float y2, Color startColor, Color endColor) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x1, y1, 0.0F).color(startColor.getRGB());
        bufferBuilder.vertex(matrix, x1, y2, 0.0F).color(startColor.getRGB());
        bufferBuilder.vertex(matrix, x2, y2, 0.0F).color(endColor.getRGB());
        bufferBuilder.vertex(matrix, x2, y1, 0.0F).color(endColor.getRGB());
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        endRender();
    }

    public static void verticalGradient(MatrixStack matrices, float left, float top, float right, float bottom, Color startColor, Color endColor) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, left, top, 0.0F).color(startColor.getRGB());
        bufferBuilder.vertex(matrix, left, bottom, 0.0F).color(endColor.getRGB());
        bufferBuilder.vertex(matrix, right, bottom, 0.0F).color(endColor.getRGB());
        bufferBuilder.vertex(matrix, right, top, 0.0F).color(startColor.getRGB());
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        endRender();
    }

    public static void drawRectVertical(MatrixStack matrices, float x, float y, float width, float height, Color startColor, Color endColor) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        setupRender();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.vertex(matrix, x, y, 0.0F).color(startColor.getRGB());
        bufferBuilder.vertex(matrix, x, y + height, 0.0F).color(endColor.getRGB());
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0F).color(endColor.getRGB());
        bufferBuilder.vertex(matrix, x + width, y, 0.0F).color(startColor.getRGB());
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        endRender();
    }
    public static void drawRectHorizontal(MatrixStack matrices, float x, float y, float width, float height, Color startColor, Color endColor) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        setupRender();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.vertex(matrix, x, y, 0.0F).color(startColor.getRGB());
        bufferBuilder.vertex(matrix, x, y + height, 0.0F).color(startColor.getRGB());
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0F).color(endColor.getRGB());
        bufferBuilder.vertex(matrix, x + width, y, 0.0F).color(endColor.getRGB());
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        endRender();
    }
    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, int c) {
        drawRect(matrices, x, y, width, height, new Color(c, true));
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, Color c) {
        if (c.getAlpha() <= 5) return;
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x, y + height, 0.0F).color(c.getRGB());
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0F).color(c.getRGB());
        bufferBuilder.vertex(matrix, x + width, y, 0.0F).color(c.getRGB());
        bufferBuilder.vertex(matrix, x, y, 0.0F).color(c.getRGB());
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        endRender();
    }

    public static void drawRect(DrawContext drawContext, float x, float y, float width, float height, Color c) {
        drawRect(drawContext.getMatrices(), x, y, width, height, c);
        //drawContext.fill((int) x, (int) y, (int) (x + width), (int) (y + height), c.getRGB());
    }
    public static boolean isHovered(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX - width <= x && mouseY >= y && mouseY - height <= y;
    }

    public static void drawRound(MatrixStack matrices, float x, float y, float width, float height, float radius, Color color) {
        renderRoundedQuad(matrices, color, x, y, width + x, height + y, radius, 4);
    }

    /**
     * 绘制带边框的圆角矩形
     */
    public static void drawRoundRectWithBorder(MatrixStack matrices, float x, float y, float width, float height,
                                               float radius, Color fillColor, Color borderColor, float borderWidth) {
        // 绘制填充
        drawRound(matrices, x, y, width, height, radius, fillColor);
        // 绘制边框
        drawRoundBorder(matrices, x, y, width, height, radius, borderColor, borderWidth);
    }

    /**
     * 绘制圆角边框
     */
    public static void drawRoundBorder(MatrixStack matrices, float x, float y, float width, float height,
                                       float radius, Color color, float borderWidth) {
        if (borderWidth <= 0) return;
        float innerRadius = Math.max(0, radius - borderWidth);

        // 上边框
        drawRound(matrices, x, y, width, borderWidth, Math.min(radius, borderWidth), color);
        // 下边框
        drawRound(matrices, x, y + height - borderWidth, width, borderWidth, Math.min(radius, borderWidth), color);
        // 左边框
        drawRound(matrices, x, y + borderWidth, borderWidth, height - 2 * borderWidth, Math.min(radius, borderWidth), color);
        // 右边框
        drawRound(matrices, x + width - borderWidth, y + borderWidth, borderWidth, height - 2 * borderWidth,
                 Math.min(radius, borderWidth), color);

        // 圆角边框
        renderRoundedQuad(matrices, color, x, y, x + radius * 2, y + radius * 2, radius, 4);
        renderRoundedQuad(matrices, color, x + width - radius * 2, y, x + width, y + radius * 2, radius, 4);
        renderRoundedQuad(matrices, color, x, y + height - radius * 2, x + radius * 2, y + height, radius, 4);
        renderRoundedQuad(matrices, color, x + width - radius * 2, y + height - radius * 2, x + width, y + height, radius, 4);
    }

    /**
     * 绘制圆形（用于开关按钮、滑动条等）
     */
    public static void drawCircle(MatrixStack matrices, float x, float y, float radius, Color color) {
        renderRoundedQuad(matrices, color, x - radius, y - radius, x + radius, y + radius, radius, 8);
    }

    /**
     * 绘制渐变圆角矩形
     */
    public static void drawRoundGradient(MatrixStack matrices, float x, float y, float width, float height,
                                        float radius, Color startColor, Color endColor, boolean vertical) {
        if (vertical) {
            verticalGradient(matrices, x, y, x + width, y + height, startColor, endColor);
        } else {
            horizontalGradient(matrices, x, y, x + width, y + height, startColor, endColor);
        }
        // 裁剪圆角（简单实现，先画渐变，再在角落画圆角裁剪）
        drawRoundCorners(matrices, x, y, width, height, radius);
    }

    /**
     * 绘制圆角以裁剪矩形
     */
    private static void drawRoundCorners(MatrixStack matrices, float x, float y, float width, float height, float radius) {
        // 四个角的外部区域（用于裁剪）
        // 左上角
        drawRect(matrices, x, y, radius, radius, new Color(0, 0, 0, 0));
        // 右上角
        drawRect(matrices, x + width - radius, y, radius, radius, new Color(0, 0, 0, 0));
        // 左下角
        drawRect(matrices, x, y + height - radius, radius, radius, new Color(0, 0, 0, 0));
        // 右下角
        drawRect(matrices, x + width - radius, y + height - radius, radius, radius, new Color(0, 0, 0, 0));
    }

    public static void renderRoundedQuad(MatrixStack matrices, Color c, double fromX, double fromY, double toX, double toY, double radius, double samples) {
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        renderRoundedQuadInternal(matrices.peek().getPositionMatrix(), c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f, fromX, fromY, toX, toY, radius, samples);
        endRender();
    }

    public static void renderRoundedQuadInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double radius, double samples) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        double[][] map = new double[][]{new double[]{toX - radius, toY - radius, radius}, new double[]{toX - radius, fromY + radius, radius}, new double[]{fromX + radius, fromY + radius, radius}, new double[]{fromX + radius, toY - radius, radius}};
        for (int i = 0; i < 4; i++) {
            double[] current = map[i];
            double rad = current[2];
            for (double r = i * 90d; r < (360 / 4d + i * 90d); r += (90 / samples)) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca);
            }
            float rad1 = (float) Math.toRadians((360 / 4d + i * 90d));
            float sin = (float) (Math.sin(rad1) * rad);
            float cos = (float) (Math.cos(rad1) * rad);
            bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca);
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    public static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public static void endRender() {
        RenderSystem.disableBlend();
    }

    public static void endBuilding(BufferBuilder bb) {
        BuiltBuffer builtBuffer = bb.endNullable();
        if (builtBuffer != null)
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
    }
}