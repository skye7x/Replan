# Replan

Prosta aplikacja To-Do na Androida (Kotlin), w której dla każdego zadania możesz ustawić:
- **kiedy zacząć** je robić (start),
- **kiedy ma być zrobione** (termin).

Aplikacja dynamicznie planuje **lokalne powiadomienia** (AlarmManager) na oba te momenty,
więc dostaniesz przypomnienie zarówno "czas zacząć", jak i "termin minął / mija".

## Uprawnienia, o które poprosi aplikacja

- **Powiadomienia** (Android 13+) — potrzebne, aby przypomnienia się wyświetlały.
- **Dokładne alarmy** (Android 12+) — aplikacja poprosi o przejście do ustawień, jeśli nie są włączone;
  bez tego przypomnienia i tak zadziałają, ale mogą być mniej precyzyjne w czasie.

- Kategorie / priorytety zadań i filtrowanie listy.
- Zadania cykliczne (codziennie / co tydzień).
- Widget na ekranie głównym.
- Synchronizacja w chmurze.
