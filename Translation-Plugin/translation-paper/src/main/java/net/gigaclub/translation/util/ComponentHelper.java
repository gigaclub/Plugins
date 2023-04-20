package net.gigaclub.translation.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;

import java.util.Collection;

public class ComponentHelper {

    public static ComponentLike join(Collection<? extends ComponentLike> components, Component delimiter) {
        return join(components.toArray(ComponentLike[]::new), delimiter);
    }

    public static Component join(ComponentLike[] components, Component delimiter) {
        TextComponent.Builder builder = Component.text();
        for (int i = 0, j = components.length; i < j; i++) {
            if (i > 0) {
                builder.append(delimiter);
            }
            builder.append(components[i]);
        }
        return builder.build();
    }

}
