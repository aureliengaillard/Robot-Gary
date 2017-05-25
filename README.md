# Projet Robot-Gary
> *Code du Bureau d'Etude IA sur Robot Lego - A.Gaillard &amp; N.Pélissier - Avril / Mai 2017*  
> *Université Paul Sabatier - Toulouse*  

Ce projet a pour but de développer une Intelligence Artificielle qui permet : 

* A un robot d'aller d'une case à une autre dans un labyrinthe qui lui est inconnu
* A une flotte de robots d'aller chercher des personnes, en coopération, en communiquant via un bloc central (une brique NXT)

# Utilisation du Code

*Ce README décrit l'utilisation sous Windows, avec Ecilpse et l'extension Lejos.*  
*Il est également possible de suivre les tutoriels sur [le site de Lejos](http://www.lejos.org/)* pour les autres configurations  

## Installation de Java

**ATTENTION**
Les bibliothèques Lejos ne sont compatibles qu'avec Java 32-bits, il vous faudra donc téleécharger la bonne version du JDK !
Les téléchargements et instructions d'installations se trouvent sur [le site d'Oracle consacré à Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
Sélectionner "Java SE Development Kit" pour "Windows x86"  

## Installation de Lejos

Pour installer Lejos sur votre ordinateur et sur votre brique NXT, ainsi que les drivers USB, vous pouvez suivre
[le tutoriel du site Lejos](http://www.lejos.org/nxt/nxj/tutorial/Preliminaries/GettingStarted.htm)  

## Installation d'Eclipse

Vous pouvez utiliser n'importe quel IDE Java pour modifier ce projet, mais Eclipse est le seul à avoir une extension Lejos,
qui facilite la compilation et l'upload du code dans la brique NXT, c'est pourquoi nous vous conseillons de l'utiliser  
Nous avons rencontré des problèmes avec Eclipse 64-bits, c'est pourquoi nous vous conseillons la versions 32-bits du programme d'installation  
[Lien de telechargement direct d'Eclipse Windows 32-bits](https://www.eclipse.org/downloads/download.php?file=/oomph/epp/neon/R2a/eclipse-inst-win32.exe)  
[Lien vers le site d'Eclipse](https://www.eclipse.org/downloads/?)  

## Installation de l'extension Lejos

L'extension Lejos, qui permet d'uploader facilement les programmessur les briques NXT, est installable en suivant [ce tutoriel](http://www.lejos.org/nxt/nxj/tutorial/Preliminaries/UsingEclipse.htm) (Categorie Installing the Eclipse plugin)  
Il vous faudra ensuite importer le projet dans Eclipse, faire un clic droit dessus, et cliquer sur Lejos NXJ > Convert to Lejos NXJ project

## Etapes pour adapter le code à votre robot

### Construction et fonctions de base

* La première étape est de construire un robot. Une fois cela fait, il faut implémenter ses fonctions de base.
Pour cela une interface Java est disponible dans le package interfaces du projet. Il faut créer une classe qui implémente cette interface.
Plusieurs exemples d'implémentations sont disponibles dans les package "robotGary", "robot2Tetes" et "gurrenLagann".  

### Création d'un "Main" pour votre robot

Il faut ensuite créer un Main pour le robot (dans le package mains). Ce main peut être l'un des suivants

* Cas d'un robot devant sortir du labyrinthe :
    
```java
package mains;

import interfaces.RobotInterface;
import programmes.Parcourer;
import robotGary.RobotGary;

public class MainGary {
  public static void main(String[] args) {
		RobotInterface gary = new RobotGary();
		Parcourer garyParcourer = new Parcourer(gary);
		garyParcourer.parcoursDEtoileLite();
	}
}
```  
    
* Cas de plusieurs robots devant sauver une liste de personnes :
    
```java
package mains;

import interfaces.RobotInterface;
import programmes.Parcourer;
import robotGary.RobotGary;

public class MainGary {
  public static void main(String[] args) {
		RobotInterface gary = new RobotGary();
		Parcourer garyParcourer = new Parcourer(gary);
		garyParcourer.sauverLesGens();
	}
}
```  

### Pairer et uploader les programmes

Il faut ensuite, pour chaque robot :

* Brancher le robot
* Clic droit sur le main du robot > Run as > Lejos NXT Program

Il faut également uploader de la même façon le main du bloc central (dans le cas du sauvetage), disponible dans le package "mains"  
Les robots doient également tous être pairés avec le bloc central, en Bluetooth.
Il suffit ensuite de lancer les programmes uploadés sur les robots et le bloc central, et suivre les indications sur celui ci.
