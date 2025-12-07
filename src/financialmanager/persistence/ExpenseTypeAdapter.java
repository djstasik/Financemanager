package financialmanager.persistence;

import com.google.gson.*;
import financialmanager.model.enums.ExpenseType;
import java.lang.reflect.Type;

public class ExpenseTypeAdapter implements JsonSerializer<ExpenseType>, JsonDeserializer<ExpenseType> {
    @Override
    public JsonElement serialize(ExpenseType src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.name());
    }

    @Override
    public ExpenseType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        String value = json.getAsString();
        try {
            return ExpenseType.valueOf(value);
        } catch (Exception e) {
            return ExpenseType.VARIABLE; // значение по умолчанию
        }
    }
}