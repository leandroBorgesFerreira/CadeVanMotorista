package br.com.simplepass.cadevanmotorista.utils.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;

/**
 * Created by leandro on 4/25/16.
 */
public class PlaceSerializer implements JsonSerializer<Place> {
    @Override
    public JsonElement serialize(Place src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("latitude", src.getLatitude());
        jsonObject.addProperty("longitude", src.getLongitude());

        jsonObject.add("entitysInsidePlace", context.serialize(src.getEntitysInsidePlace()));

        return jsonObject;
    }
}
