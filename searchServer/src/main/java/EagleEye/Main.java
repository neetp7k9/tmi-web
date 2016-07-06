//package EagleEye;
//
//import EagleEye.FeatureSearchResult;
//import EagleEye.GlobalFeatureSearch;
//import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;
//import com.sun.net.httpserver.HttpServer;
//import net.semanticmetadata.lire.aggregators.AbstractAggregator;
//import net.semanticmetadata.lire.aggregators.BOVW;
//import net.semanticmetadata.lire.builders.DocumentBuilder;
//import net.semanticmetadata.lire.builders.GlobalDocumentBuilder;
//import net.semanticmetadata.lire.imageanalysis.features.global.EdgeHistogram;
//import net.semanticmetadata.lire.imageanalysis.features.local.opencvfeatures.CvSiftExtractor;
//import net.semanticmetadata.lire.imageanalysis.features.local.opencvfeatures.CvSurfExtractor;
//import net.semanticmetadata.lire.indexers.parallel.ImagePreprocessor;
//import net.semanticmetadata.lire.indexers.parallel.ParallelIndexer;
//import net.semanticmetadata.lire.searchers.GenericFastImageSearcher;
//import net.semanticmetadata.lire.searchers.ImageSearchHits;
//import org.apache.lucene.index.DirectoryReader;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.store.IOContext;
//import org.apache.lucene.store.RAMDirectory;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.*;
//import java.lang.reflect.InvocationTargetException;
//import java.net.InetSocketAddress;
//import java.nio.file.Paths;
//import java.util.HashMap;
//import java.util.Map;
//
//public class Main {
//
//    static String globalIndexPath = "/Users/neetp7k9/Mafia/LireServer/index/global";
//    static String localIndexPath = "/Users/neetp7k9/Mafia/LireServer/index/local";
//
//    static int numberOfReferencePoints = 2000;
//    static int lenghtOfPostingList = 50;
//    static String hashFileName ="hashFile";
//
//    static int numOfResult = 30;
//    static int numHashedResult = 60;
//    static int numOfOneLabel = 30;
//    static Class<? extends AbstractAggregator> aggregatorClass = BOVW.class;
//    static Class localFeatureClass = CvSurfExtractor.class;
//
//
//    public static void main(String[] args) throws Exception {
//        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
//        server.createContext("/index", new IndexHandler());
//        server.createContext("/search", new SearchHandler());
//        server.setExecutor(null); // creates a default executor
//        server.start();
//        System.out.println("test");
//    }
//
//    static class IndexHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange t) throws IOException {
//            long start = System.currentTimeMillis();
//            String response;
//            System.out.println(t.getRequestURI());
//            System.out.println(t.getRequestMethod());
//            if(t.getRequestURI().getQuery() == null)
//                return;
//            Map<String,String> map = queryToMap(t.getRequestURI().getQuery());
//            if(map.get("file")==null){
//                response = "file is missing " + "Total time of indexing: ," + String.valueOf((System.currentTimeMillis() - start)/1000.0);
//                System.out.println(response);
//                t.sendResponseHeaders(200, response.length());
//                OutputStream os = t.getResponseBody();
//                os.write(response.getBytes());
//                os.close();
//                return;
//            }
//            System.out.println("have file");
//
//
//            System.out.println("do local index");
//            ParallelIndexer parallelIndexerLocalAppend = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, localIndexPath, new File(map.get("file")), false);
//            parallelIndexerLocalAppend.setImagePreprocessor(new ImagePreprocessor() {
//                @Override
//                public BufferedImage process(BufferedImage image) {
//                    BufferedImage newBufferedImage = new BufferedImage(image.getWidth(),
//                            image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//                    newBufferedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
//                    return newBufferedImage;
//                }
//            });
//            parallelIndexerLocalAppend.run();
//
//            System.out.println("start indexing");
//            System.out.println(map.get("file"));
//            ParallelIndexer parallelIndexerGlobalAppend = new ParallelIndexer(DocumentBuilder.NUM_OF_THREADS, globalIndexPath, new File(map.get("file")), false);
//            System.out.println("end indexing0");
//
//            parallelIndexerGlobalAppend.run();
//
//            System.out.println("end indexing");
//            response = "Indexing the file" + map.get("file") ;
//            t.sendResponseHeaders(200, response.length());
//            OutputStream os = t.getResponseBody();
//            os.write(response.getBytes());
//            os.close();
//            System.out.println("finish =>"+response);
//
//        }
//    }
//    static class SearchHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange t) throws IOException {
//            String response = null;
//            System.out.println(t.getRequestURI());
//            System.out.println(t.getRequestMethod());
//
//            Map<String,String> map = queryToMap(t.getRequestURI().getQuery());
//            if(map.get("file")==null){
//                response = "file is missing";
//                System.out.println(response);
//                t.sendResponseHeaders(200, response.length());
//                OutputStream os = t.getResponseBody();
//                os.write(response.getBytes());
//                os.close();
//                return;
//            }
//            System.out.println("test0");
//
//            if(map.get("feature")!=null){
//
//                System.out.println("do local search");
//                BufferedImage image = ImageIO.read(new FileInputStream(map.get("file")));
//
//                //type change RGBA to RGB
//                BufferedImage newBufferedImageTarget = new BufferedImage(image.getWidth(),
//                image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//                newBufferedImageTarget.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
//                image = newBufferedImageTarget;
//
//
//                IndexReader reader = DirectoryReader.open(new RAMDirectory(FSDirectory.open(Paths.get(localIndexPath)), IOContext.READONCE));
//
//                GenericFastImageSearcher cvsurfsearcher = null;
//                try {
//                    cvsurfsearcher = new GenericFastImageSearcher(30, localFeatureClass, aggregatorClass.newInstance(), 256, true, reader, localIndexPath + ".config");
//                } catch (InstantiationException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//                ImageSearchHits cvsurfhits = cvsurfsearcher.search(image, reader);
//                FeatureSearchResult featureSearchResult = new FeatureSearchResult(cvsurfhits, reader, numOfOneLabel, map.get("file"), localFeatureClass.getName(), GlobalDocumentBuilder.HashingMode.None.toString());
//                response = featureSearchResult.toString(localIndexPath);
//            }
//            else{
//                System.out.println("do global search");
//                GlobalFeatureSearch globalFeatureSearch = new GlobalFeatureSearch(EdgeHistogram.class, globalIndexPath, "", GlobalDocumentBuilder.HashingMode.None, numOfOneLabel);
//                try {
//                    globalFeatureSearch.initializeHashing(GenericFastImageSearcher.class, numOfResult, 50, hashFileName, numberOfReferencePoints, lenghtOfPostingList);
//                } catch (InstantiationException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//
//                FeatureSearchResult featureSearchResult = globalFeatureSearch.singleImageSearchResult(map.get("file"), false);
//                response = featureSearchResult.toString(globalIndexPath);
//
//            }
//
//            t.sendResponseHeaders(200, response.length());
//            OutputStream os = t.getResponseBody();
//            os.write(response.getBytes());
//            os.close();
//            System.out.println("finish search => "+response);
//        }
//    }
//    /**
//     * returns the url parameters in a map
//     * @param query
//     * @return map
//     */
//    public static Map<String, String> queryToMap(String query){
//        Map<String, String> result = new HashMap<String, String>();
//        for (String param : query.split("&")) {
//            String pair[] = param.split("=");
//            if (pair.length > 1) {
//                result.put(pair[0], pair[1]);
//            } else {
//                result.put(pair[0], "");
//            }
//        }
//        return result;
//    }
//}
//
