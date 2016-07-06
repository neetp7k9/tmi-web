//package  EagleEye;
//import net.semanticmetadata.lire.imageanalysis.features.GlobalFeature;
//import net.semanticmetadata.lire.imageanalysis.features.global.AutoColorCorrelogram;
//import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
//import net.semanticmetadata.lire.imageanalysis.features.global.EdgeHistogram;
//import net.semanticmetadata.lire.imageanalysis.features.global.FCTH;
//import net.semanticmetadata.lire.imageanalysis.features.global.centrist.SimpleCentrist;
//import net.semanticmetadata.lire.imageanalysis.features.global.centrist.SpatialPyramidCentrist;
//import net.semanticmetadata.lire.imageanalysis.features.global.joint.JointHistogram;
//import net.semanticmetadata.lire.indexers.hashing.BitSampling;
//import net.semanticmetadata.lire.indexers.hashing.LocalitySensitiveHashing;
//import net.semanticmetadata.lire.searchers.*;
//import net.semanticmetadata.lire.builders.GlobalDocumentBuilder.HashingMode;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class AllFeatureSearch
//{
////    Seacher
////    searcherList.put(HashingMode.BitSampling, BitSamplingImageSearcher.class);
////    searcherList.put(HashingMode.LSH, LshImageSearcher.class);
////    searcherList.put(HashingMode.MetricSpaces, MetricSpacesImageSearcher.class);
////    searcherList.put(HashingMode.None, GenericFastImageSearcher.class);
////    searcherList.put(HashingMode.None, VisualWordsImageSearcher.class);
////    searcherList.put(HashingMode.None, TopDocsImageSearcher.class);
////    searcherList.put(HashingMode.None, SingleNddCeddImageSearcher.class);
////    searcherList.put(HashingMode.None, GenericDocValuesImageSearcher.class);
////    searcherList.put(HashingMode.None, FastOpponentImageSearcher.class);
//
////    Global Feature Class List Element
////    EdgeHistogram.class, //MPEG7  have error with metricSpace
////    CEDD.class,
////    SimpleCentrist.class, //Centrist
////    SpatialPyramidCentrist.class, //Centrist  Could not create hashes, feature vector too long: 7936
////    FCTH.class, //FCTH
////    AutoColorCorrelogram.class, //Correlegram
////    JointHistogram.class //joint
//
//    static String indexPath = "test-index";
//    static String imagePath = "/Users/neetp7k9/Mafia/LIRE/testdata/data/image_from_doc/modified/imageList";
//    static String imageToSearch = "/Users/neetp7k9/Mafia/LIRE/testdata/data/image_from_doc/modified/project_2_image_1_org.png";
//    static String logFilePath = "/Users/neetp7k9/Mafia/LIRE/testdata/data/image_from_doc/log";
//
//    static int numberOfReferencePoints = 2000;
//    static int lenghtOfPostingList = 50;
//    static String hashFileName ="hashFile";
//
//    static int numOfResult = 30;
//    static int numHashedResult = 60;
//    static int numOfOneLabel = 30;
//
//    public static void main(String[] argvs) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
//        Class[] globalFeatureClassList = new Class[]{
//                EdgeHistogram.class, //MPEG7  have error with metricSpace
//                CEDD.class,
//                SimpleCentrist.class, //Centrist
//                SpatialPyramidCentrist.class, //Centrist  Could not create hashes, feature vector too long: 7936
//                FCTH.class, //FCTH
//                AutoColorCorrelogram.class, //Correlegram
//                JointHistogram.class //joint
//        };
//
//        HashMap<HashingMode, Class> searcherList = new HashMap<HashingMode, Class>();
//        searcherList.put(HashingMode.BitSampling, BitSamplingImageSearcher.class);
//        BitSampling.dimensions = 8000;
//        searcherList.put(HashingMode.None, GenericFastImageSearcher.class);
//        searcherList.put(HashingMode.LSH, LshImageSearcher.class);
//        //LocalitySensitiveHashing.dimensions = 8000;  HAVE TO SET TO PUBLIC TO ENABLE LONGER HASH
//        searcherList.put(HashingMode.MetricSpaces, MetricSpacesImageSearcher.class);
//        searchAllGlobal(globalFeatureClassList, searcherList);
//
//    }
//
//    public static void searchAllGlobal(Class[] globalFeatureClassList, HashMap<HashingMode, Class> searcherList) throws InvocationTargetException, IOException, InstantiationException, NoSuchMethodException, IllegalAccessException {
////        for (Class globalFeatureClass : globalFeatureClassList) {
////            for (Map.Entry<HashingMode, Class> entry : searcherList.entrySet()) {
////                HashingMode hashingMode = entry.getKey();
////                Class seacherClass = entry.getValue();
////                System.out.println("Feature : " + globalFeatureClass.getName() + " Hash " + hashingMode.toString() + " searcher " + seacherClass.getName());
////                GlobalFeatureSearch globalFeatureSearch = new GlobalFeatureSearch(globalFeatureClass, indexPath, imagePath, hashingMode, numOfOneLabel);
////
////                globalFeatureSearch.index(seacherClass, numOfResult, numHashedResult, hashFileName, numberOfReferencePoints, lenghtOfPostingList);
////                globalFeatureSearch.singleImageSearchResult(imageToSearch, true);
////
////                GenerateSearchResultFile generateSearchResultFile = new GenerateSearchResultFile(logFilePath);
////                globalFeatureSearch.allImageSearchResult(imagePath, generateSearchResultFile);
////                generateSearchResultFile.writeStatsToFile();
////
////            }
////        }
//        GenerateSearchResultFile generateSearchResultFile = new GenerateSearchResultFile(logFilePath);
//        for (Class globalFeatureClass : globalFeatureClassList) {
//            for (Map.Entry<HashingMode, Class> entry : searcherList.entrySet()) {
//                HashingMode hashingMode = entry.getKey();
//                Class seacherClass = entry.getValue();
//                System.out.println("Feature : " + globalFeatureClass.getName() + " Hash " + hashingMode.toString() + " searcher " + seacherClass.getName());
//                GlobalFeatureSearch globalFeatureSearch = new GlobalFeatureSearch(globalFeatureClass, indexPath, imagePath, hashingMode, numOfOneLabel);
//
//                globalFeatureSearch.index(seacherClass, numOfResult, numHashedResult, hashFileName, numberOfReferencePoints, lenghtOfPostingList);
//                globalFeatureSearch.singleImageSearchResult(imageToSearch, true);
//
//                globalFeatureSearch.allImageSearchResult(imagePath, generateSearchResultFile);
//            }
//        }
//        generateSearchResultFile.writeStatsToFile();
//
//    }
//}
//
