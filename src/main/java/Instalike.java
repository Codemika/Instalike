import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Map.entry;

/**
 * Instalike.
 * Для работы требуется Google Chrome и Selenium с драйвером.
 */
class Instalike {
    // Словарь путей к элементам сайта.
    private final Map<String, String> xpaths = Map.ofEntries(
            entry("login_button", "//*[@id=\"react-root\"]/section/main/div/article/div/div[1]/div/form/div[3]/button"),
            entry("first_photo", "//*[@id=\"react-root\"]/section/main/div/div[3]/article/div/div/div[1]/div[1]"),
            entry("like_button", "/html/body/div[3]/div/div[2]/div/article/div[2]/section[1]/span[1]/button"),
            entry("next_button_first", "/html/body/div[3]/div/div[1]/div/div/a"),
            entry("next_button_default", "/html/body/div[3]/div/div[1]/div/div/a[2]"),
            entry("next_button_last", "/html/body/div[3]/div/div[1]/div/div/a")
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
    Instalike(String login, String password,
              String target) {
        this(login, password, target, 3);
    }

    /**
     * @param login Логин.
     * @param password Пароль.
     * @param target Цель.
     * @param baseDelay Задержка между действиями (в секундах).
     */
    Instalike(String login, String password,
              String target, int baseDelay) {
        this.login = login;
        this.password = password;
        this.target = target;
        this.baseDelay = baseDelay;
        logger = Logger.getLogger(Instalike.class.getName());
    }

    /**
     * Начинаем лайк-кодинг!
     */
    void start() {
        log("Instalike начал свою работу!");
        browser = new ChromeDriver();
        login();
        if (findFirst()) {
            likeCurrent();
            while (getNext()) {
                likeCurrent();
            }
        }
        end();
    }

    /**
     * Логинимся.
     */
    private void login() {
        browser.get("https://www.instagram.com/accounts/login/");
        waitASecond();
        browser.findElement(new By.ByName("username")).sendKeys(login);
        waitASecond();
        browser.findElement(new By.ByName("password")).sendKeys(password);
        browser.findElement(new By.ByXPath(xpaths.get("login_button"))).click();
        waitASecond();
        log(String.format("Залогинились, как %s", login));
    }

    /**
     * Ищем первую фотографию в профиле.
     * @return True, если нашлась, false в противном случае.
     */
    private boolean findFirst() {
        browser.get(String.format("https://www.instagram.com/%s", target));
        waitASecond();
        log(String.format("Ставим лайкосы для %s.", target));
        try {
            browser.findElement(new By.ByXPath(xpaths.get("first_photo"))).click();
            waitASecond();
            return true;
        } catch (NoSuchElementException ex) {
            warn("Не могу найти первую фотку! Наверное, фоток нет вообще!");
            return false;
        }
    }

    /**
     * Лайк-кодим открытую фотографию.
     */
    private void likeCurrent() {
        var likeButton = browser.findElement(new By.ByXPath(xpaths.get("like_button")));
        try {
            browser.findElement(new By.ByClassName("glyphsSpriteHeart__filled__24__red_5"));
            log("Тут лайк уже стоит!");
        }
        catch (NoSuchElementException ex) {
            likeButton.click();
            log("Лайкос поставлен, мой генерал!");
            waitASecond();
        }
    }

    /**
     * Открываем следующую фототграфию.
     * @return True, если удалось открыть, false в противном случае.
     */
    private boolean getNext() {
        try {
            browser.findElement(new By.ByClassName("coreSpriteRightPaginationArrow")).click();
            waitASecond();
            return true;
        }
        catch (NoSuchElementException e) {
            warn("Не могу открыть следующее фото. Кажется, закончились.");
            return false;
        }
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
        logger.log(Level.INFO, String.format("Instalike: %s", message));
    }

    /**
     * Логируем предупреждение.
     * @param message Текст предупреждения.
     */
    private void warn(String message) {
        logger.log(Level.WARNING, String.format("Instalike: %s", message));
    }
}
