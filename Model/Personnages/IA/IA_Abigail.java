package stardewvalleyautomaton.Model.Personnages.IA;

import stardewvalleyautomaton.Model.Objets.*;
import java.util.ArrayList;
import stardewvalleyautomaton.Model.Cases.Case;
import stardewvalleyautomaton.Model.Cases.Case_Dirt;
import stardewvalleyautomaton.Model.Cases.Case_Grass;
import stardewvalleyautomaton.Model.Cases.Case_LightGrass;
import stardewvalleyautomaton.Model.Cases.Enum_Case;
import stardewvalleyautomaton.Model.Gestionnaires.GestionnaireDesObjets;
import stardewvalleyautomaton.Model.Gestionnaires.GestionnaireDesPersonnages;
import stardewvalleyautomaton.Model.Graphe;
import static stardewvalleyautomaton.Model.Objets.Enum_Objet.Machine_Fromage;
import stardewvalleyautomaton.Model.Personnages.Abigail;
import static stardewvalleyautomaton.Model.Personnages.Enum_Personnage.Abigail;
import static stardewvalleyautomaton.Model.Personnages.Enum_Personnage.Vache;
import static stardewvalleyautomaton.Model.Personnages.IA.Enum_Action.*;
import stardewvalleyautomaton.Model.Personnages.Personnage;
import stardewvalleyautomaton.Model.Personnages.Vache;

/**
 *
 * @author NAAJI Dorian, RAKIC Jules, RUFFIN Lélian, RABUT Quentin
 */
public class IA_Abigail extends IA {

    
    
    /**
     * Variables booléennes gérant les différents états et actions.
     */
    private boolean _ponte = false;
    private boolean _enTraitement = false;
    private boolean _traireVache = false;
    private boolean _faireFromage = false;
    private boolean _allerMachine = false;
    private boolean _allerVache = false;
    private Machine_Fromage _machine;
    private Vache _vache;
    
    /**
     * Variables gérant les besoins d'Abigail.
     */
    private int _fatigue = 0;
    private int _soif = 0;
    private int _faim = 0;
    
    
    /**
     * Variables gérant les emplacements de personnages/objets/types de cases
     */
    private Abigail _abigail;
    private Abigail doppelganger;
    private int j = 0;
    private final ArrayList<Objet> _objet = GestionnaireDesObjets.getListeDesObjets();
    private final ArrayList _perso = GestionnaireDesPersonnages.getListeDesPersonnages();
    private ArrayList<Integer> _sommets = new ArrayList<Integer>();
    
    /**
     * Variables gérant dijkstra et les mouvements.
     */
    private int _case;
    private ArrayList<Integer> dijkstra;
    private int[] _tabPred;
    private ArrayList<ArrayList<Enum_Action>> _tabDijkstra;
    private Graphe _graphe;
    private ArrayList<Enum_Action> _mouvement = new ArrayList();
    
    

