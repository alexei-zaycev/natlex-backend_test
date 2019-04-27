package ru.net.avz.test.natlex_backend_test.service;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.net.avz.test.natlex_backend_test.Utils;
import ru.net.avz.test.natlex_backend_test.dao.JobDAO;
import ru.net.avz.test.natlex_backend_test.data.GeologicalClassPOJO;
import ru.net.avz.test.natlex_backend_test.data.JobPOJO;
import ru.net.avz.test.natlex_backend_test.data.SectionPOJO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 *
 * @author Zaitsev Alexei (aka AZ) / alexei.zaycev@ya.ru / alexei.zaycev@gmail.com
 */
@Service
public class JobService {

    private final Logger LOG                                = LoggerFactory.getLogger(this.getClass());

    private static final String MSG__IMPORT__SUCCESS        = "IMPORT {}";
    private static final String MSG__EXPORT__SUCCESS        = "EXPORT {} section(s)";

    @Nonnull private final JobDAO jobDAO;

    public JobService(
            @Autowired @Nullable JobDAO jobDAO) {

        this.jobDAO = Utils.requireDI(JobDAO.class, jobDAO);
    }

    /**
     * @return сгенерированный уникальный идентификатор задачи (см. {@link JobPOJO#id()})
     */
    public @Nonnull String nextJobId() {
        return UUID.randomUUID().toString();
    }

    /**
     * запланировать отложенную обработку XLS-файла
     *
     * @param jobId уникальный идентификатор задачи (см. {@link #nextJobId()})
     * @param content содержимое XLS-файла
     * @param sheetIdx порядковый номер листа, подлежащего обработке или -1 для значения по-умолчанию (по-умолчанию 0)
     * @param ignoreFirstNRows размер "шапки" в начале листа документа, который необходимо пропустить или -1 для значения по-умолчанию (по-умолчанию 1)
     * @return уникальный идентификатор задачи (см. {@link JobPOJO#id()})
     */
    @Async
    public @Nonnull CompletableFuture<JobPOJO> parseXLS(
            @Nonnull String jobId,
            @Nonnull InputStream content,
            int sheetIdx,
            int ignoreFirstNRows) {

        assert jobId != null : "<jobId> is null";
        assert content != null : "<content> is null";

        try (HSSFWorkbook book = new HSSFWorkbook(content)) {

            int _sheetIdx = (sheetIdx < 0 ? 0 : sheetIdx);
            int _ignoreFirstNRows = (ignoreFirstNRows < 0 ? 1 : ignoreFirstNRows);

            List<SectionPOJO> sections = new LinkedList<>();

            HSSFSheet sheet = getSheetOrThrow(book, _sheetIdx);

            for (int rowIdx = sheet.getFirstRowNum() + _ignoreFirstNRows; rowIdx <= sheet.getLastRowNum(); rowIdx++) {

                HSSFRow row = getRowOrThrow(sheet, _sheetIdx, rowIdx);

                String sectionName = getCellOrThrow(row, _sheetIdx, rowIdx, 0, CellType.STRING).getStringCellValue();
                List<GeologicalClassPOJO> geologicalClasses = new LinkedList<>();

                for (int colIdx = row.getFirstCellNum() + 1; colIdx < row.getLastCellNum(); colIdx += 2) {

                    String geologicalClassName = getCellOrThrow(row, _sheetIdx, rowIdx, colIdx, CellType.STRING).getStringCellValue();
                    String geologicalClassCode = getCellOrThrow(row, _sheetIdx, rowIdx, colIdx+1, CellType.STRING).getStringCellValue();

                    geologicalClasses.add(
                            new GeologicalClassPOJO(geologicalClassName, geologicalClassCode));
                }

                sections.add(
                        new SectionPOJO(sectionName, geologicalClasses));
            }

            JobPOJO job = new JobPOJO(jobId, sections);

            LOG.info(
                    Utils.MARKER__CORE,
                    MSG__IMPORT__SUCCESS,
                    job);

            return jobDAO.addJob(job);

        } catch (Throwable ex) {
            return CompletableFuture.failedFuture(ex);
        }
    }

