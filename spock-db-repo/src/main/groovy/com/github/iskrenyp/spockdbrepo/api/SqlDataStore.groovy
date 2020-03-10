package com.github.iskrenyp.spockdbrepo.api

import com.github.iskrenyp.core.api.config.IConfigFileRuler
import com.github.iskrenyp.spockdbrepo.driver.IDataSourceRuler
import com.github.iskrenyp.spockdbrepo.exception.DbRepoException
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import static com.github.iskrenyp.core.api.constants.CommonFileUtils.CURRENT_USER_DIR_FILE_PATH

@Slf4j
class SqlDataStore implements IDataSourceRuler, IConfigFileRuler {

    static final String CONFIG_FILE_NAME = 'SqlDataSourceConfig.groovy'

    String repoName
    SqlDataSourceConfigObj config

    SqlDataStore(String repoName) {
        this.repoName = repoName
        this.config = prepareConfig(repoName)
    }

    private  <T> T runSql(SqlDataSourceConfigObj config, Closure<T> consumeSql) throws DbRepoException {
        try {
            def response = connect(config).perform(consumeSql)
            disconnect()
            return response
        } catch(Exception e) {
            log.error("There was an error while running SQL statement against $repoName with $config.driver}")
            throw new DbRepoException(e)
        }
    }

    private SqlDataSourceConfigObj prepareConfig(String repoName) throws DbRepoException {
        try {
            findConfigFile(CONFIG_FILE_NAME, CURRENT_USER_DIR_FILE_PATH).withEntry(repoName) {
                assert it.url && it.username && it.password && it.driver: log.error("You need to provide url, username, password and driver for your $repoName repository")
                new SqlDataSourceConfigObj(
                        url: it.url,
                        username: it.username,
                        password: it.password,
                        driver: it.driver)
            }
        } catch (Exception e) { throw new DbRepoException(e) }
    }

    List select(String selectStatement) throws DbRepoException {
        log.info("Executing select: $selectStatement")
        runSql(config) { Sql sql -> sql.rows(selectStatement) }
    }

    def <T extends IValidatableEntity> List<T> select(String selectStatement, Closure<T> defineEntityCl) throws DbRepoException {
        log.info("Executing select with collection mapping: $selectStatement")
        List response = runSql(config) { Sql sql -> sql.rows(selectStatement) }
        if (!response || response.empty) throw new DbRepoException("Response from ${config.url} was either null, or empty")
        List<T> entities = []
        response.each { entities << defineEntityCl(it) }
        return entities
    }

    Boolean execute(String sqlStatement) throws DbRepoException {
        log.info("Executing statement: $sqlStatement")
        runSql(config) { Sql sql -> sql.execute(sqlStatement) }
    }

    Integer executeUpdate(String sqlStatement) throws DbRepoException {
        log.info("Executing update: $sqlStatement")
        runSql(config) { Sql sql -> sql.executeUpdate(sqlStatement) }
    }

    def call(String statement, List params=null, Closure closure=null) throws DbRepoException {
        log.info("Calling $statement with params $params")
        runSql(config) { Sql sql ->
            if (params && !closure) sql.call(statement, params)
            else if (params && closure) sql.call(statement, params, closure)
            else sql.call(statement)
        }
    }

}
