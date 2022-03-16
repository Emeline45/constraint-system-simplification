#Description
Ce projet est réalisé dans le cadre de l'UE Initiation à la Recherche à l'Université de Lorraine.
Il consiste à simplifier un système de contraintes linéaires.


#Installation

Il faut installer lp_solve selon la procédure décrite dans le document : http://lpsolve.sourceforge.net/5.5/Java/README.html.  

Attention cependant à mettre `lpsolve55j.jar` dans le dossier `lib` et à l'ajouter au `CLASSPATH` Java.  

#FAQ

##J'obtiens l'erreur `java.lang.UnsatisfiedLinkError: no lpsolve55j.dll in java.library.path`, que faire ?
Pour cela, il faut le chemin vers le `.dll` dans la variable d'environnement PATH ou passer le flag `-Djava.library.path="DLL_DIR"` à Java directement (la JVM) où `DLL_DIR` est le chemin vers le dossier contenant le `.dll`.


##J'obtiens l'erreur `java.lang.UnsatisfiedLinkError: lpsolve55j.dll: Can't find dependent libraries.`, que faire ?
Il faut le chemin vers le dossier contenant le fichier `lpsolve55.dll` dans la variable d'environnement PATH.

#Licences et auteurs
Liste des auteurs :
* Emeline BONTE -- Emeline45
* Ghilain BERGERON -- Mesabloo
* Khaled SADEGH -- khaled-sadegh

Copyright © 2022- Émeline Bonte, Ghilain Bergeron, Khaled Sadegh.
Tous droits réservés.