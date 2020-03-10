package com.github.iskrenyp.spockdbrepo.driver

import com.github.iskrenyp.spockdbrepo.api.SqlDataSourceConfigObj
import com.github.iskrenyp.spockdbrepo.exception.DbRepoException
import groovy.sql.Sql
import groovy.util.logging.Slf4j

import java.sql.SQLException

@Slf4j
trait IDataSourceRuler {

    Sql sql

    IDataSourceRuler connect(SqlDataSourceConfigObj config) throws DbRepoException {
        log.info("Starting connection to $config.url")
        try {
            this.sql = Sql.newInstance(config.url, config.username, config.password, config.driver)
            return this
        } catch(ClassNotFoundException | SQLException e) {
            throw new DbRepoException("There was an error while connecting to $config.url", e)
        }
    }

    def disconnect() {
        if (this.sql) {
            log.info("Closing db connection")
            this.sql.close()
        }
    }

    def <T> T perform(Closure<T> consumeSql) throws Exception {
        this.sql.with consumeSql
    }



}