# Репозиторий для домашних работ по курсу "Разработчик на Spring Framework"

Группа 2025-03

#### Студент:

Вараксин Александр

---

<a name="content"></a>
### Содержание

- [HW-01. XML-config.](#hw01)
- [HW-02. Annotation-config.](#hw02)
- [HW-03. Spring-boot.](#hw03)
- [HW-04. Spring-shell.](#hw04)
- [HW-05. Spring-jdbc.](#hw05)
- [HW-06. Spring-ORM-JPA-Hibernate.](#hw06)

---

<a name="hw01"></a>

### HW-01. XML-config.

Создан GitHub-репозиторий для выполнения домашних заданий.
Все зависимости настроены в IoC контейнере.
Контекст описывается XML-файлом.
Список вопросов с вариантами ответа хранится в ресурсах приложения, в формате csv.
Для проверки работоспособности сервиса тестирования написан юнит-тест.

<br/>

[[вернуться к содержанию]](#content)

---

<a name="hw02"></a>

### HW-02. Annotation-config.

Контекст описан с помощью Java-based и Annotation-based конфигурации.
Имя ресурса (файла в формате csv) с вопросами, а также количество правильных ответов для прохождения теста конфигурируются через файл настроек application.properties.
Приложение запрашивает фамилию и имя студента, задает вопросы с вариантами ответа и выводит результат тестирования студента.

<br/>

[[вернуться к содержанию]](#content)

---

<a name="hw03"></a>

### HW-03. Spring-boot.

Загрузка конфигурации и настройка приложения выполняется с применением аннотаций Spring Boot.
Локаль, имя ресурса (файла в формате csv) с вопросами, а также количество правильных ответов для прохождения теста конфигурируются через файл настроек application.yml.
Локализация выводимых сообщений настроена через свойства файлов message.properties для английского языка и messages_ru_RU.properties для русского языка.
В приложении применяется авторский баннер.
Приложение запрашивает фамилию и имя студента, задает вопросы с вариантами ответа и выводит результат тестирования студента на английском или русском языке в зависимости от выбранной настройки локали.

<br/>

[[вернуться к содержанию]](#content)

---

<a name="hw04"></a>

### HW-04. Spring-shell.

Расширена функциональность приложения. Управление запуском приложения осуществляется через Spring Shell команды.
Для запуска процесса тестирования необходимо использовать одну из команд "r", "run", "s" или "start", для завершения процесса тестирования необходимо использовать команду "exit".
Тестовые классы адартированы для тестирования с частичной загрузкой контекста с помощью аннотации @SpringBootTest и соответствующих атрибутов.

<br/>

[[вернуться к содержанию]](#content)

---

<a name="hw05"></a>

### HW-05. Spring-jdbc.

Разработано однопользовательское, консольное приложение позволяющее вести каталог книг в библиотеке.
В приложении используются Spring JDBC и реляционная база данных H2.
Параметризованные запросы выполняются с помощью NamedParametersJdbcTemplate.
Создание и инициализация схемы БД выполняются через schema.sql + data.sql.
С помощью @JdbcTest разработаны интеграционные тесты всех методов DAO Author, Genre, Book.
Управление в консоли приложения осуществляется через Spring Shell команды.
#### Наименование и назначение команд Spring Shell для работы с авторами, жанрами и книгами:
- "aa" : "Find all authors".
	Example : "aa".
	Result : "Id: 1, FullName: Author_1,
			  Id: 2, FullName: Author_2,
			  Id: 3, FullName: Author_3".
- "ab" : "Find all books".
	Example : "ab".
	Result : "Id: 1, title: BookTitle_1, author: {Id: 1, FullName: Author_1}, genres: [Id: 1, Name: Genre_1],
			  Id: 2, title: BookTitle_2, author: {Id: 2, FullName: Author_2}, genres: [Id: 2, Name: Genre_2],
			  Id: 3, title: BookTitle_3, author: {Id: 3, FullName: Author_3}, genres: [Id: 3, Name: Genre_3]".
- "ag" : "Find all genres".
	Example : "ag".
	Result : "Id: 1, Name: Genre_1,
			  Id: 2, Name: Genre_2,
			  Id: 3, Name: Genre_3".
- "bbid" : "Find book by id".
	Example : "bbid 2".
	Result : "Id: 2, title: BookTitle_2, author: {Id: 2, FullName: Author_2}, genres: [Id: 2, Name: Genre_2]".
- "bins" : "Insert book".
	Example : "bins Dracula 2 1".
	Result : "Id: 4, title: Dracula, author: {Id: 2, FullName: Author_2}, genres: [Id: 1, Name: Genre_1]".
- "bdel" : "Delete book by id".
	Example : "bdel 4".
	Result : "".
- "bupd" : "Update book".
	Example : "bupd 4 Pollyanna 1 1".
	Result : "Id: 4, title: Pollyanna, author: {Id: 1, FullName: Author_1}, genres: [Id: 1, Name: Genre_1]".

<br/>

[[вернуться к содержанию]](#content)

---
<a name="hw06"></a>

### HW-06. Spring-ORM-JPA-Hibernate.

Приложение каталога книг переведено на использование Spring ORM, JPA и Hibernate (EntityManager).
Дополнена доменная модель сущностью комментария к книге (один-ко-многим).

#### Основные изменения:
- Реализованы JPA-репозитории для всех сущностей (Author, Book, Genre, Comment)
- Добавлена сущность Comment с отношением @ManyToOne к Book
- Реализованы CRUD операции для комментариев:
  - Поиск комментария по id
  - Поиск всех комментариев по id книги
  - Создание, обновление и удаление комментариев
- Оптимизированы запросы для избежания проблем N+1:
  - Использованы JOIN FETCH в запросах
  - Lazy-инициализация для связей
- Аннотация @Transactional перенесена на уровень сервисов
- Написаны интеграционные тесты для всех репозиториев с использованием @DataJpaTest и TestEntityManager
- Добавлены тесты сервисов, проверяющие отсутствие LazyInitializationException
- DDL через Hibernate отключено, используется schema.sql

#### Наименование и назначение команд Spring Shell для работы с комментариями:
- "cbbid" : "Find all comments by book id".
	Example: "cbbid 1".
	Result: "Id: 1, Text: Comment_1, BookId: 1, Id: 2, Text: Comment_2, BookId: 1".
- "cbid" : "Find comment by id".
	Example: "cbid 1".
	Result: "Id: 1, Text: Comment_1, BookId: 1".
- "csave" : "Save comment (create or update)".
	Example: "csave 1 New comment text".
	Result: "Id: 4, Text: New comment text, BookId: 1".
- "cdel" : "Delete comment by id".
	Example: "cdel 4".
	Result: "".

<br/>

[[вернуться к содержанию]](#content)