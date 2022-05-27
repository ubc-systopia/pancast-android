#!/bin/bash

rootdir="/Users/aasthakm/workspace-research/pancast/dev2/pancast-android"
apkdir="$rootdir/app/build/outputs/apk/debug"
apkfile="$apkdir/app-debug.apk"

servid="pancast"
server="pancast.cs.ubc.ca"
servdir="/home/$servid/downloads"
outfile="pancast-dongle-2.0.apk"

cmd="scp -rq $apkfile $servid@$server:$servdir/$outfile"
echo "$cmd"
eval "$cmd"

cmd="ssh $servid@$server \"chmod u+x,g+wx $servdir/$outfile\""
echo "$cmd"
eval "$cmd"
