import java.io.IOException;
import org.apache.commons.lang.StringUtils;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.*;

public class AddressHitsCountMapper
  extends Mapper<LongWritable, Text, Text, IntWritable> {
  
  @Override
  public void map(LongWritable key, Text value, Context context)
    throws IOException, InterruptedException {
    
        String line = value.toString();
        line = line.replace("\"\"", "-");
        
        String request = StringUtils.substringBetween(line ,"\"", "\"");
        String[] request_s = request.split("\\s+");
        String address = "";

        if(request_s.length >= 1)
        	  address = request_s[1];

        Configuration conf = context.getConfiguration();
        if(address.equals(new String(conf.get("address")))) {
            context.write(new Text(address), new IntWritable(1));
        }
    }
}