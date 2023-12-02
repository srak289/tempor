run:
	$(MAKE) -C src

runjar: chronos.jar
	java -jar chronos.jar

jar: chronos.jar

chronos.jar:
	$(MAKE) -C src chronos.jar
	mv src/chronos.jar .

build:
	$(MAKE) -C src build

clean:
	$(MAKE) -C src clean
	rm chronos.jar
