package club.sk1er.motionblur.command;

import club.sk1er.motionblur.MotionBlur;
import club.sk1er.motionblur.config.BlurConfig;
import gg.essential.api.EssentialAPI;
import gg.essential.api.utils.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class MotionBlurCommand extends CommandBase {

    private final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Gets the name of the command
     */
    @Override
    public String getCommandName() {
        return "motionblur";
    }

    /**
     * Gets the usage string for the command.
     */
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    /**
     * Callback when the command is invoked
     */
    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        final MinecraftUtils minecraftUtil = EssentialAPI.getMinecraftUtil();
        if (args.length == 0) {
            minecraftUtil.sendMessage(EnumChatFormatting.RED + "[MotionBlur]", " Usage: /motionblur <0 - 7>");
        } else {
            int amount;
            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                minecraftUtil.sendMessage(
                    EnumChatFormatting.RED + "[MotionBlur]",
                    " You must provide a single number.");
                return;
            }

            if (amount >= 0 && amount <= 7) {
                if (MotionBlur.instance.isFastRenderEnabled()) {
                    minecraftUtil.sendMessage(
                        EnumChatFormatting.RED + "[MotionBlur]",
                        " Motion Blur is not compatible with OptiFine's Fast Render.");
                } else {
                    if (mc.entityRenderer.getShaderGroup() != null) {
                        mc.entityRenderer.getShaderGroup().deleteShaderGroup();
                    }

                    if (amount != 0) {
                        BlurConfig.motionBlur = true;
                        BlurConfig.blurAmount = amount;
                        mc.entityRenderer.loadShader(new ResourceLocation("motionblur", "motionblur"));
                        mc.entityRenderer.getShaderGroup().createBindFramebuffers(mc.displayWidth, mc.displayHeight);
                        minecraftUtil.sendMessage(
                            EnumChatFormatting.RED + "[MotionBlur]",
                            " Motion Blur enabled with amount " + amount + ".");
                    } else {
                        BlurConfig.motionBlur = false;
                        minecraftUtil.sendMessage(EnumChatFormatting.RED + "[MotionBlur]", " Motion Blur disabled.");
                    }

                    MotionBlur.instance.saveConfig();
                }
            } else {
                minecraftUtil.sendMessage(EnumChatFormatting.RED + "[MotionBlur]", " Invalid blur amount, 0 - 7.");
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
