import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainCase2 {
    private static final int TOTAL_LENGTH = 60;

    public static void main(String[] args) {
        double[] headingDeg = new double[]{0.00, 0.00, 0.00, -0.44, -4.88, -8.33, -11.72, -15.38, -18.54, -20.35,
                -19.60, -19.26, -18.97, -18.92, -18.92, -18.81, -18.83, -18.59, -18.69, -18.56, -18.61, -18.68, -18.79,
                -18.79, -18.79, -18.70, -18.72, -18.53, -18.40, -18.52, -18.30, -11.09, -19.80, -19.85, -18.78, -18.73,
                -17.88, -18.57, -17.85, -22.47, -18.76, -18.67, -18.73, -18.67, -18.63, -18.71, -18.81, -18.63, -18.85,
                -18.70, -19.20, -24.28, -30.44, -35.96, -41.42, -45.39, -45.96, -45.97, -45.88, -45.99, -46.04, -45.90,
                -46.01, -45.99, -45.94, -45.90, -45.97, -46.07, -46.00, -45.92, -45.89, -45.99, -46.01, -45.97, -45.95,
                -45.91, -46.01, -45.95, -46.00, -45.91, -46.07, -45.92, -45.97};
        double[] smoothedHeadingDeg = Smoother.getSmoothed(headingDeg);

        Map<String, double[]> data = new HashMap<>();
        data.put("Real heading", headingDeg);
        data.put("Smoothed result", smoothedHeadingDeg);
        JFrame frame = new JFrame("Case2");
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
        Visualisation.saveFrameAsImage(frame, "Case2.png");
    }
}
