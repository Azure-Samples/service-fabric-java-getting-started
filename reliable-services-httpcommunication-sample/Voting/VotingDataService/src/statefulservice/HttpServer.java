package statefulservice; 

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import microsoft.servicefabric.data.ReliableStateManager;
import microsoft.servicefabric.data.Transaction;
import microsoft.servicefabric.data.collections.ReliableHashMap;
import microsoft.servicefabric.data.utilities.AsyncEnumeration;
import microsoft.servicefabric.data.utilities.KeyValuePair;

public class HttpServer {
    private static final Logger logger = Logger.getLogger(HttpServer.class.getName());
    private static final int STATUS_OK = 200; 
    private static final String MAP_NAME = "votesMap";

    private String baseAddress;
    private int port;
    private com.sun.net.httpserver.HttpServer server;
    private ReliableStateManager stateManager;

    public HttpServer(String baseAddress, int port, ReliableStateManager stateManager) {
        this.baseAddress = baseAddress;
        this.port = port;
        this.stateManager = stateManager;
    }

    public void start() {
        try {
            server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(this.port), 0);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        server.createContext(this.baseAddress+"getList", new HttpHandler() {
            @Override
            public void handle(HttpExchange t) {

                try {
                	
                    String itemAsKey = "";

                    ReliableHashMap<String, String> votesMap = stateManager
                            .<String, String> getOrAddReliableHashMapAsync(MAP_NAME).get();
                    
                    Transaction tx = stateManager.createTransaction();
                    AsyncEnumeration<KeyValuePair<String, String>> kv = votesMap.keyValuesAsync(tx).get();
                    while (kv.hasMoreElementsAsync().get()) {
                        KeyValuePair<String, String> k = kv.nextElementAsync().get();
                        
                    	itemAsKey += k.getKey();
                    	itemAsKey += ","; 
                    	itemAsKey += k.getValue();
                    	itemAsKey += "\n";
                    }
                    
                    tx.close();                    
                    
                    t.sendResponseHeaders(STATUS_OK, 0);
                    OutputStream os = t.getResponseBody();
                    os.write(itemAsKey.getBytes());
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        
        server.createContext(this.baseAddress+"addItem", new HttpHandler() {
            @Override
            public void handle(HttpExchange t) {

                try {
                    URI r = t.getRequestURI();
                    Map<String, String> params = queryToMap(r.getQuery());
                    String itemToAdd = params.get("item");
                    
                    ReliableHashMap<String, String> votesMap = stateManager
                            .<String, String> getOrAddReliableHashMapAsync(MAP_NAME).get();                    
                    
                    Transaction tx = stateManager.createTransaction();
                    votesMap.computeAsync(tx, itemToAdd, (k, v) -> {
                        if (v == null) {
                            return "1";
                        }
                        else {
                        	int numVotes = Integer.parseInt(v);
                        	numVotes = numVotes + 1;                         	
                            return Integer.toString(numVotes);
                        }
                    }).thenApply((l) -> {
                        return tx.commitAsync().handle((re, x) -> {
                            if (x != null) {
                                logger.log(Level.SEVERE, x.getMessage());
                            }
                            try {
                            	tx.close();
                                
                                t.sendResponseHeaders(STATUS_OK, 0);
                                OutputStream os = t.getResponseBody();
                                os.write(itemToAdd.getBytes());
                                os.close();
                            } catch (Exception e) {
                                logger.log(Level.SEVERE, e.getMessage());
                            }
                            return null;
                        });
                    }).get();    
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        
        server.createContext(this.baseAddress+"removeItem", new HttpHandler() {
            @Override
            public void handle(HttpExchange t) {

                try {
                    URI r = t.getRequestURI();
                    Map<String, String> params = queryToMap(r.getQuery());
                    String itemToRemove = params.get("item");
                                        
                    ReliableHashMap<String, String> votesMap = stateManager
                            .<String, String> getOrAddReliableHashMapAsync(MAP_NAME).get();
                    
                    Transaction tx = stateManager.createTransaction();
                    votesMap.removeAsync(tx, itemToRemove).get();
                    tx.commitAsync().get();
                    tx.close();                    
                    
                    t.sendResponseHeaders(STATUS_OK, 0);
                    OutputStream os = t.getResponseBody();
                    os.write(itemToRemove.getBytes());
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        server.setExecutor(null);
        server.start();
    }
    
    private Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }

    public void stop() {
        this.server.stop(0);
    }

}