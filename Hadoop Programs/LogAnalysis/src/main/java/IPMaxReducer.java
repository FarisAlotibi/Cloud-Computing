import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.conf.*;

public class IPMaxReducer 
    extends Reducer<Text, IntWritable, Text, IntWritable> {
        int max =0;
        String maxIP = "1";


        public void reduce(Text key, Iterable<IntWritable> values, Context context) 
            throws IOException, InterruptedException {
                
                int sum=0;

                for (IntWritable value : values) 
                {
                    sum += value.get();
                }

                if(sum > max)
                {
                    max = sum;
                    maxIP = key.toString();
                }
            }

            protected void cleanup(Context context) throws IOException, InterruptedException {
                
                context.write(new Text(maxIP), new IntWritable(max));
            }
}