build:
	$(MAKE) -C src/

run: build
	java src/Main

clean:
	$(MAKE) -C src/ clean
