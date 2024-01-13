package me.ricky.banana.systems;

import me.ricky.banana.BananaPlus;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3d;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BananaSystem extends System<BananaSystem> {
    public final Settings settings = new Settings();

    private final SettingGroup sgPrefix = settings.createGroup("Prefix");
    private final SettingGroup sgText = settings.createGroup("3D Text");
    private final SettingGroup sgServer = settings.createGroup("Server");
    private final SettingGroup sgBlocks = settings.createGroup("Blocks");
    private final SettingGroup sgEntities = settings.createGroup("Entities");

    // Prefix

    public final Setting<String> prefix = sgPrefix.add(new StringSetting.Builder()
        .name("banana+-prefix")
        .description("What prefix to use for Banana+ modules.")
        .defaultValue("Banana+")
        .onChanged(cope -> ChatUtils.registerCustomPrefix("me.ricky.banana", this::getPrefix))
        .build()
    );

    public final Setting<SettingColor> prefixColor = sgPrefix.add(new ColorSetting.Builder()
        .name("prefix-color")
        .description("Color display for the prefix.")
        .defaultValue(new SettingColor(255,193,0,255))
        .onChanged(cope -> ChatUtils.registerCustomPrefix("me.ricky.banana", this::getPrefix))
        .build()
    );

    public final Setting<Format> prefixFormat = sgPrefix.add(new EnumSetting.Builder<Format>()
        .name("prefix-format")
        .description("What type of minecraft formatting should be applied to the prefix.")
        .defaultValue(Format.Normal)
        .onChanged(cope -> ChatUtils.registerCustomPrefix("me.ricky.banana", this::getPrefix))
        .build()
    );

    public final Setting<Boolean> formatBrackets = sgPrefix.add(new BoolSetting.Builder()
        .name("format-brackets")
        .description("Whether the formatting should apply to the brackets as well.")
        .visible(() -> prefixFormat.get() != Format.Normal)
        .onChanged(cope -> ChatUtils.registerCustomPrefix("me.ricky.banana", this::getPrefix))
        .defaultValue(true)
        .build()
    );

    public final Setting<String> leftBracket = sgPrefix.add(new StringSetting.Builder()
        .name("left-bracket")
        .description("What to be displayed as left bracket for the prefix.")
        .defaultValue("[")
        .onChanged(cope -> ChatUtils.registerCustomPrefix("me.ricky.banana", this::getPrefix))
        .build()
    );

    public final Setting<String> rightBracket = sgPrefix.add(new StringSetting.Builder()
        .name("right-bracket")
        .description("What to be displayed as right bracket for the prefix.")
        .defaultValue("]")
        .onChanged(cope -> ChatUtils.registerCustomPrefix("me.ricky.banana", this::getPrefix))
        .build()
    );

    public final Setting<SettingColor> leftColor = sgPrefix.add(new ColorSetting.Builder()
        .name("left-color")
        .description("Color display for the left bracket.")
        .defaultValue(new SettingColor(150,150,150,255))
        .onChanged(cope -> ChatUtils.registerCustomPrefix("me.ricky.banana", this::getPrefix))
        .build()
    );

    public final Setting<SettingColor> rightColor = sgPrefix.add(new ColorSetting.Builder()
        .name("right-color")
        .description("Color display for the right bracket.")
        .defaultValue(new SettingColor(150,150,150,255))
        .onChanged(cope -> ChatUtils.registerCustomPrefix("me.ricky.banana", this::getPrefix))
        .build()
    );

    // Text

    public final Setting<Double> textScale = sgText.add(new DoubleSetting.Builder()
        .name("scale")
        .description("The base scale of the text.")
        .defaultValue(1)
        .range(1,5)
        .build()
    );

    public final Setting<Double> divisor = sgText.add(new DoubleSetting.Builder()
        .name("divisor")
        .description("How strongly distance should affect text size.")
        .defaultValue(6)
        .range(1,10)
        .build()
    );

    public final Setting<Double> minScale = sgText.add(new DoubleSetting.Builder()
        .name("min-scale")
        .description("The smallest text can get, regardless of distance.")
        .defaultValue(0.5)
        .range(0.1,5)
        .build()
    );

    public final Setting<Double> maxScale = sgText.add(new DoubleSetting.Builder()
        .name("max-scale")
        .description("The largest text can get, regardless of distance.")
        .defaultValue(1.7)
        .range(0.1,5)
        .build()
    );

    public BananaSystem() {
        super("banana+");
        ChatUtils.registerCustomPrefix("me.ricky.banana", this::getPrefix);
    }

    public static BananaSystem get() {
        return Systems.get(BananaSystem.class);
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.putString("version", BananaPlus.VERSION.toString());
        tag.put("settings", settings.toTag());

        return tag;
    }

    @Override
    public BananaSystem fromTag(NbtCompound tag) {
        if (tag.contains("settings")) settings.fromTag(tag.getCompound("settings"));
        return this;
    }

    // Utils

    public Text getPrefix() {
        MutableText logo = Text.literal(prefix.get());
        MutableText left = Text.literal(leftBracket.get());
        MutableText right = Text.literal(rightBracket.get());
        MutableText prefix = Text.literal("");

        if (prefixFormat.get() != Format.Normal) logo.setStyle(Style.EMPTY.withFormatting(prefixFormat.get().formatting));
        logo.setStyle(logo.getStyle().withColor(TextColor.fromRgb(prefixColor.get().getPacked())));

        if (prefixFormat.get() != Format.Normal && formatBrackets.get()) {
            left.setStyle(Style.EMPTY.withFormatting(prefixFormat.get().formatting));
            right.setStyle(Style.EMPTY.withFormatting(prefixFormat.get().formatting));
        }

        left.setStyle(left.getStyle().withColor(TextColor.fromRgb(leftColor.get().getPacked())));
        right.setStyle(right.getStyle().withColor(TextColor.fromRgb(rightColor.get().getPacked())));

        prefix.append(left);
        prefix.append(logo);
        prefix.append(right);
        prefix.append(" ");

        return prefix;
    }

    public enum Format {
        Normal(null),
        Heavy(Formatting.BOLD),
        Italic(Formatting.ITALIC),
        Underline(Formatting.UNDERLINE),
        Crossed(Formatting.STRIKETHROUGH),
        Cursed(Formatting.OBFUSCATED);

        final Formatting formatting;
        Format (Formatting formatting) {
            this.formatting = formatting;
        }
    }

    public double getScale(Vector3d pos) {
        double denom = pos.distance(
            mc.gameRenderer.getCamera().getPos().x,
            mc.gameRenderer.getCamera().getPos().y,
            mc.gameRenderer.getCamera().getPos().z
        ) / divisor.get();
        return MathHelper.clamp(textScale.get() / denom, minScale.get(), maxScale.get());
    }
}