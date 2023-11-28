ifeq (run,$(firstword $(MAKECMDGOALS)))
  RUN_ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
  $(eval $(RUN_ARGS):;@:)
endif

E =
SPACE = $E $E

JARURLS != cat JARURL.txt
JARS := $(notdir $(JARURLS))
JARSOURCES := $(patsubst %,./.jar/%,$(JARS))
JARCLASSPATH := $(subst $(SPACE),:,$(JARSOURCES))

SRCS != find . -name \*.java
OBJS := $(patsubst %.java,.jclass/%.class,$(SRCS))

.PHONY: build
build: .jclass $(JARSOURCES) $(OBJS)
	@cp schema.sql .jclass/chronos/db/

.jclass:
	@mkdir .jclass

.jclass/%.class: %.java
	javac -cp ".:$(JARCLASSPATH)" -g -d .jclass $<

.PHONY: run
run: build
	java -cp ".jclass:$(JARCLASSPATH)" chronos.Main $(RUN_ARGS)

.PHONY: debug
debug: build
	jdb -sourcepath "." -classpath ".jclass:$(JARCLASSPATH)" chronos.Main $(RUN_ARGS)

.PHONY: clean
clean:
	rm -rf .jclass chronos.jar

.PHONY: rebuild
rebuild: clean build

.PHONY: runjar
runjar: chronos.jar
	java -jar chronos.jar $(RUN_ARGS)

.PHONY: jar
jar: chronos.jar

chronos.jar: build
	mkdir jarbuild
	cp -r .jar/* jarbuild
	for J in $(JARS); do (cd jarbuild && jar xf $$J); done
	(cd jarbuild && rm *.jar)
	cp -r .jclass/* jarbuild
	cp MANIFEST.MF jarbuild
	jar cfm chronos.jar MANIFEST.MF -C jarbuild chronos -C jarbuild org -C jarbuild net
	rm -rf jarbuild

.PHONY: sources
sources: $(JARSOURCES)

.jar:
	@mkdir .jar

.jar/%.jar: .jar
	$(eval JARURL=$(shell grep $(patsubst .jar/%,%,$@) JARURL.txt))
	wget $(JARURL) -O $@
	touch $@
