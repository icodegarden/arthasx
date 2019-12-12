#!/bin/bash

arthasxHome=""
targetIp=""
tunnelServerIp=""
agentId=""
pid=""

for i in $*            
do
  array=(${i//=/ }) 
  if [[ ${array[0]} == '--arthasx-home' ]];then
    arthasxHome=${array[1]}
  fi
  if [[ ${array[0]} == '--target-ip' ]];then
    targetIp=${array[1]}
  fi
  if [[ ${array[0]} == '--tunnel-server' ]];then
    tunnelServerIp=${array[1]}
  fi
  if [[ ${array[0]} == '--agent-id' ]];then
    agentId=${array[1]}
  fi
  if [[ ${array[0]} == '--pid' ]];then
    pid=${array[1]}
  fi
done

if [[ $arthasxHome == "" ]];then
  echo '--arthasx-home must not null'
  exit 40
fi
if [[ $targetIp == "" ]];then
  echo '--target-ip must not null'
  exit 40
fi
if [[ $tunnelServerIp == "" ]];then
  echo '--tunnel-server must not null'
  exit 40
fi
if [[ $agentId == "" ]];then
  echo '--agent-id must not null'
  exit 40
fi
if [[ $pid == "" ]];then
  echo '--pid must not null'
  exit 40
fi

java -jar ${arthasxHome}/arthas-boot.jar --attach-only --target-ip $targetIp --tunnel-server 'ws://'${tunnelServerIp}':7777/ws' --agent-id $agentId $pid

echo exec cmd is [java -jar ${arthasxHome}/arthas-boot.jar --attach-only --target-ip $targetIp --tunnel-server 'ws://'${tunnelServerIp}':7777/ws' --agent-id $agentId $pid]
