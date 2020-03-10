package com.github.iskrenyp.spockdbrepo.api

import com.github.iskrenyp.core.api.config.IConfigurableEntity
import groovy.transform.TupleConstructor

@TupleConstructor
class SqlDataSourceConfigObj implements IConfigurableEntity {

    String url
    String username
    String password
    String driver
}
