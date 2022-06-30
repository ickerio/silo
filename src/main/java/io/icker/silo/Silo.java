package io.icker.silo;

import java.io.File;
import java.io.IOException;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;

public class Silo {
    public static <T> T fromNbt(Class<T> clazz, NbtCompound compound) throws IOException, ReflectiveOperationException {
        return Serializer.deserialize(clazz, compound);
    }

    public static <T> T fromNbt(Class<T> clazz, File file) throws IOException, ReflectiveOperationException {
        NbtCompound compound = NbtIo.readCompressed(file);
        return Serializer.deserialize(clazz, compound);
    }

    public static <T> NbtElement toNbt(Class<T> clazz, T item) throws IOException, ReflectiveOperationException {
        return Serializer.serialize(clazz, item);
    }

    public static <T> void toNbt(Class<T> clazz, T item, File file) throws IOException, ReflectiveOperationException {
        NbtElement element = Serializer.serialize(clazz, item);
        if (element instanceof NbtCompound) {
            NbtIo.writeCompressed((NbtCompound) element, file);
        }
    }
}
