package test.project.telega.bot.tools.keyboard.inline;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class BackConfirmController {
    public InlineKeyboardMarkup getKeyboard() {
       return new InlineKeyboardGenerator(1)
                .addConfirmButton()
                .addBackButton()
                .getKeyboard();
    }
    public static boolean isBackButtonSelected(String callbackData) {
        return callbackData.equals("back");
    }

    public static boolean isConfirmButtonSelected(String callbackData) {
        return callbackData.equals("confirm");
    }

    public static InlineKeyboardMarkup addBackButton(InlineKeyboardMarkup markup) {
        InlineKeyboardGenerator generator = new InlineKeyboardGenerator(1);
        generator.addRows(markup.getKeyboard());
        generator.addBackButton();
        return generator.getKeyboard();
    }

    public static InlineKeyboardMarkup addConfirmButton(InlineKeyboardMarkup markup) {
        InlineKeyboardGenerator generator = new InlineKeyboardGenerator(1);
        generator.addRows(markup.getKeyboard());
        generator.addConfirmButton();
        return generator.getKeyboard();
    }
}
