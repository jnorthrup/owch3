
SUBDIR=elgato_main
JARFILE=/tmp/lib_$(SUBDIR).jar 

ALLSOURCES=$(shell find -name '*.java')
ALLCLASSES=$(patsubst %.java,%.class,$(ALLSOURCES))
INSTDIR=/tmp


all: 
	find -name '*.class' | xargs rm -f
	find -name '*.java' | xargs $(JAVAC) -g

$(JARFILE): $(ALLCLASSES)
	zip -XDqR9y $(JARFILE) '*.htm*' '*.class'  

WINJ118HOME=$(shell cygpath -u /c/jdk1.1.8)
J118HOME=$(JAVA_HOME)
#J118HOME=$(WINJ118HOME)
ROOTCLSPATH=$(J118HOME)/lib/classes.zip:$(PWD)
LIBCLSPATH=$(shell find $(PWD)/../jars/ -name *.jar|xargs echo |tr ' ' :)
WINCLASSPATH="$(shell cygpath -w -p $(ROOTCLSPATH):$(LIBCLSPATH):$(PWD) |tr '\\' '/')"

#WINBUILD=Debug
WINBUILD=Release
export CFG=\"stlgraph - Win32 $(WINBUILD)\" 

JAVA=$(J118HOME)/bin/java
JAVAC=$(J118HOME)/bin/javac
#JAVAC=jikes +E -classpath "$(LIBCLSPATH)" -bootclasspath "` cygpath -up  $(JAVA_HOME)/jre/lib/rt.jar |sed 's,c:,/cygdrive/c/,g'`"

jni_lib: stlgraph/$(WINBUILD)/stlgraph.dll
	cp stlgraph/$(WINBUILD)/stlgraph.dll stlgraph
	cp stlgraph/$(WINBUILD)/stlgraph.dll . 

winclean:
	find  -iname '*.frag' -o -iname '*.class' -o -iname '*.jar' -o -iname '*~' -o -iname '#*#' -o -iname '.??*' | xargs rm -f ../jars/$(JARFILE)

wincompile:  winclean
	$(JAVAC) -g  $(PWD)/com/agilent/elgato/startup/Main.java 2>.compile||(tr <.compile  '\\' /|sed -e 's,c:,/cygdrive/c,';rm .compile) 
	cp ../python/uc_registry.ser ../etc/ 

winjar: all   jar  

winrun: all  #winjar 
	cp ../python/uc_registry.ser ../etc/ 
	$(JAVA) -classpath "$(shell cygpath -pw $(LIBCLSPATH):$(PWD))" com.agilent.elgato.startup.Main -startup.registry "file:///$(shell cygpath -w -a ../etc/uc_registry.ser)" -Resource "/config" -Source  "file:///$(shell cygpath -w -a ../etc/default.properties)" -name proto
 
winerun: all .compile #winjar
	$(JAVA) -classpath . MainPain  2>.compile&& \
	sh ../sh/edbg.sh <.compile
 
winprof: wincompile .compile
	$(JAVA)  -prof  -classpath . MainPain 
 
jar:    all
	zip -vXDR9y $(JARFILE) '*.class' 

install:: jar
	cp ../python/uc_registry.ser ../etc/ 
	mv $(JARFILE) ../jars
	
jartest: jar 
	cd $(HOME)
	$(JAVA) -cp /tmp/$(JARFILE) MainPain

htmltest: 
	$(JAVAC) -classpath . proto/*.java
	$(JAVA) -cp . proto.HTMLReadTest 

snapshot: winclean
	cd ..;tar cvzf `date '+%s'`.tgz java;cd java
