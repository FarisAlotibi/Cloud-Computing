import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.conf.*;

public class IPMax {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("ERROR: Usage: IPMax <input path> <output path>");
            System.err.println("EX: hadoop jar LogAnalysis.jar IPMax access_log output4");
            System.exit(-1);
        }
        else {

            Configuration conf = new Configuration();
            // job configuration
            Job job = new Job(conf);
            job.setJarByClass(IPMax.class);
            job.setJobName("IPMax");

            // specify input/output files
            FileInputFormat.addInputPath(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));
            
            // set classes of Mapper and Reducer
            job.setMapperClass(IPMaxMapper.class);
            job.setReducerClass(IPMaxReducer.class);

            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);
            
            System.exit(job.waitForCompletion(true) ? 0 : 1);
        }
    }
}