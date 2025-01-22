import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainCase1 {
    private static final int TOTAL_LENGTH = 60;

    public static void main(String[] args) {
        double[] headingDeg = new double[]{45.00, 45.00, 45.00, 44.82, 45.04, 44.97, 44.93, 44.99, 44.92, 44.83, 44.84,
                44.97, 45.02, 45.01, 45.02, 44.85, 44.89, 44.90, 44.83, 44.85, 44.88, 44.75, 44.76, 44.91, 44.84, 45.05,
                45.01, 44.91, 44.98, 44.90, 44.98, 45.02, 44.85, 43.70, 40.69, 39.60, 39.18, 38.22, 37.97, 36.50, 36.37,
                31.80, 35.36, 35.40, 35.43, 35.44, 35.42, 35.40, 35.37, 35.59, 35.41, 35.49, 35.36, 35.26, 35.29, 35.17,
                35.07, 35.23, 35.39, 35.28, 35.31, 35.45, 35.36, 35.39, 35.40, 35.43, 35.45, 35.48, 35.38, 35.36, 35.33,
                35.41, 35.25, 35.38, 35.12, 35.15, 35.05, 34.93, 35.09, 35.25, 35.68, 35.72, 35.59, 35.48, 35.34, 35.69,
                36.94, 37.11, 37.02, 36.82, 36.92, 37.11, 37.09, 36.98, 37.17, 37.48, 37.55, 37.93, 37.77};
        double[] smoothedHeadingDeg = Smoother.getSmoothed(headingDeg);

        Map<String, double[]> data = new HashMap<>();
        data.put("Real heading", headingDeg);
        data.put("Smoothed heading", smoothedHeadingDeg);
        JFrame frame = new JFrame("Case1");
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
        Visualisation.saveFrameAsImage(frame, "Case1.png");
    }
}
