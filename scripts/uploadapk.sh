#!/bin/bash

version=$1

if [[ $# -lt 1 ]]; then
  echo "Usage: ./scripts/uploadapk.sh <versionnumber>"
  echo "E.g.: ./scripts/uploadapk.sh 2.0"
  exit -1
fi

servid="pancast"
server="pancast.cs.ubc.ca"
#servdir="/home/$servid/downloads"
servdir="/var/www/html/$server/apk"

rootdir="/Users/aasthakm/workspace-research/pancast/dev2/pancast-android"
#apkdir="$rootdir/app/build/outputs/apk/debug"

for cfg in "debug" "release"; do
  apkdir="$rootdir/app/$cfg"
  apkfile="$apkdir/app-$cfg.apk"
  outfile="pancast-dongle-$cfg-2.0.apk"

  cmd="scp -rq $apkfile $servid@$server:$servdir/$outfile"
  echo "$cmd"
  eval "$cmd"

  cmd="ssh $servid@$server \"chmod u+x,g+wx $servdir/$outfile\""
  echo "$cmd"
  eval "$cmd"
done
