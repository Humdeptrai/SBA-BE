package sum25.studentcode.backend.modules.Options.service;

import sum25.studentcode.backend.modules.Options.dto.request.OptionsRequest;
import sum25.studentcode.backend.modules.Options.dto.response.OptionsResponse;

import java.util.List;

public interface OptionsService {
    OptionsResponse createOption(OptionsRequest request);
    OptionsResponse getOptionById(Long id);
    List<OptionsResponse> getAllOptions();
    OptionsResponse updateOption(Long id, OptionsRequest request);
    void deleteOption(Long id);
}