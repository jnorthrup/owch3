#!/bin/sh
# $Id: test.sh,v 1.2 2005/06/03 18:27:48 grrrrr Exp $
##
## $Log: test.sh,v $
## Revision 1.2  2005/06/03 18:27:48  grrrrr
## no message
##
##
#


for j in Domain IRC IRCBridge 
do 
set -x
for i in *.$j.conf 
do
	java -cp ../dist/*:../dist/lib/*  net.sourceforge.owch2.agent.$j -config $i&
done
done