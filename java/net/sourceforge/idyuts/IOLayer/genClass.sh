export XCLASS=$1
export XFILTERTYPE=$2
sh ./gen.sh <Source.pre   >${XCLASS}Source.java
sh ./gen.sh <SourceNode.pre   >${XCLASS}SourceNode.frag
sh ./gen.sh <Filter.pre   >${XCLASS}Filter.java
sh ./gen.sh <FilterNode.pre   >${XCLASS}FilterNode.frag
 
##########################
#for i in XFILTERTYPE XCLASS XPARENT XFILTER;
#do echo "	-e 's#@${i}@#\${${i}}#g;'";done  
#################
