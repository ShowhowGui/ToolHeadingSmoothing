import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.ArrayList;
import java.util.List;

public class SmoothLoss implements ILossFunction {
    private final int traLen;
    private final double lambda;

    public SmoothLoss(int traLen, double lambda) {
        if (traLen < 5) {
            throw new IllegalArgumentException("The length of trajectory must greater than or equal to 5.");
        }
        this.traLen = traLen;
        this.lambda = lambda;
    }

    @Override
    public double computeScore(INDArray indArray, INDArray indArray1,
                               IActivation iActivation, INDArray indArray2, boolean b) {
        double loss = computeScoreArray(indArray, indArray1, iActivation, indArray2).sumNumber().doubleValue();
        return (b) ? loss / indArray.size(0) : loss;
    }

    @Override
    public INDArray computeScoreArray(INDArray indArray, INDArray indArray1,
                                      IActivation iActivation, INDArray indArray2) {
        // initialisation
        INDArray predictions = iActivation.getActivation(indArray1.dup(), true);
        List<INDArray> keyInfo = getKeyInfo(predictions);

        // output
        return getScoreArray(indArray, predictions, keyInfo);
    }

    @Override
    public INDArray computeGradient(INDArray indArray, INDArray indArray1,
                                    IActivation iActivation, INDArray indArray2) {
        // apply activation function to get actual predictions
        INDArray predictions = iActivation.getActivation(indArray1.dup(), true);

        // compute the gradient of regularisation
        List<INDArray> keyInfo = getKeyInfo(predictions);

        // output
        return getGradientInfo(indArray, predictions, keyInfo);
    }

    @Override
    public Pair<Double, INDArray> computeGradientAndScore(INDArray indArray, INDArray indArray1,
                                                          IActivation iActivation, INDArray indArray2, boolean b) {
        // apply activation function to get actual predictions
        INDArray predictions = iActivation.getActivation(indArray1.dup(), true);

        // compute the gradient of regularisation
        List<INDArray> keyInfo = getKeyInfo(predictions);

        // compute score
        double loss = getScoreArray(indArray, predictions, keyInfo).sumNumber().doubleValue();
        double score = (b) ? loss / indArray.size(0) : loss;

        // compute gradient
        INDArray grad = getGradientInfo(indArray, predictions, keyInfo);

        return new Pair<>(score, grad);
    }

    @Override
    public String name() {
        return "smoothLoss";
    }

    /**
     * compute the loss value
     * @param labels: the real data
     * @param predictions: the predicted data
     * @param keyInfo: the key information for loss and gradient computation
     * @return MSE_REG:
     *      mse - the reconstruction error
     *      reg - the absolute value of heading change
     */
    public double[] get_MSE_REG(INDArray labels, INDArray predictions, List<INDArray> keyInfo){
        INDArray[] mseLoss_absDeltaTheta = getLossInfo(labels, predictions, keyInfo);
        return new double[]{mseLoss_absDeltaTheta[0].getDouble(0), mseLoss_absDeltaTheta[1].getDouble(0)};
    }

    /**
     * compute the loss value
     * @param labels: the real data
     * @param predictions: the predicted data
     * @param keyInfo: the key information for loss and gradient computation
     * @return loss: MSE + LAMBDA * MAE
     */
    private INDArray getScoreArray(INDArray labels, INDArray predictions, List<INDArray> keyInfo){
        INDArray[] mseLoss_absDeltaTheta = getLossInfo(labels, predictions, keyInfo);
        return mseLoss_absDeltaTheta[0].add(mseLoss_absDeltaTheta[1].mul(lambda)).divi(labels.size(1));
    }

    /**
     * compute the value of the loss function, including reconstruction error and heading difference
     * @param labels: the real data
     * @param prediction: the estimated data
     * @param keyInfo: the key information for loss and gradient computation
     * @return lossInfo:
     *      mseLoss - the MSE of labels and predictions
     *      absDeltaTheta - the heading difference
     */
    private INDArray[] getLossInfo(INDArray labels, INDArray prediction, List<INDArray> keyInfo){
        // compute the MSE
        INDArray error = labels.sub(prediction);
        INDArray mseLoss = error.mul(error).sum(true, 1);

        // compute the MAE of heading
        INDArray crossProductAbs = Transforms.abs(keyInfo.get(0));
        INDArray dotProductAbs = Transforms.abs(keyInfo.get(1)).add(1e-10);
        INDArray absDeltaTheta = Transforms.atan(crossProductAbs.div(dotProductAbs)).sum(true, 1);

        // output
        return new INDArray[]{mseLoss, absDeltaTheta};
    }

