package ru.zhelper.zhelper.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Procurement implements Serializable {

    //Что означает поле
    //Где можно найти по ФЗ №44
    //и по ФЗ №223
    private static final long serialVersionUID = -3159634854548811691L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //Этап закупки
    //ОБЩАЯ ИНФОРМАЦИЯ О ЗАКУПКЕ -> Этап закупки
    //ЖУРНАЛ СОБЫТИЙ -> СОБЫТИЕ -> Закупка переведена на этап "stage"
    @Column
    @Enumerated(EnumType.STRING)
    private Stage stage;

    //Уникальный идентификатор закупки
    //regNumber в ссылке на закупки или просто номер закупки
    //ОБЩИЕ СВЕДЕНИЯ О ЗАКУПКЕ -> Реестровый номер извещения
    @Column(unique = true, nullable = false, length = 30)
    private String uin;

    //Номер федерального закона
    //Можно вычислить в зависимости от длины номера uin, для ФЗ №44 19 знаков
    //для ФЗ №223 11 знаков
    @Column
    private int fzNumber;

    //Дата и время окончания срока подачи заявок
    //ИНФОРМАЦИЯ О ПРОЦЕДУРЕ ЭЛЕКТРОННОГО АУКЦИОНА -> Дата и время окончания срока подачи заявок на участие в электронном аукционе
    //ПОРЯДОК ПРОВЕДЕНИЯ ПРОЦЕДУРЫ -> Дата и время окончания подачи заявок (по местному времени заказчика)
    @Column
    private LocalDateTime applicationDeadline;

    //Максимальная цена
    //Начальная цена
    //СПИСОК ЛОТОВ -> СВЕДЕНИЯ О ЦЕНЕ ДОГОВОРА -> Начальная (максимальная) цена договора 
    @Column
    private BigDecimal contractPrice;

    //Тип закупки
    //ОБЩАЯ ИНФОРМАЦИЯ О ЗАКУПКЕ -> Способ определения поставщика (подрядчика, исполнителя)
    //ОБЩИЕ СВЕДЕНИЯ О ЗАКУПКЕ -> Способ размещения закупки
    @Column
    @Enumerated(EnumType.STRING)
    private ProcedureType procedureType;

    //Кто опубликовал извещение о закупке
    //ОБЩАЯ ИНФОРМАЦИЯ О ЗАКУПКЕ -> Размещение осуществляет
    //ЗАКАЗЧИК -> Наименование организации
    @Column
    private String publisherName;

    //Есть ли ограничения по СМП
    //ПРЕИМУЩЕСТВА, ТРЕБОВАНИЯ К УЧАСТНИКАМ -> Ограничения и запреты
    //ТРЕБОВАНИЯ К УЧАСТНИКАМ ЗАКУПКИ или ПОРЯДОК ПРОВЕДЕНИЯ ПРОЦЕДУРЫ -> Порядок подведения итогов
    @Column
    private String restrictions;

    //Ссылка на закупку на площадке размещения
    //ОБЩАЯ ИНФОРМАЦИЯ О ЗАКУПКЕ -> Адрес электронной площадки в информационно-телекоммуникационной сети "Интернет"
    //ОБЩИЕ СВЕДЕНИЯ О ЗАКУПКЕ -> Адрес электронной площадки в информационно-телекоммуникационной сети "Интернет"
    // TODO @URL(regexp = "^(http|ftp).*")
    @Column
    private URL linkOnPlacement;

    //Обеспечение заявки
    //ОБЕСПЕЧЕНИЕ ЗАЯВКИ -> Обеспечение заявки (размер)
    // = см. документацию
    @Column
    private String applicationSecure;

    //Обеспечение контракта
    //Обеспечение исполнения контракта (размер)
    // = см. документацию
    @Column
    private String contractSecure;

    //Объект закупки
    //ОБЩАЯ ИНФОРМАЦИЯ О ЗАКУПКЕ -> Наименование объекта закупки
    //ОБЩИЕ СВЕДЕНИЯ О ЗАКУПКЕ -> Наименование закупки
    @Lob
    @Column( length = 100000)
    @Type(type = "org.hibernate.type.TextType")
    private String objectOf;

    //Поле нужно чтобы отслеживать на сайте закупок необходимость обновить данные
    //Дата размещения текущей редакции извещения
    @Column
    private LocalDateTime lastUpdatedFromEIS;

    //Поле нужно чтобы отслеживать необходимость обновить данные
    @Column
    private LocalDateTime dateTimeLastUpdated;
}
