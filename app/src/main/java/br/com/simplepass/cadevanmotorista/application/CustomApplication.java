package br.com.simplepass.cadevanmotorista.application;

import android.app.Application;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;


/**
 * Application costumizado. Feito para ser utilizado o Realm
 */
public class CustomApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name("realm-br.com.cadevanmotorista-db.realm")
                .schemaVersion(19L)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                        RealmSchema schema = realm.getSchema();

                        if(oldVersion <= 19){
                            /* Colocar alterações que devem ser feitas para a versão 20 */
                        }
                    }
                })
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
