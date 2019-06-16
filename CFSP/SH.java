import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

// SH = Simple Heuristic

class SH{
    CFSP cfsp;
    Sol bestSol;
    private int popSize;
    private int ite;

    ArrayList<Sol> pop;

    public SH(CFSP cfsp, int popSize, int ite) {
    	this.cfsp = cfsp;
        bestSol = new Sol(cfsp);
        pop = new ArrayList<>();
    	
    	this.popSize = popSize;
        this.ite = ite;
    }
    
    public void localSearch(){
        insertK();
        swapK();
    }

    public void insertK(){
        Sol sc = new Sol(cfsp);
        bestSol.copy(pop.get(0));        

        for (int l = 0; l < popSize; ++l) {
            Sol v = pop.get(l);

            for(int k = 1; k <= cfsp.N / 2; k++){
                int e1 = k - 1;
                int e2 = -1 * e1;
                for(int i = 0; i < cfsp.N - (k - 1); i++){
                    for(int j = 0; j < cfsp.N - (k - 1); j++){
                        int dij = i - j;
                        if(dij != 0 && dij != k && 
                            (dij < e2 || dij > e1)){
                            for(int b = 0; b < cfsp.N; b++){
                                sc.s[b] = v.s[b];
                            }
                            // System.out.println("Array inicial " + Arrays.toString(sc));
                            if(i < j)
                                for(int b = i + k; b <= j + k - 1; b++){
                                    sc.s[b - k] = v.s[b];
                                }
                            else{
                                for(int b = j + k; b <= i + k - 1; b++){
                                    sc.s[b] = v.s[b - k];
                                }
                            }
                            for(int b = 0; b <= k - 1; b++){
                                sc.s[j + b] = v.s[i + b];
                            }
                            
                            // System.out.println("Novo array: " + Arrays.toString(sc));
                            if(bestSol.funcaoObjetivo() > sc.funcaoObjetivo()){   
                                Sol new_sol = new Sol(cfsp);
                                new_sol.copy(sc);
                                pop.add(new_sol); 
                                bestSol.copy(sc);
                                // System.out.println("MELHOROU!");
                                // System.out.println(sc.toString());
                            }   
                        }
                    }
                } 
            }
        }
    }

    void swapK(){
        bestSol.copy(pop.get(0));
        int b;
        for (int l = 0; l < popSize; ++l) {
            Sol s = pop.get(l);
            for(int k = 1; k < cfsp.N / 2; k++){
                // System.out.println("K = " + k);
                for(int i = 0; i < cfsp.N + 1 - 2 * k; i++){
                    for(int j = i + k; j < cfsp.N + 1 - k; j++){
                        // System.out.println("MAIN " + s.toString());
                        b = 0;
                        while(b < k){
                            // troca
                            int a = s.s[i + b];
                            s.s[i + b] = s.s[j + b];
                            s.s[j + b] = a;
                            b++;
                        }
                        // calcular funcao objetivo de sc
                        if(bestSol.funcaoObjetivo() > s.funcaoObjetivo()){   
                            Sol new_sol = new Sol(cfsp);
                            new_sol.copy(s);
                            pop.add(new_sol); 
                            bestSol.copy(s);
                            // System.out.println("MELHOROU! " + bestSol.toString());
                            // System.out.println(sc.toString());
                        }else{
                            // System.out.println("ELSE " + s.toString());
                            b = 0;
                            while(b < k){
                                // ta destrocando?
                                int a = s.s[j + b];
                                s.s[j + b] = s.s[i + b];
                                s.s[i + b] = a;
                                b++;
                            }
                        }
                    }
                }
            }
        }
    }

    public void run(){
        initialPopulation();
        int popMean = popMean(pop); // calcula media da população
        int currentPopMean;

        int it = 0;

        System.out.println("Populacao inicial: ");
        Collections.sort(pop);
        System.out.println(toString());
        System.out.println("Média da populacao: " + popMean);

        while(it < ite){
            // localSearch();
            swapK();
            insertK();
            
            Collections.sort(pop);

            while (pop.size() > popSize){
                pop.remove(pop.size() - 1);
            }

            System.out.println("Geracao: " + it + " Media da populacao: " + popMean);
            System.out.println("Melhor " + pop.get(0).toString());
            System.out.println("Pior " + pop.get(popSize - 1).toString());

            // System.out.println(toString());

            currentPopMean = popMean(this.pop);

            
            if(currentPopMean == popMean){
                System.out.println("CONVERGIU: " + currentPopMean);
                System.out.println(toString());
                ConnectionDB db = new ConnectionDB();
                db.atualizarPedido(pop.get(0), 1);
                break;
            }
            
            popMean = currentPopMean;

            it++;
        }
        // System.out.println(toString());
    }

    public void initialPopulation() {
        pop.clear();
        for (int i = 0; i < popSize; i++) {
            Sol sol = new Sol(cfsp);
            sol.randomSol();
            pop.add(sol);
        }
    }

    public int popMean(ArrayList<Sol> pop){
        int mean = 0;
        for (Sol var : pop) {
            mean += var.funcaoObjetivo();
        }
        return mean / pop.size();
    }

    public Sol getSol() {
        return bestSol;
    }

    @Override
    public String toString() {
        return this.pop.toString();
    }
}