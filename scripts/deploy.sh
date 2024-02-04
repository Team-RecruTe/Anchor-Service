#!/bin/bash

## Set Variables
APP_NAME="anchor"
DEV_DIR="/home/ubuntu/$APP_NAME/dev"
JAR_NAME=$(ls $DEV_DIR/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
JAR_PATH="$DEV_DIR/build/libs/$JAR_NAME"
ENV_PATH="$DEV_DIR/env"

APP_LOG="$DEV_DIR/log/app.log"
ERROR_LOG="$DEV_DIR/log/error.log"

## Stop Application
CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> Application Not Found: 종료할 어플리케이션이 없습니다."
else
  echo "> Stop Application: 실행중인 어플리케이션을 종료합니다. $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

## Start Application
NOW=$(date +%c)
sudo chmod -R u+rwx $DEV_DIR
mkdir -p $DEV_DIR/log
echo "> Start Application: 어플리케이션을 실행합니다. || $JAR_PATH ($NOW)"
. $ENV_PATH/env.sh
nohup java -jar $JAR_PATH > $APP_LOG 2> $ERROR_LOG &