package br.com.zup.estrelas.cidades.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zup.estrelas.cidades.connection.factory.ConnectionFactory;
import br.com.zup.estrelas.cidades.pojo.CidadePOJO;

public class CidadeDAO {
	private Connection conexao;

	public CidadeDAO() {
		this.conexao = new ConnectionFactory().obterConexao();
	}

	public CidadePOJO montarObjetosCidades(ResultSet rs) throws SQLException {
		CidadePOJO cidade = new CidadePOJO();

		cidade.setCep(rs.getString("cep"));
		cidade.setNome(rs.getString("nome"));
		cidade.setNumeroHabitantes(rs.getInt("numero_habitantes"));
		cidade.setCapital(rs.getBoolean("capital"));
		cidade.setEstado(rs.getString("estado"));
		cidade.setRendaPerCapita(rs.getDouble("renda_per_capita"));
		cidade.setDataFundacao(rs.getString("data_fundacao"));

		return cidade;
	}

	public boolean insereCidadeBD(CidadePOJO cidade) {
		String inserirCidadeSql = "INSERT INTO cidade"
				+ "(cep, nome, numero_habitantes, capital, estado, renda_per_capita, data_fundacao)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement stmt = conexao.prepareStatement(inserirCidadeSql);
			stmt.setString(1, cidade.getCep());
			stmt.setString(2, cidade.getNome());
			stmt.setInt(3, cidade.getNumeroHabitantes());
			stmt.setBoolean(4, cidade.isCapital());
			stmt.setString(5, cidade.getEstado());
			stmt.setDouble(6, cidade.getRendaPerCapita());
			stmt.setString(7, cidade.getDataFundacao());

			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			System.err.println("N�o foi possivel cadastrar cidade, tente novamente");
			System.err.println(e.getMessage());
			return false;
		}

		return true;
	}

	public boolean excluirCidadeBD(String cep) {
		String deletarCidadeSql = "DELETE FROM cidade c WHERE c.cep = ?";

		if (buscaCepCadastrado(cep).getCep() == null) {
			System.out.printf("CEP %s invalido, tente novamente\n", cep);
			return false;
		}

		try {
			PreparedStatement stmt = conexao.prepareStatement(deletarCidadeSql);
			stmt.setString(1, cep);

			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			System.err.printf("N�o foi possivel excluir o CEP %s no BD, tente novamente\n", cep);
			System.err.println(e.getMessage());
			return false;
		}

		return true;
	}

	public CidadePOJO buscaCepCadastrado(String cep) {
		CidadePOJO cidade = new CidadePOJO();

		String buscarCepSql = "SELECT * FROM cidade c WHERE c.cep = ?";

		try {
			PreparedStatement stmt = conexao.prepareStatement(buscarCepSql);
			stmt.setString(1, cep);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				cidade.setCep(rs.getString("cep"));
				cidade.setNome(rs.getString("nome"));
				cidade.setNumeroHabitantes(rs.getInt("numero_habitantes"));
				cidade.setCapital(rs.getBoolean("capital"));
				cidade.setEstado(rs.getString("estado"));
				cidade.setRendaPerCapita(rs.getDouble("renda_per_capita"));
				cidade.setDataFundacao(rs.getString("data_fundacao"));
			}

		} catch (SQLException e) {
			System.err.println("Erro ao lista cidade" + e.getMessage());
		}

		return cidade;
	}

	public boolean verificarEstadoCadastrado(String estado) {
		String buscarExistenciaCepSql = "SELECT * FROM estado e WHERE e.sigla = ?";

		try {
			PreparedStatement stmt = conexao.prepareStatement(buscarExistenciaCepSql);
			stmt.setString(1, estado);

			if (stmt.executeQuery().next()) {
				stmt.close();
				return true;
			}
		} catch (SQLException e) {
			return false;
		}

		return false;
	}

	public List<CidadePOJO> listarCidadesBD() {
		List<CidadePOJO> cidades = new ArrayList<>();

		String consultaCidadesSql = "SELECT * FROM cidade";

		try {
			PreparedStatement stmt = conexao.prepareStatement(consultaCidadesSql);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				CidadePOJO cidade = montarObjetosCidades(rs);
				cidades.add(cidade);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar cidade " + e.getMessage());
		}
		return cidades;
	}

	public List<CidadePOJO> listarCidadesPorNome(String nome) {
		List<CidadePOJO> cidades = new ArrayList<>();

		String consultaCidadesSql = "SELECT * FROM cidade c WHERE c.nome LIKE ?";

		try {
			PreparedStatement stmt = conexao.prepareStatement(consultaCidadesSql);
			nome += '%';
			stmt.setString(1, nome);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				CidadePOJO cidade = montarObjetosCidades(rs);
				cidades.add(cidade);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar cidade " + e.getMessage());
		}

		return cidades;
	}

	public List<CidadePOJO> listarCidadesPorSigla(String sigla) {
		List<CidadePOJO> cidades = new ArrayList<>();

		String consultaCidadesSql = "SELECT * FROM cidade c WHERE c.estado = ?";

		try {
			PreparedStatement stmt = conexao.prepareStatement(consultaCidadesSql);

			stmt.setString(1, sigla);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				CidadePOJO cidade = montarObjetosCidades(rs);
				cidades.add(cidade);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar cidade " + e.getMessage());
		}
		return cidades;
	}

	public List<CidadePOJO> listarCidadesPorCapital(boolean capital) {
		List<CidadePOJO> cidades = new ArrayList<>();

		String consultaCidadesSql = "SELECT * FROM cidade c WHERE c.capital = ?";

		try {
			PreparedStatement stmt = conexao.prepareStatement(consultaCidadesSql);

			stmt.setBoolean(1, capital);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				CidadePOJO cidade = montarObjetosCidades(rs);
				cidades.add(cidade);
			}
		} catch (Exception e) {
			System.err.println("Erro ao listar cidade " + e.getMessage());
		}
		return cidades;
	}
}
