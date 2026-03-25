package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ProjectStatusRequest {
    @NotNull
    @Min(1)
    @Max(5)
    private Integer status;
    private LocalDate actualStart;
    private LocalDate actualEnd;
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDate getActualStart() { return actualStart; }
    public void setActualStart(LocalDate actualStart) { this.actualStart = actualStart; }
    public LocalDate getActualEnd() { return actualEnd; }
    public void setActualEnd(LocalDate actualEnd) { this.actualEnd = actualEnd; }
}
