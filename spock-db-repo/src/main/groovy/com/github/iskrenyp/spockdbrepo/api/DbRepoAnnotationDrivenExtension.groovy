package com.github.iskrenyp.spockdbrepo.api


import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.extension.ExtensionException
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FieldInfo
import org.spockframework.runtime.model.SpecInfo

class DbRepoAnnotationDrivenExtension extends AbstractAnnotationDrivenExtension<Repo> {

    @Override
    void visitFieldAnnotation(Repo annotation, FieldInfo fieldInfo) {
        if (!fieldInfo.shared) throw new ExtensionException("All fields with @Repo should be declared as @Shared as well.")
        SpecInfo specInfo = fieldInfo.parent
        specInfo.addSetupSpecInterceptor(new DbRepoInterceptor(fieldInfo, annotation.name()))
    }

    private static class DbRepoInterceptor implements IMethodInterceptor {

        FieldInfo field
        String repoName

        DbRepoInterceptor(FieldInfo field, String repoName) {
            this.field = field
            this.repoName = repoName
        }

        @Override
        void intercept(IMethodInvocation invocation) throws Throwable {
            field.writeValue(invocation.instance, new SqlDataStore(repoName))
        }
    }
}


