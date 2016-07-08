package EagleEye;

import net.semanticmetadata.lire.aggregators.AbstractAggregator;
import net.semanticmetadata.lire.aggregators.BOVW;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.builders.GlobalDocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.AutoColorCorrelogram;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.EdgeHistogram;
import net.semanticmetadata.lire.imageanalysis.features.local.opencvfeatures.CvSurfExtractor;
import net.semanticmetadata.lire.indexers.parallel.ImagePreprocessor;
import net.semanticmetadata.lire.indexers.parallel.ParallelIndexer;
import net.semanticmetadata.lire.searchers.GenericFastImageSearcher;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;
import spark.Spark;


import static org.apache.lucene.document.Field.*;
import static spark.Spark.get;
import static spark.SparkBase.setPort;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    static String globalIndexPath = System.getProperty("user.dir") + "/index/global/";
    static String localIndexPath = System.getProperty("user.dir") + "/index/local/";
    static String textIndexPath = System.getProperty("user.dir") + "/index/text/";


    static int numberOfReferencePoints = 2000;
    static int lenghtOfPostingList = 50;
    static String hashFileName = "hashFile";
    static int numOfResult = 30;
    static int numHashedResult = 60;
    static int numOfOneLabel = 30;
    static Class<? extends AbstractAggregator> aggregatorClass = BOVW.class;
    static Class localFeatureClass = CvSurfExtractor.class;

    public static void main(String[] args) {
        setPort(8000);
        get("/", (req, res) -> {
            System.out.println(globalIndexPath);
            System.out.println(localIndexPath);
            System.out.println(textIndexPath);
            System.out.println("params " + req.params());
            System.out.println("attributes " + req.attributes().toString());
            System.out.println("queryParams " + req.queryParams());
            System.out.println(req.queryMap("id").value());
            System.out.println(req.queryParams("id"));
            System.out.println("url" + req.url());
            System.out.println("host " + req.host());
            return "hello from puan.com";
        });

        get("/index", (req, res) -> {
            System.out.println(req.params());
            System.out.println(req.attributes().toString());
            System.out.println(req.queryParams());
            System.out.println(req.queryMap("id").value());
            System.out.println(req.queryString());
            System.out.println(req.url());
            System.out.println(req.host());
            String response;
            if (req.queryString() == null)
                return "";
            Map<String, String> map = queryToMap(URLDecoder.decode(req.queryString()));
            if (map.get("file") == null) {
                response = "file is missing ";
                System.out.println(response);
                return response;
            }
            if (map.get("type") == null) {
                response = "type_id is missing ";
                System.out.println(response);
                return response;
            }

            System.out.println("Index Image for Type : " + map.get("type"));
            System.out.println("index local feature for : " + map.get("file"));
            System.out.println(localIndexPath + map.get("type"));

            ParallelIndexer parallelIndexerLocalAppend = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, localIndexPath + map.get("type"), new File(map.get("file")), false);

            parallelIndexerLocalAppend.setImagePreprocessor(new ImagePreprocessor() {
                @Override
                public BufferedImage process(BufferedImage image) {
                    BufferedImage newBufferedImage = new BufferedImage(image.getWidth(),
                            image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                    newBufferedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
                    return newBufferedImage;
                }
            });

            System.out.println("index local feature for : " + map.get("file"));
            parallelIndexerLocalAppend.run();

            System.out.println("index global feature for : " + map.get("file"));

            ParallelIndexer parallelIndexerGlobalAppend = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, globalIndexPath + map.get("type"), new File(map.get("file")), false);
            parallelIndexerGlobalAppend.run();

            response = "Indexing the file" + map.get("file");
            return response;
        });


        get("/search", (req, res) -> {
            String response = "";
            Map<String, String> map = queryToMap(URLDecoder.decode(req.queryString()));
            if (map.get("file") == null) {
                response = "file is missing";
                System.out.println(response);
                return response;
            }
            if (map.get("type") == null) {
                response = "type is missing";
                System.out.println(response);
                return response;
            }

            if (map.get("feature") != null) {
                System.out.println("search local feature for : " + map.get("file"));
                BufferedImage image = null;
                try {
                    image = ImageIO.read(new FileInputStream(map.get("file")));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //type change RGBA to RGB
                BufferedImage newBufferedImageTarget = new BufferedImage(image.getWidth(),
                        image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                newBufferedImageTarget.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
                image = newBufferedImageTarget;

                IndexReader reader = null;
                try {
                    reader = DirectoryReader.open(new RAMDirectory(FSDirectory.open(Paths.get(localIndexPath + map.get("type"))), IOContext.READONCE));
                    GenericFastImageSearcher cvsurfSearcher = new GenericFastImageSearcher(30, localFeatureClass, aggregatorClass.newInstance(), 256, true, reader, localIndexPath + map.get("type") + ".config");
                    ImageSearchHits cvsurfHits = cvsurfSearcher.search(image, reader);
                    FeatureSearchResult featureSearchResult = new FeatureSearchResult(cvsurfHits, reader, numOfOneLabel, map.get("file"), localFeatureClass.getName(), GlobalDocumentBuilder.HashingMode.None.toString());
                    response = featureSearchResult.toString(localIndexPath + map.get("type"));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

                System.out.println("search global feature for : " + map.get("file"));
                GlobalFeatureSearch globalFeatureSearch = new GlobalFeatureSearch(CEDD.class, globalIndexPath + map.get("type"), "", GlobalDocumentBuilder.HashingMode.None, numOfOneLabel);
             //   GlobalFeatureSearch globalFeatureSearch = new GlobalFeatureSearch(EdgeHistogram.class, globalIndexPath + map.get("type"), "", GlobalDocumentBuilder.HashingMode.None, numOfOneLabel);

                try {
                    globalFeatureSearch.initializeHashing(GenericFastImageSearcher.class, numOfResult, 50, hashFileName, numberOfReferencePoints, lenghtOfPostingList);
                    FeatureSearchResult featureSearchResult  = globalFeatureSearch.singleImageSearchResult(map.get("file"), false);
                    response = featureSearchResult.toString(globalIndexPath + map.get("type"));
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return response;
        });


        get("/delete", (req, res) -> {
            String response = "";
            Map<String, String> map = queryToMap(URLDecoder.decode(req.queryString()));
            if (map.get("image_id") == null) {
                response = "image_id is missing";
                System.out.println(response);
                return response;
            }

            System.out.println("start to delete IMAGE!!!!!!!!!");
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            IndexWriter writer = null;

            System.out.println("delete local data");
            String indexPath = localIndexPath + map.get("type");
            try {
                writer = new IndexWriter(FSDirectory.open(Paths.get(indexPath)), config);
                writer.deleteDocuments(new Term("image_id", map.get("image_id")));
                writer.commit();
                writer.close();
//
//                IndexReader reader = DirectoryReader.open(new RAMDirectory(FSDirectory.open(Paths.get(indexPath)), IOContext.READONCE));
//                System.out.println(reader.numDocs());
//                System.out.println(reader.document(1).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0]);
//                String test = reader.document(1).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];

            } catch (IOException e) {
                System.out.println("error in delete local data");
                e.printStackTrace();
            }

            System.out.println("delete global data");
            indexPath = globalIndexPath + map.get("type");
            try {


                config = new IndexWriterConfig(new StandardAnalyzer());
                writer = new IndexWriter(FSDirectory.open(Paths.get(indexPath)), config);

                writer.deleteDocuments(new Term("image_id", map.get("image_id")));
                writer.commit();
                writer.close();

            } catch (IOException e) {
                System.out.println("error in delete global data");
                e.printStackTrace();
            }

            return response;
        });


        get("/check_all", (req, res) -> {
            String response = "";
            Map<String, String> map = queryToMap(URLDecoder.decode(req.queryString()));
            if (map.get("type") == null) {
                response = "type is missing";
                System.out.println(response);
                return response;
            }

            System.out.println("start to go through");
            String indexPath = null;
            if (map.get("feature") == null) {
                System.out.println("check global feature");
                indexPath = globalIndexPath + map.get("type");
            }else{
                System.out.println("check local feature");
                indexPath = localIndexPath + map.get("type");
            }

            try{
                IndexReader reader = DirectoryReader.open(new RAMDirectory(FSDirectory.open(Paths.get(indexPath)), IOContext.READONCE));

                for(int i=0; i<reader.numDocs();i++){
                    String filePath = reader.document(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
                    response += String.valueOf(i) + " => id : " + getID(filePath) + " , " + reader.document(i).getValues("image_id")[0] + " " + filePath + "\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
            return response;
        });
    }
    public static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }
    public static int getID(String filePath){
        String pattern = "\\/000\\/000\\/(\\d+)\\/";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(filePath);
        int id = -1;
        if(m.find()){
            id = Integer.parseInt(m.group(0).substring(9,m.group(0).length()-1));
        }
        return id;
    }

}