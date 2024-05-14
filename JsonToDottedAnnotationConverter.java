package convertor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Map;

public class JsonToDottedAnnotationConverter {

    public static void main(String[] args) throws Exception {
        // Your JSON string
        String jsonString = "{ \"person\": { \"name\": \"John\", \"age\": 30, \"hobbies\": [\"Reading\", \"Gaming\"] } }";

        // Convert JSON string to Jackson JsonNode
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        // Convert JsonNode to a map with dotted annotation key-value pairs
        Map<String, Object> annotationMap = new HashMap<>();
        convertToDottedAnnotation(jsonNode, "", annotationMap);
        System.out.println(annotationMap);
    }

    private static void convertToDottedAnnotation(JsonNode jsonNode, String parent, Map<String, Object> annotationMap) {
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            objectNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                String currentPath = parent.isEmpty() ? capitalizeFirstLetter(key) : parent + "[" + (0 + 1) + "]" + "." + capitalizeFirstLetter(key);

                if (value.isArray()) {
                    convertArrayToDottedAnnotation((ArrayNode) value, currentPath, annotationMap);
                } else if (value.isObject()) {
                    convertToDottedAnnotation(value, currentPath, annotationMap);
                } else {
                    annotationMap.put(currentPath, value.asText());
                }
            });
        }
    }

    private static void convertArrayToDottedAnnotation(ArrayNode arrayNode, String parent, Map<String, Object> annotationMap) {
        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode item = arrayNode.get(i);
            String currentPath = parent + "[" + (i + 1) + "]";

            if (item.isArray()) {
                convertArrayToDottedAnnotation((ArrayNode) item, currentPath, annotationMap);
            } else if (item.isObject()) {
                convertToDottedAnnotation(item, currentPath, annotationMap);
            } else {
                annotationMap.put(currentPath, item.asText());
            }
        }
    }

    private static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
