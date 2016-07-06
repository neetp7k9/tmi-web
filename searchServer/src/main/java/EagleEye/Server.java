package EagleEye;

import net.semanticmetadata.lire.aggregators.AbstractAggregator;
import net.semanticmetadata.lire.aggregators.BOVW;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.builders.GlobalDocumentBuilder;
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
            if (map.get("user_id") == null) {
                response = "user_id is missing ";
                System.out.println(response);
                return response;
            }

            System.out.println("Index Image for User : " + map.get("user_id"));
            System.out.println("index local feature for : " + map.get("file"));

            ParallelIndexer parallelIndexerLocalAppend = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, localIndexPath + map.get("user_id"), new File(map.get("file")), false);
            parallelIndexerLocalAppend.setImagePreprocessor(new ImagePreprocessor() {
                @Override
                public BufferedImage process(BufferedImage image) {
                    BufferedImage newBufferedImage = new BufferedImage(image.getWidth(),
                            image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                    newBufferedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
                    return newBufferedImage;
                }
            });
            parallelIndexerLocalAppend.run();

            System.out.println("index global feature for : " + map.get("file"));

            ParallelIndexer parallelIndexerGlobalAppend = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, globalIndexPath + map.get("user_id"), new File(map.get("file")), false);
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
                    reader = DirectoryReader.open(new RAMDirectory(FSDirectory.open(Paths.get(localIndexPath + map.get("user_id"))), IOContext.READONCE));
                    GenericFastImageSearcher cvsurfSearcher = new GenericFastImageSearcher(30, localFeatureClass, aggregatorClass.newInstance(), 256, true, reader, localIndexPath + map.get("user_id") + ".config");
                    ImageSearchHits cvsurfHits = cvsurfSearcher.search(image, reader);
                    FeatureSearchResult featureSearchResult = new FeatureSearchResult(cvsurfHits, reader, numOfOneLabel, map.get("file"), localFeatureClass.getName(), GlobalDocumentBuilder.HashingMode.None.toString());
                    response = featureSearchResult.toString(localIndexPath + map.get("user_id"));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

                System.out.println("search global feature for : " + map.get("file"));
                GlobalFeatureSearch globalFeatureSearch = new GlobalFeatureSearch(EdgeHistogram.class, globalIndexPath + map.get("user_id"), "", GlobalDocumentBuilder.HashingMode.None, numOfOneLabel);
                try {
                    globalFeatureSearch.initializeHashing(GenericFastImageSearcher.class, numOfResult, 50, hashFileName, numberOfReferencePoints, lenghtOfPostingList);
                    FeatureSearchResult featureSearchResult  = globalFeatureSearch.singleImageSearchResult(map.get("file"), false);
                    response = featureSearchResult.toString(globalIndexPath + map.get("user_id"));
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

        get("/delete_text", (req, res) -> {
            String response = "";
            Map<String, String> map = queryToMap(URLDecoder.decode(req.queryString()));
            if (map.get("document_id") == null) {
                response = "document id is missing";
                System.out.println(response);
                return response;
            }

            System.out.println("start to delete TEXT!!!!!!!!!");

            String indexPath = textIndexPath + map.get("user_id");
            try{
                IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
                FSDirectory indexDirectory = FSDirectory.open(Paths.get(indexPath));
                IndexWriter writer = new IndexWriter(indexDirectory, config);
                writer.deleteDocuments(new Term("document_id", map.get("document_id")));
                writer.commit();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }

            return "success";
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
            String indexPath = localIndexPath + map.get("user_id");
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
            indexPath = globalIndexPath + map.get("user_id");
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


        get("/search_text", (req, res) -> {

            System.out.println("start text searching");
            String response = "";
            System.out.println(req.queryString());
            Map<String, String> map = queryToMap(URLDecoder.decode(req.queryString()));
            if (map.get("text") == null) {
                response = "text is missing";
                System.out.println(response);
                return response;
            }

            IndexSearcher searcher = null;
            QueryParser parser = null;

            try {
                System.out.println("here1");
                searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(textIndexPath + map.get("user_id")))));
                System.out.println("here2");
            } catch (IOException e) {
                System.out.println("here3");
                e.printStackTrace();
            }

            System.out.println("here");
            parser = new QueryParser("content", new StandardAnalyzer());

            Query query = null;
            TopDocs hits=null;
            String queryString = null;

            System.out.println("here");
            try {
                    queryString = map.get("text");
                    System.out.println(map.get("text"));
                    Term term = new Term("content", queryString);
                    //create the term query object
//                    query = new FuzzyQuery(term);
                    query = new FuzzyQuery(term);
                    //do the search
//                query = parser.parse(map.get("text") );
                hits = searcher.search(query, 30);

            } catch (IOException e) {
                e.printStackTrace();
            }
            for(int i=0;i<hits.scoreDocs.length;i++){
                try {
                    System.out.print(searcher.doc(hits.scoreDocs[i].doc).get("path") + " : ");
                    response += searcher.doc(hits.scoreDocs[i].doc).get("document_id") + " " + hits.scoreDocs[i].score + "\n";
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(hits.scoreDocs[i].score);
            }
            return response;
        });
        get("/index_document", (req, res) -> {
            String response = null;
            Directory indexDirectory = null;
            IndexWriter writer = null;
            Map<String, String> map = queryToMap(URLDecoder.decode(req.queryString()));
            if (map.get("file") == null) {
                response = "file is missing";
                System.out.println(response);
                return response;
            }
            if (map.get("document_id") == null) {
                response = "document id is missing";
                System.out.println(response);
                return response;
            }
            try{
                indexDirectory = FSDirectory.open(Paths.get(textIndexPath + map.get("user_id")));
                IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
                writer = new IndexWriter(indexDirectory,config);
            } catch (IOException e) {
                e.printStackTrace();
            }

            File f = new File(map.get("file"));
            PDDocument pddDocument = null;
            String content= null;
            PDFTextStripper textStripper = null;
            try {
                pddDocument = PDDocument.load(f);
                textStripper = new PDFTextStripper();
                content = textStripper.getText(pddDocument);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //System.out.println(content.substring(0, 500));

            PDDocumentInformation info = pddDocument.getDocumentInformation();
            System.out.println("Page Count=" + pddDocument.getNumberOfPages());
            System.out.println("Title=" + info.getTitle());
            System.out.println("Author=" + info.getAuthor());
            System.out.println("Subject=" + info.getSubject());
            System.out.println("Keywords=" + info.getKeywords());
            System.out.println("Creator=" + info.getCreator());
            System.out.println("Producer=" + info.getProducer());
            System.out.println("Creation Date=" + info.getCreationDate());
            System.out.println("Modification Date=" + info.getModificationDate());
            System.out.println("Trapped=" + info.getTrapped());

            System.out.println("start indexing0");
            Document doc = new Document();

            System.out.println("add document_id");
            doc.add(new StringField("document_id", map.get("document_id"), Store.YES));

            System.out.println("add Title");
            if(info.getTitle()!= null)
                doc.add(new StringField("Title", info.getTitle(), Store.YES));

            System.out.println("add author");
            if(info.getAuthor()!= null)
                doc.add(new StringField("Author", info.getAuthor(), Store.YES));
            if(info.getCreator()!= null)
                doc.add(new StringField("Creator", info.getCreator().toString(), Store.YES));
            if(info.getCreationDate()!= null)
                doc.add(new StringField("Creation Date", info.getCreationDate().toString(), Store.YES));

            System.out.println("add Modification");
            if(info.getModificationDate()!= null)
            doc.add(new StringField("Modification Date", info.getModificationDate().toString(), Store.YES));
            doc.add(new StringField("path", map.get("file"), Store.YES));

            System.out.println("add content");
            doc.add(new TextField("content",content.replaceAll("[^A-Za-z0-9.:\n \\[\\]\\-°±\\)\\(]+", ""),Store.YES));

            try {
                writer.addDocument(doc);
                writer.close();
                pddDocument.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("finish indexing");

            return "";
        });

        get("/check_all", (req, res) -> {
            String response = "";
            Map<String, String> map = queryToMap(URLDecoder.decode(req.queryString()));
            if (map.get("user_id") == null) {
                response = "user id is missing";
                System.out.println(response);
                return response;
            }

            System.out.println("start to go through");
            String indexPath = null;
            if (map.get("feature") == null) {
                System.out.println("check global feature");
                indexPath = globalIndexPath + map.get("user_id");
            }else{
                System.out.println("check local feature");
                indexPath = localIndexPath + map.get("user_id");
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
        get("/update_image_id", (req, res) -> {
            String response = "";
            Map<String, String> map = queryToMap(URLDecoder.decode(req.queryString()));
            if (map.get("user_id") == null) {
                response = "user id is missing";
                System.out.println(response);
                return response;
            }

            System.out.println("start to go through");
            String indexPath = null;
            if (map.get("feature") == null) {
                System.out.println("update global feature");
                indexPath = globalIndexPath + map.get("user_id");
            }else{
                System.out.println("update local feature");
                indexPath = localIndexPath + map.get("user_id");
            }
//            String field = "field";
//            if (map.get("field") != null) {
//                field =  map.get("field");
//            }

            try{
                IndexReader reader = DirectoryReader.open(new RAMDirectory(FSDirectory.open(Paths.get(indexPath)), IOContext.READONCE));
                IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
                FSDirectory indexDirectory = FSDirectory.open(Paths.get(indexPath));
                IndexWriter writer = new IndexWriter(indexDirectory, config);

                ArrayList<Document> storedDocument = new ArrayList<Document>();
                for(int i=0; i<reader.numDocs();i++){
                    response += String.valueOf(i) + " => id : " + getID(reader.document(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0]) + " , " + reader.document(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0] + "\n";
                    Document d = reader.document(i);
                    d.add(new StringField("image_id", String.valueOf(getID(reader.document(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0])), Store.YES));
                    storedDocument.add(d);
                }
                writer.deleteAll();
                writer.commit();
                writer.addDocuments(storedDocument);
                writer.commit();
                writer.close();
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