# LogAnalysis
It contains several MapReduce/Hadoop programs that are to analyze real anonymous logs to answer several questions based on the log.

## Log File Format
The log file is in Common Log Format:
```
10.223.157.186 - - [15/Jul/2009:14:58:59 -0700] "GET /favicon.ico HTTP/1.1" 404 209
```
```
%h %l %u %t \"%r\" %>s %b
```
* %h is the IP address of the client.
* %l is identity of the client, or "-" if it's unavailable.
* %u is username of the client, or "-" if it's unavailable • %t is the time that the server finished processing the request. The format is [day/month/year:hour:minute:second zone].
* %r is the request line from the client is given (in double quotes). It contains the method, path, query-string, and protocol or the request.
* %>s is the status code that the server sends back to the client. You will see see mostly status codes 200 (OK - The request has succeeded), 304 (Not Modified) and 404 (Not Found). See more information on status codes in W3C.org.
* %b is the size of the object returned to the client, in bytes. It will be "-" in case of status code 304.

## JAVA Files Build
You can use _Maven_ to build the codes as:
```
mvn clean install
```

## Answering Questions
1- How many hits were made to the website item “/assets/img/home-logo.png”?
```
# hadoop jar LogAnalysis.jar AddressHitsCount input_file output_file "/path/to/count"
hadoop jar LogAnalysis.jar AddressHitsCount access_log output1 "/assets/img/home-logo.png"
```

2- How many hits were made from the IP: 10.153.239.5?
```
# hadoop jar LogAnalysis.jar IPCount input_file output_file "0.0.0.0"
hadoop jar LogAnalysis.jar IPCount access_log output2 "10.153.239.5"
```

3- Which path in the website has been hit most? How many hits were made to the path?
```
# hadoop jar LogAnalysis.jar AddressHitsMost input_file output_file
hadoop jar LogAnalysis.jar AddressHitsMost access_log output3
```

4- Which IP accesses the website most? How many accesses were made by it?
```
# hadoop jar LogAnalysis.jar IPMax input_file output_file
hadoop jar LogAnalysis.jar IPMax access_log output4
```

## Check Answers
To check the results, you may run the following code:
```
hdfs dfs -cat output1/*
hdfs dfs -cat output2/*
hdfs dfs -cat output3/*
hdfs dfs -cat output4/*
```