---
title: "Apache POI"
description: "Apache POI - это Java библиотека для чтения и записи файлов Microsoft Office (Excel, Word, PowerPoint). Предоставляет программный доступ к форматам Office документов."
tags:
  - libraries
  - java
  - java-apache-poi
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Apache POI

**Apache POI** - это **Java** библиотека для чтения и записи файлов **Microsoft Office** (**Excel, `Word`, PowerPoint**). Предоставляет программный доступ к форматам **Office** документов.

## Полезные ссылки

### Официальная документация
- [Apache POI](https://poi.apache.org/) — официальный сайт
- [Apache POI GitHub](https://github.com/apache/poi) — репозиторий проекта
- [Apache POI Documentation](https://poi.apache.org/components/spreadsheet/) — документация

### См. также
- [Apache Commons](../utility-libraries/java-apache-commons.md) — **Apache Commons** для работы с файлами

## Содержание

- [Основные возможности](#основные-возможности)
  - [Работа с Excel файлами (**XSSF/HSSF**)](#работа-с-excel-файлами-xssfhssf)
    - [Создание Excel файла](#создание-excel-файла)
    - [Чтение Excel файла](#чтение-excel-файла)
    - [Форматирование ячеек](#форматирование-ячеек)
    - [Работа с формулами](#работа-с-формулами)
  - [Работа с Word документами](#работа-с-word-документами)
    - [Создание Word документа](#создание-word-документа)
    - [Чтение Word документа](#чтение-word-документа)
  - [Работа с PowerPoint](#работа-с-powerpoint)
    - [Создание презентации](#создание-презентации)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Event-driven чтение больших файлов](#event-driven-чтение-больших-файлов)
  - [SXSSF для больших файлов](#sxssf-для-больших-файлов)
  - [Работа с шаблонами](#работа-с-шаблонами)
  - [Валидация данных](#валидация-данных)
  - [Работа с изображениями](#работа-с-изображениями)
- [Integration с Spring Boot](#integration-с-spring-boot)
  - [Excel Import/Export Service](#excel-importexport-service)
  - [REST Controller для **Excel** операций](#rest-controller-для-excel-операций)
- [Testing](#testing)
  - [Unit Testing Excel операций](#unit-testing-excel-операций)
  - [Integration Testing](#integration-testing)
- [Performance Optimization](#performance-optimization)
  - [Memory Management](#memory-management)
  - [Streaming для больших отчетов](#streaming-для-больших-отчетов)
- [Best Practices](#best-practices)
  - [File Handling](#file-handling)
  - [Error Handling](#error-handling)
  - [Security Considerations](#security-considerations)
- [Migration Guide](#migration-guide)
  - [From JXL to POI](#from-jxl-to-poi)
  - [From Apache POI 3.x to 5.x](#from-apache-poi-3x-to-5x)
- [Experimental Features](#experimental-features)
  - [POI 6.0+ Features](#poi-60-features)
- [Troubleshooting](#troubleshooting)
  - [Common Issues](#common-issues)
  - [Debugging](#debugging)

## Основные возможности

### Работа с **Excel** файлами (**XSSF/HSSF**)

#### Создание **Excel** файла

Создание **workbook**, листа, строк и ячеек через **XSSFWorkbook** (**формат .xlsx**).

```java
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/
 * Создание Excel файла через Apache POI
 * Демонстрирует базовое создание workbook, листа, строк и ячеек
 */

// Создание workbook - основного объекта для работы с Excel файлом
// XSSFWorkbook используется для формата .xlsx (Excel 2007+)
Workbook workbook = new XSSFWorkbook(); // для .xlsx
// HSSFWorkbook используется для формата .xls (старый формат Excel 97-2003)
// Workbook workbook = new HSSFWorkbook(); // для .xls

// Создание листа (sheet) в workbook
// Лист - это вкладка в Excel файле, можно создать несколько листов
Sheet sheet = workbook.createSheet("Sheet1");  // Создаем лист с именем "Sheet1"

// Создание строки и ячеек - добавляем данные в лист
// createRow(0) создает первую строку (индекс начинается с 0)
Row row = sheet.createRow(0);

// Создание ячеек в строке и заполнение их данными
Cell cell1 = row.createCell(0);  // Создаем первую ячейку (колонка A)
cell1.setCellValue("Имя");      // Устанавливаем значение ячейки

Cell cell2 = row.createCell(1);  // Создаем вторую ячейку (колонка B)
cell2.setCellValue("Возраст");   // Устанавливаем значение ячейки

Cell cell3 = row.createCell(2);  // Создаем третью ячейку (колонка C)
cell3.setCellValue("Город");     // Устанавливаем значение ячейки

// Запись workbook в файл - сохраняем созданный Excel файл на диск
try (FileOutputStream fos = new FileOutputStream("example.xlsx")) {
    // write() записывает весь workbook в поток вывода
    workbook.write(fos);
    // После записи файл будет создан на диске
}
workbook.close();  // Закрываем workbook и освобождаем ресурсы
```

#### Чтение **Excel** файла
```java
/
 * Чтение Excel файла через Apache POI
 * Демонстрирует чтение данных из Excel файла с обработкой различных типов ячеек
 */
// Чтение файла - открываем существующий Excel файл для чтения
try (FileInputStream fis = new FileInputStream("example.xlsx")) {
    // Создаем Workbook из потока файла
    // XSSFWorkbook читает формат .xlsx (Excel 2007+)
    Workbook workbook = new XSSFWorkbook(fis);

    // Получение листа - получаем первый лист из workbook (индекс начинается с 0)
    Sheet sheet = workbook.getSheetAt(0);
    // Можно также получить лист по имени: workbook.getSheet("Sheet1")

    // Итерация по строкам - перебираем все строки в листе
    for (Row row : sheet) {
        // Итерация по ячейкам в строке - перебираем все ячейки в текущей строке
        for (Cell cell : row) {
            // Определяем тип ячейки и читаем значение соответственно
            switch (cell.getCellType()) {
                case STRING:
                    // Ячейка содержит текст - читаем строковое значение
                    System.out.print(cell.getStringCellValue() + "\t");
                    break;
                case NUMERIC:
                    // Ячейка содержит число - проверяем является ли оно датой
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // Ячейка отформатирована как дата - читаем как дату
                        System.out.print(cell.getDateCellValue() + "\t");
                    } else {
                        // Ячейка содержит обычное число - читаем как число
                        System.out.print(cell.getNumericCellValue() + "\t");
                    }
                    break;
                case BOOLEAN:
                    // Ячейка содержит логическое значение (true/false)
                    System.out.print(cell.getBooleanCellValue() + "\t");
                    break;
                case FORMULA:
                    // Ячейка содержит формулу - выводим саму формулу (не вычисленное значение)
                    System.out.print(cell.getCellFormula() + "\t");
                    // Для получения вычисленного значения нужно использовать FormulaEvaluator
                    break;
                default:
                    // Неизвестный тип ячейки (BLANK, ERROR и т.д.)
                    System.out.print("Unknown\t");
            }
        }
        System.out.println();  // Переход на новую строку после обработки всех ячеек в строке
    }

    workbook.close();  // Закрываем workbook и освобождаем ресурсы
}
```

#### Форматирование ячеек
```java
/
 * Форматирование ячеек в Excel через Apache POI
 * Демонстрирует создание стилей, шрифтов, цветов и форматирование чисел
 */
// Создание стилей - CellStyle определяет внешний вид ячейки
CellStyle headerStyle = workbook.createCellStyle();  // Создаем новый стиль для заголовков

// Создание шрифта - Font определяет параметры текста
Font headerFont = workbook.createFont();
headerFont.setBold(true);                              // Делаем шрифт жирным
headerFont.setFontHeightInPoints((short) 14);         // Устанавливаем размер шрифта 14 пунктов
headerStyle.setFont(headerFont);                      // Применяем шрифт к стилю

// Настройка цвета фона ячейки
headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());  // Устанавливаем цвет фона (светло-синий)
headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);             // Устанавливаем тип заливки (сплошная)

// Применение стиля к ячейке
Row headerRow = sheet.createRow(0);                    // Создаем строку заголовка
Cell headerCell = headerRow.createCell(0);             // Создаем ячейку заголовка
headerCell.setCellValue("Заголовок");                  // Устанавливаем текст в ячейку
headerCell.setCellStyle(headerStyle);                  // Применяем стиль к ячейке (жирный текст, синий фон)

// Формат чисел - настройка отображения числовых значений
CellStyle numberStyle = workbook.createCellStyle();    // Создаем стиль для чисел
// setDataFormat устанавливает формат отображения числа
// "#,##0.00" означает: разделитель тысяч, минимум одна цифра, два знака после запятой
numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

Cell numberCell = row.createCell(1);                   // Создаем ячейку для числа
numberCell.setCellValue(12345.67);                     // Устанавливаем числовое значение
numberCell.setCellStyle(numberStyle);                  // Применяем формат числа (будет отображаться как "12,345.67")

// Выравнивание текста в ячейке
CellStyle centerStyle = workbook.createCellStyle();    // Создаем стиль для центрирования
centerStyle.setAlignment(HorizontalAlignment.CENTER);  // Горизонтальное выравнивание по центру
centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);  // Вертикальное выравнивание по центру
```

#### Работа с формулами
```java
/
 * Работа с формулами в Excel через Apache POI
 * Демонстрирует создание формул и их вычисление
 */
// Создание ячеек с формулами - формулы записываются в формате Excel
Cell sumCell = row.createCell(3);
// setCellFormula устанавливает формулу в ячейку
// "SUM(A1:C1)" вычисляет сумму значений в ячейках A1, B1, C1
sumCell.setCellFormula("SUM(A1:C1)");

Cell averageCell = row.createCell(4);
// "AVERAGE(A1:C1)" вычисляет среднее арифметическое значений в ячейках A1, B1, C1
averageCell.setCellFormula("AVERAGE(A1:C1)");

Cell ifCell = row.createCell(5);
// "IF(A1>10,\"Большое\",\"Маленькое\")" - условное выражение
// Если значение A1 > 10, возвращает "Большое", иначе "Маленькое"
ifCell.setCellFormula("IF(A1>10,\"Большое\",\"Маленькое\")");

// Пересчет формул - вычисление всех формул в workbook
// FormulaEvaluator используется для вычисления формул программно
FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
evaluator.evaluateAll();  // Вычисляем все формулы во всем workbook

// Получение вычисленного значения - получаем результат вычисления формулы
CellValue cellValue = evaluator.evaluate(sumCell);  // Вычисляем значение ячейки с формулой SUM
double sum = cellValue.getNumberValue();             // Получаем числовое значение результата
// cellValue также может содержать строковое значение (getStringValue()) или ошибку (getErrorValue())
```

### Работа с **Word** документами

#### Создание **Word** документа
```java
import org.apache.poi.xwpf.usermodel.*;

/
 * Создание Word документа через Apache POI
 * Демонстрирует создание документа, параграфов, форматирование текста и таблиц
 */
// Создание документа - XWPFDocument представляет документ Word формата DOCX
XWPFDocument document = new XWPFDocument();

// Создание параграфа - параграф это текстовый блок в документе
XWPFParagraph paragraph = document.createParagraph();

// Создание текстового фрагмента (run) в параграфе
// Run позволяет установить форматирование для части текста
XWPFRun run = paragraph.createRun();
run.setText("Это пример текста в Word документе.");  // Устанавливаем текст
run.setFontSize(14);                                   // Устанавливаем размер шрифта 14 пунктов
run.setBold(true);                                      // Делаем текст жирным

// Добавление таблицы - создаем таблицу с указанным количеством строк и столбцов
// createTable(3, 3) создает таблицу с 3 строками и 3 столбцами
XWPFTable table = document.createTable(3, 3);

// Заполнение таблицы данными - итерируемся по строкам и ячейкам
for (int i = 0; i < 3; i++) {
    XWPFTableRow row = table.getRow(i);  // Получаем строку по индексу
    for (int j = 0; j < 3; j++) {
        XWPFTableCell cell = row.getCell(j);  // Получаем ячейку по индексу столбца
        // Устанавливаем текст в ячейку с координатами
        cell.setText("Ячейка " + (i + 1) + "," + (j + 1));
    }
}

// Запись документа в файл - сохраняем созданный Word документ на диск
try (FileOutputStream fos = new FileOutputStream("example.docx")) {
    // write() записывает весь документ в поток вывода
    document.write(fos);
    // После записи файл будет создан на диске в формате DOCX
}
document.close();  // Закрываем документ и освобождаем ресурсы
```

#### Чтение **Word** документа
```java
/
 * Чтение Word документа (DOCX) через Apache POI
 * Демонстрирует чтение параграфов и таблиц из существующего документа
 */
try (FileInputStream fis = new FileInputStream("example.docx")) {  // Открываем файл для чтения
    // Создаем XWPFDocument из потока файла
    // XWPFDocument представляет документ Word формата DOCX
    XWPFDocument document = new XWPFDocument(fis);

    // Чтение параграфов - получаем все параграфы из документа
    // Параграфы - это текстовые блоки в документе
    for (XWPFParagraph paragraph : document.getParagraphs()) {
        System.out.println("Параграф: " + paragraph.getText());  // Выводим текст параграфа
    }

    // Чтение таблиц - получаем все таблицы из документа
    // Таблицы содержат структурированные данные в виде строк и столбцов
    for (XWPFTable table : document.getTables()) {
        System.out.println("Таблица:");
        // Итерируемся по строкам таблицы
        for (XWPFTableRow row : table.getRows()) {
            // Итерируемся по ячейкам в строке
            for (XWPFTableCell cell : row.getTableCells()) {
                System.out.print(cell.getText() + "\t");  // Выводим текст ячейки с табуляцией
            }
            System.out.println();  // Переход на новую строку после каждой строки таблицы
        }
    }

    document.close();  // Закрываем документ и освобождаем ресурсы
}
```

### Работа с **PowerPoint**

#### Создание презентации
```java
import org.apache.poi.xslf.usermodel.*;

/
 * Создание PowerPoint презентации через Apache POI
 * Демонстрирует создание слайдов, добавление текста и изображений
 */

// Создание презентации - создаем новый объект XMLSlideShow
// XMLSlideShow представляет презентацию PowerPoint формата PPTX
XMLSlideShow ppt = new XMLSlideShow();

// Создание слайда - добавляем новый слайд в презентацию
// Каждый слайд может содержать текст, изображения, фигуры и другие элементы
XSLFSlide slide = ppt.createSlide();

// Создание текстового блока на слайде
XSLFTextShape textShape = slide.createTextBox();  // Создаем текстовое поле
textShape.setText("Заголовок слайда");            // Устанавливаем текст в поле
// setAnchor устанавливает позицию и размер текстового поля на слайде
// Параметры: x, y, width, height (в точках)
textShape.setAnchor(new Rectangle2D.Double(100, 100, 400, 50));

// Добавление изображения на слайд
try (FileInputStream fis = new FileInputStream("image.jpg")) {  // Открываем файл изображения
    // Добавляем изображение в презентацию и получаем его данные
    // PictureType.JPEG указывает формат изображения
    XSLFPictureData pictureData = ppt.addPicture(fis, XSLFPictureData.PictureType.JPEG);
    
    // Создаем фигуру изображения на слайде
    XSLFPictureShape picture = slide.createPicture(pictureData);
    
    // Устанавливаем позицию и размер изображения на слайде
    // Параметры: x, y, width, height (в точках)
    picture.setAnchor(new Rectangle2D.Double(100, 200, 300, 200));
}

// Запись презентации в файл
try (FileOutputStream fos = new FileOutputStream("presentation.pptx")) {
    ppt.write(fos);  // Записываем презентацию в файл
}
ppt.close();  // Закрываем презентацию и освобождаем ресурсы
```

## Продвинутые возможности

### **Event-driven** чтение больших файлов
```java
/
 * Event-driven чтение больших Excel файлов через Apache POI
 * Используется для обработки файлов которые не помещаются в память целиком
 * Обрабатывает файл по частям (event-driven подход) вместо загрузки всего файла
 */
public class ExcelEventReader implements XSSFSheetXMLHandler.SheetContentsHandler {

    // Текущая обрабатываемая строка и столбец
    private int currentRow = -1;  // Индекс текущей строки (-1 означает что строка не начата)
    private int currentCol = -1;  // Индекс текущего столбца (-1 означает что столбец не начат)

    /
     * Вызывается при начале обработки новой строки
     * @param rowNum номер строки (начинается с 0)
     */
    @Override
    public void startRow(int rowNum) {
        this.currentRow = rowNum;  // Сохраняем номер текущей строки
        this.currentCol = -1;      // Сбрасываем индекс столбца для новой строки
    }

    /
     * Вызывается при завершении обработки строки
     * @param rowNum номер завершенной строки
     */
    @Override
    public void endRow(int rowNum) {
        // Обработка завершения строки - здесь можно выполнить финальные действия со строкой
        // Например, сохранить строку в БД, обработать данные и т.д.
        System.out.println("Обработана строка " + rowNum);
    }

    /
     * Вызывается при обработке каждой ячейки в строке
     * @param cellReference ссылка на ячейку (например, "A1", "B2")
     * @param formattedValue отформатированное значение ячейки (как оно отображается в Excel)
     * @param comment комментарий к ячейке (если есть)
     */
    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {
        // Обработка ячейки - здесь можно обработать данные ячейки
        // Это вызывается для каждой ячейки в строке последовательно
        System.out.println("Ячейка " + cellReference + ": " + formattedValue);
    }

    /
     * Вызывается при обработке header/footer листа
     * @param text текст header или footer
     * @param isHeader true если это header, false если footer
     * @param tagName имя тега header/footer
     */
    @Override
    public void headerFooter(String text, boolean isHeader, String tagName) {
        // Обработка header/footer - обычно не требуется для обработки данных
    }
}

// Использование
try (FileInputStream fis = new FileInputStream("large-file.xlsx")) {
    XSSFWorkbook workbook = new XSSFWorkbook(fis);
    XSSFReader reader = new XSSFReader(workbook.getPackage());

    ExcelEventReader handler = new ExcelEventReader();
    XMLReader xmlReader = XMLReaderFactory.createXMLReader();

    xmlReader.setContentHandler(new XSSFSheetXMLHandler(
        workbook.getStylesSource(),
        null,
        handler,
        false
    ));

    // Чтение каждого листа
    XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) reader.getSheetsData();
    while (sheets.hasNext()) {
        try (InputStream sheetStream = sheets.next()) {
            xmlReader.parse(new InputSource(sheetStream));
        }
    }
}
```

### **SXSSF** для больших файлов
```java
// SXSSF для записи больших файлов с низким потреблением памяти
SXSSFWorkbook workbook = new SXSSFWorkbook(100); // 100 строк в памяти
Sheet sheet = workbook.createSheet();

// Создание большого количества данных
for (int i = 0; i < 100000; i++) {
    Row row = sheet.createRow(i);
    for (int j = 0; j < 10; j++) {
        Cell cell = row.createCell(j);
        cell.setCellValue("Данные " + i + "," + j);
    }

    // Принудительная запись каждые 1000 строк
    if (i % 1000 == 0) {
        ((SXSSFSheet) sheet).flushRows();
    }
}

// Автоматическая очистка временных файлов
workbook.dispose();
workbook.close();
```

### Работа с шаблонами
```java
public class ExcelTemplateProcessor {

    public void fillTemplate(String templatePath, String outputPath, Map<String, Object> data)
            throws IOException {
        try (FileInputStream fis = new FileInputStream(templatePath)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            // Поиск и замена плейсхолдеров
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.STRING) {
                        String value = cell.getStringCellValue();
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            value = value.replace("{{" + entry.getKey() + "}}",
                                                entry.getValue().toString());
                        }
                        cell.setCellValue(value);
                    }
                }
            }

            // Сохранение результата
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                workbook.write(fos);
            }
            workbook.close();
        }
    }
}

// Использование
Map<String, Object> data = Map.of(
    "name", "John Doe",
    "age", 30,
    "city", "New York"
);

ExcelTemplateProcessor processor = new ExcelTemplateProcessor();
processor.fillTemplate("template.xlsx", "result.xlsx", data);
```

### Валидация данных
```java
// Создание data validation
DataValidationHelper validationHelper = sheet.getDataValidationHelper();

// Валидация списка
DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(
    new String[]{"Option1", "Option2", "Option3"});

CellRangeAddressList addressList = new CellRangeAddressList(1, 100, 0, 0);
DataValidation validation = validationHelper.createValidation(constraint, addressList);
validation.setShowErrorBox(true);
sheet.addValidationData(validation);

// Числовая валидация
DataValidationConstraint numericConstraint = validationHelper.createIntegerConstraint(
    DataValidationConstraint.OperatorType.BETWEEN, "1", "100");

CellRangeAddressList numericRange = new CellRangeAddressList(1, 100, 1, 1);
DataValidation numericValidation = validationHelper.createValidation(numericConstraint, numericRange);
sheet.addValidationData(numericValidation);
```

### Работа с изображениями
```java
// Добавление изображения в Excel
try (FileInputStream fis = new FileInputStream("logo.png")) {
    byte[] bytes = IOUtils.toByteArray(fis);
    int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);

    CreationHelper helper = workbook.getCreationHelper();
    Drawing<?> drawing = sheet.createDrawingPatriarch();

    ClientAnchor anchor = helper.createClientAnchor();
    anchor.setCol1(2);
    anchor.setRow1(2);

    Picture picture = drawing.createPicture(anchor, pictureIdx);
    picture.resize();
}

// Добавление изображения в Word
try (FileInputStream fis = new FileInputStream("diagram.png")) {
    XWPFParagraph paragraph = document.createParagraph();
    XWPFRun run = paragraph.createRun();

    run.addPicture(fis, XWPFDocument.PICTURE_TYPE_PNG, "diagram.png",
                   Units.toEMU(200), Units.toEMU(150));
}
```

## **Integration** с **Spring Boot**

### **Excel Import**/**Export Service**
```java
@Service
public class ExcelService {

    public void exportUsersToExcel(List<User> users, OutputStream outputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            // Создание заголовков
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Name", "Email", "Created Date"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            // Заполнение данных
            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getName());
                row.createCell(2).setCellValue(user.getEmail());
                row.createCell(3).setCellValue(user.getCreatedDate().toString());
            }

            // Автоподбор ширины колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        }
    }

    public List<User> importUsersFromExcel(InputStream inputStream) throws IOException {
        List<User> users = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    User user = new User();
                    user.setId((long) row.getCell(0).getNumericCellValue());
                    user.setName(row.getCell(1).getStringCellValue());
                    user.setEmail(row.getCell(2).getStringCellValue());
                    // Парсинг даты...
                    users.add(user);
                }
            }
        }

        return users;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
```

### **REST Controller** для **Excel** операций
```java
@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private UserService userService;

    @GetMapping("/export/users")
    public ResponseEntity<byte[]> exportUsers() throws IOException {
        List<User> users = userService.findAll();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        excelService.exportUsersToExcel(users, outputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "users.xlsx");

        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }

    @PostMapping("/import/users")
    public ResponseEntity<?> importUsers(@RequestParam("file") MultipartFile file) {
        try {
            List<User> users = excelService.importUsersFromExcel(file.getInputStream());
            userService.saveAll(users);

            return ResponseEntity.ok(Map.of(
                "message", "Импортировано " + users.size() + " пользователей",
                "count", users.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Ошибка импорта: " + e.getMessage()
            ));
        }
    }
}
```

## **Testing**

### **Unit Testing Excel** операций
```java
public class ExcelServiceTest {

    private ExcelService excelService;

    @BeforeEach
    void setUp() {
        excelService = new ExcelService();
    }

    @Test
    void testExportUsersToExcel() throws IOException {
        // Given
        List<User> users = Arrays.asList(
            createUser(1L, "John", "john@example.com"),
            createUser(2L, "Jane", "jane@example.com")
        );

        // When
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        excelService.exportUsersToExcel(users, outputStream);

        // Then
        byte[] excelData = outputStream.toByteArray();
        assertTrue(excelData.length > 0);

        // Проверка содержимого
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals("Users", sheet.getSheetName());

            Row headerRow = sheet.getRow(0);
            assertEquals("ID", headerRow.getCell(0).getStringCellValue());
            assertEquals("Name", headerRow.getCell(1).getStringCellValue());

            Row dataRow = sheet.getRow(1);
            assertEquals(1.0, dataRow.getCell(0).getNumericCellValue());
            assertEquals("John", dataRow.getCell(1).getStringCellValue());
        }
    }

    @Test
    void testImportUsersFromExcel() throws IOException {
        // Given
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        // Создание заголовков
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Email");

        // Создание данных
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(1.0);
        dataRow.createCell(1).setCellValue("John");
        dataRow.createCell(2).setCellValue("john@example.com");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        // When
        byte[] excelData = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(excelData);
        List<User> users = excelService.importUsersFromExcel(inputStream);

        // Then
        assertEquals(1, users.size());
        User user = users.get(0);
        assertEquals(1L, user.getId());
        assertEquals("John", user.getName());
        assertEquals("john@example.com", user.getEmail());
    }

    private User createUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setCreatedDate(LocalDateTime.now());
        return user;
    }
}
```

### **Integration Testing**
```java
@SpringBootTest
@AutoConfigureTestDatabase
public class ExcelControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testExportUsers() throws Exception {
        // Создание тестовых данных
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        userRepository.save(user);

        // Выполнение запроса
        mockMvc.perform(get("/api/excel/export/users"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition",
                "attachment; filename=users.xlsx"))
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    void testImportUsers() throws Exception {
        // Создание тестового Excel файла
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Email");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(1.0);
        dataRow.createCell(1).setCellValue("Import User");
        dataRow.createCell(2).setCellValue("import@example.com");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile(
            "file", "users.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            outputStream.toByteArray()
        );

        // Выполнение запроса
        mockMvc.perform(multipart("/api/excel/import/users").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.count").value(1))
            .andExpect(jsonPath("$.message").value("Импортировано 1 пользователей"));
    }
}
```

## **Performance Optimization**

### **Memory Management**
```java
public class OptimizedExcelProcessor {

    // Обработка больших файлов с ограничением памяти
    public void processLargeExcel(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            Workbook workbook = new XSSFWorkbook(fis);

            // Обработка по листам
            for (Sheet sheet : workbook) {
                processSheet(sheet);
                // Очистка после обработки листа
                ((XSSFSheet) sheet).getCTWorksheet().unsetSheetData();
            }

            workbook.close();
        }
    }

    private void processSheet(Sheet sheet) {
        // Обработка листа с ограничением строк
        final int BATCH_SIZE = 1000;
        List<Row> batch = new ArrayList<>();

        for (Row row : sheet) {
            batch.add(row);

            if (batch.size() >= BATCH_SIZE) {
                processBatch(batch);
                batch.clear();
            }
        }

        // Обработка остатка
        if (!batch.isEmpty()) {
            processBatch(batch);
        }
    }

    private void processBatch(List<Row> batch) {
        // Пакетная обработка данных
        for (Row row : batch) {
            // Обработка строки
        }
    }
}
```

### **Streaming** для больших отчетов
```java
public class StreamingExcelExporter {

    public void exportLargeReport(Query query, OutputStream outputStream) throws IOException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(1000)) {
            Sheet sheet = workbook.createSheet("Report");

            // Заголовки
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Name", "Value", "Date"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Данные (потоковая обработка)
            try (Stream<ReportData> dataStream = reportRepository.streamReportData(query)) {
                AtomicInteger rowNum = new AtomicInteger(1);

                dataStream.forEach(data -> {
                    Row row = sheet.createRow(rowNum.getAndIncrement());
                    row.createCell(0).setCellValue(data.getId());
                    row.createCell(1).setCellValue(data.getName());
                    row.createCell(2).setCellValue(data.getValue());
                    row.createCell(3).setCellValue(data.getDate().toString());
                });
            }

            workbook.write(outputStream);
            workbook.dispose(); // Очистка временных файлов
        }
    }
}
```

## Лучшие практики

### **File Handling**
```java
public class ExcelFileHandler {

    // Правильная работа с файлами
    public void safeExcelOperations() {
        Workbook workbook = null;
        FileOutputStream fos = null;

        try {
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Data");

            // Операции с workbook
            populateSheet(sheet);

            fos = new FileOutputStream("output.xlsx");
            workbook.write(fos);

        } catch (IOException e) {
            throw new RuntimeException("Error processing Excel file", e);
        } finally {
            // Правильное закрытие ресурсов
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // Log error
                }
            }
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    // Log error
                }
            }
        }
    }

    // Использование try-with-resources
    public void modernExcelOperations() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream("output.xlsx")) {

            Sheet sheet = workbook.createSheet("Data");
            populateSheet(sheet);
            workbook.write(fos);
        }
    }
}
```

### **Error Handling**
```java
public class RobustExcelProcessor {

    public List<String> processExcelFile(MultipartFile file) {
        List<String> errors = new ArrayList<>();

        if (!isValidExcelFile(file)) {
            errors.add("Неверный формат файла. Ожидается Excel файл.");
            return errors;
        }

        try (Workbook workbook = createWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            if (sheet == null) {
                errors.add("Файл не содержит листов.");
                return errors;
            }

            return validateAndProcessSheet(sheet);

        } catch (OfficeXmlFileException e) {
            errors.add("Файл поврежден или имеет неверный формат Excel.");
        } catch (IOException e) {
            errors.add("Ошибка чтения файла: " + e.getMessage());
        } catch (Exception e) {
            errors.add("Неожиданная ошибка обработки файла: " + e.getMessage());
        }

        return errors;
    }

    private boolean isValidExcelFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename != null && (
            filename.endsWith(".xlsx") ||
            filename.endsWith(".xls")
        );
    }

    private Workbook createWorkbook(InputStream inputStream) throws IOException {
        // Автоматическое определение формата
        try {
            return new XSSFWorkbook(inputStream);
        } catch (OfficeXmlFileException e) {
            // Попытка открыть как старый формат
            return new HSSFWorkbook(inputStream);
        }
    }
}
```

### **Security Considerations**
```java
public class SecureExcelProcessor {

    // Защита от XML External Entity (XXE) атак
    public Workbook createSecureWorkbook(InputStream inputStream) throws IOException {
        // Отключение внешних entity references
        Workbook workbook = new XSSFWorkbook(inputStream);

        // Дополнительные меры безопасности
        // - Валидация размера файла
        // - Ограничение количества листов/строк/колонок
        // - Проверка формул на опасные функции

        return workbook;
    }

    // Ограничение ресурсов
    public void validateFileSize(MultipartFile file) {
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("Файл слишком большой");
        }
    }

    // Валидация формул
    public void validateFormulas(Sheet sheet) {
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellType() == CellType.FORMULA) {
                    String formula = cell.getCellFormula();
                    if (isDangerousFormula(formula)) {
                        throw new SecurityException("Опасная формула обнаружена: " + formula);
                    }
                }
            }
        }
    }

    private boolean isDangerousFormula(String formula) {
        // Проверка на опасные функции
        return formula.toUpperCase().contains("EXEC(") ||
               formula.toUpperCase().contains("SYSTEM(");
    }
}
```

## **Migration Guide**

### **From JXL** to **POI**
```java
// Старый код с JXL
import jxl.*;

// Новый код с POI
// JXL -> POI migration
public class JxlToPoiMigration {

    // Чтение
    public void readWithJXL(String filePath) throws Exception {
        Workbook workbook = Workbook.getWorkbook(new File(filePath));
        Sheet sheet = workbook.getSheet(0);

        for (int i = 0; i < sheet.getRows(); i++) {
            for (int j = 0; j < sheet.getColumns(); j++) {
                Cell cell = sheet.getCell(j, i);
                System.out.print(cell.getContents() + "\t");
            }
            System.out.println();
        }
        workbook.close();
    }

    // Эквивалент с POI
    public void readWithPOI(String filePath) throws Exception {
        try (Workbook workbook = WorkbookFactory.create(new File(filePath))) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                for (Cell cell : row) {
                    System.out.print(getCellValue(cell) + "\t");
                }
                System.out.println();
            }
        }
    }

    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }
}
```

### **From Apache POI** 3.x `to 5`.x
```java
// POI 3.x
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

// POI 5.x
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Изменения:
// - Удалены deprecated методы
// - Изменены типы возвращаемых значений
// - Улучшена поддержка Java 8+

public class PoiMigration {

    // Старый способ
    public void oldWay() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Sheet1");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("Hello");
    }

    // Новый способ
    public void newWay() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Hello");
    }

    // Универсальный способ открытия файлов
    public Workbook openWorkbook(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return WorkbookFactory.create(fis); // Автоматически определяет формат
        }
    }
}
```

## **Experimental Features**

### **POI** 6.0+ **Features**
```java
// Предполагаемые будущие возможности
// (основанные на roadmap и текущих разработках)

// Улучшенная поддержка OOXML
// Лучшая производительность для больших файлов
// Интеграция с Java 17+
// Поддержка новых форматов Office 2021+

// Пример возможного API для будущих версий
public class FuturePoiFeatures {

    // Streaming API для чтения
    public void streamingRead(Path filePath) throws IOException {
        // Предполагаемый API
        try (ExcelReader reader = ExcelReader.open(filePath)) {
            reader.stream()
                .forEach(row -> {
                    // Обработка строки
                    System.out.println("Row: " + row);
                });
        }
    }

    // Reactive API
    public Flux<RowData> readReactive(Path filePath) {
        // Предполагаемый reactive API
        return ExcelReactiveReader.read(filePath)
            .map(this::convertRow);
    }

    // Type-safe API
    public void typeSafeRead(Path filePath) {
        // Предполагаемый type-safe API
        ExcelDocument<Person> doc = ExcelDocument.open(filePath, Person.class);
        List<Person> people = doc.readAll();
    }
}
```

## Решение проблем

### **Common Issues**
```java
public class PoiTroubleshooting {

    // Проблема: OutOfMemoryError при больших файлах
    public Workbook openLargeFile(File file) throws IOException {
        // Решение: Использовать SXSSF или streaming чтение
        return new SXSSFWorkbook(new XSSFWorkbook(new FileInputStream(file)));
    }

    // Проблема: InvalidFormatException
    public Workbook createWorkbookSafely(File file) throws IOException {
        try {
            return WorkbookFactory.create(file);
        } catch (InvalidFormatException e) {
            // Попытка восстановления поврежденного файла
            return attemptRecovery(file);
        }
    }

    // Проблема: Incorrect cell values
    public String getCellValueSafely(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                } else {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK -> "";
            case ERROR -> "ERROR";
            default -> "UNKNOWN";
        };
    }

    // Проблема: Styles not preserved
    public void preserveStyles(Workbook source, Workbook target) {
        // Копирование стилей между workbook'ами
        for (int i = 0; i < source.getNumCellStyles(); i++) {
            CellStyle sourceStyle = source.getCellStyleAt(i);
            CellStyle targetStyle = target.createCellStyle();

            // Копирование свойств стиля
            targetStyle.cloneStyleFrom(sourceStyle);
        }
    }
}
```

### **Debugging**
```java
public class PoiDebugger {

    // Логирование операций
    public void logWorkbookOperations(Workbook workbook) {
        // Включение детального логирования
        System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.SystemOutLogger");
        System.setProperty("poi.log.level", "1"); // DEBUG level
    }

    // Валидация workbook
    public List<String> validateWorkbook(Workbook workbook) {
        List<String> issues = new ArrayList<>();

        for (Sheet sheet : workbook) {
            if (sheet.getPhysicalNumberOfRows() == 0) {
                issues.add("Лист '" + sheet.getSheetName() + "' пустой");
            }

            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.FORMULA) {
                        try {
                            cell.getCachedFormulaResultType();
                        } catch (Exception e) {
                            issues.add("Неверная формула в ячейке " +
                                     cell.getAddress() + ": " + cell.getCellFormula());
                        }
                    }
                }
            }
        }

        return issues;
    }

    // Мониторинг производительности
    public class PerformanceMonitor {
        private long startTime;

        public void start() {
            startTime = System.nanoTime();
        }

        public void logOperation(String operation) {
            long duration = System.nanoTime() - startTime;
            System.out.println(operation + " took " + (duration / 1_000_000) + "ms");
            startTime = System.nanoTime();
        }
    }
}
```


## Полезные ссылки
- [Официальная документация `Apache POI`](https://poi.apache.org/)
- [POI Quick Guide](https://poi.apache.org/components/spreadsheet/quick-guide.html)
- [GitHub репозиторий](https://github.com/apache/poi)
- [POI Examples](https://poi.apache.org/components/spreadsheet/examples.html)

## См. также
- [Jackson](../serialization/jackson.md) — **JSON** сериализация
- [Обзор библиотек](../) — **CSV** и прочие библиотеки
- [Spring Batch](../../frameworks/java-frameworks/spring/spring-batch.md) — Пакетная обработка