    /**
     * 
     */
    @Override
    protected void setActionValide() {
        this.addActionValide(attendre);
        this.addActionValide(moveLeft);
        this.addActionValide(moveRight);
        this.addActionValide(moveTop);
        this.addActionValide(moveBottom);
        this.addActionValide(traire);
        this.addActionValide(produireFromage);
        this.addActionValide(collecterOeuf);
    }

    
    /**
     * Procédure permettant de construire le graphe
     * @param g le graphe à modifier
     */
    public void constructionGraphe(Graphe g){
        boolean sommetBon = true;
        for (int i = 0; i < _objet.size(); i++){ // boucle parcourant un tableau contenant les objets de la carte
            this._sommets.add(((this._objet.get(i).getCase().getLigne() + 1) * 30) - (30 - this._objet.get(i).getCase().getColonne() - 1));
        }
            
        for (int i=0; i<900; i++){ // création du graphe
            for (int a = 0; a < this._sommets.size(); a++){
                if (i == this._sommets.get(a)){ // condition permettant de savoir si un objet est présent sur le sommet courant
                    sommetBon = false; 
                }
            }

            if (sommetBon){ // ajout des arêtes
                if (i % 30 != 0){
                    g.ajouterArete(i, i + 1);
                    g.ajouterArete(i, i + 30);
                    }
                else{
                    g.ajouterArete(i, i + 30);
                }
            } 
            else{ // suppression des arêtes
                g.supprimerArete(i, i + 1);
                g.supprimerArete(i, i - 1);
                g.supprimerArete(i, i - 30);
                g.supprimerArete(i, i + 30);
                sommetBon = true;
            }
        }
    }
        
    
    /**
     * L'algorithme de dijkstra
     * @param dep case de départ
     * @param arr case d'arrivée
     */
    private void Dijkstra(int dep, int arr) {
        //Permet de parcourir le tabpredecesseurs et d'obtenir le chemin le plus court

        this._tabPred = this._graphe.distanceDijkstra(dep, arr);

        this.dijkstra = new ArrayList();
        this._case = arr;

        int i = this._case;

        this.dijkstra.add(i);
        while (this._tabPred[i] != dep) {
            i = this._tabPred[i];
            this.dijkstra.add(i);
        }
        this.dijkstra.add(dep);

    }
    
    /**
     * tableau des actions
     * @param dep case de départ
     * @param arr case d'arrivée
     * @return une action
     */
    private ArrayList<Enum_Action> tabAction(int dep, int arr) {
        ArrayList res = new ArrayList();
        this.Dijkstra(dep, arr);
        if (this.dijkstra.size() <= 2) {
            res.add(this.mouv(1));
        } else {
            for (int i = this.dijkstra.size() - 1; i >= 1; i--) {
                res.add(this.mouv(i));
            }
        }
        return res;
    }
    
    
    /**
     * Procédure permettant de rechercher les personnages (vache et abigail) sur la carte).
     */
    private void rechPerso(){
        for (int i = 0; i <= this._perso.size() - 1; i++){
            Personnage pVache = (Personnage) this._perso.get(i);
            Personnage pAbigail = (Personnage) this._perso.get(i);
            
            if (pVache.getType() == Vache) { // recherche de la vache
                this._vache = (Vache) _perso.get(i);
            }
            
            if (pAbigail.getType() == Abigail) { // recherche d'abigail
                this._abigail = (Abigail) _perso.get(i);
            }   
        }
    }
    
    /**
     * Procédure permettant de rechercher les objets sur la carte.
     */
    private void rechObjet(){
        for (int i = 0; i <= this._objet.size() - 1; i++){
            Objet obj = (Objet) this._objet.get(i);
            if (obj.getType() == Machine_Fromage) { // recherche de la machine à fromage
                this._machine = (Machine_Fromage) this._objet.get(i);
            }
        }
    }
    

    
    /**
     * Procédure gérant les booléens relatifs à la création du fromage.
     */
    private void switchBoolFromage(){
        this._enTraitement = true;
        int a = (((this._machine.getCase().getLigne() + 2) * 30) - (30 - this._machine.getCase().getColonne() - 1)); // coordonnée de la machine à fromage
        this.bouger(a);
        this._faireFromage = false;
        this._allerMachine = true;
    }
    
    /**
     * Procédure gérant les booléens relatifs à la "récolte" du lait.
     */
    private void switchBoolVache(){
        this._enTraitement = true;
        int a = (((this._vache.getCase().getLigne() + 1) * 30) - (30 - this._vache.getCase().getColonne() - 2));
        this.bouger(a);
        this._traireVache = true;
        this.j = 0;
    }
    
