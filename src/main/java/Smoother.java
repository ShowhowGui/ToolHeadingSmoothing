import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

public class Smoother {
    public static double[] getSmoothed(double[] headingDeg){
        // get relative horizontal trajectory
        Pair<DataSet, INDArray> horizontalData = getHorizontalTra(headingDeg);

        // train the model
        double lambda = 1;
        double learningRate = 1e-3;
        int epochs = 500;
        MultiLayerNetwork model = trajectorySmoothing(horizontalData, headingDeg.length, lambda, learningRate, epochs);

        // get smoothed heading
        double[] smoothTra = model.output(horizontalData.getFirst().getFeatures()).toDoubleMatrix()[0];
        return getSmoothHeading(smoothTra, headingDeg.length, headingDeg[0]);
    }

    /**
     * infer the horizontal trajectory data based on the heading
     * @param heading: the heading in degree
     * @return
     *      dataSet: trajectory in a specific format
     *      indArray: the original trajectory for initialising model
     */
    private static Pair<DataSet, INDArray> getHorizontalTra(double[] heading){
        double[][] horizontalPosition = new double[1][heading.length * 2];
        double headingRad;
        double x = 0;
        double y = 0;
        for (int i = 0; i < heading.length; i++) {
            headingRad = Math.toRadians(heading[i]);
            x += Math.sin(headingRad);
            y += Math.cos(headingRad);
            horizontalPosition[0][i] = x;
            horizontalPosition[0][i + heading.length] = y;
        }

        INDArray indArray = Nd4j.create(horizontalPosition);
        INDArray inputData = Nd4j.create(new double[][]{{1}});
        DataSet dataSet = new DataSet(inputData, indArray);
        return new Pair<>(dataSet, indArray);
    }

    /**
     * train the model to smooth the trajectory
     * @param horizontalData: includes the trajectory data
     * @param traLen: the length of the trajectory
     * @param lambda: the weight parameter
     * @param learningRate: the learning rate of the model
     * @param epochs: the number of epochs
     * @return model: the trained model
     */
    private static MultiLayerNetwork trajectorySmoothing(Pair<DataSet, INDArray> horizontalData, int traLen,
                                         double lambda, double learningRate, int epochs){
        MultiLayerNetwork model = NeuralNetwork.getNeuralNetwork(traLen, lambda, learningRate);
        model.init();
        model.getLayer(0).setParam("W", horizontalData.getSecond());
        for (int i = 0; i < epochs; i++) {
            model.fit(horizontalData.getFirst());
        }
        return model;
    }

    private static double[] getSmoothHeading(double[] smoothTra, int traLen, double startHeading){
        double[] heading = new double[traLen];
        double[] continuousHeading = new double[traLen];
        heading[0] = startHeading;
        continuousHeading[0] = startHeading;
        for (int i = 1; i < traLen; i++) {
            double dX = smoothTra[i] - smoothTra[i - 1];
            double dY = smoothTra[i + traLen] - smoothTra[i + traLen - 1];
            double dist = Math.sqrt(dX * dX + dY * dY);
            double angle = Math.toDegrees(Math.acos(dY / dist));
            double angle_real = (dX < 0) ? 360 - angle : angle;
            heading[i] = angle_real;
        }
        for (int i = 1; i < traLen; i++){
            double deltaHeading = heading[i] - heading[i - 1];
            if (deltaHeading < -180){
                deltaHeading += 360;
            } else if (deltaHeading > 180){
                deltaHeading -= 360;
            }
            continuousHeading[i] = deltaHeading + continuousHeading[i - 1];
        }
        return continuousHeading;
    }
}
