ifeq (run,$(firstword $(MAKECMDGOALS)))
  RUN_ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
  $(eval $(RUN_ARGS):;@:)
endif

SPACE =
SPACE +=
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

.PHONY: jars
jars:
	@echo $(JARS)
	@echo $(JARSOURCES)
	@echo $(JARURLS)

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
	rm -rf .jclass main.jar

.PHONY: rebuild
rebuild: clean build

#sources: $(JARURLS)

.PHONY: runjar
runjar: chronos.jar
	java -jar chronos.jar $(RUN_ARGS)

.PHONY: jar
jar: chronos.jar

chronos.jar: rebuild
	mkdir jarbuild
	cp -r .jar/* jarbuild
	#for J in $(JARS); do (cd jarbuild && jar xf $$J net)
	(cd jarbuild && jar xf sqlite-jdbc-3.42.0.0.jar org)
	(cd jarbuild && jar xf jreadline-0.20.jar org)
	(cd jarbuild && rm *.jar)
	cp -r .jclass/* jarbuild
	cp MANIFEST.MF jarbuild
	jar cfm chronos.jar MANIFEST.MF -C jarbuild .
	rm -rf jarbuild

.jar:
	@mkdir .jar

.jar/%.jar: .jar
	echo $< $@ $^
