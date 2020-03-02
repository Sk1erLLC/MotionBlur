package club.sk1er.motionblur.resource;

import club.sk1er.motionblur.config.BlurConfig;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Locale;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class MotionBlurResource implements IResource {

  private static final String JSON =
      "{\"targets\":[\"swap\",\"previous\"],\"passes\":[{\"name\":\"phosphor\",\"intarget\":\"minecraft:main\",\"outtarget\":\"swap\",\"auxtargets\":[{\"name\":\"PrevSampler\",\"id\":\"previous\"}],\"uniforms\":[{\"name\":\"Phosphor\",\"values\":[%.2f, %.2f, %.2f]}]},{\"name\":\"blit\",\"intarget\":\"swap\",\"outtarget\":\"previous\"},{\"name\":\"blit\",\"intarget\":\"swap\",\"outtarget\":\"minecraft:main\"}]}";

  @Override
  public ResourceLocation getResourceLocation() {
    return null;
  }

  @Override
  public InputStream getInputStream() {
    double amount = 0.7 + BlurConfig.blurAmount / 100.0 * 3.0 - 0.01;
    return IOUtils.toInputStream(
        String.format(Locale.ENGLISH, JSON, amount, amount, amount), Charset.defaultCharset());
  }

  @Override
  public boolean hasMetadata() {
    return false;
  }

  @Override
  public <T extends IMetadataSection> T getMetadata(String p_110526_1_) {
    return null;
  }

  @Override
  public String getResourcePackName() {
    return null;
  }
}
