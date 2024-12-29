import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;

public class NeuralNetwork {
    public static SmoothLoss smoothLoss;
    public static MultiLayerNetwork getNeuralNetwork(int traLen, double lambda, double learningRate) {
        smoothLoss = new SmoothLoss(traLen, lambda);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .updater(new Adam(learningRate))
                .list()
                .layer(new OutputLayer.Builder(smoothLoss)
                        .nIn(1)
                        .nOut(traLen * 2)
                        .hasBias(false)
                        .activation(Activation.IDENTITY)
                        .build()
                ).build();
        return new MultiLayerNetwork(conf);
    }
}
