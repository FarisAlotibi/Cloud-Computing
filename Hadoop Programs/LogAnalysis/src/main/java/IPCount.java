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
      System.err.println("ERROR: Usage: IPCount <input path> <output path> <IP Address>");
      System.err.println("EX: hadoop jar LogAnalysis.jar IPCount access_log output2 '10.153.239.5'");
      System.exit(-1);
    }
    Configuration conf = new Configuration();
    // pass IP to the configuration to be read by the mapper/reducer
    conf.set("IP", args[2]);

    // job configuration
    Job job = new Job(conf);
    job.setJarByClass(IPCount.class);
    job.setJobName("IPCount");

    // specify input/output files
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    
    // set classes of Mapper and Reducer
    job.setMapperClass(IPCountMapper.class);
    job.setReducerClass(IPCountReducer.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}