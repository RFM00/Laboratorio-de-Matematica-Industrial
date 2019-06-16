

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionDB {
	
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	
	public int qtdProdutos;
	public int qtdMaquinas;
	private Scanner sc;
	
	// conexoes ----
	
	public ConnectionDB() {
		connect();
	}
	
	public ConnectionDB(String path) throws FileNotFoundException {
		this();
		sc = new Scanner(new FileInputStream(path));
        sc.useLocale(Locale.US);

        qtdProdutos = sc.nextInt();
        qtdMaquinas = sc.nextInt();
        
        inserirProduto();
        inserirMaquina();
        inserirProduto_has_maquina();
        
	}
	
	public void connect() {
		String DRIVER = "com.mysql.cj.jdbc.Driver";
		String SERVER = "jdbc:mysql://localhost:3306/new_db_cfsp?useTimezone=true&serverTimezone=UTC";
		String USER = "root";
		String PASS = "";
		try {
			Class.forName(DRIVER);
			this.connection = DriverManager.getConnection(SERVER, USER, PASS);
			this.statement = this.connection.createStatement();
			
			
		} catch (Exception e) {
			System.out.println("ERRO: " + e.getMessage());
			
		}
		
		qtdProdutos = 0;
		try {
			String query = "SELECT * FROM produto;";
			this.resultSet = this.statement.executeQuery(query);
			this.statement = this.connection.createStatement();
			while(this.resultSet.next()) {
				qtdProdutos++;
			}
			
		}catch(Exception e){
			System.out.println("ERRO: " + e.getMessage());
		}
		
		qtdMaquinas = 0;
		try {
			String query = "SELECT * FROM maquina;";
			this.resultSet = this.statement.executeQuery(query);
			this.statement = this.connection.createStatement();
			while(this.resultSet.next()) {
				qtdMaquinas++;
			}
		}catch(Exception e){
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	
	
	public void getDados(CFSP cfsp) {
		cfsp.M = qtdMaquinas;
        cfsp.N = qtdProdutos;
        cfsp.p = new int[cfsp.N][cfsp.M];
        getProduto_has_maquina(cfsp.p);
	}
	
//	public void getPedidoSemana(int idPedido, int [][] p) {
//		try {
//			String query = "SELECT idDemanda FROM pedido_has_demanda where idPedido = '" + idPedido + "';";
//			this.resultSet = this.statement.executeQuery(query);
//			this.statement = this.connection.createStatement();
//			
//			while(this.resultSet.next()) {
//				
//			}
//			for(int i = 0; i < qtdMaquinas; i++) {
//				for(int j = 0; j < qtdProdutos; j++) {
//		            this.resultSet.next();
//		           	p[j][i] = resultSet.getInt("custo");
//		        }
//			}					
//		}catch(Exception e){
//			System.out.println("ERRO: " + e.getMessage());
//		}
//	}
	
	public void getPedidoSemana(CFSP cfsp, int idPedido) {
		try {
			cfsp.prodQtd = new int[qtdProdutos];
			Arrays.fill(cfsp.prodQtd, 0);
			String query = "SELECT idDemanda FROM pedido_has_demanda WHERE idPedido = '" + idPedido + "';";
			this.resultSet = this.statement.executeQuery(query);
			this.statement = this.connection.createStatement();
			
			String queryDemanda;
			Statement statementDemanda = null;
			ResultSet resultSetDemanda = null;
			while(this.resultSet.next()) {
				queryDemanda = "SELECT * FROM demanda_has_produto WHERE idDemanda = '" + this.resultSet.getInt(1) + "';";
				statementDemanda = this.connection.createStatement();
				resultSetDemanda = statementDemanda.executeQuery(queryDemanda);
				
				while(resultSetDemanda.next()) {
					cfsp.prodQtd[resultSetDemanda.getInt("idProduto") - 1] += resultSetDemanda.getInt("quantidade");					
				}
			}								
		}catch(Exception e){
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	
	
	
	public void getProduto_has_maquina(int [][] p) {
		try {
			String query = "SELECT * FROM produto_has_maquina ORDER BY idMaquina;";
			this.resultSet = this.statement.executeQuery(query);
			this.statement = this.connection.createStatement();
			for(int i = 0; i < qtdMaquinas; i++) {
				for(int j = 0; j < qtdProdutos; j++) {
		            this.resultSet.next();
		           	p[j][i] = resultSet.getInt("custo");
		        }
			}					
		}catch(Exception e){
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	
	
	
	public void inserirMaquina() {
		
		for(int i = 1; i <= qtdMaquinas; i++) {
			try {
				String query = "INSERT INTO maquina (nome) VALUES ('maquina " + i + "');";
				this.statement.executeUpdate(query);
			}catch(Exception e) {
				System.out.println("ERRO: " + e.getMessage());
			}
		}
		
		
	}
	
	public void inserirProduto() {
		int[] idx = new int[qtdMaquinas];

		  for(int i = 0; i < qtdMaquinas; i++) {
		      idx[i] = i + 1;
		  }
		for(int i = 1; i <= qtdProdutos; i++) {
			try {
				Utils.shuffler(idx);
				String query = "INSERT INTO produto (nome , sequencia) VALUES ('Produto " + i + "', '" + Arrays.toString(idx) + "');";
				this.statement.executeUpdate(query);
				
			}catch(Exception e) {
				System.out.println("ERRO: " + e.getMessage());
			}
		}
	}
	
	public void inserirProduto_has_maquina() {
            		
		for(int i = 1; i <= qtdProdutos; i++) {
			for(int j = 1; j <= qtdMaquinas; j++) {
				try {
					
					String query = "INSERT INTO produto_has_maquina (idProduto, idMaquina, custo) VALUES ('" + j + "', '" + i + "', '" + sc.nextInt() + "');";
					this.statement.executeUpdate(query);
				}catch(Exception e) {
					System.out.println("ERRO: " + e.getMessage());
				}
			}
			
		}
		
		sc.close();
	}
	
	public int inserirPedido(int semana) {
		int lastIdPedido = 0;
		
		try {
			String query = "INSERT INTO pedido (semana) VALUES ('" + semana + "');";
			this.statement.executeUpdate(query);
			
			query = "SELECT LAST_INSERT_ID();";
			this.resultSet = this.statement.executeQuery(query);
			this.statement = this.connection.createStatement();
			this.resultSet.next();
			lastIdPedido = this.resultSet.getInt(1);
			
		}catch(Exception e) {
			System.out.println("ERRO: " + e.getMessage());
		}
		
		return lastIdPedido;
	}
	
	
	public int inserirDemanda(String cliente, int idProdutoQuantidade[]) {
		int lastIdDemanda = 0;
		try {
			String query = "INSERT INTO demanda (cliente) VALUES ('" + cliente + "');";
			this.statement.executeUpdate(query);
			query = "SELECT LAST_INSERT_ID();";
			this.resultSet = this.statement.executeQuery(query);
			this.statement = this.connection.createStatement();
			this.resultSet.next();
			lastIdDemanda = this.resultSet.getInt(1);
			
			for(int i = 0; i < idProdutoQuantidade.length; i++) {
				inserirDemandaProduto(lastIdDemanda, i + 1 , idProdutoQuantidade[i]);
			}
			
		}catch(Exception e) {
			System.out.println("ERRO: " + e.getMessage());
		}
		
		return lastIdDemanda;
	}
	
	public void inserirDemandaProduto(int idDemanda, int idProduto, int quantidade) {
		Statement statementDemandaProduto = null;
		try {
			String query = "INSERT INTO demanda_has_produto (idDemanda, idProduto, quantidade) VALUES ('" + idDemanda + "', '" + idProduto + "', '" + quantidade + "');";
			statementDemandaProduto = this.connection.createStatement();
			statementDemandaProduto.executeUpdate("SET FOREIGN_KEY_CHECKS=0");
			statementDemandaProduto.executeUpdate(query);
		}catch(Exception e) {
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	
	
	public void inserirPedidoDemanda(int idPedido, int idDemanda) {
		try {
			String query = "INSERT INTO pedido_has_demanda (idPedido, idDemanda) VALUES ('" + idPedido + "', '" + idDemanda + "');";
			this.statement.executeUpdate(query);
		}catch(Exception e) {
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	
	
	// -------------------------------------------------------------------------------------------------------------------
	
	
	
	public void atualizarPedido(Sol s, int idPedido) {
		int atendeu = 0;
		int tempoTotal = s.calcDias();
//		try {
//			String query = "SELECT DATEDIFF(dataEntrega, dataPedido) FROM pedido where idPedido = '" + idPedido + "';";
//			this.resultSet = this.statement.executeQuery(query);
//			this.statement = this.connection.createStatement();
//			this.resultSet.next();
//			if(tempoTotal <= this.resultSet.getInt(1)) {
//				atendeu = 1;
//			}
//		}catch(Exception e) {
//			System.out.println("ERRO: " + e.getMessage());
//		}
		
		try {
			String query = "UPDATE pedido SET fluxo = '" + Arrays.toString(s.s) + "', tempoTotal = '" + tempoTotal + "', atendeuPedido = '" + atendeu + "' WHERE idPedido = '" + idPedido + "';";
			this.statement.executeUpdate(query);
		}catch(Exception e) {
			System.out.println("ERRO: " + e.getMessage());
		}
	}	
	
	
	public void listarProdutos() {
		
		try {
			String query = "SELECT * FROM produto;";
			this.resultSet = this.statement.executeQuery(query);
			this.statement = this.connection.createStatement();
			while(this.resultSet.next()) {
				System.out.println("ID: " + this.resultSet.getString("idProduto") + "nome: " + this.resultSet.getString("nome") + "sequencia: " + this.resultSet.getString("sequencia"));
			}
			
		}catch(Exception e){
			System.out.println("ERRO: " + e.getMessage());
		}
	}	
	
	
	public void editarProduto(int idProduto, String nome, int sequencia[]) {
		try {
			String query = "UPDATE produto SET nome = '" + nome + "', sequencia = '" + sequencia + "' WHERE idProduto = " + idProduto + ";";
			this.statement.executeUpdate(query);
		}catch(Exception e) {
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	
	public void deletarProduto(int idProduto) {
		try {
			String query = "DELETE FROM produto WHERE idProduto = " + idProduto + ";";
			this.statement.executeUpdate(query);
		}catch(Exception e) {
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	

	public void limparDB() {
		try {
			
			String query = "TRUNCATE TABLE produto";
			this.statement.executeUpdate(query);
			query = "TRUNCATE TABLE pedido";
			this.statement.executeUpdate(query);
			query = "TRUNCATE TABLE maquina";
			this.statement.executeUpdate(query);
			query = "SET FOREIGN_KEY_CHECKS = 0;";
			this.statement.executeUpdate(query);
			query = "TRUNCATE TABLE demanda";
			this.statement.executeUpdate(query);
			query = "TRUNCATE TABLE demanda_has_produto";
			this.statement.executeUpdate(query);
			query = "TRUNCATE TABLE produto_has_maquina";
			this.statement.executeUpdate(query);
			query = "TRUNCATE TABLE pedido_has_demanda";
			this.statement.executeUpdate(query);
			
		}catch(Exception e){
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	
	
	public boolean ConexaoON() {
		if(this.connection != null) {
			return true;
		}else {
			return false;
		}
	}
	
	public void conexaoOFF() {
		try {
			this.connection.close();
		}catch(Exception e) {
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	
}
	
	
	
	