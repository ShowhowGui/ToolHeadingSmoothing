import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Visualisation extends JPanel {
    private final Map<String, double[][]> data;
    private String title = "";
    private String xLabel = "";
    private String yLabel = "";
    private Font font = new Font("Serif", Font.PLAIN, 25);
    private int yTickCount = 10;
    private int xTickCount = 10;
    private float lineWidth = 5.0F;
    private boolean isLegend = false;
    private boolean isWithGrid = true;
    private int positionLegend = 0;

    public Visualisation(Map<String, double[][]> data) {this.data = data;}

    public void setTitle(String title) {this.title = title;}

    public void setLabel(String xLabel, String yLabel) {
        this.xLabel = xLabel;
        this.yLabel = yLabel;
    }

    public void setFont(Font font) {this.font = font;}
    public void setIsWithGrid(boolean isWithGrid) {this.isWithGrid = isWithGrid;}
    public void setLineWidth(float lineWidth) {this.lineWidth = lineWidth;}

    /**
     * set the position of legend
     * @param positionLegend
     *      0: isLegend=false
     *      1: upper left
     *      2: upper right
     *      3: lower left
     *      4: lower right
     */
    public void setPositionLegend(int positionLegend) {
        if (positionLegend > 0){
            this.isLegend = true;
            this.positionLegend = positionLegend;
        } else {
            this.isLegend = false;
        }
    }

    public void setTickCount(int xTickCount, int yTickCount) {
        this.xTickCount = xTickCount;
        this.yTickCount = yTickCount;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // set font metrics
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        // draw white background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.BLACK);

        // get figure information
        double maxX = Double.MIN_VALUE; double minX = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE; double minY = Double.MAX_VALUE;

        String[] keys =new String[data.size()];
        int maxKeyW = 0;
        int counter = 0;
        for (Map.Entry<String, double[][]> entry : data.entrySet()) {
            keys[counter++] = entry.getKey();

            if (maxKeyW < fm.stringWidth(entry.getKey())) {
                maxKeyW = fm.stringWidth(entry.getKey());
            }

            double[][] item = entry.getValue();
            for (int i = 0; i < item[0].length; i++) {
                maxX = Math.max(maxX, item[0][i]);
                minX = Math.min(minX, item[0][i]);
                maxY = Math.max(maxY, item[1][i]);
                minY = Math.min(minY, item[1][i]);
            }
        }

        // get y ticks and y label
        String[] yTicks = new String[yTickCount];
        int maxYTicksW = 0;
        for (int i = 0; i < yTickCount; i++) {
            yTicks[i] = String.format("%.1f", minY + (maxY - minY) * i / yTickCount);
            if (maxYTicksW < fm.stringWidth(yTicks[i])) {
                maxYTicksW = fm.stringWidth(yTicks[i]);
            }
        }

        // get y labels
        String[] xTicks = new String[xTickCount];
        int maxXTicksW = 0;
        for (int i = 0; i < xTickCount; i++) {
            xTicks[i] = String.format("%.1f", minX + (maxX - minX) * i / xTickCount);
            if (maxXTicksW < fm.stringWidth(xTicks[i])) {
                maxXTicksW = fm.stringWidth(xTicks[i]);
            }
        }

        // get frame information
        int w = getWidth();
        int h = getHeight();
        int mP = 25;
        int legendItemHei = 5;
        int legendItemWid = mP * 5;
        int fHeight = fm.getHeight();
        int ho = (int) ((double) fHeight / 3);
        int vo = (int) ((double) fHeight / 6);
        int xOnY = (xLabel.isEmpty()) ? 0 : (fHeight + 5);
        int yOnX = (yLabel.isEmpty()) ? 0 : (fHeight + 5);
        int tP = (title.isEmpty()) ? 0 : (fHeight + 5);


        // get chart information
        int chartW = w - mP - Math.max(mP, maxXTicksW / 2) - yOnX - maxYTicksW - ho;
        int chartH = h - mP * 2 - xOnY - fHeight - tP;

        // draw y ticks and y label
        for (int i = 0; i < yTickCount; i++) {
            int y = h - mP - xOnY - fHeight - i * chartH / (yTickCount - 1);
            g2d.drawString(yTicks[i], mP + yOnX + maxYTicksW - fm.stringWidth(yTicks[i]), y + fHeight / 2 - vo);

            if ((i > 0) && (i < yTickCount - 1)){
                if (this.isWithGrid){
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.drawLine(mP + yOnX + maxYTicksW + ho, y, w - Math.max(mP, maxXTicksW / 2), y);
                    g2d.setColor(Color.BLACK);
                }
            } else {
                g2d.setColor(Color.BLACK);
                g2d.drawLine(mP + yOnX + maxYTicksW + ho, y, w - Math.max(mP, maxXTicksW / 2), y);
                g2d.setColor(Color.BLACK);
            }
        }

        // draw x ticks and x label
        for (int i = 0; i < xTickCount; i++) {
            int x = mP + yOnX + maxYTicksW + ho + i * chartW / (xTickCount - 1);
            g2d.drawString(xTicks[i], x - fm.stringWidth(xTicks[i]) / 2, h - mP - xOnY);

            if ((i > 0) && (i < xTickCount - 1)){
                if (this.isWithGrid){
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.drawLine(x, h - mP - xOnY - fHeight, x, mP + tP);
                    g2d.setColor(Color.BLACK);
                }
            } else {
                g2d.setColor(Color.BLACK);
                g2d.drawLine(x, h - mP - xOnY - fHeight, x, mP + tP);
                g2d.setColor(Color.BLACK);
            }
        }

        // draw label, title
        int xLabelWidth = fm.stringWidth(xLabel);
        g2d.drawString(xLabel, mP + yOnX + maxYTicksW + ho + (chartW - xLabelWidth) / 2, h - mP);

        int yLabelWidth = fm.stringWidth(yLabel);
        AffineTransform original = g2d.getTransform();
        g2d.rotate(-Math.PI / 2);
        g2d.setColor(Color.BLACK);
        g2d.drawString(yLabel, -1 * (mP + tP + (chartH + yLabelWidth) / 2), mP + yOnX - Math.min(ho, mP - 5));
        g2d.setTransform(original);

        int tLabelWidth = fm.stringWidth(title);
        g2d.drawString(title, (w - tLabelWidth) / 2, mP + fHeight - Math.min(ho, mP - 5));

        // draw lines
        g2d.setStroke(new BasicStroke(lineWidth));
        Color[] colors = ColorUtils.getGoodColors(data.size());
        for (int i = 0; i < colors.length; i++) {
            g2d.setColor(colors[i]);
            int[] xPoints = new int[data.get(keys[i])[0].length];
            int[] yPoints = new int[data.get(keys[i])[0].length];
            for (int j = 0; j < data.get(keys[i])[0].length; j++) {
                xPoints[j] = (int) (mP + yOnX + maxYTicksW + ho + chartW * (data.get(keys[i])[0][j] - minX) / (maxX - minX));
                yPoints[j] = (int) (h - mP - xOnY - fHeight - chartH * (data.get(keys[i])[1][j] - minY) / (maxY - minY));
            }
            g2d.drawPolyline(xPoints, yPoints, xPoints.length);
        }

        // draw legend
        if (this.isLegend){
            int legendStartX, legendStartY;
            legendStartY = switch (positionLegend) {
                case 1 -> {
                    legendStartX = mP * 2 + yOnX + maxYTicksW;
                    yield mP + mP / 2 + tP;
                }
                case 2 -> {
                    legendStartX = w - mP * 4 - mP / 2 - legendItemWid - maxKeyW;
                    yield mP + mP / 2 + tP;
                }
                case 3 -> {
                    legendStartX = mP * 2 + yOnX + maxYTicksW;
                    yield h - mP * 2 - xOnY - fHeight * (data.size() + 1) - (data.size() - 1) * legendItemHei;
                }
                case 4 -> {
                    legendStartX = w - mP * 4 - mP / 2 - legendItemWid - maxKeyW;
                    yield h - mP * 2 - xOnY - fHeight * (data.size() + 1) - (data.size() - 1) * legendItemHei;
                }
                default -> throw new IllegalArgumentException("The position should be 0, 1, 2, or 3.");
            };

            g2d.setColor(Color.WHITE);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g2d.fillRoundRect(legendStartX, legendStartY, mP * 3 + legendItemWid + maxKeyW,
                    mP / 2 + fHeight * data.size() + (data.size() - 1) * legendItemHei, mP, mP);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2d.setColor(Color.LIGHT_GRAY);
            int legendBoxWidth = 3;
            g2d.setStroke(new BasicStroke(legendBoxWidth));
            g2d.drawRoundRect(legendStartX, legendStartY, mP * 3 + legendItemWid + maxKeyW,
                    mP / 2 + fHeight * data.size() + (data.size() - 1) * legendItemHei, mP, mP);
            g2d.setStroke(new BasicStroke(lineWidth));
            for (int i = 0; i < data.size(); i++) {
                int iY = legendStartY + fHeight * i + fHeight / 2 + mP / 2;
                g2d.setColor(colors[i]);
                g2d.drawLine(legendStartX + mP, iY, legendStartX + mP + legendItemWid, iY);
                g2d.setColor(Color.BLACK);
                g2d.drawString(keys[i], legendStartX + mP * 2 + legendItemWid, iY + vo);
            }
        }
    }

    public static Map<String, double[][]> dataPreprocessing(Map<String, double[]> data) {
        Map<String, double[][]> newData = new HashMap<>();
        for (Map.Entry<String, double[]> entry : data.entrySet()){
            double[] gValue = entry.getValue();
            double[][] newDataDouble = new double[2][gValue.length];
            for (int i = 0; i < gValue.length; i++){
                newDataDouble[0][i] = i;
                newDataDouble[1][i] = gValue[i];
            }
            newData.put(entry.getKey(), newDataDouble);
        }
        return newData;
    }

    public static void saveFrameAsImage(JFrame frame, String filePath) {
        Container content = frame.getContentPane();

        // 创建 BufferedImage
        BufferedImage image = new BufferedImage(
                content.getWidth(),
                content.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = image.createGraphics();
        content.paint(g2d);
        g2d.dispose();

        try {
            ImageIO.write(image, "png", new File(filePath));
            System.out.println("Saved JFrame as image: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void main(String[] args) {
//        Map<String, double[][]> data = new HashMap<>();
//        data.put("1", new double[][]{{1,2,3,4,5},{1,2,3,4,7}});
//        data.put("2", new double[][]{{1,2,3,4,5},{3,2,1,4,6}});
//        data.put("3", new double[][]{{1,2,3,4,5},{5,5,5,4,6}});
//        JFrame frame = new JFrame("Visualisation");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(1200, 800);
//        Visualisation v = new Visualisation(data);
//        v.setLabel("X label", "Y label");
//        v.setPositionLegend(3);
//        v.setTitle("Title");
//        v.setIsWithGrid(false);
//        v.setLineWidth(6);
//        v.setTickCount(15, 8);
//        frame.add(v);
//        frame.setVisible(true);
//    }
}
