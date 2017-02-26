# Ubuntu 16.04 with Hadoop 2.7.3 Docker image

## Build the image

If you'd like to try directly from the Dockerfile you can build the image as:

```
docker build  -t hadoop-docker .
```

## Start a container

In order to use the Docker image you have just build use:

```
docker run -it hadoop-docker /etc/bootstrap.sh -bash
```

### Testing

You can run one of the stock examples:

**Example 1:**
```
cd $HADOOP_PREFIX
hadoop jar share/hadoop/mapreduce/hadoop-*-examples-*.jar wordcount input ex1output

# check the output
hdfs dfs -cat ex1output/*
```

**Example 2:**
```
cd $HADOOP_PREFIX
hadoop jar share/hadoop/mapreduce/hadoop-*-examples-*.jar grep input ex2output 'dfs[a-z.]+'

# check the output
hdfs dfs -cat ex2output/*
```

**Example 3:**
```
cd $HADOOP_PREFIX
hadoop jar share/hadoop/mapreduce/hadoop-*-examples-*.jar pi 2 5
```

