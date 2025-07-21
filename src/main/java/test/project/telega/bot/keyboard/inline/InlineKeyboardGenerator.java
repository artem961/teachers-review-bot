package test.project.telega.bot.keyboard.inline;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Getter
public class InlineKeyboardGenerator {
    private Integer width;
    private InlineKeyboardMarkup.InlineKeyboardMarkupBuilder builder;
    private List<InlineKeyboardButton> currentRow;
    private List<InlineKeyboardRow> buttonsRow;

    {
        currentRow = new ArrayList<>();
        builder = InlineKeyboardMarkup.builder();
        buttonsRow = new ArrayList<>();
    }

    public InlineKeyboardGenerator(Integer width) {
        this.width = width;
    }

    public InlineKeyboardGenerator addButton(InlineKeyboardButton button) {
        if (currentRow.size() == width) {
            builder.keyboardRow(
                    new InlineKeyboardRow(currentRow)
            );
            currentRow.clear();
        }
        currentRow.add(button);
        return this;
    }

    public InlineKeyboardGenerator addButton(String buttonText, String callbackData) {
        return addButton(InlineKeyboardButton
                .builder()
                .text(buttonText)
                .callbackData(callbackData)
                .build());
    }

    public InlineKeyboardGenerator addScrollButtons(Integer previousPage, Integer nextPage) {
        buttonsRow.add(new InlineKeyboardRow(
                InlineKeyboardButton
                        .builder()
                        .text("previous")
                        .callbackData("previous")
                        .build(),
                InlineKeyboardButton
                        .builder()
                        .text("next")
                        .callbackData("next")
                        .build()));
        return this;
    }

    public InlineKeyboardGenerator addBackButton(){
        buttonsRow.add( new InlineKeyboardRow(
                InlineKeyboardButton
                        .builder()
                        .text("back")
                        .callbackData("back")
                        .build()));
        return this;
    }

    public InlineKeyboardGenerator addConfirmButton(){
        buttonsRow.add( new InlineKeyboardRow(
                InlineKeyboardButton
                        .builder()
                        .text("confirm")
                        .callbackData("confirm")
                        .build()));
        return this;
    }

    public InlineKeyboardMarkup getKeyboard() {
        builder.keyboardRow(
                new InlineKeyboardRow(currentRow));

        for (InlineKeyboardRow row : buttonsRow) {
            builder.keyboardRow(row);
        }

        return builder.build();
    }
}
