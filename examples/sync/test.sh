for j in Domain MobilePayload GateKeeper  
do 

for i in *.$j.conf 
do
	java -cp ../../dist/lib/agent-1.0-SNAPSHOT.jar net.sourceforge.owch2.agent.$j -config $i&
done
done
