JAVAC = javac

.PHONY: all
all:
	@mkdir -p bin
	$(JAVAC) -cp src -s src -d bin src/v/D3.java


.PHONY: clean
clean:
	-rm -r bin

