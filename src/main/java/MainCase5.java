import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainCase5 {
    private static final int TOTAL_LENGTH = 60;

    public static void main(String[] args) {
        double[] headingDeg = new double[]{13.00, 13.00, 13.00, 12.81, 12.76, 12.77, 12.83, 12.73, 12.68, 12.61, 12.46,
                12.58, 12.47, 12.54, 12.61, 12.23, 9.00, 5.24, 1.81, -2.15, -4.01, -3.86, -3.97, -3.81, -3.97, -3.94,
                -3.94, 3.88, -2.25, -3.34, -2.41, -2.75, -2.10, -3.29, -2.06, -5.67, -5.51, -5.57, -5.36, -5.15, -5.21,
                -4.84, -4.80, -4.53, -4.86, -4.92, -5.09, -5.07, -5.30, -5.34, -5.26, -8.94, -11.41, -12.31, -12.90,
                -12.92, -12.81, -12.73, -12.51, -12.70, -12.33, -12.48, -12.32, -12.33, -12.14, -12.34, -12.17, -12.32,
                -12.26, -12.10, -12.36, -12.07, -12.20, -12.20, -12.11, -12.22, -12.14, -12.18, -12.37, -12.12, -12.31,
                -12.11, -12.27, -12.35, -12.24, -12.09, -12.29, -12.11, -12.33, -12.20, -12.07, -12.32, -12.15, -12.30,
                -12.20, -12.21, -12.10, -12.32, -12.13, -12.26, -12.39, -12.14, -12.24, -12.05, -12.18};
        double[] smoothedHeadingDeg = Smoother.getSmoothed(headingDeg);

        Map<String, double[]> data = new HashMap<>();
        data.put("Real heading", headingDeg);
        data.put("Smoothed heading", smoothedHeadingDeg);
        JFrame frame = new JFrame("Case5");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        Visualisation v = new Visualisation(Visualisation.dataPreprocessing(data));
        v.setLabel("", "Heading (deg)");
        v.setPositionLegend(2);
        v.setIsWithGrid(false);
        v.setLineWidth(6);
        v.setFont(new Font("Serif", Font.PLAIN, 30));
        frame.add(v);
        frame.setVisible(true);
        Visualisation.saveFrameAsImage(frame, "Case5.png");
    }
}