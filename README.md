# UI Automation Tests (Selenium + Java 21)
Автоматизированные UI-тесты для проверки функциональности форм авторизации и заполнения анкеты

## Стек
- **Язык**: Java 21
- **Фреймворк тестирования**: JUnit 5
- **Инструмент автоматизации**: Selenium WebDriver
- **Сборка**: Gradle
- **Управление драйверами**: WebDriverManager
- **Генерация тестовых данных**: DataFaker
- **Упрощение кода**: Lombok
- **Логирование**: SLF4J + Logback

## Структура
```text
├── .env                          # Переменные окружения (не коммитится в Git!)
├── .gitignore
├── build.gradle                  # Конфигурация зависимостей и чтение .env
├── README.md
└── src/
    ├── main/java/org/example/
    │   ├── entity/               # Data Transfer Objects (Records)
    │   │   └── Questionnaire.java
    │   └── pages/                # Page Object Model
    │       ├── AlertPage.java
    │       ├── LoginPage.java
    │       └── QuestionnairePage.java
    └── test/java/org/example/tests/
        ├── BaseTest.java         # Базовая настройка WebDriver и JUnit lifecycle
        ├── LoginTests.java       # Тесты формы авторизации
        └── QuestionnaireTests.java # Тесты формы анкеты
```

## Предварительные требования
- JDK 21

## Конфигурация и безопасность
1. Создайте файл .env в корне проекта:
```text
   TEST_EMAIL=test@protei.ru
   TEST_PASSWORD=test
```
2. Файл .env добавлен в .gitignore и никогда не должен попадать в систему контроля версий
3. При запуске задачи test, Gradle автоматически считывает этот файл и передает значения как System Properties в JVM

## Быстрый старт
1. Убедитесь, что в корне проекта создан файл .env
2. Запустите тесты через терминал:
```bash
  ./gradlew clean test
```