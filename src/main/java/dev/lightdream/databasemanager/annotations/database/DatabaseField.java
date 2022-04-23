package dev.lightdream.databasemanager.annotations.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DatabaseField {

    String columnName();

    boolean autoGenerate() default false;

    boolean unique() default false;

    boolean primaryKey() default false;

}
