package test.project.telega.bot.tools.keyboard.inline;

import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class InlineKeyboardController<T> {
    private final Function<T, String> nameGenerator;
    private final Function<T, String> callbackDataGenerator;
    private final Map<Long, Integer> userPages;
    private final Integer pageWidth;
    private final Integer pageHeight;
    @Setter
    private Collection<T> collectionToShow;

    {
        userPages = new ConcurrentHashMap<>();
    }

    public InlineKeyboardController(Function<T, String> nameGenerator,
                                    Function<T, String> callbackDataGenerator,
                                    Collection<T> collectionToShow,
                                    Integer pageWidth,
                                    Integer pageHeight
    ) {
        this.nameGenerator = nameGenerator;
        this.callbackDataGenerator = callbackDataGenerator;
        this.collectionToShow = collectionToShow;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
    }


    //region pages
    public void setUserPage(Long userId, Integer pageNumber) {
        this.userPages.put(userId, pageNumber);
    }

    public Integer getUserPage(Long userId) {
        if (userId == -1) {
            return 0;
        }

        Integer page = userPages.get(userId);

        if (page == null) {
            return 0;
        } else{
            return page;
        }
    }

    public Integer getPagesCount() {
        return (int) Math.ceil((double) collectionToShow.size() / pageWidth / pageHeight);
    }

    public String getPagesLabelForUser(Long userId) {
        return (getUserPage(userId) + 1) + "/" + getPagesCount();
    }

    public void updateUserPage(Long userId, String callbackData) {
        if (isPageButtonSelected(callbackData)) {
            Integer page = getUserPage(userId);
            if (callbackData.equals("previous") && !page.equals(0)) {
                userPages.put(userId, --page);
            } else if (callbackData.equals("next") && ((page + 1) * pageWidth * pageHeight < collectionToShow.size())) {
                userPages.put(userId, ++page);
            }
        }
    }

    public boolean isPageButtonSelected(String callbackData) {
        if (callbackData.equals("previous") || callbackData.equals("next")) {
            return true;
        }
        return false;
    }
    //endregion

    //region back
    public boolean isBackButtonSelected(String callbackData) {
        if (callbackData.equals("back")) {
            return true;
        }
        return false;
    }
    //endregion

    //region confirm
    public boolean isConfirmButtonSelected(String callbackData) {
        if (callbackData.equals("confirm")) {
            return true;
        }
        return false;
    }
    //endregion

    public InlineKeyboardMarkup getKeyboardForUser(Long userId) {
        InlineKeyboardGenerator generator = new InlineKeyboardGenerator(pageWidth);
        Integer pageNumber = getUserPage(userId);
        Integer previousPageNumber = pageNumber - 1;
        Integer nextPageNumber = pageNumber + 1;

        if (previousPageNumber < 0) {
            previousPageNumber = 0;
        }
        if (nextPageNumber * pageWidth * pageHeight > collectionToShow.size()) {
            nextPageNumber = pageNumber;
        }

        T[] array = (T[]) collectionToShow.toArray();
        int startIndex = pageNumber * pageWidth * pageHeight;
        int endIndex = startIndex + pageWidth * pageHeight;
        endIndex = Math.min(endIndex, array.length);
        for (int i = startIndex;
             i < endIndex;
             i++) {
            generator.addButton(nameGenerator.apply(array[i]),
                    callbackDataGenerator.apply(array[i]));
        }

        generator.addScrollButtons(previousPageNumber, nextPageNumber);
        return generator.getKeyboard();
    }

}
