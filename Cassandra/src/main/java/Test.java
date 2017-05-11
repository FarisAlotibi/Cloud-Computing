
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

public class Test {

    public static void main(String[] args) throws Exception {

        final CassandraService cassandraService = CassandraService.getInstance();

        cassandraService.connect();
        Session session = cassandraService.getSession();

        // Read data and insert it into the table
        try {
            
            ResultSet results = session.execute(
                "SELECT id FROM Log_Analysis.access_log_ip " +
                        "WHERE ip = \"10.153.239.5\";");

            System.out.printf("%-20s %n", "id");
            System.out.println("--------------------");

            for (Row row : results) {

                System.out.printf("%-20s $n", row.getString("title"));

            }

            
        } catch (Exception e) {
            e.printStackTrace();
            cassandraService.close();
        }
        finally {
            cassandraService.close();
        }
    }
}
