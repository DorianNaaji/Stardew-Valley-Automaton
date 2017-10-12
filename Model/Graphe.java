/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stardewvalleyautomaton.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class Graphe {
    
    //Attributs
    private HashMap<Couple,Integer> matriceAdjacence;                           //un graphe est dÃ©fini par sa matrice d'adjacence
    private int nombreDeSommets;                                                //nombre de sommet du graphe
    private int nbActionElem;
    
    private boolean mark[]; //tableau de bool indiquant si un sommet a deja ete considere
    private ArrayList<Integer> distance; //distance progressivement calculée
    private int[] predecesseur; //pred des sommets
    
    private int infini;
    
    public final static int ALPHA_NOTDEF = -999 ;
    
    
    //Constructeur du graphe
    public Graphe(int _nombreDeSommets) {
        this.nombreDeSommets = _nombreDeSommets;
        this.matriceAdjacence = new HashMap<>();
        this.mark = new boolean[(this.nombreDeSommets)+1];
        this.predecesseur = new int[(this.nombreDeSommets)+1];
        this.distance = new ArrayList<Integer>();
//        this.dijkstra = new ArrayList<Integer>();
        this.infini=1;
    }
    
    //place "valeur" en position (i,j) de la matrice 
    public void modifierMatrice(int i,int j,int valeur) {
        this.matriceAdjacence.put(new Couple(i,j), valeur);
    }
    
    //renvoie la valeur de la matrice en position (i,j)
    public int Matrice(int i,int j) {
        //valeur par dÃ©faut
        int res = 0;
        Couple c = new Couple(i,j);
        //si (i,j) est bien prÃ©sent dans la matrice
        if(this.matriceAdjacence.containsKey(c)) {
            res = this.matriceAdjacence.get(c);
        }
        return res;
    }
    
    //renvoie le nombre de sommet du graphe
    public int NombreSommet() {
        return this.nombreDeSommets;
    }
    
    //renvoie la matrice d'adjacence
    @Override
    public String toString() {
        String res = "";
        for(int i=1;i<=this.nombreDeSommets;i++) {
            for(int j=1;j<=this.nombreDeSommets;j++) {
                res += this.Matrice(i, j);
                if(j!= this.nombreDeSommets) {
                    res += " / ";
                }
            }
            if(i!= this.nombreDeSommets) {
                res += "\n";
            }
        }
        return res;
    }
    public void ajouterArc(int debut, int fin){
        this.modifierMatrice(debut, fin, 1);
    }
    
    public void ajouterArete(int debut, int fin){
        this.modifierMatrice(debut, fin, 1);
        this.modifierMatrice(fin, debut, 1);
    }
    
    public void supprimerArete(int debut, int fin)
    {
        this.modifierMatrice(debut, fin, 0);
        this.modifierMatrice(fin, debut, 0);
    }
    
    public void ajouterAretePoids(int debut, int fin , int poids){
        this.modifierMatrice(debut, fin, poids);
        this.modifierMatrice(fin, debut, poids);
    }
    
    public void nbElementaire(){
        System.out.println(this.nbActionElem);;
    }
    
    public int numeroCase(int i, int j, int m){
        return (m*(i-1)+j);
    } 
    
    public int JCase(int n,int taille){
        return n%taille;
    }
    
    public int ICase(int n, int j, int taille){
        return ((n-j)/taille);
    }
    
    
    public int distance(int a, int b){
        int res;
        Integer[] distance = new Integer[this.nombreDeSommets+1];
        int x;
        boolean Marks[] = new boolean[this.NombreSommet()+ 1];
        for (int i=0;i<=(this.nombreDeSommets);i++){
            Marks[i]=false;
            distance[i]=0;
        }
        Marks[0]=true;                    
        ArrayList<Integer> aTraiter=new ArrayList<>();
        aTraiter.add(a);
        while (aTraiter.isEmpty()!=true) {
            x=aTraiter.get(0);
            aTraiter.remove(0);
            for (int j=0;j<=this.NombreSommet();j++){
                if((Marks[j]==false) && (this.Matrice(j,x)==1)){
                    aTraiter.add(j);
                    Marks[j]=true;
                    distance[j]++;
                }
            }
        }
        res=distance[b];
        return res;
    }
    
    
    
    private void calculInfini(){
        this.infini=0;
        for(int i=1;i<=this.nombreDeSommets;i++){
            for(int j=1;j<=this.nombreDeSommets;j++){
                this.infini+=this.Matrice(i, j);
            }
        }
        this.infini+=1;
    }
    
    public int getInfinie(){
        return this.infini;
    }
    
    private void initialisation(int s){
        this.calculInfini();
        this.distance = new ArrayList<Integer>();
        for(int i=0; i<=this.nombreDeSommets;i++){
            
                this.mark[i]=false;
                this.distance.add(this.infini);            
                this.predecesseur[i]=-1;
            
        }
        this.distance.set(s, 0);
    }
    
    private int selectionSommet(){
        int indice=-1;
        int min=this.infini+2;
        for(int i=0;i<this.nombreDeSommets;i++){
            if(this.distance.get(i) < min && !mark[i]){
                indice=i;
                min=this.distance.get(i);              
            }
        }
        return indice;
    }
    
    private boolean existeSommetNonMarque(){
        boolean res=false;
        for(int i=0;i<this.nombreDeSommets;i++){
            if(!this.mark[i]){
                res=true;
            }
        }
        return res;
    }
    private void relachement(int a,int b){
        if(this.Matrice(a, b)!=0) {
            if(this.distance.get(b)>this.distance.get(a)+this.Matrice(a, b)) 
            {
                this.distance.set(b,this.distance.get(a)+this.Matrice(a, b));
                this.predecesseur[b]=a;
            }
        }
        
    }
    public int[] distanceDijkstra(int dep, int arr){
        //est ce que j'ai consideré le sommet
        //Tant que mark n'est pas vrai partout on choisit a<- le sommet non marquÃ© tel que d[a] est minimum
        //mark[a]<-vrai
        //pour tt sommet i
        //relachement(a,i) --> Si d[i]>d[a]+p[a,i] alors d[i]<- d[a]+p(a,i) et predecesseur[i]<-a
        this.initialisation(dep);
        
        
        while(this.existeSommetNonMarque()){
            int a = this.selectionSommet();
            this.mark[a]=true;
            for(int b=0;b<this.nombreDeSommets+1;b++){
                this.relachement(a, b);
            }
        }
        return this.predecesseur;
    }

    
    
    public ArrayList distancePred(){
        return this.distance;
    }
    
}
