package rip.bolt.jsonrest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify the name of the field in the JSON object (e.g discordId in Java vs id in JSON)
 * 
 * @author dentmaged
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyField {

    public String name();

}
