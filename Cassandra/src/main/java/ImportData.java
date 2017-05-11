
import com.datastax.driver.core.*;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.regex.*;
import java.util.List;
import java.util.LinkedList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.FutureCallback;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.Semaphore;

public class ImportData {

    static String filename= "access_log";
    static int count = 0;
    static String insertQuery = "";
    static PreparedStatement preparedStatement = null;
    static Semaphore semaphore = null;

    public static void main(String[] args) throws Exception {

        final CassandraService cassandraService = CassandraService.getInstance();

        cassandraService.connect();
        cassandraService.createSchema();
        Session session = cassandraService.getSession();

        // Read data and insert it into the table
        try {
            insertQuery = "INSERT INTO Log_Analysis.access_log (id, ip, identity, username, processing_time, request, status_code, object_size) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            
            preparedStatement = session.prepare(insertQuery);
            semaphore = new Semaphore(15000);
            
            Stream<String> stream = Files.lines(Paths.get(filename));
            stream.forEach(l -> {
                try {
                    semaphore.acquire();
                    // System.out.println("Starting " + Thread.currentThread().getId());
                    count++;
                    if(count % 50000 == 0)
                        System.out.println("The # of Rows Inserted " + count);
                    Matcher m = searchPattern(l);

                    BoundStatement statement = preparedStatement.bind(count, m.group(1), m.group(2), m.group(3), m.group(4), m.group(5), m.group(6), m.group(7));
                    
                    ResultSetFuture resultSetFuture = session.executeAsync(statement);

                    Futures.addCallback(resultSetFuture, new FutureCallback<ResultSet>() {
                        @Override
                        public void onSuccess( com.datastax.driver.core.ResultSet resultSet) {

                            semaphore.release();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            System.out.println("Error: " + throwable.toString());
                            semaphore.release();
                        }
                    });
                }
                catch (InterruptedException e) {

                    e.printStackTrace();
                }
            });

            // create the index
            cassandraService.createIndex();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Read data and insert it into the table
        try {
            insertQuery = "INSERT INTO Log_Analysis.access_log_request (id, ip, identity, username, processing_time, request, status_code, object_size) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            count = 0;
            preparedStatement = session.prepare(insertQuery);
            semaphore = new Semaphore(15000);
            System.out.println("Starting building access_log_request table");

            Stream<String> stream = Files.lines(Paths.get(filename));
            stream.forEach(l -> {
                try {
                    semaphore.acquire();
                    // System.out.println("Starting " + Thread.currentThread().getId());
                    count++;
                    if(count % 500000 == 0)
                        System.out.println("The # of Rows Inserted " + count);
                    Matcher m = searchPattern(l);

                    String ip = m.group(1);
                    String identity = m.group(2);
                    if(identity.trim().equals(""))
                        identity = "-";
                    String username = m.group(3);
                    if(username.trim().equals(""))
                        username = "-";
                    String processing_time = m.group(4);
                    String request = m.group(5);
                    request = StringUtils.substringBetween(m.group(5) ,"\"", "\"");
                    String[] request_s = request.split("\\s+");
                    String address = "-";

                    if(request_s.length >= 1)
                        address = request_s[1];
                    String status_code = m.group(6);
                    if(status_code.trim().equals(""))
                        status_code = "-";
                    String object_size = m.group(7);
                    if(object_size.trim().equals(""))
                        object_size = "-";

                    // BoundStatement statement = preparedStatement.bind(count, m.group(1), m.group(2), m.group(3), m.group(4), m.group(5), m.group(6), m.group(7));
                    BoundStatement statement = preparedStatement.bind(count, ip, identity, username, processing_time, address, status_code, object_size);

                    ResultSetFuture resultSetFuture = session.executeAsync(statement);

                    Futures.addCallback(resultSetFuture, new FutureCallback<ResultSet>() {
                        @Override
                        public void onSuccess( com.datastax.driver.core.ResultSet resultSet) {

                            semaphore.release();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            System.out.println("Error: " + throwable.toString());
                            semaphore.release();
                        }
                    });
                }
                catch (InterruptedException e) {

                    e.printStackTrace();
                }
            });

            
        } catch (Exception e) {
            e.printStackTrace();
            cassandraService.close();
        }
        cassandraService.close();
        
    }

    private static Matcher searchPattern(String line) {
        Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+) (.*) (.*) (\\[.*\\]) (\".*\") (.*) (.*)");
        Matcher m = p.matcher(line);
        List<String> tokens = new LinkedList<String>();
        m.find();
        return m;
    }
}
