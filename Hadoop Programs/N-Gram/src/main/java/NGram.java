import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.conf.*;

public class NGram {

    public static void main(String[] args) throws Exception {
        
        if (args.length != 3) {
            System.err.println("ERROR: Usage: NGram <input path> <output path> <n>");
            System.err.println("EX: hadoop jar NGram.jar NGram sample.txt output 4");
            System.exit(-1);
        }
        
        String input_file = args[0];
        String output_file = args[1];
        String n = args[2];

        Configuration conf = new Configuration();
        // pass n to the configuration to be read by the mapper
        conf.set("n", n);
        // job configuration
        Job job = new Job(conf);
        job.setJarByClass(NGram.class);
        job.setJobName("NGram");

        FileInputFormat.addInputPath(job, new Path(input_file));
        FileOutputFormat.setOutputPath(job, new Path(output_file));

        // set classes of Mapper and Reducer
        job.setMapperClass(NGramMapper.class);
        job.setReducerClass(NGramReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}