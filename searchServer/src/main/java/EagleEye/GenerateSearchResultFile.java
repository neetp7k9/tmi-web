//package EagleEye;
//import net.semanticmetadata.lire.builders.DocumentBuilder;
//import net.semanticmetadata.lire.searchers.ImageSearchHits;
//import org.apache.lucene.index.IndexReader;
//
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * Created by neetp7k9 on 2016/02/14.
// */
//
//public class GenerateSearchResultFile {
//    String[] fieldNames = new String[]{"Feature Name", "Hashing Type", "Index Time(s)", "Search Time(s)", "Total Time(s)", "Precision(total)", "Recall(total)", "Rotation Precision", "Resize Precision", "Noise Precision"};
//    ArrayList<Object[]> allFeatureStats = new ArrayList<Object[]>();
//    String fileName;
//
//    public GenerateSearchResultFile(String fileName) {
//        this.fileName = fileName;
//    }
//
//    public void saveOneFeatureStats(FeatureSearchResult featureSearchResult) {
//        Object[] searchResultContent = new Object[]{featureSearchResult.featureName, featureSearchResult.hashingTypeName, featureSearchResult.indexTime, featureSearchResult.searchTime, featureSearchResult.indexTime + featureSearchResult.searchTime,
//                featureSearchResult.precisionSum / featureSearchResult.numOfAllImages, featureSearchResult.recallSum / featureSearchResult.numOfAllImages, featureSearchResult.allImageRotationCount[1] / Double.valueOf(FeatureSearchResult.allImageRotationCount[0] + FeatureSearchResult.allImageRotationCount[1]),
//                featureSearchResult.allImageResizeCount[1] / Double.valueOf(FeatureSearchResult.allImageResizeCount[0] + FeatureSearchResult.allImageResizeCount[1]),
//                featureSearchResult.allImageNoiseCount[1] / Double.valueOf(FeatureSearchResult.allImageNoiseCount[0] + FeatureSearchResult.allImageNoiseCount[1])
//        };
//        allFeatureStats.add(searchResultContent);
//    }
//
//    public void writeStatsToFile() throws IOException {
//        BufferedWriter outFile = new BufferedWriter(new FileWriter(fileName));
//        for (String fieldName : fieldNames)
//            outFile.write(fieldName + " ");
//        outFile.write("\n");
//
//        for (Object[] searchResultContent : allFeatureStats) {
//            for (Object item : searchResultContent)
//                outFile.write(String.valueOf(item) + " ");
//            outFile.write("\n");
//        }
//        outFile.close();
//    }
//}