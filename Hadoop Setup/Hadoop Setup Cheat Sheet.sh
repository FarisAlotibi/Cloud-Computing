#********************************************
#* Hadoop Setup Cheat Sheet By mharbi@me.com
#********************************************

sudo apt-get update
sudo apt-get install default-jdk

# To find the default Java path
readlink -f /usr/bin/java | sed "s:bin/java::"

nano /etc/hosts
# In the file, add:
	<YOUR-GIVEN-IP> master

sudo apt-get install openssh-server
sudo apt-get install ssh
sudo apt-get install rsync

ssh-keygen -t rsa -P ''
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

sudo /etc/init.d/ssh restart

ssh localhost
exit
ssh 0.0.0.0
exit
ssh master
exit

# Disable ipv6
sudo nano /etc/sysctl.conf
net.ipv6.conf.all.disable_ipv6 = 1
net.ipv6.conf.default.disable_ipv6 = 1
net.ipv6.conf.lo.disable_ipv6 = 1

# install Hadoop
wget http://apache.mirrors.tds.net/hadoop/common/hadoop-2.7.3/hadoop-2.7.3.tar.gz
sudo mv ~/hadoop-2.7.3.tar.gz /usr/local
cd /usr/local
sudo tar -xzvf hadoop-2.7.3.tar.gz
sudo rm hadoop-2.7.3.tar.gz
sudo ln -s hadoop-2.7.3 hadoop

# ADD HADOOP VARIABLES
nano ~/.bashrc
# HADOOP VARIABLES START
	export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre
	export HADOOP_HOME=/usr/local/hadoop
	export HADOOP_PREFIX=/usr/local/hadoop
	export HADOOP_MAPRED_HOME=$HADOOP_HOME
	export HADOOP_COMMON_HOME=$HADOOP_HOME
	export HADOOP_HDFS_HOME=$HADOOP_HOME
	export YARN_HOME=$HADOOP_HOME
	export HADOOP_YARN_HOME=$HADOOP_HOME
	export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
	# Native Path
	export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
	export HADOOP_OPTS="-Djava.library.path=$HADOOP_HOME/lib"
	# path
	export PATH=$PATH:$HADOOP_HOME/bin
	export PATH=$PATH:$JAVA_HOME/bin
	export PATH=$PATH:$HADOOP_HOME/sbin
# HADOOP VARIABLES END
source ~/.bashrc


nano /usr/local/hadoop/etc/hadoop/hadoop-env.sh
	export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre
	export HADOOP_HOME_WARN_SUPPRESS="TRUE"

rm -r /app/hadoop/tmp/
sudo mkdir -p /app/hadoop/tmp
nano /usr/local/hadoop/etc/hadoop/core-site.xml
# In this file, enter the following content in between the <configuration></configuration> tag:
# START
	<property>
	        <name>hadoop.tmp.dir</name>
	        <value>/app/hadoop/tmp</value>
	        <description>A base for other temporary directories.</description>
	</property>
	<property>
	        <name>fs.default.name</name>
	        <value>hdfs://master:9000</value>
	        <description>The name of the default file system.
	                A URI whose scheme and authority determine the FileSystem implementation.
	                The uri s scheme determines the config property (fs.SCHEME.impl) naming
	                the FileSystem implementation class.  The uri s authority is used to
	                determine the host, port, etc. for a filesystem.
	        </description>
	 </property>
# END

nano /usr/local/hadoop/etc/hadoop/yarn-site.xml
# In this file, enter the following content in between the <configuration></configuration> tag:
# START
	<property>
	   <name>yarn.nodemanager.aux-services</name>
	   <value>mapreduce_shuffle</value>
	</property>

	<property>
	   <name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>
	   <value>org.apache.hadoop.mapred.ShuffleHandler</value>
	</property>

    <property>
        <name>yarn.scheduler.minimum-allocation-mb</name>
        <value>128</value>
        <description>Minimum limit of memory to allocate to each container request at the Resource Manager.</description>
    </property>
    <property>
        <name>yarn.scheduler.maximum-allocation-mb</name>
        <value>2048</value>
        <description>Maximum limit of memory to allocate to each container request at the Resource Manager.</description>
    </property>
    <property>
        <name>yarn.scheduler.minimum-allocation-vcores</name>
        <value>1</value>
        <description>The minimum allocation for every container request at the RM, 
        in terms of virtual CPU cores. Requests lower than this will not take effect, and the specified
        </description>
    </property>
    <property>
        <name>yarn.scheduler.maximum-allocation-vcores</name>
        <value>1</value>
        <description>The maximum allocation for every container request at the RM, 
        in terms of virtual CPU cores. Requests higher than this will not take effect, and will get cap
        </description>
    </property>
    <property>
        <name>yarn.nodemanager.resource.memory-mb</name>
        <value>2048</value>
        <description>Physical memory, in MB, to be made available to running containers</description>
    </property>
    <property>
        <name>yarn.nodemanager.resource.cpu-vcores</name>
        <value>2</value>
        <description>Number of CPU cores that can be allocated for containers.</description>
    </property>
    <property>
        <name>yarn.nodemanager.resource.detect-hardware-capabilities</name>
        <value>false</value>
    </property>
    <property>
        <name>yarn.nodemanager.vmem-check-enabled</name>
        <value>false</value>
    </property>
    <property>
		<name>yarn.resourcemanager.scheduler.address</name>
		<value>master:8030</value>
	</property>

	<property>
		<name>yarn.resourcemanager.address</name>
		<value>master:8032</value>
	</property>

	<property>
		<name>yarn.resourcemanager.webapp.address</name>
		<value>master:8088</value>
	</property>

	<property>
		<name>yarn.resourcemanager.resource-tracker.address</name>
		<value>master:8031</value>
	</property>

	<property>
		<name>yarn.resourcemanager.admin.address</name>
		<value>master:8033</value>
	</property>
