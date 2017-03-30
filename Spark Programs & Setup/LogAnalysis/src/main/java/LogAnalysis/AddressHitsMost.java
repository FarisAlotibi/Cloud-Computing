package LogAnalysis;

import org.apache.commons.cli.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public class AddressHitsMost {

    public static void main(String[] args) throws Exception 
    {
        long start = System.nanoTime();

        String INPUT_PATH = "", OUTPUT_PATH = "";
        boolean CACHE = false;

        //Commandline Parsing
        Options options = new Options();
        options.addOption("i", "input", true, "input path(HDFS)");
        options.addOption("o", "output", true, "output path(HDFS)");
        options.addOption("cache", "cache", true, "cached / not cached");

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options,args);
        if(cmd.hasOption("i")){
            INPUT_PATH = cmd.getOptionValue("i");
        } else{
            System.err.println("Input path is invalid");
            System.exit( 0 );
        }

        if(cmd.hasOption("o")){
            OUTPUT_PATH = cmd.getOptionValue("o");
        } else{
            System.err.println("Output path is invalid");
            System.exit( 0 );
        }

        if(cmd.hasOption("cache")){
            if(cmd.getOptionValue("cache") == "true")
                CACHE = true;
        }

        String filename = INPUT_PATH;

        // Define a configuration to use to interact with Spark
        SparkConf conf = new SparkConf().setMaster("local").setAppName("Log Analysis App");

        // Create a Java version of the Spark Context from the configuration
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Load the input data, which is a text file read from the command line
        JavaRDD<String> input = sc.textFile( filename );

        JavaRDD<String> urls = input.flatMap( s -> {
            
            String line = s.replace("\"\"", "-");
            String request = StringUtils.substringBetween(line ,"\"", "\"");
            String[] request_s = request.split("\\s+");
            String address = "";

            if(request_s.length >= 1)
                address = request_s[1];

            return Arrays.asList(address).iterator();
        });

        if(CACHE) {
        }
        else {
        }

        JavaPairRDD<String, Integer> counts;

        if(CACHE) {
            counts = urls.mapToPair( t -> new Tuple2(t, 1)).reduceByKey( (x, y) -> (int)x + (int)y ).cache();
        }
        else {
            counts = urls.mapToPair( t -> new Tuple2(t, 1)).reduceByKey( (x, y) -> (int)x + (int)y );
        }

        JavaPairRDD<String, Integer> sortedCounts;

        if(CACHE) {
            sortedCounts = counts.mapToPair(x -> x.swap()).sortByKey(false).mapToPair(x -> x.swap()).cache();
        }
        else {
            sortedCounts = counts.mapToPair(x -> x.swap()).sortByKey(false).mapToPair(x -> x.swap());
        }

        List<Tuple2<String, Integer>> top = sortedCounts.take(1);
        System.out.println("*****************************************************");
               
        for(Tuple2<String, Integer> entry : top) {
            System.out.println(entry._1 + "\t" + entry._2);
        }
        System.out.println("*****************************************************");

        // Save the word count back out to a text file, causing evaluation.
        sortedCounts.saveAsTextFile( OUTPUT_PATH );

        long end = System.nanoTime();
        long time = end-start;
        System.out.println("*****************************************************");
        System.out.println("\nTime Performance = " + time + " ns\n");
        System.out.println("*****************************************************");
    }
}