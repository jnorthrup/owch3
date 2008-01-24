for j in Domain IRC IRCBridge 
do 
for i in *.$j.conf 
do
	java -cp ../../dist/lib/owch2.jar net.sourceforge.owch2.agent.$j -config $i&
done
done
