package br.com.framework.interfac.crud;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public interface InterfaceCrud<T> extends Serializable {

	// salva os dados
	void save(T obj) throws Exception;

	void persist(T obj) throws Exception;

	// salva ou atualiza
	void saveOrUpdate(T obj) throws Exception;

	// atualiza
	void update(T obj) throws Exception;

	void delete(T obj) throws Exception;

	// salva e retorna
	T merge(T obj) throws Exception;

	// carrega a lista de dados de determinada classe
	List<T> findList(Class<T> objs) throws Exception;

	T findById(Class<T> entidade, Long id) throws Exception;
	
	T findByPorId(Class<T> entidade, Long id) throws Exception;

	List<T> findListByQueryDinamica(String s) throws Exception;

	void executeUpdateQueryDinamica(String s) throws Exception;

	void executeUpdateSQLDinamica(String s) throws Exception;

	// limpa a sessão do Hibernate
	void clearSession() throws Exception;

	// Retira um objeto da sessão do hibernate
	void evict(Object objs) throws Exception;

	Session getSession() throws Exception;

	List<?> getListSQLDinamica(String sql) throws Exception;

	// JDBC do Spring
	JdbcTemplate getJdbcTemplate();

	SimpleJdbcTemplate getSimpleJdbcTemplate();

	SimpleJdbcInsert getSimpleJdbcInsert();

	Long totalRegistro(String table) throws Exception;

	Query obterQuery(String query) throws Exception;
	
	// Carregamento dinamico vom JSF e PrimeFaces
	List<T> findListByQueryDinamica(String query, int iniciaNoRegistro, int maximoResultado) throws Exception;

}
