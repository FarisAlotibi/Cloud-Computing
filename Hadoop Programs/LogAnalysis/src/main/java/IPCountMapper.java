import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.*;

public class IPCountMapper
  extends Mapper<LongWritable, Text, Text, IntWritable> {
  
  @Override
  public void map(LongWritable key, Text value, Context context)
      throws IOException, InterruptedException {
    
        String line = value.toString();
        String ip  = new String(line.split("\\s+")[0]);

        Configuration conf = context.getConfiguration();
        if(ip.equals(new String(conf.get("IP")))) {

            context.write(new Text(ip), new IntWritable(1));
        }
    }
}