package sum25.studentcode.backend.modules.Options;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sum25.studentcode.backend.modules.Options.dto.request.OptionsRequest;
import sum25.studentcode.backend.modules.Options.dto.response.OptionsResponse;
import sum25.studentcode.backend.modules.Options.service.OptionsService;

import java.util.List;

@RestController
@RequestMapping("/api/options")
@RequiredArgsConstructor
public class OptionsController {

    private final OptionsService optionsService;

    @PostMapping
    public OptionsResponse createOption(@RequestBody OptionsRequest request) {
        return optionsService.createOption(request);
    }

    @GetMapping("/{id}")
    public OptionsResponse getOptionById(@PathVariable Long id) {
        return optionsService.getOptionById(id);
    }

    @GetMapping
    public List<OptionsResponse> getAllOptions() {
        return optionsService.getAllOptions();
    }

    @PutMapping("/{id}")
    public OptionsResponse updateOption(@PathVariable Long id, @RequestBody OptionsRequest request) {
        return optionsService.updateOption(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteOption(@PathVariable Long id) {
        optionsService.deleteOption(id);
    }
}