import java.util.Arrays;
import java.util.Objects;
import static java.util.Arrays.fill;

import java.util.ArrayList;

class Sol implements Comparable<Sol>{
    // Vetor Solução
    public int[] s;
    // Referencia a um CFSP
    public CFSP cfsp;

    public Sol(CFSP cfsp){
        this.cfsp = cfsp;
        s = new int[cfsp.N];
        fill(s, 0);
    }

    public Sol(CFSP cfsp, int[] s){
        this(cfsp);
        this.s = s;
    }

    public void randomSol(){
        s = new int[cfsp.N];
        for (int i = 0; i < cfsp.N; i++)
            s[i] = i;
        Utils.shuffler(s);
    }

    public int d(int i, int j){

        int[] sum = new int[cfsp.M];
        fill(sum, 0);

        for(int k = 1; k < cfsp.M; k++){
            for(int h = 1; h <= k; h++)
                sum[k - 1] += cfsp.p[i][h];
            for(int h = 0; h <= k - 1; h++)
                sum[k - 1] -= cfsp.p[j][h];
        }

        int max = sum[0];
        for (int var : sum){
            if (var > max)
                max = var;
        }
    
        
        if (max < 0)
            max = 0;
        return cfsp.p[i][0] + max;

    }

    int funcaoObjetivo(){
        int fo = 0;
        for(int j = 1; j < cfsp.N; j++) 
            fo += d(s[j - 1], s[j]);
        for(int k = 0; k < cfsp.M; k++)
            fo += cfsp.p[s[cfsp.N - 1]][k];
        
        return fo;
    }
    
    int calcDias() {
    	return (int)funcaoObjetivo()/480;
    }

    void copy(Sol sol){
        for(int i = 0; i < cfsp.N; i++){
            s[i] = sol.s[i];
        }
    }

    @Override
    public String toString() {
        ArrayList S = new ArrayList<>();
        for (int var : s) {
            if(cfsp.prodQtd[var] != 0){
                S.add(var);
            }
        }
        return "Sol{ " + S +
        " FO = " + funcaoObjetivo() +
        "}\n";
    }

    public int compareTo(Sol sol) {
        return Double.compare(this.funcaoObjetivo(), sol.funcaoObjetivo());
    }
}