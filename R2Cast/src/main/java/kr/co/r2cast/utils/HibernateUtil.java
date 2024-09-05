package kr.co.r2cast.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class HibernateUtil {
	private static SessionFactory sessionFactory = configureSessionFactory();
	private static ServiceRegistry serviceRegistry;
	
	/**
	 * Hibernate 세션 팩토리 구성
	 */
	private static SessionFactory configureSessionFactory() {
		SessionFactory sf = null;
		try {
			Configuration configuration = new Configuration();
			configuration.configure();
			
			serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
			sf = configuration.buildSessionFactory(serviceRegistry);
			//sf = new Configuration().configure().b .buildSessionFactory();
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
		
		return sf;
	}
	
	/**
	 * Hibernate 세션 팩토리 획득
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * Hibernate 세션 팩토리 닫기
	 */
	public static void shutdown() {
		getSessionFactory().close();
	}
	
	/**
	 * Hibernate 모델 구성 정보를 바탕으로 데이터베이스 개체 초기화
	 */
	public static void initSchema() {
		Configuration cfg = new Configuration().configure();
		SchemaExport schemaExport = new SchemaExport(cfg);
		
		schemaExport.create(true, true);
	}
}
