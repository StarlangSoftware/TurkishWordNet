Turkish WordNet KeNet
============

# WordNet

Wordnet, in its broader definition, is a highly comprehensive dictionary that is built on distinct word senses along with their definitions. Most of the words in a wordnet are open-class words such as nouns, verbs, adjectives and adverbs. Main building blocks of a wordnet are synsets, which are comprised of synonym synset members. Synsets are the distinct units in wordnets and all the mappings including intra and interlingual ones are constructed based on the synsets. In lexical semantics, it is argued that words can be defined based on the relations between them. Adopting this principle, wordnets map semantic relations such as hypernymy, meronymy or antonymy through synsets.

Constructing a wordnet, whether from scratch or by expanding a previous one, is a labor intensive process that requires several steps and extensive use of both human labor and automated systems. Since the creation of the first wordnet Princeton WordNet (PWN) in 1995 (Miller, 1995), many other wordnets have been created for several languages (e.g., Finnish WordNet FinnWordNet (Linden and Carlson, 2010), Polish WordNet (Derwojedowa et al., 2008), Norwegian WordNet (Fjeld and Nygaard, 2009), Danish WordNet (Pedersen et al., 2009), French WordNet WOLF (Sagot, 2008)). In addition, multilingual wordnets linking the wordnets of multiple languages have been created. To exemplify, EuroWordNet (EWN) is a multilingual WordNet project that consists several European languages (English, Dutch, Italian, Spanish, German, French, Czech and Estonian) (Vossen, 2007). In EWN, the wordnets were created for each language separately and then linked through an Inter-Lingual-Index based on PWN. BalkaNet, similar to EWN, is a multilingual wordnet project consisting of six Balkan languages (Bulgarian, Czech, Greek, Romanian, Serbian, and Turkish) (Tufis et al., 2004). This project was done to produce a multilingual semantic network, fully compatible with EWN and its extensions.

# Turkish WordNet

The very first step in constructing KeNet, as in every other wordnet, was to create synsets. Synset can be defined as a group of words sharing the same sense and part of speech (POS). Regarding the construction of these synsets, the first version of the database was constructed through mining of the latest Contemporary Dictionary of Turkish (CDT) (2011’s print) published by the Turkish Language Institute (TLI) (Ehsani et al., 2018). By convention, CDT marks synonyms by using commas such that synonyms of a word are given after its definition with a separation of comma. To decide on true synonyms that must occur in the same synsets, we sliced the definitions at commas and listed the comma-separated lemmas and the rest of the definitions as candidates of synonyms. Then, those lists were displayed for linguistically-informed human annotators who decided on the synonymy relation between the lemmas and the definitions. 49,774 pairs were annotated at the end of this phase. Although some of them were included as separate entries in CDT, passivized and causativized forms of verbs were deleted from KeNet as they share the same root with their active forms.

Although the vast majority of the synsets were constructed during this process, there was a need for follow-up procedures to improve the organization of the current synsets. Since the main problem encountered in synset construction was the semantic relatedness of the synset members, two other procedures were followed in order to control the synonymy relations within the synsets: the merge process and the split process.

## Merge Process

In the merge process, different synsets that should be grouped together were identified and grouped as a single synset. Three things were crucial while merging the synsets: (i) having a single and unique definition for each synset, (ii) having true synonyms as synset members in each synset and (iii) having a representative first synset member in each synset. Firstly, the synsets that were created by combining the synset members with identical senses had as many definitions as the number of synset members in them since the definitions were also merged while merging the synset members. The definitions of the merged synsets were initially combined with a pipe symbol in between them. A new definition for each merged synset was written so that each synset had a single and unique definition that covers the meaning of all its synset members. None of the synset members of a synset appeared in its definition. In this process, new definitions for 10,612 number of synsets were written by the human annotators. Secondly, some synsets were found to include unrelated synset members. Therefore, another goal of the merge process was to include only the synset members that were synonyms. 1,144 number of synsets with unrelated synset members that had been identified in other parts of the work were transferred to the split process.

## Split Process

In the split process, the synsets that included synset members with different senses were split and separate synsets were created for each group of related synset members. In order to fix this problem, we created a pool where we collected all the synsets that had unrelated synset members. We displayed these synsets on Google Sheets. Linguistically-informed human annotators then split these wrongly-merged synsets and wrote new definitions for the newly-created ones.

