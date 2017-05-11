
import com.datastax.driver.core.*;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.regex.*;
import java.util.List;
import java.util.LinkedList;

import java.util.concurrent.Semaphore;

public class ImportData {

    // final static Logger logger = LoggerFactory.getLogger(ImportData.class);
    static String[] CONTACT_POINTS = {"104.131.73.39"};
    static int PORT = 9042;
    static String filename= "access_log";
    static int count = 0;
    static String insertQuery = "";
    static PreparedStatement preparedStatement = null;
    static Semaphore semaphore = null;
    static long start = 0;

    public static void main(String[] args) throws Exception {
        
        Cluster cluster = null;
        try {
            // The Cluster object is the main entry point of the driver.
            // It holds the known state of the actual Cassandra cluster (notably the Metadata).
            // This class is thread-safe, you should create a single instance (per target Cassandra cluster), and share
            // it throughout your application.
            cluster = Cluster.builder().addContactPoints(CONTACT_POINTS).withPort(PORT).build();

            // The Session is what you use to execute queries. Likewise, it is thread-safe and should be reused.
            Session session = cluster.connect();

            // We use execute to send a query to Cassandra.
            //// Drop the existing keyspace 
            session.execute("DROP KEYSPACE IF EXISTS Log_Analysis;");
            //// Create the keyspace 
            session.execute("CREATE KEYSPACE Log_Analysis WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : '2' };");
            //// Use the keyspace 
            session.execute("USE Log_Analysis;");
            //// Create the table 
            session.execute(new SimpleStatement("CREATE TABLE access_log_ips ( " +
                "ip text, " +
                "id int, " +
                "identity text, " +
                "username text, " +
                "processing_time text, " +
                "request text, " +
                "status_code text, " +
                "object_size text, " +
                "PRIMARY KEY ( ip, id ) );"));
            
            // Read data and insert it into the table
            try {
                insertQuery = "INSERT INTO access_log_ips (id, ip, identity, username, processing_time, request, status_code, object_size) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
                preparedStatement = session.prepare(insertQuery);
                semaphore = new Semaphore(15000);
                
                Stream<String> stream = Files.lines(Paths.get(filename));
                stream.forEach(l -> {
                    try {
                        // System.out.println("Starting " + Thread.currentThread().getId());
                        count++;
                        System.out.println("Inserting " + count);
                        Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+) (.*) (.*) (\\[.*\\]) (\".*\") (.*) (.*)");
                        Matcher m = p.matcher(l);
                        List<String> tokens = new LinkedList<String>();
                        m.find();

                        // //// Insertion the data into the table
                        // session.execute( String.format("INSERT INTO access_log (id, ip, identity, username, processing_time, request, status_code, object_size) VALUES (%d, '%s', '%s', '%s', '%s', '%s', '%s', '%s');", 
                        //                                 count, m.group(1), m.group(2), m.group(3), m.group(4), m.group(5), m.group(6), m.group(7)) );
                        
                        // System.out.println("INSERTED ROWS #: " + count);

                        BoundStatement statement = preparedStatement.bind(count, m.group(1), m.group(2), m.group(3), m.group(4), m.group(5), m.group(6), m.group(7)); //value not important
                        semaphore.acquire();
                        ResultSetFuture resultSetFuture = session.executeAsync(statement);
                    }
                    catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                    finally {
                        semaphore.release();
                    }
                });

                
            } catch (Exception e) {
                e.printStackTrace();
            }

        } finally {
            // Close the cluster after we’re done with it. This will also close any session that was created from this
            // cluster.
            // This step is important because it frees underlying resources (TCP connections, thread pools...). In a
            // real application, you would typically do this at shutdown (for example, when undeploying your webapp).
            if (cluster != null)
                cluster.close();
        }
    }
}
