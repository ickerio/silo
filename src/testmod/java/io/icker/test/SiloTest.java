package io.icker.test;

import io.icker.silo.Silo;
import net.fabricmc.api.ModInitializer;

public class SiloTest implements ModInitializer {
    @Override
    public void onInitialize() {
        Silo.dummy();
    }
}
