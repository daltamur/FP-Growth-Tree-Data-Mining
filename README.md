# FP-Growth-Tree-Data-Mining
An implementation of data mining to find association rules from two years of grocery store receipts
My first use of a machine-learning based algorithm. Certainly not perfect, but this was my first project where I built everything on my own with little pseudocode referenced, just
the basic structure of the FP trie and how to gather association rules from it. Gathering useful associations where the antecedent or precedent has more than 2 products is impossible, so I just set it up to find rules leading up to that, though
developing it so that it can get rules with antecedents and precedents of any size is a simple subset creator, so I might come back and implement that, though it will sparsely be used.
