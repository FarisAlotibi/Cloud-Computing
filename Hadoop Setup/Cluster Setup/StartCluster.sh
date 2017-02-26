export MASTER_IP=138.197.42.93
export SLAVE_IP=162.243.40.202

cd /usr/local/hadoop

#start cluster
sbin/start-dfs.sh
sbin/start-yarn.sh

#start job history server
$HADOOP_PREFIX/sbin/mr-jobhistory-daemon.sh --config $HADOOP_CONF_DIR start historyserver

#test with jps
jps
ssh slave
jps
exit

# test with create an directory on HDFS
hdfs dfs -mkdir /input
hdfs dfs -ls /
hdfs dfs -mkdir /user
hdfs dfs -mkdir /user/root
hdfs dfs -put etc/hadoop/* input
hdfs dfs -ls /user/root/input

hdfs dfs -mkdir /input && hdfs dfs -mkdir /user && hdfs dfs -mkdir /user/root

bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.3.jar pi 2 5
bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.3.jar wordcount input/ output/
hdfs dfs -cat output/*
hdfs dfs -rmr output