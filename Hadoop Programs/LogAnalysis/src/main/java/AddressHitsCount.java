import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.conf.*;
public class AddressHitsCount {

  public static void main(String[] args) throws Exception {
    if (args.length != 3) {
      System.err.println("ERROR: Usage: AddressHitsCount <input path> <output path> <address>");
      System.err.println("EX: hadoop jar LogAnalysis.jar AddressHitsCount access_log output1 '/assets/img/home-logo.png'");
      System.exit(-1);
    }
    else {
      Configuration conf = new Configuration();
      // pass address to the configuration to be read by the mapper/reducer
      conf.set("address", args[2]);

      // job configuration
      Job job = new Job(conf);
      job.setJarByClass(AddressHitsCount.class);
      job.setJobName("AddressHitsCount");

      // specify input/output files
      FileInputFormat.addInputPath(job, new Path(args[0]));
      FileOutputFormat.setOutputPath(job, new Path(args[1]));
      
      // set classes of Mapper and Reducer
      job.setMapperClass(AddressHitsCountMapper.class);
      job.setReducerClass(AddressHitsCountReducer.class);

      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(IntWritable.class);
      
      System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
  }
}