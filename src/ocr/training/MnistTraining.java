package ocr.training;

import common.Config;
import java.io.IOException;
import ocr.feature_extraction.FeatureExtraction;
import ocr.feature_extraction.FeatureExtractionBuilder;
import ocr.feature_extraction.HorizontalCelledProjection;
import ocr.feature_extraction.VerticalCelledProjection;
import ocr.neural_network.NeuralNetwork;
import ocr.neural_network.NeuralNetworkBuilder;
import ocr.training.mnist.MnistManager;

public class MnistTraining {
    
    private static final int numTestSamples = 1000;

    public static void main(String[] args) throws IOException{
        
        double[][] actual = new double[numTestSamples][];
        double[][] ideal = new double[numTestSamples][];
        MnistManager m = new MnistManager(Config.MNIST_TRAIN_IMAGES, Config.MNIST_TRAIN_LABELS);
        
        FeatureExtraction fe = FeatureExtractionBuilder
                                .create()
                                .children(new HorizontalCelledProjection(4), 
                                          new VerticalCelledProjection(4))
                                .build();
        
        for(int i = 1; i <= numTestSamples; ++i) {
            // Get Pixel Matrix
            m.setCurrent(i);
            int[][] image = m.readPixelMatrix();
            
            fe.setPixelMatrix(image);
            fe.compute();
            
            // Add to Training Data
            double[] idealVector = new double[Config.OUTPUT_NEURONS];
            idealVector[m.readLabel()] = 1;
            
            actual[i-1] = fe.getFeatureVector();
            ideal[i-1] = idealVector;
        }
        
        NeuralNetwork nn = NeuralNetworkBuilder
                                .create()
                                .inputNeurons(48)
                                .hiddenNeurons(40)
                                .outputNeurons(Config.OUTPUT_NEURONS)
                                .training(actual, ideal)
                                .build();
        
        nn.persistNetwork();
    }
    
}