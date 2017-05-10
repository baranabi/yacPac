all: YacPac Yac Pac Cat

YacPac : YacPac.java
	javac YacPac.java
	
Yac : Yac.java
	javac Yac.java
	
Pac : Pac.java
	javac Pac.java
  
Cat : Cat.java
	javac Cat.java


clean:
	rm *.class
