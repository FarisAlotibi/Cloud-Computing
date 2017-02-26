export MASTER_IP=138.197.42.93
export SLAVE_IP=162.243.40.202

#ssh login
#cloud-vm-59	138.197.42.93
#cloud-vm-60	162.243.40.202
ssh $MASTER_IP


# Commands on every node
#setup JAVA 7
sudo add-apt-repository ppa:webupd8team/java 
sudo apt-get update
sudo apt-get install oracle-java7-installer
sudo update-alternatives --config java

# Config java path
# add JAVA_HOME="/usr/lib/jvm/java-7-oracle" to /etc/environment
sudo nano /etc/environment

#test java setup
source /etc/environment
echo $JAVA_HOME
java -version

#setup ssh and rsync
sudo apt-get install ssh
sudo apt-get install rsync

#download Hadoop and test locally
cd /usr/local/
wget http://www-us.apache.org/dist/hadoop/common/hadoop-2.7.3/hadoop-2.7.3.tar.gz
tar -zxf hadoop-2.7.3.tar.gz
ln -s hadoop-2.7.3 hadoop
cd hadoop

# set to the root of your Java installation
# export JAVA_HOME=/usr/lib/jvm/java-7-oracle
nano etc/hadoop/hadoop-env.sh

#test single node hadoop
mkdir input
cp etc/hadoop/*.xml input
bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.3.jar grep input output 'dfs[a-z.]+'
cat output/*

# setup etc/hosts with the IP and Node Name on every node
# 138.197.42.93 master
# 162.243.40.202 slave
# Warning: delete every IP setting with 127.0.1.1 and 127.0.0.1
nano /etc/hosts

