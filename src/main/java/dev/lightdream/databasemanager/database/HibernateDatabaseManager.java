package dev.lightdream.databasemanager.database;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.config.SQLConfig;
import dev.lightdream.databasemanager.dto.DatabaseEntry;
import dev.lightdream.lambda.lambda.ArgLambdaExecutor;
import dev.lightdream.messagebuilder.MessageBuilder;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.File;
import java.util.List;
import java.util.Properties;

public abstract class HibernateDatabaseManager {

    private final DatabaseMain main;
    private SessionFactory sessionFactory;

    public HibernateDatabaseManager(DatabaseMain main) {
        this.main = main;

        connect();
    }

    public SQLConfig sqlConfig() {
        return main.getSqlConfig();
    }

    public File dataFolder() {
        return main.getDataFolder();
    }

    public void connect() {
        try {
            Configuration configuration = new Configuration();

            Properties props = new Properties();

            switch (sqlConfig().driverType) {
                case MYSQL:
                    props.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
                    props.put("hibernate.connection.url", new MessageBuilder("jdbc:mysql://%host%/%database%%args%")
                            .parse("host", sqlConfig().host)
                            .parse("database", sqlConfig().database)
                            .parse("args", sqlConfig().args)
                            .parse()
                    );
                    props.put("hibernate.connection.username", sqlConfig().username);
                    props.put("hibernate.connection.password", sqlConfig().password);
                    props.put("hibernate.dialect ", "org.hibernate.dialect.MySQL5Dialect");
                    props.put("hibernate.current_session_context_class", "thread");
                    break;
                case SQLITE:
                    props.put("hibernate.connection.driver_class", "org.sqlite.JDBC");
                    props.put("hibernate.connection.url", new MessageBuilder("jdbc:sqlite:%file%/%database%")
                            .parse("file", dataFolder().getAbsolutePath())
                            .parse("database", sqlConfig().database)
                            .parse()
                    );
                    props.put("hibernate.connection.username", sqlConfig().username);
                    props.put("hibernate.connection.password", sqlConfig().password);
                    props.put("hibernate.dialect ", "org.hibernate.community.dialect.SQLiteDialect");
                    props.put("hibernate.current_session_context_class", "thread");
                    break;
            }

            props.putAll(sqlConfig().hibernateOptions);

            configuration.setProperties(props);

            for (Class<?> clazz : getClasses()) {
                configuration.addAnnotatedClass(clazz);
            }

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    protected abstract List<Class<?>> getClasses();


    public void save(DatabaseEntry entry) {
        executeInSession(session -> {
            if (entry.getID() != null) {
                session.merge(entry);
            } else {
                session.persist(entry);
            }
        });
    }

    private void executeInSession(ArgLambdaExecutor<Session> executor) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.getTransaction().setTimeout(main.getSqlConfig().sessionTimeout);
        executor.execute(session);
        session.getTransaction().commit();
    }

    public void delete(Object object) {
        executeInSession(session -> session.remove(object));
    }

    public <T> Query<T> get(Class<T> clazz) {
        return new Query<>(sqlConfig(), sessionFactory, clazz);
    }

    public <T> List<T> getAll(Class<T> clazz) {
        return get(clazz).execute();
    }

    public static class Query<T> {
        public Session session;
        public CriteriaBuilder builder;
        public CriteriaQuery<T> query;
        public Root<T> root;

        public Query(SQLConfig config, SessionFactory factory, Class<T> clazz) {
            this.session = factory.openSession();
            session.beginTransaction();
            session.getTransaction().setTimeout(config.sessionTimeout);

            this.builder = session.getCriteriaBuilder();
            this.query = builder.createQuery(clazz);
            this.root = query.from(clazz);
            this.query.select(root);
        }

        public List<T> execute() {
            List<T> output = session.createQuery(query).getResultList();
            session.getTransaction().commit();
            return output;
        }
    }

}
