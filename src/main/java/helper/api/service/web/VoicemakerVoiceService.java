//package helper.api.service.web;
//
//import helper.model.Language;
//import helper.model.dto.SoundingRequestDTO;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.firefox.FirefoxBinary;
//import org.openqa.selenium.firefox.FirefoxDriver;
//import org.openqa.selenium.firefox.FirefoxOptions;
//import org.openqa.selenium.firefox.FirefoxProfile;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.time.temporal.ChronoUnit;
//import java.util.*;
//
//import java.io.File;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//@Primary
//public class VoicemakerVoiceService extends VoiceService{
//    private static final String BASE_URL = "https://voicemaker.in/";
//
//    private static final int WAIT_DURATION = 900;
//
//    private static final String VOICE_MODAL_XPATH = "//div[@id='voicesModal']/div/div[@class='modal-content']";
//
//    protected static final Map<String, String> LANGUAGE_TO_LINK = new HashMap<>() {{
//        this.put("english", "en-GB");
//        this.put("русский", "ru-RU");
//        this.put("suomi", "fi-FI");
//        this.put("deutsch", "de-DE");
//        this.put("français", "fr-FR");
//        this.put("español", "es-ES");
//        this.put("italiano", "it-IT");
//    }};
//
//    @Override
//    public void getSound(String language, String word) {
//
//    }
//
//    @Override
//    public void fetchSounds(List<SoundingRequestDTO> dtos) {
//        /* Разложить входящие данные по языкам и проверить, нет ли озвучки для этого слова */
//        Map<Language, List<String>> filteredMap = filterData(arrangeData(dtos));
//
//        List<String> speakers = new ArrayList<>();
//
//        String torPath = "/home/roux/Downloads/tor-browser/Browser/firefox";
//
//        File torProfileDir = new File("/home/roux/Downloads/tor-browser/Browser/TorBrowser/Data/Browser/profile.default/");
//        FirefoxProfile torProfile = new FirefoxProfile(torProfileDir) {{
////            this.setPreference("browser.download.folderList", 2); //Use for the default download directory the last folder specified for a download
////            this.setPreference("browser.download.dir", downloadFilePath); //Set the last directory used for saving a file from the "What should (browser) do with this file?" dialog.
////            this.setPreference("browser.helperApps.neverAsk.saveToDisk", "audio/mpeg"); //list of MIME types to save to disk without asking what to use to open the file
////            this.setPreference("pdfjs.disabled", true);  // disable the built-in PDF viewer
//        }};
//
//        FirefoxOptions firefoxOptions = new FirefoxOptions() {{
//            this.setBinary(torPath);
//            this.setProfile(torProfile);
////            this.setCapability(FirefoxOptions.FIREFOX_OPTIONS, this);
////            this.addArguments("headless");
////            this.addArguments("--mute-audio");
//            this.setPageLoadStrategy(PageLoadStrategy.EAGER);
//        }};
////        options
//
//
//        WebDriver driver = new FirefoxDriver(firefoxOptions);
//        try {
//            String mainTorPage = "//body[@class='aboutTorConnect']";
//            /* Ждём коннекта браузера */
//            new WebDriverWait(driver, Duration.of(5, ChronoUnit.MINUTES))
//                .until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(mainTorPage)));
//            log.info("Соединение с сетью Tor установлено.");
//            /* Делаем запрос страницы */
//            log.info("Отправляется запрос на получение страницы: {}.", BASE_URL);
//            driver.get(BASE_URL);
//
//            /* Ждём загрузки страницы */
//            String changeButtonXPath = "//div[@class='editor-selected-voice']/span[@onclick='showVoicesModal()']";
//            new WebDriverWait(driver, Duration.of(WAIT_DURATION, ChronoUnit.SECONDS))
////                .until(webDriver -> ((JavascriptExecutor)webDriver).executeScript("return document.readyState").equals("complete"));
//                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(changeButtonXPath)));
//            log.info("Страница {} загружена.", BASE_URL);
//
//            for (Map.Entry<Language, List<String>> entry: filteredMap.entrySet()) {
//                for (String word: entry.getValue()) {
//                    log.info("Пробуем провести озвучку слова: {}", word);
//
//                    /* Очистить текстовую зону и ввести слово */
//                    WebElement inputTextArea = driver.findElement(By.id("main-textarea"));
//                    inputTextArea.clear();
//                    inputTextArea.sendKeys(word);
//                    /* В текстовой зоне - слово для перевода */
//
//                    openModalWindow(driver);
//                    log.info("Открыто окно выбора языка и спикера");
//
//                    /* Нажать на кнопку выбора "Default Voices" */
//                    String defaultVoicesButtonXPath = "//button[@title='Default voices']";
//                    WebElement defaultVoicesButton = new WebDriverWait(driver, Duration.of(WAIT_DURATION, ChronoUnit.SECONDS))
//                        .until(ExpectedConditions.elementToBeClickable(By.xpath(defaultVoicesButtonXPath)));
//                    /* Проверить, выбрана ли кнопка */
////                    boolean active = defaultVoicesButton.getAttribute("class").endsWith("active");
////                    if (!active) {
//                        defaultVoicesButton.click();
//                        log.info("Выбраны дефолтные голоса");
////                    }
//                    /* Выбираются только дефолтные голоса */
//
//                    /* todo дождаться загрузки языков */
//                    String currentLanguageXPath = "//div[@id='language-selector-div']/div/ul[@class='list']/li";
//                    log.info("Пробуем дождаться загрузки языков.");
//                    List<WebElement> languages = new WebDriverWait(driver, Duration.of(WAIT_DURATION, ChronoUnit.SECONDS))
//                        .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(currentLanguageXPath), 1));
//                    List<String> lanNames = languages.stream()
//                        .map(WebElement::getText)
//                        .collect(Collectors.toList());
//
//                    /* Найти и нажать кнопку выбора языка */
//                    String selectLanguageButtonXPath = "//div[@id='language-selector-div']/div[@class='nice-select']";
//                    WebElement selectLanguageButton = new WebDriverWait(driver, Duration.of(WAIT_DURATION, ChronoUnit.SECONDS))
//                        .until(ExpectedConditions.elementToBeClickable(By.xpath(selectLanguageButtonXPath)));
//                    selectLanguageButton.click();
//
//                    /*  Выбрать нужный язык */
//                    String selectedLanguageXPath = String.format("//div[@id='language-selector-div']/div/ul/li[@data-value='%s']", LANGUAGE_TO_LINK.get(entry.getKey().getName())) ;
//                    WebElement selectedLanguageListItem = new WebDriverWait(driver, Duration.of(WAIT_DURATION, ChronoUnit.SECONDS))
//                        .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(selectedLanguageXPath)));
//
//                    By currentLanguageBy = By.xpath("//div[@id='language-selector-div']/div/span[@class='current']");
//                    WebElement currentLanguage = driver.findElement(currentLanguageBy);
//                    if (!Objects.equals(selectedLanguageListItem.getText(), currentLanguage.getText())) {
//                        /*  Если языки отличаются, нажать нужный */
//                        selectedLanguageListItem.click();
//
//                        /* Как-то надо дождаться, чтобы список спикеров поменялся */
////                        log.info("Selected locale: {}.", LANGUAGE_TO_LINK.get(entry.getKey().getName()));
////                        String allVoicesXPathString = "//div[@class='voices']/div[@class='row voices-list']/div[@class='col-md-4']";
////                        By allVoicesBy = By.xpath(allVoicesXPathString);
////                        List<WebElement> allVoices = driver.findElements(allVoicesBy);
////                        log.info("allVoices: {}", allVoices.size());
//                        /* Как оказалось, дожидаться и не надо. */
//
//                        /* Готовим список спикеров, если он ещё не готов */
//                        if (speakers.isEmpty()) {
//                            String speakersListXPathString = "//div[@class='row voices-list']/div[@class='col-md-4']/div/div//div/div/div[@class='textBox']/span[@class='marquee']";
//                            By speakersListBy = By.xpath(speakersListXPathString);
//                            List<WebElement> speakersList = driver.findElements(speakersListBy);
//                            speakers = speakersList.stream()
//                                .map(el -> {
//                                    String gender = el.findElement(By.xpath("span/small")).getText();   // todo <-- Когда-нибудь gender пригодится - например, если нужно будет озвучивать фразы.
//                                    String speaker = el.getText();
//                                    return StringUtils.removeEnd(speaker, gender);
//                                })
//                                .collect(Collectors.toList());
//                        }
//
//                        /* TODO Проходимся по списку и для каждого спикера записываем озвучку */
//                        for (String speaker: speakers) {
//                            /* Проверяем, открыто ли модальное окно */
//                            WebElement modalWindow = driver.findElement(By.xpath(VOICE_MODAL_XPATH));
//                            boolean open = modalWindow.isDisplayed();
//                            log.info("Нужен спикер: {}, окно открыто: {}", speaker, open);
//                            if (!open) {
//                                openModalWindow(driver);
//                            }
//                            /* Если открыто, выбираем спикера по списку */
//                            String speakerToSelectXPathString = String.format("//div[@class='row voices-list']/div[@class='col-md-4']/div[contains(@apiname, '%s')]", speaker);
//                            WebElement speakerToSelect = driver.findElement(By.xpath(speakerToSelectXPathString));
//                            WebDriverWait selectSpeakerWait = new WebDriverWait(driver, Duration.of(WAIT_DURATION, ChronoUnit.SECONDS));
//                            selectSpeakerWait.until(ExpectedConditions.elementToBeClickable(By.xpath(speakerToSelectXPathString)));
//                            speakerToSelect.click();
//                            log.info("Выбран спикер {}", speaker);
//                            /* Выбран спикер */
//
//                            /* Нажать кнопку "Submit" */
//                            String submitVoiceButtonXPathString = "//button[@class='btn btn-block submit-voice']";
//                            WebDriverWait submitVoiceButtonWait = new WebDriverWait(driver, Duration.of(WAIT_DURATION, ChronoUnit.SECONDS));
//                            submitVoiceButtonWait.until(ExpectedConditions.elementToBeClickable(By.xpath(submitVoiceButtonXPathString)));
//                            WebElement submitVoiceButton = driver.findElement(By.xpath(submitVoiceButtonXPathString));
//                            submitVoiceButton.click();
//                            /* И дождаться, пока окно закроется */
//                            WebDriverWait modalWindowCloseWait = new WebDriverWait(driver, Duration.of(WAIT_DURATION, ChronoUnit.SECONDS));
//                            modalWindowCloseWait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(VOICE_MODAL_XPATH)));
//
//                            /* Нажать кнопку "Convert to speech" */
//                            String convertButtonXPath = "//button[@id='convert-button']";
//                            WebElement convertButton = new WebDriverWait(driver, Duration.of(WAIT_DURATION, ChronoUnit.SECONDS))
//                                .until(ExpectedConditions.elementToBeClickable(By.xpath(convertButtonXPath)));
//                            convertButton.click();
//                            /* Ждём, пока можно будет скачать */
//                            String downloadButtonXPath = "//div[@id='audioplayer-container']/div/div/div[@class='audioplayer']/div[@class='audioplayer-controls']/a[@class='audioplayer-download']";
//                            new WebDriverWait(driver, Duration.of(WAIT_DURATION, ChronoUnit.SECONDS))
//                                .until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(downloadButtonXPath)));
//                            WebElement downloadButton = new WebDriverWait(driver, Duration.of(WAIT_DURATION, ChronoUnit.SECONDS))
//                                .until(ExpectedConditions.elementToBeClickable(By.xpath(downloadButtonXPath)));
//                            /* todo Ну и жмём кнопку загрузки */
//                            downloadButton.click();
//
//                            Thread.sleep(3000);
//                        }
//                    }
//
////                    Thread.sleep(3000);
//                }
//            }
//
//
//
//            return;
//        } catch (Exception e) {
//            log.error("Ошибка при работке с {}.", BASE_URL);
//        } finally {
//            driver.close();
//        }
//    }
//
//    private void openModalWindow(WebDriver driver) {
//        /* Нажать на кнопку "Change" */
//        By changeSpeakerButtonBy = By.cssSelector("[onclick='showVoicesModal()']");
//        WebElement changeSpeakerButton = driver.findElement(changeSpeakerButtonBy);
//        changeSpeakerButton.click();
//        /* Появляется модальное окошко с выбором опций (языка, спикера и т.д.) */
//        String modalContentXPathString = "//div[@class='modal-content']";
//        WebDriverWait modalContentVisibilityWait = new WebDriverWait(driver, Duration.of(WAIT_DURATION, ChronoUnit.SECONDS));
//        modalContentVisibilityWait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(modalContentXPathString)));
//    }
//}
