package mcit.ddr.innovation.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class MonthlyReportDTO {
    private int year;
    private String month;
    private Map<String, Long> statusCounts = new LinkedHashMap<>();
    private Map<String, Long> categoryCounts = new LinkedHashMap<>();
}
