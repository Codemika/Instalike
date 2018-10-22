import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Map.entry;

/**
 * TwitterLike.
 * Для работы требуется Google Chrome и Selenium с драйвером.
 */
class TwitterLike {
    // Словарь путей к элементам сайта.
    private final Map<String, String> xpaths = Map.ofEntries(
            entry("login_button", "//*[@id=\"page-container\"]/div/div[1]/form/div[2]/button")

    );
    private final String login;
    private final String password;
    private final String target;
    private ChromeDriver browser;
    private int baseDelay;
    private Logger logger;

    /**
     * Конструктор.
     * @param login Логин.
     * @param password Пароль.
     * @param target Цель.
     */
    TwitterLike(String login, String password,
                String target) {
        this(login, password, target, 3);
    }

    /**
     * @param login Логин.
     * @param password Пароль.
     * @param target Цель.
     * @param baseDelay Задержка между действиями (в секундах).
     */
    TwitterLike(String login, String password,
                String target, int baseDelay) {
        this.login = login;
        this.password = password;
        this.target = target;
        this.baseDelay = baseDelay;
        logger = Logger.getLogger(TwitterLike.class.getName());
    }

    /**
     * Начинаем лайк-кодинг!
     */
    void start() {
        log("TwitterLike начал свою работу!");
        browser = new ChromeDriver();
        login();
        likeAll();
        end();
    }

    /**
     * Суть лайк-кодинга.
     */
    private void likeAll() {
        browser.get(String.format("https://twitter.com/%s", target));
        waitASecond();
        try {
            List<WebElement> buttons = browser.findElementsByCssSelector("button.ProfileTweet-actionButton.js-actionButton.js-actionFavorite");
            for (var button : buttons) {
                try {
                    button.click();
                    log("Лайк поставлен, мой сир!");
                    waitASecond();
                }
                catch (Exception ex) {
                    log("Лайк уже стоит, мой сир!");
                }
            }
        }
        catch (NoSuchElementException ex) {
            warn("Не могу найти ни одной кнопки!");
        }
    }

    /**
     * Логинимся.
     */
    private void login() {
        browser.get("https://twitter.com/login");
        waitASecond();
        browser.findElementByClassName("js-username-field").sendKeys(login);
        browser.findElementByClassName("js-password-field").sendKeys(password);
        waitASecond();
        browser.findElement(new By.ByXPath(xpaths.get("login_button"))).click();
        waitASecond();
        log(String.format("Залогинились, как %s", login));
    }

    /**
     * Заканчиваем работу браузера.
     */
    private void end() {
        browser.close();
        log("Нокс!");
    }

    /**
     * Ждём секунду. На самом деле от трёх до пяти, случайным образом решается.
     */
    private void waitASecond() {
        try {
            Thread.sleep((baseDelay + ThreadLocalRandom.current().nextInt(1, 3)) * 1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Логируем сообщение.
     * @param message Текст сообщения.
     */
    private void log(String message) {
        logger.log(Level.INFO, String.format("TwitterLike: %s", message));
    }

    /**
     * Логируем предупреждение.
     * @param message Текст предупреждения.
     */
    private void warn(String message) {
        logger.log(Level.WARNING, String.format("TwitterLike: %s", message));
    }
}
