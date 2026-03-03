package com.example.assistant_startup.remote.repository_imp

import com.example.assistant_startup.domain.models.Message
import com.example.assistant_startup.domain.repository.ChatRepo
import com.example.assistant_startup.remote.KtorClient

class ChatRepoImp(private val client: KtorClient): ChatRepo {
    override fun getMessages(): List<Message> {
        return listOf(
            Message(
                text = "Слушай, запрос из прошлого примера работает, но таблица 'Orders' разрослась до миллиона строк, и теперь всё жутко тормозит. Ты упоминал индексы — они помогут ускорить поиск по 'created_at' и 'user_id'?",
                isUser = true
            ),
            Message(
                text = "Определенно! Индексы — это как алфавитный указатель в конце толстой книги: базе данных не нужно пролистывать все страницы (делать Full Table Scan), чтобы найти нужные строки. \n\n### Что нужно сделать:\n\n1. **Индекс на внешний ключ**: `user_id` используется в JOIN, поэтому индекс здесь критически важен.\n2. **Индекс на дату**: так как мы фильтруем по `created_at`, индекс ускорит отсеивание старых заказов.\n\n### Пример команды:\n```sql\nCREATE INDEX idx_orders_user_date ON Orders(user_id, created_at);\n```\n\n**Важный нюанс:** я предложил *составной индекс*. Порядок колонок в нем имеет значение: сначала ставим то поле, по которому идет точное совпадение (`user_id`), а затем диапазон (`created_at`). Это позволит базе данных максимально эффективно использовать 'бинарный поиск'. \n\nНо помни: индексы ускоряют чтение (SELECT), но немного замедляют запись (INSERT/UPDATE), так как системе нужно обновлять и саму таблицу, и дерево индекса.",
                isUser = false
            )
        )
    }
}