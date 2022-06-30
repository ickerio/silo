package io.icker.silo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.icker.silo.annotations.Name;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class Serializer {
    private static final HashMap<Class<?>, HashMap<String, Field>> cache = new HashMap<Class<?>, HashMap<String, Field>>();

    public static <T> T deserialize(Class<T> clazz, NbtElement value) throws IOException, ReflectiveOperationException {
        if (SerializerRegistry.contains(clazz)) {
            return SerializerRegistry.fromNbtElement(clazz, value);
        }

        NbtCompound compound = (NbtCompound) value;
        T item = (T) clazz.getDeclaredConstructor().newInstance();

        HashMap<String, Field> fields = cache.get(clazz);
        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            String key = entry.getKey();
            Field field = entry.getValue();

            if (!compound.contains(key)) continue;

            Class<?> type = field.getType();

            if (clazz.isArray()) {
                field.set(item, deserializeList(genericType, (NbtList) compound.get(key)));
            } else if (ArrayList.class.isAssignableFrom(type)) {
                Class<?> genericType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                field.set(item, deserializeList(genericType, (NbtList) compound.get(key)));
            } else {
                field.set(item, deserialize(type, compound.get(key)));
            }
        }

        return item;
    }

    public static <T> ArrayList<T> deserializeList(Class<T> clazz, NbtList list) throws IOException, ReflectiveOperationException {
        ArrayList<T> store = new ArrayList<T>();

        for (int i = 0; i < list.size(); i++) {
            store.add(deserialize(clazz, list.get(i)));
        }

        return store;
    }


    public static <T> NbtElement serialize(Class<T> clazz, T item) throws IOException, ReflectiveOperationException {
        if (SerializerRegistry.contains(clazz)) {
            return SerializerRegistry.toNbtElement(clazz, item);
        }

        HashMap<String, Field> fields = cache.get(clazz);
        NbtCompound compound = new NbtCompound();
        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            String key = entry.getKey();
            Field field = entry.getValue();

            Class<?> type = field.getType();
            Object data = field.get(item);

            if (data == null) continue;

            if (clazz.isArray()) {
                compound.put(key, serializeArray(clazz, cast(item)));
            } else if (ArrayList.class.isAssignableFrom(type)) {
                Class<?> genericType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                compound.put(key, serializeList(genericType, cast(data)));
            } else {
                compound.put(key, serialize(type, cast(data)));
            }
        }

        return compound;
    }

    public static <T> NbtList serializeArray(Class<T> clazz, T[] items) throws IOException, ReflectiveOperationException {
        NbtList list = new NbtList();

        for (T item : items) {
            list.add(list.size(), serialize(clazz, item));
        }

        return list;
    }

    public static <T> NbtList serializeList(Class<T> clazz, List<T> items) throws IOException, ReflectiveOperationException {
        NbtList list = new NbtList();

        for (T item : items) {
            list.add(list.size(), serialize(clazz, item));
        }

        return list;
    }

    public static <T> void setup(Class<T> clazz) {
        HashMap<String, Field> fields = new HashMap<String, Field>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Name.class)) {
                field.setAccessible(true);
                fields.put(field.getAnnotation(Name.class).value(), field);

                Class<?> type = field.getType();
                if (!SerializerRegistry.contains(type)) {
                    if (ArrayList.class.isAssignableFrom(type)) {
                        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                        setup((Class<?>) genericType.getActualTypeArguments()[0]);
                    } else {
                        setup(type);
                    }
                }
            }
        }

        cache.put(clazz, fields);
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object key) {
        return (T) key;
    }
}
