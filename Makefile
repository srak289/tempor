.PHONY: run
run:
	$(MAKE) -C src

.PHONY: runjar
runjar: tempor.jar
	java -jar tempor.jar

.PHONY: rundemo
rundemo: tempor.jar
	java -jar tempor.jar --demo

.PHONY: jar
jar: tempor.jar

tempor.jar:
	$(MAKE) -C src tempor.jar
	cp src/tempor.jar .

.PHONY: build
build:
	$(MAKE) -C src build

.PHONY: clean
clean:
	$(MAKE) -C src clean
	rm tempor.jar
	rm time.db
