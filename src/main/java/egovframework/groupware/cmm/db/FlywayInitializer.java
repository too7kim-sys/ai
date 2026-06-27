package egovframework.groupware.cmm.db;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

/**
 * FluentConfiguration의 오버로드 셋터 때문에 Spring XML로 직접 구성이 어려워
 * 자바로 감싸 초기화한다.
 */
public class FlywayInitializer {

    private final DataSource dataSource;
    private final String locations;

    public FlywayInitializer(DataSource dataSource, String locations) {
        this.dataSource = dataSource;
        this.locations = locations;
    }

    public void migrate() {
        Flyway.configure()
                .dataSource(dataSource)
                .locations(locations.split(","))
                .baselineOnMigrate(true)
                .load()
                .migrate();
    }
}
