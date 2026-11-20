package vn.edu.nguyentrai.rooms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomRequest {

    @NotBlank(message = "Mã phòng không được để trống")
    private String roomCode;

    @NotBlank(message = "Tên phòng không được để trống")
    private String name;

    @NotNull(message = "Sức chứa không được để trống")
    @Min(value = 1, message = "Sức chứa phải lớn hơn 0")
    private Integer capacity;

    private String location;

    @Pattern(regexp = "ACTIVE|MAINTENANCE", message = "Trạng thái phải là ACTIVE hoặc MAINTENANCE")
    private String status = "ACTIVE";

    private Long roomTypeId;
}