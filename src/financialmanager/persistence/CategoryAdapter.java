package financialmanager.persistence;

import com.google.gson.*;
import financialmanager.model.entities.Category;

import java.lang.reflect.Type;

public class CategoryAdapter implements JsonSerializer<Category>, JsonDeserializer<Category> {

    @Override
    public JsonElement serialize(Category category, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", category.getId());
        jsonObject.addProperty("name", category.getName());
        jsonObject.addProperty("description", category.getDescription());
        jsonObject.addProperty("colorCode", category.getColorCode());
        return jsonObject;
    }

    @Override
    public Category deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        return new Category(
                jsonObject.get("id").getAsString(),
                jsonObject.get("name").getAsString(),
                jsonObject.get("description").getAsString(),
                jsonObject.get("colorCode").getAsString()
        );
    }
}