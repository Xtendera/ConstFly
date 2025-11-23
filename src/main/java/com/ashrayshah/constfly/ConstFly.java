package com.ashrayshah.constfly;

import com.ashrayshah.constfly.modules.ElytraFlyPlus;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class ConstFly extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("ConstFly");

    @Override
    public void onInitialize() {
        LOG.info("Initializing ConstFly...");

        // Modules
        Modules.get().add(new ElytraFlyPlus());

    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.ashrayshah.constfly";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("Xtendera", "ConstFly");
    }
}
