
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.util.stream.Stream;
import java.util.regex.*;
import java.util.List;
import java.util.LinkedList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.FutureCallback;
import org.apache.commons.lang.StringUtils;


public class CSV {

    static String filename= "access_log";
    static int count = 0;

    public static void main(String[] args) throws Exception {
        
        // Read data and insert it into the table
        try {

            Stream<String> stream = Files.lines(Paths.get(filename));
            stream.forEach(l -> {
                try {
                    count++;
                    if(count % 500000 == 0)
                        System.out.println("The # of Rows Inserted " + count);
                    Matcher m = searchPattern(l);

                    FileOutputStream fos = new FileOutputStream("access_log.csv", true);
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

                    String line = count + "," + ip + "," + identity + "," + username + "," + processing_time + ",\"" + address + "\"," + status_code + "," + object_size;
                    fos.write(line.getBytes());
                    fos.write("\n".getBytes()); 
                    fos.close();
                }
                catch (Exception e) {

                    System.out.println(e);
                }
            });

            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private static Matcher searchPattern(String line) {
        Pattern p = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+) (.*) (.*) (\\[.*\\]) (\".*\") (.*) (.*)");
        Matcher m = p.matcher(line);
        List<String> tokens = new LinkedList<String>();
        m.find();
        return m;
    }
}
