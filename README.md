# Gra w Go

Gra Go w architekturze klient–serwer, napisana w Javie 21. Klient JavaFX
komunikuje się z serwerem przez TCP. Serwer kojarzy graczy w pary, obsługuje
wiele partii jednocześnie i pozwala rozpocząć grę z botem.

## Możliwości

- Plansza 9 × 9, naprzemienne ruchy oraz bicie połączonych grup kamieni.
- Sprawdzanie oddechów, zakaz samobójstwa i reguła ko.
- Pasowanie, poddanie partii i uzgadnianie martwych grup po dwóch pasach.
- Punktacja uwzględniająca terytorium, jeńców, martwe grupy i komi 6,5 pkt.
- Bot dobierający ruchy heurystycznie.
- Zapis partii w MariaDB oraz odtwarzanie zapisanej gry po jej identyfikatorze.

## Wymagania

- JDK 21 i Maven.
- Działająca MariaDB do uruchomienia serwera i zapisu partii.
- Środowisko graficzne do klienta JavaFX i testów interfejsu.

JavaFX jest pobierany przez Maven — nie trzeba wskazywać lokalnego katalogu SDK.

## Konfiguracja bazy

Domyślna konfiguracja lokalna znajduje się w
`src/main/resources/application.properties`:

| Ustawienie | Wartość lokalna |
| --- | --- |
| Adres | `jdbc:mariadb://localhost:3306/go_game` |
| Użytkownik | `go_user` |
| Hasło przykładowe | `go_pass` |

Przed uruchomieniem serwera utwórz bazę `go_game` oraz użytkownika z dostępem
do tej bazy. Hibernate tworzy lub aktualizuje tabele przy starcie.
Konfigurację można nadpisać zmiennymi środowiskowymi Springa:
`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` i
`SPRING_DATASOURCE_PASSWORD`. Własnych haseł nie zapisuj w repozytorium.

## Uruchamianie

Skompiluj projekt:

```shell
mvn clean compile
```

Uruchom serwer w pierwszym terminalu:

```shell
mvn exec:java -Pserver
```

Serwer i klient używają portu `5000`; klient domyślnie łączy się z `localhost`.

W osobnym terminalu uruchom klienta graficznego:

```shell
mvn javafx:run
```

Dla gry między dwoma osobami uruchom drugiego klienta w kolejnym terminalu.
Można również wybrać grę z botem z poziomu interfejsu.

## Przebieg gry

Kamień stawia się przez kliknięcie przecięcia linii planszy. Przyciski pozwalają
spasować lub poddać partię. Po dwóch kolejnych pasach gra przechodzi do
uzgadniania wyniku: kliknięciem zaznacza się martwe grupy, a przyciskiem
**Accept** zatwierdza stan planszy. Akceptacja obu graczy kończy punktowanie.
Przycisk **Continue** pozwala wrócić do gry. Zapisane partie można odtwarzać,
podając identyfikator gry w interfejsie.

## Testy i dokumentacja

```shell
mvn clean verify
mvn javadoc:javadoc
```

Testy korzystają z JUnit 4 i Mockito. Testy JavaFX wymagają dostępnego ekranu;
na Linuksie bez środowiska graficznego można uruchomić je przez Xvfb:

```shell
xvfb-run -a mvn clean verify
```

Wygenerowany JavaDoc trafia do `target/site/apidocs/`. Dokumentacja wynikowa,
pliki kompilacji i lokalne ustawienia IDE są pomijane przez Git.

## Struktura projektu

| Katalog w `src/main/java/pl/edu/go/` | Odpowiedzialność |
| --- | --- |
| `client/` | JavaFX i komunikacja z serwerem |
| `model/` | plansza, ruchy, zasady i punktacja |
| `server/bot/` | strategie bota |
| `server/commandInterfaces/` | obsługa komend gry |
| `server/networkInterfaces/` | interfejsy komunikacji |
| `server/persistence/` | encje, repozytoria i zapis partii |
| `server/replay/` | odtwarzanie gier |

Konfiguracja i styl JavaFX znajdują się w `src/main/resources/`, a testy
w `src/test/java/`. Projekt korzysta z JavaFX, Spring Boot,
Spring Data JPA/Hibernate oraz sterownika MariaDB.
