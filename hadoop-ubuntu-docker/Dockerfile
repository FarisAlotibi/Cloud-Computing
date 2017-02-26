# Creates pseudo distributed hadoop 2.7.3 on Ubuntu 16.04

FROM ubuntu:16.04
MAINTAINER Mohammed_Faris

USER root

# ****************************************************************
# INSTALLING DEV. TOOLS
#   (java, curl, tar, sudo, openssh-server, openssh-client, rsync)
# ****************************************************************
RUN apt-get update
RUN apt-get install -y default-jdk curl tar sudo openssh-server openssh-client rsync

# setting java variable and path
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/jre
ENV PATH $PATH:$JAVA_HOME/bin

# passwordless ssh
RUN rm -f /etc/ssh/ssh_host_dsa_key /etc/ssh/ssh_host_rsa_key /root/.ssh/id_rsa && \
    ssh-keygen -q -N "" -t dsa -f /etc/ssh/ssh_host_dsa_key && \
    ssh-keygen -q -N "" -t rsa -f /etc/ssh/ssh_host_rsa_key && \
    ssh-keygen -q -N "" -t rsa -f /root/.ssh/id_rsa && \
    cp /root/.ssh/id_rsa.pub /root/.ssh/authorized_keys

# ****************************************************************
# INSTALLING HADOOP
# ****************************************************************
RUN curl -s http://apache.mirrors.tds.net/hadoop/common/hadoop-2.7.3/hadoop-2.7.3.tar.gz | tar -xz -C /usr/local/
RUN cd /usr/local && ln -s ./hadoop-2.7.3 hadoop

# ****************************************************************
# HADOOP VARIABLEs & PATHs
# ****************************************************************
ENV HADOOP_HOME=/usr/local/hadoop
ENV HADOOP_PREFIX /usr/local/hadoop
ENV HADOOP_COMMON_HOME $HADOOP_HOME
ENV HADOOP_HDFS_HOME $HADOOP_HOME
ENV HADOOP_MAPRED_HOME $HADOOP_HOME
ENV HADOOP_YARN_HOME $HADOOP_HOME
ENV HADOOP_CONF_DIR $HADOOP_HOME/etc/hadoop
ENV YARN_CONF_DIR $HADOOP_PREFIX/etc/hadoop
ENV HADOOP_COMMON_LIB_NATIVE_DIR $HADOOP_HOME/lib/native
# paths
ENV PATH $PATH:$HADOOP_HOME/bin
ENV PATH $PATH:$HADOOP_HOME/sbin
# fixing WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... 
# (on x86 CPU)
# ENV HADOOP_OPTS "-Djava.library.path=$HADOOP_HOME/lib"
# (on x64 CPU)
ENV HADOOP_OPTS="-Djava.library.path=$HADOOP_HOME/lib/native"

# creating directories
RUN mkdir -p /usr/local/hadoop_store/hdfs/namenode && \
    mkdir -p /usr/local/hadoop_store/hdfs/datanode

# ****************************************************************
#  CONFIGURING HADOOP
# ****************************************************************
# hadoop-env.sh
RUN sed -i '/^export JAVA_HOME/ s:.*:export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre\nexport HADOOP_PREFIX=/usr/local/hadoop\nexport HADOOP_HOME=/usr/local/hadoop\n:' $HADOOP_PREFIX/etc/hadoop/hadoop-env.sh
RUN sed -i '/^export HADOOP_CONF_DIR/ s:.*:export HADOOP_CONF_DIR=/usr/local/hadoop/etc/hadoop/:' $HADOOP_PREFIX/etc/hadoop/hadoop-env.sh
# ----------------------------------------------------------------
# core-site.xml
ADD core-site.xml.template $HADOOP_PREFIX/etc/hadoop/core-site.xml.template
RUN sed s/HOSTNAME/localhost/ $HADOOP_PREFIX/etc/hadoop/core-site.xml.template > $HADOOP_PREFIX/etc/hadoop/core-site.xml
# ----------------------------------------------------------------
# hdfs-site.xml
ADD hdfs-site.xml $HADOOP_PREFIX/etc/hadoop/hdfs-site.xml
# ----------------------------------------------------------------
# mapred-site.xml
ADD mapred-site.xml.template $HADOOP_PREFIX/etc/hadoop/mapred-site.xml.template
RUN sed s/HOSTNAME/localhost/ $HADOOP_PREFIX/etc/hadoop/mapred-site.xml.template > $HADOOP_PREFIX/etc/hadoop/mapred-site.xml
# ----------------------------------------------------------------
# yarn-site.xml
ADD yarn-site.xml.template $HADOOP_PREFIX/etc/hadoop/yarn-site.xml.template
RUN sed s/HOSTNAME/localhost/ $HADOOP_PREFIX/etc/hadoop/yarn-site.xml.template > $HADOOP_PREFIX/etc/hadoop/yarn-site.xml
# ----------------------------------------------------------------
# Permissions
RUN chmod +x $HADOOP_HOME/sbin/start-dfs.sh && \
    chmod +x $HADOOP_HOME/sbin/start-yarn.sh 
# ----------------------------------------------------------------
# formating the New Hadoop Filesystem
RUN hdfs namenode -format
# ----------------------------------------------------------------

# ****************************************************************
# FIXING BUILD ERRORs
# ****************************************************************
# ERROR: 
#   /bin/sh: 1: /usr/local/hadoop/etc/hadoop/hadoop-env.sh: Permission denied
RUN ls -la /usr/local/hadoop/etc/hadoop/*-env.sh
RUN chmod +x /usr/local/hadoop/etc/hadoop/*-env.sh
RUN ls -la /usr/local/hadoop/etc/hadoop/*-env.sh
# ----------------------------------------------------------------
# ERROR: 
#   localhost: Host key verification failed.
#   0.0.0.0: Host key verification failed.
ADD ssh_config /root/.ssh/config
RUN chmod 600 /root/.ssh/config && \
    chown root:root /root/.ssh/config
# ----------------------------------------------------------------
# ERROR: (while executing hadoop program from bootstrap.sh)
#   java.net.ConnectException: Connection refused; 
RUN sed  -i "/^[^#]*UsePAM/ s/.*/#&/"  /etc/ssh/sshd_config && \
    echo "UsePAM no" >> /etc/ssh/sshd_config && \
    echo "Port 2122" >> /etc/ssh/sshd_config
# ----------------------------------------------------------------

# bootstrap.sh 
# the file that is going to execute after the build the VM
ADD bootstrap.sh /etc/bootstrap.sh
RUN chown root:root /etc/bootstrap.sh && \
    chmod 700 /etc/bootstrap.sh
ENV BOOTSTRAP /etc/bootstrap.sh


# creating the base directory on the DFS
RUN service ssh start && \
    $HADOOP_CONF_DIR/hadoop-env.sh && \
    start-dfs.sh && \
    start-yarn.sh && \
    hdfs dfs -mkdir -p /user/root
# copying some files to the file system
RUN service ssh start && \
    $HADOOP_CONF_DIR/hadoop-env.sh && \
    start-dfs.sh && \
    start-yarn.sh && \
    hdfs dfs -put $HADOOP_PREFIX/etc/hadoop/ input

CMD ["/etc/bootstrap.sh", "-d"]

# Hdfs ports
EXPOSE 50010 50020 50070 50075 50090 8020 9000
# Mapred ports
EXPOSE 10020 19888
#Yarn ports
EXPOSE 8030 8031 8032 8033 8040 8042 8088
#Other ports
EXPOSE 49707 2122 22