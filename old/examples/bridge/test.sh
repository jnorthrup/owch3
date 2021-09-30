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

for i in *.$j.conf 
do
	java -cp ../../dist/lib/owch2.jar net.sourceforge.owch2.agent.$j -config $i&
done
done