    /**
     * Fonction permettant à Abigail d'aller récolter un oeuf.
     * @return une action : se déplacer à l'oeuf ou collecter l'oeuf voulu
     */
    private Enum_Action collecterOeuf(){
        Enum_Action action=attendre;
        
        /**
         * gestion des mouvements
         */
        if (j < this._mouvement.size()) {
            action = this._mouvement.get(j);
            j++;//
            
            /**
             * gestion des besoins
             */
            if(this._fatigue < 50){
                this._fatigue +=1; // chaque dépalcement d'une case ajoute un point de fatigue
                if(this._fatigue >= 99){ // Abigail ne réalise jamais une action faisant dépasser 100 points de fatigue
                        action = attendre;
                        j--;
                        this._fatigue-=50;
                        System.err.println("Abigail se repose.");
                    } 
            }
            if(this._fatigue >= 50){
                if(this._abigail.getCase().getType() == Enum_Case.dirt){
                    this._fatigue+=1; //chaque déplacement sur une case de dirt ajoute un point de fatigue
                    if(this._fatigue >= 99){ // Abigail ne réalise jamais une action faisant dépasser 100 points de fatigue
                        action = attendre;
                        j--;
                        this._fatigue-=50;
                        System.err.println("Abigail se repose.");
                    } 
                }
                if(this._abigail.getCase().getType() == Enum_Case.lightgrass){
                    this._fatigue+=2; //chaque déplacement sur une case de lightgrass ajoute deux points de fatigue
                    if(this._fatigue >= 98){ // Abigail ne réalise jamais une action faisant dépasser 100 points de fatigue
                        action = attendre;
                        j--;
                        this._fatigue-=50;
                        System.err.println("Abigail se repose.");
                    } 
                }
                if(this._abigail.getCase().getType() == Enum_Case.grass){
                    this._fatigue+=3; //chaque déplacement sur une case de dirt ajoute trois points de fatigue
                    if(this._fatigue >= 97){ // Abigail ne réalise jamais une action faisant dépasser 100 points de fatigue
                        action = attendre;
                        j--;
                        this._fatigue-=50;
                        System.err.println("Abigail se repose.");
                    } 
                }
            }     
            if (this._soif<100){
                this._soif +=1; // chaque mouvement ajoute un point de soif peu importe la fatigue
            }
        }
        
        else{
            action = collecterOeuf;
            this._ponte = false;
            this._enTraitement = false;
            if (this._vache.lait()){
                this._allerVache = true;
            }
            this.j = 0;
            if(this._faim<100){
                this._faim+=5; // rammaser un oeuf donne faim à Abigail et ajoute 5 points de faim
            }
        }
        System.out.println("fatigue : " + this._fatigue);
        System.out.println("soif : " + this._soif);
        System.out.println("faim : " + this._faim);
        return action;
        
    }
    
    /**
     * Fonction permettant à Abigail d'aller traire la vache
     * @return une action : se déplacer à la vache ou traire la vache
     */
    private Enum_Action traireVache(){
        Enum_Action action = attendre;
        if (j < this._mouvement.size()) {
                this._vache.action(attendre);
                action = this._mouvement.get(j);
                j++;
                
                
            /**
             * gestion des besoins
             */
            if(this._fatigue < 50){
                this._fatigue +=1; // chaque dépalcement d'une case ajoute un point de fatigue
            }
            if(this._fatigue >= 50){
                if(this._abigail.getCase().getType() == Enum_Case.dirt){
                    this._fatigue+=1; //chaque déplacement sur une case de dirt ajoute un point de fatigue
                    if(this._fatigue >= 99){ // Abigail ne réalise jamais une action faisant dépasser 100 points de fatigue
                        action = attendre;
                       // j--;
                        this._fatigue-=50;
                        System.err.println("Abigail se repose.");
                    } 
                }
                if(this._abigail.getCase().getType() == Enum_Case.lightgrass){
                    this._fatigue+=2; //chaque déplacement sur une case de lightgrass ajoute deux points de fatigue
                    if(this._fatigue >= 98){ // Abigail ne réalise jamais une action faisant dépasser 100 points de fatigue
                        action = attendre;
                       // j--;
                        this._fatigue-=50;
                        System.err.println("Abigail se repose.");
                    } 
                }
                if(this._abigail.getCase().getType() == Enum_Case.grass){
                    this._fatigue+=3; //chaque déplacement sur une case de dirt ajoute trois points de fatigue
                    if(this._fatigue >= 97){ // Abigail ne réalise jamais une action faisant dépasser 100 points de fatigue
                        action = attendre;
                       // j--;
                        this._fatigue-=50;
                        System.err.println("Abigail se repose.");
                    } 
                }
            }
            if (this._soif<100){
                this._soif +=1; // chaque mouvement ajoute un point de soif peu importe la fatigue
            }
                
            } else {
                action = traire;
                this._enTraitement = false;
                this._traireVache = false;
                this._faireFromage = true;
                this.j = 0;
            }
        System.out.println("fatigue : " + this._fatigue);
        System.out.println("soif : " + this._soif);
        System.out.println("faim : " + this._faim);
        return action;
    }
    
