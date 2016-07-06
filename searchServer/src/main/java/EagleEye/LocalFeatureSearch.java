package EagleEye; /**
 *
 *   index
 *   search
 *
 */

import net.semanticmetadata.lire.aggregators.AbstractAggregator;
import net.semanticmetadata.lire.aggregators.Aggregator;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.builders.GlobalDocumentBuilder.HashingMode;

import net.semanticmetadata.lire.indexers.hashing.BitSampling;
import net.semanticmetadata.lire.indexers.hashing.LocalitySensitiveHashing;
import net.semanticmetadata.lire.indexers.hashing.MetricSpaces;
import net.semanticmetadata.lire.indexers.parallel.ParallelIndexer;
import net.semanticmetadata.lire.searchers.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.ArrayList;


// localFeatureSearch

public class LocalFeatureSearch {

    private String indexPath;
    private String imagePath;
    private ImageSearcher searcher;
    private Class localFeatureClass;
    private int numOfOneLabel;
    private Double indexingTime;
    private Double searchingTime;

    public LocalFeatureSearch(Class localFeatureClass, String indexPath, String imagePath, int numOfOneLabel) {
        this.localFeatureClass = localFeatureClass;
        this.indexPath = indexPath;
        this.imagePath = imagePath;
        this.numOfOneLabel = numOfOneLabel;
    }

    public void index(int numOfResult, int numOfClusters, int numOfDocsForCodebooks, Class<? extends AbstractAggregator > aggregator) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        System.out.println("Feature : " + localFeatureClass.getName());
        long start = System.currentTimeMillis();

        ParallelIndexer parallelIndexer = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, indexPath, new File(imagePath), numOfClusters, numOfDocsForCodebooks, aggregator);
        parallelIndexer.addExtractor(localFeatureClass);
        parallelIndexer.run();

        IndexReader reader = DirectoryReader.open(new RAMDirectory(FSDirectory.open(Paths.get(indexPath)), IOContext.READONCE));
        searcher = new GenericFastImageSearcher(numOfResult, localFeatureClass, (Aggregator) aggregator.newInstance(), numOfClusters, true, reader, indexPath + ".config");

        System.out.printf("Total time of indexing: %s.\n", convertTime(System.currentTimeMillis() - start));
        indexingTime = (System.currentTimeMillis() - start)/1000.0;
    }
    public void setSearcher(int numOfResult, int numOfClusters, int numOfDocsForCodebooks, Class<? extends AbstractAggregator > aggregator) throws IllegalAccessException, InstantiationException, IOException {
        IndexReader reader = DirectoryReader.open(new RAMDirectory(FSDirectory.open(Paths.get(indexPath)), IOContext.READONCE));
        searcher = new GenericFastImageSearcher(numOfResult, localFeatureClass, (Aggregator) aggregator.newInstance(), numOfClusters, true, reader, indexPath + ".config");
    }
    public FeatureSearchResult singleImageSearchResult(String imageToSearch, Boolean wantToPrint) throws IOException {

        BufferedImage image = ImageIO.read(new FileInputStream(imageToSearch));
        IndexReader reader = DirectoryReader.open(new RAMDirectory(FSDirectory.open(Paths.get(indexPath)), IOContext.READONCE));
        ImageSearchHits hits = searcher.search(image, reader);

        FeatureSearchResult searchResult = new FeatureSearchResult(hits, reader, numOfOneLabel, imageToSearch, localFeatureClass.getName(), HashingMode.None.toString());
        if(wantToPrint)
            printSingleImageSearchResult(searchResult, imageToSearch);
        return searchResult;
    }


//    public void allImageSearchResult(String imageList, GenerateSearchResultFile generateSearchResultFile) throws IOException {
//        ArrayList<String> allImages = new ArrayList<>();
//        BufferedReader br = new BufferedReader(new FileReader(imageList));
//        String line;
//        FeatureSearchResult searchResult = null;
//        while ((line = br.readLine()) != null) {
//            if (line.trim().length() > 3) allImages.add(line.trim());
//        }
//        FeatureSearchResult.initialize(allImages.size());
//        long start = System.currentTimeMillis();
//
//        for (String imageToSearch : allImages)
//        {
//            searchResult = singleImageSearchResult(imageToSearch, false);
//            searchResult.calculatePrecisionAndRecall();
//            searchResult.addToSum();
//        }
//        System.out.printf("Total time of searching: %s.\n", convertTime(System.currentTimeMillis() - start));
//        searchingTime = (System.currentTimeMillis() - start)/1000.0;
//
//        FeatureSearchResult.setIndexTime(indexingTime);
//        FeatureSearchResult.setSearchTime(searchingTime);
//        generateSearchResultFile.saveOneFeatureStats(searchResult);
//
//        Double averagePercision = FeatureSearchResult.getAveragePrecision();
//        Double averageRecall = FeatureSearchResult.getAverageRecall();
//        printAllImagesSearchResult(averagePercision, averageRecall);
//    }

    private void printAllImagesSearchResult(Double averagePrecision, Double averageRecall){
        System.out.println("average precision for localFeature " + localFeatureClass.getName() + " = " + averagePrecision.toString());
        System.out.println("average recall for localFeature " + localFeatureClass.getName() + " = " + averageRecall.toString());
    }

    private void printSingleImageSearchResult(FeatureSearchResult searchResult, String imageToSearch) throws IOException {
        searchResult.getStats();
        String hitFile;
        IndexReader reader = DirectoryReader.open(new RAMDirectory(FSDirectory.open(Paths.get(indexPath)), IOContext.READONCE));
        for (int y = 0; y < searchResult.hits.length(); y++) {
            hitFile = reader.document(searchResult.hits.documentID(y)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
            hitFile = hitFile.substring(hitFile.lastIndexOf('\\') + 1);
            System.out.println(y + ". " + hitFile + " " + searchResult.hits.score(y));
        }
        System.out.println();
        System.out.println("precision = " + searchResult.precision.toString());
        System.out.println("recall = " + searchResult.recall.toString());
    }

    private String convertTime(long time) {
        double h = time / 3600000.0;
        double m = (h - Math.floor(h)) * 60.0;
        double s = (m - Math.floor(m)) * 60;
        return String.format("%s%02d:%02d", (((int) h > 0) ? String.format("%02d:", (int) h) : ""), (int) m, (int) s);
    }

}