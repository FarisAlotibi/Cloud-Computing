#****************************************************
#* Install and Run Spark 2.x on multi-node cluster – 
#* A Step by Step Guide By alotibifo@gmail.com
#****************************************************

sudo apt-get update

# Install Scala
sudo apt-get install scala

nano /etc/hosts
# In the file, add:
	<YOUR-GIVEN-IP1> master
    <YOUR-GIVEN-IP2> slave

ssh localhost
exit
ssh 0.0.0.0
exit
ssh master
exit
ssh slave
exit

cd ~

# Install Spark on Master
wget http://d3kbcqa49mib13.cloudfront.net/spark-2.1.0-bin-hadoop2.7.tgz
sudo mv ~/spark-2.1.0-bin-hadoop2.7.tgz /usr/local
cd /usr/local
sudo tar -xzvf spark-2.1.0-bin-hadoop2.7.tgz
sudo rm spark-2.1.0-bin-hadoop2.7.tgz
sudo ln -s spark-2.1.0-bin-hadoop2.7 spark

# ADD SPARK VARIABLES
nano ~/.bashrc
    export SPARK_HOME=/usr/local/spark
    export PATH=$PATH:$SPARK_HOME/bin
source ~/.bashrc

# Spark configuration
cp $SPARK_HOME/conf/spark-env.sh.template $SPARK_HOME/conf/spark-env.sh
nano $SPARK_HOME/conf/spark-env.sh
    # add/edit the following:
    export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre
    # this variable defines the amount of parallelism each Spark Worker node has.
    # it represents the number of Spark tasks (or threads) a Spark Worker can give to its Spark Executors.
    export SPARK_WORKER_CORES=8 

# Add salves
# Create configuration file slaves
nano $SPARK_HOME/conf/slaves
    # add following entries:
    slave

# ----------------------------------------------------------------------------------------------
# “Apache Spark has been installed successfully on Master, now deploy Spark on all the Slaves”
# ----------------------------------------------------------------------------------------------

# Copy setups from master to all the slaves
    # Create tar-ball of configured setup:
    cd /usr/local
    sudo tar -czf spark.tar.gz spark

    # Copy the configured tar-ball on all the slaves
    scp spark.tar.gz slave:~

# Install Spark On Slave
# Setup Pre-requisites on all the slaves:
    # Run following steps on all the slaves (or worker nodes):
        # “1. Add Entries in hosts file”
        # “2. Install Java 7”
        # “3. Install Scala”

cd /usr/local
sudo tar -xzvf spark.tar.gz

# ---------------------------------------------------------------------------------------------------------
# “Congratulations Apache Spark has been installed on all the Slaves. Now Start the daemons on the Cluster”
# ---------------------------------------------------------------------------------------------------------

# BACK TO MASTER AND RUN THIS COMMANDs:
    # Start Spark Cluster
    $SPARK_HOME/sbin/start-all.sh

    # Check whether daemons services have been started
    jps

    # Spark Master UI
    http://MASTER-IP:8080/

    # Spark application UI
    http://MASTER-IP:4040/

#Test

    #run shell
        $ ./bin/spark-shell --master yarn --deploy-mode client

    #run SparkPi on Yarn
        Master=yarn://host:7077 spark/bin/run-example SparkPi

        run-example --master yarn SparkPi 10
        
    # run Spark word count on yarn
        spark/bin/spark-submit --class org.apache.spark.examples.JavaWordCount --master yarn spark/examples/jars/spark-examples_2.11-2.1.0.jar sample.txt
        
    #run SparkPi

        #old version
        Master=spark://host:7077 spark/bin/run-example SparkPi		
        
        #new version
        spark/bin/run-example --master spark://master:7077 SparkPi	

        run-example SparkPi 10

# Useful Commands
    #change owner of the file
        chown -R user directory/ 
        
    #run the spark shell
        spark/bin/spark-shell

    #example of shell
        sc.parallelize(1 to 1000).count()

    #yarn monitoring
        (IP)master:8088

    #spark monitoring
        (IP)master:8080
        
    # put file into hadoop file system
        hadoop/bin/hadoop fs -put sample.txt

        hdfs dfs -put sample.txt
        
    # list, mkdir, cat files in hadoop/hdfs
        hadoop fs -ls
                -mkdir
                -cat

        hdfs dfs -ls 
                 -mkdir 
                 -cat
        
    # open lookup jar file content
        jar tf target/spark-example.jar

	
	