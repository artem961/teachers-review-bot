package test.project.telega;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Статический доступ к ApplicationContext Spring.
 * Позволяет получать бины в любом месте приложения.
 */
@Component
public class AppContext implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext; // Spring сам передаст контекст
    }

    /**
     * Получить бин по классу.
     */
    public static <T> T getBean(Class<T> beanClass) {
        if (context == null) {
            throw new IllegalStateException("ApplicationContext не инициализирован!");
        }
        return context.getBean(beanClass);
    }
}