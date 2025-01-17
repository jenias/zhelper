package ru.zhelper.zhelper.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.zhelper.zhelper.models.ProcedureType;
import ru.zhelper.zhelper.models.Procurement;
import ru.zhelper.zhelper.models.Stage;
import ru.zhelper.zhelper.services.exceptions.BadDataParsingException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service("zakupkiParser615Fz")
public class ZakupkiParser615Fz implements ZakupkiParser {
    Logger logger = LoggerFactory.getLogger(ZakupkiParser615Fz.class);
    private static final String UIN_SELECTOR = "span[class=navBreadcrumb__text]";
    private static final String STAGE_SELECTOR = "span[class=cardMainInfo__state]";
    private static final String DEADLINE_SELECTOR = "span[class=section__title]:contains(Дата и время окончания срока подачи заявок на участие в электронном аукционе)";
    private static final String CONTRACT_PRICE_SELECTOR = "span[class=section__title]:contains(Начальная (максимальная) цена договора, рублей)";
    private static final String METHOD_SELECTOR = "span[class=section__title]:contains(Способ определения поставщика (подрядчика, исполнителя, подрядной организации))";
    private static final String PUBLISHER_SELECTOR = "span[class=section__title]:contains(Наименование организации)";
    private static final String RESTRICTIONS_SELECTOR = "span[class=section__title]:contains(Условия оплаты выполненных работ и (или) оказанных услуг)";
    private static final String LINK_SELECTOR = "span[class=section__title]:contains(Сайт оператора электронной площадки в сети «Интернет»)";
    private static final String APPLICATION_SECURE_SELECTOR = "span[class=section__title]:contains(Размер обеспечения заявки на участие в электронном аукционе)";
    private static final String CONTRACT_SECURE_SELECTOR = "span[class=section__title]:contains(Размер обеспечения исполнения обязательств по договору)";
    private static final String OBJECT_OF_SELECTOR = "span[class=section__title]:contains(Наименование закупки)";
    private static final String LAW_SELECTOR = "div[class=cardMainInfo__title d-flex text-truncate]";
    private static final String NUMBER_TO_REPLACE = "№ ";
    private static final String REPLACEMENT = "";
    private static final String START_LAW_TO_REPLACE = "ПП РФ ";
    private static final String SPAN_SEPARATOR = " <span";
    private static final String NBSP = "nbsp";
    private static final String IN_RUSSIAN_ROUBLE = " в российских рублях";
    private static final String RUSSIAN_ROUBLE = "(Российскийрубль)";
    private static final String DOT = ".";
    private static final String COMMA = ",";
    private static final String SPACE = " ";
    private static final String FINISH_LAW_TO_REPLACE = " Электронный аукцион на оказание услуг или выполнение работ по капитальному ремонту общего имущества в многоквартирном доме";
    private static final String METHOD = "Способ определения поставщика";
    private static final String OBJECT_OF = "Наименование";
    private static final String BAD_DATA_EXCEPTION = "Bad data in {}.";
    private static final String PUBLISHER_NAME = "Организатор.";
    private static final String RESTRICTION = "Restrictions";
    private static final String CONTRACT_SECURE = "Обеспечение контракта";
    private static final String UIN = "uin";
    private static final String STAGE = "stage";
    private static final String FZ_NUMBER = "Fz number";
    private static final String APPLICATION_DEADLINE = "APPLICATION_DEADLINE";
    private static final String CONTRACT_PRICE = "CONTRACT PRICE";
    private static final String LINK_ON_PLACEMENT = "Link on placement";
    private static final String APPLICATION_SECURE = "APPLICATION SECURE";
    private static final String PARSING = "Starting parse from html. Size {}";
    private static final String PARSED = "Procurement {} was parsed.";
    private static final String DATE_TIME_FORMATTER = "dd.MM.yyyy HH:mm";
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Mobile Safari/537.36";

