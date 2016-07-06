package EagleEye;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.builders.GlobalDocumentBuilder.HashingMode;
import net.semanticmetadata.lire.imageanalysis.features.GlobalFeature;

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


// GlobalFeatureSearch

public class GlobalFeatureSearch {

    private String indexPath;
    private String imagePath;
    private ImageSearcher searcher;
    private HashingMode hashingMode;
    private Class<? extends GlobalFeature> globalFeatureClass;
    private int numOfOneLabel;
    private Double indexingTime;
    private Double searchingTime;

    public GlobalFeatureSearch(Class<? extends GlobalFeature> globalFeatureClass, String indexPath, String imagePath, HashingMode hashingMode, int numOfOneLabel) {
        this.globalFeatureClass = globalFeatureClass;
        this.indexPath = indexPath;
        this.imagePath = imagePath;
        this.hashingMode = hashingMode;
        this.numOfOneLabel = numOfOneLabel;
    }

    public void index(Class searcherClass,int numOfResult, int numOfHashedResult, String hashFileName, int numberOfReferencePoints, int lenghtOfPostingList) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        System.out.println("Feature : " + globalFeatureClass.getName() + " Hash " + hashingMode.toString());
        long start = System.currentTimeMillis();
        initializeHashing(searcherClass, numOfResult, numOfHashedResult, hashFileName, numberOfReferencePoints, lenghtOfPostingList);
//        ParallelIndexer parallelIndexer = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, indexPath, new File(imagePath), hashingMode);
        ParallelIndexer parallelIndexer = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, indexPath, new File(imagePath), false);
        parallelIndexer.addExtractor(globalFeatureClass);
        parallelIndexer.run();
        System.out.printf("Total time of indexing: %s.\n", convertTime(System.currentTimeMillis() - start));
        indexingTime = (System.currentTimeMillis() - start)/1000.0;
    }

    public void initializeHashing(Class searcherClass,int numOfResult, int numOfHashedResult, String hashFileName, int numberOfReferencePoints, int lenghtOfPostingList) throws IllegalAccessException, InstantiationException, IOException, NoSuchMethodException, InvocationTargetException {
        String fieldName = ((GlobalFeature) globalFeatureClass.newInstance()).getFieldName();
        String hashesFieldName = fieldName + DocumentBuilder.HASH_FIELD_SUFFIX;
        Class[] params = new Class[]{int.class, Class.class};
        File hashFile;
        switch (hashingMode) {
            case BitSampling:
                String bsFileName = hashFileName + "_bs2.obj";
                BitSampling.generateHashFunctions(bsFileName);
                hashFile = new File(bsFileName);
                params = new Class[]{int.class, String.class, String.class, GlobalFeature.class, InputStream.class, int.class};
                searcher = (ImageSearcher) searcherClass.getDeclaredConstructor(params).newInstance(numOfResult, fieldName, hashesFieldName, globalFeatureClass.newInstance(),new FileInputStream(hashFile), numOfHashedResult);
                break;
            case LSH:
                String lshFileName = hashFileName + "_lsh2.obj";
                params = new Class[]{int.class, String.class, String.class, GlobalFeature.class, InputStream.class, int.class};
                LocalitySensitiveHashing.generateHashFunctions(lshFileName);
                hashFile = new File(lshFileName);
                searcher = (ImageSearcher) searcherClass.getDeclaredConstructor(params).newInstance(numOfResult, fieldName, hashesFieldName, globalFeatureClass.newInstance(),new FileInputStream(hashFile), numOfHashedResult);
                break;
            case MetricSpaces:
                String msFileName = hashFileName + "_ms.obj";
                MetricSpaces.indexReferencePoints(globalFeatureClass, numberOfReferencePoints, lenghtOfPostingList, new File(imagePath), new File(msFileName));

                params = new Class[]{int.class, File.class, int.class};
                searcher = (ImageSearcher) searcherClass.getDeclaredConstructor(params).newInstance(numOfResult, new File(msFileName), numOfHashedResult);
                break;
            default:
                searcher = (ImageSearcher) searcherClass.getDeclaredConstructor(params).newInstance(numOfResult, globalFeatureClass);
                break;
        }
    }
//
//    public void allImageSearchResult(String imageList, GenerateSearchResultFile generateSearchResultFile) throws IOException {
//        ArrayList<String> allImages = new ArrayList<>();
//        BufferedReader br = new BufferedReader(new FileReader(imageList));
//        String line;
//        FeatureSearchResult searchResult = null;
//
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
        System.out.println("average precision for globalFeature " + globalFeatureClass.getName() + " = " + averagePrecision.toString());
        System.out.println("average recall for globalFeature " + globalFeatureClass.getName() + " = " + averageRecall.toString());
    }

    public FeatureSearchResult singleImageSearchResult(String imageToSearch, Boolean wantToPrint) throws IOException {
        BufferedImage image = ImageIO.read(new FileInputStream(imageToSearch));
        IndexReader reader = DirectoryReader.open(new RAMDirectory(FSDirectory.open(Paths.get(indexPath)), IOContext.READONCE));
//        System.out.println("Documents in the reader22: " + reader.maxDoc());
//        System.out.println("Image To Search "+imageToSearch);
        long start = System.currentTimeMillis();
        ImageSearchHits hits = searcher.search(image, reader);
//        System.out.printf("Total time of searching: %s.\n", convertTime(System.currentTimeMillis() - start));

        FeatureSearchResult searchResult = new FeatureSearchResult(hits, reader, numOfOneLabel, imageToSearch, globalFeatureClass.getName(), hashingMode.toString());
        if(wantToPrint)
            printSingleImageSearchResult(searchResult);
        return searchResult;
    }

    private void printSingleImageSearchResult(FeatureSearchResult searchResult) throws IOException {
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