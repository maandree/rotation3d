.POSIX:

JAVAC = javac

SRC =\
	srv/v/D3.java\
	srv/v/VMaths.java


all: $(SRC)
	@mkdir -p bin
	$(JAVAC) -cp src -s src -d bin src/v/D3.java

clean:
	-rm -r -- bin

.PHONY: all clean
