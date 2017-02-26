export MASTER_IP=138.197.42.93
export SLAVE_IP=162.243.40.202

# generate ssh key on master
ssh-keygen -t rsa -P ""

#delivery ssh key from master to every node
ssh-copy-id -i $HOME/.ssh/id_rsa.pub root@master
ssh-copy-id -i $HOME/.ssh/id_rsa.pub root@slave

# test none-password SSH login
ssh slave

#Configure cluster
cd /usr/local/hadoop

#Appoint master on every node
nano etc/hadoop/masters
#Appoint slaves on master node
#master and slave nodes both act as slaves
nano etc/hadoop/slaves

#setup enviroments in $HOME/.bashrc
## Set Hadoop-related environment variables
export HADOOP_PREFIX=/usr/local/hadoop
export HADOOP_HOME=/usr/local/hadoop
export HADOOP_CONF_DIR=${HADOOP_HOME}/etc/hadoop
export HADOOP_MAPRED_HOME=${HADOOP_HOME}
export HADOOP_COMMON_HOME=${HADOOP_HOME}
export HADOOP_HDFS_HOME=${HADOOP_HOME}
export YARN_HOME=${HADOOP_HOME}
export HADOOP_LIBEXEC_DIR=${HADOOP_HOME}/libexec
export HADOOP_COMMON_LIB_NATIVE_DIR=${HADOOP_HOME}/lib/native
export HADOOP_OPTS="-Djava.library.path=${HADOOP_HOME}/lib"

nano ~/.bashrc
#copy .bashrc
scp -r .bashrc root@slave:/root/

source ~/.bashrc

## Add Hadoop bin/ directory to PATH
export PATH=$PATH:$HADOOP_PREFIX/bin

#create directory for HDFS
#create directories for Name node and data node on master
mkdir -p /hdfs/namenode
mkdir -p /hdfs/datanode
#create direcotry for Datanode on slave
ssh root@slave "mkdir -p /hdfs/datanode"

#configure the setting
cd $HADOOP_PREFIX/etc/hadoop/
#See the example in the HadoopTutorial package
nano core-site.xml
nano hdfs-site.xml
nano yarn-site.xml
nano mapred-site.xml
#copy the setting to slave
scp -r *.xml root@slave:/usr/local/hadoop/etc/hadoop/

#Formate the namenode
hadoop namenode -format
