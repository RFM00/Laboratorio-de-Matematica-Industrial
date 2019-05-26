import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import static java.util.Arrays.fill;

class CFSP{
    // Número de Operações
    public int N;
    // Número de Máquinas
    public int M;
    // Matriz m x n, que representa o custo de cada máquina em cada operação.
    public int[][] p;

    public int[] prodQtd;
    
    public CFSP() {
    	
    }
    
    public CFSP(String path) throws FileNotFoundException {
        Scanner sc = new Scanner(new FileInputStream(path));
        sc.useLocale(Locale.US);

        N = sc.nextInt();
        M = sc.nextInt();

        p = new int[N][M];
        prodQtd = new int[N];
        fill(prodQtd, 1);

        for(int i = 0; i < M; i++)
            for(int j = 0; j < N; j++)
                p[j][i] = sc.nextInt();
            
        sc.close();

    }

    public CFSP(CFSP cfsp, int[] prodQtd){
        N = cfsp.N;
        M = cfsp.M;

        p = new int[N][M];
        this.prodQtd = prodQtd;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if(prodQtd[i] == 0)
                    p[i][j] = 0;
                else
                    p[i][j] = cfsp.p[i][j]; 
            }
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                p[i][j] *= prodQtd[i];
            }
        }
    }

    @Override
    public String toString(){
        return "CFSP{" + 
                "N = " + this.N + 
                ", M = " + this.M + 
                ",\n p = " + Arrays.deepToString(p) +
                ",\n Quantidade pedido: " + Arrays.toString(prodQtd);
    }
}