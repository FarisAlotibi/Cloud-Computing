import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.conf.*;

public class IPCount {

  public static void main(String[] args) throws Exception {
    if (args.length != 3) {
      System.err.println("Usage: IPCount <input path> <output path> <IP Address>");
      System.exit(-1);
    }
    Configuration conf = new Configuration();
    conf.set("IP", args[2]);

    Job job = new Job(conf);
    job.setJarByClass(IPCount.class);
    job.setJobName("IPCount");

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    
    job.setMapperClass(IPCountMapper.class);
    job.setReducerClass(IPCountReducer.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
