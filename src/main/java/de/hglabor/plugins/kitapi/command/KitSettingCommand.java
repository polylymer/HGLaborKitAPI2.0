package de.hglabor.plugins.kitapi.command;

import de.hglabor.plugins.kitapi.KitApi;
import de.hglabor.plugins.kitapi.kit.AbstractKit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;

import java.util.Optional;

public class KitSettingCommand {
    public KitSettingCommand() {
        new CommandAPICommand("kitsetting")
                .withArguments(kitArgument())
                .withArguments(//TODO depends on  kitArgument)
    }

    public Argument kitArgument() {
        return new CustomArgument<>("kit", (input) -> {
            Optional<AbstractKit> kitInput = KitApi.getInstance().getAllKits().stream().filter(kit -> kit.getName().equalsIgnoreCase(input)).findFirst();
            if (kitInput.isEmpty()) {
                throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder("Unknown kit: ").appendArgInput());
            } else {
                return kitInput.get();
            }
        }).overrideSuggestions(sender -> KitApi.getInstance().getAllKits().stream().map(AbstractKit::getName).toArray(String[]::new));
    }

    public Argument kitSettings() {
        return new CustomArgument<>("kit", (input) -> {
            Optional<AbstractKit> kitInput = KitApi.getInstance().getAllKits().stream().filter(kit -> kit.getName().equalsIgnoreCase(input)).findFirst();
            if (kitInput.isEmpty()) {
                throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder("Unknown kit: ").appendArgInput());
            } else {
                return kitInput.get();
            }
        }).overrideSuggestions((commandSender, objects) -> {
            //TODO vom kit holen YEEEEEEEET
            return new String[]{"awd"};
        });
    }
}