    /**
     * Fonction permettant à Abigail d'aller faire du fromage
     * @return une action : se déplacer à la machine à fromage ou produire du fromage
     */
    private Enum_Action faireFromage(){
        Enum_Action action = attendre;
        if (j < this._mouvement.size()) {
                action = this._mouvement.get(j);
                j++;
                
                
                /**
             * gestion des besoins
             */
            if(this._fatigue < 50){
                this._fatigue +=1; // chaque dépalcement d'une case ajoute un point de fatigue
                if(this._fatigue >= 99){ // Abigail ne réalise jamais une action faisant dépasser 100 points de fatigue
                        action = attendre;
                        j--;
                        this._fatigue-=50;
                        System.err.println("Abigail se repose.");
                    } 
            }
            if(this._fatigue >= 50){
                if(this._abigail.getCase().getType() == Enum_Case.dirt){
                    this._fatigue+=1; //chaque déplacement sur une case de dirt ajoute un point de fatigue
                    if(this._fatigue >= 99){ // Abigail ne réalise jamais une action faisant dépasser 100 points de fatigue
                        action = attendre;
                        j--;
                        this._fatigue-=50;
                        System.err.println("Abigail se repose.");
                    } 
                }
                if(this._abigail.getCase().getType() == Enum_Case.lightgrass){
                    this._fatigue+=2; //chaque déplacement sur une case de lightgrass ajoute deux points de fatigue
                    if(this._fatigue >= 98){ // Abigail ne réalise jamais une action faisant dépasser 100 points de fatigue
                        action = attendre;
                        j--;
                        this._fatigue-=50;
                        System.err.println("Abigail se repose.");
                    } 
                }
                if(this._abigail.getCase().getType() == Enum_Case.grass){
                    this._fatigue+=3; //chaque déplacement sur une case de dirt ajoute trois points de fatigue
                    if(this._fatigue >= 97){ // Abigail ne réalise jamais une action faisant dépasser 100 points de fatigue
                        action = attendre;
                        j--;
                        this._fatigue-=50;
                        System.err.println("Abigail se repose.");
                    } 
                }

            }
            if(this._soif<100){
                this._soif +=1; // chaque mouvement ajoute un point de soif peu importe la fatigue
            }
            } else {
                action = produireFromage;
                this.j = 0;
                this._allerMachine = false;
                this._enTraitement = false;
            }
        System.out.println("fatigue : " + this._fatigue);
        System.out.println("soif : " + this._soif);
        System.out.println("faim : " + this._faim);
        return action;
    }
    
    /**
     * Fonction gérant les déplacements dans la grille
     * @param i une case
     * @return la direction à emprunter par Abigail
     */
    private Enum_Action mouv(int i) {
        Enum_Action res = attendre;
        int a = this.dijkstra.get(i - 1) - this.dijkstra.get(i);
        switch (a) {
            case 1:
                res = moveRight;
                break;
            case -1:
                res = moveLeft;
                break;
            case 30:
                res = moveBottom;
                break;
            case -30:
                res = moveTop;
                break;
        }

        return res;
    }
    
