package converter.ecomXslToJson.logic;


import caits.utils.CalculateException;
import converter.ecomXslToJson.entities.common.*;
import converter.ecomXslToJson.entities.ecom.Place;
import converter.ecomXslToJson.entities.ecom.PlacesOfReception;
import converter.ecomXslToJson.entities.ecom.SheetEntity;
import converter.ecomXslToJson.entities.ecom.TariffSheet;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static converter.ecomXslToJson.entities.common.HCN.*;

/**
 * Класс отвечающий за парсинг эксель фалов ЕКОМ.
 */
@Service
public class EcomXslToJson implements IXslToJson {
    private GisAddressSpecifier gas;
    private final Map<String, Place> places = new HashMap<>();
    private final Map<String, PlacesOfReception> placesOfReceptionMap = new HashMap<>();

    @Autowired
    public void setGas(GisAddressSpecifier gas) {
        this.gas = gas;
    }

    /**
     * Метод получает записи с 5 листов и записывает их в необходимом порядке.
     *
     * @param sid          уникальный идентификатор задания.
     * @param xslFilePath  путь к файлу экселя.
     * @param jsonFilePath путь к файлу JSON.
     * @param tariffCode   общий тарифный код.
     */
    @Override
    public void execute(String sid, String xslFilePath, String jsonFilePath, String tariffCode) {
        File xslFile = new File(xslFilePath);
        try (Workbook workbook = WorkbookFactory.create(xslFile)) {
            recInfo(sid, "Начало парсинга файла " + xslFilePath);
            ProgressBar currentBar = this.statusMap.get(sid).getBar();
            currentBar.setMax(12);
            TariffSheet tariffCityToCity = getTariffsFromXsl(workbook, tariffCode, CITY_TO_CITY_TARIFFS, TO_CITY_TARIFF);
            recInfo(sid, "Получение записей из  " + CITY_TO_CITY_TARIFFS + " завершено.");
            currentBar.incValue();
            TariffSheet tariffCityToRegion = getTariffsFromXsl(workbook, tariffCode, CITY_TO_REGIONS_TARIFFS, TO_REGION_TARIFF);
            recInfo(sid, "Получение записей из  " + CITY_TO_REGIONS_TARIFFS + " завершено.");
            currentBar.incValue();
            List<SheetEntity> cityToCity = getFromSheet(workbook, CITY_TO_CITY);
            recInfo(sid, "Получение записей из  " + CITY_TO_CITY + " завершено.");
            currentBar.incValue();
            List<SheetEntity> cityToRegion = getFromSheet(workbook, CITY_TO_REGION);
            recInfo(sid, "Получение записей из  " + CITY_TO_REGION + " завершено.");
            currentBar.incValue();
            List<PlacesOfReception> placesOfReception = getPlacesOfReception(workbook);
            recInfo(sid, "Получение записей из  " + RECEPTIONS + " завершено.");
            currentBar.incValue();
            recInfo(sid, "Начало получения идентификаторов сущностей . . .");
            for (Place cur : places.values()) {
                String currentGuid = gas.getAddressGuid(cur.getForGuidString());
                if (!currentGuid.equals(NOT_FOUND)) {
                    cur.setGuid(currentGuid);
                } else {
                    recError(sid, "Для " + cur.getForGuidString() + " невозможно получить GUID");
                }
            }
            currentBar.incValue();
            recInfo(sid, "Получение идентификаторов завершено.");
            recInfo(sid, "Начало получения идентификаторов мест приёма . . .");
            for (PlacesOfReception cur : placesOfReceptionMap.values()) {
                String guidIndex = gas.getAddressGuid(String.valueOf(cur.getFormattedIndex()));
                String guidFrom = gas.getAddressGuid(cur.getFormattedFrom());
                if (NOT_FOUND.equals(guidIndex)) {
                    recError(sid, cur.getFormattedIndex() + " не найден");
                }
                if (NOT_FOUND.equals(guidFrom)) {
                    recError(sid, cur.getFormattedFrom() + " не найден");
                }
                cur.setIndexGuid(guidIndex);
                cur.setFromGuid(guidFrom);
            }
            currentBar.incValue();
            recInfo(sid, "Получение идентификаторов мест приёма завершено.");
            if (this.statusMap.get(sid).getState() == TaskState.WORK) {
                recInfo(sid, "Начало записи JSON в файл.");
                try (FileWriter fw = new FileWriter(new File(jsonFilePath))) {
                    JSONCreate jsonCreate = new JSONCreate(tariffCode);
                    fw.write(tariffCityToCity.toJsonString());
                    recInfo(sid, CITY_TO_CITY_TARIFFS + " подкготовленны к записи.");
                    currentBar.incValue();
                    fw.write(jsonCreate.createEcomJson(cityToCity, CITY_TO_CITY));
                    recInfo(sid, CITY_TO_CITY + " подкготовленны к записи.");
                    currentBar.incValue();
                    fw.write(tariffCityToRegion.toJsonString());
                    recInfo(sid, CITY_TO_REGIONS_TARIFFS + " подкготовленны к записи.");
                    currentBar.incValue();
                    fw.write(jsonCreate.createEcomJson(cityToRegion, CITY_TO_REGION));
                    recInfo(sid, CITY_TO_REGION + " подкготовленны к записи.");
                    currentBar.incValue();
                    fw.write(jsonCreate.createEcomReceptionsJson(placesOfReception, RECEPTIONS));
                    recInfo(sid, RECEPTIONS + " подкготовленны к записи.");
                    currentBar.incValue();
                }
                recInfo(sid, "Запись в файл успешно произведена.");
            } else {
                recInfo(sid, "Задача прервана");
            }
            setStateOkIfAllRight(sid);
        } catch (IOException | CalculateException e) {
            recError(sid, e.getMessage());
        } catch (AbortException ae) {
            TaskStatus ts = this.statusMap.get(sid);
            ts.addMessage(TaskMessageType.WARNING, ae.getMessage());
        }
    }

