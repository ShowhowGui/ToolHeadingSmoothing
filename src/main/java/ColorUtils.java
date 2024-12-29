import java.awt.*;

public class ColorUtils {
    public static Color[] getGoodColors(int number) {
        Color[] cs = new Color[number];
        switch (number) {
            case 1:
                cs[0] = HSL2RGB(0, 0, 0);
                break;
            case 2:
                cs[0] = HSL2RGB(204, 0.8F, 0.9F);
                cs[1] = HSL2RGB(341, 0.7F, 0.9F);
                break;
            case 3:
                cs[0] = HSL2RGB(206, 0.8F, 0.8F);
                cs[1] = HSL2RGB(150, 0.7F, 0.7F);
                cs[2] = HSL2RGB(36, 0.76F, 0.9F);
                break;
            default:
                cs = getDifferentColor(number);
        }
        return cs;
    }

    public static Color HSL2RGB(float hue, float saturation, float lightness) {
        float c = lightness * saturation;
        float x = c * (1.0F - Math.abs(hue / 60.0F % 2.0F - 1.0F));
        float m = lightness - c;
        float r = 0.0F;
        float g = 0.0F;
        float b = 0.0F;
        if (0.0F <= hue && hue < 60.0F) {
            r = c;
            g = x;
            b = 0.0F;
        } else if (60.0F <= hue && hue < 120.0F) {
            r = x;
            g = c;
            b = 0.0F;
        } else if (120.0F <= hue && hue < 180.0F) {
            r = 0.0F;
            g = c;
            b = x;
        } else if (180.0F <= hue && hue < 240.0F) {
            r = 0.0F;
            g = x;
            b = c;
        } else if (240.0F <= hue && hue < 300.0F) {
            r = x;
            g = 0.0F;
            b = c;
        } else if (300.0F <= hue && hue < 360.0F) {
            r = c;
            g = 0.0F;
            b = x;
        }

        int red = Math.round((r + m) * 255.0F);
        int green = Math.round((g + m) * 255.0F);
        int blue = Math.round((b + m) * 255.0F);
        return new Color(red, green, blue);
    }

    public static float[] RGB2HSL(Color color) {
        float r = (float)color.getRed() / 255.0F;
        float g = (float)color.getGreen() / 255.0F;
        float b = (float)color.getBlue() / 255.0F;
        float cmax = Math.max(r, Math.max(g, b));
        float cmin = Math.min(r, Math.min(g, b));
        float delta = cmax - cmin;
        float hue;
        if (delta == 0.0F) {
            hue = 0.0F;
        } else if (cmax == r) {
            hue = 60.0F * ((g - b) / delta);
        } else if (cmax == g) {
            hue = 60.0F * ((b - r) / delta + 2.0F);
        } else {
            hue = 60.0F * ((r - g) / delta + 4.0F);
        }

        if (hue < 0.0F) {
            hue += 360.0F;
        }

        float saturation;
        if (cmax == 0.0F) {
            saturation = 0.0F;
        } else {
            saturation = delta / cmax;
        }

        return new float[]{hue, saturation, cmax};
    }

    private static Color[] getDifferentColor(int number){
        Color[] cs = new Color[number];
        for (int i = 0; i < number; i++) {
            cs[i] = HSL2RGB((float) (360 * i) / number, 1F, 1F);
        }
        return cs;
    }
}
