package club.sk1er.motionblur;

import club.sk1er.motionblur.command.MotionBlurCommand;
import club.sk1er.motionblur.config.BlurConfig;
import club.sk1er.motionblur.resource.MotionBlurResourceManager;
import gg.essential.api.EssentialAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

@Mod(name = "Motion Blur", modid = "motionblurmod", version = "2.1.2")
public class MotionBlur {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Map<String, FallbackResourceManager> domainResourceManagers = ((SimpleReloadableResourceManager) mc.getResourceManager()).domainResourceManagers;
    private Field cachedFastRender;
    private int ticks;

    @Mod.Instance("motionblurmod")
    public static MotionBlur instance;

    private File configFile;

    public MotionBlur() {
        try {
            //noinspection JavaReflectionMemberAccess
            cachedFastRender = GameSettings.class.getDeclaredField("ofFastRender");
        } catch (Exception ignored) {
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        configFile = new File("./config/motionblur.cfg");
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new MotionBlurCommand());

        loadConfig();
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (domainResourceManagers != null) {
            if (!domainResourceManagers.containsKey("motionblur")) {
                domainResourceManagers.put("motionblur", new MotionBlurResourceManager(mc.metadataSerializer_));
            }
        }

        ++ticks;
        if (ticks % 5000 == 0) {
            if (isFastRenderEnabled() && BlurConfig.motionBlur) {
                if (mc.thePlayer != null && mc.theWorld != null) {
                    EssentialAPI.getMinecraftUtil().sendMessage(
                        EnumChatFormatting.RED + "[MotionBlur]",
                        " Motion Blur is not compatible with OptiFine's Fast Render.");
                }
            }
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (BlurConfig.motionBlur && mc.thePlayer != null && GameSettings.isKeyDown(mc.gameSettings.keyBindTogglePerspective)) {
            EntityRenderer entityRenderer = mc.entityRenderer;
            if (entityRenderer == null || entityRenderer.isShaderActive()) return;
            entityRenderer.loadShader(new ResourceLocation("motionblur", "motionblur"));
        }
    }

    public boolean isFastRenderEnabled() {
        try {
            return cachedFastRender.getBoolean(mc.gameSettings);
        } catch (Exception ignored) {
            return false;
        }
    }

    public void saveConfig() {
        Configuration configuration = new Configuration(configFile);
        updateConfig(configuration, false);
        configuration.save();
    }

    private void loadConfig() {
        Configuration configuration = new Configuration(configFile);
        configuration.load();
        updateConfig(configuration, true);
        configuration.save();
    }

    private void updateConfig(Configuration config, boolean load) {
        Property property = config.get("general", "enabled", false);
        if (load) {
            BlurConfig.motionBlur = property.getBoolean();
        } else {
            property.set(BlurConfig.motionBlur);
        }

        property = config.get("general", "amount", 0);
        if (load) {
            BlurConfig.blurAmount = property.getDouble();
        } else {
            property.set(BlurConfig.blurAmount);
        }
    }
}