    @Override
    public Procurement parse(String url) {
        if (logger.isDebugEnabled()) {
            logger.debug(PARSING, url);
        }
        Element body = null;
        try {
            Document document = Jsoup.connect(url).userAgent(USER_AGENT).get();
            body = document.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Procurement procurement = new Procurement();
        procurement.setUin(getUin(body));
        procurement.setStage(getStage(body));
        procurement.setFzNumber(getFzNumber(body));
        procurement.setApplicationDeadline(getApplicationDeadline(body));
        procurement.setContractPrice(getContractPrice(body));
        procurement.setProcedureType(getProcedureType(body));
        procurement.setPublisherName(getPublisherName(body));
        procurement.setRestrictions(getRestrictions(body));
        procurement.setLinkOnPlacement(getLinkOnPlacement(body));
        procurement.setApplicationSecure(getApplicationSecure(body));
        procurement.setContractSecure(getContractSecure(body));
        procurement.setObjectOf(getObjectOf(body));
        // Нет на странице даты последнего обновления, берем текущюю
        procurement.setLastUpdatedFromEIS(LocalDateTime.now());
        procurement.setDateTimeLastUpdated(LocalDateTime.now());
        if (logger.isDebugEnabled()) {
            logger.debug(PARSED, procurement);
        }
        return procurement;
    }

    protected String getObjectOf(Element body) {
        try {
            return body.select(OBJECT_OF_SELECTOR).first().siblingElements().first().text();
        } catch (NullPointerException exception) {
            logger.error(BAD_DATA_EXCEPTION, OBJECT_OF, exception);
            throw new BadDataParsingException(OBJECT_OF, exception);
        }
    }

    protected String getContractSecure(Element body) {
        try {
            return getPriceFromLine(body.select(CONTRACT_SECURE_SELECTOR).first().siblingElements().first().text());
        } catch (NullPointerException exception) {
            logger.error(BAD_DATA_EXCEPTION, CONTRACT_SECURE, exception);
            throw new BadDataParsingException(CONTRACT_SECURE, exception);
        }
    }

    protected String getApplicationSecure(Element body) {
        try {
            return getPriceFromLine(body.select(APPLICATION_SECURE_SELECTOR).first().siblingElements().first().text());
        } catch (NullPointerException exception) {
            logger.error(BAD_DATA_EXCEPTION, APPLICATION_SECURE, exception);
            throw new BadDataParsingException(APPLICATION_SECURE, exception);
        }
    }

    protected URL getLinkOnPlacement(Element body) {
        try {
            return new URL(body.select(LINK_SELECTOR).first().siblingElements().first().text());
        } catch (NullPointerException | MalformedURLException exception) {
            logger.error(BAD_DATA_EXCEPTION, LINK_ON_PLACEMENT, exception);
            throw new BadDataParsingException(LINK_ON_PLACEMENT, exception);
        }
    }

    protected String getRestrictions(Element body) {
        try {
            return body.select(RESTRICTIONS_SELECTOR).first().siblingElements().first().text();
        } catch (NullPointerException exception) {
            logger.error(BAD_DATA_EXCEPTION, RESTRICTION, exception);
            throw new BadDataParsingException(RESTRICTION, exception);
        }
    }

    protected String getPublisherName(Element body) {
        try {
            return body.select(PUBLISHER_SELECTOR).first().siblingElements().first().text();

        } catch (NullPointerException exception) {
            logger.error(BAD_DATA_EXCEPTION, PUBLISHER_NAME, exception);
            throw new BadDataParsingException(PUBLISHER_NAME, exception);
        }
    }

    protected ProcedureType getProcedureType(Element body) {
        try {
            String procedureType = body.select(METHOD_SELECTOR).first().siblingElements().first().text();
            return ProcedureType.get(procedureType);
        } catch (NullPointerException exception) {
            logger.error(BAD_DATA_EXCEPTION, METHOD, exception);
            throw new BadDataParsingException(METHOD, exception);
        }
    }

    protected BigDecimal getContractPrice(Element body) {
        try {
            String textContractPrice = getPriceFromLine(body.select(CONTRACT_PRICE_SELECTOR).first().siblingElements().first().text());
            return new BigDecimal(textContractPrice);
        } catch (NullPointerException exception) {
            logger.error(BAD_DATA_EXCEPTION, CONTRACT_PRICE, exception);
            throw new BadDataParsingException(CONTRACT_PRICE, exception);
        }
    }

    protected LocalDateTime getApplicationDeadline(Element body) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
        try {
            String textDate = body.select(DEADLINE_SELECTOR).
                    first().siblingElements().first().html().split(SPAN_SEPARATOR)[0];
            return LocalDateTime.parse(textDate, formatter);
        } catch (NullPointerException exception) {
            logger.error(BAD_DATA_EXCEPTION, APPLICATION_DEADLINE, exception);
            throw new BadDataParsingException(BAD_DATA_EXCEPTION, exception);
        }
    }

    protected int getFzNumber(Element body) {
        try {
            return Integer.parseInt(body.select(LAW_SELECTOR).first().text().
                    replace(START_LAW_TO_REPLACE, REPLACEMENT).replace(FINISH_LAW_TO_REPLACE, REPLACEMENT));
        } catch (NullPointerException exception) {
            logger.error(BAD_DATA_EXCEPTION, FZ_NUMBER, exception);
            throw new BadDataParsingException(BAD_DATA_EXCEPTION, exception);
        }
    }

    protected String getUin(Element body) {
        try {
            return body.select(UIN_SELECTOR).first().html().replace(NUMBER_TO_REPLACE, REPLACEMENT);
        } catch (NullPointerException exception) {
            logger.error(BAD_DATA_EXCEPTION, UIN, exception);
            throw new BadDataParsingException(BAD_DATA_EXCEPTION, exception);
        }
    }

    protected Stage getStage(Element body) {
        try {
            return Stage.get(body.select(STAGE_SELECTOR).first().html());
        } catch (NullPointerException exception) {
            logger.error(BAD_DATA_EXCEPTION, STAGE, exception);
            throw new BadDataParsingException(BAD_DATA_EXCEPTION, exception);
        }
    }

    private String getPriceFromLine(String textWithPrice) {
        return textWithPrice.replace(NBSP, REPLACEMENT).replace(IN_RUSSIAN_ROUBLE, REPLACEMENT).
                replace(SPACE, REPLACEMENT).replace(COMMA, DOT).replace(RUSSIAN_ROUBLE, REPLACEMENT);
    }
}