    /**
     * Метод получения итератора по строкам листа из книги по его имени.
     *
     * @param workbook  книга.
     * @param sheetName имя листа
     * @return итератор по строкам листа.
     */
    private Iterator<Row> getRowsIterator(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        return sheet.rowIterator();
    }

    /**
     * Метод получения тарифов с листа.
     *
     * @param workbook       книга содержащая лист с тарифами.
     * @param ecomTariffCode общий тарифный код.
     * @param sheetName      имя листа с тарифами.
     * @param sign           признак принадлежности тарифов к исползующей их сущности.
     * @return карту тарифов.
     */
    private TariffSheet getTariffsFromXsl(Workbook workbook, String ecomTariffCode, String sheetName, int sign) {
        Iterator<Row> rows = getRowsIterator(workbook, sheetName);
        int idCodeTariff = 0;
        List<Integer> columnValues = new ArrayList<>();
        if (rows.hasNext()) {
            Row row = rows.next();
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cur = row.getCell(i);
                if (TARIFF_CODE.equals(cur.getStringCellValue())) {
                    idCodeTariff = cur.getColumnIndex();
                } else {
                    columnValues.add(Integer.valueOf(
                            (cur.getStringCellValue().replaceAll("[^\\d]+", ""))));
                }
            }
        }
        TariffSheet ts = new TariffSheet(ecomTariffCode, sign, sheetName);
        ts.addColumns(columnValues);
        for (; rows.hasNext(); ) {
            Row row = rows.next();
            for (int i = 1; i < row.getLastCellNum(); i++) {
                if (i != idCodeTariff) {
                    ts.addTariff((int) row.getCell(idCodeTariff).getNumericCellValue(),
                            columnValues.get(i - 1),
                            (int) row.getCell(i).getNumericCellValue());
                }
            }
        }
        return ts;
    }

    /**
     * Метод проверяет наличие данного места в карте мест, если оно найдено то возвращает его,
     * иначе добавляет в карту переданное в параметрах место и возвращает его.
     *
     * @param place место для проверки.
     * @return найденное место или текущее.
     */
    private Place getOrCreate(Place place) {
        if (places.containsKey(place.getForGuidString())) {
            return places.get(place.getForGuidString());
        } else {
            places.put(place.getForGuidString(), place);
            return place;
        }
    }

    /**
     * Метод преобразует строки листа в объекты.
     *
     * @param workbook  книга.
     * @param sheetName название листа.
     * @return лист объектов со значениями строк.
     */
    private List<SheetEntity> getFromSheet(Workbook workbook, String sheetName) {
        Iterator<Row> rows = getRowsIterator(workbook, sheetName);
        List<SheetEntity> list = new ArrayList<>();
        Map<String, Integer> columnNames = getColumnNameMap(rows);
        for (; rows.hasNext(); ) {
            Row row = rows.next();
            Place from = new Place(
                    row.getCell(columnNames.get(CITY_FROM)).getStringCellValue(),
                    row.getCell(columnNames.get(REGION_FROM)).getStringCellValue()
            );
            Place to;
            if (!columnNames.containsKey(CITY_TO)) {
                to = new Place(row.getCell(columnNames.get(REGION_TO)).getStringCellValue());
            } else {
                to = new Place(
                        row.getCell(columnNames.get(CITY_TO)).getStringCellValue(),
                        row.getCell(columnNames.get(REGION_TO)).getStringCellValue()
                );
            }
            from = getOrCreate(from);
            to = getOrCreate(to);
            SheetEntity current;
            if (columnNames.containsKey(WAY)) {
                current = new SheetEntity(
                        from, to,
                        row.getCell(columnNames.get(WAY)).getStringCellValue(),
                        (int) row.getCell(columnNames.get(TARIFF_CODE)).getNumericCellValue(), TO_CITY_TARIFF
                );
            } else {
                current = new SheetEntity(
                        from, to,
                        (int) row.getCell(columnNames.get(TARIFF_CODE)).getNumericCellValue(), TO_REGION_TARIFF
                );
            }
            list.add(current);
        }
        return list;
    }

    /**
     * Метод преобразует строки листа в места приёма.
     *
     * @param workbook книга.
     * @return лист мест приёма.
     */
    private List<PlacesOfReception> getPlacesOfReception(Workbook workbook) {
        Iterator<Row> rows = getRowsIterator(workbook, HCN.RECEPTIONS);
        List<PlacesOfReception> list = new ArrayList<>();
        Map<String, Integer> columnNames = getColumnNameMap(rows);
        while (rows.hasNext()) {
            Row row = rows.next();
            PlacesOfReception cur = new PlacesOfReception(
                    (int) row.getCell(columnNames.get(INDEX)).getNumericCellValue(),
                    row.getCell(columnNames.get(FROM)).getStringCellValue(),
                    row.getCell(columnNames.get(TYPE)).getStringCellValue(),
                    row.getCell(columnNames.get(ATTACHMENT)).getStringCellValue(),
                    YES.equals(row.getCell(columnNames.get(INCREASE)).getStringCellValue())
            );
            placesOfReceptionMap.put(cur.getFormattedIndex() + cur.getFormattedFrom(), cur);
            list.add(cur);
        }
        return list;
    }
}
