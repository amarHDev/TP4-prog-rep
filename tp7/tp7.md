# TP7-prog-rep

## Partie 3 : Implémentation wait-free

1. Est-ce que le scan et le update terminent toujours?   

**Réponse :** Oui. Le scan et le update terminent toujours (on fait 2 * n lectures)  

    Cette implémentation n’est pas atomique, donnez un contre exemple !  

**Contre exemple :**  Les Threads vont faire chaqu'une un update, elle vont commencer par faire un scan qui n'aura que les valeurs initiales et l'écrivent dans le tableau.  
Par exemple la thread 3 commence est fait un scan et elle fait un premier collect, la thread 1 compte à elle fait un update puis la thread 3 fait son second collect et retourne la valeur du scan de la thread 1, cette dernière ne sera pas correct car de nombreux updates on était fait et il faut qu'il soit là au niveau du scan.  

2. Même question si on prend celle dans le deuxième collect ! 

**Réponse :** Même idée sauf que l'éciture de l'update 2 qui correspond au collect 2, va se trouvé au milieu du collect 1 et collect 2, mais le scan correspondant à cet update peut se trouvé très loin en arrière et donc si un scan se trouve avant l'update d'une autre thread alors on écrira d'ancienne valeurs sans prendre en compte les autres updates.  

3. Combien fait-on au plus de collect pour réaliser un scan ?  

**Réponse :** Pour réaliser un scan il faut 3 collect !  

Est-ce que le scan et le update terminent toujours ?  

**Réponse :** Oui le scan et l’update terminent toujours !  

Est-ce que l’implémentation obtenue est atomique (faites une preuve ou donnez un contre-exemple)?  

**Réponse :** Oui. L'implémentation est atomique  

**Preuve :** L'écriture de l’update 3 correspondant au collect 3 se trouve entre le collect2 et collect3, par contre son scan se trouve entre l'écriture de l’update 2 et l'écriture de l'autre update et donc on peut adopter cette valeur de scan.    

4. Réalisez cette implémentation atomique wait-free.  
    Code à trouvé au niveau du dossier tp7 dans le fichier "Prog-rep-TP7.zip"