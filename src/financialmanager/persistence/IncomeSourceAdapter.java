package financialmanager.persistence;

import com.google.gson.*;
import financialmanager.model.enums.IncomeSource;
import java.lang.reflect.Type;

public class IncomeSourceAdapter implements JsonSerializer<IncomeSource>, JsonDeserializer<IncomeSource> {
    @Override
    public JsonElement serialize(IncomeSource src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.name());
    }

    @Override
    public IncomeSource deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        String value = json.getAsString();
        try {
            return IncomeSource.valueOf(value);
        } catch (Exception e) {
            return IncomeSource.OTHER; // значение по умолчанию
        }
    }
}