    /**
     * get the necessary for computing the gradient
     * @param predictions: the estimated data
     * @param keyInfo: the key information for loss and gradient computation
     * @return grad: the gradient of the loss function
     */
    private INDArray getGradientInfo(INDArray labels, INDArray predictions, List<INDArray> keyInfo){
        INDArray gradMSE = predictions.sub(labels).mul(2);

        INDArray mulAlphaBeta = keyInfo.get(0).mul(keyInfo.get(1));
        INDArray signFun = Transforms.sign(mulAlphaBeta);  // batchSize * (featureSize - 2)

        INDArray signFunLeft = signFun.get(NDArrayIndex.all(), NDArrayIndex.interval(0, traLen - 4));
        INDArray signFunMiddle = signFun.get(NDArrayIndex.all(), NDArrayIndex.interval(1, traLen - 3));
        INDArray signFunRight = signFun.get(NDArrayIndex.all(), NDArrayIndex.interval(2, traLen - 2));

        INDArray deltaSignFun1 = signFunMiddle.sub(signFunLeft);  // batchSize * (featureSize - 4)
        INDArray deltaSignFun2 = signFunRight.sub(signFunMiddle);  // batchSize * (featureSize - 4)

        INDArray dx1Middle = keyInfo.get(2).get(NDArrayIndex.all(), NDArrayIndex.interval(1, traLen - 3));
        INDArray dy1Middle = keyInfo.get(3).get(NDArrayIndex.all(), NDArrayIndex.interval(1, traLen - 3));
        INDArray dx2Middle = keyInfo.get(4).get(NDArrayIndex.all(), NDArrayIndex.interval(1, traLen - 3));
        INDArray dy2Middle = keyInfo.get(5).get(NDArrayIndex.all(), NDArrayIndex.interval(1, traLen - 3));

        // define the gradient of the regularisation term
        INDArray gradIncrement = predictions.mul(0);
        INDArray gradX = deltaSignFun2.mul(dy2Middle).sub(deltaSignFun1.mul(dy1Middle));
        INDArray gradY = deltaSignFun1.mul(dx1Middle).sub(deltaSignFun2.mul(dx2Middle));
        gradIncrement.get(NDArrayIndex.all(), NDArrayIndex.interval(2, traLen - 2)).assign(gradX);
        gradIncrement.get(NDArrayIndex.all(), NDArrayIndex.interval(traLen + 2, 2 * traLen - 2)).assign(gradY);

        // output
        return gradMSE.add(gradIncrement.mul(lambda));
    }

    /**
     * compute the key information for loss and gradient computation
     * @param predictions: the prediction value; batchSize * featureSize
     * @return keyInfo:
     *      crossProduct - cross product (alpha); batchSize * (featureSize - 2)
     *      dotProduct - dot product (beta); batchSize * (featureSize - 2)
     *      dx1 - middleX sub leftX; batchSize * (featureSize - 2)
     *      dy1 - middleY sub leftY; batchSize * (featureSize - 2)
     *      dx2 - rightX sub middleX; batchSize * (featureSize - 2)
     *      dy2 - rightY sub middleY; batchSize * (featureSize - 2)
     */
    private List<INDArray> getKeyInfo(INDArray predictions){
        // data preprocessing
        INDArray xLeft = predictions.get(NDArrayIndex.all(), NDArrayIndex.interval(0, traLen-2));
        INDArray xMiddle = predictions.get(NDArrayIndex.all(), NDArrayIndex.interval(1, traLen-1));
        INDArray xRight = predictions.get(NDArrayIndex.all(), NDArrayIndex.interval(2, traLen));

        INDArray yLeft = predictions.get(NDArrayIndex.all(), NDArrayIndex.interval(traLen, 2 * traLen-2));
        INDArray yMiddle = predictions.get(NDArrayIndex.all(), NDArrayIndex.interval(traLen + 1, 2 * traLen-1));
        INDArray yRight = predictions.get(NDArrayIndex.all(), NDArrayIndex.interval(traLen + 2, 2 * traLen));

        // compute differences
        INDArray dx1 = xMiddle.sub(xLeft);
        INDArray dy1 = yMiddle.sub(yLeft);
        INDArray dx2 = xRight.sub(xMiddle);
        INDArray dy2 = yRight.sub(yMiddle);

        // compute the crossProduct (alpha)
        INDArray crossProduct = dx2.mul(dy1).sub(dx1.mul(dy2));

        // compute the doteProduct (beta)
        INDArray dotProduct = dx1.mul(dx2).add(dy1.mul(dy2));

        // output
        List<INDArray> keys = new ArrayList<>();
        keys.add(crossProduct);
        keys.add(dotProduct);
        keys.add(dx1);
        keys.add(dy1);
        keys.add(dx2);
        keys.add(dy2);
        return keys;
    }
}
