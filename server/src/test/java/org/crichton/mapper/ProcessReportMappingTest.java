package org.crichton.mapper;

import org.crichton.domain.utils.mapper.report.ReportMapper;
import org.crichton.domain.utils.mapper.report.UnitTesterPluginResultMapper;
import org.crichton.models.report.UnitTestReport;
import org.crichton.util.ObjectMapperUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import runner.dto.ProcessedReportDTO;

import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class ProcessReportMappingTest {

    @Autowired
    private ReportMapper reportMapper;

    @Test
    public void testConvertProcessReportNonEmptyUnitToUnitTestReport() throws URISyntaxException {
        var file = Path.of(this.getClass().getResource("/coyote_non_emtpy_unit_result.json").toURI()).toFile();
        var dto = ObjectMapperUtils.convertJsonToObject(file, ProcessedReportDTO.class);


        assertThat(dto).isNotNull();
        assertThat(dto.getInfo()).isNotNull();

        var report = UnitTesterPluginResultMapper.INSTANCE.toUnitTestPluginReport(dto);

        assertThat(report).isNotNull();

        assertThat(report.getPluginName()).isEqualTo("coyote");

        assertThat(report.getReports()).isNotNull();
        assertThat(report.getReports()).isNotEmpty();

        var defectDtos = reportMapper.toUnitTestDefectDtos(report);

        assertThat(defectDtos).isNotNull();
        assertThat(defectDtos).isNotEmpty();



    }

    @Test
    public void testConvertProcessReportEmptyUnitToUnitTestReport() throws URISyntaxException {
        var file = Path.of(this.getClass().getResource("/coyote_empty_unit_result.json").toURI()).toFile();
        var dto = ObjectMapperUtils.convertJsonToObject(file, ProcessedReportDTO.class);


        assertThat(dto).isNotNull();
        assertThat(dto.getInfo()).isNotNull();

        var report = UnitTesterPluginResultMapper.INSTANCE.toUnitTestPluginReport(dto);

        assertThat(report).isNotNull();

        assertThat(report.getPluginName()).isEqualTo("coyote");

        assertThat(report.getReports()).isNotNull();
        assertThat(report.getReports()).isEmpty();

        var defectDtos = reportMapper.toUnitTestDefectDtos(report);

        assertThat(defectDtos).isNotNull();
        assertThat(defectDtos).isEmpty();


    }

}
