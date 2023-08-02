package net.gigaclub.translation;

import net.gigaclub.base.odoo.Odoo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.xmlrpc.XmlRpcException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Translation {

    private final Odoo odoo;
    private final Plugin plugin;


    private String category;

    public Translation(String hostname, String database, String username, String password, Object plugin) {
        this.odoo = new Odoo(hostname, database, username, password);
        this.category = "";
        this.plugin = (Plugin) plugin;


    }


    public void setCategory(String category) {
        this.category = category;
    }

    public @NotNull Component t(String name, String UUID, TagResolver... tagResolvers) {
        try {


            String result = (String) this.odoo.getModels().execute("execute_kw", Arrays.asList(
                    this.odoo.getDatabase(), this.odoo.getUid(), this.odoo.getPassword(),
                    "gc.translation", "get_translation_by_player_uuid", Arrays.asList(name, UUID, this.category)
            ));
            try {
                return MiniMessage.miniMessage().deserialize(result, tagResolvers);
            } catch (IllegalStateException e) {
                return Component.text(result);
            }
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return Component.text("");
    }


    public boolean checkIfTranslationExists(String translationName) {
        return this.odoo.search_count(
                "gc.translation",
                List.of(
                        List.of(
                                Arrays.asList("name", "=", translationName)
                        )
                )
        ) > 0;
    }

    public void registerTranslation(String translationName) {
        if (!this.checkIfTranslationExists(translationName)) {
            String category = this.category;
            this.odoo.create(
                    "gc.translation",
                    List.of(
                            new HashMap() {{
                                put("name", translationName);
                                put("category", category);
                            }}
                    )
            );
        }
    }

    public void registerTranslation(HashMap<String, String> translationValues) {
        String translationName = translationValues.remove("translationName");
        if (!this.checkIfTranslationExists(translationName)) {
            String category = this.category;
            this.odoo.create(
                    "gc.translation",
                    List.of(
                            new HashMap() {{
                                put("name", translationName);
                                put("category", category);
                                put("values", translationValues);
                            }}
                    )
            );
        }
    }

    public void registerTranslations(List<Object> translationValues) {
        for (Object translationValue : translationValues) {
            if (translationValue instanceof String name) {
                registerTranslation(name);
            } else if (translationValue instanceof HashMap) {
                registerTranslation((HashMap) translationValue);
            }
        }
    }


}