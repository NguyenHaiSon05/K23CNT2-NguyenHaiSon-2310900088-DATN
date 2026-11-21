package vn.edu.nguyentrai.rooms.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatsResponse {
    private long totalRooms;
    private long totalBookings;
    private Map<String, Long> byStatus;
    private List<TopRoom> topRooms;

    @Getter
    @AllArgsConstructor
    public static class TopRoom {
        private String roomCode;
        private String roomName;
        private long count;
    }
}