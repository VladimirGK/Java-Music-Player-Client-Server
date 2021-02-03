# Spotify :notes:

[`Spotify`](https://www.spotify.com/) е платформа за `stream`-ване на музика, която предоставя на потребителите достъп до милиони песни на изпълнители от цял свят.

> `Stream`-ването е метод за предаване на данни, използван обикновено за мултимедийни файлове. При него възпроизвеждането на съдържанието върху устройството на потребителя започва още с достъпването му, без да се налага то отначало да бъде изтеглено изцяло като файл и после да се стартира в подходящ плеър. Предаването на данните протича едновременно с възпроизвеждането, затова е необходима постоянна мрежова свързаност.

## Условие

Създайте приложение по подобие на `Spotify`, състоящо се от две части - сървър и клиент.

### **Spotify Server**

Предоставя следните функционалности на клиента:
- регистриране в платформата чрез **email** и **парола** (**потребителите трябва да се съхраняват във файл**)
- login в платформата чрез **email** и **парола**
- съхраняване на набор от песни, достъпни на потребителите за слушане
- търсене на песни
- създаване на статистика на най-слушаните песни от потребителите
- създаване на плейлисти (**плейлистите трябва да се съхраняват във файлове**)
- добавяне на песни към плейлисти
- връщане на информация за даден плейлист
- `stream`-ване на песни

### **Spotify Client**

`Spotify` клиентът трябва да има `command line interface` със следните команди:

```bash
register <email> <password>
login <email> <password>
disconnect
search <words> - връща всички песни, в чиито имена или имената на изпълнителите им, се среща потърсената дума (или думи)
top <number> - връща списък с *number* от най-слушаните песни в момента, сортиран в намаляващ ред
create-playlist <name_of_the_playlist>
add-song-to <name_of_the_playlist> <song>
show-playlist <name_of_the_playlist>
play <song>
stop
```
