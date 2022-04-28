Turkish WordNet KeNet
============

# WordNet

Wordnet, in its broader definition, is a highly comprehensive dictionary that is built on distinct word senses along with their definitions. Most of the words in a wordnet are open-class words such as nouns, verbs, adjectives and adverbs. Main building blocks of a wordnet are synsets, which are comprised of synonym synset members. Synsets are the distinct units in wordnets and all the mappings including intra and interlingual ones are constructed based on the synsets. In lexical semantics, it is argued that words can be defined based on the relations between them. Adopting this principle, wordnets map semantic relations such as hypernymy, meronymy or antonymy through synsets.

Constructing a wordnet, whether from scratch or by expanding a previous one, is a labor intensive process that requires several steps and extensive use of both human labor and automated systems. Since the creation of the first wordnet Princeton WordNet (PWN) in 1995 (Miller, 1995), many other wordnets have been created for several languages (e.g., Finnish WordNet FinnWordNet (Linden and Carlson, 2010), Polish WordNet (Derwojedowa et al., 2008), Norwegian WordNet (Fjeld and Nygaard, 2009), Danish WordNet (Pedersen et al., 2009), French WordNet WOLF (Sagot, 2008)). In addition, multilingual wordnets linking the wordnets of multiple languages have been created. To exemplify, EuroWordNet (EWN) is a multilingual WordNet project that consists several European languages (English, Dutch, Italian, Spanish, German, French, Czech and Estonian) (Vossen, 2007). In EWN, the wordnets were created for each language separately and then linked through an Inter-Lingual-Index based on PWN. BalkaNet, similar to EWN, is a multilingual wordnet project consisting of six Balkan languages (Bulgarian, Czech, Greek, Romanian, Serbian, and Turkish) (Tufis et al., 2004). This project was done to produce a multilingual semantic network, fully compatible with EWN and its extensions.

# Turkish WordNet

The very first step in constructing KeNet, as in every other wordnet, was to create synsets. Synset can be defined as a group of words sharing the same sense and part of speech (POS). Regarding the construction of these synsets, the first version of the database was constructed through mining of the latest Contemporary Dic- tionary of Turkish (CDT) (2011’s print) published by the Turkish Language Institute (TLI) (Ehsani et al., 2018). By convention, CDT marks synonyms by using commas such that synonyms of a word are given after its definition with a separation of comma. To decide on true synonyms that must occur in the same synsets, we sliced the definitions at commas and listed the comma-separated lemmas and the rest of the definitions as candidates of synonyms. Then, those lists were displayed for linguistically-informed human annotators who decided on the synonymy relation between the lem- mas and the definitions. 49,774 pairs were annotated at the end of this phase. Although some of them were included as separate entries in CDT, passivized and causativized forms of verbs were deleted from KeNet as they share the same root with their active forms.

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

Each entry in the dictionary is enclosed by <SYNSET> and </SYNSET> tags. Synset members are represented as literals and their sense numbers. <ID> shows the unique identifier given to the synset. <POS> and <DEF> tags denote part of speech and definition, respectively. As for the <EXAMPLE> tag, it gives a sample sentence for the synset.
	
Video Lectures
============

[<img src=video1.jpg width="50%">](https://youtu.be/RLVTegHva_k)
	
For Developers
============

You can also see [Python](https://github.com/starlangsoftware/TurkishWordNet-Py), [Cython](https://github.com/starlangsoftware/TurkishWordNet-Cy), [C++](https://github.com/starlangsoftware/TurkishWordNet-CPP), [Swift](https://github.com/starlangsoftware/TurkishWordNet-Swift), [Js](https://github.com/starlangsoftware/TurkishWordNet-Js), or [C#](https://github.com/starlangsoftware/TurkishWordNet-CS) repository.

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

# Cite

	@inproceedings{bakay21,
 	title={{T}urkish {W}ord{N}et {K}e{N}et},
 	year={2021},
 	author={O. Bakay and O. Ergelen and E. Sarmis and S. Yildirim and A. Kocabalcioglu and B. N. Arican and M. Ozcelik and E. Saniyar and O. Kuyrukcu and B. 	Avar and O. T. Y{\i}ld{\i}z},
 	booktitle={Proceedings of GWC 2021}
 	}
