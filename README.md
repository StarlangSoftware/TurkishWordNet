For Developers
============

You can also see [Python](https://github.com/starlangsoftware/TurkishWordNet-Py), [C++](https://github.com/starlangsoftware/TurkishWordNet-CPP), or [C#](https://github.com/starlangsoftware/TurkishWordNet-CS) repository.

## Requirements

* [Java Development Kit 8 or higher](#java), Open JDK or Oracle JDK
* [Maven](#maven)
* [Git](#git)

### Java 

To check if you have a compatible version of Java installed, use the following command:

    java -version
    
If you don't have a compatible version, you can download either [Oracle JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or [OpenJDK](https://openjdk.java.net/install/)    

### Maven
To check if you have Maven installed, use the following command:

    mvn --version
    
To install Maven, you can follow the instructions [here](https://maven.apache.org/install.html).      

### Git

Install the [latest version of Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).

## Download Code

In order to work on code, create a fork from GitHub page. 
Use Git for cloning the code to your local or below line for Ubuntu:

	git clone <your-fork-git-link>

A directory called WordNet will be created. Or you can use below link for exploring the code:

	git clone https://github.com/starlangsoftware/TurkishWordNet.git

## Open project with IntelliJ IDEA

Steps for opening the cloned project:

* Start IDE
* Select **File | Open** from main menu
* Choose `WordNet/pom.xml` file
* Select open as project option
* Couple of seconds, dependencies with Maven will be downloaded. 


## Compile

**From IDE**

After being done with the downloading and Maven indexing, select **Build Project** option from **Build** menu. After compilation process, user can run WordNet.

**From Console**

Go to `WordNet` directory and compile with 

     mvn compile 

## Generating jar files

**From IDE**

Use `package` of 'Lifecycle' from maven window on the right and from `WordNet` root module.

**From Console**

Use below line to generate jar file:

     mvn install

## Maven Usage

        <dependency>
            <groupId>io.github.starlangsoftware</groupId>
            <artifactId>WordNet</artifactId>
            <version>1.0.27</version>
        </dependency>

Detailed Description
============

+ [WordNet](#wordnet)
+ [SynSet](#synset)
+ [Synonym](#synonym)

## WordNet

To load the WordNet KeNet,

	WordNet a = new WordNet();

To load a particular WordNet,

	WordNet domain = new WordNet("domain_wordnet.xml", new Locale("tr"));

To bring all the synsets,

	Collection<SynSet> synSetList()

To bring a particular synset,

	SynSet getSynSetWithId(String synSetId)

And, to bring all the meanings (Synsets) of a particular word, the following is used.

	ArrayList<SynSet> getSynSetsWithLiteral(String literal)

## SynSet

Synonym is procured in order to find the synonymous literals of a synset.

	Synonym getSynonym()
	
In order to obtain the Relations inside a synset as index based, the following method is used.

	Relation getRelation(int index)

For instance, all the relations in a synset,

	for (int i = 0; i < synset.relationSize(); i++){
		relation = synset.getRelation(i);
		...
	}

## Synonym

The literals inside the Synonym are found as index based with the following method.


	Literal getLiteral(int index)

For example, all the literals inside a synonym can be found with the following:

	for (int i = 0; i < synonym.literalSize(); i++){
		literal = synonym.getLiteral(i);
		...
	}
