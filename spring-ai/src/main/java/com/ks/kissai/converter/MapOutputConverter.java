package com.ks.kissai.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.converter.AbstractMessageOutputConverter;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ResponseTextCleaner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class MapOutputConverter<T> extends AbstractMessageOutputConverter<Map<String, T>> {

    private static final String FORMAT_TEMPLATE = """
            Your response should be in JSON format.
            Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
            Do not include markdown code blocks in your response.
            Remove the ```json markdown from the output.
            The root JSON value must be an object used as a Java Map<String, %s>.
            Every top-level property name must be the "%s" field value from the corresponding object.
            Every top-level property value must match the schema in additionalProperties.
            The "%s" field should still be present inside each top-level property value.
            Do not wrap the result in array fields such as "items", "list", "fighters", "data", or "results".
            Do not return a top-level array.
            Here is the JSON Schema instance your output must adhere to:
            ```%s```
            """;

    private final ParameterizedTypeReference<Map<String, T>> mapTypeReference;

    private final ParameterizedTypeReference<T> valueTypeReference;

    private final ObjectMapper objectMapper;

    private final String mapKeyFieldName;

    private final BeanOutputConverter<Map<String, T>> delegate;

    private final String jsonSchema;

    public MapOutputConverter(Class<T> valueType) {
        this(new MappingJackson2MessageConverter(), createMapTypeReference(valueType), null, null);
    }

    private MapOutputConverter(MessageConverter messageConverter,
                               ParameterizedTypeReference<Map<String, T>> mapTypeReference,
                               ObjectMapper objectMapper,
                               ResponseTextCleaner textCleaner) {
        super(messageConverter);
        Assert.notNull(messageConverter, "messageConverter must not be null");
        Assert.notNull(mapTypeReference, "mapTypeReference must not be null");
        this.mapTypeReference = mapTypeReference;
        this.valueTypeReference = extractValueTypeReference(mapTypeReference);
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
        this.mapKeyFieldName = resolveMapKeyFieldName(this.valueTypeReference);
        this.delegate = createBeanOutputConverter(mapTypeReference, objectMapper, textCleaner);
        this.jsonSchema = generateJsonSchema(objectMapper, textCleaner);
    }

    @Override
    public Map<String, T> convert(@NotNull String source) {
        return this.delegate.convert(source);
    }

    @NotNull
    @Override
    public String getFormat() {
        return String.format(FORMAT_TEMPLATE, this.valueTypeReference.getType().getTypeName(), this.mapKeyFieldName,
                this.mapKeyFieldName, this.jsonSchema);
    }

    private String generateJsonSchema(ObjectMapper objectMapper, ResponseTextCleaner textCleaner) {
        try {
            BeanOutputConverter<T> valueConverter = createBeanOutputConverter(this.valueTypeReference, objectMapper,
                    textCleaner);
            JsonNode valueSchema = this.objectMapper.readTree(valueConverter.getJsonSchema());
            if (valueSchema.isObject()) {
                ((com.fasterxml.jackson.databind.node.ObjectNode) valueSchema).remove("$schema");
            }
            JsonNode mapSchema = this.objectMapper.createObjectNode()
                    .put("$schema", "https://json-schema.org/draft/2020-12/schema")
                    .put("type", "object")
                    .put("description",
                            "A JSON object whose property names are map keys and property values match the schema in additionalProperties.")
                    .set("additionalProperties", valueSchema);
            return this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapSchema);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not generate JSON Schema for " + this.mapTypeReference.getType(), e);
        }
    }

    private static <V> BeanOutputConverter<V> createBeanOutputConverter(
            ParameterizedTypeReference<V> typeReference, ObjectMapper objectMapper,
            ResponseTextCleaner textCleaner) {
        if (textCleaner != null) {
            return new BeanOutputConverter<>(typeReference, objectMapper, textCleaner);
        }
        if (objectMapper != null) {
            return new BeanOutputConverter<>(typeReference, objectMapper);
        }
        return new BeanOutputConverter<>(typeReference);
    }

    private static <V> ParameterizedTypeReference<Map<String, V>> createMapTypeReference(Class<V> valueType) {
        Assert.notNull(valueType, "valueType must not be null");
        return createMapTypeReference(ResolvableType.forClass(valueType).getType());
    }

    private static <V> ParameterizedTypeReference<Map<String, V>> createMapTypeReference(Type valueType) {
        Type mapType = ResolvableType
                .forClassWithGenerics(Map.class, ResolvableType.forClass(String.class), ResolvableType.forType(valueType))
                .getType();
        return ParameterizedTypeReference.forType(mapType);
    }

    private static <V> ParameterizedTypeReference<V> extractValueTypeReference(
            ParameterizedTypeReference<Map<String, V>> mapTypeReference) {
        ResolvableType mapType = ResolvableType.forType(mapTypeReference.getType());
        ResolvableType keyType = mapType.getGeneric(0);
        ResolvableType valueType = mapType.getGeneric(1);
        Assert.isTrue(String.class.equals(keyType.resolve()), "mapTypeReference key type must be String");
        Assert.notNull(valueType.getType(), "mapTypeReference value type must not be null");
        return ParameterizedTypeReference.forType(valueType.getType());
    }

    private static String resolveMapKeyFieldName(ParameterizedTypeReference<?> valueTypeReference) {
        Class<?> valueType = ResolvableType.forType(valueTypeReference.getType()).resolve();
        Assert.notNull(valueType, "value type must be resolvable to a Class");
        return findAnnotatedRecordComponent(valueType)
                .or(() -> findAnnotatedField(valueType))
                .or(() -> findFirstRecordComponent(valueType))
                .or(() -> findFirstField(valueType))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Could not resolve map key field from " + valueType.getName()));
    }

    private static Optional<String> findAnnotatedRecordComponent(Class<?> valueType) {
        if (!valueType.isRecord()) {
            return Optional.empty();
        }
        return Arrays.stream(valueType.getRecordComponents())
                .filter(recordComponent -> recordComponent.isAnnotationPresent(MapKeyField.class)
                        || recordComponent.getAccessor().isAnnotationPresent(MapKeyField.class))
                .map(RecordComponent::getName)
                .findFirst();
    }

    private static Optional<String> findAnnotatedField(Class<?> valueType) {
        return Arrays.stream(valueType.getDeclaredFields())
                .filter(field -> !field.isSynthetic())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> field.isAnnotationPresent(MapKeyField.class))
                .sorted(Comparator.comparing(Field::getName))
                .map(Field::getName)
                .findFirst();
    }

    private static Optional<String> findFirstRecordComponent(Class<?> valueType) {
        if (!valueType.isRecord() || valueType.getRecordComponents().length == 0) {
            return Optional.empty();
        }
        return Optional.of(valueType.getRecordComponents()[0].getName());
    }

    private static Optional<String> findFirstField(Class<?> valueType) {
        return Arrays.stream(valueType.getDeclaredFields())
                .filter(field -> !field.isSynthetic())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .findFirst()
                .map(Field::getName);
    }
}
