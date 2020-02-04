package com.github.iskrenyp.core.api.config

trait IConfigurableEntity {

    Map<String, ?> toMap() {
        this.class.declaredFields.findAll { !it.synthetic }.collectEntries {
            [ (it.name):this."$it.name" ]
        }
    }

}