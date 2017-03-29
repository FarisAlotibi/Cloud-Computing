import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

public class LastfmAnalysis {

    public static void main(String[] args) throws Exception 
    {
        if( args.length == 0 )
        {
            System.out.println( "Usage: WordCount <file>" );
            System.exit( 0 );
        }

        String filename = args[0];

        // Define a configuration to use to interact with Spark
        SparkConf conf = new SparkConf().setMaster("local").setAppName("Lastfm Analysis App");

        // Create a Java version of the Spark Context from the configuration
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Load the input data, which is a text file read from the command line
        JavaRDD<String> input = sc.textFile( filename );

        // with lambdas: split the input string into artists
        JavaRDD<String> artists = input.flatMap( s -> {
            String[] fields = s.split( "\t" );
            return Arrays.asList(fields[1] + "\t" + fields[2]).iterator();
            //Arrays.asList(new String[]{fields[1], fields[2]}).iterator();
        });

        final String header = artists.first();
        artists = artists.filter(s -> !s.equalsIgnoreCase(header));

        // with lambdas: transform the collection of artists into pairs (word and 1) and then count them
        JavaPairRDD<String, Integer> counts = artists.mapToPair( t -> {
            if(t.trim() != "") {
                String[] fields = t.split( "\t" );
                return new Tuple2( fields[0], Integer.parseInt(fields[1]) );
            }
            return new Tuple2(t, 0);
        }).reduceByKey( (x, y) -> (int)x + (int)y );

        JavaPairRDD<String, Integer> sortedCounts = counts.mapToPair(x -> x.swap()).sortByKey(false).mapToPair(x -> x.swap());

        // System.out.println(sortedCounts.take(10).getClass().getSimpleName());
        List<Tuple2<String, Integer>> top10 = sortedCounts.take(10);
        System.out.println("*****************************************************");
        System.out.println("artist\tlistening_count");
        
        for(Tuple2<String, Integer> entry : top10) {

            System.out.println(entry._1 + "\t" + entry._2);
        }
        System.out.println("*****************************************************");

        // sortedCounts.take(10).foreach(System.out.println);
        // sortedCounts.take(10).foreach(indvArray -> indvArray.foreach(println));

        // Save the word count back out to a text file, causing evaluation.
        // sortedCounts.saveAsTextFile( "output" );
    }
}

