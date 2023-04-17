/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.api;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.customplugin.PlatformInterface;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.crop.CropConfig;
import net.momirealms.customcrops.api.object.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.pot.PotConfig;
import net.momirealms.customcrops.api.object.season.CCSeason;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.api.util.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class CustomCropsAPI {

    private static CustomCropsAPI instance;
    private final CustomCrops plugin;

    public CustomCropsAPI(CustomCrops plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static CustomCropsAPI getInstance() {
        return instance;
    }

    public boolean isCrop(String stage_id) {
        return plugin.getCropManager().getCropConfigByStage(stage_id) != null;
    }

    public CropConfig getCropConfig(String crop_config_id) {
        return plugin.getCropManager().getCropConfigByID(crop_config_id);
    }

    public ItemStack buildItem(String id) {
        return plugin.getIntegrationManager().build(id);
    }

    public boolean removeCustomItem(Location location, ItemMode itemMode) {
        if (itemMode == ItemMode.TRIPWIRE || itemMode == ItemMode.CHORUS)
            return plugin.getPlatformInterface().removeCustomBlock(location);
        else if (itemMode == ItemMode.ITEM_FRAME)
            return plugin.getPlatformInterface().removeItemFrame(location);
        else if (itemMode == ItemMode.ITEM_DISPLAY)
            return plugin.getPlatformInterface().removeItemDisplay(location);
        return false;
    }

    public void placeCustomItem(Location location, String id, ItemMode itemMode) {
        if (itemMode == ItemMode.TRIPWIRE)
            plugin.getPlatformInterface().placeTripWire(location, id);
        else if (itemMode == ItemMode.ITEM_FRAME)
            plugin.getPlatformInterface().placeItemFrame(location, id);
        else if (itemMode == ItemMode.ITEM_DISPLAY)
            plugin.getPlatformInterface().placeItemDisplay(location, id);
        else if (itemMode == ItemMode.CHORUS)
            plugin.getPlatformInterface().placeChorus(location, id);
    }

    public void changePotModel(SimpleLocation simpleLocation, Pot pot) {
        Location location = simpleLocation.getBukkitLocation();
        if (location == null) return;
        PlatformInterface platform = plugin.getPlatformInterface();
        if (platform.removeAnyBlock(location)) {
            String replacer = pot.isWet() ? pot.getConfig().getWetPot(pot.getFertilizer()) : pot.getConfig().getDryPot(pot.getFertilizer());
            if (ConfigUtils.isVanillaItem(replacer)) {
                location.getBlock().setType(Material.valueOf(replacer));
            } else {
                platform.placeNoteBlock(location, replacer);
            }
        } else {
            CustomCrops.getInstance().getWorldDataManager().removePotData(simpleLocation);
        }
    }

    public void changePotModel(SimpleLocation simpleLocation, PotConfig potConfig, Fertilizer fertilizer, boolean wet) {
        Location location = simpleLocation.getBukkitLocation();
        if (location == null) return;
        PlatformInterface platform = plugin.getPlatformInterface();
        if (platform.removeAnyBlock(location)) {
            String replacer = wet ? potConfig.getWetPot(fertilizer) : potConfig.getDryPot(fertilizer);
            if (ConfigUtils.isVanillaItem(replacer)) {
                location.getBlock().setType(Material.valueOf(replacer));
            } else {
                platform.placeNoteBlock(location, replacer);
            }
        } else {
            CustomCrops.getInstance().getWorldDataManager().removePotData(simpleLocation);
        }
    }

    public boolean isGreenhouse(SimpleLocation simpleLocation) {
        return plugin.getWorldDataManager().isGreenhouse(simpleLocation);
    }

    public CCSeason getCurrentSeason(String world) {
        return plugin.getIntegrationManager().getSeasonInterface().getSeason(world);
    }
}