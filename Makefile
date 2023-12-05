run:
	$(MAKE) -C src

runjar: tempor.jar
	java -jar tempor.jar

jar: tempor.jar

tempor.jar:
	$(MAKE) -C src tempor.jar
	mv src/tempor.jar .

build:
	$(MAKE) -C src build

clean:
	$(MAKE) -C src clean
	rm tempor.jar
