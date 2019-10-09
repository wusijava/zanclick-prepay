#!/bin/bash
SERVER_NAME='prepay'
JAR_NAME='prepay.jar'
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/config
# 获取应用的端口号
SERVER_PORT=`cat $CONF_DIR/application.yml|tr -s '\n'|sed -nr '/server:/,+1p'|sed -nr '/^  port: [0-9]+/ s/.*port: ([0-9]+).*/\1/p'`
PIDS=`ps -ef | grep java | grep "$CONF_DIR" |awk '{print $2}'`
if [ "$1" = "status" ]; then
  if [ -n "$PIDS" ]; then
    echo "The $SERVER_NAME is running...!"
    echo "PID: $PIDS"
    exit 0
  else
    echo "The $SERVER_NAME is stopped"
    exit 0
  fi
elif [ "$1" = "start" ]; then
    if [ -n "$PIDS" ]; then
      echo "ERROR: The $SERVER_NAME already started!"
      echo "PID: $PIDS"
      exit 1
    fi
    if [ -n "$SERVER_PORT" ]; then
      SERVER_PORT_COUNT=`netstat -tln | grep $SERVER_PORT | wc -l`
      if [ $SERVER_PORT_COUNT -gt 0 ]; then
        echo "ERROR: The $SERVER_NAME port $SERVER_PORT already used!"
        exit 1
      fi
    fi
    LOGS_DIR=$DEPLOY_DIR/logs
    if [ ! -d $LOGS_DIR ]; then
      mkdir $LOGS_DIR
    fi
    STDOUT_FILE=$LOGS_DIR/catalina.out
    JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -DLOGS_DIR=$LOGS_DIR"
    JAVA_DEBUG_OPTS=""
    if [ "$2" = "debug" ]; then
      JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
    fi
    JAVA_JMX_OPTS=""
    if [ "$2" = "jmx" ]; then
      JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
    fi
    JAVA_MEM_OPTS=""
    BITS=`java -version 2>&1 | grep -i 64-bit`
    if [ -n "$BITS" ]; then
      JAVA_MEM_OPTS=" -server -Xmx512m -Xms512m -Xmn256m -XX:PermSize=128m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
    else
      JAVA_MEM_OPTS=" -server -Xms512m -Xmx512m -XX:SurvivorRatio=2 -XX:+UseParallelGC "
    fi
    CONFIG_FILES=" -Dlogging.path=$LOGS_DIR -Dlogging.config=$CONF_DIR/logback.xml -Dspring.config.location=$CONF_DIR/application.yml "
    echo -e "Starting the $SERVER_NAME ..."
    echo "JAVA_OPTS:$JAVA_OPTS"
    if [ "$2" = "debug" ]; then
      echo "JAVA_DEBUG_OPTS:$JAVA_DEBUG_OPTS"
    fi
    if [ "$2" = "jmx" ]; then
      echo "JAVA_JMX_OPTS:$JAVA_JMX_OPTS"
    fi
    echo "JAVA_MEM_OPTS:$JAVA_MEM_OPTS"
    nohup java $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS $CONFIG_FILES -jar $DEPLOY_DIR/lib/$JAR_NAME > $STDOUT_FILE 2>&1 &
    COUNT=0
    while [ $COUNT -lt 1 ]; do
      echo -e ".\c"
      sleep 1
      if [ -n "$SERVER_PORT" ]; then
        COUNT=`netstat -an | grep $SERVER_PORT | wc -l`
      else
       COUNT=`ps -ef | grep java | grep "$DEPLOY_DIR" | awk '{print $2}' | wc -l`
      fi
      if [ $COUNT -gt 0 ]; then
        break
      fi
    done
    echo "OK!"
    PIDS=`ps -ef | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
    echo "PID: $PIDS"
    echo "STDOUT: $STDOUT_FILE"
elif [ "$1" = "stop" ]; then
  if [ -z "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME does not started!"
    exit 1
  fi
  echo -e "Stopping the $SERVER_NAME ...\c"
  for PID in $PIDS ; do
    kill $PID > /dev/null 2>&1
  done
  COUNT=0
  while [ $COUNT -lt 1 ]; do
    echo -e ".\c"
    sleep 1
    COUNT=1
    for PID in $PIDS ; do
      PID_EXIST=`ps -ef -p $PID | grep java`
      if [ -z "$PID_EXIST" ]; then
        COUNT=0
        break
      fi
    done
  done
  echo "OK!"
  echo "PID: $PIDS"
elif [ "$1" = "restart" ]; then
  "$DEPLOY_DIR/bin/start.sh" stop
  sleep 1
  "$DEPLOY_DIR/bin/start.sh" start "$2"
else
  echo -e "Usage:\t start \t\t start the server,with [debug|jmx] behind will start with the chosen mode \n\t stop   \t stop the server \n\t status \t show server status\n\t restart\t restart the server,like start"
fi
