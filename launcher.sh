#!/bin/bash


# Change this to your netid
netid=nxd122930

#
# Root directory of your project
PROJDIR=$HOME/ACN/Project

#
# This assumes your config file is named "config.txt"
# and is located in your project directory
#
echo "Enter the topology file: "
read name

CONFIG=$PROJDIR/$name

#
# Directory your java classes are in
#
BINDIR=$PROJDIR

#
# Your main project class
#
PROG=MainClass_Client

#n=1

cat $CONFIG | sed -e "s/#.*//" | sed -e "/^\s*$/d" | 
(
    read i
    echo $i
    while read line 
    do
        host=$( echo $line | cut -f4 -d"," | cut -f1 -d"/" | cut -f2 -d" " )

	n=$( echo $line | cut -f1 -d",")

        echo $host

	echo $n

	#echo "ssh $netid@$host java $BINDIR/$PROG $n $CONFIG" 
        ssh -l "$netid" "$host" "cd $BINDIR;java MainClass_Client $n $CONFIG" &
	#n=$(( n + 1 ))
    done
   
)


