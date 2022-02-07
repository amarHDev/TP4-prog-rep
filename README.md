# TP4-prog-rep


## Partie 1 , Exercice 1 : 
-----------------------

###  Question 1 : L’histoire fournit est-elle linéarisable par rapport à la specification séquentielle d’une file ?
------------

**Réponse :** Oui, cette histoire peut être linéarisable 

si le processus 1 écrit x puis y:   

    p1 p.enq(x)
    p1 p.enq(y)
    

puis le processus 3 lit à partir de la file, il lira x

    p3 p.deq()
    p3 p: x
    

puis le processus 2 lit à partir de la file, il  lira y

    p2 p.deq()
    p2 p: y
    

Cela respect bien la specification séquentielle, on a mit x le premier puis y sur la file, et on dépiler x puis y, , La relation d'arrive avant est également préservée :

p.enq(x) est bien p.deq(x)
p.enq(y) est bien p.deq(y) 

###  Question 2 : p2 et p3 aurait-il pu obtenir une autre réponse à leur demande de deq ?
------------

**Réponse :** Oui, p2 et p3 peuvent obtenir une autre réponse car comme les processus sont concurents, on peut les mettres dans n'importe quel orde, on peut donc exécuter :

    p1 p.enq(x)
    p1 p.enq(y)

et ensuite executer P2 au lieu de P3, ce qui pousse P2 à dépiler avant P3, et  lorsque P2 va dépiler, il enlèvera x et lorsque P3 va dépiler il enlèvera y

Avec cette execution P3 et P3 obtiennent d'autres valeurs possible


 ## Partie 1 , Exercice 2 : 
-----------------------

###  Question 1 :
------------


###  Question 2 : Cette implémentation réalise-t-elle une implémentation linéarisable d’une pile ? Si oui indiquer les points de linéarisation.
------------
**Réponse :** Oui, cette implèmentation de pile est linèarisable. Les point de linéarisations font référence au moment ou les opérations font réellement effets, donc notre cas il y'a trois points de linéarisations, à savoir :
    - Lors de l'ajout d'elements à la pile : cela prend effet avec **t[sommet]=j**
    - Lorsqu'ont enlève un element à la pile : cela prend effet : 
        - soit lorsqu'ont fait un **return -1** si la pile est vide
        - soit lorsqu'on décrémente le sommet **sommet=sommet-1**