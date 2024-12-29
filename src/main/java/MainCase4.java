import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainCase4 {
    private static final int TOTAL_LENGTH = 60;

    public static void main(String[] args) {
        double[] headingDeg = new double[]{13.00, 13.00, 13.00, 12.56, 8.12, 4.67, 1.28, -2.38, -5.54, -7.35, -6.60,
                -6.26, -5.97, -5.92, -5.92, -5.81, -5.83, -5.59, -5.69, -5.56, -5.61, -5.68, -5.79, -5.79, -5.79, -5.70,
                -5.72, -5.53, -5.40, -5.52, -5.30, -5.00, -8.77, -10.50, -10.12, -11.70, -11.48, -13.82, -13.50, -21.51,
                -14.19, -14.39, -14.01, -14.17, -14.01, -14.01, -13.83, -14.02, -13.86, -14.01, -13.94, -13.78, -14.05,
                -13.76, -13.89, -13.89, -13.80, -13.90, -13.82, -13.86, -14.06, -13.81, -14.00, -13.80, -13.95, -14.04,
                -13.92, -13.78, -13.97, -13.80, -14.02, -13.89, -13.75, -14.00, -13.83, -13.99, -13.89, -13.90, -13.79,
                -14.01, -13.82, -13.94, -14.07, -13.82, -13.92, -13.74, -13.86};
        double[] smoothedHeadingDeg = Smoother.getSmoothed(headingDeg);

        Map<String, double[]> data = new HashMap<>();
        data.put("Real heading", headingDeg);
        data.put("Smoothed heading", smoothedHeadingDeg);
        JFrame frame = new JFrame("Case4");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        Visualisation v = new Visualisation(Visualisation.dataPreprocessing(data));
        v.setLabel("", "Heading (deg)");
        v.setPositionLegend(4);
        v.setIsWithGrid(false);
        v.setLineWidth(6);
        v.setFont(new Font("Serif", Font.PLAIN, 30));
        frame.add(v);
        frame.setVisible(true);
        Visualisation.saveFrameAsImage(frame, "Case4.png");
    }
}