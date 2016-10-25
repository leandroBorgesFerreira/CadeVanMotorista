package br.com.simplepass.cadevanmotorista.utils.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import br.com.simplepass.cadevanmotorista.dto.PathShare;

/**
 * Created by leandro on 4/25/16.
 */
public class PathShareSerializer implements JsonSerializer<PathShare> {
    @Override
    public JsonElement serialize(PathShare src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("driverName", src.getDriverName());
        jsonObject.add("path", context.serialize(src.getPath()));
        return jsonObject;
    }
}
