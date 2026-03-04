package dev.luminous.mod.modules.impl.client;

import dev.luminous.mod.modules.Module;
import dev.luminous.mod.modules.settings.impl.ColorSetting;
import dev.luminous.mod.modules.settings.impl.EnumSetting;

import java.awt.*;

public class Colors extends Module {
    public static Colors INSTANCE;

    // 主题枚举
    public enum Theme {
        DARK("Dark", "原版深色主题"),
        CYBERPUNK("Cyberpunk", "赛博朋克"),
        AURORA("Aurora", "极光幻彩"),
        MATRIX("Matrix", "黑客帝国"),
        SUNSET("Sunset", "日落黄昏"),
        OCEAN("Ocean", "深海极光");

        private final String name;
        private final String chinese;

        Theme(String name, String chinese) {
            this.name = name;
            this.chinese = chinese;
        }

        public String getName() {
            return name;
        }

        public String getChinese() {
            return chinese;
        }
    }

    public Colors() {
        super("Colors", Category.Client);
        setChinese("颜色");
        INSTANCE = this;
    }

    public final EnumSetting<Theme> theme = add(new EnumSetting<>("Theme", Theme.DARK));
    public final ColorSetting clientColor = add(new ColorSetting("ClientColor", new Color(255, 0, 0)).injectBoolean(true));

    @Override
    public void enable() {
        this.state = true;
    }

    @Override
    public void disable() {
        this.state = true;
    }

    @Override
    public boolean isOn() {
        return true;
    }

    /**
     * 获取当前主题的主色调
     */
    public Color getMainColor() {
        switch (theme.getValue()) {
            case CYBERPUNK:
                return new Color(0xff006e);
            case AURORA:
                return new Color(0xffb3c1);
            case MATRIX:
                return new Color(0x00ff41);
            case SUNSET:
                return new Color(0xff6b35);
            case OCEAN:
                return new Color(0x457b9d);
            case DARK:
            default:
                return new Color(255, 0, 0);
        }
    }

    /**
     * 获取背景渐变起始色
     */
    public Color getBackgroundStart() {
        switch (theme.getValue()) {
            case CYBERPUNK:
                return new Color(0x1a1a2e);
            case AURORA:
                return new Color(0x2d2d2d);
            case MATRIX:
                return new Color(0x0a0a0a);
            case SUNSET:
                return new Color(0x2b1d0e);
            case OCEAN:
                return new Color(0x0d1b2a);
            case DARK:
            default:
                return new Color(0, 0, 0, 0);
        }
    }

    /**
     * 获取背景渐变结束色
     */
    public Color getBackgroundEnd() {
        switch (theme.getValue()) {
            case CYBERPUNK:
                return new Color(0x16213e);
            case AURORA:
                return new Color(0x4a4e69);
            case MATRIX:
                return new Color(0x1a2f1a);
            case SUNSET:
                return new Color(0x4a2c2a);
            case OCEAN:
                return new Color(0x1b263b);
            case DARK:
            default:
                return new Color(80, 80, 80, 80);
        }
    }

    /**
     * 获取分类颜色
     */
    public Color getCategoryColor(String categoryName) {
        Theme currentTheme = theme.getValue();

        switch (currentTheme) {
            case CYBERPUNK:
                return getCyberpunkCategoryColor(categoryName);
            case AURORA:
                return getAuroraCategoryColor(categoryName);
            case MATRIX:
                return getMatrixCategoryColor(categoryName);
            case SUNSET:
                return getSunsetCategoryColor(categoryName);
            case OCEAN:
                return getOceanCategoryColor(categoryName);
            case DARK:
            default:
                return new Color(255, 0, 0);
        }
    }

    // 赛博朋克主题分类颜色
    private Color getCyberpunkCategoryColor(String categoryName) {
        switch (categoryName.toLowerCase()) {
            case "combat":
                return new Color(0xff006e); // 霓虹粉
            case "movement":
                return new Color(0x3a86ff); // 赛博蓝
            case "render":
                return new Color(0x8338ec); // 荧光紫
            case "player":
                return new Color(0xfb5607); // 橙色
            case "misc":
                return new Color(0x06d6a0); // 青色
            case "exploit":
                return new Color(0xffbe0b); // 黄色
            default:
                return new Color(0xff006e);
        }
    }

    // 极光幻彩主题分类颜色
    private Color getAuroraCategoryColor(String categoryName) {
        switch (categoryName.toLowerCase()) {
            case "combat":
                return new Color(0xffb3c1); // 樱花粉
            case "movement":
                return new Color(0xa2d2ff); // 天空蓝
            case "render":
                return new Color(0xcdb4db); // 薰衣草紫
            case "player":
                return new Color(0xb5e48c); // 薄荷绿
            case "misc":
                return new Color(0xffd166); // 柠檬黄
            case "exploit":
                return new Color(0xffafcc); // 玫瑰粉
            default:
                return new Color(0xffb3c1);
        }
    }

    // 黑客帝国主题分类颜色
    private Color getMatrixCategoryColor(String categoryName) {
        switch (categoryName.toLowerCase()) {
            case "combat":
                return new Color(0x00ff41); // 矩阵绿
            case "movement":
                return new Color(0xff3333); // 警示红
            case "render":
                return new Color(0x00ccff); // 数据蓝
            case "player":
                return new Color(0xffd700); // 金色
            case "misc":
                return new Color(0xcc33ff); // 紫色
            case "exploit":
                return new Color(0xff6600); // 橙红
            default:
                return new Color(0x00ff41);
        }
    }

    // 日落黄昏主题分类颜色
    private Color getSunsetCategoryColor(String categoryName) {
        switch (categoryName.toLowerCase()) {
            case "combat":
                return new Color(0xff6b35); // 火焰红
            case "movement":
                return new Color(0xf7931e); // 日落橙
            case "render":
                return new Color(0xffcc00); // 暖黄
            case "player":
                return new Color(0xff8fa3); // 珊瑚粉
            case "misc":
                return new Color(0x9b5de5); // 紫罗兰
            case "exploit":
                return new Color(0xff4757); // 深红
            default:
                return new Color(0xff6b35);
        }
    }

    // 深海极光主题分类颜色
    private Color getOceanCategoryColor(String categoryName) {
        switch (categoryName.toLowerCase()) {
            case "combat":
                return new Color(0xe63946); // 珊瑚红
            case "movement":
                return new Color(0x457b9d); // 海洋蓝
            case "render":
                return new Color(0xa8dadc); // 浪花白
            case "player":
                return new Color(0x2a9d8f); // 海藻绿
            case "misc":
                return new Color(0x4a0e4e); // 深紫
            case "exploit":
                return new Color(0x1d3557); // 深蓝
            default:
                return new Color(0xe63946);
        }
    }
}