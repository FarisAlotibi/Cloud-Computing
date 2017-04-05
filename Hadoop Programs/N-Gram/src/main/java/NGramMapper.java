import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.*;

public class NGramMapper
    extends Mapper<LongWritable, Text, Text, IntWritable> {

    @Override
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {

        Configuration conf = context.getConfiguration();
        // read variable n that entered by the user
        int n = Integer.parseInt(conf.get("n"));
        String line = value.toString();
        // removing any non-letters and spaces
        String plain = line.replaceAll("[^a-zA-Z\\s]", "");
        plain = plain.replaceAll("\\s+", "");
        // to lower case
        plain = plain.toLowerCase();
        
        for(int x = 0; x < plain.length()-n+1; x++){
            context.write(new Text(plain.substring(x, x+n)), new IntWritable(1));
        }
    }
}