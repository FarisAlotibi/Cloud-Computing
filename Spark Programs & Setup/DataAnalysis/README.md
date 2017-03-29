# Build the program to get the Jar file {in the program directory}
    - mvn package 
# Run the program using Spark submitter
    - spark/bin/spark-submit --class Class-Name --master Cluster Name {local, yarn, spark://master:7077} Jar-File-Path -i Input-File-Path -o Output-Name
    - Ex/ 
        - spark/bin/spark-submit --class LastfmAnalysis --master yarn DataAnalysis/DataAnalysis-spark.jar -i user_artists.dat -o output3
    - Input file and output will be in HDFS