# END

cp /usr/local/hadoop/etc/hadoop/mapred-site.xml.template /usr/local/hadoop/etc/hadoop/mapred-site.xml
nano /usr/local/hadoop/etc/hadoop/mapred-site.xml
# In this file, enter the following content in between the <configuration></configuration> tag:
# START
	<property>
        <name>yarn.app.mapreduce.am.resource.mb</name>
        <value>1024</value>
    </property>
    <property>
        <name>yarn.app.mapreduce.am.command-opts</name>
        <value>-Xmx768m</value>
    </property>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
        <description>Execution framework.</description>
    </property>
    <property>
        <name>mapreduce.map.cpu.vcores</name>
        <value>1</value>
        <description>The number of virtual cores required for each map task.</description>
    </property>
    <property>
        <name>mapreduce.reduce.cpu.vcores</name>
        <value>1</value>
        <description>The number of virtual cores required for each map task.</description>
    </property>
    <property>
        <name>mapreduce.map.memory.mb</name>
        <value>1024</value>
        <description>Larger resource limit for maps.</description>
    </property>
    <property>
        <name>mapreduce.map.java.opts</name>
        <value>-Xmx768m</value>
        <description>Heap-size for child jvms of maps.</description>
    </property>
    <property>
        <name>mapreduce.reduce.memory.mb</name>
        <value>1024</value>
        <description>Larger resource limit for reduces.</description>
    </property>
    <property>
        <name>mapreduce.reduce.java.opts</name>
        <value>-Xmx768m</value>
        <description>Heap-size for child jvms of reduces.</description>
    </property>
    <property>
        <name>mapreduce.reduce.memory.mb</name>
        <value>1024</value>
        <description>Larger resource limit for reduces.</description>
    </property>
    <property>
        <name>mapreduce.reduce.java.opts</name>
        <value>-Xmx768m</value>
        <description>Heap-size for child jvms of reduces.</description>
    </property>
    <property>
       <name>mapreduce.jobhistory.address</name>
       <value>master:10020</value>
  	</property>
  	<property>
		<name>mapreduce.jobhistory.webapp.address</name>
		<value>master:19888</value>
    </property>
	<property>
 		<name>mapreduce.jobtracker.address</name>
 		<value>master:54311</value>
	</property>
# END

mkdir -p /usr/local/hadoop_store/hdfs/namenode
mkdir -p /usr/local/hadoop_store/hdfs/datanode
nano /usr/local/hadoop/etc/hadoop/hdfs-site.xml
# In this file, enter the following content in between the <configuration></configuration> tag:
# START
 <property>
  <name>dfs.replication</name>
  <value>3</value>
  <description>Default block replication.The actual number of replications can be specified when the file is created.
         The default is used if replication is not specified in create time.
  </description>
 </property>
 <property>
   <name>dfs.namenode.name.dir</name>
   <value>file:/usr/local/hadoop_store/hdfs/namenode</value>
 </property>
 <property>
   <name>dfs.datanode.data.dir</name>
   <value>file:/usr/local/hadoop_store/hdfs/datanode</value>
 </property>

# END

# Format the New Hadoop Filesystem
hdfs namenode -format

# Start Hadoop
start-dfs.sh
start-yarn.sh
mr-jobhistory-daemon.sh --config $HADOOP_CONF_DIR start historyserver

jps

hadoop fs -ls
# if error. root is the username
hdfs dfs -mkdir -p /user/root

# For TEST
hadoop jar /usr/local/hadoop/share/hadoop/mapreduce/hadoop-*-examples-*.jar pi 2 5

hadoop jar /usr/local/hadoop/share/hadoop/mapreduce/hadoop-*-examples-*.jar wordcount input output
hadoop jar /usr/local/hadoop/share/hadoop/mapreduce/hadoop-*-examples-*.jar grep input output 'dfs[a-z.]+'


# TO STOP FIREWALL ON UBUNTU
sudo service ufw stop