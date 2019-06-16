import java.io.FileNotFoundException;
import java.util.Collections;

class Main{
    public static void main(String args[]) throws FileNotFoundException{

//        ConnectionDB db = new ConnectionDB("instances/problem1.txt");
    	ConnectionDB clearDB = new ConnectionDB();
    	clearDB.limparDB();
    	clearDB.conexaoOFF();
    	
    	int[] prodQtd = {1, 1, 4, 0, 1};
		ConnectionDB db = new ConnectionDB("instances/problem2.txt");
//		db.inserirPedido("Jorge", "2019-05-21", "2019-05-27", prodQtd);
		CFSP cfsp = new CFSP();
	    db.getDados(cfsp);
	    
	    int a = db.inserirDemanda("Joao", prodQtd);
	    a = db.inserirDemanda("Joaquim", prodQtd);
	    
	    a = db.inserirPedido(1);
	    db.inserirPedidoDemanda(a, 1);
	    db.inserirPedidoDemanda(a, 2);
	    
	    
	    
//	    System.out.println(a);
	    db.getPedidoSemana(cfsp, 1);
	    System.out.println(cfsp);
	    SH sh = new SH(cfsp, 50, 10);
	    sh.run();
	    db.conexaoOFF();
//      db.limparDB();

    }
}