    /**
     * IA D'Abigail.
     * @return une action
     */
    @Override
    public Enum_Action action() {
        //Déclaration variables
        Enum_Action actionFinale = attendre;

      
        /**
         * Gestion du graphe
         */
        if (this._graphe==null){ 
            this._graphe = new Graphe(30*30);
            this.constructionGraphe(this._graphe);
            this.rechPerso();
            this.rechObjet();
        }

        /**
         * Gestion des oeufs
         */
        if (this._enTraitement == false) {
            this.analyseOeuf();
        }
        if ( (this._ponte) && !(this._allerVache) ) {
            actionFinale = this.collecterOeuf();
        }


        
        /**
         * Gestion du lait
         */
        if ( (this._vache.lait() && this._traireVache == false && this._faireFromage != true && this._abigail.getLait() != true) && (actionFinale == attendre)  ) {
            this.switchBoolVache();
        }
        if (this._traireVache == true) {
            actionFinale = this.traireVache();
        }
        
        
        /**
         * Gestion du fromage
         */
        if (this._faireFromage == true) {
            this.switchBoolFromage();
        }
        
        if ( (this._allerMachine) && (this._abigail.getLait()) && (!this._abigail.getFromage()) ) {
            actionFinale = this.faireFromage();
        }
        if ( (this._abigail.getFromage()) && (this._faim>70) ) {
            this._abigail.actionSpeciale(manger);
            this._faim=0;
            System.err.println("Abigail avait faim... Elle a mangé son fromage.");
        }
        
        if( (this._soif > 70) && (this._abigail.getLait()) ){
            this._abigail.setLait(false);
            System.err.println("Abigail avait trop soif... Elle a bu le lait. ");
        }
        
        return actionFinale;
    }
    

    /**
     * 
     * @param b
     * @return 
     */
    private int bouger(int b) {

        int a = (((this.personnage().getCase().getLigne() + 1) * 30) - (30 - this.personnage().getCase().getColonne() - 1));
        if (a != b) {
            this._mouvement = this.tabAction(a, b);
        } else {
            this._mouvement.add(attendre);
        }
        return this._mouvement.size();
    }

    /**
     * 
     * @param b
     * @return 
     */
    public ArrayList<Enum_Action> bougerTab(int b) {
        ArrayList<Enum_Action> res = new ArrayList();
        int a = (((this.personnage().getCase().getLigne() + 1) * 30) - (30 - this.personnage().getCase().getColonne() - 1));
        if (a != b) {
            res = this.tabAction(a, b);
        } else {
            res.add(attendre);
        }
        return res;
    }

    

    /**
     * 
     * @return 
     */
    private boolean analyseOeuf() {
        boolean res = false;
        int ind = 0;
        ArrayList<Enum_Action> temp = new ArrayList();
        ArrayList<Enum_Action> tab = new ArrayList();
        this._tabDijkstra = new ArrayList();
        if (this._enTraitement == false) {
            for (int i = 0; i < _objet.size(); i++){
                Objet o = (Objet) _objet.get(i);
                if (o.getType() == Enum_Objet.Oeuf){
                    this._ponte = true;
                    this._enTraitement = true;
                    res = true;
                    temp = this.bougerTab(((o.getCase().getLigne() + 1) * 30) - (30 - o.getCase().getColonne() - 1));
                    this._tabDijkstra.add(temp);
                }
            }
            
            if (this._tabDijkstra.size() > 1) {
                tab = this._tabDijkstra.get(0);

                for (int k = 1; k < this._tabDijkstra.size(); k++) {
                    if (tab.size() > this._tabDijkstra.get(k).size()) {
                        tab = this._tabDijkstra.get(k);
                        ind = k;
                    }
                }
                this._tabDijkstra.remove(ind);
            } 
            else if (this._tabDijkstra.size() == 1) {
                tab = temp;
                this._tabDijkstra.remove(0);
            }

            this._mouvement = tab;
        }

        return res;
    }
}
