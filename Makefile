TARGET_JARS=rnodi_owch.jar

export CLASSPATH=.:archiver.jar

#for cygwin uncomment the following line
#CLASSPATH=`cygpath -p -w $(CLASSPATH)`

ALLSOURCES=$(shell find -name '*.java')
ALLCLASSES=$(patsubst %.java,%.class,$(ALLSOURCES))

all: $(TARGET_JARS)

$(ALLCLASSES): $(ALLSOURCES)
	find -name '*.class' | xargs rm    -f
	find -name '*.java'  | xargs javac -deprecation  -g

$(TARGET_JARS): $(ALLCLASSES)
	zip -XDqR9y $(TARGET_JARS) '*.htm*' '*.class'

install::
