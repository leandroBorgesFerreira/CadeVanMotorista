package br.com.simplepass.cadevanmotorista.utils.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.dto.PathShare;

/**
 * Created by leandro on 4/25/16.
 */
public class PathSerializer implements JsonSerializer<Path> {
    @Override
    public JsonElement serialize(Path src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("name", src.getName());
        jsonObject.addProperty("direction", src.getDirection());

        jsonObject.add("places", context.serialize(src.getPlaces()));

        return jsonObject;
    }
}
