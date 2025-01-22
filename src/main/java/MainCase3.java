import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainCase3 {
    private static final int TOTAL_LENGTH = 60;

    public static void main(String[] args) {
        double[] headingDeg = new double[]{135.00, 135.00, 135.00, 134.81, 134.76, 134.77, 134.83, 134.73, 134.68,
                134.61, 134.46, 134.58, 134.47, 134.54, 134.61, 134.23, 131.00, 127.24, 123.81, 119.85, 117.99, 118.14,
                118.03, 118.19, 118.03, 118.06, 118.06, 118.04, 118.02, 118.04, 117.84, 117.90, 117.77, 117.93, 118.03,
                118.13, 118.24, 118.39, 125.30, 118.90, 120.26, 121.77, 122.83, 124.44, 124.96, 126.74, 120.21, 129.35,
                129.33, 129.48, 129.27, 130.23, 133.09, 133.47, 133.63, 133.72, 133.63, 133.54, 133.43, 133.29, 133.22,
                132.90, 132.89, 132.79, 132.88, 132.94, 133.02, 132.88, 133.10, 132.98, 133.14, 133.13, 133.37, 133.25,
                133.16, 133.16, 133.08, 133.10, 133.01, 133.04, 133.12, 133.02, 133.01, 133.15, 133.13, 133.17, 133.10,
                133.19, 133.07, 133.07, 133.14, 133.09, 133.21, 133.09, 133.14, 133.02, 133.02, 133.05, 133.13, 133.12,
                133.14, 133.11, 133.12, 133.06};
        double[] smoothedHeadingDeg = Smoother.getSmoothed(headingDeg);

        Map<String, double[]> data = new HashMap<>();
        data.put("Real heading", headingDeg);
        data.put("Smoothed heading", smoothedHeadingDeg);
        JFrame frame = new JFrame("Case3");
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
        Visualisation.saveFrameAsImage(frame, "Case3.png");
    }
}
