import com.datastax.driver.core.*;
import java.util.HashMap;
import java.util.Map;
import com.google.common.hash.Hashing;

public class IPMax {
    public static void main(String[] args) throws Exception {

        final CassandraService cassandraService = CassandraService.getInstance();

        cassandraService.connect();
        Session session = cassandraService.getSession();

        try {
            String query = "SELECT ip, count(*) AS count FROM Log_Analysis.access_log GROUP BY ip";

            ResultSet result = session.execute(query);
            HashMap<String,Integer> count_results = new HashMap();

            for (Row row : result){
                int i = (int) row.getLong(1);
                count_results.put(row.getString(0), i);
            }

            // Similar to Spark result
            count_results.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
                .limit(1) 
                .forEach(System.out::println);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            cassandraService.close();
        }
    }
}