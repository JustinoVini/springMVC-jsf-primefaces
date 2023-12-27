package br.com.framework.hibernate.session;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.SessionFactoryImplementor;

import br.com.framework.implementacao.crud.VariavelConexaoUtil;

/**
 * Responsavel pela conexão com o hibernate
 * 
 * @author emers
 *
 */

public class HibernateUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	public static String JAVA_COMP_ENV_JDBC_DATA_SOURCE = "java:/comp/env/jdbc/datasources";

	private static SessionFactory sessionFactory = buildSessionFactory();

	/**
	 * Responsavel por ler o arquivo de configuração hibernate.cfg.xml
	 * 
	 * @return SessionFactory
	 */
	private static SessionFactory buildSessionFactory() {

		try {

			if (sessionFactory == null) {
				sessionFactory = new Configuration().configure().buildSessionFactory();
			}

			return sessionFactory;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExceptionInInitializerError("Erro ao cria conexão com SessionFactory");
		}

	}

	/**
	 * Retorna o sessionFactory corrente
	 * 
	 * @return SessionFactory
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * Retorna a sessão do SessionFactory
	 * 
	 * @return Session
	 */
	public static Session getCurrentySession() {
		return getSessionFactory().getCurrentSession();
	}

	/**
	 * Abre nvoa sessão no SessionFactory
	 * 
	 * @return Session
	 */
	public static Session openSession() {
		if (sessionFactory == null) {
			buildSessionFactory();
		}

		return sessionFactory.openSession();
	}
	
	/**
	 * Obtem a connection do provedor de conexão
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnectionProvider() throws SQLException {
		
		return ((SessionFactoryImplementor) sessionFactory).getConnectionProvider().getConnection();
		
	}
	
	/**
	 * 
	 * @return Connection no InitialContext
	 * @throws Exception
	 */
	public static Connection getConnection() throws Exception {
		InitialContext context = new InitialContext();
		DataSource ds = (DataSource) context.lookup(JAVA_COMP_ENV_JDBC_DATA_SOURCE);
		
		return ds.getConnection();
	}
	
	/**
	 * 
	 * @return DataSourceJNDI
	 * @throws NamingException
	 */
	public DataSource getDataSourceJndi() throws NamingException {
		InitialContext context = new InitialContext();
		return (DataSource) context.lookup(VariavelConexaoUtil.JAVA_COMP_ENV_JDBC_DATA_SOURCE);
	}

}
