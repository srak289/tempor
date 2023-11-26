build:
	-mkdir .jclass
	find . -name \*.java | xargs -I '{}' javac -cp ".:./argparse4j-0.9.0.jar" -d .jclass '{}'

clean:
	rm -rf .jclass

jar:
	wget https://github.com/argparse4j/argparse4j/releases/download/argparse4j-0.9.0/argparse4j-0.9.0.jar
