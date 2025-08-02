package mcit.ddr.innovation.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class MonthlyReportDTO {
    // Use Integer instead of int to allow null (no year specified)
    private Integer year;
    // month as String; null or "ALL" can indicate no specific month
    private String month;
    private Map<String, Long> statusCounts = new LinkedHashMap<>();
    private Map<String, Long> categoryCounts = new LinkedHashMap<>();
}
