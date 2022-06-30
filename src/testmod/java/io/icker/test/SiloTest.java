package io.icker.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import io.icker.silo.Silo;
import io.icker.silo.annotations.Name;
import net.fabricmc.api.ModInitializer;

public class SiloTest implements ModInitializer {
    @Override
    public void onInitialize() {

        try {
            Data data = new Data();
            data.testByte = Byte.MAX_VALUE;
            data.testShort = Short.MAX_VALUE;
            data.testInt = Integer.MAX_VALUE;
            data.testLong = Long.MAX_VALUE;
            data.testFloat = Float.MAX_VALUE;
            data.testBoolean = true;
            data.testByteArray = "test byte array".getBytes();
            data.testIntArray = new int[]{ 1, 2, 3, 4, 5 };
            data.testLongArray = new long[]{ 6, 7, 8, 9, 10 };
            data.testString = "test string!";
            data.testUUID = UUID.randomUUID();
            data.testArray = new String[]{ "here", "is", "a", "string" };
            data.testArrayList = new ArrayList<String>() {
                {
                    add("some");
                    add("test");
                    add("data");
                }
            };

            io.icker.silo.Serializer.setup(Data.class);
            Silo.toNbt(Data.class, data, new File("data.dat"));

        } catch (IOException | ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public class Data {
        @Name("testByte")
        public byte testByte;
        
        @Name("testShort")
        public short testShort;

        @Name("testInt")
        public int testInt;

        @Name("testLong")
        public long testLong;

        @Name("testFloat")
        public float testFloat;

        @Name("testBoolean")
        public boolean testBoolean;

        @Name("testByteArray")
        public byte[] testByteArray;

        @Name("testIntArray")
        public int[] testIntArray;

        @Name("testLongArray")
        public long[] testLongArray;

        @Name("testString")
        public String testString;

        @Name("testUUID")
        public UUID testUUID;

        @Name("testArray")
        public String[] testArray;

        @Name("testArrayList")
        public ArrayList<String> testArrayList;
    }
}
