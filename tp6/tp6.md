# TP4-prog-rep


## Partie 3 , Exercice 1 : 
-------------------------

Not yet





## Partie 3 , Exercice 2 : 
-------------------------

1. Propriétés du snapshot
------------------------- 

Parmi les propriétés suivantes, les quelles sont vraies ? (démonstration ou contre-exemple)  

**(a) Pour tout i: scan i [i] = i**  

**Réponse :** Propriété vrai. Vu que les opérations sont atomiques et que la "thread i" écrit la valeur au niveau de l'indice "i" il y'aura pas de conflit,  et aussi on a l'écriture puis la lecture sur le même indice.   

Donc si on a un scan_i[5] on aura le résultat retourné 5   

**Exemple :**  

Le tableau de mémoire partagée initial:  

|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|
|-1 |-1 |-1 |-1 |-1 |... |... |-1 |-1  

La tableau après scan et update de tt les threads :

|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|
|0 |1 |2 |3 |4 |5 |... |... |n-1  

**(b) Pour j ≠ i, scan j [i] = i ou scan j [i] = −1**

**Réponse :** Propriété vrai. Vu qu'ont regarde le scan et l'update de i via une thread j, avant il se peut que la thread i n'a rien écrit et donc on aura comme retour de scan_j[i] = -1 et si la thread i avait écrit un valeur avant et bien on lira cette valeur déja écrite et scan_j[i]=i  

**Exemple cas 1:**

Dans le cas ou la thread 2 n'a pas fait un update(x) et que la thread 4 a lit la valeur a la position 2, cette dernière lira -1 car la thread 2 n'a pas fait d'update    

|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|
|0 |1 |-1 |3 |4 |... |... |n-2 |-1  

La valeur de la thread 2 est bien restée a -1   

**Exemple cas 2:**

Dans le cas ou la thread 2 a fait une update(x) et que la thread 4 a lit la valeur a la position 2, cette dernière lira x, car la thread 2 a fait l'update(x) à l'indice 2  

Tableau arpés update(x) par la thread 2  
|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|
|0 |1 |x |3 |4 |... |... |n-2 |-1

Quand la thread 4 lira, elle lira x (scan_j[i]=x)    

**(c) Pour j ≠ i, si scan j[i] = i alors scan i[j] = j**  

**Réponse :** Propriété fausse. Car on peut tomber dans un cas ou j < i et qu'ont a pas encore fait la mise à jour de la case j du tableau  

**Exemple :**  

Si on prend i = 2 et j = 4, et que on fait un scan_j[2] on tombe sur la valeur 2 mais quand on fera un scan_i[4] pourrai tomber sur -1 si la thread j n'a pas encore fait d'update

|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|
|0 |1 |2 |3 |-1 |... |... |n-2 |n-1  

i pourra donc lire une autre valeur (-1) car j n'a pas encore fait d'update pour ça valeur


**(d) Pour j ≠ i, si scan j [i] = i alors scan i [j] = −1**

**Réponse :** Propriété fausse. Car on peut lire la valeur de j après son update

**Exemple :** 
Si on prend i = 2 et j = 4, et que on fait un scan_j[2] on tombe sur la valeur 2 et quand on fera un scan_i[4] pourrai tomber sur 4 car la thread j a fait une update(4)  

|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|  
|0     |1     |2     |3     |4     |...   |...   |n-2   |n-1  


**(e) Pour j ≠ i, scan j [i] = i ou scan i [j] = j**

**Réponse :** Propriété vrai. Car i fait un update dans la case i et scan la case j et j fait un update de la case j et scan case i  

**Pour j ≠ i, scan j ⊆ scan i ou scan i ⊆ scan j ( la relation ⊆ est A ⊆ B si et seulement si " si A[i] ≠ −1 alors A[i] = B[i] ")**

**Réponse :** Propriété vrai. Si on a un scan j est inclu dans un scan i cela veux dire que scan i contient toutes les valeurs qui étaient dans scan j  

2. Implémentation non-blocking
------------------------------