Currently, there are 77,330 synsets, 109,049 synset members and 80,956 distinct synset members in KeNet. The POS categories that are included are nouns, adverbs, adjectives, adverbs, interjections, pronouns, postpositions and conjunctions.

|Part of Speech|# of Synsets|
|---|---|
|Nouns|44,074|
|Verbs|17,791|
|Adjectives|12,416|
|Adverbs|2,550|
|Interjections|342|
|Pronouns|68|
|Conjunctions|60|
|Postpositions|29|
|Total|77,330|

## Data Format

The structure of a sample synset is as follows:

	<SYNSET>
		<ID>TUR10-0038510</ID>
		<LITERAL>anne<SENSE>2</SENSE>
		</LITERAL>
		<POS>n</POS>
		<DEF>...</DEF>
		<EXAMPLE>...</EXAMPLE>
	</SYNSET>

Each entry in the dictionary is enclosed by \<SYNSET> and \</SYNSET> tags. Synset members are represented as literals and their sense numbers. \<ID> shows the unique identifier given to the synset. \<POS> and \<DEF> tags denote part of speech and definition, respectively. As for the \<EXAMPLE> tag, it gives a sample sentence for the synset.

Simple Web Interface
============
[Turkish WordNet Link 1](http://104.247.163.162/nlptoolkit/turkish-wordnet.html) [Turkish WordNet Link 2](https://starlangsoftware.github.io/nlptoolkit-web-simple/turkish-wordnet.html)

[Turkish WordNet Tree Link 1](http://104.247.163.162/nlptoolkit/turkish-wordnet-tree.html) [Turkish WordNet Tree Link 2](https://starlangsoftware.github.io/nlptoolkit-web-simple/turkish-wordnet-tree.html)

[English WordNet Link 1](http://104.247.163.162/nlptoolkit/english-wordnet.html) [English WordNet Link 2](https://starlangsoftware.github.io/nlptoolkit-web-simple/english-wordnet.html)

[English WordNet Tree Link 1](http://104.247.163.162/nlptoolkit/english-wordnet-tree.html) [English WordNet Tree Link 2](https://starlangsoftware.github.io/nlptoolkit-web-simple/english-wordnet-tree.html)

Video Lectures
============

[<img src=video1.jpg width="50%">](https://youtu.be/RLVTegHva_k)[<img src=video2.jpg width="50%">](https://youtu.be/DFc_XEqJshU)[<img src=video3.jpg width="50%">](https://youtu.be/KyA32rOv308)
	
For Developers
============

You can also see [Python](https://github.com/starlangsoftware/TurkishWordNet-Py), [Cython](https://github.com/starlangsoftware/TurkishWordNet-Cy), [C++](https://github.com/starlangsoftware/TurkishWordNet-CPP), [C](https://github.com/starlangsoftware/TurkishWordNet-C), [Swift](https://github.com/starlangsoftware/TurkishWordNet-Swift), [Js](https://github.com/starlangsoftware/TurkishWordNet-Js), [Php](https://github.com/starlangsoftware/TurkishWordNet-Php), or [C#](https://github.com/starlangsoftware/TurkishWordNet-CS) repository.

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
            <version>1.0.56</version>
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

# Cite

	@inproceedings{bakay21,
 	title={{T}urkish {W}ord{N}et {K}e{N}et},
 	year={2021},
 	author={O. Bakay and O. Ergelen and E. Sarmis and S. Yildirim and A. Kocabalcioglu and B. N. Arican and M. Ozcelik and E. Saniyar and O. Kuyrukcu and B. 	Avar and O. T. Y{\i}ld{\i}z},
 	booktitle={Proceedings of GWC 2021}
 	}

For Contibutors
============

### pom.xml file
1. Standard setup for packaging is similar to:
```
    <groupId>io.github.starlangsoftware</groupId>
    <artifactId>Amr</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    <name>NlpToolkit.Amr</name>
    <description>Abstract Meaning Representation Library</description>
    <url>https://github.com/StarlangSoftware/Amr</url>

    <organization>
        <name>io.github.starlangsoftware</name>
        <url>https://github.com/starlangsoftware</url>
    </organization>

    <licenses>
        <license>
            <name>The Apache Software License, Version 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
        </license>
    </licenses>

    <developers>
        <developer>
            <name>Olcay Taner Yildiz</name>
            <email>olcay.yildiz@ozyegin.edu.tr</email>
            <organization>Starlang Software</organization>
            <organizationUrl>http://www.starlangyazilim.com</organizationUrl>
        </developer>
    </developers>

    <scm>
        <connection>scm:git:git://github.com/starlangsoftware/amr.git</connection>
        <developerConnection>scm:git:ssh://github.com:starlangsoftware/amr.git</developerConnection>
        <url>http://github.com/starlangsoftware/amr/tree/master</url>
    </scm>

    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
```
2. Only top level dependencies should be added. Do not forget junit dependency.
```
    <dependencies>
        <dependency>
            <groupId>io.github.starlangsoftware</groupId>
            <artifactId>AnnotatedSentence</artifactId>
            <version>1.0.78</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.1</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```
3. Maven compiler, gpg, source, javadoc plugings should be added.
```
	<plugin>
		<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-compiler-plugin</artifactId>
		<version>3.6.1</version>
		<configuration>
			<source>1.8</source>
			<target>1.8</target>
		</configuration>
	</plugin>
	<plugin>
		<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-gpg-plugin</artifactId>
		<version>1.6</version>
		<executions>
			<execution>
				<id>sign-artifacts</id>
				<phase>verify</phase>
				<goals>
					<goal>sign</goal>
				</goals>
			</execution>
		</executions>
	</plugin>
	<plugin>
		<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-source-plugin</artifactId>
		<version>2.2.1</version>
		<executions>
			<execution>
				<id>attach-sources</id>
				<goals>
					<goal>jar-no-fork</goal>
				</goals>
			</execution>
		</executions>
	</plugin>
	<plugin>
		<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-javadoc-plugin</artifactId>
		<configuration>
			<source>8</source>
		</configuration>
		<version>3.10.0</version>
		<executions>
			<execution>
				<id>attach-javadocs</id>
				<goals>
					<goal>jar</goal>
				</goals>
			</execution>
		</executions>
	</plugin>
```
4. Currently publishing plugin is Sonatype.
```
	<plugin>
		<groupId>org.sonatype.central</groupId>
		<artifactId>central-publishing-maven-plugin</artifactId>
		<version>0.8.0</version>
		<extensions>true</extensions>
		<configuration>
			<publishingServerId>central</publishingServerId>
			<autoPublish>true</autoPublish>
		</configuration>
	</plugin>
```
5. For UI jar files use assembly plugins.
```
	<plugin>
		<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-assembly-plugin</artifactId>
		<version>2.2-beta-5</version>
		<executions>
			<execution>
				<id>sentence-dependency</id>
				<phase>package</phase>
				<goals>
					<goal>single</goal>
				</goals>
				<configuration>
					<archive>
						<manifest>
							<mainClass>Amr.Annotation.TestAmrFrame</mainClass>
						</manifest>
					</archive>
					<finalName>amr</finalName>
				</configuration>
			</execution>
		</executions>
		<configuration>
			<descriptorRefs>
				<descriptorRef>jar-with-dependencies</descriptorRef>
			</descriptorRefs>
			<appendAssemblyId>false</appendAssemblyId>
		</configuration>
	</plugin>
```
### Resources
1. Add resources to the resources subdirectory. These will include image files (necessary for UI), data files, etc.
   
### Java files
1. Do not forget to comment each function.
```
    /**
     * Returns the value of a given layer.
     * @param viewLayerType Layer for which the value questioned.
     * @return The value of the given layer.
     */
    public String getLayerInfo(ViewLayerType viewLayerType){
```
2. Function names should follow caml case.
```
    public MorphologicalParse getParse()
```
3. Write toString methods, if necessary.
4. Use Junit for writing test classes. Use test setup if necessary.
```
public class AnnotatedSentenceTest {
    AnnotatedSentence sentence0, sentence1, sentence2, sentence3, sentence4;
    AnnotatedSentence sentence5, sentence6, sentence7, sentence8, sentence9;

    @Before
    public void setUp() throws Exception {
        sentence0 = new AnnotatedSentence(new File("sentences/0000.dev"));
```
