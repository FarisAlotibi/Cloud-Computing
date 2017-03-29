// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collection;

// import org.apache.spark.api.java.*;
// import org.apache.spark.api.java.function.*;
// import org.apache.spark.SparkConf;
// import scala.Tuple2;

// public class WordCount {
//   public static void main(String[] args) {
//     JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("Spark Count"));
    
//     // split each document into words
//     JavaRDD<String> tokenized = sc.textFile(args[0]).flatMap(
//       l -> Arrays.asList(s.split(" "))
//     );
    
//     // count the occurrence of each word
//     JavaPairRDD<String, Integer> counts = tokenized.mapToPair(
//       new PairFunction<String, String, Integer>() {
//         @Override
//         public Tuple2<String, Integer> call(String s) {
//           return new Tuple2<String, Integer>(s, 1);
//         }
//       }
//     ).reduceByKey(
//       new Function2<Integer, Integer, Integer>() {
//         @Override
//         public Integer call(Integer i1, Integer i2) {
//           return i1 + i2;
//         }
//       }, 1		//number of reducers = 1
//     );

//     counts.sortByKey(true).saveAsTextFile(args[1]); 
//     System.exit(0);
//   }
// }