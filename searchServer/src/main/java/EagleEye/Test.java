//package EagleEye;
//
//import net.semanticmetadata.lire.aggregators.AbstractAggregator;
//import net.semanticmetadata.lire.aggregators.BOVW;
//import net.semanticmetadata.lire.builders.DocumentBuilder;
//import net.semanticmetadata.lire.imageanalysis.features.global.AutoColorCorrelogram;
//import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
//import net.semanticmetadata.lire.imageanalysis.features.global.EdgeHistogram;
//import net.semanticmetadata.lire.imageanalysis.features.global.FCTH;
//import net.semanticmetadata.lire.imageanalysis.features.global.centrist.SimpleCentrist;
//import net.semanticmetadata.lire.imageanalysis.features.global.centrist.SpatialPyramidCentrist;
//import net.semanticmetadata.lire.imageanalysis.features.global.joint.JointHistogram;
//import net.semanticmetadata.lire.imageanalysis.features.local.opencvfeatures.CvSiftExtractor;
//import net.semanticmetadata.lire.imageanalysis.features.local.opencvfeatures.CvSurfExtractor;
//import net.semanticmetadata.lire.indexers.parallel.ParallelIndexer;
//
//import java.io.File;
//
///**
// * Created by neetp7k9 on 2016/03/04.
// */
//public class Test {
//
//    public static void main(String argv[]){
//        System.out.print(System.getProperty("java.library.path"));
//        Class<? extends AbstractAggregator> aggregatorClass = BOVW.class;
//        Class localFeatureClass = CvSiftExtractor.class;
//        String globalIndexPath = "/Users/neetp7k9/Mafia/spark/EagleEye/index/global";
//        String localIndexPath = "/Users/neetp7k9/Mafia/spark/EagleEye/index/local";
//        String imageList = "/Users/neetp7k9/Mafia/RubyGrape/imageList";
//
//        int numOfDocsForCodebooks = 500;
//        int[] numOfClusters = new int[] {32, 64, 128, 256};
//        ParallelIndexer parallelIndexer;
//        parallelIndexer = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, globalIndexPath, new File(imageList), numOfClusters, numOfDocsForCodebooks, aggregatorClass);
//        parallelIndexer.addExtractor(CEDD.class);
//        parallelIndexer.addExtractor(EdgeHistogram.class);
//        parallelIndexer.addExtractor(SimpleCentrist.class);
//        parallelIndexer.addExtractor(SpatialPyramidCentrist.class);
//        parallelIndexer.addExtractor(FCTH.class);
//        parallelIndexer.addExtractor(AutoColorCorrelogram.class);
//        parallelIndexer.addExtractor(JointHistogram.class);
//        parallelIndexer.run();
//
//        parallelIndexer = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, localIndexPath, new File(imageList), numOfClusters, numOfDocsForCodebooks, aggregatorClass);
//        parallelIndexer.addExtractor(CvSiftExtractor.class);
//        parallelIndexer.addExtractor(CvSurfExtractor.class);
//        parallelIndexer.run();
//
//    }
//}
