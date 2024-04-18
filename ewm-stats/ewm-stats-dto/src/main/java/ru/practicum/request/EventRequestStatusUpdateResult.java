package ru.practicum.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateResult {

    private List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
    private List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

    void addToConfirmed(ParticipationRequestDto request) {
        confirmedRequests.add(request);
    }

    void addToRejected(ParticipationRequestDto request) {
        rejectedRequests.add(request);
    }

}
