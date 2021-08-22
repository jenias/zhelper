package ru.zhelper.services;

import ru.zhelper.models.Procurement;

public interface ZakupkiParser {
    Procurement parse(String html);
}