    private @Nonnull HSSFSheet getSheetOrThrow(
            @Nonnull HSSFWorkbook book,
            int sheetIdx)
            throws JobParsingException {

        assert book != null : "<book> is null";
        assert sheetIdx >= 0;

        if (book.getNumberOfSheets() <= sheetIdx)
            throw new JobParsingException(String.format(
                    "[xls] incorrect sheets count: %d",
                    book.getNumberOfSheets()));

        return book.getSheetAt(sheetIdx);
    }

    private @Nonnull HSSFRow getRowOrThrow(
            @Nonnull HSSFSheet sheet,
            int sheetIdx,
            int rowIdx)
            throws JobParsingException {

        assert sheet != null : "<sheet> is null";
        assert sheetIdx >= 0;
        assert rowIdx >= 0;

        if (sheet.getLastRowNum() + 1 <= rowIdx)
            throw new JobParsingException(String.format(
                    "[xls/sheet{%d}] incorrect rows count: %d",
                    sheetIdx,
                    sheet.getLastRowNum() + 1));

        return sheet.getRow(rowIdx);
    }

    private @Nonnull HSSFCell getCellOrThrow(
            @Nonnull HSSFRow row,
            int sheetIdx,
            int rowIdx,
            int cellIdx,
            @Nonnull CellType cellType)
            throws JobParsingException {

        assert row != null : "<row> is null";
        assert sheetIdx >= 0;
        assert rowIdx >= 0;
        assert cellIdx >= 0;
        assert cellType != null : "<cellType> is null";

        if (row.getLastCellNum() <= cellIdx)
            throw new JobParsingException(String.format(
                    "[xls/sheet{%d}/row{%d}] incorrect cells count: %d",
                    sheetIdx,
                    rowIdx,
                    row.getLastCellNum()));

        HSSFCell cell = row.getCell(cellIdx);

        if (cell.getCellType() != cellType)
            throw new JobParsingException(String.format(
                    "[xls/sheet{%d}/row{%d}/cell{%d}] incorrect cell type: actual=%s, expect=%s",
                    sheetIdx,
                    rowIdx,
                    cellIdx,
                    cell.getCellType(),
                    cellType));

        return cell;
    }

    /**
     * @param sections множество секций
     * @return XLS-файла, содержащий переданные секции
     */
    @Async
    public @Nonnull CompletableFuture<HSSFWorkbook> buildXLS(
            @Nonnull Stream<SectionPOJO> sections) {

        assert sections != null : "<sections> is null";

        try {

            HSSFWorkbook book = new HSSFWorkbook();
            HSSFSheet sheet = book.createSheet();

            HSSFRow header = sheet.createRow(0);
            appendStringCellToRow(header, "Section name");

            sections.forEach(section -> {

                HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);

                appendStringCellToRow(row, section.name());

                section.geologicalClasses().forEach(geologicalClass -> {

                    if (row.getLastCellNum() == header.getLastCellNum()) {
                        int geologicalClassNumb = Math.floorDiv(header.getLastCellNum(), 2) + 1;
                        appendStringCellToRow(header, String.format("Class %d name", geologicalClassNumb));
                        appendStringCellToRow(header, String.format("Class %d code", geologicalClassNumb));
                    }

                    appendStringCellToRow(row, geologicalClass.name());
                    appendStringCellToRow(row, geologicalClass.code());
                });
            });

            LOG.info(
                    Utils.MARKER__CORE,
                    MSG__EXPORT__SUCCESS,
                    book.getSheetAt(0).getLastRowNum());        // size(header) == 1

            return CompletableFuture.completedFuture(book);

        } catch (Throwable ex) {
            return CompletableFuture.failedFuture(ex);
        }
    }

    private @Nonnull HSSFCell appendStringCellToRow(
            @Nonnull HSSFRow row,
            @Nonnull String cellValue) {

        assert row != null : "<row> is null";
        assert cellValue != null : "<cellValue> is null";

        HSSFCell cell = row.createCell(
                                Math.max(row.getLastCellNum(), 0),    // избавляемся от -1 у пустой строки
                                CellType.STRING);

        cell.setCellValue(cellValue);

        return cell;
    }

}