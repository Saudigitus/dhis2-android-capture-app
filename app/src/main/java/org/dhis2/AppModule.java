package org.dhis2;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.hilt.work.HiltWorkerFactory;

import org.apache.commons.jexl2.JexlEngine;
import org.dhis2.commons.di.dagger.PerUser;
import org.dhis2.utils.ExpressionEvaluatorImpl;
import org.dhis2.commons.resources.ResourceManager;
import org.hisp.dhis.rules.RuleExpressionEvaluator;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final App application;


    public AppModule(@NonNull App application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context context() {
        return application;
    }

    @Provides
    @Singleton
    HiltWorkerFactory workerFactory() {
        return application.workerFactory;
    }

    @Provides
    @Singleton
    JexlEngine jexlEngine() {
        return new JexlEngine();
    }

    @Provides
    @Singleton
    RuleExpressionEvaluator ruleExpressionEvaluator(@NonNull JexlEngine jexlEngine) {
        return new ExpressionEvaluatorImpl(jexlEngine);
    }

    @Provides
    @Singleton
    ResourceManager resources() {
        return new ResourceManager(application);
    }
}
