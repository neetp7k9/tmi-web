package EagleEye;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class FeatureSearchResult {
    /*feature type info */
    public static String featureName;
    public static String hashingTypeName;
    /* stats for time*/
    public static Double indexTime;
    public static Double searchTime;

    public ImageSearchHits hits;
    public static int numsOfOneLabel;
    public Double precision;
    public Double recall;
    public static Double precisionSum;
    public static Double recallSum;
    public int[] singleImageRotationCount;
    public int[] singleImageResizeCount;
    public int[] singleImageNoiseCount;
    public static int[] allImageRotationCount;
    public static int[] allImageResizeCount;
    public static int[] allImageNoiseCount;
    public static int numOfAllImages;
    //double scoreSum;
    //double scoreSumSampleSize;
    /*target image path*/
    public String targetImagePath;
    public IndexReader reader;
    public FeatureSearchResult(ImageSearchHits hits, IndexReader reader, int numsOfOneLabel, String targetImagePath, String featureName, String hashingTypeName) {
        this.hits = hits;
        this.numsOfOneLabel = numsOfOneLabel;
        this.targetImagePath = targetImagePath;
        this.reader = reader;
        this.singleImageRotationCount = new int[]{0, 0};
        this.singleImageResizeCount = new int[]{0, 0};
        this.singleImageNoiseCount = new int[]{0, 0};
        this.featureName = featureName;
        this.hashingTypeName = hashingTypeName;
    }
    //initize counts and size
    public static void initialize(int numOfImages){
        numOfAllImages = numOfImages;
        precisionSum = 0d;
        recallSum = 0d;
        allImageRotationCount = new int[]{0, 0};
        allImageResizeCount = new int[]{0, 0};
        allImageNoiseCount = new int[]{0, 0};
        indexTime = -1.0;
        searchTime = -1.0;
    }
    public static void setIndexTime(Double secs){
        indexTime = secs;
    }
    public static void setSearchTime(Double secs){
        searchTime = secs;
    }
    public static double getAveragePrecision(){
        return precisionSum/numOfAllImages;
    }
    public static double getAverageRecall(){
        return recallSum/numOfAllImages;
    }
    //convert hits to precise recall
    public void getStats() {
        calculatePrecisionAndRecall();
    }
    public void calculatePrecisionAndRecall() {
        //Precision=TP/(TP+FP) TP = nums of same label, TP+FP = hits.length
        //Recall=TP/(TP+FN) TP = nums of same label, TP+FN = total nums of same label
        //Including Precision/Recall type
        int targetLabel = getFileLabel(targetImagePath);
        int sameLabelCount = 0;
        for (int y = 0; y < hits.length(); y++)
            if (getFileLabel(getFileName(y)) == targetLabel){
                calculateImageType(targetImagePath, 1);
                sameLabelCount++;
            }
            else {// the current label is not equal to our search label
                calculateImageType(targetImagePath, 0);
            }
        precision = Double.valueOf(sameLabelCount) / hits.length();
        recall = Double.valueOf(sameLabelCount) / numsOfOneLabel;
    }

    public void addToSum(){
        precisionSum += precision;
        recallSum += recall;
        for(int i= 0;i<2;i++){
            allImageRotationCount[i] += singleImageRotationCount[i];
            allImageResizeCount[i] += singleImageResizeCount[i];
            allImageNoiseCount[i] += singleImageNoiseCount[i];
        }
    }
    public  String getFileName(int position) {
        String fileName = "";
        try {
            fileName = reader.document(hits.documentID(position)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileName = fileName.substring(fileName.lastIndexOf('\\') + 1);
        return fileName;
    }
    public int getFileLabel(String filePath) {
        /*{projectName}_{imageName}_{[resize,rotate,noise]}_{[degree,size]}.png */
        /* ex: project_2_image_1_rotate_60.png */
        String[] imageNameArray = getImageNameArray(filePath);
        if (imageNameArray.length >= 5)
            return Integer.valueOf(imageNameArray[1])*1000+Integer.valueOf(imageNameArray[3]);
        else
            System.out.println("Irregular file name");
        return -1;
    }
    public void calculateImageType(String filePath, int posTrueOrFalse){
        String[] imageNameArray = getImageNameArray(filePath);
        switch(imageNameArray[4]) {
            case "rotate":
                singleImageRotationCount[posTrueOrFalse]++;
                break;
            case "noise":
                singleImageNoiseCount[posTrueOrFalse]++;
                break;
            case "resize":
                singleImageResizeCount[posTrueOrFalse]++;
                break;
        }
        return;
    }

    public String[] getImageNameArray(String filePath){
        String[] filePathArray = filePath.split("/");
        return filePathArray[filePathArray.length - 1].split("_");
    }
    public String toString(String indexPath) throws IOException {
        StringBuilderWriter sw = new StringBuilderWriter();
        String hitFile;
        IndexReader reader = DirectoryReader.open(new RAMDirectory(FSDirectory.open(Paths.get(indexPath)), IOContext.READONCE));
        for (int y = 0; y < hits.length(); y++) {
            hitFile = reader.document(hits.documentID(y)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
            System.out.println(y);
            System.out.println(reader.document(hits.documentID(y)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0]);

            hitFile = hitFile.substring(hitFile.lastIndexOf('\\') + 1);
            sw.write(y + ". " + fileNameConvert(hitFile) + " " + hits.score(y) + "\n");
            //sw.write(y + ". " + hitFile + " " + hits.score(y) + "\n");
            //System.out.print(sw.toString());
        }
        sw.write("\n");
        return sw.toString();
    }


    public String fileNameConvert(String fileName){
        return fileName.substring(fileName.indexOf("/public/system/images/")).split("/")[7];

    }
}
